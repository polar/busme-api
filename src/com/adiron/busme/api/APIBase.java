package com.adiron.busme.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class APIBase {
    private static final String LOGTAG = APIBase.class.getName();
	
	protected BasicHttpContext localHttpContext;
	protected DefaultHttpClient httpClient;

	protected String csrfParam;

	protected String csrfToken;

	protected String masterParam;

	protected String masterToken;

	protected CookieStore cookieStore;

	protected APIBase() {
		initializeHttpClient();
	}
	
	protected HttpClient initializeHttpClient() {
		  DefaultHttpClient httpclient = new DefaultHttpClient();
		  BasicHttpContext localContext = new BasicHttpContext();
		  cookieStore = httpclient.getCookieStore();
		  localHttpContext = localContext;
		  return httpClient = httpclient;
	}
	
	protected String convertStreamToString(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			line = reader.readLine();
			while(line != null) {
				sb.append(line);
				line = reader.readLine();
				if (line != null) {
					sb.append("\n");
				}
			}
		} finally {
			reader.close();
		}
		Log.d(LOGTAG, "Got " + sb);
		return sb.toString();
	}
	
	class Tag {
		String name;
		Map<String, String> attributes = new HashMap<String, String>();
		String text;
		List<Tag> childNodes = new ArrayList<Tag>();
		void print(PrintWriter out) {
			out.printf("<%s ", name);
			for(String key : attributes.keySet()) {
				out.printf("%s=%s ", key, attributes.get(key));
			}
			out.print(">\n");
			for(Tag tag : childNodes) {
				tag.print(out);
			}
			out.printf("</%s>\n", name);
		}
		public String toString() {
			StringWriter sout = new StringWriter();
			PrintWriter out = new PrintWriter(sout);
			this.print(out);
			return sout.toString();
		}
	}
	
	public Tag xmlParse(InputStream myData) { 
	    XmlPullParser parser = Xml.newPullParser();

	    try {
	         parser.setInput(new InputStreamReader(myData));
	         int eventType = parser.getEventType();
	         Stack<Tag> stack = new Stack<Tag>();
	         stack.push(new Tag());
	         while (eventType != XmlPullParser.END_DOCUMENT) {
	             if(eventType == XmlPullParser.START_TAG) {
	            	 Tag parent = stack.peek();
	            	 Tag tag = new Tag();
	            	 parent.childNodes.add(tag);
	            	 if (!parser.isEmptyElementTag()) {
	            		 stack.push(tag);
	            	 }
	            	 tag.name = parser.getName();
	            	 int len = parser.getAttributeCount();
	            	 for (int i = 0; i < len; i ++) {
	            		tag.attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i)); 
	            	 }
	            	 tag.text = parser.nextText();
	             } else if (eventType == XmlPullParser.END_TAG) {
	            	 Tag tag = stack.pop();
	             }
	             eventType = parser.next();
	         }         
	 	    return stack.pop();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	

}
