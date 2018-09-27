package app.cmdctrl;

import org.apache.commons.cli.CommandLine;

public class CheckCmdResult {

	public String command;
	public CommandLine cmdLine;
	public String result;
	public boolean isValid;
	
	public CheckCmdResult(String command, CommandLine cmdLine, String result, boolean isValid) {
		this.command = command;
		this.cmdLine = cmdLine;
		this.result = result;
		this.isValid = isValid;
	}
	
	public CheckCmdResult(String command, String result, boolean isValid) {
		this.command = command;
		this.result = result;
		this.isValid = isValid;
	}
	
}
