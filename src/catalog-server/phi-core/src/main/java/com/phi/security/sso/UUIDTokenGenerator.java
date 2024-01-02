package com.phi.security.sso;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.safehaus.uuid.EthernetAddress;

/**
 * Creates messages UID for esb messages
 * @author francesco.rossi
 *
 */
public class UUIDTokenGenerator implements TokenGenerator {

	private static final Logger log = Logger.getLogger(UUIDTokenGenerator.class);

	/**
	 * Machine host names
	 */
	private static final EthernetAddress LOCAL_ADDRESS;

	static {
		EthernetAddress address;
		try {
			address = getMacAddress();
		} catch (SocketException e) {
			log.warn("Error while resolving local network address. A dummy address will be used.", e);
			address = org.safehaus.uuid.UUIDGenerator.getInstance().getDummyAddress();
		}

		LOCAL_ADDRESS = address;
		log.debug("MAC address for this server is: "+LOCAL_ADDRESS+". This will be used for time-based UUID generation.");
	}

	@Override
	public String getToken() {
		return UUIDTokenGenerator.newTimeBasedUuid();
	}

	/**
	 * Reads mac address
	 */
	private static EthernetAddress getMacAddress() throws SocketException {
		byte[] mac = null;

		for (Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces(); ifs.hasMoreElements(); ) {
			final NetworkInterface ifc = ifs.nextElement();
			if (ifc.isUp() && !ifc.isLoopback()) {
				log.trace("Found non-local interface: " + ifc.getInetAddresses());
				mac = ifc.getHardwareAddress();
				break;
			}
		}

		if (mac != null && mac.length == 6 ) {
			
			return new EthernetAddress(mac);
		} else {
			EthernetAddress ea = org.safehaus.uuid.UUIDGenerator.getInstance().getDummyAddress();
			log.warn("No non-local interface found, will us a dummy MAC address: " + ea);
			return ea;
		}
	}

	/**
	 * Generate a new time-based UUID. Will use computer's MAC address for UUID creation.
	 *
	 * @return A new time-based UUID.
	 */
	public static String newTimeBasedUuid() {
		org.safehaus.uuid.UUIDGenerator uuidGenerator = org.safehaus.uuid.UUIDGenerator.getInstance();
		return uuidGenerator.generateTimeBasedUUID(LOCAL_ADDRESS).toString().toUpperCase();
	}

}
