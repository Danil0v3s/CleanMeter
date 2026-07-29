// Builds the .NET HardwareMonitor backend and copies the published binaries
// (HardwareMonitor.exe, presentmon, runtime deps) into src-tauri/resources/<rid>
// so Tauri bundles them into the app's resource directory.
//
// This is the Tauri equivalent of the old Gradle `compileMonitor` +
// `copyMonitorFiles` tasks. Run automatically via `beforeBuildCommand`, or
// manually with `bun run build:backend` to populate binaries for dev.

import { execFileSync } from "node:child_process";
import {
  copyFileSync,
  existsSync,
  mkdirSync,
  readdirSync,
  rmSync,
} from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

// Remove a directory's contents best-effort. A file locked by a running service
// (e.g. PresentMonService.exe) is left in place with a warning instead of
// aborting the build and leaving the dir half-wiped.
function cleanDir(dir) {
  if (!existsSync(dir)) {
    mkdirSync(dir, { recursive: true });
    return;
  }
  for (const entry of readdirSync(dir)) {
    try {
      rmSync(join(dir, entry), { recursive: true, force: true });
    } catch {
      console.warn(`[build-backend] could not remove ${entry} (in use?); leaving it`);
    }
  }
}

// Copy a directory tree, skipping any file that can't be overwritten (e.g. locked
// by a running service) rather than throwing.
function copyDirSafe(src, dest) {
  mkdirSync(dest, { recursive: true });
  for (const entry of readdirSync(src, { withFileTypes: true })) {
    const s = join(src, entry.name);
    const d = join(dest, entry.name);
    if (entry.isDirectory()) {
      copyDirSafe(s, d);
      continue;
    }
    try {
      copyFileSync(s, d);
    } catch (err) {
      console.warn(`[build-backend] skipped ${entry.name} (in use?): ${err.code ?? err.message}`);
    }
  }
}

const scriptDir = dirname(fileURLToPath(import.meta.url));
const appTauriDir = resolve(scriptDir, "..");
const repoRoot = resolve(appTauriDir, "..");

// Runtime identifier to publish for. The backend is currently Windows-only;
// override with `--rid <rid>` (e.g. linux-x64, osx-arm64) when that lands.
const ridArg = process.argv.indexOf("--rid");
const rid = ridArg !== -1 ? process.argv[ridArg + 1] : "win-x64";

const csproj = join(
  repoRoot,
  "HardwareMonitor",
  "HardwareMonitor",
  "HardwareMonitor.csproj",
);
const publishDir = join(appTauriDir, "src-tauri", "resources", rid);

console.log(`[build-backend] publishing HardwareMonitor (${rid})`);
console.log(`[build-backend]   csproj : ${csproj}`);
console.log(`[build-backend]   output : ${publishDir}`);

if (!existsSync(csproj)) {
  console.error(`[build-backend] csproj not found: ${csproj}`);
  process.exit(1);
}

// Start from a clean output dir so stale binaries never linger in the bundle.
// Lock-tolerant: a running service's exe is left in place rather than aborting.
cleanDir(publishDir);

try {
  execFileSync(
    "dotnet",
    [
      "publish",
      csproj,
      "-c",
      "Release",
      "-r",
      rid,
      "-p:PublishAot=false",
      "-o",
      publishDir,
    ],
    { stdio: "inherit" },
  );
} catch (err) {
  console.error("[build-backend] dotnet publish failed");
  process.exit(err.status ?? 1);
}

// The csproj's CopyPresentMon target already drops presentmon into the publish
// dir; copy it explicitly as a fallback in case that target is ever removed.
const presentmonSrc = join(repoRoot, "presentmon");
if (existsSync(presentmonSrc)) {
  copyDirSafe(presentmonSrc, publishDir);
}

// Service lifecycle scripts must sit next to the binaries: each resolves its exe
// (HardwareMonitor.exe / PresentMonService.exe) via %~dp0 and is invoked per-service
// (e.g. `service-create.bat presentmon`).
const scriptsSrc = join(appTauriDir, "src-tauri", "resources", "scripts");
if (existsSync(scriptsSrc)) {
  copyDirSafe(scriptsSrc, publishDir);
}

console.log(`[build-backend] done -> ${publishDir}`);
