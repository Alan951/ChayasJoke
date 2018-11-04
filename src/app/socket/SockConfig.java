package app.socket;

public class SockConfig {
	public int connMode;
	public String address;
	public int port;
	public boolean attemptConnect;
	public int attempt_times;
	public boolean autoConnect;
	
	public static final int SERVER_MODE = 0;
	public static final int CLIENT_MODE = 1;

	public SockConfig(int port) {
		this.port = port;
	}
	
	public SockConfig(String address, int port) {
		this.address = address;
		this.port = port;
		this.connMode = 0;
	}
	
	public SockConfig(String address, int port, boolean attemptConnect, int attempt_times, boolean autoConnect) {
		this.address = address;
		this.port = port;
		this.attemptConnect = attemptConnect;
		this.attempt_times = attempt_times;
		this.autoConnect = autoConnect;
		this.connMode = 1;
	}
	
	public String getAddress() {
		return address;
	}
	
	public SockConfig setAddress(String address) {
		this.address = address;
		
		return this;
	}
	
	public int getPort() {
		return port;
	}
	
	public SockConfig setPort(int port) {
		this.port = port;
		
		return this;
	}
	
	public boolean isAttemptConnect() {
		return attemptConnect;
	}
	
	public SockConfig setAttemptConnect(boolean attemptConnect) {
		this.attemptConnect = attemptConnect;
		
		return this;
	}
	
	public int getAttemptTimes() {
		return attempt_times;
	}
	
	public SockConfig setAttemptTimes(int attempt_times) {
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
