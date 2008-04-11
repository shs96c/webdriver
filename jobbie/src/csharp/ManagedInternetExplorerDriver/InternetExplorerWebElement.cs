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

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern uint webdriver_elementGetTextLength(IntPtr handle);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void webdriver_elementGetText(
            IntPtr handle, [Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder res, uint resLength);
        public string Text
        {
            get 
            {
                uint length = webdriver_elementGetTextLength(handle);
                StringBuilder result = new StringBuilder((int) length);
                webdriver_elementGetText(handle, result, length);
                return result.ToString();
            }
        }


        [DllImport("InternetExplorerDriver")]
        private static extern bool webdriver_isElementVisible(IntPtr handle);
        public bool Visible
        {
            get { return webdriver_isElementVisible(handle); }
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
            StringBuilder result = new StringBuilder((int) length);
            webdriver_elementGetAttribute(handle, attributeName, result, length);
            return result.ToString();
        }

        private IntPtr handle;
    }
}
