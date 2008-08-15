using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    public interface IWebDriver : IDisposable
    {
        string CurrentUrl
        {
            get;
        }

        void Get(string url);

        IWebElement FindOneElement(By mechanism, string locator);
    }
}
