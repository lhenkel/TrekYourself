package com.example.trekyourself;

public class MonitoredServer {

	private String serverName;
	private String serverStatus;
	private String serverMessage;
	
	public void MonitoredServer(String serverNameIn) {
		serverName = serverNameIn;
	}
	
	public void setServerStatus(String statusIn) {
		serverStatus = statusIn;
	}
	
	public void setServerMessage(String messageIn) {
		serverMessage = messageIn;
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
