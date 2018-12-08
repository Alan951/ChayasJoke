package app.cmdctrl.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import app.cmdctrl.RouteCmdResult;
import app.cmdctrl.controllers.config.CmdCredential;
import app.joke.MessageSocket;

import com.jalan.cksock.MessageWrapper;
import com.jalan.cksock.SockService;

public class CmdRemoteClient {

	private SockService sockService;
	private CmdCredential credential;
	
	public CmdRemoteClient(SockService sockService) throws IOException{
		this.sockService = sockService;
		
		init();
		
		this.sockService.connect();
	}
	
	public CmdRemoteClient(SockService sockService, CmdCredential cred) throws IOException {
		this(sockService);
		
		this.credential = cred;
	}
	
	public void openCmd() {
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			String command = sc.nextLine();
			
			if(command.equals("exit") || command.equals("salir")) {
				try {
					sockService.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
				
				
				System.out.println("Bye...");
				break;
			}
			
			sockService.sendDataPlz(command);
		}
	}
	
	public void init() {
		this.sockService.getConnectionObserver()
			.filter((conn) -> conn.status.equals(SockService.CONNECTED_STATUS))
			.subscribe((conn) -> {
				
			this.sockService.getMessageObserver().subscribe((message) -> {
				if(message.getPayload() instanceof RouteCmdResult) {
					RouteCmdResult result = (RouteCmdResult)message.getPayload();
					
					System.out.println("[SERVER]: " + result.resultCode);
					System.out.println("[SERVER]: " + result.result);
				}else if(message.getPayload() instanceof MessageSocket) {
					handleMessage(message);
				}
			});
			
			if(this.credential != null) {
				MessageSocket message = new MessageSocket();
				message.setAction(MessageSocket.ACTION_REQ_SET_REMOTE_SERV_W_CRED);
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", this.credential.getUsername());
				map.put("pass", this.credential.getPassword());
				message.setJokeParams(map);
				
				this.sockService.sendDataPlz(message);
			}else {
				this.sockService.sendDataPlz(new MessageSocket(MessageSocket.ACTION_REQ_SET_REMOTE_SERV));
			}
			
		});
		
	}
	
	public void handleMessage(MessageWrapper messageWrapped) {
		MessageSocket message = (MessageSocket)messageWrapped.getPayload();
		
		switch(message.getAction()) {
			case MessageSocket.ACTION_REMOTE_WELCOME:
				System.out.println("[*] Welcome");
				break;
			case MessageSocket.ACTION_NEED_AUTH:
				System.out.println("[!] Requiere de autenticación. Use -u <username> y luego proporcione la contraseña");
				System.exit(0);
				break;
			case MessageSocket.ACTION_REMOTE_AUTH_FAIL:
				System.out.println("[!] Usuario y/o contraseña incorrectos");
				System.exit(0);
				break;
			default:
				System.out.println(message);
				break;
		}
	}
}
