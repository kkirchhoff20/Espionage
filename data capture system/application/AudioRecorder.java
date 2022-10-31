package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder implements Runnable
{
    // record duration, in milliseconds
    final long RECORD_TIME = 20000;  // 20 seconds
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.AU;
 
    // the line from which audio data is captured
    TargetDataLine line;
    
    AudioInputStream ais;
    
    ByteArrayOutputStream audioOutput;
    
    Path path;
    
    public AudioRecorder(Path p)
    {
    	this.path = p;
    }
    
    
    /**
     * Defines an audio format
     * testing new modifications
     */
    AudioFormat getAudioFormat() 
    {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
    public void setUp()
    {
        AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) 
        {
            System.out.println("Line not supported");
            System.exit(0);
        }
        try
        {
        	line = (TargetDataLine) AudioSystem.getLine(info);
        	line.open(format);
        }
        catch (LineUnavailableException ex) 
        {
            ex.printStackTrace();
        }
    }
    /**
     * Captures the sound and record into a WAV file
     */
    void startRecording() 
    {
        line.start();   // start capturing
        System.out.println("Line Buffer Size: " + line.getBufferSize());
        System.out.println("Start capturing...");
 
        ais = new AudioInputStream(line);
        audioOutput = new ByteArrayOutputStream();
    	try
    	{
            AudioSystem.write(ais, fileType, audioOutput);
    	}
    	catch (IOException ioe) 
    	{
            ioe.printStackTrace();
        }

        System.out.println("Start recording...");
        //writeToFile();
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() 
    {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
 
    void writeToFile()
    {
    	
    	
    	//System.out.println(byte_array.length);
    	
        try(OutputStream outputStream = new FileOutputStream(path.toString())) 
        {
            audioOutput.writeTo(outputStream);
            
            System.out.print("Wrote to " + path.toString());
    	}
    	catch (IOException ioe) 
    	{
            ioe.printStackTrace();
        }
        
    }
    
    public void writeToWav()
    {
    	byte[] byte_array = audioOutput.toByteArray();
        AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(byte_array), 
                getAudioFormat(), 
                byte_array.length
            );
        File wavFile = new File(path.toString());
        AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
       try
       {
    	   AudioSystem.write(stream, fileType, wavFile);
       }
       catch(IOException ex)
       {
    	   ex.printStackTrace();
       }
    }


	@Override
	public void run() 
	{
		System.out.println("pizza on the run");
		this.startRecording();
	}
}