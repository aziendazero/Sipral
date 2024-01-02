package com.phi.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.act.DocRepository;
import com.phi.security.UserBean;
import com.phi.sign.util.AddPdfFooter;

@BypassInterceptors
@Name("DocSigner")
@Scope(ScopeType.CONVERSATION)
public class DocSigner extends DigitalSigner implements Serializable {

	private static final long serialVersionUID = -4942717190563255367L;
	private static final Logger log = Logger.getLogger(DocSigner.class);	

	private Integer footerFontSize = 7;	
	private Integer footerRectangle_llx = 20;
	private Integer footerRectangle_lly = 100;
	private Integer footerRectangle_urx = 500;
	private Integer footerRectangle_ury = 10;
	private Boolean footerDefault = true;
	private String  extensionFile= "";

	private static final Properties signProperties = new Properties();
	
	static {
		InputStream seamPropertiesIs = null;
		try {
			URL url = RepositoryManager.findUrl("phi-sign", "seam.properties");

			seamPropertiesIs = url.openStream();
			
			signProperties.load(seamPropertiesIs);
			
		} catch (Exception e) {
			log.error("Error loading phi-sign seam.properties", e);
		} finally {
			if (seamPropertiesIs != null) {
				try {
					seamPropertiesIs.close();
				} catch (IOException e) {
					log.error("Error loading phi-sign seam.properties, input stream not closed!", e);
				}
			}
		}
	}


	// ----------------------------------------
	// --- Singleton pattern implementation ---  
	// ---------------------------------------- 

	/**
	 * Conversation scope instance retrieving
	 * @return instance of the action, singleton into the conversation
	 */
	public static DocSigner instance() {
		return (DocSigner) Component.getInstance(DocSigner.class, ScopeType.CONVERSATION);
	}

	public DocSigner() throws Exception {

		parseProperties(signProperties);

		UserBean userBean = UserBean.instance();
		if (userBean != null) {
			setCustomerInfo(userBean.getNameGiv() + " " + userBean.getNameFam());
		}		
	}

	@Destroy
	public void destroy() throws Exception {
		super.destroy();
	}

	public void parseProperties(Properties properties) throws Exception {
		super.parseProperties(properties);

		if (properties.getProperty("sign.footer.rectangle.llx") != null) {
			footerRectangle_llx = Integer.parseInt(properties.getProperty("sign.footer.rectangle.llx"));
		}

		if (properties.getProperty("sign.footer.rectangle.lly") != null) {
			footerRectangle_lly = Integer.parseInt(properties.getProperty("sign.footer.rectangle.lly"));
		}

		if (properties.getProperty("sign.footer.rectangle.urx") != null) {
			footerRectangle_urx = Integer.parseInt(properties.getProperty("sign.footer.rectangle.urx"));
		}

		if (properties.getProperty("sign.footer.rectangle.ury") != null) {
			footerRectangle_ury = Integer.parseInt(properties.getProperty("sign.footer.rectangle.ury"));
		}		

		if (properties.getProperty("sign.footer.font.size") != null) {
			footerFontSize = Integer.parseInt(properties.getProperty("sign.footer.font.size"));
		}		
		if (properties.getProperty("sign.footer.default") != null) {
			footerDefault = Boolean.parseBoolean(properties.getProperty("sign.footer.default"));
		}
		if (properties.getProperty("sign.extension.files") != null) {
			extensionFile = properties.getProperty("sign.extension.files");
		}
	}



	// ----------------------------
	// --- Signer configuration ---
	// ---------------------------- 

	public Integer getFooterFontSize() {
		return footerFontSize;
	}

	public void setFooterFontSize(Integer footerFontSize) {
		this.footerFontSize = footerFontSize;
	}

	public Integer getFooterRectangle_llx() {
		return footerRectangle_llx;
	}

	public void setFooterRectangle_llx(Integer footerRectangle_llx) {
		this.footerRectangle_llx = footerRectangle_llx;
	}

	public Integer getFooterRectangle_lly() {
		return footerRectangle_lly;
	}

	public void setFooterRectangle_lly(Integer footerRectangle_lly) {
		this.footerRectangle_lly = footerRectangle_lly;
	}

	public Integer getFooterRectangle_urx() {
		return footerRectangle_urx;
	}

	public void setFooterRectangle_urx(Integer footerRectangle_urx) {
		this.footerRectangle_urx = footerRectangle_urx;
	}

	public Integer getFooterRectangle_ury() {
		return footerRectangle_ury;
	}

	public void setFooterRectangle_ury(Integer footerRectangle_ury) {
		this.footerRectangle_ury = footerRectangle_ury;
	}

	public Boolean getFooterDefault() {
		return footerDefault;
	}

	public void setFooterDefault(Boolean footerDefault) {
		this.footerDefault = footerDefault;
	}
	public String getExtensionFile() {
		return extensionFile;
	}

	public void setExtensionFile(String extensionFile) {
		this.extensionFile = extensionFile;
	}
//	private boolean isSigleSign(){
//		boolean singleSign = true;
//		DocRepositoryAction docReposAction = DocRepositoryAction.instance();
//		if (docReposAction.getTemporary().get("multipleSign")!= null && (Boolean) docReposAction.getTemporary().get("multipleSign")==true){
//			singleSign = false;
//		}
//		return singleSign;
//	}

	/**
	 * Direct signer configuration. If some of them are missing an exception 
	 * is thrown. However, the signer is partially configured in order to allow 
	 * the user to add only the missing parameters.
	 * @param req servlet request, required parameters: aliasSigner & pinSigner
	 * @throws IllegalArgumentException if some parameters are missing
	 */
	public void configure(String customerInfo, 
			String aliasSigner, String pinSigner) 
					throws IllegalArgumentException {	
		setSigner(aliasSigner);
		setPin(pinSigner);
		setCustomerInfo(customerInfo);
	}

	public String sign (DocRepository docrepos, SignType signType, Date signDocDateReplace) throws Exception {
		String contentType = docrepos.getContentType() != null ? docrepos.getContentType().getCode() : "";
		signType = signType == null?getSignType():signType;
		String[] extFiles =  extensionFile.split(",");
		String fileName ="";
		
		if(docrepos.getContentType() != null){
			contentType = docrepos.getContentType().getCode();

		}
		boolean noSign= true;
		for(String extFile : extFiles){
			if(extFile.equals(contentType)){
				noSign= false;

				// get signer information
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();			
				//sign (docrepos.getUniqueDocumentNumber(),new Date(), new ByteArrayInputStream(new byte [10]), ostream);

				// get patient language
				//			CodeValuePhi language = null;
				//			if (docrepos.getPatient() != null) {
				//				language = docrepos.getPatient().getLanguage();
				//			}
				//Decode Base64
				if(Base64.isBase64(docrepos.getContent())){
					Base64.decodeBase64(docrepos.getContent());
				}
				
				if (signType == SignType.CAdES) {
					// add footer
					docrepos.setContent (AddPdfFooter.addFooter(docrepos, null,signDocDateReplace, null, 
							footerDefault,footerRectangle_llx, footerRectangle_lly,footerRectangle_urx, footerRectangle_ury, footerFontSize));				
				}
				else if (signType != SignType.PAdES) {
					throw new Exception ("SignType not allowed");
				}
				// Define file name. Fix when there is a tag for name and extension
				if(docrepos.getContentType().getCode().equals("PDF")||docrepos.getContentType().getCode().equals("PDF_ADOBE")){
					fileName=  docrepos.getUniqueDocumentNumber()+".pdf";
				}
				else{
					throw new Exception ("Extension not allowed");
				}
				// sign document
				//ostream = new ByteArrayOutputStream();	
				sign(fileName,docrepos.getUniqueDocumentNumber(), signType,
						docrepos.getSignDocDate(), new ByteArrayInputStream(docrepos.getContent()), ostream);
				docrepos.setContent(ostream.toByteArray());

				if (signType == SignType.CAdES) {
					contentType = "PDF_PKCS7";
				}else if (signType == SignType.PAdES) {
					contentType = "PDF_ADOBE";
				}
				else {
					throw new Exception ("content type " + contentType + " not allowed for sign");
				}
				break;
			}
		}
		if(noSign) {
			throw new Exception ("content type " + contentType + " not allowed for sign");
		}
		return contentType;
	}




	/**
	 * Default configuration for the sytem test. Do not use on production.
	 */
	public void testConfiguration() {
		setSigner("testaslb02");
		setPin("12345678");
		setCustomerInfo(UserBean.instance().getNameGiv() + 
				" " + UserBean.instance().getNameFam());
	}	
}