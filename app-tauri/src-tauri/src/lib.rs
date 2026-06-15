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
fn get_overlay_settings(state: tauri::State<'_, AppState>) -> Option<Value> {
    state.overlay_settings.lock().unwrap().clone()
}

/// Persists overlay settings and pushes them to the overlay window.
#[tauri::command]
fn set_overlay_settings(app: tauri::AppHandle, state: tauri::State<'_, AppState>, settings: Value) {
    *state.overlay_settings.lock().unwrap() = Some(settings.clone());
    let _ = app.emit("overlay-settings-changed", settings);
}

/// Toggles click-through on the overlay window (used when the position is locked).
#[tauri::command]
fn set_overlay_click_through(app: tauri::AppHandle, ignore: bool) {
    if let Some(overlay) = app.get_webview_window("overlay") {
        let _ = overlay.set_ignore_cursor_events(ignore);
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .manage(AppState::default())
        .invoke_handler(tauri::generate_handler![
            get_overlay_settings,
            set_overlay_settings,
            set_overlay_click_through
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
