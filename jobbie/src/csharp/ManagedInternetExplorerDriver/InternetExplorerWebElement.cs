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
        private static extern uint webdriver_elementGetAttributeLength(IntPtr handle, [MarshalAs(UnmanagedType.LPWStr)] string attributeName);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void webdriver_elementGetAttribute(
            IntPtr handle, [MarshalAs(UnmanagedType.LPWStr)] string attributeName, [Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder res, uint resLength);
        public string GetAttribute(string attributeName)
        {
            uint length = webdriver_elementGetAttributeLength(handle, attributeName);
            StringBuilder result = new StringBuilder((int) length + 1);
            webdriver_elementGetAttribute(handle, attributeName, result, length + 1);
            Console.WriteLine("In method: " + result);
            return result.ToString();
        }

        private IntPtr handle;
    }
}
