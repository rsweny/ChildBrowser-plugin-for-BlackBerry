package com.motek.blackberry.webworks.extensions;

import org.w3c.dom.Document;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.web.WidgetConfig;
import net.rim.device.api.web.WidgetExtension;
//import net.rim.device.api.system.EventLogger;


/**
 * @author      Ryan Sweny <rsweny@motekmobile.com>
 * @version     1.0
 * @since       2011-10-10
 */

public class ChildBrowserExtension implements WidgetExtension {

	public String[] getFeatureList() {
		String[] result = new String[1];
		result[0] = "motek";
		return result;
	}

	public void loadFeature(String feature, String version, Document doc, ScriptEngine scriptEngine) throws Exception
	{
		if (feature == "motek") {
			scriptEngine.addExtension("motek.childbrowser", new ChildBrowserNamespace());
			//EventLogger.register(ChildBrowserFunction.GUUID_STRING, "childbrowser",EventLogger.VIEWER_STRING);
			//EventLogger.logEvent(ChildBrowserFunction.GUUID_STRING,"added childbrowser extension".toString().getBytes());
		}
	}

	public void register(WidgetConfig arg0, BrowserField arg1) {
		// TODO Auto-generated method stub

	}

	public void unloadFeatures(Document arg0) {
		// TODO Auto-generated method stub

	}

}
