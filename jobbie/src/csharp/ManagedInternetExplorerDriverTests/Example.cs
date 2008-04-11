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
            query.Clear();
            query.SendKeys("cheese");
            query.Submit();

            IWebElement link = driver.FindOneElement(By.LINK_TEXT, "Cheese - Wikipedia, the free encyclopedia");
            Console.WriteLine("Returned: " + link.GetAttribute("href"));

            Console.WriteLine("Is link visible? " + link.Visible);
            Console.WriteLine("Link text: " + link.Text);
            link.Click();

            System.Threading.Thread.Sleep(5000);
        }
    }
}
