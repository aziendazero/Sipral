package com.phi.sign;

import it.insielmercato.firmadigitale.WrapperFirmaDigitalePkBoxSoap;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;

//@Deprecated
public class PkBoxSoapInvoker extends DigitalSignatureInvoker {
	private static final Logger log = Logger.getLogger(PkBoxSoapInvoker.class);
	
	private String customerInfo;	
	private WrapperFirmaDigitalePkBoxSoap pkboxSoapWrapper;
	
	public PkBoxSoapInvoker(Properties properties) 
					throws IllegalArgumentException, Exception {
		createWrapper(properties);
	}	
	
	@Override
	@Create
	public void createWrapper (Properties properties) 
					throws IllegalArgumentException, Exception {
		pkboxSoapWrapper = new WrapperFirmaDigitalePkBoxSoap ();
	   
		pkboxSoapWrapper.setProperty(
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_MODE,
	    		getProperty(properties, "pkbox.mode"));
		pkboxSoapWrapper.setProperty(
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_ENCODING,
	    		getProperty(properties, "pkbox.encoding"));
		pkboxSoapWrapper.setProperty( 
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_ENVIRONMENT, 
	    		getProperty(properties, "pkbox.soap.environment"));
		pkboxSoapWrapper.setProperty( 
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_URL, 
	    		getProperty(properties, "pkbox.soap.url"));
		pkboxSoapWrapper.setProperty(
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_CONNECT_TIMEOUT,
	    		getProperty(properties, "pkbox.soap.connect.timeout"));	    
		pkboxSoapWrapper.setProperty(
	    		WrapperFirmaDigitalePkBoxSoap.PKBOX_REQUEST_TIMEOUT,
	    		getProperty(properties, "pkbox.soap.request.timeout"));
		//pkboxSoapWrapper.setProperty(
	    //		WrapperFirmaDigitalePkBoxSoap.SIGN_TYPE,
	    //		getProperty(properties, "sign.type"));
		pkboxSoapWrapper.startSession();		
	}
	
	@Override
	@Destroy
	public void destroyWrapper (Properties properties) 
			throws IllegalArgumentException, Exception {
		pkboxSoapWrapper.endSession();
	}
	
	@Override
	public void sign (String fileName,String uniqueDocumentNumber, SignType signType,
			Date dateSign, InputStream inputStream, OutputStream outputStream )
						throws Exception {
		if (log.isInfoEnabled()){
			log.info("Start HSM sign call with uniqueDocumentNumber:"+uniqueDocumentNumber+" - customerInfo:"+customerInfo+" - signer:"+signer+" - dateSign:"+dateSign);
		}
		pkboxSoapWrapper.sign(customerInfo, signer, pin, signerPin, dateSign, inputStream, outputStream);
		if (log.isInfoEnabled()){
			log.info("End HSM sign call with uniqueDocumentNumber:"+uniqueDocumentNumber+" - customerInfo:"+customerInfo);
		}
	}

	@Override
	public void addsign ( 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
						throws Exception {
		/*pkboxSoapWrapper.addsign(customerInfo, signer, pin, signerPin, 
				dateSign, inputStream, outputStream);*/
	}

	@Override
	public VerifyInfo verify (String uniqueDocumentNumber, 
			InputStream inputStream, OutputStream outputStream )
						throws Exception {
		if (log.isInfoEnabled()){
			log.info("Start HSM verify call with uniqueDocumentNumber:"+uniqueDocumentNumber+" - customerInfo:"+customerInfo+" - signer:"+signer);
		}
		VerifyInfo verifyInfo = null;
		verifyInfo = new VerifyInfo (pkboxSoapWrapper.verify(customerInfo, inputStream, outputStream));
		if (log.isInfoEnabled()){
			log.info("End HSM verify call with uniqueDocumentNumber:"+uniqueDocumentNumber+" - customerInfo:"+customerInfo);
		}
		return verifyInfo;
	}

	@Override
	public void pdfsign( 
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
						throws Exception {
		if (log.isDebugEnabled()) {
			log.debug(String.format("pdfsign (customerInfo=%s, signer=%s, pin=%s, signerPin=%s, reason=%s, location=%s, contact=%s)", 
					customerInfo, signer, pin, signerPin, reason, location, contact));
		}
		/*pkboxSoapWrapper.pdfsign(customerInfo, signer, pin, signerPin, reason, location, contact, 
				dateSign, inputStream, outputStream);*/
	}

	@Override
	public void pdfaddsign( 
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
					throws Exception {
		/*pkboxSoapWrapper.pdfaddsign(customerInfo, signer, pin, signerPin, reason, location, contact, 
				dateSign, inputStream, outputStream);*/
	}

	@Override
	public VerifyInfo pdfverify ( InputStream inputStream ) throws Exception {
		return null;
		/*return new VerifyInfo (pkboxSoapWrapper.pdfverify(customerInfo, inputStream));*/
	}	

	
	
	// --------------------------
	// --- Property accessors ---
	// --------------------------
	
	public String getCustomerInfo() {
		return customerInfo;
	}
	
	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}
}
