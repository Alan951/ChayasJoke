package app;

import java.io.IOException;
import java.net.BindException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;


import app.cmdctrl.CheckCmdResult;
import app.cmdctrl.CmdHelper;
import app.cmdctrl.controllers.CmdClient;
import app.cmdctrl.controllers.CmdRemoteClient;
import app.cmdctrl.controllers.CmdServ;
import app.config.DefaultConfigure;
import app.config.Verbosity;
import app.joke.MessageSocket;
import com.jalan.cksock.SockConfig;
import com.jalan.cksock.SockLogger;
import com.jalan.cksock.SockServerService;
import com.jalan.cksock.SockService;

public class App {
	
	private CmdServ cmdAndControl;
	private CmdClient cmdClientRecv;
	private CmdRemoteClient cmdRClient;
	private SockConfig sockConfig;
	private int runMode;
	
	public static void main(String [] args) throws IOException, Exception{
		/*if(args.length < 1) {
			System.out.println("[*] Error, faltan parametros de ejecución.\n\t-s Server mode\n\t-c Client mode");
			return;
		}*/
		
		//String mode = args[0];
		try {
			new App()
				.bootstrap()
					.initConfig(args)
						.run();
		}catch(BindException e) {
			System.out.println("[!] Error. Puerto en uso.");
		}
		
	}
	
	/*
	 * TODO: validate input data.
	 * 
	 * */
	public App initConfig(String[] args) throws Exception {
		SockLogger.autoConfigure();
		
		try {
			CheckCmdResult ccr = CmdHelper.parseCommand(args, CmdHelper.startOptions());
			
			CommandLine cli = ccr.cmdLine;
			
			if(cli.hasOption("server")) {
				this.runMode = SockConfig.SERVER_MODE;
				this.sockConfig = DefaultConfigure.getAutoSockConfigServer();
				
				if(cli.hasOption("port")) {
					sockConfig.setPort(Integer.parseInt(cli.getOptionValue("port")));
				}
				
			}else if(cli.hasOption("client")) {
				this.runMode = SockConfig.CLIENT_MODE;
				this.sockConfig = DefaultConfigure.getautoSockConfigClient();
				
				if(cli.hasOption("port")) {
					sockConfig.setPort(Integer.parseInt(cli.getOptionValue("port")));
				}
				
				if(cli.hasOption("connectionAttempt")) {
					sockConfig.setAttemptConnect(true);
				}
				
				if(cli.hasOption("autoConn")) {
					sockConfig.setAutoConnect(true);
				} else {
					sockConfig.setAutoConnect(false);
				}
				
				if(cli.hasOption("numAttempts")) {
					sockConfig.setAttemptTimes(Integer.parseInt(cli.getOptionValue("numAttempts")));
				}
				
				if(cli.hasOption("ip")) {
					sockConfig.setAddress(cli.getOptionValue("ip"));
				}
			}else if(cli.hasOption("remote-server")){
				this.runMode = 3;
				this.sockConfig = new SockConfig();
				this.sockConfig.setAttemptTimes(-1);
				
				if(cli.hasOption("ip")) {
					this.sockConfig.setAddress(cli.getOptionValue("ip"));
				}else {
					throw new MissingArgumentException("IP Address of remote server missing");
				}
				
				if(cli.hasOption("port")) {
					this.sockConfig.setPort(Integer.parseInt(cli.getOptionValue("port")));
				}else {
					throw new MissingArgumentException("Port address of remote server missing");
				}
				
				sockConfig.setConnMode(this.runMode);
				
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ChayasJoke", CmdHelper.startOptions());
				System.exit(0);
			}
			
		}catch(MissingArgumentException e) {
			//e.printStackTrace();
			throw e;
		}
		
		return this;
		
	}
	
	public App bootstrap() {
		GlobalOpts.verboseLevel = Verbosity.VERBOSE_DEBUG;
		
		return this;
	}
	
	public void run() throws IOException {
		if(this.runMode == SockConfig.CLIENT_MODE) {
			runClientMode(this.sockConfig);
		}else if(this.runMode == SockConfig.SERVER_MODE) {
			runServerMode(this.sockConfig);
		}else if(this.runMode == 3) {
			runRemoteServerMode(this.sockConfig);
		}
	}
	
	public void runClientMode(SockConfig config) throws IOException{
		System.out.println("[*] Client mode running");
		
		SockService socket = new SockService();
		
		socket.getConnectionObserver()
			.filter((conn) -> conn.status.equals(SockService.CONNECTED_STATUS))
			.subscribe((conn) -> {
				if(cmdClientRecv != null) { //TODO: cerrar recursos(?)
					
				}
				cmdClientRecv = new CmdClient(socket);
				cmdClientRecv.start();
			});
		
		socket.setConfig(config);
		socket.connect();
	}
	
	public void runServerMode(SockConfig config) throws BindException{
		System.out.println("[*] Server mode running");
		
		SockServerService server = new SockServerService(config);
		
		server.listen();
		cmdAndControl = new CmdServ(server);
		cmdAndControl.openCmd();
	}
	
	public void runRemoteServerMode(SockConfig config) throws IOException {
		System.out.println("[*] Remote Server mode running");
		 
		SockService socket = new SockService();
		
		socket.setConfig(config);
		cmdRClient = new CmdRemoteClient(socket);
		cmdRClient.openCmd();
	}
	
	public static void exit() {
		System.out.println("[!] Bye...");
	}
}
