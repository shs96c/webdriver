using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace com.googlecode.webdriver.ie
{
    class InternetExplorerWebElement : IWebElement
    {
        public InternetExplorerWebElement(IntPtr handle)
        {
            this.handle = handle;
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_deleteElementInstance(IntPtr handle);
        ~InternetExplorerWebElement()
        {
            webdriver_deleteElementInstance(handle);
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_elementSendKeys(IntPtr handle,
            [MarshalAs(UnmanagedType.LPWStr)]
            string text);
        public void SendKeys(string text)
        {
            webdriver_elementSendKeys(handle, text);
        }

        private IntPtr handle;
    }
}
