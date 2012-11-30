package com.adiron.busme.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

public class BuspassAPI extends APIBase {
    private static final String LOGTAG = BuspassAPI.class.getName();
	
	String initialUrl;
	
	public BuspassAPI(String initialUrl) {
		super();
		this.initialUrl = initialUrl;
	}
	
	public static BuspassAPI getInstance(String initialUrl) {
		return new BuspassAPI(initialUrl);
		
	}
	
	public class Buspass {
		String version;
		String mode;
		String name;
		String loginUrl;
		String lon;
		String lat;
		String timezone;
		String time;
		String timeoffset;
		String datefmt;
		String getRouteJourneyIdsUrl;
		String getRouteDefinitionUrl;
		String getJourneyLocationUrl;
		String postJourneyLocationUrl;
		String getMessageUrl;
	}
	
	Buspass buspass;

	public boolean get() throws IOException {
		try {
			Log.d(LOGTAG, "Opening " + initialUrl);
			HttpGet request = new HttpGet(initialUrl);
			HttpResponse resp = httpClient.execute(request, localHttpContext);
			Header[] headers = resp.getAllHeaders();
			for (Header h : headers) {
				Log.d(LOGTAG, "Header: " + h.getName() + " " + h.getValue());
			}
			HttpEntity ent = resp.getEntity();
			InputStream in = ent.getContent();
			Tag tag = xmlParse(in);  
			tag = tag.childNodes.get(0); // Get the first child.
			if (tag.name == "API") {
				Buspass bp = buspass = new Buspass();
				if ("1".equals(tag.attributes.get("majorVersion"))) {
					bp.mode = tag.attributes.get("mode");
					bp.name = tag.attributes.get("name");
					bp.loginUrl = tag.attributes.get("login");
					bp.lon = tag.attributes.get("lon");
					bp.lat = tag.attributes.get("lat");
					bp.timezone = tag.attributes.get("timezone");
					bp.time = tag.attributes.get("time");
					bp.timeoffset = tag.attributes.get("timeoffset");
					bp.datefmt = tag.attributes.get("datefmt");
					bp.getRouteJourneyIdsUrl = tag.attributes.get("getRouteJourneyIds");
					bp.getRouteDefinitionUrl = tag.attributes.get("getRouteDefinition");
					bp.getJourneyLocationUrl = tag.attributes.get("getJourneyLocation");
					bp.postJourneyLocationUrl = tag.attributes.get("postJourneyLocation");
					bp.getMessageUrl = tag.attributes.get("getMessageUrl");
				}
				return true;
			}
		} catch (IllegalArgumentException e) {
			Log.d(LOGTAG, "Exception" + e.getMessage());
			throw new IOException(e.getMessage());
		} catch (IOException e) {
			Log.d(LOGTAG, "Exception" + e);
			throw e;
		}
		return false;
	}
	
	public String version() {
		return buspass.version;
	}	
	public String mode() {
		return buspass.mode;
	}	
	public String name() {
		return buspass.name;
	}	
	public String loginUrl() {
		return buspass.loginUrl;
	}	
	public String lon() {
		return buspass.lon;
	}	
	public String lat() {
		return buspass.lat;
	}	
	public String timezone() {
		return buspass.timezone;
	}	
	public String timeoffset() {
		return buspass.timeoffset;
	}	
	public String datefmt() {
		return buspass.datefmt;
	}	
	public String getRouteJourneyIdsUrl() {
		return buspass.getRouteJourneyIdsUrl;
	}	
	public String getRouteDefinitionUrl() {
		return buspass.getRouteDefinitionUrl;
	}	
	public String getJourneyLocationUrl() {
		return buspass.getJourneyLocationUrl;
	}	
	public String postJourneyLocationUrl() {
		return buspass.postJourneyLocationUrl;
	}	
	public String getMessageUrl() {
		return buspass.getMessageUrl;
	}
}
