using Microsoft.Win32.SafeHandles;
using System.Runtime.InteropServices;
using System;

namespace OpenQa.Selenium.IE
{
    internal class SafeInternetExplorerDriverHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        private SafeInternetExplorerDriverHandle()
            : base(true)
        {
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_deleteDriverInstance(IntPtr handle);

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_close(IntPtr driver);

        protected override bool ReleaseHandle()
        {
            webdriver_close(handle);
            webdriver_deleteDriverInstance(handle);
            // TODO(simonstewart): Are we really always successful?
            return true;
        }
    }
}
