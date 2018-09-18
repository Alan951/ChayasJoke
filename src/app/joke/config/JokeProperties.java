package app.joke.config;

public class JokeProperties {
	private String fileName;
	private String className;
	
	public JokeProperties(String fileName, String className) {
		this.fileName = fileName;
		this.className = className;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
}
