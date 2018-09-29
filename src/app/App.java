package app;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;

import app.cmdctrl.BasicFunc;
import app.cmdctrl.BasicFuncResult;
import app.cmdctrl.CheckCmdResult;
import app.cmdctrl.CmdClient;
import app.cmdctrl.CmdHelper;
import app.cmdctrl.CmdServ;
import app.config.DefaultConfigure;
import app.config.Verbosity;
import app.socket.SockConfig;
import app.socket.ServerSockService;
import app.socket.SockService;

public class App {
	
	private CmdServ cmdAndControl;
	private CmdClient cmdClientRecv;
	private SockConfig sockConfig;
	private int runMode;
	
	public static void main(String [] args) throws IOException{
		/*if(args.length < 1) {
			System.out.println("[*] Error, faltan parametros de ejecución.\n\t-s Server mode\n\t-c Client mode");
			return;
		}*/
		
		//String mode = args[0];
		
		new App()
			.bootstrap()
				.initConfig(args)
					.run();
	}
	
	public App initConfig(String[] args) {
		try {
			
			
			CheckCmdResult ccr = CmdHelper.parseCommand(args, CmdHelper.startOptions());
			
			CommandLine cli = ccr.cmdLine;
			
			if(cli.hasOption("server")) {
				this.runMode = 2;
				this.sockConfig = DefaultConfigure.getAutoSockConfigServer();
				
				if(cli.hasOption("port")) {
					sockConfig.setPort(Integer.parseInt(cli.getOptionValue("port")));
				}
				
			}else if(cli.hasOption("client")) {
				this.runMode = 1;
				this.sockConfig = DefaultConfigure.getautoSockConfigClient();
				
			}else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ChayasJoke", CmdHelper.startOptions());
				System.exit(0);
			}
			
		}catch(MissingArgumentException e) {
			e.printStackTrace();
		}
		
		return this;
		
	}
	
	public App bootstrap() {
		GlobalOpts.verboseLevel = Verbosity.VERBOSE_DEBUG;
		
		return this;
	}
	
	public void run() throws IOException {
		if(this.runMode == 1) {
			runClientMode(this.sockConfig);
		}else if(this.runMode == 2) {
			runServerMode(this.sockConfig);
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
	
	public void runServerMode(SockConfig config) {
		System.out.println("[*] Server mode running");
		
		ServerSockService server = new ServerSockService(config);
		
		server.startInComingConnections();
		cmdAndControl = new CmdServ(server);
		cmdAndControl.openCmd();
		
	}
}
