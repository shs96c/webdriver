package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.remote.server.rest.Handler;
import com.googlecode.webdriver.remote.server.rest.ResultType;

public class StubHandler implements Handler {
	public ResultType handle() {
		return ResultType.SUCCESS;
	}
}