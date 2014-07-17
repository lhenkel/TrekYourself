package com.henkelsoft.trekyourself;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import android.util.Log;
import com.henkelsoft.trekyourself.*;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class XMLCalResponseHandler implements ResponseHandler<TreeMap<Date, String>> {
	//private final List<String> mResults = new ArrayList<String>();
	
	private TreeMap<Date, String> calHash;	
	
	@Override
	public TreeMap<Date, String> handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
		
		//Log.d("TREK", "Running Calendar HTTP Grab!");
		Log.d("CURDEBUG","Running Calendar HTTP Grab!");
		try {

			Log.d("TREK", "Running Calendar HTTP Grab!2");
			// Create the Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			// Set the Parser's input to be the XML document in the HTTP Response
			//xpp.setInput(new InputStreamReader(response.getEntity()
			//		.getContent()));
 
			calHash = new TreeMap<Date, String>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); //**<--What class is this?**
		    Document doc = builder.parse(in); //if we step through to here, this line executes then goes directly to "x" below

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			
			// Get Host up/down state
			NodeList nList = doc.getElementsByTagName("entry");
		 
			
			
			Log.d("TREK", "Running Calendar HTTP Grab.. got entris:" +nList.getLength());
			//String eventName;
			//String eventContent;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				//Log.d("TREK", "Running Here! Num=" + Integer.toString(temp));
				Node nNode = nList.item(temp);
		 
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				//Log.d("TREK", "Current Element :" + nNode.getNodeName());
				Log.d("TREK", "Current Element Here3434");
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String eventName = (String) eElement.getElementsByTagName("title").item(0).getTextContent();
					String eventContent  =  (String)  eElement.getElementsByTagName("summary").item(0).getTextContent();
					//Log.d("TREK", "content: : " + eventContent);
					int endOfTime = eventContent.indexOf(" to ");
					String dateTimeStr = eventContent.substring(6, endOfTime);
					 
					Log.d("TREK", "title Name : " + eventName + " Time " + dateTimeStr);
					//Log.d("TREK", "time : " + dateTimeStr);
					SimpleDateFormat parserSDF;
					
					if (dateTimeStr.indexOf(":") > 0) {
						
						parserSDF = new SimpleDateFormat("EEE MMM d, yyyy hh:mma");
					} else {
						//System.out.println("Here2 -" + dateTimeStr + " / " + dateTimeStr.indexOf(':') );
						parserSDF = new SimpleDateFormat("EEE MMM d, yyyy hha");
					}					 
					//Log.d("TREK", "Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//Log.d("TREK", "State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					
					Date eventDateTime = parserSDF.parse(dateTimeStr);
					
					calHash.put(eventDateTime, eventName);
					
				}
/*
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					
					String eventName = eElement.getElementsByTagName("title").item(0).getTextContent();
					String eventContent  = eElement.getElementsByTagName("content").item(0).getTextContent();
					
					int endOfTime = eventContent.indexOf(" to ");
					String dateTimeStr = eventContent.substring(6, endOfTime);
					 
					Log.d("TREK", "title Name : " + eventName);
					Log.d("TREK", "time : " + dateTimeStr);

					SimpleDateFormat parserSDF;
					
					if (dateTimeStr.indexOf(":") > 0) {
						//System.out.println("Here1");
						parserSDF = new SimpleDateFormat("EEE MMM d, yyyy hh:mma");
					} else {
						//System.out.println("Here2 -" + dateTimeStr + " / " + dateTimeStr.indexOf(':') );
						parserSDF = new SimpleDateFormat("EEE MMM d, yyyy hha");
					}					 
					//Log.d("TREK", "Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//Log.d("TREK", "State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					
					Date eventDateTime = parserSDF.parse(dateTimeStr);
					
					calHash.put(eventDateTime, eventName);
		 
				}*/
			}

			
	 	} catch (Exception e) {
	 		
	    	e.printStackTrace();
	    }
    
	    return calHash;
	}		
	

}
