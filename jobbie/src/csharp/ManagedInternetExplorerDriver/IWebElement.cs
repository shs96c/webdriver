using System;
using System.Collections.Generic;
using System.Text;

namespace com.googlecode.webdriver
{
    public interface IWebElement
    {
        void Clear();
        void SendKeys(string text);

        void Submit();

        void Click();

        string GetAttribute(string attributeName);
    }
}
