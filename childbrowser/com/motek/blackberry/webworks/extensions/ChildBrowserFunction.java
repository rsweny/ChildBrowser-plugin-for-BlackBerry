package com.motek.blackberry.webworks.extensions;

import java.io.IOException;

import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.script.ScriptableFunction;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;

import blackberry.core.ScriptableFunctionBase;
import blackberry.core.FunctionSignature;

public class ChildBrowserFunction extends ScriptableFunctionBase
{
	public static final long GUUID_STRING =  199648737L;
	public static final long GUUID_EXCEPTION = 199648738L;
	
	public BrowserScreen browserScreen;
	
	public Object invoke(Object obj, Object[] args) throws Exception
	{
		EventLogger.register(GUUID_STRING, "childbrowser",EventLogger.VIEWER_STRING);
		EventLogger.register(GUUID_EXCEPTION, "childbrowserE",EventLogger.VIEWER_EXCEPTION);
		if (obj != null) EventLogger.logEvent(GUUID_STRING,obj.toString().getBytes());

		try 
		{
			synchronized(Application.getEventLock())
			{
				String url = (String)args[0];
				ScriptableFunction callback = (ScriptableFunction)args[1];
				EventLogger.logEvent(GUUID_STRING,url.toString().getBytes());
				browserScreen = new BrowserScreen(url, callback, obj);
				UiApplication.getUiApplication().pushScreen(browserScreen);
			}
		} 
		catch (Exception ex) 
		{
			EventLogger.logEvent(GUUID_STRING,new String("childbrowser Error: "+ex.getMessage()+" "+ex.toString()).getBytes());
			EventLogger.logEvent(GUUID_EXCEPTION, new String("childbrowser err: "+ex.toString()).getBytes());
			throw ex;
		}
		return UNDEFINED;
	}
	
	protected Object execute( Object thiz, Object[] args ) throws Exception
	{
		return invoke(thiz, args);
	}
	
	public String getLocation()
	{
		return browserScreen.browserfield.getDocumentUrl();
	}
	
	
	/**
	* @see blackberry.core.ScriptableFunctionBase#getFunctionSignatures
	*/
    protected FunctionSignature[] getFunctionSignatures() {
        FunctionSignature fs = new FunctionSignature(2);
        fs.addParam( String.class, true );
        fs.addParam( ScriptableFunction.class, true );
        return new FunctionSignature[] { fs };
    }
	
	
}
