using System;
using System.Collections.Generic;
using System.Text;
using com.googlecode.webdriver;

namespace com.googlecode.webdriver.ie
{
    class Example
    {
        static void Main(string[] args)
        {
            IWebDriver driver = new InternetExplorerDriver();

            driver.Get("http://www.google.com");

            Console.WriteLine("URL is: " + driver.CurrentUrl);

            IWebElement query = driver.FindOneElement(By.NAME, "q");
            query.SendKeys("cheese");

            Console.WriteLine("\nDang");
            System.Threading.Thread.Sleep(5000);
        }
    }
}
