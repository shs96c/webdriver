#include "stdafx.h"
#include <ExDisp.h>
#include "utils.h"
#include "ElementWrapper.h"
#include <iostream>
#include <vector>
#include <iterator>

using namespace std;

ElementWrapper* getWrapper(JNIEnv *env, jobject obj)
{
	jclass cls = env->GetObjectClass(obj);
	jfieldID fid = env->GetFieldID(cls, "nodePointer", "J");
	jlong value = env->GetLongField(obj, fid);

	return (ElementWrapper *) value;
}

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_click
  (JNIEnv *env, jobject obj) 
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	wrapper->click();
}

JNIEXPORT jstring JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getValue
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);

	return wstring2jstring(env, wrapper->getValue());
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_doSendKeys
  (JNIEnv *env, jobject obj, jstring newValue)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	wchar_t* converted = (wchar_t*) env->GetStringChars(newValue, NULL);
	wrapper->sendKeys(converted);
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_clear
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	wrapper->clear();
}

JNIEXPORT jstring JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getText
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	return wstring2jstring(env, wrapper->getText());
}

JNIEXPORT jstring JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getAttribute
  (JNIEnv *env, jobject obj, jstring attributeName)
{
	ElementWrapper* wrapper = getWrapper(env, obj);

	const wchar_t* converted = (wchar_t*) env->GetStringChars(attributeName, NULL);
	return wstring2jstring(env, wrapper->getAttribute(converted));
}

JNIEXPORT jboolean JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_isEnabled
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	return wrapper->isEnabled() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_isSelected
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	return wrapper->isSelected() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_setSelected
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	try {
		wrapper->setSelected();
	} catch (const char *message) {
		throwUnsupportedOperationException(env, message);
	}
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_submit
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	try {
		wrapper->submit();
	} catch (const char* message) {
		throwNoSuchElementException(env, message);
	}
}

JNIEXPORT jboolean JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_toggle
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	return wrapper->toggle() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_isDisplayed
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	return wrapper->isDisplayed() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jobject JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getLocation
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	long x = wrapper->getX();
	long y = wrapper->getY();

	jclass pointClass = env->FindClass("java/awt/Point");
	jmethodID cId = env->GetMethodID(pointClass, "<init>", "(II)V");
	
	return env->NewObject(pointClass, cId, x, y);
}

JNIEXPORT jobject JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getSize
  (JNIEnv *env, jobject obj) 
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	long width = wrapper->getWidth();
	long height = wrapper->getHeight();

	jclass pointClass = env->FindClass("java/awt/Dimension");
	jmethodID cId = env->GetMethodID(pointClass, "<init>", "(II)V");

	return env->NewObject(pointClass, cId, width, height);
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getChildrenOfTypeNatively
  (JNIEnv *env, jobject obj, jobject list, jstring tagName)
{
	jclass listClass = env->FindClass("java/util/List");
	jmethodID addId = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

	jclass ieeClass = env->FindClass("com/googlecode/webdriver/ie/InternetExplorerElement");
	jmethodID cId = env->GetMethodID(ieeClass, "<init>", "(J)V");

	const wchar_t* converted = (wchar_t*) env->GetStringChars(tagName, NULL);
	ElementWrapper* wrapper = getWrapper(env, obj);
	const std::vector<ElementWrapper*>* elements = wrapper->getChildrenWithTagName(converted);

	std::vector<ElementWrapper*>::const_iterator end = elements->end();
	std::vector<ElementWrapper*>::const_iterator cur = elements->begin();

	while(cur < end)
	{
		ElementWrapper* wrapper = *cur;
		jobject wrapped = env->NewObject(ieeClass, cId, wrapper);
		env->CallVoidMethod(list, addId, wrapped);
		cur++;
	}
}

JNIEXPORT void JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_deleteStoredObject
  (JNIEnv *env, jobject obj)
{
	ElementWrapper* wrapper = getWrapper(env, obj);
	delete wrapper;
}

JNIEXPORT jstring JNICALL Java_com_googlecode_webdriver_ie_InternetExplorerElement_getValueOfCssProperty
  (JNIEnv *env, jobject obj, jstring propertyName)
{
	ElementWrapper* wrapper = getWrapper(env, obj);

	const wchar_t* converted = (wchar_t*) env->GetStringChars(propertyName, NULL);
	return wstring2jstring(env, wrapper->getValueOfCssProperty(converted));
}

#ifdef __cplusplus
}
#endif
