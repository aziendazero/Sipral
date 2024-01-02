package com.phi.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import it.insielmercato.firmadigitale.WrapperFirmaDigitaleMini;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.ShowMediaBean;
import com.phi.entities.act.DocRepository;
import com.phi.entities.actions.DocRepositoryAction;
import com.phi.entities.dataTypes.CodeValue;

// FIXME this servlet is pretty much the same of DownloadServlet, find the way to use just one of them
public class ShowDocRepositoryServlet extends HttpServlet {
	public void init(ServletConfig config) throws ServletException {
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

	protected void service(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		new ContextualHttpServletRequest(req) {

			@Override
			public void process() throws Exception {
				doWork(req, resp);
			}
		}.run();

	}

	/**
	 * Moved from inside dContextualHttpServletRequest, where it was a class
	 * method, to here, as method of ImportServlet class
	 * 
	 * @param req
	 * @param resp
	 */
	public void doWork(HttpServletRequest req, HttpServletResponse resp) {
		try {
			ServletOutputStream out = resp.getOutputStream();
			String convIdParam = req.getParameter("cid");
			String fileAbsPath = req.getParameter("filename");
			String fileRelPath = req.getParameter("relfilename");
			String docId = req.getParameter("docid");
			boolean isTemporary = Boolean.parseBoolean(req.getParameter("inTemporary"));
			
			Manager.instance().switchConversation(convIdParam);
			DocRepositoryAction dra = DocRepositoryAction.instance();
			
			if (isTemporary && fileAbsPath != null && !fileAbsPath.isEmpty()) {
				Object attach = dra.getTemporary().get(fileAbsPath);
				if (attach instanceof byte[]) {
					byte[] content = (byte[])attach;
					if (dra.isBytePdf(content)) {
						resp.setContentType("application/pdf");
					} else {
						resp.setContentType("application/octet-stream");
					}
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//					resp.setHeader( "Content-Disposition", "attachment; filename=\"" + sdf.format(new Date()) + "_DownloadedPDF.pdf\"" );
					resp.setHeader( "Content-Disposition", null);
					resp.setHeader("Expires", "0");
					resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					out.write(content);
					out.flush();
					out.close();
				}
			} else {

				//check if DocRepository is inject in conversation tnx of convIdParam
				DocRepository docrepos = dra.getEntity();

				//useful when docrepos is not in conversation
				if (docrepos==null && (docId != null && !"".equals(docId))){
					docrepos = dra.read(Long.parseLong(docId));
				}

				if (docrepos != null && fileAbsPath != null && !fileAbsPath.isEmpty()) {
					if (docrepos.getContent() != null) {
						// retrieve from the document the content to display
						byte[] content = null;
						CodeValue contentType = docrepos.getContentType();

						// add response content type
						if (contentType != null && 
								("PDF".equals (contentType.getCode()) ||
										"PDF_PKCS7".equals (contentType.getCode()) ||
										"PDF_ADOBE".equals (contentType.getCode()))) {
							resp.setContentType("application/pdf");
						}
						else {
							resp.setContentType("application/octet-stream");
						}
						resp.setHeader("Content-Disposition", null);
						resp.setHeader("Expires", "0");
						resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
						resp.setHeader("Pragma", "public");						

						// verify signed document
						if (contentType != null && "PDF_PKCS7".equals (contentType.getCode())) {

							String basicRoot = Thread.currentThread()
									.getContextClassLoader().getResource(".").getPath();							

							// verify document
							WrapperFirmaDigitaleMini wrapper = new WrapperFirmaDigitaleMini();
							wrapper.setProperty("ImageFile", basicRoot + req.getContextPath()+"_BACKEND.jar/pknet_InsielMercato_1.9.15.49b.gif.p7m");
							//wrapper.setProperty("ImageFile", basicRoot + req.getContextPath()+"_BACKEND.jar/pknet_Insiel_mercato.1.9.31.gif.p7m");
							wrapper.setProperty("SignatureVerification", "0");
							try {
								//write document to verify if it signed.
								//								FileOutputStream fileOuputStream = new FileOutputStream(basicRoot + req.getContextPath()+"_BACKEND.jar/test_p7m_"+docrepos.getInternalId()+".p7m"); 
								//							    fileOuputStream.write(docrepos.getContent());
								//							    fileOuputStream.close();

								wrapper.init();
								ByteArrayOutputStream baos = new ByteArrayOutputStream ();
								wrapper.verify(new ByteArrayInputStream(docrepos.getContent()), baos);
								content = baos.toByteArray();
							}
							finally {
								wrapper.close();
							}

						} else {
							content = docrepos.getContent();
						}
						// write the content with the response
						out.write(content);
						out.flush();
					} else {
						resp.setCharacterEncoding("UTF-8");
						out.print(new String("Error during doc [+" + docId
								+ "] CONTENT loading from DB".getBytes("UTF-8")));
					}
					out.close();
				} else if (fileAbsPath != null && !fileAbsPath.isEmpty() || fileRelPath != null && !fileRelPath.isEmpty()) {
					// First get the file InputStream using
					// ServletContext.getResourceAsStream() method.
					// FileRelPath use for relative path using relFileName when i need full relative path to open file
					if (fileRelPath != null && !fileRelPath.isEmpty()) {
						fileAbsPath = getServletContext().getRealPath(fileRelPath);
					}
					File docFile = new File(fileAbsPath);
//					String mimeType = null;
					if (docFile.exists()) {
//						mimeType = new MimetypesFileTypeMap().getContentType(docFile);
						InputStream is = new FileInputStream(fileAbsPath);
						if (is != null) {
//							resp.setContentType(mimeType);
//							byte[] buff = new byte[2048];
//							int bytesRead;
//							while (-1 != (bytesRead = is.read(buff, 0, buff.length))) {
//								out.write(buff, 0, bytesRead);
//							}
							byte[] content = IOUtils.toByteArray(is);
							if (dra.isBytePdf(content)) {
								resp.setContentType("application/pdf");
							} else {
								resp.setContentType("application/octet-stream");
							}
							resp.setHeader( "Content-Disposition", null);
							resp.setHeader("Expires", "0");
							resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
							out.write(content);
							out.flush();
						}
						out.close();
						is.close();
					}
				} else {
					resp.setCharacterEncoding("UTF-8");
					out.print(new String("Error document not found".getBytes("UTF-8")));
					out.close();
				}
			}
		} catch (PhiException e) {
			log.error(e);
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}

	}

}
