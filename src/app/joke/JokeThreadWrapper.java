package app.joke;

public class JokeThreadWrapper {

	private JokeBase joke;
	private Thread thread;
	
	public JokeThreadWrapper(JokeBase joke, Thread thread) {
		super();
		this.joke = joke;
		this.thread = thread;
	}
	
	public JokeBase getJoke() {
		return joke;
	}
	
	public void setJoke(JokeBase joke) {
		this.joke = joke;
	}
	
	public Thread getThread() {
		return thread;
	}
	
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
	
	
}
