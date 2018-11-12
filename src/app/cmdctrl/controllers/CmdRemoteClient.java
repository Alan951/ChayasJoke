package app.cmdctrl.controllers;

import java.io.IOException;
import java.util.Scanner;

import app.socket.SockService;

public class CmdRemoteClient {

	private SockService sockService;
	
	public CmdRemoteClient(SockService sockService){
		this.sockService = sockService;
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
	
	public void initialize() {
		this.sockService.getConnectionObserver()
			.filter((conn) -> conn.status.equals(SockService.CONNECTED_STATUS))
			.subscribe((conn) -> {
			this.sockService.getMessageObserver().subscribe((message) -> {
				System.out.println("[SERVER]: " + message.toString());
			});
		});
		
	}
	
}
