package app.cmdctrl.controllers;

import java.util.Scanner;

import org.apache.commons.cli.MissingArgumentException;

import app.GlobalOpts;
import app.cmdctrl.BasicFunc;
import app.cmdctrl.CheckCmdResult;
import app.cmdctrl.CmdHelper;
import app.config.Verbosity;
import app.joke.MessageSocket;
import app.socket.MessageWrapper;
import app.socket.SockServerService;
import rx.subjects.PublishSubject;

public class CmdServ {
	
	private SockServerService serverService;
	private BasicFunc basicFunc;
	
	public CmdServ(SockServerService serverService) {
		this.serverService = serverService;
		basicFunc = new BasicFunc(this.serverService);
	}
	
	public void openCmd() {
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
			System.out.println("[*] Command Control started");
		
		this.serverService.getClientMessagesObserver().subscribe((newMessageObject) -> handleMessage(newMessageObject) );
		
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			String command = sc.nextLine();
			
			if(command.equals("exit") || command.equals("salir")){
				System.out.println("Bye...");
				serverService.closeAll();
				break;
			}
			
			CheckCmdResult cmd = checkCommand(command);
			
			if(cmd.isValid) {
				basicFunc.routeCommand(command, cmd.cmdLine).then((result) -> {
					if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_DEBUG)
						System.out.println("[*] Result of command routed: "+result);
				});
			}else {
				System.out.println("[!] Error\n"+cmd.result);
			}
		}
		
		//sc.close();
	}
	
	public void handleMessage(MessageWrapper messageObj) {
		System.out.println("handleMessage: " + messageObj.toString());
		
		if(messageObj.getPayload() instanceof String) {
			
		}else if(messageObj.getPayload() instanceof MessageSocket) {
			MessageSocket message = (MessageSocket) messageObj.getPayload();
			if(message.getAction().equals(MessageSocket.ACTION_REQ_SET_REMOTE_SERV)) {
				System.out.println("Desea ser remote-server el cliente: " + messageObj.getSource());
			}
		}
	}
	
	public CheckCmdResult checkCommand(String command) {
		if(command.trim().isEmpty()) {
			return new CheckCmdResult(command, "[!] Comando no valido", false);
		}
		
		CheckCmdResult result;
		
		try {
			result = CmdHelper.parseCommand(command, CmdHelper.getOptions());
			
			if(result.cmdLine.getOptions().length == 0) {
				result = null; 
			}
			
		}catch(MissingArgumentException e) {
			return new CheckCmdResult(command, "[!] Se esperaban argumentos para el comando", false); 
		}
		
		if(result == null) {
			return new CheckCmdResult(command, "[!] Comando no valido", false);
		}
		
		return result;
	}
	
}
