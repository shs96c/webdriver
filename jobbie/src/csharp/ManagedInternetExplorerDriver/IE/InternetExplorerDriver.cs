using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace OpenQa.Selenium.IE
{
    public class InternetExplorerDriver : IWebDriver
    {
        [DllImport("InternetExplorerDriver")]
        private static extern SafeInternetExplorerDriverHandle webdriver_newDriverInstance();
        public InternetExplorerDriver()
        {
            handle = webdriver_newDriverInstance();
        }

        public void Dispose()
        {
            if (!disposed)
            {
                handle.Dispose();
                disposed = true;
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern void webdriver_get(SafeHandle handle, string url);
        public void Get(string url)
        {
            if (disposed)
            {
                throw new ObjectDisposedException("handle");
            }
            webdriver_get(handle, url);
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern string webdriver_getCurrentUrl(SafeHandle handle);
        public string CurrentUrl
        {
            get
            {
                return webdriver_getCurrentUrl(handle);
            }
        }

        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern IntPtr webdriver_findElementById(SafeHandle handle, [MarshalAs(UnmanagedType.LPWStr)] String id);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern IntPtr webdriver_findElementByLinkText(SafeHandle handle, [MarshalAs(UnmanagedType.LPWStr)] String linkText);
        [DllImport("InternetExplorerDriver", CharSet = CharSet.Unicode)]
        private static extern IntPtr webdriver_findElementByName(SafeHandle handle, [MarshalAs(UnmanagedType.LPWStr)] String name);
        public IWebElement FindOneElement(By mechanism, string locator)
        {
            IntPtr rawElement;

            try
            {
                switch (mechanism)
                {
                    case By.Id:
                        rawElement = webdriver_findElementById(handle, locator);
                        break;

                    case By.LinkText:
                        rawElement = webdriver_findElementByLinkText(handle, locator);
                        break;

                    case By.Name:
                        rawElement = webdriver_findElementByName(handle, locator);
                        break;

                    default:
                        throw new ArgumentException("Unrecognised element location mechanism: " + mechanism);
                }

                return new InternetExplorerWebElement(rawElement);
            }
            catch (SEHException)
            {
                // Unable to find the element
                return null;
            }
        }

        private bool disposed = false;
        private SafeInternetExplorerDriverHandle handle;
    }
}
