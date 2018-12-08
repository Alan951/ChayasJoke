package app.cmdctrl.controllers;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.cli.MissingArgumentException;

import app.GlobalOpts;
import app.ScannerService;
import app.cmdctrl.BasicFunc;
import app.cmdctrl.CheckCmdResult;
import app.cmdctrl.CmdHelper;
import app.cmdctrl.controllers.config.CmdConfigLoader;
import app.cmdctrl.controllers.config.CmdCredential;
import app.cmdctrl.controllers.config.CmdServConfig;
import app.config.Verbosity;
import app.joke.MessageSocket;
import com.jalan.cksock.MessageWrapper;
import com.jalan.cksock.SockServerService;
import com.jalan.cksock.SockService;
import rx.subjects.PublishSubject;

public class CmdServ {
	
	private SockServerService serverService;
	private SockService remoteServerService;
	private CmdCredential credentials;
	private BasicFunc basicFunc;
	
	public CmdServ(SockServerService serverService) {
		this.serverService = serverService;
		basicFunc = new BasicFunc(this.serverService);
		
		try {
			CmdConfigLoader.getInstance().loadCmdServConfig();
		}catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void openCmd() {
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
			System.out.println("[*] Command Control started");
		
		this.serverService.getClientMessagesObserver().subscribe((newMessageObject) -> handleMessage(newMessageObject) );
		
		Scanner sc = ScannerService.getInstance().getScanner();
		
		while(true) {
			String command = sc.nextLine();	
			
			if(command.equals("exit") || command.equals("salir")){
				System.out.println("Bye...");
				serverService.closeAll();
				break;
			}
			
			onEnterCommand(command);
		}
		
		sc.close();
	}
	
	public void onEnterCommand(String command) {
		CheckCmdResult cmd = checkCommand(command);
		
		if(cmd.isValid) {
			basicFunc.routeCommand(command, cmd.cmdLine).then((result) -> {
				System.out.println("[:] ResultCode: " + result.resultCode);
				if(result.result != null)
					System.out.println(result.result);
				
				if(this.remoteServerService != null) {
					this.remoteServerService.sendDataPlz(result);
				}
			});
		}else {
			System.out.println("[!] Error\n"+cmd.result);
			
			if(this.remoteServerService != null) {
				this.remoteServerService.sendDataPlz("[!] Error\n"+cmd.result);
			}
		}
	}
	
	
	public void setClientRemote(SockService service) {
		this.remoteServerService = service;
		this.remoteServerService.sendDataPlz(new MessageSocket(MessageSocket.ACTION_REMOTE_WELCOME));
		this.remoteServerService.getConnectionObserver().filter((connEvt) -> connEvt.status == SockService.DISCONNECTED_STATUS).subscribe((onDisconnected) -> this.remoteServerService = null);
	}
	
	public void handleMessage(MessageWrapper messageObj) {
		System.out.println("handleMessage: " + messageObj.toString());
		
		if(this.remoteServerService != null && this.remoteServerService != messageObj.getSource()) {
			this.remoteServerService.sendDataPlz(messageObj);
		}
		
		if(messageObj.getPayload() instanceof String) {
			if(this.remoteServerService != null && this.remoteServerService == messageObj.getSource())
				onEnterCommand((String)messageObj.getPayload());
			
			
		}else if(messageObj.getPayload() instanceof MessageSocket) {
			MessageSocket message = (MessageSocket) messageObj.getPayload();
			
			if(message.getAction().equals(MessageSocket.ACTION_REQ_SET_REMOTE_SERV) || message.getAction().equals(MessageSocket.ACTION_REQ_SET_REMOTE_SERV_W_CRED)) {
				if(CmdConfigLoader.getInstance().getCmdServConfig().isRequiredAuthRemoteClient()) {
					if(message.getAction().equals(MessageSocket.ACTION_REQ_SET_REMOTE_SERV_W_CRED)) {
						String usuario = message.getJokeParams().get("user");
						String password = message.getJokeParams().get("pass");
						
						if(CmdConfigLoader
								.getInstance()
								.getCmdServConfig()
								.getUsers().stream()
								.filter((cred) -> 
									cred.getUsername()
									.equals(usuario) && cred.getPassword()
									.equals(password))
									.findFirst()
									.isPresent()) {
							
							setClientRemote(messageObj.getSource());
						}else {
							messageObj.getSource().sendDataPlz(new MessageSocket(MessageSocket.ACTION_REMOTE_AUTH_FAIL));
							
							try {
								Thread.sleep(500);
								messageObj.getSource().close();
							}catch(Exception e) {
								e.printStackTrace();
							}
							
						}
					}else {
						messageObj.getSource().sendDataPlz(new MessageSocket(MessageSocket.ACTION_NEED_AUTH));

						try {
							Thread.sleep(500);
							messageObj.getSource().close();
						}catch(InterruptedException e) {
							e.printStackTrace();
						}catch(IOException e) {
							e.printStackTrace();
						}
					}
				}else {
					setClientRemote(messageObj.getSource());
					return;
				}				
			}
		}
	}
	
	public CheckCmdResult checkCommand(String command) {
		if(command.trim().isEmpty()) {
			return new CheckCmdResult(command, "[!] No se encontro comando", false);
		}
		
		CheckCmdResult result;
		
		try {
			result = CmdHelper.parseCommand(command, CmdHelper.getOptions());
			
			if(result != null && result.cmdLine.getOptions().length == 0) {
				result = null; 
			}
			
		}catch(MissingArgumentException e) {
			return new CheckCmdResult(command, "[!] Se esperaban argumentos para el comando", false); 
		}
		
		if(result == null) {
			return new CheckCmdResult(command, "[!] Comando "+command+" no valido", false);
		}
		
		return result;
	}
	
}
