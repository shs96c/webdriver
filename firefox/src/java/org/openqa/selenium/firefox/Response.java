package org.openqa.selenium.firefox;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;

public class Response {
	private final JSONObject result;
    private final String methodName;
    private final Context context;
    private final String responseText;
    private boolean isError;

    public Response(String json) {
        try {
            result = new JSONObject(json.trim());

            methodName = (String) result.get("commandName");
            String contextAsString = (String) result.get("context");
            if (contextAsString != null)
                context = new Context(contextAsString);
            else
                context = null;
            responseText = String.valueOf(result.get("response"));

            isError = (Boolean) result.get("isError");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCommand() {
        return methodName;
    }

    public Context getContext() {
        return context;
    }

    public String getResponseText() {
        return responseText;
    }

    public boolean isError() {
        return isError;
    }

  public String toString() {
    return result.toString();
  }

  public Object getExtraResult(String fieldName) {
    	try {
			return result.get(fieldName);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
    }

    public void ifNecessaryThrow(Class<? extends RuntimeException> exceptionClass) {
        if (!isError)
            return;

        RuntimeException toThrow;
        try {
            Constructor<? extends RuntimeException> constructor = exceptionClass.getConstructor(String.class);
            toThrow = constructor.newInstance(getResponseText());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(getResponseText());
        }

        throw toThrow;
    }
}
