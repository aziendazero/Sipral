package com.phi.servlet;

//import it.insielmercato.firmadigitale.WrapperFirmaDigitale;
//import it.insielmercato.firmadigitale.WrapperFirmaDigitaleMini;
import it.pkbox.client.PKBoxException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.omg.CORBA.portable.ApplicationException;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.exception.PhiException;
import com.phi.entities.act.DocRepository;
import com.phi.entities.actions.DocRepositoryAction;
import com.phi.security.UserBean;


/**
 * @author Russian
 * 
 *         Digital signature servlet meant to work with Intesi PkNet applet.
 * 
 * 
 */
public class PkNetSigner extends HttpServlet {

	private static final long serialVersionUID = 4360946840405798481L;
	private java.io.PrintWriter out;
    private Logger logger = Logger.getLogger(this.getClass().getName());

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

//	@SuppressWarnings("unchecked")
	public void doWork(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ApplicationException, PKBoxException, PhiException {

		String convIdParam = request.getParameter("cid");
		String signedBy = request.getParameter("s");
		
		Manager.instance().switchConversation(convIdParam);

		String basicRoot = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
//		RepositoryAMB repoz = (RepositoryAMB) Component.getInstance("repositoryAMB");
//		Properties properties = repoz.getSeamProperties();
//		String locup = properties.getProperty("upload.path");
		String basePath = basicRoot; //+ properties.getProperty("solution_name")+".war/";
//		if (locup == null) {
//			locup = "uploads/";
//		} else if (locup != null) {
//			if(locup.endsWith("/") == false && 
//					locup.endsWith("\\") == false) {
//				locup += "/";
//			}
//		}

		String solutionName = request.getContextPath();
		UserBean ub = UserBean.instance();
		String username = ub.getUsername();
		DocRepositoryAction docreposAct = DocRepositoryAction.instance();

		out = response.getWriter();

		//Java Web start version doesent return this headers
		// check if pkNet applet returned errors
//		String appletError = request.getHeader("Error");
//		if (appletError == null)
//			appletError = "";

//		String appletErrorDesc = request.getHeader("ErrorDesc");
//		if (appletErrorDesc == null)
//			appletErrorDesc = "";

		//Java Web start version doesent return this header
//		if (!"0".equals(appletError)) {
//			response.setContentType("text/html");
//			out.println(response.encodeURL(request.getScheme() + "://"
//					+ request.getServerName() + ":" + request.getServerPort()
//					+ "/" + solutionName+ "/common/signature/error.seam;jsessionid=" + request.getSession().getId() + "?cid="+convIdParam+"&errordesc="+appletErrorDesc));
//
//			out.close();
//			docreposAct.getTemporary().put("signError", true);
//			return;
//		}
			try{
				// Get the envelope length
				String EnvelopeLen = request.getHeader("content-length");
				if (EnvelopeLen != null && EnvelopeLen.length() > 2) {
					// Get envelope stream
					InputStream envelopes = null;
					envelopes = request.getInputStream();

					// Extract envelope data
					byte[] envelope = new byte[Integer.parseInt(EnvelopeLen)];
					int toBeRead = envelope.length;
					int offset = 0;
					int read;

					while (toBeRead > 0) {
						read = envelopes.read(envelope, offset, toBeRead);
						offset += read;
						toBeRead -= read;
					}


					DocRepository docrepos = docreposAct.getEntity();
					//FOOTER ADD
					if (request.getParameter("stop") == null && docrepos!=null) {
						if (logger.isDebugEnabled())  
							saveByteOnFile(envelope,basePath,"envelopePreVerify_"+docrepos.getInternalId()+"_"+new Date().getTime() + ".pdf");
						
						//REMOVED, now javaWebStart PkNet adds footer
//						com.intesi.pknet.VerifyInfo  vi = veryfyCertificatePkNet(envelope,request);
//						logger.info("Status verification: " + vi.toString());
//
//						VerifyInfo verifyInfo = new VerifyInfo(vi);						
//						if (verifyInfo.getSignerCount() <= 0) {
//							throw new Exception ("signer not found");
//						}
//
//						if (docrepos.getSignDocDate()==null){
//							docrepos.setSignDocDate(new Date());
//						}		
//						
//						// get patient language
//						CodeValuePhi language = null;
//						if (docrepos.getPatient() != null) {
//							language = docrepos.getPatient().getLanguage();
//						}						
//						
//						DocSigner docSigner = new DocSigner();
//						byte[] docWithFooter = AddPdfFooter.addFooter(docrepos, 
//								verifyInfo.getSigner().get(0), 
//										docrepos.getSignDocDate(), language, docSigner.getFooterDefault(),
//										docSigner.getFooterRectangle_llx(), docSigner.getFooterRectangle_lly(),
//										docSigner.getFooterRectangle_urx(), docSigner.getFooterRectangle_ury(), docSigner.getFooterFontSize());
//
//
////						if (logger.isDebugEnabled()) 
//							saveByteOnFile(docWithFooter,basePath,"postFooter_"+docrepos.getInternalId()+"_"+new Date().getTime());
						
						//insert doc with FOOTER that will be signed in the next call of this servlet done by PkNet APPLET
						//No docVesion ++ is required beacuse is not signed
//						docrepos.setContent(docWithFooter);
							
						docrepos.setContent(envelope);
						docreposAct.setCodeValue("contentType", "PHIDIC", "PHIDOCContentType", "PDF");
						docreposAct.setCodeValue("completionStatus", "PHIDIC", "PHIDOCCompletionStatus", "LA");
						docrepos.setSignedBy(signedBy);
						docrepos.setSignDocDate(new Date());
						docreposAct.create(docrepos);
						CatalogPersistenceManagerImpl.instance().flushSession();
						// SERVLET RESPONSE
						response.setContentType("text/html");

						// NEW POST URL (mandatory) for SIGNed PDF WITH FOOTER
						out.println(response.encodeURL(request.getScheme() + "://"
								+ request.getServerName() + ":" + request.getServerPort()
								+ request.getContextPath() + "/PkNetSigner?&cid=" + convIdParam + "&savePath="
								+ request.getParameter("savePath")
								+ "&stop=true&solutionName=" + solutionName
								+ "&username=" + username));

						// NEW ACTION (mandatory)
						out.println("Action=SIGN");

						// NEW DOCUMENT URL (mandatory)
						out.println("DocumentURL="
								+ request.getScheme()
								+ "://"
								+ request.getServerName()
								+ ":"
								+ request.getServerPort()
								+ request.getContextPath()
								+ "/ShowDocRepositoryServlet?cid="+convIdParam+"&filename="+docrepos.getFileName()+"&docid="+docrepos.getInternalId());

						out.println("Message=Firma in corso!");

					}else if (docrepos!=null){
						int dVer = 0;
						//content signed 
//						if(docrepos.getDocVersion() != null) {
							dVer = docrepos.getDocVersion();
							dVer++;
//						} 
						docrepos.setDocVersion(dVer);
						docrepos.setContent(envelope);
						docreposAct.setCodeValue("contentType", "PHIDIC", "PHIDOCContentType", "PDF_PKCS7");
						docreposAct.setCodeValue("completionStatus", "PHIDIC", "PHIDOCCompletionStatus", "LA");														
						docreposAct.create(docrepos);
						CatalogPersistenceManagerImpl.instance().flushSession();
						if (logger.isDebugEnabled()) 
							saveByteOnFile(envelope,basePath,"signed_"+docrepos.getInternalId()+"_"+new Date().getTime());

						//useful to return the signed pdf name that will be used by ShowDocumentServlet to show
						out.println(response.encodeURL(request.getScheme() + "://"
								+ request.getServerName() + ":" + request.getServerPort()
								+ "/" + request.getContextPath()
								+ "/common/signature/selfClose.seam;jsessionid=" + request.getSession().getId() + "?cid=" + convIdParam +"&docid="+docrepos.getInternalId()+"&target=_blank" ));

					}else{
						logger.error("docrepos is null!");
					}

				}
			/*} catch (PKNetException e1) {
				logger.error("PkNetEx",e1);
				out.println(response.encodeURL(request.getScheme() + "://"
						+ request.getServerName() + ":" + request.getServerPort()
						+ "/" + request.getContextPath()
						+ "/common/signature/error.seam?errordesc=" + e1.getMessage()));*/
			} catch (Exception e2) {
				logger.error("Ex",e2);
				out.println(response.encodeURL(request.getScheme() + "://"
						+ request.getServerName() + ":" + request.getServerPort()
						+ "/" + request.getContextPath()
						+ "/common/signature/error.seam;jsessionid=" + request.getSession().getId() + "?cid="+convIdParam + "&errordesc=" + e2.getMessage()));
				docreposAct.getTemporary().put("signError", true);
			}finally{
				out.close();
			}
//		}
	}

	/**
	 * Extracts data from Certificate, verifying the signature of the pdf
	 * 
	 * @param inputStr : pdf doc byte[]
	 * @param request : useful to calculate URL for pkNet Licence
	 * @return List of information extract by certificate (getFirmatario, cert getSerialNumber, getEnteCertificatore)
	 * @throws PKNetException
	 */
//	private com.intesi.pknet.VerifyInfo veryfyCertificatePkNet(byte[] inputStr,HttpServletRequest request) /*throws PKNetException */{		
//		com.intesi.pknet.VerifyInfo vi = null;
//		try {
////			Properties prop = new Properties();
//			// PkNet initialization via pkNet Wrapper made by IM
//			WrapperFirmaDigitaleMini gestoreFirma = new WrapperFirmaDigitaleMini();
//			//gestoreFirma.setProperty("ImageFile","http://"+request.getServerName()+":"+request.getServerPort()+"/"+request.getContextPath()+"/common/signature/pknet_Insiel_mercato.1.9.15.gif.p7m");
//			gestoreFirma.setProperty("ImageFile","http://"+request.getServerName()+":"+request.getServerPort()+"/"+request.getContextPath()+"/common/signature/pknet_InsielMercato_1.9.15.49b.gif.p7m");
//			gestoreFirma.setProperty("SignatureVerification", "3");
//			gestoreFirma.setProperty("StatusVerification", "1");
//			gestoreFirma.setProperty("Log", "true");
//			gestoreFirma.setProperty("LogFile", "pknet.log");
//			gestoreFirma.init();
//			
////			com.intesi.pknet.Envelope envelope = new com.intesi.pknet.Envelope();
////
////			vi = envelope.verify(null, inputStr);//.pdfverify(inputStr);
//			
//			vi = gestoreFirma.pdfverify(new ByteArrayInputStream(inputStr));
//			
//		} catch (Exception ex) {
//			logger.error("Read certificate data:", ex);
//		} catch (Throwable t) {
//			t.printStackTrace();
//			logger.error("Read certificate data:", t);
//		}/*finally {
//			PKNet.Finalize();
//		}*/
//		return vi;
//	}

	/**
	 * Utility method for DEBUG only
	 * 
	 * @param content
	 * @param path
	 * @param name
	 */
	private void saveByteOnFile(byte[] content, String path,String name){
		try {
			FileOutputStream fileOut = null;
			fileOut = new FileOutputStream(path+name);				
			fileOut.write(content, 0, content.length);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	private void writeResponse(HttpServletResponse response, HttpServletRequest request, String responseBody) {
//		try {
//			try {
//				out.println("<html>");
//				out.println("<head>");
//				out.println("<title>PHI</title>");
//				out.println("</head>");
//				out.println("<body>");
//				out.println(responseBody);
//				out.println("</body>");
//				out.println("</html>");
//				
//
//
//			} catch (Exception e) {
//				logger.error("Error writing response", e);
//			}
//		} catch (Exception e) {
//			logger.error("Error during login",e);
//		}
//	}

}
