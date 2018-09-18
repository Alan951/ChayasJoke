package app.joke;

import java.io.Serializable;
import java.util.Map;

public class MessageSocket implements Serializable {

	private static final long serialVersionUID = 1813513776131357264L;
	
	
	public static final String ACTION_EXECUTE_JOKE 		= "EXEC_JOKE";
	public static final String ACTION_EXECUTE_COMMAND 	= "EXEC_CMD";
	public static final String ACTION_JOKE_NOT_FOUND 	= "JOKE_NOT_FOUND";
	public static final String ACTION_JOKE_EXECUTED 	= "JOKE_EXECUTED";
	public static final String ACTION_COMMAND_EXECUTED	= "CMD_EXECUTED";

	private int code;
	private String action;
	private String command;
	private String param;
	private String jokeName;
	private Map<String, String> jokeParams;
	
	public MessageSocket() {}
	
	public MessageSocket(String action) {
		this.action = action;
		this.code = 200;
	}
	
	public MessageSocket(int code, String action) {
		this.code = code;
		this.action = action;
	}
	
	public MessageSocket(String action, String command) {
		this.code = 200;
		this.action = action;
		this.command = command;
	}
	
	public MessageSocket(String action, String command, String param) {
		this.code = 200;
		this.action = action;
		this.command = command;
		this.param = param;
	}
	
	public MessageSocket(String action, String jokeName, Map<String, String> jokeParams) {
		this.code = 200;
		this.action = action;
		this.jokeName = jokeName;
		this.jokeParams = jokeParams;
	}
	
	public MessageSocket(int code, String action, String jokeName, Map<String, String> jokeParams) {
		this.code = code;
		this.action = action;
		this.jokeName = jokeName;
		this.jokeParams = jokeParams;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getJokeName() {
		return jokeName;
	}

	public void setJokeName(String jokeName) {
		this.jokeName = jokeName;
	}

	public Map<String, String> getJokeParams() {
		return jokeParams;
	}

	public void setJokeParams(Map<String, String> jokeParams) {
		this.jokeParams = jokeParams;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@Override
	public String toString() {
		return "MessageSocket [code=" + code + ", action=" + action + ", jokeName=" + jokeName + ", jokeParams="
				+ jokeParams + "]";
	}
}
