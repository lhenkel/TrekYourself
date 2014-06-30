package com.example.trekyourself;

public class MonitoredServer {

	private String serverName;
	private String serverStatus;
	private String serverMessage;
	
	MonitoredServer(String serverNameIn) {
		serverName = serverNameIn;
	}

	MonitoredServer() {
		
	}
	
	
	public void setServerStatus(String statusIn) {
		serverStatus = statusIn;
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
