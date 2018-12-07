package app;

import java.util.Scanner;

public class ScannerService {

	private static ScannerService instance;
	
	private Scanner sc;
	
	private ScannerService() {
		sc = new Scanner(System.in);
	}
	
	public static ScannerService getInstance() {
		if(instance == null)
			instance = new ScannerService();
		
		return instance;
	}
	
	public Scanner getScanner() {
		return this.sc;
	}
	
}
