package socket;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.BindException;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.Test;

import com.jalan.cksock.SockConfig;
import com.jalan.cksock.SockLogger;
import com.jalan.cksock.SockLoggerConfig;
import com.jalan.cksock.SockServerService;
import com.jalan.cksock.SockService;

class BasicTest {

	Logger logger = Logger.getLogger(BasicTest.class);
	
	@Test
	void test() throws InterruptedException, IOException{
		SockLogger.autoConfigure();
		
		SockConfig sockConfigServer = new SockConfig(951);
		SockConfig sockConfigClient = new SockConfig("localhost", 951, false, -1, true);
		
		SockServerService sockServer = new SockServerService(sockConfigServer);
		SockLoggerConfig.setCustomPattern(sockServer.getLogger(),  " [SERVER] " + PatternLayout.TTCC_CONVERSION_PATTERN);
		
		try {
			sockServer.listen();
		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread.sleep(1500);
		
		SockService sockService = new SockService(sockConfigClient);
		SockLoggerConfig.setCustomPattern(sockService.getLogger(),  " [CLIENT] " + PatternLayout.TTCC_CONVERSION_PATTERN);
		
		Thread.sleep(1000);
		
		sockService.getMessageObserver().subscribe((Object message) -> {
			
		});
		
		Thread.sleep(1000);
		
		sockService.sendDataPlz("Hola prro");
		
		Thread.sleep(1000);
		
		sockServer.sendData("K tal prro", 1L);
		
		Thread.sleep(3500);
		
	}

}
