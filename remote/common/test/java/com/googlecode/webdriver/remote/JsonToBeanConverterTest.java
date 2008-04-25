package com.googlecode.webdriver.remote;

import com.googlecode.webdriver.internal.OperatingSystem;
import junit.framework.TestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

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
