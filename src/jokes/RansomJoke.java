package jokes;

import java.util.Map;

import app.joke.Joke;
import app.joke.JokeBase;

public class RansomJoke extends JokeBase implements Joke{

	@Override
	public String getJokeName() {
		return "Ransom Joke";
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String command() {
		return "RANSOM_JOKE";
	}

	@Override
	public String run() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParams(Map<String, String> param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
