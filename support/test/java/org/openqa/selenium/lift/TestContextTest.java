package org.openqa.selenium.lift;

import static org.openqa.selenium.lift.match.NumericalMatchers.atLeast;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.lift.find.Finder;

/**
 * Unit test for {@link WebDriverTestContext}.
 * 
 * @author rchatley (Robert Chatley)
 *
 */
public class TestContextTest extends MockObjectTestCase {

	WebDriver webdriver = mock(WebDriver.class);
	TestContext context = new WebDriverTestContext(webdriver);
	private WebElement element = mock(WebElement.class);
	
	public void testIsCreatedWithAWebDriverImplementation() throws Exception {
		new WebDriverTestContext(webdriver);
	}
	
	public void testCanNavigateToAGivenUrl() throws Exception {

		final String url = "http://www.example.com";
		
		checking(new Expectations() {{ 
			one(webdriver).get(url);
		}});
		
		context.goTo(url);
	}
	
	@SuppressWarnings("unchecked")
	public void testCanAssertPresenceOfWebElements() throws Exception {
		
		final Finder<WebElement, WebDriver> finder = mock(Finder.class);
		
		checking(new Expectations() {{ 
			one(finder).findFrom(webdriver); will(returnValue(oneElement()));
		}});
		
		context.assertPresenceOf(finder);
	}
	
	@SuppressWarnings("unchecked")
	public void testCanCheckQuantitiesOfWebElementsAndThrowsExceptionOnMismatch() throws Exception {
		
		final Finder<WebElement, WebDriver> finder = mock(Finder.class);
		
		checking(new Expectations() {{ 
			allowing(finder).findFrom(webdriver); will(returnValue(oneElement()));
			exactly(2).of(finder).describeTo(with(any(Description.class))); // in producing the error msg
		}});
		
		try {
			context.assertPresenceOf(atLeast(2), finder);
			fail("should have failed as only one element found");
		} catch (AssertionError error) {
			// expected
			assertThat(error.getMessage(), containsString("a value greater than <1>"));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testCanDirectTextInputToSpecificElements() throws Exception {
		 
		final Finder<WebElement, WebDriver> finder = mock(Finder.class);
		final String inputText = "test";
		
		checking(new Expectations() {{ 
			one(finder).findFrom(webdriver); will(returnValue(oneElement()));
			one(element).sendKeys(inputText);
		}});
		
		context.type(inputText, finder);
	}
	
	@SuppressWarnings("unchecked")
	public void testCanTriggerClicksOnSpecificElements() throws Exception {
		 
		final Finder<WebElement, WebDriver> finder = mock(Finder.class);
		
		checking(new Expectations() {{ 
			one(finder).findFrom(webdriver); will(returnValue(oneElement()));
			one(element).click();
		}});
		
		context.clickOn(finder);
	}
	
	@SuppressWarnings("unchecked")
	public void testThrowsAnExceptionIfTheFinderReturnsAmbiguousResults() throws Exception {
		 
		final Finder<WebElement, WebDriver> finder = mock(Finder.class);
		
		checking(new Expectations() {{ 
			one(finder).findFrom(webdriver); will(returnValue(twoElements()));
		}});
		
		try {
			context.clickOn(finder);
			fail("should have failed as more than one element found");
		} catch (AssertionError error) {
			// expected
			assertThat(error.getMessage(), containsString("did not know what to click on"));
		}
	}
	
	private Collection<WebElement> oneElement() {
		return Collections.singleton(element);
	}
	
	private Collection<WebElement> twoElements() {
		return Arrays.asList(new WebElement[] {element, element});
	}
}
