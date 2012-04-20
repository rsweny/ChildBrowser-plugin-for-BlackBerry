package com.motek.blackberry.webworks.extensions;

import net.rim.device.api.script.Scriptable;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Application;

public class ChildBrowserNamespace extends Scriptable 
{
	public static final String FIELD_CHILDBROWSER_OPEN = "open";
	public static final String FIELD_CHILDBROWSER_CLOSE = "close";
	public static final String FIELD_CHILDBROWSER_LOG = "log";
	public static final String HTTP = "http";
	public static final String FIELD_CHILDBROWSER_GETLOCATION = "getLocation";
	private ChildBrowserFunction childBrowser;
	
	public ChildBrowserNamespace() {
		this.childBrowser = new ChildBrowserFunction();
	}
	
	public Object getField(String name) throws Exception
	{
		EventLogger.register(ChildBrowserFunction.GUUID_STRING, "childbrowser",EventLogger.VIEWER_STRING);

		if(name.equals(FIELD_CHILDBROWSER_OPEN))
		{
			return this.childBrowser;
		}
		else if(name.equals(FIELD_CHILDBROWSER_CLOSE)) 
		{
			try
			{
				synchronized(Application.getEventLock()) {
					this.childBrowser.browserScreen.close();
				}
				return new Boolean(true);
			}
			catch(Exception e)
			{
				return new Boolean(false);
			}
		}
		else if(name.equals(FIELD_CHILDBROWSER_LOG)) 
		{
			return this.childBrowser;
		}
		else if(name.equals(HTTP)) 
		{
			return this.childBrowser;
		}
		else if(name.equals(FIELD_CHILDBROWSER_GETLOCATION))
		{
			String loc = this.childBrowser.getLocation();
			EventLogger.logEvent(ChildBrowserFunction.GUUID_STRING,("getLocation "+loc).toString().getBytes());
			return loc;
		}
		
		EventLogger.logEvent(ChildBrowserFunction.GUUID_STRING,(name + " MISSING!").toString().getBytes());
		return super.getField(name);
	}
}



