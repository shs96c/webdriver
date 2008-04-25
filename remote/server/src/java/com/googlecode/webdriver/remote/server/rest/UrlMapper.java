package com.googlecode.webdriver.remote.server.rest;

import com.googlecode.webdriver.remote.server.DriverSessions;

import java.util.HashSet;
import java.util.Set;

public class UrlMapper {
    private Set<ResultConfig> configs = new HashSet<ResultConfig>();
	private final DriverSessions sessions;

	public UrlMapper(DriverSessions sessions) {
		this.sessions = sessions;
	}

	public ResultConfig bind(String url, Class<? extends Handler> handlerClazz) {
		ResultConfig config = new ResultConfig(url, handlerClazz, sessions);
		configs.add(config);
		return config;
	}

	public ResultConfig getConfig(String url) throws Exception {
		for (ResultConfig config : configs) {
			if (config.isFor(url))
				return config;
		}
		
		return null;
	}
}
