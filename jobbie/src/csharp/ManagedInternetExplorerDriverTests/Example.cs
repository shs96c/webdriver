using System;
using System.Collections.Generic;
using System.Text;
using com.googlecode.webdriver;
using com.googlecode.webdriver.ie;

namespace com.googlecode.webdriver.ie.ManagedInternetExplorerDriverTests
{
    class Example
    {
        static void Main(string[] args)
        {
            IWebDriver driver = new InternetExplorerDriver();

            driver.Get("http://www.google.com");

            Console.WriteLine("URL is: " + driver.CurrentUrl);
        }
    }
}
