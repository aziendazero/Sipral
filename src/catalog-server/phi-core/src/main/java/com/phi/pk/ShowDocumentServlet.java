package com.phi.pk;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.phi.cs.view.bean.ShowMediaBean;


/**
 * A servlet to return a pdf document. It can be called inside an iframe.
 * @author gaion
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
public class ShowDocumentServlet extends HttpServlet 
{
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
	}

	private static final long serialVersionUID = -3195651995454845522L;
	private final static Logger log = Logger.getLogger(ShowMediaBean.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		super.doGet(req, resp);
		this.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		super.doPost(req, resp);
		this.service(req, resp);
	}

	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		new ContextualHttpServletRequest(req) {

			@Override
			public void process() throws Exception {
				doWork(req,resp);
			}
		}.run();

	}

	/**
	 *  servlet in general is called with inConv or filename paramter (not both).
	 *  
	 *  servlet parameters:
	 *  cid: conversation id to switch to a proper existing conversation. (required)
	 *  
	 *  filename: name of the report to be generated. When passed other parameters are ignored.  
	 *  
	 *  inConv  When inConv=true, the byteArray is taken from convarsation object "lastGeneratedReportName"
	 *  		when inConv=xxxx,  the byteArray is taken from conversation object with given name xxxx
	 *  
	 *  removeFromConv=false (optional)  do not remove from conversation the object used to take the byte array. 
	 *  								 used by some solution, to generate one time the byte array, and reprint the same report more times.
	 *  
	 *  
	 *  
	 */
	@SuppressWarnings("unchecked")
	public void doWork(HttpServletRequest req, HttpServletResponse resp) {
		
		ServletOutputStream out = null; 
	
		try {
			
			out = resp.getOutputStream();
			String convIdParam = req.getParameter("cid");
			String mimeType = "application/pdf";
			String filename = req.getParameter("filename");
			String inConv = req.getParameter("inConv");
			String removeFromConv = req.getParameter("removeFromConv");
			
			Manager.instance().switchConversation(convIdParam);
			
			if (inConv != null && !inConv.isEmpty() ) {
				Context conv = Contexts.getConversationContext();
				if (filename == null || filename.isEmpty()) {
					if (inConv.equals("true")){
						filename = (String)conv.get("lastGeneratedReportName");
					}
					else {
						filename = inConv;
					}
				}
						
				if (filename != null && !filename.isEmpty()) {
				
					Object attach = conv.get(filename);
					
					if (attach instanceof byte[]) {
						byte[] content = (byte[])attach;
						if (isBytePdf(content)) {
							resp.setContentType("application/pdf");
							resp.addHeader("Content-Disposition", "inline; filename=" + filename + ".pdf");
						} else {
							resp.setContentType("application/octet-stream");
							resp.addHeader("Content-Disposition", null);
						}
						
	
						resp.setHeader("Expires", "0");
						resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
						out.write(content);
						out.flush();
						
					}
					
					if (!"false".equals(removeFromConv)) {  
						conv.remove(filename);
						conv.remove("lastGeneratedReportName");
					}
					
				}
				else {
					log.error("missing filename or conversation name lastGeneratedReportName");
				}
			}
			else {
				if (filename != null && filename.isEmpty()) {
					InputStream is = new FileInputStream(filename);
						
					if (mimeType != null) {
						 
						if (mimeType.equals("application/pdf")) {
							if (is != null) {
								resp.setContentType(mimeType);
								byte[] buff = new byte[2048];
								int bytesRead;
								while (-1 != (bytesRead = is.read(buff, 0,
										buff.length))) {
									out.write(buff, 0, bytesRead);
								}
							} else {
								resp.setCharacterEncoding("UTF-8");
								out.print(new String(
										"Errore interno nella presentazione del documento"
												.getBytes("UTF-8")));
							}
							
						}
					}
				}
				else {
					log.error("missing filename");
				}
				
			}
					
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("error closing ShowDocumentServlet output stream.");
				}
			}
		}
	

	}
	
	private boolean isBytePdf(byte[] content){
	    	return content[0] == 0x25		// %
	    			&& content[1] == 0x50	// P
	    			&& content[2] == 0x44	// D
	    			&& content[3] == 0x46	// F
	    			&& content[4] == 0x2D;	// -
	}
	

}
