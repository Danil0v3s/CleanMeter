package win32

import com.sun.jna.Native
import com.sun.jna.WString
import com.sun.jna.platform.win32.ShellAPI
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

interface Shell32Impl : ShellAPI, StdCallLibrary {
    companion object {
        val INSTANCE = Native.load("shell32", Shell32Impl::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun ShellExecuteW(hwnd: HWND?, lpOperation: String, lpFile: String, lpParameters: String?, lpDirectory: String?, nShowCmd: Int): WinDef.HINSTANCE
}