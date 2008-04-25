package com.googlecode.webdriver.remote;

public class RemoteWebDriver {
	private CommandExecutor executor;
    private Capabilities capabilities;
    private SessionId sessionId;

    public RemoteWebDriver(Capabilities desiredCapabilities) throws Exception {
		executor = new HttpCommandExecutor();

        Response response = execute("newSession", desiredCapabilities);
        String rawText = response.getResponseText();

        capabilities = new JsonToBeanConverter().convert(DesiredCapabilities.class, rawText);
        sessionId = response.getSessionId();
    }

    public void get(String url) {
        execute("get", url);
    }

    public String getTitle() {
        Response response = execute("getTitle");
        return response.getResponseText();
    }

    public String getCurrentUrl() {
        return execute("currentUrl").getResponseText();
    }

    private Response execute(String commandName, Object... parameters) {
        Command command = new Command(sessionId, new Context("foo"), commandName, parameters);
        try {
            return executor.execute(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
