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


public class XMLSoundResponseHandler implements ResponseHandler<Map<String, Integer>> {
	//private final List<String> mResults = new ArrayList<String>();
	
	private Map<String, Integer> soundHash;	
	
	@Override
	public Map<String, Integer> handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
		
		//Log.d("TREK", "Running Calendar HTTP Grab!");
		Log.d("CURDEBUG","Running Sound HTTP Grab!");
		try {

			Log.d("TREK", "Running Sound HTTP Grab!2");
			// Create the Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			// Set the Parser's input to be the XML document in the HTTP Response
			//xpp.setInput(new InputStreamReader(response.getEntity()
			//		.getContent()));
 
			soundHash = new HashMap<String, Integer>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); //**<--What class is this?**
		    Document doc = builder.parse(in); //if we step through to here, this line executes then goes directly to "x" below

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			
			// Get Host up/down state
			NodeList nList = doc.getElementsByTagName("sound_level");
			
			Log.d("TREKSOUND", "Running Sound HTTP Grab.. got entris:" +nList.getLength());
			//String eventName;
			//String eventContent;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				//Log.d("TREK", "Running Here! Num=" + Integer.toString(temp));
				Node nNode = nList.item(temp);
		 
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				//Log.d("TREK", "Current Element :" + nNode.getNodeName());
				Log.d("TREKSOUND", "Current Element Here4545");
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					/*
					int min_noise_level = Integer.parseInt((String) eElement.getElementsByTagName("min_noise_level").item(0).getTextContent());
					int omg_noise_level = Integer.parseInt((String) eElement.getElementsByTagName("omg_noise_level").item(0).getTextContent());
					int rfl_noise_level = Integer.parseInt((String) eElement.getElementsByTagName("rfl_noise_level").item(0).getTextContent());
					int irritation_level = Integer.parseInt((String) eElement.getElementsByTagName("irritation_level").item(0).getTextContent());
					*/
					
					soundHash.put("min_noise_level", Integer.parseInt((String) eElement.getElementsByTagName("min_noise_level").item(0).getTextContent()));
					soundHash.put("omg_noise_level", Integer.parseInt((String) eElement.getElementsByTagName("omg_noise_level").item(0).getTextContent()));
					soundHash.put("rfl_noise_level", Integer.parseInt((String) eElement.getElementsByTagName("rfl_noise_level").item(0).getTextContent()));
					soundHash.put("irritation_level", Integer.parseInt((String) eElement.getElementsByTagName("irritation_level").item(0).getTextContent()));
				}

			}

			
	 	} catch (Exception e) {
	 		
	    	e.printStackTrace();
	    }
		Log.d("TREKSOUND", "Returning!");
	    return soundHash;
	}		
	

}
