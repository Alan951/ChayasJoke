package app.cmdctrl.controllers.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CmdServConfig implements Serializable{

	private String serverAlias;
	private List<CmdCredential> users;
	private boolean requiredAuthRemoteClient;
	
	public CmdServConfig() {}
	
	public CmdServConfig(String serverAlias, List<CmdCredential> users, boolean requiredAuthRemoteClient) {
		this.serverAlias = serverAlias;
		this.users = users;
		this.requiredAuthRemoteClient = requiredAuthRemoteClient;
	}

	public String getServerAlias() {
		return serverAlias;
	}
	
	public void setServerAlias(String serverAlias) {
		this.serverAlias = serverAlias;
	}
	
	public List<CmdCredential> getUsers() {
		if(this.users == null) {
			this.users = new ArrayList<CmdCredential>();
		}
		
		return users;
	}
	
	public void setUsers(List<CmdCredential> users) {
		this.users = users;
	}
	
	public boolean isRequiredAuthRemoteClient() {
		return requiredAuthRemoteClient;
	}
	
	public void setRequiredAuthRemoteClient(boolean requiredAuthRemoteClient) {
		this.requiredAuthRemoteClient = requiredAuthRemoteClient;
	}
	
	
	
}
