package application;

public class LogEntry 
{
	private String key;
	private String event;
	private long timeStamp;
	
	public LogEntry(String k, String e, long v)
	{
		key = k;
		event = e;
		timeStamp = v;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
	
	public String getEvent()
	{
		return event;
	}
}
