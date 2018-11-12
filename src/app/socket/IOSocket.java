package app.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import app.GlobalOpts;
import app.config.Verbosity;

public class IOSocket {
	private SockService service;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Thread oisThread;
	
	private boolean flagOis;
	
	private static Logger logger = Logger.getLogger(IOSocket.class);
	
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
			
			logger.info("OIS thread started");
			
			Object inMessage = "";
			
			while(this.flagOis) { //While thread is up
				try {
					logger.info("waiting for messaged");
					
					while((inMessage = this.ois.readObject() ) != null) {						
						service.inComingData(inMessage);
					}
				}catch(ClassNotFoundException e) {
					logger.error("Error al parsear el mensaje de entrada", e);
					e.printStackTrace();
					
				}catch(IOException e) {
					//e.printStackTrace();				
					
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
		logger.debug("sendData invoked: " + data);
	
		this.oos.writeObject(data);
	}
	
	public void stop() throws IOException {
		logger.debug("socket stopped invoked");
		
		this.flagOis = false;
		this.oisThread.interrupt();
		
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
