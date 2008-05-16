package com.googlecode.webdriver.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.webdriver.How;

/**
 * Used to mark a field on a Page Object to indicate an alternative mechanism
 * for locating the element. Used in conjunction with
 * {@link com.googlecode.webdriver.support.PageFactory#proxyElement(com.googlecode.webdriver.WebDriver, Object, java.lang.reflect.Field)}
 * this allows users to quickly and easily create PageObjects
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindBy {
    How how() default How.ID;
    String using();
}
