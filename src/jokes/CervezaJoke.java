package jokes;

import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.FloatControl;

import app.joke.Joke;
import app.joke.JokeBase;

public class CervezaJoke extends JokeBase implements Joke {
	
	private Map<String, String> param;

	public String getJokeName() {
		return "Cerveza Joke";
	}

	@Override
	public String command() {
		return "CERVEZA_JOKE";
	}

	@Override
	public String run() {
		try {
			Runtime.getRuntime().exec("./nircmdc.exe setsysvolume 100000");
			Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe https://youtu.be/DuoCd7UEkpc?t=33");
		}catch(IOException e) {
			e.printStackTrace();
			
			return "Error al abrir. Mesaje de error: " + e.getMessage();
		}
		
		return "Ventana abierta!";
		
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
		return "Esta broma ejecuta nircmdc.exe para aumentar el volumen de la computadora victima\n"
				+ "y ejecuta chrome con el path \"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe\"\n"
				+ "enviando por parametro la siguiente url: \"https://youtu.be/DuoCd7UEkpc?t=33\"\n"
				+ "el cual es la dirección del video de \"Cerveza Cerveza de Wendy Sulca\"";
	}

	@Override
	public String toString() {
		return "CervezaJoke [param=" + param + ", getJokeName()=" + getJokeName() + ", command()=" + command()
				+ ", help()=" + help() + "]";
	}

}
