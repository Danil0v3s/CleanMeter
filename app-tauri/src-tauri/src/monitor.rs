//! Spawns the bundled .NET HardwareMonitor backend.
//!
//! This is the temporary "just run it" path: we launch HardwareMonitor.exe as a
//! plain child process and kill it on exit. Service management (the
//! service-*.bat scripts the old Kotlin app used) will be wired up later.

use std::path::PathBuf;
use std::process::{Child, Command};

use tauri::{AppHandle, Manager};

/// Resolves HardwareMonitor.exe across the dev and production layouts.
fn executable_path(app: &AppHandle) -> Option<PathBuf> {
    // Production: bundled into <resource_dir>/win-x64/ via tauri.conf.json
    // `bundle.resources`.
    if let Ok(resource_dir) = app.path().resource_dir() {
        let bundled = resource_dir.join("win-x64").join("HardwareMonitor.exe");
        if bundled.exists() {
            return Some(bundled);
        }
    }

    // Dev: `bun run build:backend` publishes into src-tauri/resources/win-x64.
    // CARGO_MANIFEST_DIR is baked in at compile time and points at src-tauri on
    // the dev machine, so this resolves without Tauri copying resources.
    let dev = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("resources")
        .join("win-x64")
        .join("HardwareMonitor.exe");
    if dev.exists() {
        return Some(dev);
    }

    None
}

/// Starts the HardwareMonitor backend. Returns the child handle so the caller
/// can terminate it when the app exits. Logs and returns `None` on failure
/// rather than aborting app startup.
pub fn start(app: &AppHandle) -> Option<Child> {
    let exe = match executable_path(app) {
        Some(path) => path,
        None => {
            eprintln!(
                "[monitor] HardwareMonitor.exe not found - run `bun run build:backend` first"
            );
            return None;
        }
    };

    // Run from the backend's own directory so it finds presentmon.exe and
    // ignored-processes.txt sitting next to it.
    let mut command = Command::new(&exe);
    if let Some(dir) = exe.parent() {
        command.current_dir(dir);
    }

    // HardwareMonitor.exe is a console app; suppress its window so it runs
    // silently in the background.
    #[cfg(windows)]
    {
        use std::os::windows::process::CommandExt;
        const CREATE_NO_WINDOW: u32 = 0x0800_0000;
        command.creation_flags(CREATE_NO_WINDOW);
    }

    match command.spawn() {
        Ok(child) => {
            println!("[monitor] started HardwareMonitor ({})", exe.display());
            Some(child)
        }
        Err(err) => {
            eprintln!("[monitor] failed to start HardwareMonitor: {err}");
            None
        }
    }
}
