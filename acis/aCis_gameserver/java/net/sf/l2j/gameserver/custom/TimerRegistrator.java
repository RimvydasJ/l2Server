package net.sf.l2j.gameserver.custom;
import net.sf.l2j.commons.concurrent.ThreadPool;

import java.util.Calendar;
import java.util.Hashtable;

public class TimerRegistrator implements Runnable
{
	protected static final TimerRegistrator _instance = new TimerRegistrator();
	public static TimerRegistrator getInstance(){return _instance;	}

	public TimerRegistrator()
	{
		ThreadPool.schedule(this,1000);
	}
	
	private Hashtable<String, ChangeFactionZone> changeFactionZoneTask_times = new Hashtable<>();
	
	public void addFactionZoneChangeEvent(String time)
	{
		changeFactionZoneTask_times.put(time, new ChangeFactionZone());
	}

	public void cleanFactionChangeTimes(){
		changeFactionZoneTask_times.clear();
	}
	
	public void checkForVoting()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		String hourStr = ""; String minStr = "";
		if(hour<10)	hourStr = "0"+hour;	else hourStr = ""+hour;		
		if(min<10) minStr = "0"+min; else minStr = ""+min;		
		String currentTime = hourStr+":"+minStr;
		ChangeFactionZone task = changeFactionZoneTask_times.get(currentTime);
		if(task != null)
			ThreadPool.schedule(task,1000);
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		while(true)		{
			checkForVoting();
			try
			{
				Thread.sleep(60000); //1 minute
			}
			catch(Exception e){}
		}
	}
	
	private class ChangeFactionZone implements Runnable
	{
		@Override
		public void run() 
		{
			FactionZoneManager.getInstance().changeFactionZoneRandom();
		}		
	}
}

