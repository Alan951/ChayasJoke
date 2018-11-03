package app.joke.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import app.joke.JokeLoader;

public class JokePropsWrapper {

	@SerializedName("Jokes") 
	private List<JokeProperties> jokeProperties;
	
	public JokePropsWrapper() {
		this.jokeProperties = new ArrayList<JokeProperties>();
	}

	public List<JokeProperties> getJokeProperties() {
		return jokeProperties;
	}

	public void setJokeProperties(List<JokeProperties> jokeProperties) {
		this.jokeProperties = jokeProperties;
	}
	
	public String getClassNameOfJoke(String fileName) {
		Optional<JokeProperties> jP = this.jokeProperties.stream().filter(jokeProp -> jokeProp.getFileName().equalsIgnoreCase(fileName)).findFirst();
		
		if(jP.isPresent()) {
			return jP.get().getClassName();
		}
		
		if(fileName.endsWith(".class")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		
		return JokeLoader.DEFAULT_CLASSNAME;
	}
	
}
