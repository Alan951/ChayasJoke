package app.joke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jalan.cksock.SockService;

public class JokeExecutor {
	
	private SockService sockService;
	
	public static final int JOKE_EXECUTED = 1;
	public static final int JOKE_ERROR = 0; //TODO: implement catching error when attempting execute
	public static final int JOKE_NOT_FOUND = -1;
	
	private List<JokeThreadWrapper> executingJokes;
	
	public JokeExecutor(SockService sockService) {
		this.sockService = sockService;
		
		executingJokes = new ArrayList<>();
	}
	
	public int executeJoke(String jokeName) {
		JokeBase joke = JokeFactory.getJoke(jokeName);
		
		if(joke != null) {
			joke.setSockService(sockService);
			
			executeJoke(joke);
			return JOKE_EXECUTED;
		}else { //joke not found
			return JOKE_NOT_FOUND;
		}
	}
	
	public void executeJokeWithParams(String jokeName, Map<String, String> jokeParams) {
		JokeBase joke = JokeFactory.getJoke(jokeName);
		
		if(joke != null) {
			joke.setParams(jokeParams);
			
			executeJoke(joke);
		}
	}
	
	public void executeJoke(JokeBase joke) {
		System.out.println("[*] Execute joke: "+ joke.getJokeName());
		
		Thread jokeThread = new Thread(() -> {
			String result = joke.run();
			
			if(result != null) {
				this.sockService.sendDataPlz(result);
			}
			
			this.removeExecutingJokeByJoke(joke);
		});
		
		this.executingJokes.add(new JokeThreadWrapper(joke, jokeThread));
		
		jokeThread.start();
	}
	
	public boolean stopJoke(String jokeName) {
		Optional<JokeThreadWrapper> jtwrapper = this.executingJokes.stream()
				.filter((jtw) -> jtw.getJoke().getJokeName().equals(jokeName))
				.findFirst();
		
		if(jtwrapper.isPresent()) {
			jtwrapper.get().getThread().interrupt(); //stop thread
			removeExecutingJokeByJoke(jtwrapper.get().getJoke()); //remove joke from list of joke's running
			return true;
		}else {
			return false;
		}
	}
	
	private boolean removeExecutingJokeByJoke(JokeBase joke) {
		return this.executingJokes.removeIf((jtw) -> jtw.getJoke().equals(joke));
	}
	
	public boolean jokeRunning(String jokeName) {
		return this.executingJokes.stream()
				.filter((jtw) -> jtw.getJoke().getJokeName().equals(jokeName))
				.findFirst().isPresent();
	}
	
	
	
}
