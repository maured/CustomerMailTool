package logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(MyLogger.class);
	
	public void infoLevel(String str) 
	{
		LOGGER.info(str);
	}

//	public void debugLevel(String str)
//	{
//		LOGGER.debug(str);
//	}

	public void errorLevel(String str)
	{
		LOGGER.error(str);
	}
	
}
