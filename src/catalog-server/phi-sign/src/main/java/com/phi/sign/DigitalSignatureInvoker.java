package com.phi.sign;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

public abstract class DigitalSignatureInvoker {
	
	protected String signer;
	protected String signerPin;
	protected String pin;	
	
	
	
	// -------------------------
	// --- Wrapping protocol ---
	// -------------------------
	
	protected abstract void createWrapper ( Properties properties ) 
					throws IllegalArgumentException, Exception;
	
	protected abstract void destroyWrapper ( Properties properties ) 
			throws IllegalArgumentException, Exception;
	
	// ----------------------------------
	// --- Digital signature protocol ---
	// ----------------------------------
	
	public abstract void sign (String fileName,String uniqueDocumentNumber, SignType signType,
			Date dateSign, InputStream inputStream, OutputStream outputStream ) 
					throws Exception;
	
	public abstract void addsign ( 
			Date dateSign, InputStream inputStream, OutputStream outputStream ) 
					throws Exception;
	
	public abstract VerifyInfo verify (String uniqueDocumentNumber,
			InputStream inputStream, OutputStream outputStream ) 
					throws Exception;
	
	public abstract void  pdfsign (
			String reason, String location, String contact,  
			Date dateSign, InputStream inputStream, OutputStream outputStream )
					throws Exception;
	
	public abstract void  pdfaddsign (
			String reason, String location, String contact,  
			Date dateSign, InputStream inputStream, OutputStream outputStream )
					throws Exception;
	
	public abstract VerifyInfo pdfverify ( InputStream inputStream ) 
					throws Exception;
	
	
	
	// --------------------------
	// --- Property accessors ---
	// --------------------------
	
	protected String getProperty(Properties properties, String property) {
		String value = properties.getProperty(property);
		if(value == null || value.trim().length() == 0) {
			throw new IllegalArgumentException("missing property " + property);
		}
		return value.trim();
	}
	
	public void setSigner(String signer) {
		this.signer = signer;
		this.signerPin = signer;
	}
	
	public String getSigner() {
		return signer;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public String getPin() {
		return pin;
	}
}
