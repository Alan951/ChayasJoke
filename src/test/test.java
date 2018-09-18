package test;

import java.net.URISyntaxException;

import app.joke.Joke;
import app.joke.JokeBase;
import app.joke.JokeFactory;
import app.joke.JokeLoader;

public class test {
	
	public static void main(String []args) throws URISyntaxException {
		Joke joke = JokeFactory.getJoke("CERVEZA_JOKE");
		
		System.out.println(joke);
		JokeBase jokeBase = (JokeBase)joke;
		System.out.println(jokeBase);
		System.out.println(jokeBase.getSockService());
		
		/*JokeLoader loader = JokeLoader.getInstance();
		
		//loader.loadJokePropWrapper();
		loader.loadInernalJokes();
		loader.loadExternalJokes();*/
	}
}
