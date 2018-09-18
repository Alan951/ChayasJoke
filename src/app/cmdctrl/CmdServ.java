package app.cmdctrl;

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;

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
			
			String checkResult = null;
			
			if((checkResult = checkCommand(command)) == null){
				
				basicFunc.routeCommand(command).then((result) -> {
					System.out.println("[R]: "+result);
				});
				
			}else {
				System.out.println("[*] Error: "+checkResult);
			}
		}
		
		sc.close();
	}
	
	
	
	public String checkCommand(String command) {
		if(command.trim().isEmpty()) {
			return "Comando no valido";
		}
		
		CommandLine cmd = CmdHelper.parseCommand(command);
		
		if(cmd == null) {
			return "[!] Comando no valido";
		}
		
		//verify joke exists
		/*if(CmdHelper.existsOption("joke", cmd.getOptions()) && !JokeFactory.exists(cmd.getOptionValue("joke"))) { // don't exists
			
			return "No existe \"joke\" seleccionado";
		}*/
		
		return null;
	}
	
}
