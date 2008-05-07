package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.internal.OperatingSystem;
import junit.framework.TestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.List;

public class JsonToBeanConverterTest extends TestCase {
    public void testCanConstructASimpleString() throws Exception {
        String text = new JsonToBeanConverter().convert(String.class, "cheese");
        
        assertThat(text, is("cheese"));
    }

    public void testCanPopulateAMap() throws Exception {
        JSONObject toConvert = new JSONObject();
        toConvert.put("cheese", "brie");
        toConvert.put("foodstuff", "cheese");

        Map<String, String> map = new JsonToBeanConverter().convert(Map.class, toConvert.toString());
        assertThat(map.size(), is(2));
    }

    public void testCanPopulateASimpleBean() throws Exception {
        JSONObject toConvert = new JSONObject();
        toConvert.put("value", "time");

        SimpleBean bean = new JsonToBeanConverter().convert(SimpleBean.class, toConvert.toString());

        assertThat(bean.getValue(), is("time"));
    }
    
    public void testWillSilentlyDiscardUnusedFieldsWhenPopulatingABean() throws Exception {
        JSONObject toConvert = new JSONObject();
        toConvert.put("value", "time");
        toConvert.put("frob", "telephone");

        SimpleBean bean = new JsonToBeanConverter().convert(SimpleBean.class, toConvert.toString());

        assertThat(bean.getValue(), is("time"));
    }
    
    public void testShouldSetPrimitiveValuesToo() throws Exception {
        JSONObject toConvert = new JSONObject();
        toConvert.put("magicNumber", 3);

        Map map = new JsonToBeanConverter().convert(Map.class, toConvert.toString());

        assertThat(3L, is(map.get("magicNumber")));
    }

    public void testShouldPopulateFieldsOnNestedBeans() throws Exception {
        JSONObject toConvert = new JSONObject();
        toConvert.put("name", "frank");
        JSONObject child = new JSONObject();
        child.put("value", "lots");
        toConvert.put("bean", child);

        ContainingBean bean = new JsonToBeanConverter().convert(ContainingBean.class, toConvert.toString());

        assertThat(bean.getName(), is ("frank"));
        assertThat(bean.getBean().getValue(), is("lots"));
    }

    public void testShouldProperlyFillInACapabilitiesObject() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities("browser", "version", OperatingSystem.ANY);
        capabilities.setJavascriptEnabled(true);
        String text = new BeanToJsonConverter().convert(capabilities);

        DesiredCapabilities readCapabilities = new JsonToBeanConverter().convert(DesiredCapabilities.class, text);

        assertEquals(capabilities, readCapabilities);
    }

    public void testShouldBeAbleToInstantiateBooleans() throws Exception {
        JSONArray array = new JSONArray();
        array.add(true);
        array.add(false);

        boolean first = new JsonToBeanConverter().convert(Boolean.class, array.get(0));
        boolean second = new JsonToBeanConverter().convert(Boolean.class, array.get(1));

        assertTrue(first);
        assertFalse(second);
    }
    
    public void testShouldUseAMapToRepresentComplexObjects() throws Exception {
        JSONObject toModel = new JSONObject();
        toModel.put("thing", "hairy");
        toModel.put("hairy", "true");

        Map modelled = (Map) new JsonToBeanConverter().convert(Object.class, toModel);
        assertEquals(2, modelled.size());
    }

    public void testShouldConvertAResponseWithAnElementInIt() throws Exception {
        String json = "{\"value\":{\"value\":\"\",\"text\":\"\",\"selected\":false,\"enabled\":true,\"id\":\"three\"},\"context\":\"con\",\"sessionId\":\"sess\",\"error\":false}";
        Response converted = new JsonToBeanConverter().convert(Response.class, json);

        Map value = (Map) converted.getValue();
        assertEquals("three", value.get("id"));
    }
    
    public void testConvertABlankStringAsAStringEvenWhenAskedToReturnAnObject() throws Exception {
        Object o = new JsonToBeanConverter().convert(Object.class, "");

        assertTrue(o instanceof String);
    }

    public void testCanHandleValueBeingAnArray() throws Exception {
      String[] value = {"Cheese", "Peas"};

      Response response = new Response();
      response.setContext("foo");
      response.setSessionId("bar");
      response.setValue(value);
      response.setError(true);

      String json = new BeanToJsonConverter().convert(response);
      Response converted = new JsonToBeanConverter().convert(Response.class, json);

      assertEquals(2, ((List) converted.getValue()).size());
      assertTrue(converted.isError());
    }

    public static class SimpleBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ContainingBean {
        private String name;
        private SimpleBean bean;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SimpleBean getBean() {
            return bean;
        }

        public void setBean(SimpleBean bean) {
            this.bean = bean;
        }
    }
}
