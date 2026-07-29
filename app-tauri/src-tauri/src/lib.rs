mod pipe;
mod services;
mod settings;

use std::sync::Mutex;

use serde_json::Value;
use tauri::{Emitter, Manager};

/// Holds the latest overlay settings as opaque JSON. The authoritative data
/// model lives in the frontend; Rust only relays settings between the settings
/// window and the overlay window. Real hardware data will be fed in later by
/// the existing native project.
#[derive(Default)]
struct AppState {
    overlay_settings: Mutex<Option<Value>>,
}

/// Returns the last known overlay settings (if any).
#[tauri::command]
fn get_overlay_settings(
    app: tauri::AppHandle,
    state: tauri::State<'_, AppState>,
) -> Option<Value> {
    if let Some(v) = state.overlay_settings.lock().unwrap().clone() {
        return Some(v);
    }
    // A window may ask before `setup` finished loading from disk; fall back to
    // reading the file directly so persisted settings are never missed.
    let from_disk = settings::load(&app);
    if let Some(v) = from_disk.clone() {
        *state.overlay_settings.lock().unwrap() = Some(v);
    }
    from_disk
}

/// Persists overlay settings and pushes them to the overlay window.
#[tauri::command]
fn set_overlay_settings(app: tauri::AppHandle, state: tauri::State<'_, AppState>, settings: Value) {
    *state.overlay_settings.lock().unwrap() = Some(settings.clone());
    settings::save(&app, &settings);
    let _ = app.emit("overlay-settings-changed", settings);
}

/// Toggles click-through on the overlay window (used when the position is locked).
#[tauri::command]
fn set_overlay_click_through(app: tauri::AppHandle, ignore: bool) {
    if let Some(overlay) = app.get_webview_window("overlay") {
        let _ = overlay.set_ignore_cursor_events(ignore);
    }
}

/// Tells the backend which PresentMon app to source frame timings from.
#[tauri::command]
fn select_present_mon_app(pipe: tauri::State<'_, pipe::PipeHandle>, name: String) {
    pipe.select_present_mon_app(&name);
}

/// Tells the backend how often to poll hardware sensors (milliseconds).
#[tauri::command]
fn select_polling_rate(pipe: tauri::State<'_, pipe::PipeHandle>, interval: u16) {
    pipe.select_polling_rate(interval);
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .manage(AppState::default())
        .invoke_handler(tauri::generate_handler![
            get_overlay_settings,
            set_overlay_settings,
            set_overlay_click_through,
            select_present_mon_app,
            select_polling_rate,
            services::check_service,
            services::install_service,
            services::stop_service
        ])
        .setup(|app| {
            // Restore persisted overlay settings so windows can hydrate from them.
            if let Some(saved) = settings::load(app.handle()) {
                *app.state::<AppState>().overlay_settings.lock().unwrap() = Some(saved);
            }
            // The HardwareMonitor backend runs as the CleanMeterHardwareMonitor
            // Windows service (installed via the consent window), not as a child
            // process — so it holds the ETW/sensor privilege and there's a single
            // pipe server. We only connect to it here.
            // Connect to the backend's pipe and stream readings to the windows.
            let pipe = pipe::start(app.handle().clone());
            app.manage(pipe);
            Ok(())
        })
        .build(tauri::generate_context!())
        .expect("error while building tauri application")
        .run(|_app_handle, _event| {
            // Backend lifecycle is managed as a Windows service, not by the app.
        });
}
