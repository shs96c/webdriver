package com.googlecode.webdriver.remote;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.util.HashMap;
import java.util.Map;

public class HttpCommandExecutor implements CommandExecutor {
    private final static boolean GET = true;
    private final static boolean POST = false;

    private Map<String, CommandInfo> nameToUrl = new HashMap<String, CommandInfo>();
	private HttpClient client;
	
	public HttpCommandExecutor() {
		client = new HttpClient();
		client.getHostConfiguration().setHost("localhost", 7055);
		
        nameToUrl.put("newSession",   new CommandInfo("/session", POST));
		nameToUrl.put("get",          new CommandInfo("/session/:sessionId/:context/url", POST));
		nameToUrl.put("currentUrl",   new CommandInfo("/session/:sessionId/:context/url", GET));
        nameToUrl.put("getTitle",     new CommandInfo("/session/:sessionId/:context/title", GET));
        nameToUrl.put("pageSource",   new CommandInfo("/session/:sessionId/:context/source", GET));
        nameToUrl.put("setVisible",   new CommandInfo("/session/:sessionId/:context/visible", POST));
        nameToUrl.put("getVisible",   new CommandInfo("/session/:sessionId/:context/visible", GET));
        nameToUrl.put("findElement",  new CommandInfo("/session/:sessionId/:context/element", POST));

        nameToUrl.put("clickElement",        new CommandInfo("/session/:sessionId/:context/element/:id/click", POST));
        nameToUrl.put("submitElement",       new CommandInfo("/session/:sessionId/:context/element/:id/submit", POST));
        nameToUrl.put("getElementText",      new CommandInfo("/session/:sessionId/:context/element/:id/text", GET));
        nameToUrl.put("sendKeys",            new CommandInfo("/session/:sessionId/:context/element/:id/value", POST));
        nameToUrl.put("getElementValue",     new CommandInfo("/session/:sessionId/:context/element/:id/value", GET));

        nameToUrl.put("getElementAttribute", new CommandInfo("/session/:sessionId/:context/element/:id/:name", GET));
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
        Response response = null;

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
        response.setError(httpMethod.getStatusCode() != HttpStatus.SC_OK);

        return response;
    }

    private boolean isRedirect(HttpMethod httpMethod) {
        int code = httpMethod.getStatusCode();
        return (code == 301 || code == 302 || code == 303 || code == 307) && httpMethod.getResponseHeader("location") != null;
    }

    private static class CommandInfo {
		private final String url;
		private final boolean idempotent;

		public CommandInfo(String url, boolean idempotent) {
			this.url = url;
			this.idempotent = idempotent;
		}
		
		public HttpMethodBase getMethod(Command command) {
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

            if (!idempotent)
			    return new PostMethod(urlBuilder.toString());

            GetMethod get = new GetMethod(urlBuilder.toString());
            get.setFollowRedirects(true);
            return get;
        }

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
