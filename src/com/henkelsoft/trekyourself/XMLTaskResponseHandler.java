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

//List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
public class XMLTaskResponseHandler implements ResponseHandler<List<Map<String, String>>> {
	//private final List<String> mResults = new ArrayList<String>();
	
	private Map<String, String> taskHash;	
	
	@Override
	public List<Map<String, String>> handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
		
		List<Map<String, String>> listOfTaskMaps = new ArrayList<Map<String, String>>();
		
		//Log.d("TREK", "Running Calendar HTTP Grab!");
		Log.d("CURDEBUG","Running Task HTTP Grab!");
		try {

			Log.d("TREK", "Running Task HTTP Grab!2");
			// Create the Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			// Set the Parser's input to be the XML document in the HTTP Response
			//xpp.setInput(new InputStreamReader(response.getEntity()
			//		.getContent()));
 
			taskHash = new HashMap<String, String>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); //**<--What class is this?**
		    Document doc = builder.parse(in); //if we step through to here, this line executes then goes directly to "x" below

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			
			// Get Host up/down state
			NodeList nList = doc.getElementsByTagName("task");
			
			Log.d("TREKTASK", "Running Task HTTP Grab.. got entris:" +nList.getLength());
			//String eventName;
			//String eventContent;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				//Log.d("TREK", "Running Here! Num=" + Integer.toString(temp));
				Node nNode = nList.item(temp);
				 
				
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				Log.d("TREKTASK", "Current Element Here4545");
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
				
					HashMap<String, String> curHash = new HashMap<String, String>();
					
					
					curHash.put("title", (String) eElement.getElementsByTagName("title").item(0).getTextContent());
					curHash.put("status", (String) eElement.getElementsByTagName("status").item(0).getTextContent());
					curHash.put("note", (String) eElement.getElementsByTagName("note").item(0).getTextContent());
					listOfTaskMaps.add(curHash);
				}

			}

			
	 	} catch (Exception e) {
	 		
	    	e.printStackTrace();
	    }
		Log.d("TREKTASK", "Returning!");
	    return listOfTaskMaps;
	}		
	

}
