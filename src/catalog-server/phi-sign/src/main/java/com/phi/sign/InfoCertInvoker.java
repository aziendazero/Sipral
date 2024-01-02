package com.phi.sign;

import it.insielmercato.firmadigitale.WrapperFirmaDigitaleInfoCert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;

public class InfoCertInvoker extends DigitalSignatureInvoker {
//	private static final Logger log = Logger.getLogger(InfoCertInvoker.class);
	
	private WrapperFirmaDigitaleInfoCert infoCertWrapper;	
		
	public InfoCertInvoker(Properties properties) throws 
			IllegalArgumentException, Exception {
		createWrapper(properties);
	}
	
	
	@Create
	@Override
	protected void createWrapper(Properties properties)
			throws IllegalArgumentException, Exception {
		infoCertWrapper = new WrapperFirmaDigitaleInfoCert();		
		infoCertWrapper.setProperty(WrapperFirmaDigitaleInfoCert.INFOCERT_URL, 
				getProperty(properties, "infocert.url"));
		infoCertWrapper.setProperty(WrapperFirmaDigitaleInfoCert.SIGN_TYPE, 
				getProperty(properties, "sign.type"));
	}

	@Destroy
	@Override
	protected void destroyWrapper(Properties properties)
			throws IllegalArgumentException, Exception {
		infoCertWrapper.close();		
	}

	@Override
	public void sign(String fileName,String uniqueDocumentNumber, SignType signType,
			Date dateSign, InputStream inputStream, OutputStream outputStream)
			throws Exception {
		try {
			infoCertWrapper.startSession();
			infoCertWrapper.sign(fileName,null, signer, pin, null, signType.name(), dateSign, inputStream, outputStream);
		}
		finally {
			infoCertWrapper.endSession();
		}
	}

	@Override
	public void addsign(Date dateSign, InputStream inputStream,
			OutputStream outputStream) throws Exception {
		throw new Exception ("Not implemented yet.");
	}

	@Override
	public VerifyInfo verify(String uniqueDocumentNumber,
			InputStream inputStream, OutputStream outputStream)
			throws Exception {
		VerifyInfo verifyInfo = null;
		try {
			infoCertWrapper.startSession();
			verifyInfo = new VerifyInfo (infoCertWrapper.verify (null, inputStream, outputStream));
		}
		finally {
			infoCertWrapper.endSession();
		}
		
		return verifyInfo;
	}

	@Override
	public void pdfsign(String reason, String location, String contact,
			Date dateSign, InputStream inputStream, OutputStream outputStream)
			throws Exception {
		throw new Exception ("Not implemented yet.");		
	}

	@Override
	public void pdfaddsign(String reason, String location, String contact,
			Date dateSign, InputStream inputStream, OutputStream outputStream)
			throws Exception {
		throw new Exception ("Not implemented yet.");
	}

	@Override
	public VerifyInfo pdfverify(InputStream inputStream) throws Exception {
		throw new Exception ("Not implemented yet.");
	}

}
