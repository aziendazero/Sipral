package com.phi.sign;

import it.insielmercato.firmadigitale.WrapperFirmaDigitalePkBoxHttp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;


public class PkBoxHttpInvoker extends DigitalSignatureInvoker {
	
	private WrapperFirmaDigitalePkBoxHttp pkboxHttpWrapper;	
	
	public PkBoxHttpInvoker(Properties properties) throws 
			IllegalArgumentException, Exception {
		createWrapper(properties);
	}
	
	@Override
	@Create
	public void createWrapper (Properties properties) 
					throws IllegalArgumentException, Exception {
		pkboxHttpWrapper = new WrapperFirmaDigitalePkBoxHttp();
		pkboxHttpWrapper.setProperty(
				WrapperFirmaDigitalePkBoxHttp.PKBOX_MODE,
	    		getProperty(properties, "pkbox.mode"));
		pkboxHttpWrapper.setProperty(
	    		WrapperFirmaDigitalePkBoxHttp.PKBOX_ENCODING,
	    		getProperty(properties, "pkbox.encoding"));		
		
		pkboxHttpWrapper.setProperty(
				"Timeout",
	    		getProperty(properties, "pkbox.http.timeout"));			
		pkboxHttpWrapper.setProperty(
				"ProxyHost",
	    		getProperty(properties, "pkbox.http.proxy.host"));				
		pkboxHttpWrapper.setProperty(
				"ProxyPort",
	    		getProperty(properties, "pkbox.http.proxy.port"));	
		pkboxHttpWrapper.setProperty(
				"ProxySocksPort",
	    		getProperty(properties, "pkbox.http.proxy.socks.port"));	
		pkboxHttpWrapper.setProperty(
				"ProxyUser",
	    		getProperty(properties, "pkbox.http.proxy.user"));	
		pkboxHttpWrapper.setProperty(
				"ProxyPwd",
	    		getProperty(properties, "pkbox.http.proxy.pwd"));			
		
        int maxServers = 1;
        while (getProperty(properties, "pkbox.http.url." + maxServers) != null &&
               !getProperty(properties, "pkbox.http.url." + maxServers).isEmpty()) {
            maxServers++;
        }			
        for (int index = 1; index <= maxServers; index ++) {
    		pkboxHttpWrapper.setProperty(
    				"PkBoxUrl" + index,
    	    		getProperty(properties, "pkbox.http.url." + index)); 
    		pkboxHttpWrapper.setProperty(
    				"PkBoxRealm" + index,
    	    		getProperty(properties, "pkbox.http.realm." + index));   
    		pkboxHttpWrapper.setProperty(
    				"PkBoxUser" + index,
    	    		getProperty(properties, "pkbox.http.user." + index)); 
    		pkboxHttpWrapper.setProperty(
    				"PkBoxPassword" + index,
    	    		getProperty(properties, "pkbox.http.password." + index));      		
        }		
	}
	
	@Override
	@Destroy
	public void destroyWrapper (Properties properties) 
			throws IllegalArgumentException, Exception {
		pkboxHttpWrapper.close();
	}

	@Override
	public void sign (String fileName,String uniqueDocumentNumber, SignType signType,
			Date dateSign, InputStream inputStream, OutputStream outputStream )
						throws Exception {
		try {
			pkboxHttpWrapper.startSession();
			pkboxHttpWrapper.sign(signer, pin, dateSign, inputStream, outputStream);
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
	}

	@Override
	public void addsign ( 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
						throws Exception {
		try {
			pkboxHttpWrapper.startSession();
			pkboxHttpWrapper.addsign(signer, pin, dateSign, inputStream, outputStream);
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
	}
	
	@Override
	public VerifyInfo verify (String uniqueDocumentNumber, 
			InputStream inputStream, OutputStream outputStream )
						throws Exception {
		VerifyInfo verifyInfo = null;
		try {
			pkboxHttpWrapper.startSession();
			verifyInfo = new VerifyInfo (pkboxHttpWrapper.verify (inputStream, outputStream));
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
		
		return verifyInfo;
	}

	@Override
	public void pdfsign ( 
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream)
						throws Exception {
		try {
			pkboxHttpWrapper.startSession();
			pkboxHttpWrapper.pdfsign(signer, pin, reason, location, contact, dateSign, 
									 inputStream, outputStream);
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
	}
	
	@Override
	public void pdfaddsign ( 
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream)
					throws Exception {
		try {
			pkboxHttpWrapper.startSession();
			pkboxHttpWrapper.pdfaddsign(signer, pin, reason, location, contact, 
					dateSign, inputStream, outputStream);
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
	}

	@Override
	public VerifyInfo pdfverify ( InputStream inputStream ) throws Exception {
		VerifyInfo verifyInfo = null;		
		try {
			pkboxHttpWrapper.startSession();
			pkboxHttpWrapper.pdfverify(inputStream);
		}
		finally {
			pkboxHttpWrapper.endSession();
		}
		return verifyInfo;		
	}	
}
