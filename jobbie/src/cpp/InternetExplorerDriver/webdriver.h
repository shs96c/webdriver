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

EXPORT WebElement* webdriver_findElementByName(WebDriver* handle, const wchar_t* name);

EXPORT void webdriver_elementSendKeys(WebElement* handle, const wchar_t* text);

#ifdef __cplusplus
}
#endif