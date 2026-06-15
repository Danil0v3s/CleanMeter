// Settings window root. Full structure is ported in Commit 2.
export function SettingsApp() {
  return (
    <div className="flex min-h-svh items-center justify-center bg-background text-foreground">
      <div className="flex flex-col items-center gap-2">
        <img src="/logo.png" alt="Clean Meter" className="size-10" />
        <h1 className="text-lg font-medium">Clean Meter — Settings</h1>
        <p className="text-sm text-muted-foreground">Tauri + React scaffold ready.</p>
      </div>
    </div>
  )
}
