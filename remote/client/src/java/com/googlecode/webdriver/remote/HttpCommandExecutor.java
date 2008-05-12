package com.googlecode.webdriver.remote;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpCommandExecutor implements CommandExecutor {
    private enum HttpVerb {
      GET() {
        public HttpMethod createMethod(String url) {
          GetMethod getMethod = new GetMethod(url);
          getMethod.setFollowRedirects(true);
          return getMethod;
        }
      },
      POST() {
        public HttpMethod createMethod(String url) {
          return new PostMethod(url);
        }
      },
      DELETE() {
        public HttpMethod createMethod(String url) {
          return new DeleteMethod(url);
        }
      };

      public abstract HttpMethod createMethod(String url);
    }

    private Map<String, CommandInfo> nameToUrl = new HashMap<String, CommandInfo>();
	private HttpClient client;
	
	public HttpCommandExecutor() {
		client = new HttpClient();
		client.getHostConfiguration().setHost("localhost", 7055);
		
        nameToUrl.put("newSession",   new CommandInfo("/session", HttpVerb.POST));
        nameToUrl.put("get",          new CommandInfo("/session/:sessionId/:context/url", HttpVerb.POST));
        nameToUrl.put("currentUrl",   new CommandInfo("/session/:sessionId/:context/url", HttpVerb.GET));
        nameToUrl.put("getTitle",     new CommandInfo("/session/:sessionId/:context/title", HttpVerb.GET));
        nameToUrl.put("pageSource",   new CommandInfo("/session/:sessionId/:context/source", HttpVerb.GET));
        nameToUrl.put("setVisible",   new CommandInfo("/session/:sessionId/:context/visible", HttpVerb.POST));
        nameToUrl.put("getVisible",   new CommandInfo("/session/:sessionId/:context/visible", HttpVerb.GET));
        nameToUrl.put("findElement",  new CommandInfo("/session/:sessionId/:context/element", HttpVerb.POST));

        nameToUrl.put("clickElement",        new CommandInfo("/session/:sessionId/:context/element/:id/click", HttpVerb.POST));
        nameToUrl.put("clearElement",        new CommandInfo("/session/:sessionId/:context/element/:id/clear", HttpVerb.POST));
        nameToUrl.put("submitElement",       new CommandInfo("/session/:sessionId/:context/element/:id/submit", HttpVerb.POST));
        nameToUrl.put("getElementText",      new CommandInfo("/session/:sessionId/:context/element/:id/text", HttpVerb.GET));
        nameToUrl.put("sendKeys",            new CommandInfo("/session/:sessionId/:context/element/:id/value", HttpVerb.POST));
        nameToUrl.put("getElementValue",     new CommandInfo("/session/:sessionId/:context/element/:id/value", HttpVerb.GET));
        nameToUrl.put("isElementSelected",   new CommandInfo("/session/:sessionId/:context/element/:id/selected", HttpVerb.GET));
        nameToUrl.put("setElementSelected",  new CommandInfo("/session/:sessionId/:context/element/:id/selected", HttpVerb.POST));
        nameToUrl.put("toggleElement",       new CommandInfo("/session/:sessionId/:context/element/:id/toggle", HttpVerb.POST));
        nameToUrl.put("isElementEnabled",    new CommandInfo("/session/:sessionId/:context/element/:id/enabled", HttpVerb.GET));

        nameToUrl.put("getElementAttribute", new CommandInfo("/session/:sessionId/:context/element/:id/:name", HttpVerb.GET));

        nameToUrl.put("getAllCookies",    new CommandInfo("/session/:sessionId/:context/cookie", HttpVerb.GET));
        nameToUrl.put("addCookie",        new CommandInfo("/session/:sessionId/:context/cookie", HttpVerb.POST));
        nameToUrl.put("deleteAllCookies", new CommandInfo("/session/:sessionId/:context/cookie", HttpVerb.DELETE));
        nameToUrl.put("deleteCookie",     new CommandInfo("/session/:sessionId/:context/cookie/:name", HttpVerb.DELETE));
    }
	
	public Response execute(Command command) throws Exception {
		CommandInfo info = nameToUrl.get(command.getMethodName());
                HttpMethod httpMethod = info.getMethod(command);

        httpMethod.addRequestHeader("Accept", "application/json");

        String payload = new BeanToJsonConverter().convert(command.getParameters());

        if (httpMethod instanceof PostMethod)
            ((PostMethod) httpMethod).setRequestEntity(new StringRequestEntity(payload, "application/json", "UTF-8"));

        client.executeMethod(httpMethod);

        // TODO: SimonStewart: 2008-04-25: This is really shabby
        if (isRedirect(httpMethod)) {
            Header newLocation = httpMethod.getResponseHeader("location");
            httpMethod = new GetMethod(newLocation.getValue());
            httpMethod.setFollowRedirects(true);
            httpMethod.addRequestHeader("Accept", "application/json");
            client.executeMethod(httpMethod);
        }

        Response response = createResponse(httpMethod);

        return response;
	}

    private Response createResponse(HttpMethod httpMethod) throws Exception {
        Response response;

        Header header = httpMethod.getResponseHeader("Content-Type");

        if (header != null && header.getValue().startsWith("application/json")) {
            response = new JsonToBeanConverter().convert(Response.class, httpMethod.getResponseBodyAsString());
        } else {
            response = new Response();
            response.setValue(httpMethod.getResponseBodyAsString());
            String uri = httpMethod.getURI().toString();
            int sessionIndex = uri.indexOf("/session/");
            if (sessionIndex != -1) {
                sessionIndex += "/session/".length();
                int nextSlash = uri.indexOf("/", sessionIndex);
                if (nextSlash != -1) {
                    response.setSessionId(uri.substring(sessionIndex, nextSlash));
                    response.setContext("foo");
                }
            }
        }
        response.setError(!(httpMethod.getStatusCode() > 199 && httpMethod.getStatusCode() < 300));

        return response;
    }

    private boolean isRedirect(HttpMethod httpMethod) {
        int code = httpMethod.getStatusCode();
        return (code == 301 || code == 302 || code == 303 || code == 307) && httpMethod.getResponseHeader("location") != null;
    }

    private static class CommandInfo {
		private final String url;
		private final HttpVerb verb;

		public CommandInfo(String url, HttpVerb verb) {
			this.url = url;
			this.verb = verb;
		}
		
		public HttpMethod getMethod(Command command) {
            StringBuilder urlBuilder = new StringBuilder("/hub");
            for (String part : url.split("/")) {
                if (part.length() == 0)
                    continue;
                
                urlBuilder.append("/");
                if (part.startsWith(":")) {
                    urlBuilder.append(get(part.substring(1), command));
                } else {
                    urlBuilder.append(part);
                }
            }


            return verb.createMethod(urlBuilder.toString());
        }

        @SuppressWarnings("unchecked")
		private String get(String propertyName, Command command) {
            if ("sessionId".equals(propertyName))
                return command.getSessionId().toString();
            if ("context".equals(propertyName))
                return command.getContext().toString();

            // Attempt to extract the property name from the parameters
            if (command.getParameters().length > 0 && command.getParameters()[0] instanceof Map) {
                return String.valueOf(((Map) command.getParameters()[0]).get(propertyName));
            }

            return "place-holder";
        }
    }
}
