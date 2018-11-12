package app.cmdctrl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import app.GlobalOpts;
import app.config.Verbosity;
import app.joke.JokeFactory;
import app.joke.JokeLoader;
import app.joke.MessageSocket;
import app.socket.SockServerService;

public class BasicFunc {
	
	private SockServerService serverService;
	
	protected Integer result;
	protected CommandLine cmd;
	
	public BasicFunc(SockServerService serverService) {
		this.serverService = serverService;
	}
	
	public BasicFunc routeCommand(String command, CommandLine cmd) {		
		return setCommandLine(cmd)
				.routeCommand(command);
	}

	public BasicFunc routeCommand(String command) {
		String mainCommand = cmd.getOptions()[0].getLongOpt();
		
		if(GlobalOpts.verboseLevel == Verbosity.VERBOSE_DEBUG) {
			System.out.println("[*] Main Command: " + mainCommand);
		}
		
		switch(mainCommand) {
			case "joke":
				this.result = executeJoke(cmd);
				break;
			case "echo":
				this.result = sendEcho(cmd);
				break;
			case "joke-list":
				this.result = showJokeList();
				break;
			case "client-list":
				this.result = showClientList();
				break;
			case "help":
				this.result = showHelp();
				break;
		}
		
		return this;
	}
	
	public int executeJoke(CommandLine cmd) {
		//verifica que joke exista
		if(!JokeFactory.exists(cmd.getOptionValue("joke"))){
			if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
				System.out.println("[!] Joke seleccionado no existe");
			return -1;
		}
		
		MessageSocket message = new MessageSocket(MessageSocket.ACTION_EXECUTE_JOKE);
		String joke = cmd.getOptionValue("joke");
		message.setJokeName(joke);
		Map<String, String> jokeParams;
		boolean abortExec = false;
		
		//Verifica si tiene parametros
		if(CmdHelper.existsOption("params", cmd.getOptions())) {
			if(cmd.getOptionValues("params").length % 2 != 0) {
				System.out.println("[!] Lista de parametros erronea");
				abortExec = true;
			}else {
				jokeParams = new HashMap<String, String>();
				
				for(int x = 0 ; x < cmd.getOptionValues("params").length ; x += 2) {							
					jokeParams.put(cmd.getOptionValues("params")[x], cmd.getOptionValues("params")[x+1]);
				}
				
				message.setJokeParams(jokeParams);
				
				//System.out.println(jokeParams);
			}
		}
		
		if(CmdHelper.existsOption("id", cmd.getOptions())) {
			long id = Long.parseLong(cmd.getOptionValue("id"));
			
			serverService.sendData(message, id);
			return 1;
		}else {
			if(joke != null && !abortExec) {
				this.serverService.sendAll(message);
				//sendCommand = true;
				return 1;
			}
		}
		
		return 0;
	}

	public int showHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ChayasJoke", CmdHelper.getOptions());
		
		return 1;
	}
	
	public int showJokeList() {
		System.out.println(JokeLoader.getInstance().getJokeListNames());
		return 1;
	}
	
	public int showClientList() {
		System.out.println("ID\tIP");
		this.serverService.getClients().forEach((sockService) -> {
			System.out.println("["+ sockService.getId() +"]\t"+sockService.getSocket().getInetAddress().getHostAddress());
		});
		
		return 1;
	}
	
	public int sendEcho(CommandLine cmd) {
		String echoMessage = cmd.getOptionValue("echo");
		System.out.println("Echo sended: "+echoMessage);
		
		if(echoMessage != null) {
			if(CmdHelper.existsOption("id", cmd.getOptions())) {
				String idstr = cmd.getOptionValue("id");
				long id = Long.parseLong(idstr);
				
				this.serverService.sendData(
						new MessageSocket(
								MessageSocket.ACTION_EXECUTE_COMMAND, 
								Commands.ECHO.name(), 
								echoMessage), 
						id);
			}else {
				this.serverService
					.sendAll(new MessageSocket(MessageSocket.ACTION_EXECUTE_COMMAND, Commands.ECHO.name(), echoMessage));
				//sendCommand = true;
			}						
		}
		
		return 1;
	}
	
	public BasicFunc then(BasicFuncResult callback) {
		callback.run(result);
		
		this.result = 0;
		
		return this;
	}
	
	public BasicFunc setCommandLine(CommandLine cmd) {
		this.cmd = cmd;
		
		return this;
	}
	
}
