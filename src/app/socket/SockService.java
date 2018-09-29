package app.socket; 

import java.io.IOException;
import java.net.Socket;

import app.GlobalOpts;
import app.config.Verbosity;
import rx.subjects.PublishSubject;

public class SockService {
	public static final String CONNECTED_STATUS = "CONNECTED";
	public static final String DISCONNECTED_STATUS = "DISCONNECTED";
	public static final String ATTEMPT_CONNECT_STATUS = "ATTEMPT_CONNECT";
	
	private Socket socket;
	private IOSocket ioSocket;
	private SockConfig conf;
	
	private long id;
	
	private PublishSubject<Object> observerMessages;
	private PublishSubject<ConnectionStatus> observerConnection = PublishSubject.create();
	
	public SockService() {
		//Default configuration Socket
		//this.conf = new SockConfig("localhost", 4465);
	}
	
	public void connect() throws IOException {
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_DEBUG) {
			System.out.println("[!] Attempting connect to "+this.conf.address+":"+this.conf.port);
		}
		this.connect(this.conf);
	}
	
	public void connect(SockConfig conf) throws IOException{
		this.setConfig(conf);
		
		if(conf.getAttemptTimes() != 0) {
			new Thread(() -> {
				try {
					int attempts = 0;
					
					while(attempts < conf.attempt_times) {
						System.out.println("[*] ATTEMPTING CONNECT");
						this.observerConnection.onNext(new ConnectionStatus("ATTEMPT_CONNECT_STATUS", this));
						
						if(this.connectSocket()) {
							break;
						}
						
						Thread.sleep(1000 * 10);
					}
					
				}catch(InterruptedException e) {
					e.printStackTrace();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}).start();
		}else {
			System.out.println("[*] ATTEMPTING CONNECT");
			this.observerConnection.onNext(new ConnectionStatus("ATTEMPT_CONNECT_STATUS", this));
			connectSocket();
		}
	}
	
	private boolean connectSocket() throws IOException {
		try {
			this.socket = new Socket(this.conf.getAddress(), this.conf.getPort());
		}catch(IOException e) {
			//e.printStackTrace();
			return false;
		}
		
		this.onConnected();
		
		return true;
	}
	
	private void onConnected() throws IOException {
		this.observerMessages = PublishSubject.create();
		
		this.ioSocket = new IOSocket(this);
		this.ioSocket.start();
		
		System.out.println("[*] onConnected invoked");
		this.observerConnection.onNext(new ConnectionStatus(SockService.CONNECTED_STATUS, this));
	}
	
	private void onDisconnected() throws IOException {
		System.out.println("[*] onDisconnected invoked");
		this.observerConnection.onNext(new ConnectionStatus(SockService.DISCONNECTED_STATUS, this));
		
		if(this.conf.isAutoConnect()) {
			this.connect();
		}
	}
	
	public boolean close() throws IOException{
		this.observerMessages.onCompleted();
		
		try {
			this.ioSocket.stop();
			this.socket.close();
			
			this.onDisconnected();
		}catch(IOException e) {
			//e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public SockService setConfig(SockConfig conf) {
		this.conf = conf;
		
		return this;
	}
	
	public void setSocket(Socket socket) throws IOException {
		if(!socket.isClosed()) {
			this.socket = socket;
			
			this.onConnected();
		}
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public PublishSubject<Object> getMessageObserver(){
		return this.observerMessages;
	}
	
	public void inComingData(Object message) {
		this.observerMessages.onNext(message);
	}
	
	public void sendData(Object data) throws IOException {
		this.ioSocket.sendData(data);
	}
	
	public void sendDataPlz(Object data) {
		try {
			this.ioSocket.sendData(data);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public PublishSubject<ConnectionStatus> getConnectionObserver(){
		return this.observerConnection;
	}
}
