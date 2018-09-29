package app.cmdctrl;

/*
 * Comandos que pueden ser enviados a la computadora victima
 * 
 * */
public enum Commands {
	ECHO, HOSTNAME, JOKE_RUNNING, JOKE_STOP;
	
	public static boolean exists(String command) {
		for(Commands cmd : values()) {
			if(cmd.name().equalsIgnoreCase(command)) {
				return true;
			}
		}
		
		return false;
	}
}
