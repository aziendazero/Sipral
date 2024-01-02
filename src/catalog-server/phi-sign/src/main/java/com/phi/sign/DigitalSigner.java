package com.phi.sign;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class DigitalSigner {

	public static enum InvokerName {
		INV_PKBOX_SOAP,
		INV_PKBOX_HTTP,
		INV_INFOCERT
	}

	private static final Logger log = Logger.getLogger(DigitalSigner.class);
	
	private DigitalSignatureInvoker invoker;
	private InvokerName invokerName = InvokerName.INV_PKBOX_HTTP;
	private SignType signType = SignType.CAdES;
	
	private String docSignerType;

	protected void parseProperties(Properties properties) throws Exception {
		
		String docSignerType = properties.getProperty("docSignerType");
		if (docSignerType != null && !docSignerType.isEmpty()) {
			this.docSignerType = docSignerType;
		}
		
		String pkboxSoapUrl = properties.getProperty("pkbox.soap.url");
		if (pkboxSoapUrl != null && !pkboxSoapUrl.isEmpty()) {
			invokerName = InvokerName.INV_PKBOX_SOAP;
		}

		String infoCertUrl = properties.getProperty("infocert.url");
		if (infoCertUrl != null && !infoCertUrl.isEmpty()) {
			invokerName = InvokerName.INV_INFOCERT;			
		}
		
		signType = SignType.find (properties.getProperty("sign.type"));
		if (signType == null) {
			log.warn("sign type " + properties.getProperty("sign.type") + " unknow use CAdES as default");
			signType = SignType.CAdES;
		}				
		
		if (invokerName == InvokerName.INV_PKBOX_SOAP) {
			try {
				invoker = new PkBoxSoapInvoker(properties);
			} catch(IllegalArgumentException e) {
				// Do nothing, soap inker just not instantied
			}			
		}
		else if (invokerName == InvokerName.INV_PKBOX_HTTP) {
			throw new Exception("Deprecated invoker.");
		}
		else  if (invokerName == InvokerName.INV_INFOCERT) {
			invoker = new InfoCertInvoker(properties);
		}			
	}	
	
	public void destroy () throws Exception {
		if (invoker != null) {
			invoker.destroyWrapper(null);
			invoker = null;
		}
	}
	
	
	protected void sign (String fileName,String uniqueDocumentNumber, SignType signType,
			Date dateSign, InputStream inputStream, OutputStream outputStream ) 
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		invoker.sign(fileName,uniqueDocumentNumber, signType, dateSign, inputStream, outputStream);
	}
	
	/*protected void addsign ( 
			Date dateSign, InputStream inputStream, OutputStream outputStream ) 
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		invoker.addsign(dateSign, inputStream, outputStream);
	}*/
	
	protected VerifyInfo verify (String uniqueDocumentNumber,
			InputStream inputStream, OutputStream outputStream ) 
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		return invoker.verify(uniqueDocumentNumber, inputStream, outputStream);
	}

	/*protected void pdfsign (
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		invoker.pdfsign(reason, location, contact, dateSign, inputStream, outputStream);
	}*/
	
	/*protected void pdfaddsign (
			String reason, String location, String contact, 
			Date dateSign, InputStream inputStream, OutputStream outputStream )
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		invoker.pdfaddsign(reason, location, contact, dateSign, inputStream, outputStream);
	}	*/
	
	/*protected VerifyInfo pdfverify ( InputStream inputStream ) 
					throws UnsupportedOperationException, Exception {
		verifyInvoker();
		return invoker.pdfverify(inputStream);
	}*/
	
	private void verifyInvoker ( ) throws UnsupportedOperationException {
		if(invoker == null) {
			throw new UnsupportedOperationException(
					"no digital signeture invoker avaiable");
		}
	}

	// ----------------------------------
	// --- Invoker property accessors ---
	// ----------------------------------
	
	
	public String getDocSignerType() {
		return docSignerType;
	}
	
	public SignType getSignType() {
		return signType;
	}

	public void setSigner(String signer) throws UnsupportedOperationException {
		verifyInvoker();
		if (invoker != null) {
			invoker.setSigner(signer);
		}
	}
	
	public String getSigner() throws UnsupportedOperationException {
		verifyInvoker();
		return invoker.getSigner();
	}

	public void setPin(String pin) throws UnsupportedOperationException {
		verifyInvoker();
		if (invoker != null) {
			invoker.setPin(pin);
		}
	}
	
	public String getPin() throws UnsupportedOperationException {
		verifyInvoker();
		return invoker.getPin();
	}
	
	public void setCustomerInfo(String customerInfo) 
					throws UnsupportedOperationException {
		if(invokerName == InvokerName.INV_PKBOX_SOAP) {
			((PkBoxSoapInvoker)invoker).setCustomerInfo(customerInfo);
		}
	}
	
	public String getCustomerInfo() throws UnsupportedOperationException {
		String customerInfo = null;
		if(invokerName == InvokerName.INV_PKBOX_SOAP) {
			customerInfo = ((PkBoxSoapInvoker)invoker).getCustomerInfo();
		}
		return customerInfo;
	}
}