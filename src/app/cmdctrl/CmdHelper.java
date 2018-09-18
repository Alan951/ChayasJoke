package app.cmdctrl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdHelper {
	public static Options getOptions() {		
		Options options = new Options();
		
		Option chooseJoke = new Option("j", "joke", true, "Joke name");
		chooseJoke.setRequired(false);
		options.addOption(chooseJoke);
		
		Option chooseIdClient = new Option("i", "id", true, "Id client to send command");
		chooseIdClient.setRequired(false);
		options.addOption(chooseIdClient);
		
		Option paramsJoke = new Option("p", "params", true, "Params of Joke");
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
	
	public static CommandLine parseCommand(String command) {
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		
		try {
			List<String> cmds2 = new ArrayList<>();
			String cmds[] = command.split(" ");
			String tempCmd = "";
			for(int x = 0 ; x < cmds.length ; x++) {
				if(!tempCmd.equals("") || cmds[x].startsWith("\"")) {
					if(cmds[x].startsWith("\"")) {
						tempCmd += cmds[x].replaceAll("\"", "");
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
			
			cmd = parser.parse(CmdHelper.getOptions(), cmds2.stream().toArray(String[]::new));
			
		}catch(ParseException e) {
			//e.printStackTrace();
			return null;
		}
		
		return cmd;
	}
}
