package app.cmdctrl;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import app.GlobalOpts;
import app.ScannerService;
import app.cmdctrl.controllers.config.CmdConfigLoader;
import app.cmdctrl.controllers.config.CmdCredential;
import app.cmdctrl.controllers.config.CmdServConfig;
import app.config.Verbosity;
import app.joke.JokeFactory;
import app.joke.JokeLoader;
import app.joke.MessageSocket;
import com.jalan.cksock.SockServerService;
import com.jalan.cksock.SockService;

public class BasicFunc {
	
	private SockServerService serverService;
	
	protected RouteCmdResult result;
	protected CommandLine cmd;
	
	public BasicFunc() {}
	
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
			case "add-user":
				this.result = addUser();
				break;
		}
		
		return this;
	}
	
	public RouteCmdResult executeJoke(CommandLine cmd) {
		//verifica que joke exista
		if(!JokeFactory.exists(cmd.getOptionValue("joke"))){
			return new RouteCmdResult(-1, "[!] Joke seleccionado no exisste");
		}
		
		MessageSocket message = new MessageSocket(MessageSocket.ACTION_EXECUTE_JOKE);
		String joke = cmd.getOptionValue("joke");
		message.setJokeName(joke);
		Map<String, String> jokeParams;
		String result = "";
		boolean abortExec = false;
		
		//Verifica si tiene parametros
		if(CmdHelper.existsOption("params", cmd.getOptions())) {
			if(cmd.getOptionValues("params").length % 2 != 0) {
				result = "[!] Lista de parametros erronea";
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
			return new RouteCmdResult(1);
		}else {
			if(joke != null && !abortExec) {
				this.serverService.sendAll(message);
				//sendCommand = true;
				return new RouteCmdResult(1);
			}
		}
		
		return new RouteCmdResult(0, result);
	}

	public RouteCmdResult showHelp() {
		HelpFormatter formatter = new HelpFormatter();
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, "ChayasJoke", null, CmdHelper.getOptions(), HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
		
		return new RouteCmdResult(1, sw.toString());
	}
	
	public RouteCmdResult showJokeList() {
		return new RouteCmdResult(1, JokeLoader.getInstance().getJokeListNames().toString());
	}
	
	public RouteCmdResult showClientList() {
		String result = "ID\tIP\n";
		
		for(SockService serv : this.serverService.getClients()) {
			result += "["+ serv.getId() +"]\t"+serv.getSocket().getInetAddress().getHostAddress() + "\n";
		}
		
		return new RouteCmdResult(1, result);
	}
	
	public RouteCmdResult sendEcho(CommandLine cmd) {
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
		
		return new RouteCmdResult(1);
	}
	
	/*
	 * TODO: validate input data
	 * */
	public RouteCmdResult addUser() {
		Scanner sc = ScannerService.getInstance().getScanner();
		
		System.out.println("Ingresar usuario");
		
		String usuario = sc.nextLine();
		char password[];
		String pass;
		
		if(System.console() != null) {
			Console console = System.console();
			password = console.readPassword("Ingresar password: ");
			pass = new String(password);
		}else {
			System.out.println("Ingresar password");
			pass = sc.nextLine();
		}
		
		CmdCredential cred = new CmdCredential(usuario, pass);
				
		System.out.println("Add user credential: " + cred);
		
		try {
			CmdConfigLoader
				.getInstance()
				.loadAndGetCmdServConfig()
				.getUsers().add(cred);
			
			CmdConfigLoader.getInstance().saveCmdServConfig(null);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		return new RouteCmdResult(1);
	}
	
	public BasicFunc then(BasicFuncResult callback) {
		callback.run(result);
		
		this.result = new RouteCmdResult(0);
		
		return this;
	}
	
	public BasicFunc setCommandLine(CommandLine cmd) {
		this.cmd = cmd;
		
		return this;
	}
	
}
