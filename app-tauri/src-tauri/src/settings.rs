//! Persists overlay settings to disk so they survive restarts.
//!
//! The Kotlin app stored these in a cwd-relative `preferences.json`; here we use
//! the per-user app config directory (e.g. `%APPDATA%/app.cleanmeter.desktop/`),
//! which is the correct writable location regardless of launch directory.

use std::fs;
use std::path::PathBuf;

use serde_json::Value;
use tauri::{AppHandle, Manager};

const SETTINGS_FILE: &str = "overlay-settings.json";

fn settings_path(app: &AppHandle) -> Option<PathBuf> {
    let dir = app.path().app_config_dir().ok()?;
    Some(dir.join(SETTINGS_FILE))
}

/// Reads the persisted overlay settings, or `None` if absent/unreadable.
pub fn load(app: &AppHandle) -> Option<Value> {
    let path = settings_path(app)?;
    let content = fs::read_to_string(path).ok()?;
    // Tolerate a UTF-8 BOM in case the file was hand-edited by an editor that adds one.
    serde_json::from_str(content.trim_start_matches('\u{feff}')).ok()
}

/// Writes the overlay settings as pretty JSON, creating the config dir if needed.
/// Failures are swallowed (matches the Kotlin "silently fail" behavior).
pub fn save(app: &AppHandle, settings: &Value) {
    let Some(path) = settings_path(app) else {
        return;
    };
    if let Some(parent) = path.parent() {
        let _ = fs::create_dir_all(parent);
    }
    if let Ok(json) = serde_json::to_string_pretty(settings) {
        let _ = fs::write(path, json);
    }
}
