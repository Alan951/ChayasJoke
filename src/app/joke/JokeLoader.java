package app.joke;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.GlobalOpts;
import app.config.Verbosity;
import app.joke.config.JokeProperties;
import app.joke.config.JokePropsWrapper;
import javafx.util.Pair;

public class JokeLoader {

	private static String jokeExternalPath = System.getProperty("user.dir") + File.separator + "jokes";
	private static String jokeInternalPath;
	
	public final static String FILE_NAME_JOKE_CONFIG = "config.json";
	public final static String DEFAULT_CLASSNAME = "joke";
	
	private Map<String, Constructor<? extends JokeBase>> jokeList;
	
	private JokePropsWrapper jpw;
	
	private static JokeLoader instance;
	
	public static JokeLoader getInstance() {
		if(instance == null) {
			instance = new JokeLoader();
		}
		
		return instance;
	}
	
	private JokeLoader() {
		loadJokes();
	}
	
	protected void reLoadJokes() {
		loadJokes();
	}
	
	private void loadJokes() {
		this.jokeList = new HashMap<String, Constructor<? extends JokeBase>>();
		
		try {
			JokeLoader.jokeInternalPath = getClass().getResource("/jokes").toURI().toURL().getPath();
			loadJokePropWrapper();
			loadInernalJokes();
			loadExternalJokes();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void setJokeExternalPath(String path) {
		JokeLoader.jokeExternalPath = path;
	}
	
	public String getJokeInternalPath() {
		return JokeLoader.jokeExternalPath;
	}
	
	public int loadInernalJokes() {
		File file = new File(jokeInternalPath);
		int jokesLoaded = 0;
		
		if(!(file.exists() && file.isDirectory())) {
			System.out.println("[!] Internal Joke Path doesn't exists. "+ file.getAbsolutePath());
			return -1;
		}
		
		for(File jokeFile : file.listFiles()) {
			if(jokeFile.isFile()) {
				Pair<String, Constructor<? extends JokeBase>> jokePair = loadJoke(jokeFile, "jokes."+jokeFile.getName().replaceFirst("[.][^.]+$", ""));
				
				if(jokePair.getValue() == null) {
					System.out.println("[!] Joke file \""+ jokeFile.getName() +"\".");
					continue;
				}
				
				System.out.println("[*] Joke Loaded: \""+ jokeFile.getName() +"\"");
				this.jokeList.put(jokePair.getKey(), jokePair.getValue());	
				
				jokesLoaded++;
			}
		}
		
		System.out.println("[*] Internal Jokes loaded: "+jokesLoaded);
		
		return jokesLoaded;
	}
	
	public int loadExternalJokes() {
		int jokesLoaded = 0;
		
		if(jokeExternalPath == null) {
			return -1;
		}
		
		File dirJokes = new File(JokeLoader.jokeExternalPath);
		
		if(!dirJokes.exists()) {
			System.out.println("[*] Joke External Path doesn't exists");
			if(dirJokes.mkdirs()) {
				System.out.println("[*] Joke External Folder created");
			}else {
				System.out.println("[!] Can't create Joke External Folder");
				return -1;
			}
		}
		
		for(File jokeFile : dirJokes.listFiles()) {
			if(jokeFile.isFile()) {
				if(jokeFile.getName().equals(JokeLoader.FILE_NAME_JOKE_CONFIG)) {
					continue;
				}
				
				String className = jpw.getClassNameOfJoke(jokeFile.getName());
				
				if(className == null)
					continue;
				
				Pair<String, Constructor<? extends JokeBase>> jokePair = loadJoke(jokeFile, className);
				
				if(jokePair.getValue() == null) {
					System.out.println("[!] Joke file \""+ jokeFile.getName() +"\" error");
					continue;
				}
					
				System.out.println("[*] Joke Loaded: \""+ jokeFile.getName() +"\"");
				//this.jokeList.add(joke);
				this.jokeList.put(jokePair.getKey(), jokePair.getValue());
				
				jokesLoaded++;
			}
		}
		
		System.out.println("[*] External Jokes loaded: "+jokesLoaded);
		
		return jokesLoaded;
	}
	
	public boolean loadJokePropWrapper() {
		File file = new File(jokeExternalPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG);
		if(!file.exists()) {
			System.out.println("[*] Config jokes file doesn't exists");
			
			try {
				if(!file.getParentFile().exists()) {
					System.out.println(file.getParentFile());
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				
				if(!createExampleConfigProperties()) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		try(Reader reader = new FileReader(JokeLoader.jokeExternalPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG)){
			Gson gson = new GsonBuilder().create();
			this.jpw = gson.fromJson(reader, JokePropsWrapper.class);
		}catch(Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	private boolean createExampleConfigProperties() {
		JokePropsWrapper jpw = new JokePropsWrapper();
		
		JokeProperties jp1 = new JokeProperties("JokeExample1", "app.joke");
		JokeProperties jp2 = new JokeProperties("JokeExample2", "module.joke");
		JokeProperties jp3 = new JokeProperties("JokeExample3", "main_package");
		
		jpw.getJokeProperties().add(jp1);
		jpw.getJokeProperties().add(jp2);
		jpw.getJokeProperties().add(jp3);
		
		try (Writer writer = new FileWriter(JokeLoader.jokeExternalPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG)) {
		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    gson.toJson(jpw, writer);
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
		
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
			System.out.println("[*] Create file config created example at: " + JokeLoader.jokeExternalPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG);
		
		return true;
	}
	
	@SuppressWarnings("unused")
	private Pair<String, Constructor<? extends JokeBase>> loadJoke(String path) {
		File file = new File(path);
		
		if(file.exists() && file.isFile()) {
			return loadJoke(file, "jokes");
		}
		
		return null;
	}
	
	private Pair<String, Constructor<? extends JokeBase>> loadJoke(File file, String className) {
		if(!(file.getName().endsWith(".class") || file.getName().endsWith(".jar"))) {
			return null;
		}
		
		try {
			ClassLoader loader = URLClassLoader.newInstance(
					new URL[] { file.toURI().toURL() }, getClass().getClassLoader());
			
			Class jokeClass = loader.loadClass(className);
			Constructor<? extends JokeBase> constructor = jokeClass.getConstructor();
			
			return new Pair<String, Constructor<? extends JokeBase>>(constructor.newInstance().command(), constructor);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Map<String, Constructor<? extends JokeBase>> getJokeConsutrctorList(){
		return this.jokeList;
	}
	
	public List<String> getJokeListNames() {
		return new ArrayList<>(this.jokeList.keySet());
	}

}
