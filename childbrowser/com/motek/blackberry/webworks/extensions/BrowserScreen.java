package com.motek.blackberry.webworks.extensions;

import org.w3c.dom.Document;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.system.EventLogger;

import blackberry.core.ScriptableFunctionBase;
import net.rim.device.api.script.ScriptableFunction;

import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.browser.field2.BrowserFieldConfig;

public class BrowserScreen extends MainScreen
{
	BrowserField browserfield;
	
	public BrowserScreen(String url, final ScriptableFunction func, final Object thiz)
	{		
		BrowserFieldConfig config = new BrowserFieldConfig();
		config.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_CARET);
		
		String unsabotageUserAgent = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; SCH-I9000 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
		config.setProperty(BrowserFieldConfig.USER_AGENT,unsabotageUserAgent);
		
		browserfield = new BrowserField(config);
	   
		
		BrowserFieldListener listener = new BrowserFieldListener() 
		{
			public void documentLoaded(BrowserField browserField, Document document)
			{
				EventLogger.logEvent(ChildBrowserFunction.GUUID_STRING, (browserfield.getDocumentUrl() + " doc loaded: " + document.toString()).getBytes());
				try
				{
					Object[] args = new Object[ 1 ];
					args[ 0 ] = browserfield.getDocumentUrl();
					func.invoke( thiz, args );
				}
				catch(Exception e)
				{
					EventLogger.logEvent(ChildBrowserFunction.GUUID_STRING, ("doc loaded err: " + e.toString()).getBytes());
				}
			}
		};
		
		browserfield.addListener(listener);
		browserfield.requestContent(url);
		add(browserfield);
	}
} 