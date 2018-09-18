package app.joke;

import java.util.Map;

public interface Joke {
	
	public String getJokeName();
	
	public String help();
	
	public String command();
	
	public String run();
	
	public void setParams(Map<String, String> param);
	
	public Map<String, String> getParams();
	
}
