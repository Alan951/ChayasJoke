package app.joke;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
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

	private static String jokesPath = System.getProperty("user.dir") + File.separator + "jokes";
	
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
		loadJokesWithJson();
	}
	
	protected void reLoadJokes() {
		loadJokesWithJson();
	}
	
	private boolean validJokeFile(String fileName) {
		return fileName.endsWith(".class") || fileName.endsWith(".jar");
	}
	
	private void loadJokesWithJson() {
		this.jokeList = new HashMap<String, Constructor<? extends JokeBase>>();
		
		JokeLoader.jokesPath = "jokes/";
		
		loadJokePropWrapper();
		
		int jokesLoaded = 0;
		
		for(JokeProperties jokeProp: jpw.getJokeProperties()) {
			String jokeFileName = jokeProp.getFileName();
			
			jokeFileName = JokeLoader.jokesPath + jokeFileName;
			
			System.out.print("[!] Loading \""+ jokeProp.getClassName() +"\"");
			
			if(!new File(jokeFileName).exists()) {
				System.out.println("... Error. The file not exists!");
				
				continue;
			}
			
			File jokeFile = new File(jokeFileName);
			
			String className = jokeProp.getClassName();
			
			Pair<String, Constructor<? extends JokeBase>> jokePair = loadJoke(jokeFile, className);
			
			if(jokePair.getValue() == null) {
				System.out.println("[!] Joke file \""+ jokeFileName +"\" error");
				continue;
			}
				
			//System.out.println("[*] Joke Loaded: \""+ jokeFileName +"\"");
			//this.jokeList.add(joke);
			this.jokeList.put(jokePair.getKey(), jokePair.getValue());
			
			jokesLoaded++;
			
			System.out.println("... OK!");
		}
	}
	
	private void loadJokes() {
		this.jokeList = new HashMap<String, Constructor<? extends JokeBase>>();
		
		JokeLoader.jokesPath = "jokes/";
		
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_DEBUG) {
			System.out.println("[*] JokesPath = " + new File(JokeLoader.jokesPath).getAbsolutePath());
		}
		
		loadJokePropWrapper();
		int jokesLoaded = 0;
		
		if(jokesPath == null) {
			//return -1;
		}
		
		File dirJokes = new File(JokeLoader.jokesPath);
		
		if(!dirJokes.exists()) {
			System.out.println("[*] Joke External Path doesn't exists");
			if(dirJokes.mkdirs()) {
				System.out.println("[*] Joke External Folder created");
			}else {
				System.out.println("[!] Can't create Joke External Folder");
			}
		}
		
		for(File jokeFile : dirJokes.listFiles()) {
			if(jokeFile.isFile()) {
				if(jokeFile.getName().equals(JokeLoader.FILE_NAME_JOKE_CONFIG) || !validJokeFile(jokeFile.getName())) {
					continue;
				}
				
				String className = jpw.getClassNameOfJoke(jokeFile.getName());
				//String className = jokeFile.getName();
				
				if(className == null) {
					//continue;
				}
					
				
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
		
		System.out.println("[*] Jokes: "+jokesLoaded);		
	}
	
	public void setJokesPath(String path) {
		JokeLoader.jokesPath = path;
	}
	
	public String getJokesPath() {
		return JokeLoader.jokesPath;
	}
	
	public boolean loadJokePropWrapper() {
		File file = new File(jokesPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG);
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
		
		try(Reader reader = new FileReader(JokeLoader.jokesPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG)){
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
		
		//Lista de jokes predeterminados
		JokeProperties jp1 = new JokeProperties("CervezaJoke.class", "jokes.CervezaJoke");
		JokeProperties jp2 = new JokeProperties("MouseJoke.class", "jokes.MouseJoke");
		JokeProperties jp3 = new JokeProperties("RansomJoke.class", "jokes.RansomJoke");
		
		jpw.getJokeProperties().add(jp1);
		jpw.getJokeProperties().add(jp2);
		jpw.getJokeProperties().add(jp3);
		
		try (Writer writer = new FileWriter(JokeLoader.jokesPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG)) {
		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    gson.toJson(jpw, writer);
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
		
		if(GlobalOpts.verboseLevel >= Verbosity.VERBOSE_NORMAL)
			System.out.println("[*] Create file config created example at: " + JokeLoader.jokesPath + File.separator + JokeLoader.FILE_NAME_JOKE_CONFIG);
		
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
			
			Class<? extends JokeBase> jokeClass = (Class<? extends JokeBase>)loader.loadClass(className);
			Constructor<? extends JokeBase> constructor = jokeClass.getConstructor();
			
			return new Pair<String, Constructor<? extends JokeBase>>(constructor.newInstance().command(), constructor);
		}catch(Exception e) {
			System.out.println("[*] Error al intentar cargar " + file.getName() + ": " + e.getMessage());
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
