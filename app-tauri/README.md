# Clean Meter — Tauri + React

Desktop UI for Clean Meter, embedded with **Tauri (Rust)** and built with **Vite + React + shadcn**.

Two windows:

- **`settings`** (`index.html` → `src/settings/`) — the settings app, styled with shadcn.
- **`overlay`** (`overlay.html` → `src/overlay/`) — the transparent, always-on-top stats overlay (pixel-perfect with the original Compose overlay).

State/callbacks flow through React context providers (`src/contexts/`) and mirror the original `SettingsViewModel` API. Live hardware data is fed in later by the existing native project.

## Develop

```bash
bun install
bun run tauri dev     # launches both windows
```

## Build

```bash
bun run build         # vite multi-page build → dist/
bun run tauri build   # native bundle
```
