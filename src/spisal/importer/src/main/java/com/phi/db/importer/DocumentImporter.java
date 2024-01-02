package com.phi.db.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.Query;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.prevnet.entities.Documentiscan;
import com.prevnet.entities.Pratiche;
import com.prevnet.entities.Tblblob;
import com.prevnet.mappings.MapDocumenti;
import com.prevnet.mappings.MapPersoneFisiche;
import com.prevnet.mappings.MapScans;

public class DocumentImporter extends EntityManagerUtilities {
	
	private static final Logger thislog = Logger.getLogger(DocumentImporter.class.getName());
	private static MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
	private static DocumentImporter instance = null;
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final String contentTypeJson = "application/json; charset=UTF-8";
	
	private String alfrescoUrl;
	private String alfrescoUsername;
	private String alfrescoPassword;
	private String alfrescoLoginUrl;
	private String alfrescoUploadUrl;
	private String alfrescoGetById;
	private String alfrescoBrowse;
	private String alfrescoCreateFolderUrl;
	private String alfrescoDeleteUrl;
	private String alfrescoTicket = null;
	private String alfrescoTicketHeader = null;
	
	public static DocumentImporter getInstance() {
		if(instance == null) {
			instance = new DocumentImporter();
		}
		return instance;
	}
	
	public DocumentImporter(){

		CodeValueParameter url = getParameter("p.general.alfrescoUrl");
		CodeValueParameter user = getParameter("p.general.alfrescoUsername");
		CodeValueParameter pass = getParameter("p.general.alfrescoPassword");
		
		if (url == null || user == null || pass == null) {
			log.warn("Alfresco parameters not configured!");
		}
		
		if(url!=null)
			alfrescoUrl = url.getValue();
		
		if(user!=null)
			alfrescoUsername = user.getValue();
		
		if(pass!=null)
			alfrescoPassword = pass.getValue();
			
		alfrescoLoginUrl = 			alfrescoUrl + "/alfresco/service/api/login";
		alfrescoUploadUrl = 		alfrescoUrl + "/alfresco/service/api/upload";
		alfrescoGetById = 			alfrescoUrl + "/alfresco/service/api/node/content/";
		alfrescoBrowse = 			alfrescoUrl + "/alfresco/api/-default-/public/cmis/versions/1.1/browser/root";
		alfrescoCreateFolderUrl = 	alfrescoUrl + "/alfresco/service/api/node/folder/";
		alfrescoDeleteUrl =		 	alfrescoUrl + "/alfresco/service/api/archive/";
	}

	public static void main (String[] args) {
		DocumentImporter importer = new DocumentImporter();
		importer.closeResource();
		
	}

	public void importDocuments(Pratiche source, Procpratiche target){
		if(source!=null && target!=null){
			if(source.getTblblobs()!=null && !source.getTblblobs().isEmpty()){
				for(Tblblob tbl : source.getTblblobs()){
					if(tbl.getImmaginefile()!=null && tbl.getNomefile()!=null){
						if(!checkMappingDocs(tbl.getIdtblblob())){
							String path = getPath(false, target);
							String fileName = tbl.getIdtblblob()+"-"+tbl.getNomefile();
							byte[] document = tbl.getImmaginefile();
							String mimeType = mime.getContentType(fileName);
							
							String folderNodeRef = alfrescoGetOrCreateFolder(path, path, null);
							
							String nodeRef = alfrescoUploadNew(document, folderNodeRef, fileName);
							if (nodeRef != null) {
								AlfrescoDocument aDoc = new AlfrescoDocument();
								
								aDoc.setCreatedBy(this.getClass().getSimpleName()+ulss);
								aDoc.setCreationDate(source.getData());
								aDoc.setName(fileName);
								aDoc.setNodeRef(nodeRef);
								aDoc.setMimeType(mimeType);
								aDoc.setPath(path);
								aDoc.setIsTemplate(false);
								
								if(tbl.getDescrizionefile()!=null){
									String descrizione = tbl.getDescrizionefile();
									int length = descrizione.length();
									descrizione = descrizione.substring(0,length<=255?length:255);
									aDoc.setDescription(descrizione);
								}				
								
								saveOnTarget(aDoc);
								saveMappingDocs(tbl, aDoc);
								target.addDocumenti(aDoc);
								saveOnTarget(target);
							}
						}
					}
				}
			}

			/*
			if(source.getDocumentiscans()!=null && !source.getDocumentiscans().isEmpty()){
				for(Documentiscan scan : source.getDocumentiscans()){
					if(scan.getFilescan()!=null && scan.getNomefile()!=null){
						if(!checkMappingScans(scan.getIddocumentiscan())){
							String path = getPath(false, target);
							String fileName = scan.getIddocumentiscan()+"-"+scan.getNomefile();
							byte[] document = scan.getFilescan();
							String mimeType = mime.getContentType(fileName);
							
							String folderNodeRef = alfrescoGetOrCreateFolder(path, path, null);
							
							String nodeRef = alfrescoUploadNew(document, folderNodeRef, fileName);
							if (nodeRef != null) {
								AlfrescoDocument aDoc = new AlfrescoDocument();
								
								aDoc.setCreatedBy(this.getClass().getSimpleName()+ulss);
								aDoc.setCreationDate(source.getData());
								aDoc.setName(fileName);
								aDoc.setNodeRef(nodeRef);
								aDoc.setMimeType(mimeType);
								aDoc.setPath(path);
								aDoc.setIsTemplate(false);
								
								if(scan.getDescrizionefile()!=null){
									String descrizione = scan.getDescrizionefile();
									int length = descrizione.length();
									descrizione = descrizione.substring(0,length<=255?length:255);
									aDoc.setDescription(descrizione);
								}				
								
								saveOnTarget(aDoc);
								saveMappingScans(scan, aDoc);
								target.addDocumenti(aDoc);
								saveOnTarget(target);
							}
						}
					}
				}
			}*/

		}
	}
	
	private String alfrescoUploadNew(byte[] document, String folderNodeRef, String fileName) {
		return alfrescoUpload(document, folderNodeRef, null, fileName);
	}
	
	/**
	 * alfresco upload new file or update an existing file
	 * @param document byte array
	 * @param folderNodeRef if != null -> upload new file
	 * @param documentNodeRef if != null -> update to an existing file
	 * @param fileName
	 * @return
	 */
	private String alfrescoUpload(byte[] document, String folderNodeRef, String documentNodeRef, String fileName) {
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
			log.error("Alfresco upload failed", e);
		}
		return nodeRef;
	}
	
	/**
	 * alfresco login
	 * @throws PhiException 
	 */
	@SuppressWarnings("unchecked")
	private void alfrescoLogin() {
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
		}
	}
	
	/**
	 * Read string from HttpResponse
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private String getContent(HttpResponse response) throws IOException {
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
	private String getPath(Boolean isTemplate, Procpratiche pratica) {
				
		String path = null;
		
		Calendar cal = Calendar.getInstance();
		
		if (pratica != null) {
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
		} 
		String ret = clean(path);
		return ret;
	}
	
	/**
	 * alfresco createFolder
	 * create folder with path
	 * @throws PhiException 
	 */
	private String alfrescoCreateFolder(String parentFolderNodeRef, String folderName) {
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
			log.error("Alfresco createFolder failed parentFolder " + parentFolderNodeRef + " folderName " + folderName);
		}
		return nodeRef;
	}
	
	/**
	 * alfresco get or create folder
	 * find nodeRef of folder with path, if doesen't exsist create it.
	 * @throws PhiException 
	 */
	private String alfrescoGetOrCreateFolder(String fullPath, String currentPath, String nodeRef) {
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
	
	private String alfrescoGetFolderNoderef(String path) {
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
			log.error("Alfresco getFolderNoderef failed, path: " + path);
		}
		return nodeRef;
	}
	
	private String clean(String path) {
		String ret = path.replace("*", "").replace("\"", "").replace("'", "").replace("<", "").replace(">", "").replace(",", "").replace(".", "").replace("?", "").replace("!", "").replace("|", "").replace("#", "");
		
		return ret;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingDocs(long id){
		MapDocumenti m = sourceEm.find(MapDocumenti.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapDocumenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapDocumenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapDocumenti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingScans(long id){
		MapScans m = sourceEm.find(MapScans.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapScans m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapScans> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapScans map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}
	
	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private AlfrescoDocument getMappedDocs(long id){
		String hqlMapping = "SELECT m FROM MapDocumenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapDocumenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapDocumenti map = list.get(0);
			Query qDocs = targetEm.createQuery("SELECT p FROM AlfrescoDocument p WHERE p.internalId = :id");
			qDocs.setParameter("id", map.getIdphi());
			List<AlfrescoDocument> lp = qDocs.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}
	
	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private AlfrescoDocument getMappedScans(long id){
		String hqlMapping = "SELECT m FROM MapScans m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapScans> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapScans map = list.get(0);
			Query qScans = targetEm.createQuery("SELECT p FROM AlfrescoDocument p WHERE p.internalId = :id");
			qScans.setParameter("id", map.getIdphi());
			List<AlfrescoDocument> lp = qScans.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}
	
	private void saveMappingDocs(Tblblob source, AlfrescoDocument target){
		MapDocumenti map = new MapDocumenti();
		map.setIdprevnet(source.getIdtblblob());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);

		saveOnSource(map);

		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}
	
	private void saveMappingScans(Documentiscan source, AlfrescoDocument target){
		MapScans map = new MapScans();
		map.setIdprevnet(source.getIddocumentiscan());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);

		saveOnSource(map);

		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}
	
	@Override
	protected void deleteImportedData(String ulss) {
				
	}
}
