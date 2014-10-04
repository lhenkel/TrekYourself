package com.henkelsoft.trekyourself;

import java.util.HashMap;

public class MonitoredServer {

	private String serverName;
	private String serverStatus;
	private String serverMessage;
	private HashMap<String, Object> serviceStatusMap;
	
	MonitoredServer(String serverNameIn) {
		serverName = serverNameIn;
		serviceStatusMap = new HashMap<String,Object>();
	}

	MonitoredServer() {
		serviceStatusMap = new HashMap<String,Object>();
	}
	
	public void setServerStatus(String statusIn) {
		serverStatus = statusIn;
	}

	public void addServiceStatus(String serviceName, int serviceStatus, String longText) {
		HashMap<String,Object> curStatusMap = new HashMap<String,Object>();
		curStatusMap.put("Status", serviceStatus);
		curStatusMap.put("LongText", longText);
		serviceStatusMap.put(serviceName, curStatusMap);
	}
	
	public HashMap<String,Object> getStatusMap() {
		return serviceStatusMap;
	}
	public void setServerMessage(String messageIn) {
		serverMessage = messageIn;
	}

	public void setServerName(String nameIn) {
		serverName = nameIn;
	}	
	
	public String getName() {
		return serverName;
	}
	public String getStatus() {
		return serverStatus;
	}
	public String getMessage() {
		return serverMessage;
	}
}
