package com.googlecode.webdriver.remote;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonToBeanConverter {
    public <T> T convert(Class<T> clazz, Object text) throws Exception {
        if (text == null)
            return null;

        if (String.class.equals(clazz)) {
            return (T) text;
        }

        if (isPrimitive(clazz)) {
            return (T) text; 
        }

        if (isEnum(clazz, text)) {
            return (T) convertEnum(clazz, text);
        }

        Object o;
        try {
            o = new JSONParser().parse(new StringReader(String.valueOf(text)));
        } catch (Error e) {
            return (T) text;
        }

        if (Map.class.isAssignableFrom(clazz))
            return (T) convertMap((JSONObject) o);

        if (Object.class.equals(clazz)) {
            return (T) convertObjectToMap((JSONObject) o);
        }

        return convertBean(clazz, (JSONObject) o);
    }

    private Map convertObjectToMap(JSONObject jsonObject) {
        return new HashMap(jsonObject);
    }

    private Enum convertEnum(Class clazz, Object text) {
        if (clazz.isEnum())
            return Enum.valueOf(clazz, String.valueOf(text));

        Class[] allClasses = clazz.getClasses();
        for (Class current : allClasses) {
            if (current.isEnum()) {
                return Enum.valueOf(current, String.valueOf(text));
            }
        }

        return null;
    }

    private boolean isEnum(Class<?> clazz, Object text) {
        return clazz.isEnum() || text instanceof Enum;
    }

    private Object convert(Object toConvert) throws Exception {
        return toConvert;
    }

    public <T> T convertBean(Class<T> clazz, JSONObject toConvert) throws Exception {
        T t = clazz.newInstance();
        PropertyDescriptor[] allProperties = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        for (PropertyDescriptor property : allProperties) {
            if ("class".equals(property.getName()))
                continue;

            Object value = toConvert.get(property.getName());

            Method write = property.getWriteMethod();
            if (write == null)
                continue;

            Class<?> type = write.getParameterTypes()[0];

            write.invoke(t, convert(type, value));
        }

        return t;
    }

    private Map convertMap(JSONObject toConvert) throws Exception {
        Map map = new HashMap();

        Iterator allEntries = toConvert.entrySet().iterator();
        while (allEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) allEntries.next();
            map.put(entry.getKey(), convert(entry.getValue()));
        }

        return map;
    }

    private boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive())
            return true;

        if (Boolean.class.isAssignableFrom(clazz))
            return true;


        if (Byte.class.isAssignableFrom(clazz))
            return true;

        if (Character.class.isAssignableFrom(clazz))
            return true;

        if (Double.class.isAssignableFrom(clazz))
            return true;

        if (Float.class.isAssignableFrom(clazz))
            return true;

        if (Integer.class.isAssignableFrom(clazz))
            return true;

        if (Long.class.isAssignableFrom(clazz))
            return true;

        if (Short.class.isAssignableFrom(clazz))
            return true;

        if (Void.class.isAssignableFrom(clazz))
            return true;

        return false;
    }
}
