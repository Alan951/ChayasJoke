package app.config;

import app.socket.SockConfig;

public class DefaultConfigure {

	public static SockConfig getAutoSockConfigServer() {
		return new SockConfig("127.0.0.1", 951);
	}
	
	public static SockConfig getautoSockConfigClient() {
		return new SockConfig("127.0.0.1", 951, false, -1, true);
	}
	
}
