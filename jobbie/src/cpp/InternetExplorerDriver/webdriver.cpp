#include "stdafx.h"
#include "webdriver.h"
#include "InternetExplorerDriver.h"
#include "utils.h"
#include <stdio.h>

struct WebDriver {
       InternetExplorerDriver *ie;
};

struct WebElement {
		ElementWrapper *element;
};

extern "C"
{
WebDriver* webdriver_newDriverInstance()
{
	startCom();
    WebDriver *driver = new WebDriver();
   
    driver->ie = new InternetExplorerDriver();
	driver->ie->setVisible(true);

    return driver;
}

void webdriver_deleteDriverInstance(WebDriver* driver)
{
	driver->ie->close();
    delete driver->ie;
    delete driver;
}

void webdriver_get(WebDriver* driver, wchar_t* url)
{
	driver->ie->get(url);
}

void webdriver_close(WebDriver* driver)
{
	driver->ie->close();
}

const wchar_t* webdriver_getCurrentUrl(WebDriver* driver)
{
	driver->ie->getCurrentUrl();

	const std::wstring originalString(driver->ie->getCurrentUrl());
	size_t length = originalString.length() + 1;
	wchar_t* toReturn = new wchar_t[length];

	wcscpy_s(toReturn, length, originalString.c_str());
	return toReturn;
}

WebElement* webdriver_findElementById(WebDriver* handle, const wchar_t* id)
{
	ElementWrapper* wrapper = handle->ie->selectElementById(id);
	if (wrapper)
	{
		WebElement* element = new WebElement();
		element->element = wrapper;
		return element;
	}

	return NULL;
}

WebElement* webdriver_findElementByLinkText(WebDriver* handle, const wchar_t* linkText)
{
	ElementWrapper* wrapper = handle->ie->selectElementByLink(linkText);
	if (wrapper)
	{
		WebElement* element = new WebElement();
		element->element = wrapper;
		return element;
	}

	return NULL;
}

WebElement* webdriver_findElementByName(WebDriver* driver, const wchar_t* name)
{
	ElementWrapper* wrapper = driver->ie->selectElementByName(name);
	if (wrapper)
	{
		WebElement* element = new WebElement();
		element->element = wrapper;
		return element;
	}

	return NULL;
}

void webdriver_deleteElementInstance(WebElement* handle)
{
	delete handle->element;
    delete handle;
}

void webdriver_elementClear(WebElement* handle)
{
	handle->element->clear();
}

void webdriver_elementSendKeys(WebElement* handle, const wchar_t* text)
{
	handle->element->sendKeys(text);
}

void webdriver_elementSubmit(WebElement* handle)
{
	handle->element->submit();
}

void webdriver_elementClick(WebElement* handle)
{
	handle->element->click();
}

std::wstring returnAttributeString;
const wchar_t* webdriver_elementGetAttribute(WebElement* handle, const wchar_t* attributeName)
{
	returnAttributeString = handle->element->getAttribute(attributeName);
	return returnAttributeString.c_str();
}
}