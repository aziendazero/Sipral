package com.phi.dictionaryManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.actions.CodeSystemAction;
import com.phi.entities.actions.CodeValueAction;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;

//Class similar to Dictionary Manager homonymous Class
//CodeExtension and CodeEquivalent non implemented.


@BypassInterceptors
@Name("DictionaryExportUtil")
@Scope(ScopeType.CONVERSATION)
public class DictionaryExportUtil implements Serializable{

	private static final long serialVersionUID = 6696147801062724124L;
	private static final String SEPARATOR = "|";
    private static final String SEPARATORSECONDARY = ":";
    private static final String NL = System.getProperty("line.separator");

    private static final Logger log = Logger.getLogger(BaseAction.class);
	
    private List<String> allOIDs = null;
    private StringBuffer exportBuffer;
    List<String> allowedDomains = null;

    public void exportCsv (CodeSystem cs, CodeValue cv) throws IOException {
		
    	String outputFileName ="";
    	if (cv != null) {
    		String type = cv.getType();
    		if (type.equals("S") || type.equals("A") || type.equals("T") ) {
    			//
    		}
    		outputFileName= cs.getName() + "_"+cv.getDisplayName()+".csv";
    	}
    	else {
    		outputFileName= cs.getName()+".csv";
    	}
    	
    	prepareExportBuffer(cs, cv);
    	byte[] byteDownload = exportBuffer.toString().getBytes();
    	
    	FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
		response.setContentType( "text/csv" );
		response.setCharacterEncoding("UTF-8");
		response.setContentLength( byteDownload.length );
		response.setHeader("Expires", "0");
		response.setHeader("Content-Disposition", "attachment;filename="+ outputFileName);
        
		
		PrintWriter os = response.getWriter();
		os.write(exportBuffer.toString());
		os.flush();
		os.close();
		facesContext.responseComplete();
		
		String cvLog = cv == null ? "" :"and selected cv: "+cv.getCode()+" ["+cv.getId()+"] ";
		log.info("generated CSV for "+cs.getName()+"["+cs.getId()+"] "+cvLog+"- SIZE: "+byteDownload.length + " byte");
        
    }
    
    
  
    
    public void prepareExportBuffer(CodeSystem cs, CodeValue cv){
    	
		log.debug("start export calculation for "+cs.getName()+" ["+cs.getId()+"] and selected cv: "+cv);
		CodeValueAction cva = CodeValueAction.instance();
		
		allOIDs = new ArrayList<String>();
		allowedDomains = (List<String>)cva.getTemporary().get("allowedDomains");
		exportBuffer =  new StringBuffer();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		exportBuffer.append("#" +
				Globals.PROPS_CODEVALUEMODEL+"="+ cs.getCodeValueClass() + SEPARATOR +
				Globals.PROPS_CODESYSTEMID+"="+ cs.getId() + SEPARATOR +
				Globals.PROPS_CODESYSTEMNAME+"="+cs.getName() + SEPARATOR +
				Globals.PROPS_CODESYSTEMDISPLAYNAME+"="+cs.getDisplayName() + SEPARATOR +
				Globals.PROPS_VERSION+"="+cs.getVersion() + SEPARATOR +
				Globals.PROPS_DESCRIPTION+"="+cs.getDescription() + SEPARATOR +
				Globals.PROPS_LANGUAGE+"="+cs.getIsoCodeLang() + SEPARATOR +
//							(keywords == null ? "" : (PROPS_KEYWORDS+"="+ keywords + SEPARATOR)) +
				Globals.PROPS_AUTHORITYNAME+"="+cs.getAuthorityName() + SEPARATOR +
				Globals.PROPS_AUTHORITYDESCRIPTION+"="+cs.getAuthorityDescription()+ SEPARATOR +
				Globals.PROPS_AUTHORITYCONTACTINFORMATION+"="+cs.getContactInformation() +
				(cs.getValidFrom() == null ? "" : SEPARATOR + (Globals.PROPS_VALIDFROM+"="+ sdf.format(cs.getValidFrom()))) +
				(cs.getValidTo() == null ? "" : (SEPARATOR + Globals.PROPS_VALIDTO+"="+sdf.format(cs.getValidTo()))) + NL);
		exportBuffer.append(Globals.EXPORT_IDSUFFIX+SEPARATOR+
				Globals.EXPORT_CODE+SEPARATOR+
				Globals.EXPORT_NAME+SEPARATOR+
				Globals.EXPORT_DESCRIPTION+SEPARATOR+
				Globals.EXPORT_ELEMENTTYPE+SEPARATOR+
				Globals.EXPORT_SEQUENCENUMBER+SEPARATOR+
				Globals.EXPORT_STATUS);
		
		if (allowedDomains == null)
			allowedDomains= new ArrayList<String>();
		
		// TRANSLATIONS
		List<String> languages = Arrays.asList(new String[] {"de","en","it"});
		Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
		for (String language : languages) {
			exportBuffer.append(SEPARATOR+Globals.EXPORT_TRANSLATION+language+Globals.EXPORT_TRANSLATIONEND);
		}
		
		
		// VALIDITY
		exportBuffer.append(SEPARATOR+Globals.EXPORT_VALIDFROM);
		exportBuffer.append(SEPARATOR+Globals.EXPORT_VALIDTO);
		
		exportBuffer.append(NL);

		if (cv != null) {
			//export one domain/code and its child
			if (cv.getType().equals("C") || allowedDomains.isEmpty() || allowedDomains.contains(cv.getId())) {
				writeBufferLine(cv,cs.getId());
			}
		}
		else {
			//Export entire codesystem: get list of top levels, and export them.
			CodeSystemAction csa = CodeSystemAction.instance();
			
			try {
				List<Object[]> toplevels = csa.getTopLevel();
				for (Object[] topres : toplevels) {
					String topLevelId = (String) topres[0];
					if (allowedDomains.isEmpty() || allowedDomains.contains(topLevelId)) {
						CodeValue cvChild = (CodeValue)CatalogPersistenceManagerImpl.getAdapter().get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), topLevelId);
//						CodeValue cvChild = cva.get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), topLevelId);
						writeBufferLine(cvChild,cs.getId());
					}
				}
			} catch (Exception e) {
				log.error("errog getting top levels for code system: "+cs.getName());
			}			
		}
		
		log.debug("Export completed for "+cs.getName()+" ["+cs.getId()+"] and selected cv: "+cv);
	}

    
	/**
	 * Recursively writes into exporting CSV file current Code Value datas
	 * 
	 * @param cv - current CodeValue
	 * @throws IOException
	 */
	private void writeBufferLine (CodeValue cv, String codeSystemId)  {

		
		String currentID = cv.getId();

		if (allOIDs.contains(currentID)) {
			log.debug("* CodeValue \""+cv.getDisplayName()+"\" aldeady exported: SKIPPED"+(Globals.CODEVALUE_TYPE_CONCEPTLEAF.equals(cv.getType()) ? "" : " [with all its descendants]")+NL);
			return;
		}
		
		// IGNORE DUMMY TOPLEVEL CV
		if (codeSystemId.equals(cv.getOid())) {
			log.debug("* Dummy Top Level CV \""+cv.getDisplayName()+"\": SKIPPED"+NL);
		} else {
			allOIDs.add(currentID);

			String currentCode = cv.getCode();
			String itemType = cv.getType();
			
			if (Globals.CODEVALUE_TYPE_ABSTRACTDOMAIN.equals(itemType) ||
					Globals.CODEVALUE_TYPE_TOPLEVEL.equals(itemType)) {
				currentCode = "-";
			}

			// remove code System ID from current ID
			if (currentID.startsWith(codeSystemId)) {
				currentID = currentID.substring(codeSystemId.length()+1);
			}

			// buffering current code value datas
			exportBuffer.append(currentID);
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(currentCode);
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getDisplayName());
			exportBuffer.append(SEPARATOR);
			if (cv.getDescription() != null) {
				exportBuffer.append(cv.getDescription().replace("\n", "\\n").replace("\r", "\\r"));
			}
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getType());
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getSequenceNumber()+(cv.isDefaultChild() ? SEPARATORSECONDARY + "true" : ""));
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getStatus());

			// EXTENSIONS: NOT IMPLEMENTED.
			
			List<String> languages = Arrays.asList(new String[] {"de","en","it"});
			Collections.sort(languages, String.CASE_INSENSITIVE_ORDER);
			// TRANSLATIONS
			for (String language : languages) {
				exportBuffer.append(SEPARATOR);
				String translation = cv.getTranslation(language);
				if (translation != null)
					exportBuffer.append(translation.trim());
			}

			
			// CODE EQUIVALENT [TARGET]: NOT IMPLEMENTED.

			// VALIDITY
			SimpleDateFormat sdf = new SimpleDateFormat(Globals.EXPORT_DATEFORMAT);
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getValidFrom() != null ? sdf.format(cv.getValidFrom()) : "");
			exportBuffer.append(SEPARATOR);
			exportBuffer.append(cv.getValidTo() != null ? sdf.format(cv.getValidTo()) : "");

			exportBuffer.append(NL);

			

		}
		// recursively write data from children code value
		if (!Globals.CODEVALUE_TYPE_CONCEPTLEAF.equals(cv.getType())) {
			List<CodeValue> children = new ArrayList<CodeValue>(cv.getChildren());
			Collections.sort(children, new Comparator<CodeValue>(){
				public int compare(CodeValue cv1, CodeValue cv2) {
					String id1 = cv1.getId();
					String id2 = cv2.getId();
					return id1.compareToIgnoreCase(id2);
				}
			});
			for (CodeValue childCV : children) {
				
				if (childCV.getType().equals("C") || allowedDomains.isEmpty() || allowedDomains.contains(childCV.getId())) {
					writeBufferLine(childCV,codeSystemId);
				}
			}
		}

		
		return;
	}
	
}
