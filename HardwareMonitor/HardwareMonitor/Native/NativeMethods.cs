using System.Runtime.InteropServices;

namespace HardwareMonitor.Native;

public static class NativeMethods
{
    public const string LOW_INTEGRITY_SSL_SACL = "S:(ML;;NW;;;LW)";

    public static int ERROR_SUCCESS = 0x0;

    public const int LABEL_SECURITY_INFORMATION = 0x00000010;

    public enum SE_OBJECT_TYPE
    {
        SE_UNKNOWN_OBJECT_TYPE = 0,
        SE_FILE_OBJECT,
        SE_SERVICE,
        SE_PRINTER,
        SE_REGISTRY_KEY,
        SE_LMSHARE,
        SE_KERNEL_OBJECT,
        SE_WINDOW_OBJECT,
        SE_DS_OBJECT,
        SE_DS_OBJECT_ALL,
        SE_PROVIDER_DEFINED_OBJECT,
        SE_WMIGUID_OBJECT,
        SE_REGISTRY_WOW64_32KEY
    }



    [DllImport("advapi32.dll", EntryPoint = "ConvertStringSecurityDescriptorToSecurityDescriptorW")]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern Boolean ConvertStringSecurityDescriptorToSecurityDescriptor(
        [MarshalAs(UnmanagedType.LPWStr)] String strSecurityDescriptor,
        UInt32 sDRevision,
        ref IntPtr securityDescriptor,
        ref UInt32 securityDescriptorSize);

    [DllImport("kernel32.dll", EntryPoint = "LocalFree")]
    public static extern UInt32 LocalFree(IntPtr hMem);

    [DllImport("Advapi32.dll", EntryPoint = "SetSecurityInfo")]
    public static extern int SetSecurityInfo(SafeHandle hFileMappingObject,
        SE_OBJECT_TYPE objectType,
        Int32 securityInfo,
        IntPtr psidOwner,
        IntPtr psidGroup,
        IntPtr pDacl,
        IntPtr pSacl);
    [DllImport("advapi32.dll", EntryPoint = "GetSecurityDescriptorSacl")]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern Boolean GetSecurityDescriptorSacl(
        IntPtr pSecurityDescriptor,
        out IntPtr lpbSaclPresent,
        out IntPtr pSacl,
        out IntPtr lpbSaclDefaulted);
}
