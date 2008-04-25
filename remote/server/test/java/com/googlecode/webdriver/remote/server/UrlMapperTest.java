
package com.googlecode.webdriver.remote.server;

import com.googlecode.webdriver.remote.server.rest.Handler;
import com.googlecode.webdriver.remote.server.rest.ResultConfig;
import com.googlecode.webdriver.remote.server.rest.ResultType;
import com.googlecode.webdriver.remote.server.rest.UrlMapper;
import junit.framework.TestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UrlMapperTest extends TestCase {
	public void testShouldBePossibleToBindAHandler() throws Exception {
		UrlMapper mapper = new UrlMapper(new DriverSessions());
		
		mapper.bind("/foo", StubHandler.class);
		
		ResultConfig config = mapper.getConfig("/foo");
		
		assertThat(config, is(notNullValue()));
	}
	
	public void testShouldInjectDependenciesViaTheConstructor() throws Exception {
		DriverSessions sessions = new DriverSessions();
		UrlMapper mapper = new UrlMapper(sessions);
		mapper.bind("/example", SessionHandler.class);
		
		ResultConfig config = mapper.getConfig("/example");
		SessionHandler handler = (SessionHandler) config.getHandler("/example");
		
		assertThat(handler.getSessions(), is(notNullValue()));
	}
	
	public static class SessionHandler implements Handler {
		private final DriverSessions sessions;

		public SessionHandler(DriverSessions sessions) {
			this.sessions = sessions;
		}
		
		public DriverSessions getSessions() {
			return sessions;
		}

		public ResultType handle() {
			return ResultType.SUCCESS;
		}
	}
}
