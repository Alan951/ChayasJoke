package app.cmdctrl;

import app.joke.JokeExecutor;
import app.joke.MessageSocket;
import app.socket.SockService;

public class CmdClient {

	private SockService sockService;
	private JokeExecutor jokeExecutor;
	
	public CmdClient() {}
	
	public CmdClient(SockService socksService) {
		this.sockService = socksService;
		
		jokeExecutor = new JokeExecutor(sockService);
	}
	
	public void start() {
		startInCommingMessages();
	}
	
	public void startInCommingMessages() {
		System.out.println("startIncommingMessages invoked");
		sockService.getMessageObserver().subscribe(inMessage -> {
			String inStringMessage;
			MessageSocket inMessageObject;
			
			System.out.println("[*] inCommingMessage "+inMessage);
			
			if(inMessage instanceof String) {
				inStringMessage = (String)inMessage;
				
				if(Commands.exists(inStringMessage)) {
					runCommand(inStringMessage, null);
				}else {
					int r = jokeExecutor.executeJoke(inStringMessage);
					if(r == JokeExecutor.JOKE_NOT_FOUND)
						this.sockService.sendDataPlz(new MessageSocket(MessageSocket.ACTION_JOKE_NOT_FOUND));
					else if(r == JokeExecutor.JOKE_EXECUTED)
						this.sockService.sendDataPlz(new MessageSocket(MessageSocket.ACTION_JOKE_EXECUTED));
				}
				
			}else if(inMessage instanceof MessageSocket) {
				inMessageObject = (MessageSocket)inMessage;
				
				if(inMessageObject.getAction().equals(MessageSocket.ACTION_EXECUTE_COMMAND)) {
					runCommand(inMessageObject.getCommand(), inMessageObject.getParam());
				}else if(inMessageObject.getAction().equals(MessageSocket.ACTION_EXECUTE_JOKE)) {
					if(inMessageObject.getJokeParams() != null && inMessageObject.getJokeParams().size() > 0) {
						jokeExecutor.executeJokeWithParams(inMessageObject.getJokeName(), inMessageObject.getJokeParams());
					}else {
						jokeExecutor.executeJoke(inMessageObject.getJokeName());
					}
				}
			}
		});
	}
	
	/*
	 * return 1: OK command
	 * return 0: Command not found
	 * return -1: Parameter command not found
	 * */
	public int runCommand(String command, String param) {
		System.out.println("[*] runCommand: "+ command);
		int ok = 1, err = 0, pnf = -1;
		
		if(Commands.ECHO.name().equals(command)) {
			if(param == null)
				return pnf;
			
			sockService.sendDataPlz(param);
			
			return ok;
		}else if(Commands.JOKE_RUNNING.name().equals(command)) {
			if(param == null)
				return pnf;
			
			sockService.sendDataPlz(
					new MessageSocket(
							MessageSocket.ACTION_COMMAND_EXECUTED, 
							command, 
							Boolean.toString(jokeExecutor.jokeRunning(param))));
			
			return ok;
		}
		
		return err;
	}
	
}
