using System;
using System.Collections.Generic;
using System.Text;

namespace com.googlecode.webdriver
{
    public interface IWebDriver
    {
        string CurrentUrl
        {
            get;
        }

        void Get(string url);

        void Close();
    }
}
