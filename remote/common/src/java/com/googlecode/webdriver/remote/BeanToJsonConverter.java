package com.googlecode.webdriver.remote;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class BeanToJsonConverter {
    public String convert(Object toConvert) throws Exception {
        Object returned = realConvert(toConvert);
        return returned == null ? null : returned.toString();
    }

    private Object realConvert(Object toConvert) throws Exception {
        if (toConvert == null)
            return null;

        if (toConvert.getClass().isArray())
            return convertArray(toConvert);        

        // Assume that strings have already been converted
        if (isPrimitiveType(toConvert))
            return toConvert;

        if (toConvert instanceof String) {
            return toConvert;
        }

        if (toConvert instanceof Map)
            return convertMap((Map) toConvert);

        if (toConvert instanceof Collection)
            return convertCollection((Collection) toConvert);

        if (toConvert.getClass().isEnum() || toConvert instanceof Enum)
            return toConvert.toString();

        return convertBean(toConvert);
    }

    // I'm missing something really obvious
    private boolean isPrimitiveType(Object toConvert) {
        if (toConvert.getClass().isPrimitive())
            return true;

        if (toConvert instanceof Boolean)
            return true;

        if (toConvert instanceof Byte)
            return true;

        if (toConvert instanceof Character)
            return true;

        if (toConvert instanceof Double)
            return true;

        if (toConvert instanceof Float)
            return true;

        if (toConvert instanceof Integer)
            return true;

        if (toConvert instanceof Long)
            return true;

        if (toConvert instanceof Short)
            return true;

        if (toConvert instanceof Void)
            return true;

        return false;
    }

    private Object convertCollection(Collection collection) throws Exception {
        JSONArray json = new JSONArray();

        if (collection == null)
            return json;

        for (Object o : collection) {
            json.add(realConvert(o));
        }

        return json;
    }

    private Object convertArray(Object array) throws Exception {
        JSONArray json = new JSONArray();

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            json.add(realConvert(Array.get(array, i)));
        }

        return json;
    }

    private Object convertBean(Object toConvert) throws Exception {
        JSONObject json = new JSONObject();

        BeanInfo beanInfo = Introspector.getBeanInfo(toConvert.getClass());
        PropertyDescriptor[] allProperties = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : allProperties) {
            if ("class".equals(property.getName()))
                continue;

            Method read = property.getReadMethod();
            if (read == null)
                continue;

            try {
                Object result = read.invoke(toConvert);
                json.put(property.getName(), realConvert(result));
            } catch (Exception e) {
                // Skip this property
            }
        }

        return json;
    }

    private Object convertMap(Map map) throws Exception {
        JSONObject json = new JSONObject();
        for (Object rawEntry : map.entrySet()) {
            Map.Entry entry = (Map.Entry) rawEntry;
            json.put(entry.getKey(), realConvert(entry.getValue()));
        }

        return json;
    }
}
