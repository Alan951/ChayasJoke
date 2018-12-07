package app.cmdctrl.controllers.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CmdConfigLoader {

	public static CmdConfigLoader instance;
	
	private String cmdServConfigPath;
	
	private CmdServConfig cmdServConfig;
	
	private CmdConfigLoader() {
		cmdServConfigPath = System.getProperty("user.dir") + File.separator + "cmdConfig.json";
	}
	
	public static CmdConfigLoader getInstance() {
		if(instance == null)
			instance = new CmdConfigLoader();
		
		return instance;
	}
	
	public CmdConfigLoader loadCmdServConfig() throws IOException {
		if(new File(this.cmdServConfigPath).exists()) {
			try(Reader reader = new FileReader(this.cmdServConfigPath)){
				Gson gson = new GsonBuilder().create();
				this.cmdServConfig = gson.fromJson(reader, CmdServConfig.class);
				System.out.println("Archivo CmdServConf cargado ["+this.cmdServConfigPath+"]");
			}
		}else {
			this.cmdServConfig = new CmdServConfig();
		}
		
		return this;
	}
	
	public CmdServConfig loadAndGetCmdServConfig() throws IOException {
		if(this.cmdServConfig == null) {
			loadCmdServConfig();
		}
		
		return this.cmdServConfig;
	}
	
	public CmdServConfig getCmdServConfig() {
		return this.cmdServConfig;
	}
	
	public void saveCmdServConfig(CmdServConfig config) throws IOException {	
		if(config == null)
			config = this.cmdServConfig;
		
		try(Writer writer = new FileWriter(this.cmdServConfigPath)){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			gson.toJson(config, writer);
			System.out.println("Archivo CmdServConf guardado");
		}
	}
	
}
