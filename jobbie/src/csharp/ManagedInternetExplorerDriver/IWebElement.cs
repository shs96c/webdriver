using System;
using System.Collections.Generic;
using System.Text;

namespace OpenQa.Selenium
{
    public interface IWebElement
    {
        string Text
        {
            get;
        }

        bool Visible
        {
            get;
        }

        void Clear();
        void SendKeys(string text);

        void Submit();

        void Click();

        string GetAttribute(string attributeName);
    }
}
