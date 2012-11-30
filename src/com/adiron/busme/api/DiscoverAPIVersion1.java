package com.adiron.busme.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;


import android.util.Log;

public class DiscoverAPIVersion1 extends DiscoverAPI {
    private static final String LOGTAG = DiscoverAPIVersion1.class.getName();
	
	String initialUrl;
	String discoverUrl;
	
	DiscoverAPIVersion1(String initialUrl) {
		this.initialUrl = initialUrl;
	}
	
	public static DiscoverAPIVersion1 getInstance(String url) {
		return new DiscoverAPIVersion1(url);
	}
	
	@Override
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
				if ("d1".equals(tag.attributes.get("version"))) {
					discoverUrl = tag.attributes.get("discover");
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
	
	public class Master {
		public Float lon, lat;
		public String slug;
		public String name;
		public String apiUrl;
	}  
	
	public List<Master> discover(Double lon, Double lat) throws IOException {
		
		try {
            Log.d(LOGTAG, "Opening " + discoverUrl );
            Tag tag = xmlParse(new URL(discoverUrl+"?lon="+lon+"&lat="+lat).openStream());
			tag = tag.childNodes.get(0); // Get the first child.
            List<Master> masters = new ArrayList<Master>();
            if (tag.name == "masters") {
            	for(Tag t : tag.childNodes) {
            		if (t.name == "master") {
            			Map<String, String> attrs = t.attributes;
            			Master m = new Master();
                        String node = attrs.get("lon");
                        if (node != null) {
                        	m.lon = Float.parseFloat(node);
                        }
                        node = attrs.get("lat");
                        if (node != null) {
                        	m.lat = Float.parseFloat(node);
                        }
                        m.name = attrs.get("name");
                        m.slug = attrs.get("slug");
                        m.apiUrl = attrs.get("api");
                        masters.add(m);
            		}
            	}
            	return masters;
            }
         	throw new IOException("No Masters");
         } catch (IllegalArgumentException e) {
      	   Log.d(LOGTAG, "Exception" + e.getMessage());
         	throw new IOException(e.getMessage());
         } catch (IOException e) {
     	   Log.d(LOGTAG, "Exception" + e);
     	   throw e;
         }
	}

}
