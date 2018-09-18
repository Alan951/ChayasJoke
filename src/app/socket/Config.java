package app.socket;

public class Config {
	public String address;
	public int port;
	public boolean attemptConnect;
	public int attempt_times;
	public boolean autoConnect;

	public Config(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public Config(String address, int port, boolean attemptConnect, int attempt_times, boolean autoConnect) {
		this.address = address;
		this.port = port;
		this.attemptConnect = attemptConnect;
		this.attempt_times = attempt_times;
		this.autoConnect = autoConnect;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Config setAddress(String address) {
		this.address = address;
		
		return this;
	}
	
	public int getPort() {
		return port;
	}
	
	public Config setPort(int port) {
		this.port = port;
		
		return this;
	}
	
	public boolean isAttemptConnect() {
		return attemptConnect;
	}
	
	public Config setAttemptConnect(boolean attemptConnect) {
		this.attemptConnect = attemptConnect;
		
		return this;
	}
	
	public int getAttemptTimes() {
		return attempt_times;
	}
	
	public Config setAttemptTimes(int attempt_times) {
		this.attempt_times = attempt_times;
		
		return this;
	}
	
	public boolean isAutoConnect() {
		return autoConnect;
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	@Override
	public String toString() {
		return "Config [address=" + address + ", port=" + port + ", attemptConnect=" + attemptConnect
				+ ", attempt_times=" + attempt_times + "]";
	}
	
}
