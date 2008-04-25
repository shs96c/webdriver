package com.googlecode.webdriver.remote;

public interface CommandExecutor {
	Response execute(Command command) throws Exception;
}
