package app.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import app.GlobalOpts;
import app.config.Verbosity;

public class IOSocket {
	private SockService service;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Thread oisThread;
	
	private boolean flagOis;
	
	public IOSocket() {}
	
	public IOSocket(SockService service) {
		this.service = service;
	}
	
	public boolean itsUp() {
		return false;
	}
	
	public void start() throws IOException{
		this.initStreams();
	}
	
	private void initStreams() throws IOException {
		this.initOOS();
		this.initOIS();
	}
	
	private void initOOS() throws IOException {
		this.oos = new ObjectOutputStream(this.service.getSocket().getOutputStream());
	}
	
	private void initOIS() throws IOException {
		this.ois = new ObjectInputStream(this.service.getSocket().getInputStream());
		
		this.oisThread = new Thread(() -> {
			this.flagOis = true;
			if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_DEBUG)
				System.out.println("[*] initOIS invoked - thread started");
			Object inMessage = "";
			
			while(this.flagOis) { //While thread is up
				try {
					if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
						System.out.println("[*] IOSocket waiting for messages");
					while((inMessage = this.ois.readObject() ) != null) {						
						service.inComingData(inMessage);
					}
				}catch(ClassNotFoundException e) {
					e.printStackTrace();
				}catch(IOException e) {
					e.printStackTrace();
					
					this.flagOis = false;
					
					try {
						//this.stop(); //Stop streams
						this.service.close();
					}catch(IOException t) {
						t.printStackTrace();
					}
				}
			}
			
			
		});
		
		this.oisThread.start();
	}
	
	public void sendData(Object data) throws IOException {
		System.out.println("[*] IOSocket sendData invoked: "+ data);
		this.oos.writeObject(data);
	}
	
	public void stop() throws IOException {
		this.flagOis = false;
		
		this.closeOIS();
		this.closeOOS();
	}
	
	private void closeOOS() throws IOException {
		this.oos.flush();
		this.oos.close();
	}
	
	private void closeOIS() throws IOException {
		this.ois.close();
	}
}
