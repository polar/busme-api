package com.adiron.busme.api;

import java.io.IOException;

public class DiscoverAPI extends APIBase {
    private static final String LOGTAG = DiscoverAPI.class.getName();
    
	DiscoverAPI() {
		initializeHttpClient();
	}
	
	public boolean get() throws IOException {
		return false;
	}
}
