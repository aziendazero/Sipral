package com.phi.pk;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.pdf.DocumentData;
import org.jboss.seam.pdf.DocumentStore;
import org.jboss.seam.pdf.myutil.DataBean;

import com.phi.security.UserBean;

/**
 *  pdfExporter is used to generate bytecode array from a seam report.
 *  The bytecode is used by pkNet for signature.
 *  But it's used also when a process need to generate a pdf from a node, and then render it.
 *  
 *  To proper see the PDF generated, you need to enable the Servlet in web.xml:
 *  
    <servlet>
        <description/>
        <servlet-name>ShowDocumentServlet</servlet-name>
        <servlet-class>com.phi.pk.ShowDocumentServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>ShowDocumentServlet</servlet-name>
        <url-pattern>/ShowDocumentServlet</url-pattern>
    </servlet-mapping>
 * 
 * 
 */

@Name("pdfExporter")
@BypassInterceptors
public class PdfExporter {

	private static final Logger log = Logger.getLogger(PdfExporter.class);

	
	public byte[] pdfExport (String pageName) {  //generate byte array, and save in conv.
		return pdfExport(pageName, Conversation.instance().getId(), false, true);
	}
	/**
	 * returns the byte array of the PDF file generated using the page xhtml
	 * 
	 * @param page
	 *            the page name of the xhtml for the pdf
	 * @return the byte array containing the PDF file data
	 */
	public byte[] pdfExport(String page, String cid) {
		return pdfExport(page, cid, false,false);
	}

	public byte[] pdfExport(String page, String cid,boolean createEmptyFacesContext) {
		return pdfExport(page, cid, createEmptyFacesContext,false);
	}
	
	/**
	 * @see PdfExporter#pdfExport(String, String)
	 * @param page  the page name of the xhtml for the pdf
	 * @param cid
	 * @param createEmptyFacesContext if true create an empty FacesContext and 
	 * 		  do not execute a redirect
	 * @return the byte array containing the PDF file data
	 */
	public byte[] pdfExport(String page, String cid,boolean createEmptyFacesContext, boolean storeInConv) {
		byte[] pdfBytes = null;
		EmptyFacesContext emptyFacesContext = null;
		try {
			
			if (cid == null) {
				log.error("Unable to switch to null conversation for user "+UserBean.instance().getUsername()+": pdf "+page+" not created.");
			}
			else if (page == null || page.isEmpty()) {
				log.error("Unable to render null/empty page.");
			}
			else {
				boolean success = Manager.instance().switchConversation(cid);

				if (!success) {
					log.error("Error while switching conversation");
				} else {
					log.info("switch to conversation [" + cid + "] successful");
				}

				if(createEmptyFacesContext){
					emptyFacesContext = new EmptyFacesContext();
				}
	
				try {
					DocumentStore store = DocumentStore.instance();

					// deactivating PDF generation redirect
					DataBean datas = (DataBean)Component.getInstance(DataBean.class);
					datas.setRedirect(createEmptyFacesContext);
	
					if (page.endsWith(".report")) {
						page = page.replace(".report",".xhtml");
					}
					if (!page.endsWith(".xhtml")) {
						page = page+".xhtml";
					}
					
					Renderer renderer = Renderer.instance();
					renderer.render(page);
	
					String nextId = store.newId();
					long docId = Long.parseLong(nextId) - 1;
	
					DocumentData data = store.getDocumentData("" + docId);    			    			 
					pdfBytes = data.getData();
	
				} catch (Exception e) {
					log.error(e);
				}
			}
		} catch (Exception e) {
			log.error(e);

		} finally {
			if(emptyFacesContext!=null){
				log.info("restore facesContext "+cid);
				emptyFacesContext.restore();
			}
		}

		//used by ShowDocumentServlet of catalog server. To allow to create a pdf from process, store in conv, and show it in a new windows.
		Context conv = Contexts.getConversationContext();
		if (storeInConv) {
			//store in conversation the generated pdf, 
			conv.set(page, pdfBytes);
		}
		
		conv.set("lastGeneratedReportName",page);
		
		// return pdfBytes;
		return pdfBytes;


	}

}