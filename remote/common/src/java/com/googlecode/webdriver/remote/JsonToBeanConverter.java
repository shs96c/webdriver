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
import java.util.List;

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

        if (isPrimitive(text.getClass())) {
            return (T) text;
        }

        if (isEnum(clazz, text)) {
            return (T) convertEnum(clazz, text);
        }

        if ("".equals(String.valueOf(text)))
            return (T) text;

        if (text != null && text instanceof String && !((String) text).startsWith("{") && Object.class.equals(clazz))
            return (T) text;        

        Object o;
        try {
            o = new JSONParser().parse(new StringReader(String.valueOf(text)));
        } catch (Error e) {
            return (T) text;
        }

        if (Map.class.isAssignableFrom(clazz))
            return (T) convertMap((Map) o);

        if (List.class.isAssignableFrom(o.getClass()))
            return (T) convertList((List) o);

        if (isPrimitive(o.getClass())) {
            return (T) o;
        }

        if (Object.class.equals(clazz)) {
            return (T) convertObjectToMap((Map) o);
        }

        return convertBean(clazz, (JSONObject) o);
    }

  private Map convertObjectToMap(Map jsonObject) {
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

            try {
                write.invoke(t, convert(type, value));
            } catch (Exception e) {
                throw new Exception(
                        String.format("Property name: %s -> %s on class %s", property.getName(), value, type),
                        e);
            }
        }

        return t;
    }

    private Map convertMap(Map toConvert) throws Exception {
        Map map = new HashMap();

        Iterator allEntries = toConvert.entrySet().iterator();
        while (allEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) allEntries.next();
            map.put(entry.getKey(), convert(entry.getValue()));
        }

        return map;
    }

    private List convertList(List list) {
        return list;
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
