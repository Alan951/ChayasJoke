package jokes;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Map;

import app.joke.Joke;
import app.joke.JokeBase;

public class MouseJoke extends JokeBase implements Joke{

	private String result;
	private Robot bot;
	
	public String getJokeName() {
		return "Mouse Joke";
	}

	@Override
	public String command() {
		return "MOUSE_JOKE";
	}
	
	private boolean tUp = true;
	private boolean jokeUp = true;

	@Override
	public String run() {
		
		
		try {
			bot = new Robot();
		}catch(AWTException e) {
			e.printStackTrace();
			result = "ERROR: "+e.getMessage();
			return result;
		}
		
		new Thread(() -> {
			int times = 0;
			
			jokeUp = true;
			
			while(jokeUp){
				times++;
				
				Thread t = new Thread(() -> {
					try {
						tUp = true;
						pauseMouse();
						Thread.sleep(1000 * 5);
						
						tUp = false;
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
					
				});
				
				
				try {
					t.start();
					Thread.sleep(1000 * 15);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
				
				if(times >= 10) {
					jokeUp = false;
				}
			}
			
			
			
		}).start();
		
		
		
		this.result = "Joke Executed";
		
		return result;
	}
	
	private void pauseMouse() {
		System.out.println("[*] Mouse Pause invoked");
		
		Point p = MouseInfo.getPointerInfo().getLocation();
		new Thread(() -> {
			while(tUp) {
				bot.mouseMove((int)p.getX(), (int)p.getY());
			}
		}).start();
		
	}
	
	private void resumeMouse() {
		tUp = false;
	}
	
	public String toString() {
		return getJokeName();
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

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return null;
	}
}
