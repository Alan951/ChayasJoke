package app.cmdctrl;

import java.util.Scanner;

import org.apache.commons.cli.MissingArgumentException;

import app.GlobalOpts;
import app.config.Verbosity;
import app.socket.ServerSockService;

public class CmdServ {
	
	private ServerSockService serverService;
	private BasicFunc basicFunc;
	
	public CmdServ(ServerSockService serverService) {
		this.serverService = serverService;
		basicFunc = new BasicFunc(this.serverService);
	}
	
	public void openCmd() {
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
			System.out.println("[*] Command Control started");
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
		
		sc.close();
	}
	
	public CheckCmdResult checkCommand(String command) {
		if(command.trim().isEmpty()) {
			return new CheckCmdResult(command, "[!] Comando no valido", false);
		}
		
		CheckCmdResult result;
		
		try {
			result = CmdHelper.parseCommand(command, CmdHelper.getOptions());
		}catch(MissingArgumentException e) {
			return new CheckCmdResult(command, "[!] Se esperaban argumentos para el comando", false); 
		}
		
		if(result == null) {
			return new CheckCmdResult(command, "[!] Comando no valido", false);
		}
		
		return result;
	}
	
}
