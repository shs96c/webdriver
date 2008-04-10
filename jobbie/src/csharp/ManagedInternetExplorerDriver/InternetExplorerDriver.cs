using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

using IWebDriver = com.googlecode.webdriver.IWebDriver;

namespace com.googlecode.webdriver.ie
{
    public class InternetExplorerDriver : IWebDriver
    {
        [DllImport("InternetExplorerDriver")]
        private static extern IntPtr webdriver_newDriverInstance();
        public InternetExplorerDriver()
        {
            handle = webdriver_newDriverInstance();
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_deleteDriverInstance(IntPtr handle);
        ~InternetExplorerDriver()
        {
            Close();
            webdriver_deleteDriverInstance(handle);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void webdriver_get(IntPtr handle, string url);
        public void Get(string url)
        {
            webdriver_get(handle, url);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern string webdriver_getCurrentUrl(IntPtr handle);
        public string CurrentUrl
        {
            get
            {
                Console.WriteLine("Getting current url: " + handle);
                return webdriver_getCurrentUrl(handle);
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern IntPtr webdriver_findElementByName(IntPtr handle, string url);
        public IWebElement FindOneElement(By mechanism, string locator)
        {
            IntPtr rawElement = webdriver_findElementByName(handle, locator);
            return new InternetExplorerWebElement(rawElement);
        }

        [DllImport("InternetExplorerDriver")]
        private static extern void webdriver_close(IntPtr driver);
        public void Close()
        {
            webdriver_close(handle);
        }

        private IntPtr handle;
    }
}
