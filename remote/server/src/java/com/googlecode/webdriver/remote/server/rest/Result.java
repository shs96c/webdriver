package com.googlecode.webdriver.remote.server.rest;

import com.googlecode.webdriver.remote.server.rest.Renderer;

public class Result {
	private final String mimeType;
	private final Renderer renderer;

	public Result(String mimeType, Renderer renderer) {
		this.mimeType = mimeType;
		this.renderer = renderer;
	}

	public boolean isExactMimeTypeMatch(String contentType) {
		// Not the world's best heuristic.
        if (contentType == null)
            return false;
        
        String[] types = contentType.split("[,;]");
		for (String type : types) {
			if (mimeType.equals(type))
				return true;
		}
		return false;
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	@Override
	public String toString() {
		return String.format("Result: %s -> %s", mimeType, renderer.getClass());
	}
}
