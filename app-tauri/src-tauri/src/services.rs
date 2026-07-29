//! Windows service lifecycle for the CleanMeter backend, driven by the bundled
//! self-elevating `.bat` scripts (see `resources/scripts/`). The consent window
//! uses these commands to check and install/start `PresentMonSharedService` and
//! `CleanMeterHardwareMonitor`.
//!
//! Tauri never talks to the PresentMon service directly — it only manages
//! lifecycle here; the actual PresentMon API integration lives in the .NET
//! HardwareMonitor backend.

use tauri::AppHandle;

#[derive(serde::Serialize, Default, Clone, Copy)]
pub struct ServiceStatus {
    pub installed: bool,
    pub running: bool,
}

/// Maps a stable UI key to the actual Windows service name.
fn service_name(key: &str) -> Option<&'static str> {
    match key {
        "hardwaremonitor" => Some("CleanMeterHardwareMonitor"),
        "presentmon" => Some("PresentMonSharedService"),
        _ => None,
    }
}

/// Queries a service via `sc query`. Non-elevated, so it's safe to poll from the
/// UI. Returns `installed=false` for an unknown/absent service.
#[cfg(windows)]
pub fn query(name: &str) -> ServiceStatus {
    use std::os::windows::process::CommandExt;
    use std::process::Command;
    const CREATE_NO_WINDOW: u32 = 0x0800_0000;

    match Command::new("sc")
        .args(["query", name])
        .creation_flags(CREATE_NO_WINDOW)
        .output()
    {
        Ok(out) => {
            let stdout = String::from_utf8_lossy(&out.stdout);
            ServiceStatus {
                installed: out.status.success(),
                running: stdout.contains("RUNNING"),
            }
        }
        Err(_) => ServiceStatus::default(),
    }
}

#[cfg(not(windows))]
pub fn query(_name: &str) -> ServiceStatus {
    ServiceStatus::default()
}

/// Reports whether a service is installed and running.
#[tauri::command]
pub fn check_service(key: String) -> ServiceStatus {
    match service_name(&key) {
        Some(name) => query(name),
        None => ServiceStatus::default(),
    }
}

/// Locates a bundled lifecycle script next to the backend binaries: the packaged
/// resource dir in production, the dev publish dir otherwise.
#[cfg(windows)]
fn resolve_script(app: &AppHandle, name: &str) -> Option<std::path::PathBuf> {
    use tauri::Manager;

    if let Ok(resource_dir) = app.path().resource_dir() {
        let bundled = resource_dir.join("win-x64").join(name);
        if bundled.exists() {
            return Some(strip_verbatim(bundled));
        }
    }
    let dev = std::path::PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("resources")
        .join("win-x64")
        .join(name);
    if dev.exists() {
        return Some(dev);
    }
    None
}

/// `cmd.exe` can't parse the `\\?\` extended-length prefix that `resource_dir()`
/// returns, so strip it before handing a path to cmd (otherwise the batch fails
/// with "The system cannot find the path specified").
#[cfg(windows)]
fn strip_verbatim(p: std::path::PathBuf) -> std::path::PathBuf {
    let s = p.to_string_lossy().into_owned();
    if let Some(rest) = s.strip_prefix(r"\\?\UNC\") {
        std::path::PathBuf::from(format!(r"\\{rest}"))
    } else if let Some(rest) = s.strip_prefix(r"\\?\") {
        std::path::PathBuf::from(rest)
    } else {
        p
    }
}

/// Runs a lifecycle script for the given service key. The script self-elevates
/// via UAC, so this returns immediately — the caller should poll `check_service`
/// to observe the result.
#[cfg(windows)]
fn run_script(app: &AppHandle, script: &str, key: &str) -> Result<(), String> {
    use std::os::windows::process::CommandExt;
    use std::process::Command;
    const CREATE_NO_WINDOW: u32 = 0x0800_0000;

    if service_name(key).is_none() {
        return Err(format!("unknown service key: {key}"));
    }
    let path = resolve_script(app, script).ok_or_else(|| format!("{script} not found"))?;
    Command::new("cmd")
        .arg("/c")
        .arg(&path)
        .arg(key)
        .creation_flags(CREATE_NO_WINDOW)
        .spawn()
        .map(|_| ())
        .map_err(|e| e.to_string())
}

/// Creates + starts the service (via `service-create.bat`). Triggers a UAC prompt.
#[tauri::command]
pub fn install_service(app: AppHandle, key: String) -> Result<(), String> {
    #[cfg(windows)]
    {
        run_script(&app, "service-create.bat", &key)
    }
    #[cfg(not(windows))]
    {
        let _ = (&app, &key);
        Err("services are Windows-only".into())
    }
}

/// Stops the service (via `service-stop.bat`). Triggers a UAC prompt.
#[tauri::command]
pub fn stop_service(app: AppHandle, key: String) -> Result<(), String> {
    #[cfg(windows)]
    {
        run_script(&app, "service-stop.bat", &key)
    }
    #[cfg(not(windows))]
    {
        let _ = (&app, &key);
        Err("services are Windows-only".into())
    }
}
