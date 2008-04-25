package com.googlecode.webdriver.remote;

public class Response {
    private boolean isError;
    private String text;
    private String sessionId;
    private String context;

    public Response() {
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResponseText() {
        return text;
    }    

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionId getSessionId() {
    	return new SessionId(sessionId);
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Context getContext() {
    	return new Context(context);
    }

    public String toString() {
        return String.format("(%s %s %s: %s)", getSessionId(), getContext(), isError(), getResponseText());
    }
}
