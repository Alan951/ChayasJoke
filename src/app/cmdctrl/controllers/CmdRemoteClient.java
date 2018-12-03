package app.cmdctrl.controllers;

import java.io.IOException;
import java.util.Scanner;

import app.cmdctrl.RouteCmdResult;
import app.joke.MessageSocket;
import com.jalan.cksock.SockService;

public class CmdRemoteClient {

	private SockService sockService;
	
	public CmdRemoteClient(SockService sockService) throws IOException{
		this.sockService = sockService;
		
		init();
		
		this.sockService.connect();
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
				}
			});
			
			this.sockService.sendDataPlz(new MessageSocket(MessageSocket.ACTION_REQ_SET_REMOTE_SERV));
		});
		
		
	}
}
