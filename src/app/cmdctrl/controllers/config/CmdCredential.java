package app.cmdctrl.controllers.config;

public class CmdCredential {

	private String username;
	private String password;
	
	public CmdCredential(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public CmdCredential() {}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "CmdCredential [username=" + username + ", password=" + password + "]";
	}
	
}
