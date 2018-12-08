package app.cmdctrl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdHelper {
	public static Options startOptions() {
		Options options = new Options();
		
		Option hostClientMode = new Option("s", "server", false, "Server Mode");
		Option ipAddrToConnect = new Option("ip", "ip", true, "IP Address of Server. Default: \"127.0.0.1\"");
		ipAddrToConnect.setType(String.class);
		
		Option port = new Option("p", "port", true, "Port to connect or to binding server. Default: \"951\"");
		port.setType(Integer.class);
		
		Option connectAttempt = new Option("ca", "connectionAttempt", false, "Enable connection attempts to server. Default: \"false\"");
		
		Option attemptConnectTimes = new Option("t", "numAttempts", true, "Set the number of attempts to connect. The default value is: -1 (infinite) but if the connection attempt is false, only connect once.");
		attemptConnectTimes.setType(Integer.class);
		
		Option autoConnect = new Option("ac", "autoConn", false, "Enable auto-connection. When close connection, auto-connection try connecting again. Use -ca with -ac to create a daemon which forever try connect to joke-server.");	
		
		Option hostServerMode = new Option("c", "client", false, "Victim Mode");
		Option help = new Option("h", "help", false, "This message");
		
		Option setUser = new Option("u", "user", true, "Set user credential authentication to connect with remote server");
		
		Option remoteServerMode = new Option("rs", "remote-server", false, "Connect to server");
		Option passwordRemoteServ = new Option("pass", "password", false, "Password for connect to remote server");
		
		options.addOption(remoteServerMode);
		options.addOption(passwordRemoteServ);
		options.addOption(hostClientMode);
		options.addOption(ipAddrToConnect);
		options.addOption(port);
		options.addOption(connectAttempt);
		options.addOption(attemptConnectTimes);
		options.addOption(autoConnect);
		options.addOption(hostServerMode);
		options.addOption(help);
		options.addOption(setUser);
		
		return options;
	}
	
	public static Options getOptions() {		
		Options options = new Options();
		
		Option addUser = new Option("a", "add-user", false, "Add new user credential to validate authentication");
		addUser.setRequired(false);
		options.addOption(addUser);
		
		Option enableAuth = new Option("ea", "enable-auth", true, "Enable authentication for remote server");
		enableAuth.setType(Boolean.class);
		options.addOption(enableAuth);
	
		Option chooseJoke = new Option("j", "joke", true, "Joke name");
		chooseJoke.setRequired(false);
		options.addOption(chooseJoke);
		
		Option chooseIdClient = new Option("i", "id", true, "Id client to send command");
		chooseIdClient.setRequired(false);
		options.addOption(chooseIdClient);
		
		Option paramsJoke = new Option("p", "params", true, "Params of Joke in format \"paramName=valueOfParam\". Joke is required");
		paramsJoke.setRequired(false);
		paramsJoke.setValueSeparator('=');
		paramsJoke.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(paramsJoke);
		
		
		options.addOption(new Option("l", "joke-list", false, "List of Jokes"));
		
		options.addOption(new Option("cl", "client-list", false, "List clients"));
		
		options.addOption(new Option("h", "help", false, "This message"));
		
		
		Option echoOpt = new Option("e", "echo", true, "Echo test");
		echoOpt.setArgs(1);
		options.addOption(echoOpt);
		
		
		return options;
	}
	
	public static boolean existsOption(String option, Option[] options) {
		
		for(int x = 0 ; x < options.length ; x++) {
			if(options[x].getLongOpt().equals(option)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static CheckCmdResult parseCommand(String command, Options options) throws MissingArgumentException {
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		
		try {
			List<String> cmds2 = new ArrayList<>();
			String cmds[] = command.split(" ");
			String tempCmd = "";
			for(int x = 0 ; x < cmds.length ; x++) {
				if(!tempCmd.equals("") || cmds[x].startsWith("\"")) {
					if(cmds[x].startsWith("\"")) {
						if(cmds[x].endsWith("\"")) { //arg "without_spaces"
							cmds2.add(cmds[x].replaceAll("\"", ""));
							tempCmd = "";
						}else {
							tempCmd += cmds[x].replaceAll("\"", "");
						}
						
					}else if(cmds[x].endsWith("\"")) {
						tempCmd += " "+cmds[x].replaceAll("\"", "");
						cmds2.add(tempCmd);
						tempCmd = "";
					}else {
						//cmds2.add(" "+cmds[x]);
						tempCmd += " "+cmds[x];
					}
				}else {
					cmds2.add(cmds[x]);
					
				}
			}
			
			if(!tempCmd.equals(""))
				throw new ParseException("Falta cierre de comillas");
			
			cmd = parser.parse(options, cmds2.stream().toArray(String[]::new));
			
		}catch(MissingArgumentException e) {
			throw e;
		}catch(ParseException e) {
			//e.printStackTrace();
			return null;
		}
		
		return new CheckCmdResult(command, cmd, "ok", true);
	}
	
	public static CheckCmdResult parseCommand(String args[], Options options) throws MissingArgumentException {
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		
		try {
			
			
			cmd = parser.parse(options, args);
			
		}catch(MissingArgumentException e) {
			throw e;
		}catch(ParseException e) {
			//e.printStackTrace();
			return null;
		}
		
		return new CheckCmdResult(null, cmd, "ok", true);
	}
}
