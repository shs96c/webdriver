package com.googlecode.webdriver.remote;

import junit.framework.TestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringReader;
import java.util.*;


public class BeanToJsonConverterTest extends TestCase {
    public void testShouldBeAbleToConvertASimpleString() throws Exception {
        String json = new BeanToJsonConverter().convert("cheese");

        assertThat(json, is("cheese"));
    }

    public void testShouldConvertAMapIntoAJsonObject() throws Exception {
        Map<String, String> toConvert = new HashMap<String, String>();
        toConvert.put("cheese", "cheddar");
        toConvert.put("fish", "nice bit of haddock");

        String json = new BeanToJsonConverter().convert(toConvert);

        JSONObject converted = (JSONObject) new JSONParser().parse(new StringReader(json));
        assertThat((String) converted.get("cheese"), is("cheddar"));
    }

    public void testShouldConvertASimpleJavaBean() throws Exception {
        String json = new BeanToJsonConverter().convert(new SimpleBean());

        JSONObject converted = (JSONObject) new JSONParser().parse(new StringReader(json));
        assertThat((String) converted.get("foo"), is("bar"));        
    }

    public void testShouldConvertArrays() throws Exception {
        String json = new BeanToJsonConverter().convert(new BeanWithArray());

        JSONObject converted = (JSONObject) new JSONParser().parse(new StringReader(json));
        JSONArray allNames = (JSONArray) converted.get("names");
        assertThat(allNames.size(), is(3));
    }
    
    public void testShouldConvertCollections() throws Exception {
        String json = new BeanToJsonConverter().convert(new BeanWithCollection());

        JSONObject converted = (JSONObject) new JSONParser().parse(new StringReader(json));
        JSONArray allNames = (JSONArray) converted.get("something");
        assertThat(allNames.size(), is(2));
    }
    
    public void testShouldNotChokeWhenCollectionIsNull() throws Exception {
        try {
            new BeanToJsonConverter().convert(new BeanWithNullCollection());
        } catch (Exception e) {
            e.printStackTrace();
            fail("That shouldn't have happened");
        }
    }

    public void testShouldConvertEnumsToStrings() throws Exception {
        // If this doesn't hang indefinitely, we're all good
        new BeanToJsonConverter().convert(State.INDIFFERENT);
    }

    public void testShouldConvertEnumsWithMethods() throws Exception {
        // If this doesn't hang indefinitely, we're all good
        new BeanToJsonConverter().convert(WithMethods.CHEESE);
    }

    public void testNullAndAnEmptyStringAreEncodedDifferently() throws Exception {
        BeanToJsonConverter converter = new BeanToJsonConverter();

        String nullValue = converter.convert(null);
        String emptyString = converter.convert("");

        assertFalse(emptyString.equals(nullValue));
    }
    
    private static class SimpleBean {
        public String getFoo() {
            return "bar";
        }
    }

    private static class BeanWithArray {
        public String[] getNames() {
            return new String[] { "peter", "paul", "mary"};
        }
    }

    private static class BeanWithCollection {
        public Set getSomething() {
            Set<Integer> integers = new HashSet<Integer>();
            integers.add(1);
            integers.add(43);
            return integers;
        }
    }

    private static class BeanWithNullCollection {
        public List getList() {
            return null;
        }
    }

    public static enum State {
        GOOD,
        BAD,
        INDIFFERENT
    }

    public static enum WithMethods {
        CHEESE() {
            public void eat(String foodStuff) {
                // Does nothing
            }
        },
        EGGS() {
            public void eat(String foodStuff) {
                // Does nothing too
            }
        };

        public abstract void eat(String foodStuff);
    }
}
