package com.henkelsoft.trekyourself;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.henkelsoft.trekyourself.*;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;



public class XMLResponseHandler implements ResponseHandler<HashMap<String, MonitoredServer>> {

	private final List<String> mResults = new ArrayList<String>();
	
	private HashMap<String, MonitoredServer> serverHash;
	
	@Override
	public HashMap<String, MonitoredServer> handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
		
		Log.d("TREK", "Running Server HTTP Grab! What Arent this executing2");
		
		try {

			Log.d("TREK", "Running Server HTTP Grab! What Arent this executing");
			// Create the Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			// Set the Parser's input to be the XML document in the HTTP Response
			//xpp.setInput(new InputStreamReader(response.getEntity()
			//		.getContent()));
 
			serverHash = new HashMap<String, MonitoredServer>();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); //**<--What class is this?**
		    Document doc = builder.parse(in); //if we step through to here, this line executes then goes directly to "x" below

//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
	//		Document doc = dBuilder.parse(new InputStreamReader(response.getEntity()
		//			.getContent()).toString());
			 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			MonitoredServer curServer = null;
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			
			// Get Host up/down state
			NodeList nList = doc.getElementsByTagName("host");
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
				//Log.d("TREK", "Running Here! Num=" + Integer.toString(temp));
				Node nNode = nList.item(temp);
		 
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					
					String serverName = eElement.getElementsByTagName("host_name").item(0).getTextContent();
					String curStatus  = eElement.getElementsByTagName("current_state").item(0).getTextContent();
					curServer = new MonitoredServer();
					curServer.setServerName(serverName);
					curServer.setServerStatus(curStatus);
					 
					//System.out.println("Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//Log.d("TREK", "Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//Log.d("TREK", "State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					
					//System.out.println("State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					serverHash.put(serverName, curServer);
		 
				}
			}

			nList = doc.getElementsByTagName("service");	// now loop through and see if any services are down
			 
			//Log.d("TREK", "----------------------------");
			
			
		 	for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
				//Log.d("TREK", "Current Element :" + nNode.getNodeName());
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					
					String serverName = eElement.getElementsByTagName("host_name").item(0).getTextContent();
					//int foo = Integer.parseInt("1234");
					String curServiceStatusStr = eElement.getElementsByTagName("current_state").item(0).getTextContent();
					int curServiceStatus  = Integer.parseInt(curServiceStatusStr);
					String curService  = eElement.getElementsByTagName("service_description").item(0).getTextContent();
					
					curServer = (MonitoredServer) serverHash.get(serverName);
					
					int overalServerStatus = Integer.parseInt(curServer.getStatus()); 
					
					if (curServiceStatus > overalServerStatus) {
						// s*@t just got real, yo (sorry, watching the Wire)
						curServer.setServerStatus(curServiceStatusStr);
						curServer.setServerMessage(curService);
						serverHash.put(serverName, curServer);
					}
					
					//Log.d("TREK", "Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//Log.d("TREK", "State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					//System.out.println("Host Name : " + eElement.getElementsByTagName("host_name").item(0).getTextContent());
					//System.out.println("State : " + eElement.getElementsByTagName("current_state").item(0).getTextContent());
					serverHash.put(serverName, curServer);
		 
				}
			}
			
			
	 	} catch (Exception e) {
	 		
	    	e.printStackTrace();
	    }
		 
	    /*Iterator it = serverHash.entrySet().iterator();
	    MonitoredServer curServer = null;
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        //System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        curServer =  (MonitoredServer) pairs.getValue();
	        //System.out.println(pairs.getKey());
	        Log.d("TREK", (String) pairs.getKey());
	        //System.out.println(curServer.getStatus());
	        Log.d("TREK", (String) curServer.getStatus());
	        it.remove(); // avoids a ConcurrentModificationException
	    }		*/ 
	    
	    return serverHash;
	}	
	
}
