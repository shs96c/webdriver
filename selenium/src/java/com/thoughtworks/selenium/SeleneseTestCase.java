/*
 * Copyright 2004 ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.thoughtworks.selenium;


import java.io.*;
import java.lang.reflect.*;
import java.util.regex.Pattern;

import junit.framework.*;

/**
 * Provides a JUnit TestCase base class that implements some handy functionality 
 * for Selenium testing (you are <i>not</i> required to extend this class).
 * 
 * <p>This class adds a number of "verify" commands, which are like "assert" commands,
 * but they don't stop the test when they fail.  Instead, verification errors are all
 * thrown at once during tearDown.</p>
 * 
 * @author Nelson Sproul (nsproul@bea.com) Mar 13-06
 */
public class SeleneseTestCase extends TestCase {

    private static final boolean THIS_IS_WINDOWS = File.pathSeparator.equals(";");
    
    private boolean captureScreetShotOnFailure = false;
    
    /** Use this object to run all of your selenium tests */
    protected Selenium selenium;
    
    private StringBuffer verificationErrors = new StringBuffer();
    
    public SeleneseTestCase() {
        super();
    }
    
	public SeleneseTestCase(String name) {
        super(name);
    }

    /** Calls this.setUp(null)
	 * @see #setUp(String)
	 */
    public void setUp() throws Exception {
        super.setUp();
        this.setUp(null);
    }

    /**
     * Runs the bare test sequence, capturing a screenshot if a test fails
     * @exception Throwable if any exception is thrown
     */
    // @Override
    public void runBare() throws Throwable {
        if (!isCaptureScreetShotOnFailure()) {
            super.runBare();
            return;
        }
        setUp();
        try {
            runTest();
        } catch (Throwable t) {
            if (selenium != null) {
                String filename = getName() + ".png";
                try {
                    selenium.captureScreenshot(filename);
                    System.err.println("Saved screenshot " + filename);
                } catch (Exception e) {
                    System.err.println("Couldn't save screenshot " + filename + ": " + e.getMessage());
                    e.printStackTrace();
                }
                throw t;
            }
        }
        finally {
            tearDown();
        }
    }
    
    
    /**
     * Calls this.setUp with the specified url and a default browser.  On Windows, the default browser is *iexplore; otherwise, the default browser is *firefox.
     * @see #setUp(String, String)
     * @param url the baseUrl to use for your Selenium tests
     * @throws Exception
     * 
     */
    public void setUp(String url) throws Exception {
      if(THIS_IS_WINDOWS){
	     setUp(url, "*iexplore");
      }else{
	     setUp(url, "*firefox");
      } 
    }
    
    /**
     * Creates a new DefaultSelenium object and starts it using the specified baseUrl and browser string
     * @param url the baseUrl for your tests
     * @param browserString the browser to use, e.g. *firefox
     * @throws Exception
     */
    public void setUp(String url, String browserString) throws Exception {
        super.setUp();
        int port = getDefaultPort();
        if (url==null) {
            url = "http://localhost:" + port;
;
        }
        selenium = new DefaultSelenium("localhost", port, browserString, url);
        selenium.start();
        selenium.setContext(this.getClass().getSimpleName() + "." + getName());
    }

    private int getDefaultPort() {
        try {
            Class c = Class.forName("org.openqa.selenium.server.SeleniumServer");
            Method getDefaultPort = c.getMethod("getDefaultPort", new Class[0]);
            Integer portNumber = (Integer)getDefaultPort.invoke(null, new Object[0]);
            return portNumber.intValue();
        } catch (Exception e) {
            return 4444;
        }
    }

    /** Like assertTrue, but fails at the end of the test (during tearDown) */
    public void verifyTrue(boolean b) {
        try {
            assertTrue(b);
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }
    
    /** Like assertFalse, but fails at the end of the test (during tearDown) */
    public void verifyFalse(boolean b) {
        try {
            assertFalse(b);
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }
    
    /** Returns the body text of the current page */
    public String getText() {
        return selenium.getEval("this.page().bodyText()");
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(Object s1, Object s2) {
        try {
            assertEquals(s1, s2);
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }
    
    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(boolean s1, boolean s2) {
        try {
            assertEquals(new Boolean(s1), new Boolean(s2));
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }

    /** Like JUnit's Assert.assertEquals, but knows how to compare string arrays */
    public static void assertEquals(Object s1, Object s2) {
        if (s1 instanceof String && s2 instanceof String) {
            assertEquals((String)s1, (String)s2);
        } else if (s1 instanceof String && s2 instanceof String[]) {
            assertEquals((String)s1, (String[])s2);
        } else if (s1 instanceof String && s2 instanceof Number) {
            assertEquals((String)s1, ((Number)s2).toString());
        }
        else {
            if (s1 instanceof String[] && s2 instanceof String[]) {
                
                String[] sa1 = (String[]) s1;
                String[] sa2 = (String[]) s2;
                if (sa1.length!=sa2.length) {
                    throw new AssertionFailedError("Expected " + sa1 + " but saw " + sa2);
                }
                for (int j = 0; j < sa1.length; j++) {
                    Assert.assertEquals(sa1[j], sa2[j]);
                }
            }
        }
    }
    
    /** Like JUnit's Assert.assertEquals, but handles "regexp:" strings like HTML Selenese */ 
    public static void assertEquals(String s1, String s2) {
        assertTrue("Expected \"" + s1 + "\" but saw \"" + s2 + "\" instead", seleniumEquals(s1, s2));
    }
    
    /** Like JUnit's Assert.assertEquals, but joins the string array with commas, and 
     * handles "regexp:" strings like HTML Selenese
     */
    public static void assertEquals(String s1, String[] s2) {
    	assertEquals(s1, stringArrayToSimpleString(s2));
    }
    
    /** Compares two strings, but handles "regexp:" strings like HTML Selenese
     * 
     * @param expectedPattern
     * @param actual
     * @return true if actual matches the expectedPattern, or false otherwise
     */
    public static boolean seleniumEquals(String expectedPattern, String actual) {
        if (actual.startsWith("regexp:") || actual.startsWith("regex:") || actual.startsWith("regexpi:") || actual.startsWith("regexi:")) {
            // swap 'em
        	String tmp = actual;
            actual = expectedPattern;
            expectedPattern = tmp;
        }
        Boolean b;
        b = handleRegex("regexp:", expectedPattern, actual, 0);
        if (b != null) { return b.booleanValue(); }
        b = handleRegex("regex:", expectedPattern, actual, 0);
        if (b != null) { return b.booleanValue(); }
        b = handleRegex("regexpi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE);
        if (b != null) { return b.booleanValue(); }
        b = handleRegex("regexi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE);
        if (b != null) { return b.booleanValue(); }
        
        if (expectedPattern.startsWith("exact:")) {
            String expectedExact = expectedPattern.replaceFirst("exact:", "");
            if (!expectedExact.equals(actual)) {
                System.out.println("expected " + actual + " to match " + expectedPattern);
                return false;
            }
            return true;
        }
        
        String expectedGlob = expectedPattern.replaceFirst("glob:", "");
        expectedGlob = expectedGlob.replaceAll("([\\]\\[\\\\{\\}$\\(\\)\\|\\^\\+.])", "\\\\$1");

        expectedGlob = expectedGlob.replaceAll("\\*", ".*");
        expectedGlob = expectedGlob.replaceAll("\\?", ".");
        if (!Pattern.compile(expectedGlob, Pattern.DOTALL).matcher(actual).matches()) {
            System.out.println("expected \"" + actual + "\" to match glob \"" + expectedPattern + "\" (had transformed the glob into regexp \"" + expectedGlob + "\"");
            return false;
        }
        return true;
    }

    private static Boolean handleRegex(String prefix, String expectedPattern, String actual, int flags) {
        if (expectedPattern.startsWith(prefix)) {
            String expectedRegEx = expectedPattern.replaceFirst(prefix, ".*") + ".*";
            Pattern p = Pattern.compile(expectedRegEx, flags);
            if (!p.matcher(actual).matches()) {
                System.out.println("expected " + actual + " to match regexp " + expectedPattern);
                return Boolean.FALSE;                    
            }
            return Boolean.TRUE;
        }
        return null;
    }
    
    /** Compares two objects, but handles "regexp:" strings like HTML Selenese
     * @see #seleniumEquals(String, String)
     * @return true if actual matches the expectedPattern, or false otherwise
     */
    public static boolean seleniumEquals(Object expected, Object actual) {
        if (expected instanceof String && actual instanceof String) {
            return seleniumEquals((String)expected, (String)actual);
        }
        return expected.equals(actual);
    }
    
    /** Asserts that two string arrays have identical string contents */
    public static void assertEquals(String[] s1, String[] s2) {
        String comparisonDumpIfNotEqual = verifyEqualsAndReturnComparisonDumpIfNot(s1, s2);
        if (comparisonDumpIfNotEqual!=null) {
            throw new AssertionFailedError(comparisonDumpIfNotEqual);
        }
    }
    
    /** Asserts that two string arrays have identical string contents (fails at the end of the test, during tearDown) */
    public void verifyEquals(String[] s1, String[] s2) {
        String comparisonDumpIfNotEqual = verifyEqualsAndReturnComparisonDumpIfNot(s1, s2);
        if (comparisonDumpIfNotEqual!=null) {
            verificationErrors.append(comparisonDumpIfNotEqual);
        }
    }
    
    private static String verifyEqualsAndReturnComparisonDumpIfNot(String[] s1, String[] s2) {
        boolean misMatch = false;
        if (s1.length != s2.length) {
            misMatch = true;
        }
        for (int j = 0; j < s1.length; j++) {
            if (!seleniumEquals(s1[j], s2[j])) {
                misMatch = true;
                break;
            }
        }
        if (misMatch) {
            return "Expected " + stringArrayToString(s1) + " but saw " + stringArrayToString(s2);
        }
        return null;
    }
    
    private static String stringArrayToString(String[] sa) {
        StringBuffer sb = new StringBuffer("{");
        for (int j = 0; j < sa.length; j++) {
            sb.append(" ")
            .append("\"")
            .append(sa[j])
            .append("\"");            
        }
        sb.append(" }");
        return sb.toString();
    }
    
    private static String stringArrayToSimpleString(String[] sa) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < sa.length; j++) {
            sb.append(sa[j]);
            if (j < sa.length -1) {
            	sb.append(',');
            }          
        }
        return sb.toString();
    }

    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(Object s1, Object s2) {
        try {
            assertNotEquals(s1, s2);
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }
    
    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(boolean s1, boolean s2) {
        try {
            assertNotEquals(new Boolean(s1), new Boolean(s2));
        } catch (AssertionFailedError e) {
            verificationErrors.append(e);
        }
    }
    
    /** Asserts that two objects are not the same (compares using .equals()) */
    public static void assertNotEquals(Object obj1, Object obj2) {
        if (obj1.equals(obj2)) {
            fail("did not expect values to be equal (" + obj1.toString() + ")");
        }
    }
    
    /** Asserts that two booleans are not the same */
    public static void assertNotEquals(boolean b1, boolean b2) {
        assertNotEquals(new Boolean(b1), new Boolean(b2));
    }
    
    /** Sleeps for the specified number of milliseconds */
    public void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    }
    
    /** Asserts that there were no verification errors during the current test, failing immediately if any are found */
    public void checkForVerificationErrors() {
        assertEquals("", verificationErrors.toString());
        clearVerificationErrors();
    }

    /** Clears out the list of verification errors */
    public void clearVerificationErrors() {
        verificationErrors = new StringBuffer();
    }
    
    /** checks for verification errors and stops the browser */
    public void tearDown() throws Exception {
    	try {
    		checkForVerificationErrors();
    	} finally {
    		selenium.stop();
    	}
    }

    protected boolean isCaptureScreetShotOnFailure() {
        return captureScreetShotOnFailure;
    }

    protected void setCaptureScreetShotOnFailure(boolean captureScreetShotOnFailure) {
        this.captureScreetShotOnFailure = captureScreetShotOnFailure;
    }
}
