package application;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;

public class SampleController 
{
	
	@FXML TextArea textArea1 = new TextArea();
	@FXML Button btNew = new Button();
	@FXML TextArea textArea2 = new TextArea();
	@FXML Button btBegin = new Button();
	private PauseTransition pauseTransition;
	String url = "https://en.wikipedia.org/wiki/Special:Random";	
	String baseDir = "C:\\Users\\kbk17\\Espionage-Data\\";
	KeyLogger kl;
	AudioRecorder recorder;
	
	public void btNewClicked()
	{
		System.out.print(textArea1.isWrapText());
		try
		{
			Document doc = Jsoup.connect(url).get();
			Elements paragraphs = doc.getElementsByTag("p");
			Element p = paragraphs.get(0);
			textArea1.setText(p.wholeText());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void btBeginClicked()
	{
		textArea2.setEditable(true);
		//note: this is for logging purposes (not keylogger) for cleaning up spammed error messages
    	KeyLogger.init();
		try 
		{
			GlobalScreen.registerNativeHook(); 
		}
		catch (NativeHookException e)
		{
			e.printStackTrace();
			System.exit(-1); 
		}
	
		long t0 = System.currentTimeMillis();
		/*
		 * Pairs of audio/keylogger transcript will look like:
		 * 	audio_1212022.wav
		 *  transcript_1212022.txt
		 */
		Path transcriptFile = Paths.get(baseDir + "transcript" + t0 + ".txt");
		Path audioFile = Paths.get(baseDir + "audio" + t0 + ".wav");
		kl = new KeyLogger(transcriptFile);
		recorder = new AudioRecorder(audioFile);
		textArea2.setDisable(false);
		textArea2.requestFocus();
		GlobalScreen.addNativeKeyListener(kl);
        Thread recorderThread = new Thread(recorder);
        recorder.setUp();
        recorderThread.start();
		pauseTransition = new PauseTransition(Duration.seconds(15));
	    pauseTransition.setOnFinished(evt -> timeout());
	    pauseTransition.playFromStart();
	}
	
	public void timeout()
	{
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch(NativeHookException ex)
		{
			ex.printStackTrace();
		}
        
		pauseTransition.stop();
		recorder.finish();
        recorder.writeToWav();
		System.out.print("timeout() has run");
		textArea2.clear();
		textArea2.setEditable(false);
		textArea2.setDisable(true);
		kl.writeToFile();
	}
}
