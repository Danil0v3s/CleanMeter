package win32;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32Impl extends com.sun.jna.platform.win32.Kernel32 {
    Kernel32Impl INSTANCE = Native.load("kernel32", Kernel32Impl.class, W32APIOptions.DEFAULT_OPTIONS);
    HANDLE OpenFileMapping(int lfProtect, boolean bInherit, String lpName);
    HANDLE OpenEvent(int i, boolean bManualReset, String lpName);
}