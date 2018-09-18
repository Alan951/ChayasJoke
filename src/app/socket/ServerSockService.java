package app.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerSockService {
	private ServerSocket serverSock;
	
	private List<SockService> clientSocks;
	
	private boolean flagInComConn;
	
	public ServerSockService() {
		clientSocks = new ArrayList<SockService>();
		
	}
	
	public List<SockService> getClients() {
		return this.clientSocks;
	}
	
	public boolean startInComingConnections() {
		System.out.println("[*] Server startInComingConnections");
		
		try {
			this.flagInComConn = true;
			serverSock = new ServerSocket(4465);
			
			new Thread(() -> {
				long idAI = 0;
				
				while(this.flagInComConn) {
					idAI++;
					
					try {
						Socket socket = serverSock.accept();
						
						SockService sockService = new SockService();
						sockService.setSocket(socket);
						sockService.setId(idAI);
						
						sockService.getMessageObserver().subscribe((msg) -> {
							System.out.println("[*] NewMessage from ["+sockService.getId()+"]: "+msg);
						});
						
						sockService.getConnectionObserver()
							.filter((evt) -> evt.status.equals(SockService.DISCONNECTED_STATUS))
							.subscribe((evt) -> {
								
							System.out.println("[Disconnected]");
							this.clientSocks.remove(evt.service);
						});
					
						this.clientSocks.add(sockService);
						
						System.out.println("[*] Server newConnectionAdded");
						
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}catch(IOException e) {
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}

	public void closeAll() {
		this.clientSocks.forEach((client) -> {
			try {
				client.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void close(long id) {
		Optional<SockService> sockClient = this.clientSocks.stream().filter(client -> client.getId() == id).findFirst();
		if(sockClient.isPresent()) {
			try {
				sockClient.get().close();
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public boolean sendAll(Object data) {
		for(SockService client : this.clientSocks) {
			sendData(data, client.getId());
		}
		
		return true;
	}
	
	public boolean sendData(Object data, long id) {
		Optional<SockService> sockFiltered = this.clientSocks.stream()
				.filter((client) -> client.getId() == id)
				.findFirst();
		
		if(sockFiltered.isPresent()) {
			sockFiltered.get().sendDataPlz(data);
			return true;
		}else {
			return false;
		}
	}
}
