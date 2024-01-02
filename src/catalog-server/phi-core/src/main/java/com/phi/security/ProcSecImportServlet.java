package com.phi.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.FileUploadException;

import com.phi.entities.actions.ProcSecurityAction;

public class ProcSecImportServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ProcSecurityAction.class);
	
	public void init(ServletConfig config) throws ServletException 	{
		super.init(config);
	}

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

	protected void service(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		new ContextualHttpServletRequest(req) {

			@Override
			public void process() throws Exception {
				doWork(req,resp);
			}
		}.run();

	}

	private void doWork( HttpServletRequest req, HttpServletResponse resp ) throws IOException 	{
		
		
//		if (!ServletFileUpload.isMultipartContent(req)) {
//			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
//		}
		String fileName= null;
		FileItemFactory fif = new DiskFileItemFactory();
		ServletFileUpload uploadHandler = new ServletFileUpload(fif);
		uploadHandler.setFileSizeMax(1024*1024*15); // 15 M
		String responseText = "";
		try {

			List<FileItem> items = uploadHandler.parseRequest(req);

			for (FileItem item : items) {
				if (!item.isFormField()) {
					fileName = item.getName();
					byte[] uploadedCsvByteArr = item.get();
					
					ProcSecurityAction procSecAction = ProcSecurityAction.instance();
					
					
					InputStream is = null;
					BufferedReader bfReader = null;
					int count=0;
					try {
						is = new ByteArrayInputStream(uploadedCsvByteArr);
						bfReader = new BufferedReader(new InputStreamReader(is));
						String csvLine =null;
						
						while((csvLine = bfReader.readLine()) != null){
							boolean ret = procSecAction.updateProcSecurity(csvLine);
							if (ret)
								count++;
						}
						
					} catch (IOException e) {
						log.error("error reading file. Message: "+e.getMessage()+ "\n"+e.getStackTrace().toString() );
						
					} finally {
						if(is != null) 
							is.close();
					}
					
					responseText = "Uploaded "+fileName + ". Created/Updated security for "+count+" processes.";
					req.getSession().setAttribute("file_uploaded", fileName);
					
					item.delete();
				}
			}
		}  catch (FileUploadException e) {
			responseText = "ERROR! Unable to upload the selected file.";      
		} catch (Exception e) {
			log.error("Error executing process security import servlet. "+e.getMessage());
			throw new RuntimeException(e);
		} finally {
			
			writeResponse(resp, responseText);
		}
		
	}
	
	private void writeResponse(HttpServletResponse resp, String responseBody) {

		resp.setContentType("text/html");
		java.io.PrintWriter out;
		try {
			out = resp.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<link type='text/css' rel='stylesheet' media='screen,projection' href='SKIN/css/default.css' /><!--[if lt IE 8]>" +
					"<link type='text/css' rel='stylesheet' media='screen,projection' href='SKIN/css/ie.css' /><![endif]-->");
			out.println("<title>File caricato!</title>");

			out.println("</head>");
			out.println("<body>");
			out.println(responseBody);
			out.println("</body>");
			out.println("</html>");

			out.close();
		} catch (IOException e) {

		}
	}
	
}