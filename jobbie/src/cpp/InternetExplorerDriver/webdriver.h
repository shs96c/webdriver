#pragma once

#include <wchar.h>

#define EXPORT __declspec(dllexport)

#ifdef __cplusplus
extern "C" {
#endif

struct WebDriver;
typedef struct WebDriver WebDriver;

struct WebElement;
typedef struct WebElement WebElement;

EXPORT WebDriver* webdriver_newDriverInstance();
EXPORT void webdriver_deleteDriverInstance(WebDriver* handle);
EXPORT void webdriver_deleteElementInstance(WebElement* handle);

EXPORT void webdriver_get(WebDriver* driver, wchar_t* url);
EXPORT void webdriver_close(WebDriver* driver);

EXPORT const wchar_t* webdriver_getCurrentUrl(WebDriver* driver);

EXPORT WebElement* webdriver_findElementById(WebDriver* handle, const wchar_t* id);
EXPORT WebElement* webdriver_findElementByLinkText(WebDriver* handle, const wchar_t* linkText);
EXPORT WebElement* webdriver_findElementByName(WebDriver* handle, const wchar_t* name);

EXPORT void webdriver_elementClear(WebElement* handle);
EXPORT void webdriver_elementSendKeys(WebElement* handle, const wchar_t* text);
EXPORT void webdriver_elementSubmit(WebElement* handle);

EXPORT void webdriver_elementClick(WebElement* handle);

EXPORT const wchar_t* webdriver_elementGetAttribute(WebElement* handle, const wchar_t* attributeName);

#ifdef __cplusplus
}
#endif