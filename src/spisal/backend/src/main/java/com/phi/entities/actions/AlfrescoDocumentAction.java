package com.phi.entities.actions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.ParereTecnico;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.SignedDocument;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.odt.SpisalOdtEngine;
import com.phi.parameters.ParameterManager;
import com.phi.rest.ReportRest;
import com.phi.security.SpisalUserAction;
import com.phi.security.UserBean;

/**
 * 
 * Alfresco Object manages Rest and CIMS methods
 * login, browse, search, history and save
 * Api doc: http://im-spisallx:8080/alfresco/service/index/lifecycle/
 * Web Scripts Home: http://im-spisallx:8080/alfresco/service/index
 * Node browser: http://im-spisallx:8080/alfresco/s/admin/admin-nodebrowser
 * 
 */

@BypassInterceptors
@Name("AlfrescoDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoDocumentAction extends BaseAction<AlfrescoDocument, Long> {

	private static final long serialVersionUID = 1314193043L;
	private static final Logger log = Logger.getLogger(AlfrescoDocumentAction.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String contentTypeJson = "application/json; charset=UTF-8";

	public static AlfrescoDocumentAction instance() {
		return (AlfrescoDocumentAction) Component.getInstance(AlfrescoDocumentAction.class, ScopeType.CONVERSATION);
	}

	private static String alfrescoUrl;
	private static String alfrescoUsername;
	private static String alfrescoPassword;

	private static String alfrescoLoginUrl;
	private static String alfrescoUploadUrl;
	private static String alfrescoGetById;
	private static String alfrescoBrowse;
	private static String alfrescoCreateFolderUrl;
	private static String alfrescoDeleteUrl;

	private String alfrescoTicket = null;
	private String alfrescoTicketHeader = null;

	static {
		try {
			ParameterManager pm = ParameterManager.instance();
			alfrescoUrl = pm.getParameter("p.general.alfrescoUrl", "value").toString();
			alfrescoUsername = pm.getParameter("p.general.alfrescoUsername", "value").toString();
			alfrescoPassword = pm.getParameter("p.general.alfrescoPassword", "value").toString();

			if (alfrescoUrl == null || alfrescoUsername == null || alfrescoPassword == null) {
				log.warn("Alfresco parameters not configured!");
			}

			alfrescoLoginUrl = 			alfrescoUrl + "/alfresco/service/api/login";
			alfrescoUploadUrl = 		alfrescoUrl + "/alfresco/service/api/upload";
			alfrescoGetById = 			alfrescoUrl + "/alfresco/service/api/node/content/";
			alfrescoBrowse = 			alfrescoUrl + "/alfresco/api/-default-/public/cmis/versions/1.1/browser/root";
			alfrescoCreateFolderUrl = 	alfrescoUrl + "/alfresco/service/api/node/folder/";
			alfrescoDeleteUrl =		 	alfrescoUrl + "/alfresco/service/api/archive/";

		} catch (PhiException e) {
			log.error("Error getting alfresco parameters: p.general.alfrescoUrl or p.general.alfrescoUsername or p.general.alfrescoPassword", e);
		}
	}

	public boolean isArpav(){
		boolean isArpav = false;
		try{
			SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
			isArpav = sua.isArpav();
		} catch (Exception ex) {
			log.error(ex);
		}		
		return isArpav;
	}

	public void updateNrUpload(AlfrescoDocument document){
		Integer nr = document.getNrUpload();

		if(nr == null){
			nr = 0;
		}

		document.setNrUpload(nr + 1);
	}
	
	public void applyExtension(AlfrescoDocument document, String extension) {
		if (!document.getName().endsWith(extension)) {
			document.setName(document.getName() + extension);
		}
	}

	public void initDoc(AlfrescoDocument document, AlfrescoDocument template, String prefix) {
		if (template == null) {
			document.setName("");
			document.setDescription("");

		} else {
			if (template.getName() != null) {
				String templateName = template.getName();
				if(isArpav()){
					prefix = prefix.toUpperCase();
					templateName = templateName.toUpperCase();
				}
				String docName = checkName(prefix, templateName);

				//document.setName(docName.replaceAll("[^a-zA-Z0-9\\.\\-\\s]", "_"));
				document.setName(docName.replaceAll("/", ""));
				
				if(isArpav()){
					applyExtension(document, ".ODT");
				}else{
					applyExtension(document, ".odt");
				}

				if (template.getName().contains(".")) {
					document.setDescription(template.getName().substring(0, template.getName().lastIndexOf(".")));
				} else {
					document.setDescription(template.getName());
				}
			} else {
				document.setName(prefix);
			}
		}
	}

	public String checkName(String prefix, String templateName) { 
		int count = 0;
		Boolean exist = true;
		String nameToCheck = "";

		try {
			/* Prefix: Pratica Medicina del lavoro 4654
			 * templateName: XYZ.odt
			 * 
			 * docName: prefix + " - " + templateName >>--> Pratica Medicina del lavoro 12345_X - XYZ.odt */  

			do {
				count++;
				String newPrefix = "";

				if (count==1)
					newPrefix = prefix;
				else 
					newPrefix = prefix + "_" + count;					

				nameToCheck = (newPrefix + " - " + templateName).replaceAll("/", "");

				Query q = ca.createQuery("select ad.internalId from AlfrescoDocument ad where ad.name = :nameToCheck");
				q.setParameter("nameToCheck", nameToCheck);

				List<AlfrescoDocument> list = q.getResultList();
				if(list==null || list.size()==0) {
					log.info("Checked name: " + nameToCheck + " - OK!");
					exist = false;
				} else {
					log.info("Checked name: " + nameToCheck + " - Existing!");
				}

			} while (exist);


		}catch(Exception e){
			log.error(e.getMessage());
		}

		return nameToCheck;

	}

	/**
	 * Generate document from template and save it into alfresco
	 * @param template from wich document is generated
	 * @param path of new document into alfresco
	 * @param document save alfresco id (noderef)
	 * @return
	 * @throws Exception
	 */
	public byte[] generateFromTemplateAndSave(AlfrescoDocument template, String path, AlfrescoDocument document) throws Exception {

		byte[] templateBytes = alfrescoGet(template.getNodeRefUrl());

		SpisalUserAction sua = null;
		try{
			sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
		} catch (Exception ex) {
			log.error(ex);
		}

		SpisalOdtEngine odtE = SpisalOdtEngine.instance();
		byte[] odtBarr = null;
		if(sua!=null && sua.isArpav()){

			boolean noBindings = (Boolean)getTemporary().get("noBindings")!=null ? (Boolean)getTemporary().get("noBindings") : false;
			if(noBindings){
				odtBarr = odtE.generateUnfilledBody(templateBytes, "ARPAV");
			}else{
				odtBarr = odtE.generateFromTemplate(templateBytes, "ARPAV");
			}
		}else{
			odtBarr = odtE.generateFromTemplate(templateBytes, "ULSS");
		}

		if (odtBarr == null){
			getTemporary().put("brokenGeneration",true);
			return null;
		}
		else {
			getTemporary().put("brokenGeneration",false);
		}

		String folderNodeRef = alfrescoGetOrCreateFolder(path, path, null);

		String newNodeRef = alfrescoUploadNew(odtBarr, folderNodeRef, document.getName());

		document.setNodeRef(newNodeRef);
		document.setPath(path);
		document.setFromTemplate(true);

		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue defaultStatus = voc.getCodeValue("PHIDIC", "documentstatus", "1", "C");
		if(defaultStatus!=null) defaultStatus=(CodeValuePhi) defaultStatus;
		document.setDocumentStatus((CodeValuePhi) defaultStatus);
		document.setSignaturesReqN(template.getSignaturesReqN());
		document.setSignaturesPresent(0);

		return odtBarr;
	}



	/**
	 * Replaces styles.xml inside document.odt with styles.xml of model.odt
	 * 
	 * see: http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html
	 * 
	 * @param modelNodeRef alfresco id of model that contains header and footer
	 * @throws PhiException 
	 */
	public void applyModelToDocument(String modelNodeRef) throws PhiException  {

		try {
			SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
			boolean isArpav = sua.isArpav();

			byte[] modelBarr = alfrescoGet(modelNodeRef);
			byte[] documentBarr = alfrescoGet(getEntity().getNodeRefUrl());
			SpisalOdtEngine odtE = SpisalOdtEngine.instance();

			if (!getEntity().getIsTemplate()) {
				//If isn't template generate doc
				if(isArpav){
					modelBarr = odtE.generateFromTemplate(modelBarr, "ARPAV");
				}else{
					modelBarr = odtE.generateFromTemplate(modelBarr, "ULSS");
				}
			}

			byte[] newDocumentBarr = null;
			if(isArpav){
				newDocumentBarr = odtE.applyModelFirstPageToDocument(modelBarr, documentBarr);
			}else{
				newDocumentBarr = odtE.applyModelToDocument(modelBarr, documentBarr);
			}

			alfrescoUploadUpdate(newDocumentBarr, getEntity().getNodeRef());
		} catch (Exception e) {
			throw new PhiException("Error applying model noderef " + modelNodeRef + " to document with noderef " + entity.getNodeRef(), 
					e, ErrorConstants.GENERIC_ERR_CODE);
		}
	}

	public void applyModelToDocumentList(List<AlfrescoDocument> al,String modelNodeRef) throws PhiException  {

		for (AlfrescoDocument ad : al) {
			if(ad.getNodeRef()!=null && ad.getNodeRefUrl()!=null){
				if(ad.getIsSelected()!=null && ad.getIsSelected()==true){


					try {
						byte[] modelBarr = alfrescoGet(modelNodeRef);
						byte[] documentBarr = alfrescoGet(ad.getNodeRefUrl());
						SpisalOdtEngine odtE = SpisalOdtEngine.instance();
						SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");

						if (!ad.getIsTemplate()) {
							//If isn't template generate doc
							if(sua.isArpav()){
								modelBarr = odtE.generateFromTemplate(modelBarr, "ARPAV");
							}else{
								modelBarr = odtE.generateFromTemplate(modelBarr, "ULSS");
							}
						}


						byte[] newDocumentBarr = odtE.applyModelToDocument(modelBarr, documentBarr);

						alfrescoUploadUpdate(newDocumentBarr, ad.getNodeRef());
					} catch (Exception e) {
						throw new PhiException("Error applying model noderef " + modelNodeRef + " to document with noderef " + ad.getNodeRef(), 
								e, ErrorConstants.GENERIC_ERR_CODE);
					}


				}

			}else{
				log.warn(ad.getName() + " has a null nodeRef or nodeRefUrl");
			}
		}

	}

	/**
	 * Fix Template: removes all styles from inside ${ ... } and [# ... ]
	 * 
	 * @param model AlfrescoDocument of model to fix
	 * @throws PhiException 
	 */
	public void fixModel(AlfrescoDocument model) throws PhiException {

		log.warn("Fixing model '" + model.getName() + "' nodeRef: " + model.getNodeRef());

		try {
			byte[] modelBarr = alfrescoGet(model.getNodeRefUrl());

			SpisalOdtEngine odtE = SpisalOdtEngine.instance();

			byte[] newModelBarr = odtE.fixModel(modelBarr);

			//For debug:
			//FileUtils.writeByteArrayToFile(new File("fixed.odt.zip"), newModelBarr);

			alfrescoUploadUpdate(newModelBarr, model.getNodeRef());

		} catch (Exception e) {
			throw new PhiException("Error fixing model '" + model.getName() + "' nodeRef: " + model.getNodeRef(), 
					e, ErrorConstants.GENERIC_ERR_CODE);
		}
	}



	/**
	 * alfresco login
	 * @throws PhiException 
	 */
	@SuppressWarnings("unchecked")
	public void alfrescoLogin() throws PhiException {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(alfrescoLoginUrl);
			post.addHeader("Content-Type", contentTypeJson);
			StringEntity data = new StringEntity("{\"username\":\"" + alfrescoUsername + "\",\"password\":\"" + alfrescoPassword + "\"}", "UTF-8");
			post.setEntity(data);


			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(post);
				HttpEntity entity = response.getEntity();
				String content = getContent(response);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Alfresco login failed : status " + response.getStatusLine().getStatusCode() + " error: " + content);
				}

				Map<String, Object> res = mapper.readValue(content, Map.class);

				Map<String, Object> resData = (Map<String, Object>)res.get("data");
				alfrescoTicket = resData.get("ticket").toString();
				String toEncode = ":" + alfrescoTicket;
				alfrescoTicketHeader = "Basic " + Base64.encodeBase64String(toEncode.getBytes("UTF-8"));

				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}

		} catch (Exception e) {
			log.error("Alfresco login failed", e);
			FacesErrorUtils.addErrorMessage(ErrorConstants.WEB_SERVICE_ERR_CODE, "Alfresco login failed " + e.getMessage());
		}
	}

	public String getAlfrescoTicket() {
		return alfrescoTicket;
	}

	/**
	 * alfresco upload new file
	 * @param document byte array
	 * @param folderNodeRef alfresco id of folder
	 * @param fileName name of the file
	 */
	public String alfrescoUploadNew(byte[] document, String folderNodeRef, String fileName) throws PhiException {
		return alfrescoUpload(document, folderNodeRef, null, fileName);
	}

	/**
	 * alfresco upload an update to an existing file
	 * @param document byte array
	 * @param documentNodeRef alfresco id of an existing document
	 */
	public String alfrescoUploadUpdate(byte[] document, String documentNodeRef) throws PhiException {
		return alfrescoUpload(document, null, documentNodeRef, "blob");
	}

	/**
	 * alfresco upload new file or update an existing file
	 * @param document byte array
	 * @param folderNodeRef if != null -> upload new file
	 * @param documentNodeRef if != null -> update to an existing file
	 * @param fileName
	 * @return
	 */
	private String alfrescoUpload(byte[] document, String folderNodeRef, String documentNodeRef, String fileName) throws PhiException {
		String nodeRef = null;
		try {

			if (alfrescoTicket == null) {
				alfrescoLogin();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(alfrescoUploadUrl);
			post.addHeader("Authorization", alfrescoTicketHeader);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("filedata", document, ContentType.create("application/vnd.oasis.opendocument.text"), fileName);

			if (folderNodeRef != null) {
				//upload new file
				builder.addTextBody("destination", folderNodeRef, ContentType.TEXT_PLAIN);
				builder.addTextBody("filename", fileName);
			} else if (documentNodeRef != null) {
				//update an existing file
				builder.addTextBody("updateNodeRef", documentNodeRef, ContentType.TEXT_PLAIN);
			} else {
				throw new IllegalArgumentException("Unable to upload a file to Alfresco: folderNodeRef, fileName and documentNodeRef are null");
			}

			post.setEntity(builder.build());

			CloseableHttpResponse response = httpClient.execute(post);

			try {
				HttpEntity entity = response.getEntity();
				String content = getContent(response);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Alfresco upload failed : status " + response.getStatusLine().getStatusCode() + " error: " + content);
				}

				@SuppressWarnings("unchecked")
				Map<String, Object> res = mapper.readValue(content, Map.class);
				nodeRef = res.get("nodeRef").toString();

				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}

		} catch (Exception e) {
			throw new PhiException("Alfresco upload failed folderNodeRef " + folderNodeRef + " documentNodeRef " + documentNodeRef + " name " + fileName, e, ErrorConstants.WEB_SERVICE_ERR_CODE);
		}
		return nodeRef;
	}

	/**
	 * alfresco delete file
	 * delete a node by nodeRef
	 * @throws PhiException 
	 */
	public void alfrescoDelete(String nodeRef) throws PhiException {
		String nodeRefPath = nodeRef.replace("://", "/");
		try {
			if (alfrescoTicket == null) {
				alfrescoLogin();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpDelete delete = new HttpDelete(alfrescoDeleteUrl + nodeRefPath); 
			delete.addHeader("Authorization", alfrescoTicketHeader);
			CloseableHttpResponse response = httpClient.execute(delete);

			/* H0061870 - Se il documento non esiste in alfresco, non lanciare eccezione per procedere con l'eliminazione dell'entity
			if (response.getStatusLine().getStatusCode() != 200) { */
			if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 404) {
				throw new RuntimeException("Alfresco delete failed : " + response.getStatusLine().getStatusCode());
			}

			try {
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			throw new PhiException("Alfresco error deleting" + nodeRef, e, ErrorConstants.WEB_SERVICE_ERR_CODE);
		}
	}


	public void delete(){

		try {

			List<SignedDocument> sds = getEntity().getSignedDocument();
			SignedDocumentAction sda = new SignedDocumentAction();
			if(sds!=null){
				for(int i=0;i<sds.size();i++){
					sda.entity=sds.get(i);
					sda.delete();

				}
			}

			super.delete();
		} catch (PhiException e) {
			log.error(e.getMessage());
		}

	}


	/**
	 * alfresco get document
	 * @param nodeRef id in Alfresco
	 * @return document InputStream
	 * @throws PhiException 
	 */
	public byte[] alfrescoGet(String nodeRef) throws PhiException {
		byte[] bytes = null;
		try {

			if (alfrescoTicket == null) {
				alfrescoLogin();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet get = new HttpGet(alfrescoGetById + nodeRef + "?alf_ticket=" + alfrescoTicket); 
			CloseableHttpResponse response = httpClient.execute(get);

			try {
				HttpEntity entity = response.getEntity();
				InputStream odtIs = response.getEntity().getContent();
				bytes = IOUtils.toByteArray(odtIs);
				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			throw new PhiException("Alfresco get failed " + nodeRef, e, ErrorConstants.WEB_SERVICE_ERR_CODE);
		}
		return bytes;
	}


	/**
	 * alfresco find nodeRef of folder with path
	 * @param path
	 * @throws PhiException 
	 */
	@SuppressWarnings("unchecked")
	private String alfrescoGetFolderNoderef(String path) throws PhiException {
		String nodeRef = null;
		try {
			if (alfrescoTicket == null) {
				alfrescoLogin();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			path = path.replace(" ", "+");//URLEncoder.encode(path, "UTF-8");
			HttpGet get = new HttpGet(alfrescoBrowse + path + "?cmisselector=object"); //"&alf_ticket=" + alfrescoTicket );
			get.addHeader("Authorization", alfrescoTicketHeader);
			CloseableHttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() == 404) {
				return null;
			}

			try {
				HttpEntity entity = response.getEntity();
				String content = getContent(response);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Alfresco getFolder failed : status " + response.getStatusLine().getStatusCode() + " error: " + content);
				}

				Map<String, Object> res = mapper.readValue(content, Map.class);
				nodeRef = ((Map<String, Object>)((Map<String, Object>)res.get("properties")).get("alfcmis:nodeRef")).get("value").toString();

				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}

		} catch (Exception e) {
			throw new PhiException("Alfresco getFolderNoderef failed, path: " + path, e, ErrorConstants.WEB_SERVICE_ERR_CODE);
		}
		return nodeRef;
	}

	/**
	 * alfresco createFolder
	 * create folder with path
	 * @throws PhiException 
	 */
	private String alfrescoCreateFolder(String parentFolderNodeRef, String folderName) throws PhiException {
		String parentFolderNodeRefPath = parentFolderNodeRef.replace("://", "/");
		String nodeRef = null;
		try {

			if (alfrescoTicket == null) {
				alfrescoLogin();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(alfrescoCreateFolderUrl + parentFolderNodeRefPath);
			post.addHeader("Authorization", alfrescoTicketHeader);
			post.addHeader("Content-Type", contentTypeJson);
			StringEntity data = new StringEntity("{\"name\":\"" + folderName + "\"}", "UTF-8");
			post.setEntity(data);

			CloseableHttpResponse response = httpClient.execute(post);

			try {
				HttpEntity entity = response.getEntity();
				String content = getContent(response);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Alfresco createFolder failed : status " + response.getStatusLine().getStatusCode() + " error: " + content);
				}

				@SuppressWarnings("unchecked")
				Map<String, Object> res = mapper.readValue(content, Map.class);
				nodeRef = res.get("nodeRef").toString();

				EntityUtils.consume(entity);
			} finally {
				if (response != null) {
					response.close();
				}
			}

		} catch (Exception e) {
			throw new PhiException("Alfresco createFolder failed parentFolder " + parentFolderNodeRef + " folderName " + folderName, 
					e, ErrorConstants.WEB_SERVICE_ERR_CODE);
		}
		return nodeRef;
	}

	/**
	 * alfresco get or create folder
	 * find nodeRef of folder with path, if doesen't exsist create it.
	 * @throws PhiException 
	 */
	private String alfrescoGetOrCreateFolder(String fullPath, String currentPath, String nodeRef) throws PhiException {
		String folderNodeRef = nodeRef;
		if (nodeRef == null) {
			folderNodeRef = alfrescoGetFolderNoderef(currentPath);
		}

		if (folderNodeRef != null) { //exsist

			if (fullPath.equals(currentPath)) {
				return folderNodeRef;
			} else {
				String relativePath = fullPath.substring(currentPath.length() + 1, fullPath.length());
				int nextSlash = relativePath.indexOf('/',1);
				if (nextSlash != -1) {
					relativePath = relativePath.substring(0, relativePath.indexOf('/',1));
				}
				String newNodeRef = alfrescoCreateFolder(folderNodeRef, relativePath);

				return alfrescoGetOrCreateFolder(fullPath, currentPath + "/" + relativePath, newNodeRef);
			}

		} else { //not exsists

			int lastSlash = currentPath.lastIndexOf('/');
			String newPath = currentPath.substring(0, lastSlash);

			return alfrescoGetOrCreateFolder(fullPath, newPath, nodeRef);

		}
	}


	/**
	 * Read string from HttpResponse
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static String getContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String body = "";
		String content = "";

		while ((body = rd.readLine()) != null) 
		{
			content += body + "\n";
		}
		return content.trim();
	}

	/**
	 * Returns path of a Protocollo or Procpratiche
	 * example: /Shared/Ulss1/Spisal/Infortuni sul lavoro/2016/(Pratiche o Comunicazioni)
	 * @param Protocollo or Procpratiche
	 * @return path to save into alfresco
	 */
	public String getPath(Boolean isTemplate, Impianto impianto, VerificaImp verifica, Addebito addebito, String impOpVer, PersoneGiuridiche personeGiuridiche) {
		if (entity != null ||(impianto==null && verifica==null && addebito==null && personeGiuridiche == null && isTemplate==null)) 
			return null;

		if ((impOpVer == null || "".equals(impOpVer)) && isTemplate==null)
			return null;

		String path = null;

		if (isTemplate) {
			path = "/Shared/Template";
		} else if ("imp".equals(impOpVer)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(impianto.getCreationDate());
			path = "/Shared/Arpav/"+ cal.get(Calendar.YEAR) +"/Impianti/IntId" + impianto.getInternalId();
		} else if ("add".equals(impOpVer)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(addebito.getCreationDate());
			path = "/Shared/Arpav/"+ cal.get(Calendar.YEAR) +"/Addebiti/IntId" + addebito.getInternalId();
		} else if ("ver".equals(impOpVer)){

			//Se si proviene dalla gestione verifiche ancora non c'è l'impianto in conversation
			if (impianto==null){
				if (verifica.getImpPress()!=null)
					impianto=verifica.getImpPress();
				else if (verifica.getImpMonta()!=null)
					impianto=verifica.getImpMonta();
				if (verifica.getImpRisc()!=null)
					impianto=verifica.getImpRisc();
				if (verifica.getImpSoll()!=null)
					impianto=verifica.getImpSoll();
				if (verifica.getImpTerra()!=null)
					impianto=verifica.getImpTerra();
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(impianto.getCreationDate());
			path = "/Shared/Arpav/"+ cal.get(Calendar.YEAR) +"/Impianti/IntId" + impianto.getInternalId() +"/Verifiche/IntId" + verifica.getInternalId();
		} else if (personeGiuridiche != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(personeGiuridiche.getCreationDate());
			path = "/Shared/Arpav/"+ cal.get(Calendar.YEAR) +"/PersoneGiuridiche/IntId" + personeGiuridiche.getInternalId();
		}

		String ret = clean(path);
		return ret;
	}

	/**
	 * Returns path of a Protocollo or Procpratiche
	 * example: /Shared/Ulss1/Spisal/Infortuni sul lavoro/2016/(Pratiche o Comunicazioni)
	 * @param Protocollo or Procpratiche
	 * @return path to save into alfresco
	 */
	public String getPath(Boolean isTemplate, Protocollo proto, Procpratiche pratica/*, Attivita attivita, Soggetto soggetto, Provvedimenti provvedimenti*/) {

		if (entity != null) {
			return null;
		}

		String path = null;

		Calendar cal = Calendar.getInstance();

		if (isTemplate) {
			path = "/Shared/Template";
		} else if (pratica != null) {
			cal.setTime(pratica.getData());
			if (pratica.getServiceDeliveryLocation() == null) {//Pratica senza linea di lavoro
				path = "/Shared/" + pratica.getUoc().getParent().getOrganization().getName().getGiv() + "/Spisal/IN/" + cal.get(Calendar.YEAR) + "/Pratiche";
			} else {
				if(pratica.getNumero()!=null && !pratica.getNumero().isEmpty()){
					String numero = pratica.getNumero();
					path = "/Shared/" + pratica.getUoc().getParent().getOrganization().getName().getGiv() + "/Spisal/" + pratica.getServiceDeliveryLocation().getName().getGiv() + "/" + cal.get(Calendar.YEAR) + "/Pratiche/"+numero;
				}else{
					path = "/Shared/" + pratica.getUoc().getParent().getOrganization().getName().getGiv() + "/Spisal/" + pratica.getServiceDeliveryLocation().getName().getGiv() + "/" + cal.get(Calendar.YEAR) + "/Pratiche";
				}
			}
		} else if (proto != null) {
			cal.setTime(proto.getData());
			if (proto.getServiceDeliveryLocation() == null || proto.getServiceDeliveryLocation().getParent() == null) {//Segnalazione senza spisal
				path = "/Shared/IN/Spisal/IN/" + cal.get(Calendar.YEAR) + "/Comunicazioni";
			} else {
				if (proto.getUos() == null) {//Segnalazione appena arrivata:
					path = "/Shared/" + proto.getServiceDeliveryLocation().getParent().getName().getGiv() + "/Spisal/IN/" + cal.get(Calendar.YEAR) + "/Comunicazioni";
				} else {
					path = "/Shared/" + proto.getServiceDeliveryLocation().getParent().getName().getGiv() + "/Spisal/" + proto.getUos().getName().getGiv() + "/" + cal.get(Calendar.YEAR) + "/Comunicazioni";
				}
			}
		}
		String ret = clean(path);
		return ret;
	}

	public void setReadTemplatesFilters(Procpratiche pratica, Protocollo comunicazione, Attivita attivita, Provvedimenti provvedimento) throws Exception {

		Vocabularies voc = VocabulariesImpl.instance();

		cleanRestrictions();

		if (provvedimento != null) {
			if (provvedimento.getType() != null) {
				if ("Disp".equals(provvedimento.getType().getCode())) { //Disposizioni
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "dispositions"));
				} else if ("301bis".equals(provvedimento.getType().getCode())) { //Ex art. 301 bis
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "administrativeIllicit"));
				} else if ("758".equals(provvedimento.getType().getCode())) { //Iter 758
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "procedure758"));
				}else if ("ex14".equals(provvedimento.getType().getCode())) { //Sospensioni
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "suspension"));
				}
			}
		} else if (attivita != null) {
			getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "ELEMENTARACTIVITY"));
		} else if (pratica != null) {
			if (pratica.getServiceDeliveryLocation() != null && pratica.getServiceDeliveryLocation().getArea() != null) {
				String code = pratica.getServiceDeliveryLocation().getArea().getCode();
				if ("WORKDISEASE".equals(code)) {
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "WORKDISEASE"));
				} else if ("WORKACCIDENT".equals(code)) {
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "WORKACCIDENT"));
				} else if ("WORKMEDICINE".equals(code)) {
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "WORKMEDICINE"));	
				} else if ("SUPERVISION".equals(code)) {
					if (pratica.getVigilanza() != null && pratica.getVigilanza().getType() != null &&
							"Asbestos".equals(pratica.getVigilanza().getType().getCode())) { //Amianto
						getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "asbestos"));
					}
				} else if ("TECHNICALADVICE".equals(code)) {
					if (pratica.getParereTecnico() != null && !pratica.getParereTecnico().isEmpty()) {
						ParereTecnico pt = pratica.getParereTecnico().get(0);
						if (pt.getType() != null && "TA09".equals(pt.getType().getCode())) { // Lavoratrici madri
							getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "motherWorker"));
						} else { //Espressione pareri tecnici formalizzati
							getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "technicalAdvice"));
						}
					} else { //Espressione pareri tecnici formalizzati
						getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "technicalAdvice"));
					}
				} else {
					getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "GENERIC"));
				}
			} else {
				getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "GENERIC"));
			}

		} else {
			getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "GENERIC"));
		}
	}


	/**
	 * @return list of templates
	 */
	public List<AlfrescoDocument> readTemplates() {

		Criteria templCrit = ca.createCriteria(AlfrescoDocument.class); 

		templCrit/*.setProjection(Projections.projectionList()
	        	.add(Projections.property("internalId").as("internalId"))
	        	.add(Projections.property("name.giv").as("giv"))      
	        )*/
		.addOrder(Order.asc("name"))
		.add(Restrictions.eq("isTemplate", true));

		String name = (String)getLike().get("name");
		if (name != null) {
			templCrit.add(Restrictions.like("name", "%" + name + "%"));
		}

		CodeValuePhi type = (CodeValuePhi)getEqual().get("type");
		if (type != null ) {
			templCrit.createCriteria("type","type")
			.add(Restrictions.eq("type.id", type.getId()));
		}

		CodeValuePhi typeArpav = (CodeValuePhi)getEqual().get("typeArpav");
		if (typeArpav != null ) {
			templCrit.createCriteria("typeArpav","typeArpav")
			.add(Restrictions.eq("typeArpav.id", typeArpav.getId()));
		}

		List<Long> sdlocIds = UserBean.instance().getSdLocs();
		if (sdlocIds != null && !sdlocIds.isEmpty()) {
			templCrit.add(Restrictions.or(Restrictions.isNull("serviceDeliveryLocation.internalId"), Restrictions.in("serviceDeliveryLocation.internalId", sdlocIds)));
		}

		LazyList<AlfrescoDocument> lst = new LazyList<AlfrescoDocument>(templCrit, 10, Integer.MAX_VALUE, entityProjections, false, entityClass);
		PagedDataModel<AlfrescoDocument> dm = new PagedDataModel<AlfrescoDocument>(lst, conversationName + "Template");
		Contexts.getConversationContext().set(conversationName + "Template" + listSuffix, dm);

		return lst;
	}

	/**
	 * @return list of Header Footer models
	 */
	public List<AlfrescoDocument> readHeaderAndFooters(boolean isArpav) {

		Criteria templCrit = ca.createCriteria(AlfrescoDocument.class); 

		if(isArpav){
			templCrit/*.setProjection(Projections.projectionList()
        	.add(Projections.property("internalId").as("internalId"))
        	.add(Projections.property("name.giv").as("giv"))      
        	)*/
			.addOrder(Order.asc("name"))
			.add(Restrictions.eq("isTemplate", true))
			.createCriteria("typeArpav","typeArpav")
			.add(Restrictions.eq("typeArpav.code", "HEADERFOOTER"));
			
		}else{
			templCrit/*.setProjection(Projections.projectionList()
        	.add(Projections.property("internalId").as("internalId"))
        	.add(Projections.property("name.giv").as("giv"))      
        	)*/
			.addOrder(Order.asc("name"))
			.add(Restrictions.eq("isTemplate", true))
			.createCriteria("type","type")
			.add(Restrictions.eq("type.code", "HEADERFOOTER"));

		}

		LazyList<AlfrescoDocument> lst = new LazyList<AlfrescoDocument>(templCrit, 3, Integer.MAX_VALUE, entityProjections, false, entityClass);
		PagedDataModel<AlfrescoDocument> dm = new PagedDataModel<AlfrescoDocument>(lst, conversationName + "HeaderFooter");
		Contexts.getConversationContext().set(conversationName + "HeaderFooter" + listSuffix, dm);

		return lst;
	}


	public void ejectTemplates() {
		Contexts.getConversationContext().set(conversationName + "Template" + listSuffix, null);
		Contexts.getConversationContext().set(conversationName + "Template", null);
	}


	/**
	 * @return list ServiceDeliveryLocation with code ULSS 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getUlss() {

		Criteria sdlocs = ca.createCriteria(ServiceDeliveryLocation.class); 

		sdlocs/*.setProjection(Projections.projectionList()
	        	.add(Projections.property("internalId").as("internalId"))
	        	.add(Projections.property("name.giv").as("giv"))      
	        )*/
		.addOrder(Order.asc("name.giv"))
		.createCriteria("code","code")
		.add(Restrictions.eq("code.code", "ULSS"));

		List<ServiceDeliveryLocation> lst = sdlocs.list();


		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		for (ServiceDeliveryLocation row : lst) {
			selectItems.add(new SelectItem(row, row.getName().getGiv()));
		}

		return selectItems;
	}

	private String clean(String path) {
		String ret = path.replace("*", "").replace("\"", "").replace("'", "").replace("<", "").replace(">", "").replace(",", "").replace(".", "").replace("?", "").replace("!", "").replace("|", "").replace("#", "");

		return ret;
	}


	public void switchToPdfAndLock(){

		getTemporary().remove("signatureNotificationMessage");

		byte[] documentBytes = null;

		AlfrescoDocument original = getEntity();

		try {
			documentBytes = alfrescoGet(getEntity().getNodeRefUrl());
			ReportRest rr = new ReportRest();

			byte[] pdfBarr;
			if(!original.getMimeType().equalsIgnoreCase("application/pdf")){
				pdfBarr = rr.odtToPdf(documentBytes);
			}else{
				pdfBarr = documentBytes;
			}



			if (pdfBarr == null){
				log.error(this.getClass().getSimpleName()+": generazione pdf fallita.");
				getTemporary().put("signatureNotificationMessage", "Attenzione! Si è verificato un errore nella generazione del file .pdf. Il documento non è stato inviato alla firma.");
			}else{

				String path = original.getPath();
				if(path==null){
					getTemporary().put("signatureNotificationMessage", "Attenzione! Si è verificato un errore nella generazione del file .pdf. Non è stato possibile convertire il documento in pdf.");
					return;
				}
				String folderNodeRef = alfrescoGetOrCreateFolder(path, path, null);



				String pdfName=original.getInternalId()+"_"+System.currentTimeMillis()+".pdf";
				String uploadedPdfNodeRef = alfrescoUploadNew(pdfBarr,folderNodeRef, pdfName);

				if (uploadedPdfNodeRef != null) {

					SignedDocument signedDoc = new SignedDocument();
					SignedDocumentAction sda = new SignedDocumentAction();
					sda.entity = signedDoc;


					signedDoc.setName(pdfName);
					signedDoc.setNodeRef(uploadedPdfNodeRef);
					signedDoc.setPath(folderNodeRef);
					sda.create();
					sda.link("alfrescoDocument", original);
					link("signedDocument",signedDoc);

					original.setSignedDocumentLatest(sda.getEntity());
					// lock del documento originale (questo) in modo che poi venga nascosto e mostrata l'ultima versione PDF dello stesso
					original.setLockedForSignature(true);

					create();


				}else{
					log.error(this.getClass().getSimpleName()+": upload pdf fallito");
					getTemporary().put("signatureNotificationMessage", "Attenzione! Si è verificato un errore nella generazione del file .pdf. Il documento non è stato caricato su Alfresco.");
				}


			}

		} catch (PhiException e) {
			System.err.println(e.getMessage());			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}



	}


	public void sendToSignatureService() throws PhiException, IOException{

		AlfrescoDocument original = getEntity();
		temporary.remove("signatureNotificationMessage");

		ParameterManager pm = ParameterManager.instance();
		String signatureServiceUrl = null;
		try {
			signatureServiceUrl = pm.getParameter("p.general.signature.signatureurl", "value").toString();
		} catch (Exception e) {
			log.error(e.getMessage());


			if(signatureServiceUrl==null || signatureServiceUrl.isEmpty()){
				log.error("Impossibile trovare l'url del servizio di firma digitale nel parametro 'p.general.signature.signatureUrl'");
				getTemporary().put("signatureNotificationMessage", "Errore: impossibile trovare l'url del servizio di firma digitale nel parametro 'p.general.signature.signatureUrl'");
				original.setLockedForSignature(false);

				return;
			}
		}

		//signatureServiceUrl = "http://172.31.227.45:8090/signature/"; 
		//signatureServiceUrl = "http://localhost:8081/signature/";

		String user 		= UserBean.instance().getCurrentEmployee().getSignUser(); //"utente03";//temporary.get("username").toString().replaceAll("\"", "\\\\\"");
		String userPWD	 	= UserBean.instance().getCurrentEmployee().getSignClearPassword(); //"";// temporary.get("password").toString().replaceAll("\"", "\\\\\"");

		if(user==null) user = "";
		if(userPWD==null) userPWD = "";

		String otpPWD		= temporary.get("pin").toString().replaceAll("\"", "\\\\\"");

		byte[] documentByteArray;

		if(original.getSignedDocumentLatest()==null){
			log.error("original.getSignedDocumentLatest() = null");
			getTemporary().put("signatureNotificationMessage", "Errore: Il documento da firmare è nullo.");
			original.setLockedForSignature(false);

			return;
		}

		//		if(!original.getMimeType().equalsIgnoreCase("application/pdf")){
		documentByteArray = alfrescoGet(original.getSignedDocumentLatest().getNodeRefUrl());	
		//		}else{ 
		//			documentByteArray = alfrescoGet(original.getNodeRef());
		//		}



		if(documentByteArray==null){
			log.error("impossibile recuperare l'ultimo documento firmato da alfrescodocument");
			getTemporary().put("signatureNotificationMessage", "Attenzione! Si è verificato un errore nella generazione del file .pdf. Impossibile recuperare l'ultima versione bloccata del documento.");
			original.setLockedForSignature(false);

			return;
		}


		PdfReader reader = null;

		if(original.getSignaturesPresent()==null || original.getSignaturesPresent()==0){
			// PRIMA FIRMA: inserire una pagina in fondo

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfStamper stamper;
			try {

				reader = new PdfReader(documentByteArray);

				stamper = new PdfStamper(reader, baos);
				stamper.insertPage(reader.getNumberOfPages()+1,	reader.getPageSizeWithRotation(1));
				stamper.close();
				//reader.close();
				documentByteArray = baos.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
				getTemporary().put("signatureNotificationMessage", "Impossibile modificare il documento: " + e.getMessage());
				original.setLockedForSignature(false);

				return;
			}

			original.setPreventDelete(true);
		}

		//create();

		//conta il numero di pagine
		int pages = -1;
		try{
			if(reader==null){
				reader = new PdfReader(documentByteArray);
			}
			pages = reader.getNumberOfPages();
			reader.close();
		}catch(Exception e){
			System.err.println(e.getMessage());
			getTemporary().put("signatureNotificationMessage", "Errore: " + e.getMessage());
			original.setLockedForSignature(false);

			return;
		}
		log.warn("numero di pagine: "+pages);

		String encodedDocumentByteArray = Base64.encodeBase64String(documentByteArray);
		JSONObject jo=new JSONObject();
		jo.put("User", user); 
		jo.put("UserPWD", userPWD);
		jo.put("OtpPWD", otpPWD);
		jo.put("Binaryinput", encodedDocumentByteArray);
		jo.put("Page", pages);

		if(original.getSignaturesPresent()==null) original.setSignaturesPresent(0);

		jo.put("SignatureNumber",original.getSignaturesPresent()+1);

		//String expectedPattern = "dd-MM-yyyy";
		//String testo = original.getSignedDocumentLatest().getTesto()+"Firmato digitalmente da: "+ UserBean.instance().getNameFam()+" "+UserBean.instance().getNameGiv()+" in data "+(new SimpleDateFormat("dd/MM/yyyy hh.mm")).format(new Date());
		//jo.put("Testo", testo);


		/*
		List<Employee> firmatari = original.getSignedDocumentLatest().getSigner();
		if(firmatari==null)firmatari=new ArrayList<Employee>();
		firmatari.add(UserBean.instance().getCurrentEmployee());
		for(Employee emp : firmatari){
			testo+="\n"+emp.getName()+" ("+new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date())+")";
		}
		 */
		//testo = "333 TERZA FIRMA 333";
		String testo = "Firmato da: " + UserBean.instance().getNameFam()+" "+UserBean.instance().getNameGiv()+" in data:"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		jo.put("Testo",testo); 
		log.warn(testo);

		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("signatureRequest", jo);

		CloseableHttpClient client = HttpClients.createDefault();

		log.info("url servizio di firma: \n"+signatureServiceUrl);
		log.info("json firma:\n" + jsonMessage.toString());

		try {
			HttpPost request = new HttpPost(signatureServiceUrl);
			StringEntity params =new StringEntity(jsonMessage.toString());
			request.addHeader("content-type","application/json");
			request.setEntity(params);
			HttpResponse response = client.execute(request);

			HttpEntity signedEntity = response.getEntity();

			if (signedEntity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(result.toString());

				JSONObject signatureResponse = (JSONObject) json.get("signatureResponse"); 

				String status = signatureResponse.get("Status").toString();
				String description = signatureResponse.get("Description").toString();

				if(status.equalsIgnoreCase("OK")){
					log.info("Il servizio di firma remota ha ricevuto il documento e risposto OK. Salvataggio del documento firmato ricevuto in corso...");
					String binaryOutput = signatureResponse.get("Binaryoutput").toString();

					byte[] decodedBinaryOutput = Base64.decodeBase64(binaryOutput);
					//alfrescoGetOrCreateFolder(original.getNodeRefUrl(), original.getNodeRefUrl(), null)
					String path = original.getPath();
					if(path==null){
						log.error("null path!");
						return;
					}
					String folderNodeRef = alfrescoGetOrCreateFolder(path, path, null);


					String pdfName=original.getInternalId()+"_"+System.currentTimeMillis()+".pdf";
					String uploadedPdfNodeRef = alfrescoUploadNew(decodedBinaryOutput,folderNodeRef, pdfName);

					if (uploadedPdfNodeRef != null) {

						//create SignedDocument
						SignedDocument signedDoc = new SignedDocument();
						SignedDocumentAction sda = new SignedDocumentAction();
						sda.entity = signedDoc;
						signedDoc.setName(pdfName);
						signedDoc.setNodeRef(uploadedPdfNodeRef);
						signedDoc.setPath(folderNodeRef);

						sda.link("signer", UserBean.instance().getCurrentEmployee());
						sda.create();

						link("signedDocument",signedDoc);

						original.setSignedDocumentLatest(sda.getEntity());
						// lock del documento originale (questo) in modo che poi venga nascosto e mostrata l'ultima versione PDF dello stesso
						original.setLockedForSignature(true); 

						create();


					}else{
						log.error(this.getClass().getSimpleName()+": upload pdf fallito");
						getTemporary().put("signatureNotificationMessage", "Attenzione! Si è verificato un errore nella generazione del file .pdf. Il documento non è stato caricato su Alfresco.");
						original.setLockedForSignature(false);

						return;
					}

				} else {

					if(description.equals("0001")) temporary.put("signatureNotificationMessage","Errore generico nel processo di firma");
					else if(description.equals("0002")) temporary.put("signatureNotificationMessage","Errore: parametri non corretti per il tipo di trasporto indicato");
					else if(description.equals("0003")) temporary.put("signatureNotificationMessage","Errore in fase di verifica delle credenziali");
					else if(description.equals("0004")) temporary.put("signatureNotificationMessage","Errore nel PIN");
					else if(description.equals("0005")) temporary.put("signatureNotificationMessage","Errore: tipo di trasporto non valido");
					else if(description.equals("0006")) temporary.put("signatureNotificationMessage","Errore: tipo di trasporto non autorizzato");
					else if(description.equals("0007")) temporary.put("signatureNotificationMessage","Errore: profilo Di firma PDF non valido");
					else if(description.equals("0008")) temporary.put("signatureNotificationMessage","Errore: impossibile completare l’operazione di marcatura temporale");
					else if(description.equals("0009")) temporary.put("signatureNotificationMessage","Errore: credenziali di delega non valide");
					else if(description.equals("0010")) temporary.put("signatureNotificationMessage","Errore: lo stato dell’utente non è valido");
					//else if(status.equals("KO")) temporary.put("signatureNotificationMessage","Il servizio di firma ha incontrato un errore non specificato ('KO')");

					log.error("Il servizio di firma remota ha riscontrato un errore: "+ temporary.get("signatureNotificationMessage"));
					original.setLockedForSignature(false);

					return;
				}
			}

		}catch (Exception ex) {

			log.error(ex.getMessage());
			temporary.put("signatureNotificationMessage","Impossibile connettersi al servizio di firma.");
			original.setLockedForSignature(false);

			return;

		} finally {
			client.close();
		}


		////////////////////////////////////////////////////////////////////////////////////////////////
		//aumento del numero delle firme
		if(original.getSignaturesPresent()==null){
			original.setSignaturesPresent(1);
			original.getSignedDocumentLatest().setSignaturesPresent(1);
		}else{ 
			int newNumber = original.getSignaturesPresent()+1;
			original.setSignaturesPresent(newNumber);
			original.getSignedDocumentLatest().setSignaturesPresent(newNumber);
		}

		// set dello stato (avanzamento verso parzialmente o completamente firmato)

		boolean fullySigned = false;

		if(original.getSignaturesReqN()!=null && original.getSignaturesPresent()!=null) fullySigned = original.getSignaturesReqN()<=original.getSignaturesPresent();

		boolean fromTemplate = false;
		if(original.getFromTemplate()!=null && original.getFromTemplate()){
			fromTemplate=true;
		}

		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue documentStatus;
		try {
			if(fullySigned || !original.getFromTemplate()){
				documentStatus= voc.getCodeValue("PHIDIC", "documentstatus", "3", "C");
			}else{
				documentStatus= voc.getCodeValue("PHIDIC", "documentstatus", "2", "C"); // parzialmente firmato
			}

			if(documentStatus!=null){
				documentStatus=(CodeValuePhi) documentStatus;
			}
			original.setDocumentStatus((CodeValuePhi) documentStatus);
			temporary.put("signatureNotificationMessage", "Il processo di firma è stato eseguito correttamente.");

		} catch (PersistenceException e) {
			temporary.put("signatureNotificationMessage", "Errore: "+e.getMessage());
			e.printStackTrace();
		} catch (DictionaryException e) {
			temporary.put("signatureNotificationMessage", "Errore: "+e.getMessage());
			e.printStackTrace();
		} 


	}

	/* Restituisce true se l'utente loggato è abilitato su almeno un distretto con gestione firma digitale */
	public boolean showSign(){

		try{
			SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
			List<ServiceDeliveryLocation> uocList = sua.getDistretti();

			if (uocList==null || uocList.size()<1)
				return false;

			for (ServiceDeliveryLocation sdl:uocList){
				if (this.showSign(sdl))
					return true;
			}

		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
			return false;
		}

		return false;
	}

	/* Restituisce true se il Distretto (UOC) in input è configurato per la firma digitale */
	public boolean showSign(ServiceDeliveryLocation uoc){
		boolean ret = false;

		try{
			if (uoc==null || uoc.getInternalId()==0)
				return ret;

			Long uocId = uoc.getInternalId();
			CodeValueParameter cvp = ca.get(CodeValueParameter.class, "p.general.signature.avviamento");
			if (cvp==null)
				return ret;

			CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(cvp, uocId);
			String visible = evaluatedParameter.get("visible").toString();

			ret = (visible!=null && "true".equals(visible))?true:false;

		} catch (Exception ex) {
			log.error(ex);
			return ret;
		}

		return ret;
	}

	/* Restituisce la data di inizio validità del parametro di configurazione firma digitale del Distretto (UOC) in input */
	public Date getSignValidFrom(ServiceDeliveryLocation uoc){
		Date validFrom = null;
		try{
			if (uoc==null || uoc.getInternalId()==0)
				return null;

			Long uocId = uoc.getInternalId();
			CodeValueParameter cvp = ca.get(CodeValueParameter.class, "p.general.signature.avviamento");

			if (cvp==null)
				return null;

			CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(cvp, uocId);

			String visible = evaluatedParameter.get("visible").toString();

			//Firma digitale non configurata o non visibile
			if (visible==null || "false".equals(visible))
				return null;

			Object obj = evaluatedParameter.get("validFrom");

			if (obj!=null)
				validFrom = (Date)obj;

		} catch (Exception ex) {
			log.error(ex);
			return validFrom ;
		}

		return validFrom;
	}

	@SuppressWarnings("unchecked")
	public void sendToRegisterService() throws ClientProtocolException, IOException, PhiException{

		AlfrescoDocument original = getEntity();
		temporary.remove("signatureNotificationMessage");

		ParameterManager pm = ParameterManager.instance();
		String registerServiceUrl = null;
		try {
			registerServiceUrl = pm.getParameter("p.general.signature.registerurl", "value").toString();
		} catch (PhiException e) {
			log.error(e.getMessage());
		}

		if(registerServiceUrl.isEmpty()){

			log.error("Impossibile trovare l'url del servizio di protocollazione nel parametro 'p.general.signature.signatureurl'");
			getTemporary().put("signatureNotificationMessage", "Errore: impossibile trovare l'url del servizio di protocollazione nel parametro 'p.general.signature.registerurl'");
			return;
		}

		//registerServiceUrl = "http://172.31.227.45:8090/protocollo/";
		//registerServiceUrl = "http://localhost:8081/protocollo/";

		//		String username = temporary.get("username").toString().replaceAll("\"", "\\\\\"");
		//		String password	= temporary.get("password").toString().replaceAll("\"", "\\\\\"");
		String username = "wsuser";
		String password = "password1";



		byte[] documentByteArray = alfrescoGet(original.getSignedDocumentLatest().getNodeRefUrl());
		String encodedDocumentByteArray = Base64.encodeBase64String(documentByteArray);

		JSONObject jo=new JSONObject();
		jo.put("Applicazione", "prova");
		jo.put("Ente", "prova");
		jo.put("ULSS", "050112"); 
		jo.put("Username", username.toString());
		jo.put("Password", password.toString());
		if(original.getOggetto()!=null)
			jo.put("Oggetto", original.getOggetto().replaceAll("\"", "'"));

		jo.put("IdDocumento", original.getSignedDocumentLatest().getInternalId()+"");
		jo.put("NomeFileContenuto", original.getSignedDocumentLatest().getName());

		jo.put("Contenuto", encodedDocumentByteArray);


		if(original.getNote()!=null){
			jo.put("Note", original.getNote().replaceAll("\"", "'"));
		}else{
			jo.put("Note", "");
		}

		jo.put("LineaDiLavoro", "01");

		JSONArray jDestinatari = new JSONArray();

		if(original.getDestinatari()!=null && original.getDestinatari().size()>=1){
			for(int i=0;i<original.getDestinatari().size();i++){

				JSONObject jDestinatario = new JSONObject();
				jDestinatario.put("Tipo", ""+original.getDestinatari().get(i).getTipo().getCode().toUpperCase());
				jDestinatario.put("Nome", ""+original.getDestinatari().get(i).getNome());
				jDestinatario.put("Cognome", ""+original.getDestinatari().get(i).getCognome());
				jDestinatario.put("Denominazione", ""+original.getDestinatari().get(i).getDenominazione());
				jDestinatario.put("Email", ""+original.getDestinatari().get(i).getEmail());
				if(original.getDestinatari().get(i).getAddr()!=null){
					jDestinatario.put("Indirizzo", ""+original.getDestinatari().get(i).getAddr().getStr()+" "+original.getDestinatari().get(i).getAddr().getBnr());
					jDestinatario.put("Citta", ""+original.getDestinatari().get(i).getAddr().getCty());
				}
				jDestinatario.put("InvioPC", original.getDestinatari().get(i).getInvioPc().toString());

				jDestinatari.add(jDestinatario);

			}
		}else{
			log.error("Si è cercato di inviare al protocollo un documento con zero destinatari.");
			getTemporary().put("signatureNotificationMessage", "Errore: inserire almeno un destinatario.");
			return;
		}

		jo.put("Destinatari", jDestinatari);

		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put((String)"protocolloRequest", (JSONObject)jo);

		CloseableHttpClient client = HttpClients.createDefault();


		try{

			HttpPost request = new HttpPost(registerServiceUrl);
			StringEntity params =new StringEntity(jsonMessage.toString());
			log.info("Richiesta di protocollazione inviata: ");
			log.warn("\n"+jsonMessage.toString());

			request.addHeader("content-type","application/json");
			request.setEntity(params);
			HttpResponse response = client.execute(request);
			Vocabularies voc = VocabulariesImpl.instance();

			HttpEntity protocolloEntity = response.getEntity();
			CodeValue sentStatus = voc.getCodeValue("PHIDIC", "documentstatus", "4", "C"); 
			if(sentStatus!=null) sentStatus=(CodeValuePhi) sentStatus;
			getEntity().setDocumentStatus((CodeValuePhi) sentStatus);
			getEntity().setPreventDelete(true);

			log.info("File inviato dal servizio di firma remota.");
			create();

			if (protocolloEntity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(protocolloEntity.getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				JSONParser parser = new JSONParser();

				log.info(result.toString());
				if(result.toString().isEmpty()){
					log.error("register service answered with empty result");
					getTemporary().put("signatureNotificationMessage", "Errore: impossibile recuperare la risposta del servizio di protocollo.");
					return;
				}

				JSONObject json = (JSONObject) parser.parse(result.toString());
				JSONObject protocolloResponse = (JSONObject) json.get("protocolloResponse"); 
				//String status = protocolloResponse.get("Status").toString();

				client.close();

				String CodiceEsito = protocolloResponse.get("CodiceEsito").toString();
				String DescrizioneEsito = protocolloResponse.get("DescrizioneEsito").toString();
				String IdDocumento = protocolloResponse.get("IdDocumento").toString();
				String NumeroProtocollo = protocolloResponse.get("NumeroProtocollo").toString();
				String DataProtocollo = protocolloResponse.get("DataProtocollo").toString();

				log.info(">>----------------------------> DataProtocollo: "+protocolloResponse.get("DataProtocollo")+"| ToString: "+ DataProtocollo);

				String expectedPattern = "yyyy-MM-dd";
				SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

				SignedDocument sd = original.getSignedDocumentLatest();
				SignedDocumentAction sda = new SignedDocumentAction();
				sda.entity=sd;

				Date dip = new Date();
				sd.setDataInvioProtocollo(dip);

				if(CodiceEsito.equals("000")){
					log.info("Il servizio di protocollazione ha riposto '"+DescrizioneEsito+"' [numero di protocollo: "+NumeroProtocollo+"]");
					temporary.put("signatureNotificationMessage", DescrizioneEsito+", numero di protocollo assegnato: "+NumeroProtocollo);

					CodeValue signedStatus = voc.getCodeValue("PHIDIC", "documentstatus", "5", "C");  
					if(signedStatus!=null) signedStatus=(CodeValuePhi) signedStatus;
					original.setDocumentStatus((CodeValuePhi) signedStatus);

					sd.setNProtocollo(NumeroProtocollo);

					String dp = DataProtocollo;
					Date date = formatter.parse(dp);
					sd.setDataProtocollo(date);



					create();




				}else if(CodiceEsito.equals("107") || CodiceEsito.equals("108")){
					log.error("Il servizio di protocollazione ha riposto '"+DescrizioneEsito+"'");
					temporary.put("signatureNotificationMessage", "Il servizio di protocollazione ha riposto '"+DescrizioneEsito+"'");

				}else{
					log.error("Il servizio di protocollazione ha riposto con un codice di errore non previsto ("+CodiceEsito+")");
					temporary.put("signatureNotificationMessage", "Il servizio di protocollazione ha riposto con un codice di errore non previsto ("+CodiceEsito+")");
				}







			}

		}catch(Exception e){
			getTemporary().put("signatureNotificationMessage", "Errore generico");
			log.error(e.getMessage());
		}
	}

	public String getPdfIconColor(AlfrescoDocument doc){

		String color = "#FF00FF"; //purple = error
		if(doc==null || doc.getDocumentStatus()==null || doc.getDocumentStatus().getCode()==null)return color;
		String statusCode = doc.getDocumentStatus().getCode();

		if(statusCode.equals("1")){		//generato
			color = "#000000";
		}else if(statusCode.equals("2")){	//parzialmente firmato
			color = "#F1C40F";				
		}else if(statusCode.equals("3")){	//firmato
			color = "#03A600";
		}else if(statusCode.equals("4")){	//inviato
			color = "#FF0000"; // inviato ma non protocollato: solo in caso di un errore al protocollo
		}else if(statusCode.equals("5")){	//protocollato
			color = "#A4A4A4";
		}

		return color;

	}

	public String getDatiProtocollo(AlfrescoDocument doc){

		String result = "";

		if(doc!=null && doc.getSignedDocumentLatest()!=null && doc.getSignedDocumentLatest().getNProtocollo()!=null){

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date regDate =  doc.getSignedDocumentLatest().getDataProtocollo();        
			String reportDate = df.format(regDate);

			result="Prot. n°"+doc.getSignedDocumentLatest().getNProtocollo()+" del "+ reportDate;
		}			
		return result;

	}

	public void doFilters(List<AlfrescoDocument> documentList){


		Boolean showDeactivated = false;
		if(temporary.get("showDeactivated")!=null && (Boolean)temporary.get("showDeactivated")){
			showDeactivated=true;

		}
		Boolean showNotSigned = false;
		if(temporary.get("showNotSigned")!=null && (Boolean)temporary.get("showNotSigned")){
			showNotSigned=true; 

		}
		Boolean showPartiallySigned = false;
		if(temporary.get("showPartiallySigned")!=null && (Boolean)temporary.get("showPartiallySigned")){
			showPartiallySigned=true;

		}
		Boolean showSigned = false;
		if(temporary.get("showSigned")!=null && (Boolean)temporary.get("showSigned")){
			showSigned=true;

		}
		Boolean showSentToRegister = false;
		if(temporary.get("showSentToRegister")!=null && (Boolean)temporary.get("showSentToRegister")){
			showSentToRegister=true;

		}
		Boolean showRegistered = false;
		if(temporary.get("showRegistered")!=null && (Boolean)temporary.get("showRegistered")){
			showRegistered=true;

		}


		List<AlfrescoDocument> newList = new ArrayList<AlfrescoDocument>();

		for(int i=0;i<documentList.size();i++){


			AlfrescoDocument doc = documentList.get(i);

			String statusCode="";

			if(doc.getDocumentStatus()!=null && doc.getDocumentStatus().getCode()!=null){
				statusCode = doc.getDocumentStatus().getCode();
			}



			if(doc.getIsActive()){
				if(statusCode.equals("1") && !showNotSigned){
					continue;
				}
				if(statusCode.equals("2") && !showPartiallySigned){
					continue;
				}
				if(statusCode.equals("3") && !showSigned){ 
					continue;
				}
				if(statusCode.equals("4") && !showSentToRegister){ 
					continue;
				}
				if(statusCode.equals("5") && !showRegistered){ 
					continue;
				}
			}else if(!showDeactivated){
				continue;
			}



			newList.add(doc);

		}

		injectList(newList, "AlfrescoDocumentList");

	}

	public Boolean checkValidName(AlfrescoDocument ad){

		String path=temporary.get("alfrescoPath").toString();

		Boolean result = true;

		try{
			Query q = ca.createQuery("select ad.internalId, ad.name from AlfrescoDocument ad where ad.path= :path and ad.name = :name");
			q.setParameter("path", path);
			q.setParameter("name", ad.getName());

			log.info("Q: "+q.toString()+"("+path+","+ad.getName()+")");

			List<AlfrescoDocument> list = q.getResultList();
			if(list!=null && list.size()>0){
				result = false;
			}
		}catch (Exception e) {
			log.error(e.getMessage());
		}



		return result;

	}

	/**
	 * @return list ServiceDeliveryLocation with code ULSS 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getArpav() {

		Criteria sdlocs = ca.createCriteria(ServiceDeliveryLocation.class); 

		sdlocs/*.setProjection(Projections.projectionList()
	        	.add(Projections.property("internalId").as("internalId"))
	        	.add(Projections.property("name.giv").as("giv"))      
	        )*/
		.addOrder(Order.asc("name.giv"))
		.createCriteria("code","code")
		.add(Restrictions.eq("code.code", "ARPAV"));

		List<ServiceDeliveryLocation> lst = sdlocs.list();


		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		for (ServiceDeliveryLocation row : lst) {
			selectItems.add(new SelectItem(row, row.getName().getGiv()));
		}

		return selectItems;
	}

	public void setReadTemplatesFilters(Impianto impianto, VerificaImp verifica, Addebito addebito, PersoneGiuridiche personeGiuridiche) throws Exception {

		Vocabularies voc = VocabulariesImpl.instance();

		cleanRestrictions();

		if(impianto != null){
			if (impianto instanceof ImpPress) {
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "01"));
			}else if (impianto instanceof ImpRisc) {
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "02"));
			}else if (impianto instanceof ImpMonta) {
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "03"));
			}else if (impianto instanceof ImpSoll) {
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "04"));
			}else if (impianto instanceof ImpTerra) {
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "05"));
			}			
		}else if (verifica != null) {
			if (verifica.getImpPressCpy()!= null) {
				if ("01".equals(verifica.getImpPressCpy().getCode().getCode())) { //Impianti a pressione
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "01"));
				}
			}else if (verifica.getImpRiscCpy()!= null) {
				if ("02".equals(verifica.getImpRiscCpy().getCode().getCode())) { //Impianti di riscaldamento
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "02"));
				}
			}else if (verifica.getImpMontaCpy()!= null) {
				if ("03".equals(verifica.getImpMontaCpy().getCode().getCode())) { //Ascensori e montacarichi
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "03"));
				}
			}else if (verifica.getImpSollCpy()!= null) {
				if ("04".equals(verifica.getImpSollCpy().getCode().getCode())) { //Impianti di sollevamento
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "04"));
				}
			}else if (verifica.getImpTerraCpy()!= null) {
				if ("05".equals(verifica.getImpTerraCpy().getCode().getCode())) { //Impianti elettrici
					getEqual().put("typeArpav", voc.getCodeValueCsDomainCode("PHIDIC", "ImpType", "05"));
				}
			}
		}
	}

	public List<SelectItem> getMixedVocItems() throws Exception{
		List<SelectItem> out = new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();

		out = voc.getIdValues("PHIDIC:IMPTYPE");

		SelectItem last = new SelectItem();
		CodeValuePhi lastCode = (CodeValuePhi)voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "HEADERFOOTER");
		last.setValue(lastCode);
		last.setLabel(lastCode.getCurrentTranslation());
		last.setDescription(lastCode.getCurrentDescription());

		out.add(last);
		return out;

	}
}

