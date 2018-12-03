package app.joke;

import java.util.Map;

import com.jalan.cksock.SockService;

public class JokeBase implements Joke{
	
	protected SockService sockService;
	protected Map<String, String> params;
	
	@Override
	public String getJokeName() {
		return "Unnamed";
	}

	@Override
	public String help() {
		return null;
	}

	@Override
	public String command() {
		return null;
	}

	@Override
	public String run() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public Map<String, String> getParams() {
		return this.params;
	}
	
	public void setSockService(SockService sockService) {
		this.sockService = sockService;
	}
	
	public SockService getSockService() {
		return this.sockService;
	}

}
