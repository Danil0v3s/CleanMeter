using System.ComponentModel;
using System.Runtime.InteropServices;

namespace HardwareMonitor.Native;

public class InterProcessSecurity
{

    public static void SetLowIntegrityLevel(SafeHandle hObject)
    {
        IntPtr pSD = IntPtr.Zero;
        IntPtr pSacl;
        IntPtr lpbSaclPresent;
        IntPtr lpbSaclDefaulted;
        uint securityDescriptorSize = 0;

        if (NativeMethods.ConvertStringSecurityDescriptorToSecurityDescriptor(NativeMethods.LOW_INTEGRITY_SSL_SACL, 1, ref pSD, ref securityDescriptorSize))
        {
            if (NativeMethods.GetSecurityDescriptorSacl(pSD, out lpbSaclPresent, out pSacl, out lpbSaclDefaulted))
            {
                var err = NativeMethods.SetSecurityInfo(hObject,
                    NativeMethods.SE_OBJECT_TYPE.SE_KERNEL_OBJECT,
                    NativeMethods.LABEL_SECURITY_INFORMATION,
                    IntPtr.Zero,
                    IntPtr.Zero,
                    IntPtr.Zero,
                    pSacl);
                if (err != NativeMethods.ERROR_SUCCESS)
                {
                    throw new Win32Exception(err);
                }
            }
            NativeMethods.LocalFree(pSD);
        }
    }
}