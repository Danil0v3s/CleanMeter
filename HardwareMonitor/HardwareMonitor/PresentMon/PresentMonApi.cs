// P/Invoke bindings for Intel PresentMon's client API (PresentMonAPI2Loader.dll).
//
// The loader DLL re-exports the whole `pm*` surface and forwards to the middleware
// (PresentMonAPI2.dll), which in turn talks to PresentMonSharedService over a named
// pipe. So HardwareMonitor never opens an ETW session itself: it asks the service to
// track a PID and reads aggregated metrics back out of shared memory.
//
// Enum values mirror SDK/PresentMonAPI.h (API 3.3, shipped with PresentMon 2.5.1)
// EXACTLY in declaration order — do not reorder.

using System.Runtime.InteropServices;

namespace HardwareMonitor.PresentMon;

internal static class PresentMonApi
{
    // The loader is flat-copied next to HardwareMonitor.exe (see presentmon/ + the
    // csproj CopyPresentMon target), so the default DLL search resolves it.
    private const string Dll = "PresentMonAPI2Loader.dll";

    // PM_STATUS — only the codes we branch on. 0 == success for every call.
    public const int PM_STATUS_SUCCESS = 0;
    public const int PM_STATUS_ALREADY_TRACKING_PROCESS = 8;

    /// <summary>PM_STAT — statistic applied over the dynamic-query window.</summary>
    public enum PM_STAT
    {
        PM_STAT_NONE,
        PM_STAT_AVG,
        PM_STAT_PERCENTILE_99,
        PM_STAT_PERCENTILE_95,
        PM_STAT_PERCENTILE_90,
        PM_STAT_PERCENTILE_01,
        PM_STAT_PERCENTILE_05,
        PM_STAT_PERCENTILE_10,
        PM_STAT_MAX,
        PM_STAT_MIN,
        PM_STAT_MID_POINT,
        PM_STAT_MID_LERP,
        PM_STAT_NEWEST_POINT,
        PM_STAT_OLDEST_POINT,
        PM_STAT_COUNT,
        PM_STAT_NON_ZERO_AVG,
    }

    /// <summary>
    /// PM_METRIC — full enum transcribed in order so the underlying integer values
    /// match the header. We only reference a handful, but a partial enum would assign
    /// wrong values, so keep it complete.
    /// </summary>
    public enum PM_METRIC
    {
        PM_METRIC_APPLICATION,
        PM_METRIC_SWAP_CHAIN_ADDRESS,
        PM_METRIC_GPU_VENDOR,
        PM_METRIC_GPU_NAME,
        PM_METRIC_CPU_VENDOR,
        PM_METRIC_CPU_NAME,
        PM_METRIC_CPU_START_TIME,
        PM_METRIC_CPU_START_QPC,
        PM_METRIC_CPU_FRAME_TIME,
        PM_METRIC_CPU_BUSY,
        PM_METRIC_CPU_WAIT,
        PM_METRIC_DISPLAYED_FPS,
        PM_METRIC_PRESENTED_FPS,
        PM_METRIC_GPU_TIME,
        PM_METRIC_GPU_BUSY,
        PM_METRIC_GPU_WAIT,
        PM_METRIC_DROPPED_FRAMES,
        PM_METRIC_DISPLAYED_TIME,
        PM_METRIC_SYNC_INTERVAL,
        PM_METRIC_PRESENT_FLAGS,
        PM_METRIC_PRESENT_MODE,
        PM_METRIC_PRESENT_RUNTIME,
        PM_METRIC_ALLOWS_TEARING,
        PM_METRIC_GPU_LATENCY,
        PM_METRIC_DISPLAY_LATENCY,
        PM_METRIC_CLICK_TO_PHOTON_LATENCY,
        PM_METRIC_GPU_SUSTAINED_POWER_LIMIT,
        PM_METRIC_GPU_POWER,
        PM_METRIC_GPU_VOLTAGE,
        PM_METRIC_GPU_FREQUENCY,
        PM_METRIC_GPU_TEMPERATURE,
        PM_METRIC_GPU_FAN_SPEED,
        PM_METRIC_GPU_UTILIZATION,
        PM_METRIC_GPU_RENDER_COMPUTE_UTILIZATION,
        PM_METRIC_GPU_MEDIA_UTILIZATION,
        PM_METRIC_GPU_POWER_LIMITED,
        PM_METRIC_GPU_TEMPERATURE_LIMITED,
        PM_METRIC_GPU_CURRENT_LIMITED,
        PM_METRIC_GPU_VOLTAGE_LIMITED,
        PM_METRIC_GPU_UTILIZATION_LIMITED,
        PM_METRIC_GPU_MEM_POWER,
        PM_METRIC_GPU_MEM_VOLTAGE,
        PM_METRIC_GPU_MEM_FREQUENCY,
        PM_METRIC_GPU_MEM_EFFECTIVE_FREQUENCY,
        PM_METRIC_GPU_MEM_TEMPERATURE,
        PM_METRIC_GPU_MEM_SIZE,
        PM_METRIC_GPU_MEM_USED,
        PM_METRIC_GPU_MEM_UTILIZATION,
        PM_METRIC_GPU_MEM_MAX_BANDWIDTH,
        PM_METRIC_GPU_MEM_WRITE_BANDWIDTH,
        PM_METRIC_GPU_MEM_READ_BANDWIDTH,
        PM_METRIC_GPU_MEM_POWER_LIMITED,
        PM_METRIC_GPU_MEM_TEMPERATURE_LIMITED,
        PM_METRIC_GPU_MEM_CURRENT_LIMITED,
        PM_METRIC_GPU_MEM_VOLTAGE_LIMITED,
        PM_METRIC_GPU_MEM_UTILIZATION_LIMITED,
        PM_METRIC_CPU_UTILIZATION,
        PM_METRIC_CPU_POWER_LIMIT,
        PM_METRIC_CPU_POWER,
        PM_METRIC_CPU_TEMPERATURE,
        PM_METRIC_CPU_FREQUENCY,
        PM_METRIC_CPU_CORE_UTILITY,
        PM_METRIC_APPLICATION_FPS,
        PM_METRIC_FRAME_TYPE,
        PM_METRIC_ANIMATION_ERROR,
        PM_METRIC_ALL_INPUT_TO_PHOTON_LATENCY,
        PM_METRIC_INSTRUMENTED_LATENCY,
        PM_METRIC_ANIMATION_TIME,
        PM_METRIC_GPU_EFFECTIVE_FREQUENCY,
        PM_METRIC_GPU_VOLTAGE_REGULATOR_TEMPERATURE,
        PM_METRIC_GPU_MEM_EFFECTIVE_BANDWIDTH,
        PM_METRIC_GPU_OVERVOLTAGE_PERCENT,
        PM_METRIC_GPU_TEMPERATURE_PERCENT,
        PM_METRIC_GPU_POWER_PERCENT,
        PM_METRIC_GPU_FAN_SPEED_PERCENT,
        PM_METRIC_GPU_CARD_POWER,
        PM_METRIC_PRESENT_START_TIME,
        PM_METRIC_PRESENT_START_QPC,
        PM_METRIC_BETWEEN_PRESENTS,
        PM_METRIC_IN_PRESENT_API,
        PM_METRIC_BETWEEN_DISPLAY_CHANGE,
        PM_METRIC_UNTIL_DISPLAYED,
        PM_METRIC_RENDER_PRESENT_LATENCY,
        PM_METRIC_BETWEEN_SIMULATION_START,
        PM_METRIC_PC_LATENCY,
        PM_METRIC_DISPLAYED_FRAME_TIME,
        PM_METRIC_BETWEEN_APP_START,
        PM_METRIC_PRESENTED_FRAME_TIME,
        PM_METRIC_FLIP_DELAY,
        PM_METRIC_PROCESS_ID,
        PM_METRIC_SESSION_START_QPC,
        PM_METRIC_COUNT_,
    }

    /// <summary>
    /// PM_QUERY_ELEMENT — blittable, matches the native 32-byte layout
    /// (metric@0, stat@4, deviceId@8, arrayIndex@12, dataOffset@16, dataSize@24).
    /// The register call fills in dataOffset/dataSize.
    /// </summary>
    [StructLayout(LayoutKind.Sequential)]
    public struct PM_QUERY_ELEMENT
    {
        public PM_METRIC metric;
        public PM_STAT stat;
        public uint deviceId;
        public uint arrayIndex;
        public ulong dataOffset;
        public ulong dataSize;
    }

    // --- session lifecycle ---

    [DllImport(Dll)]
    public static extern int pmOpenSession(out IntPtr pHandle);

    [DllImport(Dll)]
    public static extern int pmCloseSession(IntPtr handle);

    // --- process tracking (service opens/reads ETW for these PIDs on our behalf) ---

    [DllImport(Dll)]
    public static extern int pmStartTrackingProcess(IntPtr handle, uint processId);

    [DllImport(Dll)]
    public static extern int pmStopTrackingProcess(IntPtr handle, uint processId);

    // --- dynamic (windowed, statistic-processed) queries ---

    [DllImport(Dll)]
    public static extern int pmRegisterDynamicQuery(
        IntPtr sessionHandle,
        out IntPtr pHandle,
        [In, Out] PM_QUERY_ELEMENT[] pElements,
        ulong numElements,
        double windowSizeMs,
        double metricOffsetMs);

    [DllImport(Dll)]
    public static extern int pmFreeDynamicQuery(IntPtr handle);

    [DllImport(Dll)]
    public static extern int pmPollDynamicQuery(
        IntPtr handle,
        uint processId,
        [In, Out] byte[] pBlob,
        ref uint numSwapChains);

    /// <summary>
    /// Override the registry lookup for the middleware DLL path. We point it at the
    /// PresentMonAPI2.dll we bundle next to us, so resolution never depends on the
    /// service having written HKLM yet (and pins us to the version we ship).
    /// </summary>
    [DllImport(Dll, CharSet = CharSet.Ansi)]
    public static extern void pmLoaderSetPathToMiddlewareDll_(string path);
}
