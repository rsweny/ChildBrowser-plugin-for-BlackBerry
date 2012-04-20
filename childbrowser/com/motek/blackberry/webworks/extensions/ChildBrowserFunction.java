package com.motek.blackberry.webworks.extensions;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

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
		EventLogger.register(GUUID_STRING, "BGifteeWW",EventLogger.VIEWER_STRING);
		EventLogger.register(GUUID_EXCEPTION, "BGifteeWWE",EventLogger.VIEWER_EXCEPTION);
		try 
		{
			if (args.length == 1)
			{
				//this is the log call
				String msg = (String)args[0];
				System.out.println(msg);
				EventLogger.logEvent(GUUID_STRING,msg.getBytes());
			}
			else if (args.length == 3)
			{
				//this is the http call
				String url = (String)args[0];
				EventLogger.logEvent(GUUID_STRING,new String("childbrowser http to: " +url).getBytes());
				String postData = (String)args[1];
				ScriptableFunction callback = (ScriptableFunction)args[2];
				doRequest(url, postData, callback, obj);
			}
			else
			{
				//this is the open call
				synchronized(Application.getEventLock())
				{
					String url = (String)args[0];
					ScriptableFunction callback = (ScriptableFunction)args[1];
					EventLogger.logEvent(GUUID_STRING,url.toString().getBytes());
					browserScreen = new BrowserScreen(url, callback, obj);
					UiApplication.getUiApplication().pushScreen(browserScreen);
				}
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
	
	
		/**
	* Takes in a XML request and returns a XMLNode hierarchy of the response. If a listener and listElementName is
	* present, objects from the response will be streamed to the listener on the fly.
	*
	* @param reqXML the full XML request as a String
	*/

	public void doRequest(String url, String reqXML, ScriptableFunction callback, Object thiz)
	{
		HttpConnection connection = null;
		InputStream is = null;
		try
		{
			url += ";deviceside=false;ConnectionType=mds-public;ConnectionTimeout=20000";
			System.out.println("doRequest:" + url);
			if (reqXML != null && reqXML.length() > 0)
			{
				// data to POST
				System.out.println("request XML:" + reqXML);
				byte[] requestData = reqXML.getBytes("UTF-8");
				connection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", "" + requestData.length);
				connection.setRequestProperty("User-Agent", "Cabins Reader");
				connection.setRequestMethod(HttpConnection.POST);
				OutputStream os = connection.openOutputStream();
				os.write(requestData, 0, requestData.length);
				os.close();
			}
			else
			{
				// simple GET
				connection = (HttpConnection) Connector.open(url);
				connection.setRequestMethod(HttpConnection.GET);
			}

			is = connection.openInputStream();
			String content_length = connection.getHeaderField("Content-Length");
			EventLogger.logEvent( GUUID_STRING, new String("childbrowser response: " + connection.getResponseCode()).getBytes() );
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[256];

			while (true)
			{
				int rd = is.read(buf, 0, 256);
				if (rd == -1)
				break;
				bos.write(buf, 0, rd);
			}

			buf = bos.toByteArray();
			String responseDump = new String(buf, "UTF-8");
			is.close();
			
			//return String to javascript
			Object[] args = new Object[ 1 ];
			args[ 0 ] = responseDump;
			callback.invoke( thiz, args );
		}
		catch(Exception e)
		{
			//return String to javascript
			try
			{
				String responseDump = e.toString();
				Object[] args = new Object[ 1 ];
				args[ 0 ] = responseDump;
				callback.invoke( thiz, args );
			}
			catch(Exception ignore)
			{
			}
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (Throwable e)
				{
				}
				is = null;
			}

			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (Throwable e)
				{
				}
				connection = null;
			}
		}
	}
	
	
	
	
	
}
