package com.phi.cs.timer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class PhiTimerCommons {

	private static final Logger log = Logger.getLogger(PhiTimerCommons.class);
	
	public static String getHostName() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error("Check Network configuraiton and host resolution: hostname can not be found.");
			hostname = "unknowHost";//+(new Random()).nextInt(8000000) + 1000000;
		}
		
		java.util.Properties b =System.getProperties();
		if (b==null)
			return hostname+"_"+"PortsDefaultBindings";
		String portSet = (String)b.get("jboss.service.binding.set");
		if (portSet==null || portSet.isEmpty())
			portSet="PortsDefaultBindings";
		return hostname+"_"+portSet;
	}
	
}
