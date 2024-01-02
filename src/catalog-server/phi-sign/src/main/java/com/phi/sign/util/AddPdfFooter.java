package com.phi.sign.util;

import it.pkbox.client.DNParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.phi.entities.act.DocRepository;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.sign.Signer;

public class AddPdfFooter {
//	private static final Logger log = Logger.getLogger(AddPdfFooter.class);	

	private static String getFirmatario (String subjectDN) {
		if (subjectDN != null) {
			DNParser parser = new DNParser(subjectDN);

			// restituisco il firmatario del documento
			String name = parser.get("CN");
			if (name == null) {
				name = parser.get("GIVENNAME");
			}
			if (name.indexOf("/") > 0) {
				String []vals = name.split("/");
				name = vals[0] + " " + vals[1];
			}
			return name;
		}
		else {
			return null;
		}
	}	

//	private static String getEnteCertificatore (String issuerDN) {
//		if (issuerDN != null) {
//			DNParser parser = new DNParser(issuerDN);
//			return parser.get("O");
//		}
//		else {
//			return null;
//		}
//	}    

	public static byte[] addFooter (DocRepository docRepos, Signer signer,Date dataSignReplace,
			CodeValuePhi language, Boolean footerDefault,
			float llx, float lly, float urx, float ury, float fontSize) throws Exception {

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		byte [] input = docRepos.getContent();
		Date dataSign = docRepos.getSignDocDate();
		boolean replace = false;
		boolean cancel = false;
		if(docRepos.getCompletionStatus().getCode().equals("LA")){
			replace= true;
		}
		if (docRepos.getAvailabilityStatus().getCode().equals("CA")){
			cancel = true;
		}
		String uniqueIdReplace = docRepos.getApplicationOwner() + "-" + docRepos.getParentDocumentNumber();
		String versionNoteReplace = docRepos.getVersionNote();
		String genEntityId = docRepos.getGeneratorEntityId();
		String[] genEntityIdSplit = genEntityId.split("-");
		String codeLabel = genEntityIdSplit[0];

		addFooter(new ByteArrayInputStream(input), ostream, signer, dataSign, replace,cancel, 
				uniqueIdReplace, dataSignReplace, versionNoteReplace, language, footerDefault,
				llx, lly, urx, ury, fontSize, codeLabel);

		return ostream.toByteArray();
	}

	private static class FooterItem {
		private String text;
		private Font font;

		public FooterItem (String text, Font font) {
			this.text = text;
			this.font = font;
		}

		public String getText() {
			return text;
		}

//		public void setText(String text) {
//			this.text = text;
//		}

		public Font getFont() {
			return font;
		}

//		public void setFont(Font font) {
//			this.font = font;
//		}
	}

	public static void addFooter (InputStream isPdf, OutputStream osPdf, Signer signer, Date dataSign, 
			boolean replace,boolean cancel, String uniqueIdReplace, Date dataSignReplace, String versionNoteReplace,
			CodeValuePhi language, Boolean footerDefault,
			float llx, float lly, float urx, float ury, float fontSize, String codeLabel) throws Exception {

//		BigInteger serialNumber = new BigInteger(((signer.getCertificateSN() == null)?"0":signer.getCertificateSN()));

		List<FooterItem> footSigned = new ArrayList<FooterItem> (); 

		BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
		
		Font font = new Font(helvetica, fontSize, Font.BOLD);
		//		
		//Set font
//		Font fontVerdanaBold = new Font(getFontFamaly("verdana"));
		font.setSize(fontSize);
		font.setStyle(Font.BOLD);
		
		footSigned.add(new FooterItem("---------------------------------------------------------------------------------------------------------------", font));
		if (replace == true) {
			footSigned.add(new FooterItem(String.format("Il presente documento SOSTITUISCE il precedente numero ID (%s), emesso in data %s, per %s.", 
					uniqueIdReplace, 
					new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dataSignReplace),
					versionNoteReplace), font));
			footSigned.add(new FooterItem("", font));
		}
		footSigned.add(new FooterItem("Rappresentazione di un documento firmato elettronicamente, secondo la normativa vigente.", font));
		footSigned.add(new FooterItem(String.format("Firmato da: %s in data %s", getFirmatario(signer.getSubjectDN()), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dataSign)), font));
//		footSigned.add(new FooterItem(String.format("Numero certificato %s emesso dall'ente certificatore %s" , serialNumber.toString(16), getEnteCertificatore(signer.getIssuerDN())), font));
		footSigned.add(new FooterItem("Il documento è conservato secondo la normativa vigente.", font));
		footSigned.add(new FooterItem("---------------------------------------------------------------------------------------------------------------", font));

		
		//footSigned.add(new FooterItem("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", fontVerdanaBold));

//		if (replace == true) {
//			if (footerDefault) {
//				//get static label
//				codeLabel= "footerDefault_replace";
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//
//				//set parameters
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSignReplace);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSignReplace);
//
//				footSigned.add(new FooterItem(String.format(message, uniqueIdReplace, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dataSignReplace),versionNoteReplace), fontVerdanaBold));
//			
//			}
//			else {
//				//get static label
//				codeLabel = codeLabel.concat("_replace");
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//
//				//set parameters
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSign);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSign);
//				String dateRp = new SimpleDateFormat("dd-MM-yyyy").format(dataSignReplace);
//				String hourRp = new SimpleDateFormat("HH:mm:ss").format(dataSignReplace);
//
//				footSigned.add(new FooterItem(String.format(message,  date, hour,dateRp,hourRp,date, hour,dateRp,hourRp), fontVerdanaBold));
//				
//			}					
//		}
//		else if (cancel== true){
//
//			if (footerDefault) {
//				// get static label for footer
//				codeLabel = "footerDefault_cancel";
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//				
//				//set parameters
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSign);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSign);
//
//				footSigned.add(new FooterItem(String.format(message, date, hour, date, hour), fontVerdanaBold));
//				
//			}
//			else{
//				// get static label for footer
//				codeLabel = codeLabel.concat("_cancel");
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//				
//				//set parameters		
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSign);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSign);
//
//				footSigned.add(new FooterItem(String.format(message, date, hour, date, hour), fontVerdanaBold));
//			
//			}
//		}
//		else{
//			if (footerDefault) {
//				codeLabel = "footerDefault_new_sign";
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//				//footSigned.add(new FooterItem(String.format(message, getFirmatario(signer.getSubjectDN()), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dataSign)), fontVerdanaBold));
//			}
//			else {
//				// get static label for footer
//				codeLabel = codeLabel.concat("_new_sign");
//				String message = FunctionsBean.instance().getStaticTranslation(codeLabel);
//
//				//set parameters
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSign);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSign);
//
//				footSigned.add(new FooterItem(String.format(message, signer.getSubjectDN(), date, hour, signer.getSigners()getCertificateSN(), signer.getSignerCount()), fontVerdanaBold));
//				
//			}
//		}

		if (!footerDefault && footSigned.size() < 4) {
			List<FooterItem> footSignedTmp = new ArrayList<FooterItem> (); 
//			Font font = new Font(getFontFamaly("verdana"));
			font.setSize(fontSize);
			font.setStyle(Font.NORMAL);				
			for (int i = 0; i < (4-footSigned.size()); i ++) {
				footSignedTmp.add(new FooterItem("", font));
			}
			footSignedTmp.addAll(footSigned);
			footSigned = footSignedTmp;
		}

		addFooter (isPdf, osPdf, footSigned, llx, lly, urx, ury, fontSize);
	}


	private static void addFooter (InputStream isPdf, OutputStream osPdf, List<FooterItem> footSigned, 
			float llx, float lly, float urx, float ury, float fontSize) throws Exception {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(isPdf);
			stamper = new PdfStamper (reader, osPdf);
	

			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				PdfContentByte cb = stamper.getOverContent(i);
				ColumnText ct = new ColumnText(cb);
				ct.setAlignment(Element.ALIGN_CENTER);
				ct.setSimpleColumn(llx, lly, urx, ury);
				ct.setLeading(fontSize);
				for(FooterItem item : footSigned){
					ct.addText(Chunk.NEWLINE);
					ct.addText(new Phrase(fontSize, item.getText(), item.getFont()));
				}
				ct.go();
			}			
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (Exception ex) {}
			try {
				if (stamper != null) {
					stamper.close();
				}
			}
			catch (Exception ex) {}			
		}
	}

//	private static Font getFontFamaly(String fontName) {
//		if (!FontFactory.isRegistered(fontName)) {			
//			FontFactory.register(RepositoryManager.findUrl("PHI_DOC_backend.jar", "verdana.ttf").toString());
//		}
//		return FontFactory.getFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//	}


//	public static void main (String []args) throws Exception {
//		FileInputStream fis = null;
//		FileOutputStream fos = null;
//
//		float llx = 20;
//		float lly = 120; 
//		float urx = 570; 
//		float ury = 10; 
//
//		Integer footerFontSize = 9;	
//
//		try {
//			fis = new FileInputStream ("c:\\Referto2.pdf");
//			fos = new FileOutputStream ("c:\\test_mod.pdf");
//
//			if (!FontFactory.isRegistered("verdana")) {	
//				FontFactory.register("C:\\PHI221\\ws-solutions\\PHI_DOC\\backend\\src\\main\\resources\\verdana.ttf");
//			}
//			Font verdanaFont = FontFactory.getFont("verdana", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//
//
//			List<FooterItem> footSigned = new ArrayList<FooterItem>();
//
//
//			int fontSize = 9;
//			boolean replace = false;
//			Date dataSignReplace = new Date();
//			Date dataSign = new Date();
//
//			//BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
//			if (replace == true) {
//				Font fontBold = new Font(verdanaFont);
//				fontBold.setSize(fontSize);
//				fontBold.setStyle(Font.BOLD);
//				String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSignReplace);
//				String hour = new SimpleDateFormat("HH:mm:ss").format(dataSignReplace);
//				footSigned.add(new FooterItem(String.format("Dieser Befund ersetzt den vorausgegangenen Befund vom %s um %s Uhr", date, hour), fontBold));
//				footSigned.add(new FooterItem(String.format("Referto emesso in sostituzione del precedente del %s alle %s", date, hour), fontBold));
//			}
//
//			//Font font = new Font(helvetica, fontSize, Font.NORMAL);
//			//Font font = getFontFamaly("verdana");
//			Font font = new Font(verdanaFont);
//			font.setSize(fontSize);
//			font.setStyle(Font.NORMAL);
//			//font.setColor(BaseColor.RED);
//			String date = new SimpleDateFormat("dd-MM-yyyy").format(dataSign);
//			String hour = new SimpleDateFormat("HH:mm:ss").format(dataSign);
//			String firmatario = "Francesco Zanutto";
//			footSigned.add(new FooterItem(String.format("Digital unterzeichnet am %s um  %s %s", date, hour, firmatario), font));
//			footSigned.add(new FooterItem(String.format("Firmato digitalmente il %s alle %s %s", date, hour, firmatario), font));			
//
//			addFooter (fis, fos, footSigned,llx,lly,urx,ury,footerFontSize);			
//		}
//		finally {
//			try {
//				if (fis != null) {
//					fis.close();
//				}
//			}
//			catch (Exception ex){}
//			try {
//				if (fos != null) {
//					fos.close();
//				}
//			}
//			catch (Exception ex){}			
//
//		}
//	}
}
