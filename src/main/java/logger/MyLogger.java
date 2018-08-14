package logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
	
	public void testlog() 
	{
		Logger logger = Logger.getLogger("Logger très simple");
		logger.log(Level.INFO, "Test de logger");
	}
}
