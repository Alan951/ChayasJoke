package app.joke;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JokeFactory {
	
	public static JokeBase getJoke(String jokeName) {		
		JokeLoader jokeLoader = JokeLoader.getInstance();
		
		Constructor<? extends JokeBase> jokeConstruct = jokeLoader.getJokeConsutrctorList().get(jokeName);
		
		if(jokeConstruct != null)
			try {
				JokeBase joke = jokeConstruct.newInstance();				
				
				return joke;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return null;
	}
	
	public static boolean exists(String joke) {
		return JokeLoader.getInstance().getJokeConsutrctorList().keySet().contains(joke);
	}
	
}
