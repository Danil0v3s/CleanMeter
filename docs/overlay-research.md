# CleanMeter — Linux port & in-game overlay research

Working notes from a research session on 2026-08-01. Nothing here has been
built or verified by compilation — it is a briefing so the work can be resumed
on another machine.

**Branch:** `feat/rust-wrapper` · **Scope:** `app-tauri` only (the Kotlin `app/`
module is out of scope; `HardwareMonitor/` + `presentmon/` are the Windows
backend).

---

## How to resume

Paste this into Claude Code on the Windows machine:

> Read `docs/overlay-research.md`. I want to build the experimental in-game
> overlay for the Windows build — start with the Vulkan implicit layer path
> described in section 5. Prototype the ImGui renderer standalone first.

Prerequisites on that machine: MSVC toolchain, Vulkan SDK, Rust, `bun`, .NET 9
SDK (for the existing `HardwareMonitor` backend), and a Vulkan game to test in.

---

## 1. The premise that failed

The starting assumption was "we use LibreHardwareMonitor, so we can adapt it for
Linux." **That does not hold.**

- LHM depends on the **WinRing0 kernel driver**, WMI, and Windows P/Invokes. The
  NuGet package targets netstandard2.0 so it *compiles* on Linux, but returns no
  data — there is no Linux sensor backend inside it.
- `HardwareMonitor.csproj` also pins `<RuntimeIdentifier>win-x64`.
- **PresentMon is likewise Windows-only** (ETW-based).

So 0% of the sensor layer ports. What *does* port is more valuable — see below.

## 2. The actual portable asset

The frontend is already platform-agnostic and this is the key finding.

`app-tauri/src/lib/model/hardwareMonitorData.ts` defines a **flat sensor list**:

```ts
interface HwSensor {
  Name: string; Identifier: string; HardwareIdentifier: string
  SensorType: HwSensorType; Value: number
}
```

The overlay binds widgets to **user-chosen `Identifier` strings**
(`GpuSection.tsx:33`, `NetSection.tsx:32`), so nothing in React knows what LHM
is. `pipe.rs` is also already `#[cfg]`-stubbed for non-Windows.

**Any backend that emits this struct drives the entire UI unchanged.** The port
is "replace the data source, keep the contract."

### Contract details that are load-bearing

| Consumer | Requirement |
|---|---|
| `ramUsage()` | sensor named exactly `Memory Used`, in **GB** |
| `ramUsagePercent()` | sensor named exactly `Memory Available`, in **GB** |
| `readings(data, "GPU"/"CPU")` | identifier or name must contain that substring |
| `networkReadings()` | identifier must contain `/nic/` |
| VRAM bar (`GpuSection`) | percent 0–100, and `Name` must contain `memory` |
| Total VRAM label | **MB** (divided by 1000 for display) |
| `fps()` | `/presentmon/frametime` in ms |

Identifiers should mirror LHM's shape (`/amdcpu/0/load/0`, `/gpu-nvidia/0/power/0`,
`/nic/0/throughput/8`) so settings files stay meaningful across platforms.

### Known latent bug

`hardwareMonitorData.ts:88` — `fps()` divides by a frametime that defaults to `1`,
so with **no frame source it renders `1000` FPS**, and with a real `0` it renders
`Infinity`. This affects Windows too whenever no game is running. Needs a guard.

## 3. Linux sensor sources (verified on a CachyOS box)

Verified against real hardware: Ryzen 7 9800X3D, AMD Raphael iGPU (`card0`) +
RTX 4080 (`card1`), KDE Wayland.

| Metric | Source | Notes |
|---|---|---|
| CPU load | `/proc/stat` | delta between ticks; `busy = total - (idle + iowait)` |
| CPU temp | hwmon `k10temp`/`zenpower`/`coretemp` | channels are **sparse** — `temp1`, `temp3`, no `temp2`. Scan, don't count |
| CPU clock | `cpufreq/scaling_cur_freq` | kHz |
| CPU power | `/sys/class/powercap/*/energy_uj` | **root-only (`-r-------- root`)** since the PLATYPUS mitigation — unavailable unelevated. MangoHud has the same limit |
| AMD GPU | `card0/device/gpu_busy_percent`, `mem_info_vram_used/total`, `device/hwmon/hwmonN/{temp1,power1,freq1}_input` | |
| NVIDIA GPU | NVML (`libnvidia-ml.so.1` present) | **no `nvidia` hwmon block exists** — NVML is required, not optional |
| RAM | `/proc/meminfo` | use `MemAvailable`, not `MemFree` |
| Network | `/sys/class/net/*/statistics/{rx,tx}_bytes` | delta; skip `lo` |
| **FPS/frametime** | **nothing** | requires frame interception — see below |

GPU product names can come from `/usr/share/hwdata/pci.ids` (present on Arch).

### Blocker: the Wayland overlay

Tauri's `always_on_top` and `set_position` are **silent no-ops on Wayland**
(tao#1134, tauri#14913). The recent drag-to-move commit and the click-through
toggle in `lib.rs:47` both depend on exactly those.

Fix would be the `wlr-layer-shell` protocol (KDE Plasma supports it) applied to
Tauri's GTK3 window via `gtk-layer-shell`, before the window is realized. Tauri
does not expose this; it is real surgery.

## 4. MangoHud findings

### It cannot be a general sensor provider

- `mangoapp` receives data from the layer over a **SysV message queue**
  (`ftok("mangoapp", 65)`, `msgget`) carrying only `pid`, `visible_frametime_ns`,
  `app_frametime_ns`, `latency_ns`, FSR settings and output dimensions — **no
  hardware sensors**.
- Its only structured sensor output is a **CSV log file**
  (`fps, frametime, cpu_load, cpu_power, gpu_load, cpu_temp, gpu_temp,
  gpu_core_clock, gpu_mem_clock, gpu_vram_used, gpu_power, ram_used, swap_used, …`),
  written per `log_interval`, and only while attached to a running game.
- No streaming IPC. The settings window needs live sensors with no game running,
  so this fails as a provider.

**Usable as:** a reference implementation for sysfs quirks, and a live source for
**frametime only** via that message queue.

### Its UI is Dear ImGui

- Immediate mode, C++, drawn inside the game's swapchain by the Vulkan/OpenGL layer.
- **Layout is ImGui tables** — label in column 1, right-aligned value in column 2
  (`ImguiNextColumnFirstItem()`, `right_aligned_text()`).
- **Elements are config-ordered function pointers**: `display_params` maps a
  config key to a draw function, `sort_elements()` builds `ordered_functions`,
  the render loop iterates it.
- **Styling surface:** pushed style vars (`WindowBorderSize`, `ItemSpacing`,
  `Alpha`), plus `background_alpha`, `round_corners`, `font_file`, `font_size`,
  `text_outline`, 9 `position` anchors, per-metric colors (`gpu_color`, …).
- **No theme system.** Two layout modes: vertical and `horizontal`.
- License: **MIT** — safe to port from with attribution.

### CleanMeter's design ports to ImGui better than expected

The overlay uses **zero CSS-only effects** — no `backdrop-filter`, no blur, no
box-shadow, no gradients except one mask in `LineGraph`. It is flat translucent
rounded rects, an SVG arc, a polyline, and flat token colors.

| CleanMeter | ImGui equivalent |
|---|---|
| `Pill` (`rgba(0,0,0,0.3)`, radius 9999/8) | `ImDrawList::AddRectFilled` with rounding — 1:1 |
| `CircularProgress` SVG arc | `PathArcTo` + `PathStroke` — 1:1 |
| `LineGraph` + edge-fade mask | `AddPolyline` with per-vertex alpha — close |
| `overlayColors` tokens | trivial |

**You draw via the draw list, not via widgets.** `ImGui::GetForegroundDrawList()`
returns a full-viewport `ImDrawList` with **no `Begin`/`End`, no window flags, no
auto-layout** — absolute pixel coordinates and a 2D vector API, which is close to
the absolutely-positioned divs + SVG the overlay already uses. All the "ImGui is
opinionated" friction lives in the widget layer, which is skipped entirely.

The whole `Pill` is one call — rounding clamps to half the smaller dimension, so
`borderRadius: 9999` translates literally:

```cpp
dl->AddRectFilled(min, max, IM_COL32(0,0,0,77), (max.y - min.y) * 0.5f);
```

Anti-aliasing is on by default for fills and strokes.

**`AutoFitText` is not a problem** (this reverses an earlier assumption). Before
ImGui 1.92 fonts were single-size and had to be pre-baked, which would have made
the continuous min/max fit quantized. **1.92 (June 2025) reworked the font
system:** `PushFont(NULL, size)` sets an arbitrary size at runtime, each `ImFont`
keeps an `ImFontBaked` per size in use, and the atlas grows on demand. So measure
with `CalcTextSize` at a candidate size and shrink until it fits the slot, as
designed. `style.FontScaleDpi` / `FontScaleMain` covers the `mapScale` user scale
without backend-specific texture re-uploads. **Requires ImGui ≥ 1.92 and a
backend supporting `ImGuiBackendFlags_HasTextures`** — pin this.

**The remaining friction — layout.** There is no flexbox; `gap: 12`,
`padding: "4px 12px"`, `alignItems: center` become a cursor advanced by hand. But
the design already thinks in fixed pixels (`LABEL.fps.width = 40`, `temp: 28`,
`vram: 38`) precisely so overlay width stays stable — that is closer to immediate
mode than to CSS. It is arithmetic, not a layout engine. Roughly a 50-line helper.

**The layout *model* is the real mismatch with MangoHud, not the styling.**
MangoHud assumes a
   vertical stack of table rows; CleanMeter is a horizontal row of self-contained
   pills with fixed internal slots. You cannot express that via
   `ordered_functions` — you would bypass the table system and draw the window
   yourself. A CleanMeter look is therefore a **third layout mode**, not a config
   preset. That is a large upstream ask and needs buy-in before writing it.

## 5. Windows in-game overlay (the experiment to try)

### Mechanism asymmetry

**Vulkan has a sanctioned entry point.** Implicit layers are a documented Khronos
loader feature:

- Register a JSON manifest under `HKLM\SOFTWARE\Khronos\Vulkan\ImplicitLayers`
  (and `WOW6432Node\...` for 32-bit games), value = path to JSON, **DWORD data
  `0` means enabled** (anything else disables it).
- Export `vkNegotiateLoaderLayerInterfaceVersion`, chain through
  `vkGetInstanceProcAddr` / `vkGetDeviceProcAddr`, intercept
  `vkCreateSwapchainKHR` / `vkQueuePresentKHR` / `vkDestroySwapchainKHR`.
- **No injection, no inline hooking, no patched memory.** This is why the path is
  defensible.

**DirectX has no equivalent.** No layer mechanism exists for D3D11/12. The
MangoHud Windows fork uses a **DXGI proxy DLL** — drop `dxgi.dll` beside the
game exe, forward exports to the real system DLL, hook `Present`. It works, but
"unsigned proxy DLL adjacent to the game binary intercepting calls" is exactly
the cheat-loader signature. That fork ships **no anti-cheat warnings at all** —
treat as a red flag, not reassurance.

### Anti-cheat reality

No public partner/allowlist program was found for either EAC or BattlEye.

- **BattlEye** generally tolerates non-cheat overlays *unless the game developer
  opts out* (ReShade is blocked in PUBG, Fortnite). Per-title, not per-tool.
- **EAC** is rougher: monitoring software is a known false-positive source, and
  EAC has banned users for **forcing MSI Afterburner's overlay into unsupported
  titles**. Older RTSS builds are blacklisted outright.

### Design rules (the goal is to stand down, not to hide)

1. **Fail closed.** At layer init, detect `EasyAntiCheat_x64.dll`,
   `BEClient_x64.dll`, Vanguard etc. in the process and **disable yourself,
   render nothing.** Single most important decision in the feature.
2. **Sign the DLL** with a trusted code-signing cert — required by most
   anti-cheat for DLLs loaded into third-party processes. Needs budget + entity.
3. **Off by default, per-game opt-in.** Do *not* copy MangoHud-Windows'
   `MANGOHUD=1` system-wide pattern — globally forcing an overlay into every game
   is the exact behaviour that got Afterburner users banned.
4. **No obfuscation, no packing, no anti-debug.** Those traits are what tip a
   classifier from "overlay" to "cheat."
5. Label it experimental in the UI, singleplayer/benchmarking-oriented, with the
   ban risk stated plainly.

### Language & process topology

**The DLL is the mechanism, not a language artifact.** A Vulkan implicit layer
*is* a shared library the loader maps into the game's process — there is no way
to reach the swapchain from outside its address space. Two binaries result:

```
CleanMeter.exe (Tauri)  ──┐
                          ├── both are pipe clients of HardwareMonitor
cleanmeter_layer.dll  ────┘   (loaded into the game by the Vulkan loader)
```

**Constraint: the layer must be its own minimal crate.** Not the existing Tauri
lib — pulling wry/webkit/Tauri into a game process is absurd on the merits and a
loud anti-cheat signal. Keep it to `ash` + ImGui + a pipe client.

**Rust can do all of it:**

| Piece | Crate |
|---|---|
| The DLL | `cdylib` crate, `#[no_mangle] extern "system"` exports |
| Vulkan | `ash` — thin, 1:1 with the C API, right for raw dispatch tables (**not** `vulkano`) |
| Layer plumbing | Google's [`vk-layer-for-rust`](https://google.github.io/vk-layer-for-rust/doc-Windows/vulkan_layer/index.html), trait-based, on `ash`. **Evaluate early** rather than assuming |
| ImGui | [`dear-imgui-rs`](https://crates.io/crates/dear-imgui-rs) — tracks ImGui **1.92.7/1.92.8** with a Vulkan backend, so the dynamic-font story survives into Rust. The older `imgui-rs` lags upstream badly; do not reach for it by reflex |

**Structural win:** the decode logic in `pipe.rs` (`Cursor`, `decode_data`, frame
header parsing) becomes a **shared workspace crate used by both the exe and the
layer DLL** — the layer inherits a tested decoder and stays tiny, which is
exactly the property you want in code loaded into other people's processes.

**Cost:** MangoHud's `src/vulkan.cpp` is C++, so in Rust you are *translating it,
not lifting it* — and that is the fiddliest part of the job (swapchain
recreation, per-image resources, present-path synchronisation), where a working
reference is worth the most. Rust still looks right given the surrounding
codebase, but timebox the `vk-layer-for-rust` evaluation; hand-rolling the
dispatch chain on `ash` is very doable, whereas discovering the need late is not.

### Phasing

**Phase 0 — the renderer (do this first).** Port Pill / Progress / LineGraph to
`ImDrawList` against the existing tokens in `app-tauri/src/overlay/tokens.ts`
(`overlayColors`, `LABEL` slot widths, `mapScale`). **Testable standalone in a
plain ImGui window** with no layer, no injection, no game, no anti-cheat
exposure. This asset is shared with the MangoHud-upstream route — worth building
either way, and it is the natural place to validate `dear-imgui-rs`.

**Phase 1 — Vulkan implicit layer.**
- Layer dispatch boilerplate + swapchain lifecycle tracking.
- ImGui Vulkan backend against swapchain images: render pass, pipeline,
  descriptor pool, command buffers, correct recreation on resize/fullscreen.
  This is the bulk of the work.
- **The layer becomes a second client of the existing pipe.** It collects nothing
  and holds no privilege — it connects to `\\.\pipe\HardwareMonitor_31337` and
  renders the same frames the Tauri window already receives (see `pipe.rs` for
  the framing: `[command:u16 LE][length:u32 LE][payload]`). Keeps the in-game DLL
  small and boring, which is itself an anti-cheat virtue.
- Bonus: being in the present path gives frametime directly — no PresentMon on
  that route.
- Reference: MangoHud `src/vulkan.cpp` (MIT).

**Phase 2 — DirectX.** Hold. No defensible mechanism, and D3D11/12 is where the
online titles with kernel anti-cheat live.

## 6. Strategic options (undecided)

Given MangoHud is free, ubiquitous, and already renders inside the swapchain,
"CleanMeter on Linux" needs a distinct shape. Three candidates discussed:

1. **Desktop monitor** — sleek always-visible panel on a second monitor, not over
   games. Plays to the UI strength; layer-shell is *designed* for panels, so the
   Wayland problem largely dissolves.
2. **MangoHud frontend** — CleanMeter owns the config UI and styling (MangoHud
   has no config GUI at all), MangoHud does in-game rendering. The Tauri settings
   window writes `MangoHud.conf`. None of the sensor-collection work is needed.
3. **Full port** — own sensor collection *and* fight for the in-game overlay.
   Most work, most duplication of solved problems.

Options 2 and the Windows-layer work converge on the same ImGui renderer asset.

## 7. Scratch files — untracked, not wired in

Five files were written before scope was agreed, then set aside. They are
**untracked, not referenced by `lib.rs`, and not in `Cargo.toml`** — the build is
unaffected. They will **not** reach another machine unless committed.

```
app-tauri/src-tauri/src/model.rs        # shared wire structs, PascalCase serde
app-tauri/src-tauri/src/linux/sysfs.rs  # read helpers, hwmon discovery
app-tauri/src-tauri/src/linux/cpu.rs    # /proc/stat, hwmon temps, cpufreq, RAPL
app-tauri/src-tauri/src/linux/memory.rs # /proc/meminfo
app-tauri/src-tauri/src/linux/net.rs    # rx/tx byte deltas
```

**None of it has ever been compiled** — the dev machine had no Rust toolchain
(no `cargo`, no `rustup`). Treat as unverified drafts. GPU collection was never
written. Delete freely if the Linux path is not taken.

---

## Sources

- [Vulkan loader & layer interface](https://vulkan.lunarg.com/doc/view/1.3.290.0/mac/loader_and_layer_interface.html)
- [ImGui FONTS.md — dynamic fonts (1.92+)](https://github.com/ocornut/imgui/blob/master/docs/FONTS.md) · [1.92 dynamic fonts & texture updates](https://github.com/ocornut/imgui/issues/8465)
- [`dear-imgui-rs`](https://crates.io/crates/dear-imgui-rs) · [`ash`](https://crates.io/crates/ash) · [`vk-layer-for-rust`](https://google.github.io/vk-layer-for-rust/doc-Windows/vulkan_layer/index.html)
- [Vulkan implicit layers on Windows (registry)](https://asawicki.info/news_1683_vulkan_layers_dont_work_look_at_registry)
- [Best practices for API layers / code signing](https://fredemmott.com/blog/2024/11/25/best-practices-for-openxr-api-layers.html)
- [MangoHud](https://github.com/flightlessmango/MangoHud) · [LICENSE (MIT)](https://github.com/flightlessmango/MangoHud/blob/master/LICENSE)
- [MangoHud-Windows fork](https://github.com/Leclowndu93150/MangoHud-Windows)
- [BattlEye FAQ](https://www.battleye.com/support/faq/)
- [Easy Anti-Cheat — PCGamingWiki](https://www.pcgamingwiki.com/wiki/Easy_Anti-Cheat)
- [LibreHardwareMonitor platform discussion](https://github.com/LibreHardwareMonitor/LibreHardwareMonitor/discussions/791)
- [tao#1134 — Wayland always_on_top](https://github.com/tauri-apps/tao/issues/1134) · [tauri#14913 — Wayland positioning](https://github.com/tauri-apps/tauri/issues/14913)
