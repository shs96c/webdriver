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
        private static extern void webdriver_elementClear(IntPtr handle);
        public void Clear()
        {
            webdriver_elementClear(handle);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void webdriver_elementSendKeys(IntPtr handle, [MarshalAs(UnmanagedType.LPWStr)] string text);
        public void SendKeys(string text)
        {
            webdriver_elementSendKeys(handle, text);
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_elementSubmit(IntPtr handle);
        public void Submit()
        {
            webdriver_elementSubmit(handle);
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_elementClick(IntPtr handle);
        public void Click()
        {
            webdriver_elementClick(handle);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern IntPtr webdriver_elementGetAttribute(
            IntPtr handle, [MarshalAs(UnmanagedType.LPWStr)] string attributeName);
        public string GetAttribute(string attributeName)
        {
            IntPtr result = webdriver_elementGetAttribute(handle, attributeName);
            return Marshal.PtrToStringUni(result);
        }

        private IntPtr handle;
    }
}
