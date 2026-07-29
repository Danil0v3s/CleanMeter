//! Named-pipe client for the HardwareMonitor backend.
//!
//! Port of the Kotlin `PipeClient` + `HardwareMonitorReader`. Connects to the
//! backend's `\\.\pipe\HardwareMonitor_31337` pipe, decodes the binary frames it
//! pushes, and emits a `hardware-data` event (shaped like the frontend's
//! `HardwareMonitorData`) to all windows.
//!
//! Wire framing (server -> client): `[command: u16 LE][length: u32 LE][payload]`.
//! Commands sent client -> server use `[command: u16 LE][size: u16 LE][bytes]`
//! (no 4-byte length prefix) to match the C# `OnClientData` parser.

use std::sync::{Arc, Mutex};
use std::time::{Duration, SystemTime, UNIX_EPOCH};

use serde::Serialize;
use tauri::{AppHandle, Emitter};

const PIPE_NAME: &str = r"\\.\pipe\HardwareMonitor_31337";

// Command codes — mirror Kotlin `Command` / C# `MonitorPacketCommand`.
const CMD_DATA: u16 = 0;
#[cfg(windows)]
const CMD_SELECT_PRESENTMON_APP: u16 = 2;
const CMD_PRESENTMON_APPS: u16 = 3;
#[cfg(windows)]
const CMD_SELECT_POLLING_RATE: u16 = 4;
#[cfg(windows)]
const CMD_SET_FOREGROUND_APPLICATION: u16 = 5;

/// PresentMon app names are sent as fixed-width, NUL-padded fields.
const PRESENTMON_NAME_SIZE: usize = 128;

/// Guards against a desynced stream allocating an absurd payload buffer.
const MAX_PAYLOAD: usize = 16 * 1024 * 1024;

#[derive(Serialize, Clone, Default)]
#[serde(rename_all = "PascalCase")]
struct Hardware {
    name: String,
    identifier: String,
    hardware_type: i32,
}

#[derive(Serialize, Clone, Default)]
#[serde(rename_all = "PascalCase")]
struct Sensor {
    name: String,
    identifier: String,
    hardware_identifier: String,
    sensor_type: i32,
    value: f32,
}

/// Mirrors the frontend `HardwareMonitorData` interface (PascalCase fields).
#[derive(Serialize, Clone, Default)]
#[serde(rename_all = "PascalCase")]
struct HardwareMonitorData {
    last_poll_time: u64,
    hardwares: Vec<Hardware>,
    sensors: Vec<Sensor>,
    present_mon_apps: Vec<String>,
}

/// Little-endian cursor over a payload slice; every read is bounds-checked so a
/// malformed frame yields `None` instead of panicking.
struct Cursor<'a> {
    buf: &'a [u8],
    pos: usize,
}

impl<'a> Cursor<'a> {
    fn new(buf: &'a [u8]) -> Self {
        Self { buf, pos: 0 }
    }

    fn take(&mut self, n: usize) -> Option<&'a [u8]> {
        let end = self.pos.checked_add(n)?;
        let slice = self.buf.get(self.pos..end)?;
        self.pos = end;
        Some(slice)
    }

    fn u16(&mut self) -> Option<u16> {
        let b = self.take(2)?;
        Some(u16::from_le_bytes([b[0], b[1]]))
    }

    fn i32(&mut self) -> Option<i32> {
        let b = self.take(4)?;
        Some(i32::from_le_bytes([b[0], b[1], b[2], b[3]]))
    }

    fn f32(&mut self) -> Option<f32> {
        let b = self.take(4)?;
        Some(f32::from_le_bytes([b[0], b[1], b[2], b[3]]))
    }

    /// Reads `len` bytes as UTF-8, stopping at the first NUL (the backend
    /// NUL-pads fixed-width fields).
    fn string(&mut self, len: usize) -> Option<String> {
        let b = self.take(len)?;
        let end = b.iter().position(|&c| c == 0).unwrap_or(b.len());
        Some(String::from_utf8_lossy(&b[..end]).into_owned())
    }
}

/// Decodes a `Data` payload: `[hwCount:i32][sensorCount:i32][hardware...][sensor...]`.
fn decode_data(payload: &[u8], data: &mut HardwareMonitorData) {
    let mut c = Cursor::new(payload);
    let hardware_count = match c.i32() {
        Some(n) if n >= 0 => n as usize,
        _ => return,
    };
    let sensor_count = match c.i32() {
        Some(n) if n >= 0 => n as usize,
        _ => return,
    };

    let mut hardwares = Vec::with_capacity(hardware_count);
    for _ in 0..hardware_count {
        let name_len = match c.u16() {
            Some(n) => n as usize,
            None => return,
        };
        let id_len = match c.u16() {
            Some(n) => n as usize,
            None => return,
        };
        let (Some(name), Some(identifier), Some(hardware_type)) =
            (c.string(name_len), c.string(id_len), c.i32())
        else {
            return;
        };
        hardwares.push(Hardware {
            name,
            identifier,
            hardware_type,
        });
    }

    let mut sensors = Vec::with_capacity(sensor_count);
    for _ in 0..sensor_count {
        let name_len = match c.u16() {
            Some(n) => n as usize,
            None => return,
        };
        let id_len = match c.u16() {
            Some(n) => n as usize,
            None => return,
        };
        let hwid_len = match c.u16() {
            Some(n) => n as usize,
            None => return,
        };
        let (Some(name), Some(identifier), Some(hardware_identifier), Some(sensor_type), Some(value)) = (
            c.string(name_len),
            c.string(id_len),
            c.string(hwid_len),
            c.i32(),
            c.f32(),
        ) else {
            return;
        };
        sensors.push(Sensor {
            name,
            identifier,
            hardware_identifier,
            sensor_type,
            value,
        });
    }

    // Only commit once the whole frame parsed, so a truncated packet never
    // half-replaces the previous good reading.
    data.hardwares = hardwares;
    data.sensors = sensors;
}

/// Decodes a `PresentMonApps` payload: `[count:u16][name:128]...`. Prepends the
/// implicit "Auto" entry, matching the Kotlin reader.
fn decode_present_mon_apps(payload: &[u8], data: &mut HardwareMonitorData) {
    let mut c = Cursor::new(payload);
    let count = match c.u16() {
        Some(n) => n as usize,
        None => return,
    };
    let mut apps = Vec::with_capacity(count + 1);
    apps.push("Auto".to_string());
    for _ in 0..count {
        match c.string(PRESENTMON_NAME_SIZE) {
            Some(name) => apps.push(name),
            None => break,
        }
    }
    data.present_mon_apps = apps;
}

fn now_millis() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_millis() as u64)
        .unwrap_or(0)
}

/// Encodes a `[command][size][utf8 bytes]` client-to-server packet.
#[cfg(windows)]
fn encode_string_command(command: u16, value: &str) -> Vec<u8> {
    let bytes = value.as_bytes();
    let mut out = Vec::with_capacity(4 + bytes.len());
    out.extend_from_slice(&command.to_le_bytes());
    out.extend_from_slice(&(bytes.len() as u16).to_le_bytes());
    out.extend_from_slice(bytes);
    out
}

/// Encodes a `[command][value]` client-to-server packet (two `u16` fields).
#[cfg(windows)]
fn encode_short_command(command: u16, value: u16) -> Vec<u8> {
    let mut out = Vec::with_capacity(4);
    out.extend_from_slice(&command.to_le_bytes());
    out.extend_from_slice(&value.to_le_bytes());
    out
}

/// Handle for sending commands back to the backend over the pipe. Cloneable and
/// stored in Tauri state so commands can reach the (reconnecting) write side.
#[derive(Clone)]
#[cfg_attr(not(windows), allow(dead_code))]
pub struct PipeHandle {
    write_slot: Arc<Mutex<Option<std::fs::File>>>,
}

impl PipeHandle {
    #[cfg(windows)]
    fn send(&self, packet: &[u8]) {
        use std::io::Write;
        if let Some(file) = self.write_slot.lock().unwrap().as_mut() {
            let _ = file.write_all(packet).and_then(|_| file.flush());
        }
    }

    /// Selects the PresentMon app whose frame data drives the FPS reading.
    pub fn select_present_mon_app(&self, name: &str) {
        #[cfg(windows)]
        self.send(&encode_string_command(CMD_SELECT_PRESENTMON_APP, name));
        #[cfg(not(windows))]
        let _ = name;
    }

    /// Sets the backend hardware polling interval, in milliseconds.
    pub fn select_polling_rate(&self, interval: u16) {
        #[cfg(windows)]
        self.send(&encode_short_command(CMD_SELECT_POLLING_RATE, interval));
        #[cfg(not(windows))]
        let _ = interval;
    }
}

#[cfg(windows)]
fn read_loop(
    app: &AppHandle,
    write_slot: &Arc<Mutex<Option<std::fs::File>>>,
) -> std::io::Result<()> {
    use std::fs::OpenOptions;
    use std::io::Read;

    let mut pipe = OpenOptions::new()
        .read(true)
        .write(true)
        .open(PIPE_NAME)?;

    // Hand a write handle to the foreground watcher so it can talk back.
    if let Ok(clone) = pipe.try_clone() {
        *write_slot.lock().unwrap() = Some(clone);
    }
    println!("[pipe] connected to {PIPE_NAME}");

    let mut data = HardwareMonitorData::default();
    let mut header = [0u8; 6];
    loop {
        pipe.read_exact(&mut header)?;
        let command = u16::from_le_bytes([header[0], header[1]]);
        let length =
            u32::from_le_bytes([header[2], header[3], header[4], header[5]]) as usize;
        if length > MAX_PAYLOAD {
            return Err(std::io::Error::new(
                std::io::ErrorKind::InvalidData,
                "payload exceeds maximum",
            ));
        }

        let mut payload = vec![0u8; length];
        pipe.read_exact(&mut payload)?;

        match command {
            CMD_DATA => decode_data(&payload, &mut data),
            CMD_PRESENTMON_APPS => decode_present_mon_apps(&payload, &mut data),
            _ => continue,
        }

        data.last_poll_time = now_millis();
        let _ = app.emit("hardware-data", &data);
    }
}

/// Returns the file name of the foreground window's process (e.g. `game.exe`).
#[cfg(windows)]
fn foreground_process_name() -> Option<String> {
    use std::os::raw::c_void;

    #[link(name = "user32")]
    extern "system" {
        fn GetForegroundWindow() -> *mut c_void;
        fn GetWindowThreadProcessId(hwnd: *mut c_void, pid: *mut u32) -> u32;
    }
    #[link(name = "kernel32")]
    extern "system" {
        fn OpenProcess(access: u32, inherit: i32, pid: u32) -> *mut c_void;
        fn QueryFullProcessImageNameW(
            handle: *mut c_void,
            flags: u32,
            buf: *mut u16,
            size: *mut u32,
        ) -> i32;
        fn CloseHandle(handle: *mut c_void) -> i32;
    }

    const PROCESS_QUERY_LIMITED_INFORMATION: u32 = 0x1000;

    unsafe {
        let hwnd = GetForegroundWindow();
        if hwnd.is_null() {
            return None;
        }
        let mut pid: u32 = 0;
        GetWindowThreadProcessId(hwnd, &mut pid);
        if pid == 0 {
            return None;
        }
        let handle = OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION, 0, pid);
        if handle.is_null() {
            return None;
        }
        let mut buf = [0u16; 260];
        let mut size = buf.len() as u32;
        let ok = QueryFullProcessImageNameW(handle, 0, buf.as_mut_ptr(), &mut size);
        CloseHandle(handle);
        if ok == 0 {
            return None;
        }
        let path = String::from_utf16_lossy(&buf[..size as usize]);
        let name = path
            .rsplit(|c| c == '\\' || c == '/')
            .next()
            .unwrap_or(&path)
            .to_string();
        if name.is_empty() {
            None
        } else {
            Some(name)
        }
    }
}

/// Starts the pipe client: a reader/reconnect thread that emits `hardware-data`,
/// plus (on Windows) a watcher that reports the foreground app so PresentMon's
/// "Auto" mode can attribute frame timings — i.e. live FPS.
pub fn start(app: AppHandle) -> PipeHandle {
    let write_slot: Arc<Mutex<Option<std::fs::File>>> = Arc::new(Mutex::new(None));

    {
        let app = app.clone();
        let write_slot = write_slot.clone();
        std::thread::spawn(move || loop {
            #[cfg(windows)]
            {
                if let Err(err) = read_loop(&app, &write_slot) {
                    eprintln!("[pipe] disconnected: {err}");
                }
                *write_slot.lock().unwrap() = None;
            }
            #[cfg(not(windows))]
            {
                let _ = (&app, &write_slot);
            }
            std::thread::sleep(Duration::from_millis(1000));
        });
    }

    #[cfg(windows)]
    {
        use std::io::Write;
        let write_slot = write_slot.clone();
        std::thread::spawn(move || {
            let mut last: Option<String> = None;
            loop {
                if let Some(name) = foreground_process_name() {
                    if last.as_deref() != Some(name.as_str()) {
                        if let Some(file) = write_slot.lock().unwrap().as_mut() {
                            let packet = encode_string_command(CMD_SET_FOREGROUND_APPLICATION, &name);
                            if file.write_all(&packet).and_then(|_| file.flush()).is_ok() {
                                last = Some(name);
                            }
                        }
                    }
                }
                std::thread::sleep(Duration::from_millis(2000));
            }
        });
    }

    PipeHandle { write_slot }
}
