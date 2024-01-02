package com.phi.rest;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.banner.Banner;
import com.phi.entities.PhiRevisionEntity;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.events.PhiEvent;
import com.phi.json.HibernateModule;
import com.phi.parameters.ParameterManager;
import com.phi.rest.action.RestAction;
import com.phi.rest.datamodel.InjectDatamodel;
import com.phi.rest.datamodel.ListDatamodel;


/**
 * Abstract class for managing entities from rest.
 * 
 * Http methods:
 * GET /		: get list of entity
 * GET /10		: get entity with id 10
 * POST /		: create a new entityClass, return id
 * PUT /8		: update entity with id 8
 * DELETE /12	: delete entity with id 12
 * 
 * @author alex.zupan
 *
 * @param <T> entity class managed by concrete class. For example: PatientRest extends BaseRest<Patient> 
 */


public abstract class BaseRest<T extends BaseEntity> implements Serializable {
	
	private static final long serialVersionUID = 8556346700912237063L;
	
	protected static final String BASE_REST_URL = "resource/rest/";
	protected static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
	protected String conversationName = null;
	
	protected Class<T> entityClass;	
	protected T entity;
	
	protected static final Logger log = Logger.getLogger(BaseRest.class);
	protected CatalogAdapter ca;
	
	// Dafault page size
	protected static final Integer readPageSize = 10;
	
	@javax.ws.rs.core.Context
	HttpServletRequest servletRequest;
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	private static final String uploadFolderDefault = "/tmp/";

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	protected String currentLang = WordUtils.capitalize(Locale.instance().getLanguage());

	@SuppressWarnings({"rawtypes", "unchecked"})
	public BaseRest() {
		
		Type superClasss = (Type) getClass().getGenericSuperclass();
		ParameterizedType genericSuperclass = null;
		
		if (superClasss instanceof ParameterizedType) {
			genericSuperclass = (ParameterizedType)superClasss;
		} else if (superClasss instanceof Class) {
			genericSuperclass = (ParameterizedType)((Class)superClasss).getGenericSuperclass();
		}

        entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        
        conversationName = entityClass.getSimpleName();
        
		ca = CatalogPersistenceManagerImpl.instance();	
	}
	
	
	/**
	 * Get list of entities
	 * @param restrictions read restrictions
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */	
	@GET
	@Path("{restrictions}/{page}")
	@Produces(APPLICATION_JSON_UTF8)
	public Response get(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page, @javax.ws.rs.core.Context ServletContext servletContext) throws UnsupportedEncodingException {
		
		long t = System.currentTimeMillis();
		
		Map<String,List<String>> restrictionMap = decodeResctrictions(restrictions);
		
		try {
			
			if (page < 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 0: not paged, 1: first page, ...").build();
			}

			RestAction action = new RestAction(entityClass);
			
			Integer readPageSize = BaseRest.readPageSize;
			
			if (restrictionMap.containsKey("readPageSize")) {
				readPageSize = Integer.parseInt(restrictionMap.get("readPageSize").get(0));
			}
			
			restrictions2action(action, restrictionMap);
						
			if (page > 0){
				int firstResult = (page - 1) * readPageSize;
				int maxResults = readPageSize + 1;
            
				action.getEntityCriteria().setFirstResult(firstResult);
				action.getEntityCriteria().setMaxResults(maxResults);
			}
            
            @SuppressWarnings("unchecked")
			List<T> results = action.getEntityCriteria().list();
              
            String readUrl = BASE_REST_URL + entityClass.getSimpleName().toLowerCase() + "s/" + restrictions + "/"; //FIXMEEEEEEEEEEEEEEEEEEEEE

            ListDatamodel<T> dm = new ListDatamodel<T>(results, readUrl, readPageSize, page );
            
            // FIXME: FLEX doesen't support reading of status. Only 200 is a valid status.
			/*
            if (results.size() == 0) {  
				return Response.status(Response.Status.NOT_FOUND).entity("Entity " + entityClass.getSimpleName() + " not found for restrictions: " + restrictionMap).build();
			}
            */
            
			if (results.size() == 1 && results.get(0) instanceof BaseEntity) { //auto inject
				log.warn("Auto inject removed to have a more consistent conversation java-javascript, conversationName: " + conversationName + ", restrictions: " + restrictions);
//				Object obj =  results.get(0);
//				ActionInterface<?> baseAction = (ActionInterface<?>)Component.getInstance(conversationName+"Action");
//				if (baseAction != null) {
//					baseAction.inject(obj);
//				} else {
//					Context conversation = Contexts.getConversationContext();
//					conversation.set(conversationName, obj);
//				}
			}
			
			String result = mapper.writeValueAsString(dm);
			
			log.info("[cid="+Conversation.instance().getId()+"] " 
					+ "Read " + entityClass.getSimpleName() + ": " 
					+ (results == null ? "0" : results.size()) + " in "
					+ (System.currentTimeMillis() - t)
					+ " ms {entityCriteria: " + action.getEntityCriteria() + "}");	
			//log.info("[cid="+Conversation.instance().getId()+"] Object read: " + entityClass.getSimpleName() + " with restrictions: " + restrictionMap);
						
			Events.instance().raiseEvent(PhiEvent.READ, entityClass.getSimpleName());
			
			return Response.ok(result).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get entities " + entityClass.getSimpleName() + " by restrictions: " + restrictionMap, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get entities " + entityClass.getSimpleName() + " by restrictions: " + restrictionMap).build();
		}	
	}
	

	/**
	 * Get entity by id, put in conversation
     * If id == 0 eject from conversation
     * 
	 * @param restrictions
	 * @return json representation
	 */	
	@GET
	@Path("/{restrictions}")
	@Produces(APPLICATION_JSON_UTF8)
	//@Produces("application/hal+json")
	public Response inject(@PathParam("restrictions") PathSegment restrictions) {
		Response response = load(restrictions, true);
		return response;
	}
	
	/**
	 * Get entity by id, without injecting
     * 
	 * @param restrictions
	 * @return json representation
	 */	
	@GET
	@Path("get/{restrictions}")
	@Produces(APPLICATION_JSON_UTF8)
	public Response get(@PathParam("restrictions") PathSegment restrictions) {
		Response response = load(restrictions);
		return response;
	}
	
	private Response load(PathSegment restrictions) {
		return load(restrictions, false);
	}
	
	private Response load(PathSegment restrictions, boolean inject) {
		
		long id = 0L;
		
		try {
			InjectDatamodel<T> res = new InjectDatamodel<T>();
			// Inject and set entity (if id <= 0 entity is ejected and passed null)
			id = Long.parseLong(restrictions.getPath());

			if (id > 0) {
				entity = ca.get(entityClass, id);
				
				if (entity == null) {
					throw new PhiException("Error getting entity with id " + id,ErrorConstants.PERSISTENCE_RIM_OBJECT_INITIALIZATION_ERR_INTERNAL_MSG);
				}
				if (entity instanceof HibernateProxy) { //deproxy
					entity = ((T)((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation());
				}
			} else {
				entity = null;
			}

			// Deproxy required children
			MultivaluedMap<String, String> restrictionMap = restrictions.getMatrixParameters();
			List<String> loadList = restrictionMap.get("load");
			
			if (loadList != null) {
				for (String load : loadList) {
					loadProxy(entity,load);				
				}
			}
			
			res.setEntity(entity);
			
			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			res.setAdditional(additional);

			if (inject) {
				inject(entity);
				addBanner(res);
			}
			
			String json = mapper.writeValueAsString(res);
			
			//HATEAOS
//			Resource resource = HyperExpress.createResource(entity, "application/hal+json");
//			String json = mapper.writeValueAsString(resource);
			
			Events.instance().raiseEvent(PhiEvent.INJECT, entity);
			
			return Response.ok(json).build();
			
		} catch (NumberFormatException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id + ". Id is not a valid number", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id + ". Id is not a valid number").build();
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		}

	}

	// Function to inject the entity
	@SuppressWarnings("unchecked")
	protected void inject(long id) throws PhiException{
		if (id > 0) {
			entity = ca.get(entityClass, id);
			
			if (entity == null) {
				throw new PhiException("Error getting entity with id " + id,ErrorConstants.PERSISTENCE_RIM_OBJECT_INITIALIZATION_ERR_INTERNAL_MSG);
			}
		} else {
			entity = null;
		}
		inject(entity);
	}

	@SuppressWarnings("unchecked")
	private void inject(T entity) {

		Context conversation = Contexts.getConversationContext();
		
		BaseAction<?, ?> action = (BaseAction<?, ?>)Component.getInstance(conversationName+"Action");
		
		if (entity != null) {
	
			if (action == null) {
				conversation.set(conversationName, entity);
			} else {
				action.inject(entity);
			}
			
			log.info("[cid="+Conversation.instance().getId()+"] Object injected: " + entityClass.getSimpleName() + ", id: " + entity.getInternalId());
			
		} else { //eject
			
			if (action == null) {
				conversation.remove(conversationName);
			} else {
				action.eject();
			}
			
			log.info("[cid="+Conversation.instance().getId()+"] Object ejected: " + entityClass.getSimpleName());			
		}	
		
	}
	
	@POST
	@Path("/injectnew")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response injectNew(String jsonEntity) {
		
			BaseAction<?, ?> action = (BaseAction<?, ?>) Component.getInstance(conversationName + "Action");
			
			if (jsonEntity == null || jsonEntity.isEmpty()){
				try {
					action.inject(action.newEntity());
				} catch (Exception e) {
					log.error("error injecting new Entity");
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("invalid injection").build();
				} 
			}
			else {
				try {
					T entity = mapper.readValue(jsonEntity, entityClass);
					action.inject(entity);
					
				} catch (Exception e) {
					log.error("error parsing injected Entity");
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("invalid injected entity").build();
				} 
			}
			
			
			return Response.ok().build(); 
	}
	
	
	/**
	 * Get entity history by entity id, includes current if includeCurrent = true, orderDesc orders results, attribute checks listed attributes changes only
     * 
	 * @param id
	 * @return json representation
	 */	
	@GET
	@Path("/history/{id : \\d+}") //support digit only
	@Produces(APPLICATION_JSON_UTF8)
	public Response getHistory(@PathParam("id") long id, @DefaultValue("true") @QueryParam("includeCurrent") Boolean includeCurrent, @DefaultValue("true") @QueryParam("orderDesc") Boolean historyOrderDescending, @QueryParam("attribute") List<String> attributesChanged) {
		try {
			// TODO: add select
			long t = System.currentTimeMillis();
			
			List<T> history = null;
			if (attributesChanged.size()==0) {
				history = ca.getHistoryof(entityClass, id);
			} else {
				history = ca.getHistoryof(entityClass, id, attributesChanged.toArray(new String[attributesChanged.size()]));
			}
			
			if (includeCurrent) {
				
				entity = ca.get(entityClass, id);
				
				if (entity == null) {
					return Response.status(Response.Status.NOT_FOUND).entity("Entity " + entityClass.getSimpleName() + " not found for id: " + id).build();
				}
				
				if (entity instanceof BaseEntity) {
					PhiRevisionEntity rev = new PhiRevisionEntity();
					rev.setCurrent(true);
					((BaseEntity)entity).setRevision(rev);
				}
				history.add(entity);
			}

			if (historyOrderDescending) {
				Collections.reverse(history);
			}

			log.debug("[cid="+Conversation.instance().getId()+"] Readed History"+
					(includeCurrent ? "IncludeCurrent" : "")+" for "+ entityClass.getName() + " internalId "+ id +
					" with " + history.size()+" results in " +(System.currentTimeMillis()-t) + " ms " + (attributesChanged == null || attributesChanged.size() == 0 ? "" : attributesChanged));
			
			String jsonEntity = mapper.writeValueAsString(history);
			
			Events.instance().raiseEvent(PhiEvent.HISTORY, ca.load(entityClass, id));
			
			return Response.ok(jsonEntity).build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		}
	}
	
	
	/**
	 * Create new entity, return id
	 * 
	 * If the custom header "x-method-override" is set to "UPDATE" then update() method is called. 
	 * Use it if PUT HTTP method isn't available.
	 * 
	 * If the custom header "x-method-override" is set to "DELETE" then delete() method is called. 
     * Use it if DELETE HTTP method isn't available.
	 * 
	 * @param jSonEntity
	 * @return id of the new entity
	 */	
	@POST
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			if (methodOverride != null) {
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					return update(jSonEntity);
				} else if (methodOverride.equalsIgnoreCase("DELETE")) {
					Long id = Long.parseLong(jSonEntity);
					return delete(id);
				} else {
					log.error("Error x-method-override contains unknown method: " + methodOverride);
					throw new IllegalArgumentException("Error x-method-override contains unknown method: " + methodOverride);
				}
			} else { // CREATE
				T entity = mapper.readValue(jSonEntity, entityClass);
				
				entity = create(entity);
				/*
				ca.create(entity);
				ca.flushSession();
				
				this.entity = entity;
				*/
				String jsonId = mapper.writeValueAsString(entity.getInternalId());
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass.getSimpleName() + ", id: " + entity.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, entity);
				
				return Response.ok(jsonId).build(); //FIXME change to created
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
	
	protected T create(T entity) throws PersistenceException, PhiException {
		ca.create(entity);
		ca.flushSession();
		
		this.entity = entity;
		return entity;
	}
	
	/**
	 * Update an entity
	 * 
	 * It may get invoked by the @POST equivalent if the "x-method-override" header is configured for "UPDATE"
	 * 
	 * @param jSonEntity
	 */	
	@PUT
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response update(String jSonEntity) {
		try {
			T entity = mapper.readValue(jSonEntity, entityClass);
			
			entity = update(entity);
			/*
			T updatedEntity = (T)ca.update(entity);
			ca.flushSession();
			this.entity=updatedEntity;
			*/
			String jsonEntity = mapper.writeValueAsString(entity);

			log.info("[cid="+Conversation.instance().getId()+"] Object updated: " + entityClass.getSimpleName() + ", id: " + entity.getInternalId());
			
			Events.instance().raiseEvent(PhiEvent.CREATE, entity);
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
	protected T update(T entity) throws PersistenceException, PhiException {
		@SuppressWarnings("unchecked")
		T updatedEntity = (T)ca.update(entity);
		ca.flushSession();
		this.entity=updatedEntity;
		return updatedEntity;
	}
	
	/**
	 * Delete entity by id
	 * 
	 * It may get invoked by the @POST equivalent if the "x-method-override" header is configured for "DELETE"
	 * 
	 * @param id
	 */	
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id) {
		try {
			T entityToBeDeleted = ca.load(entityClass, id);
			
			delete(entityToBeDeleted);
			/*
			ca.delete(entityToBeDeleted);
			ca.flushSession();
			*/
			log.info("[cid="+Conversation.instance().getId()+"] Object deleted: " + entityClass.getSimpleName() + ", id: " + id);
						
			Events.instance().raiseEvent(PhiEvent.DELETE, entityToBeDeleted);
			
			return Response.ok().build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error delete entity by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error delete entity by id: " + id).build();
		}
	}

	protected void delete(T entity) throws PersistenceException {
		ca.delete(entity);
		ca.flushSession();
	}
	
	
	/**
	 * Upload file into entity.field, field must be of type byte[]
	 * @param req multipart file upload
	 * @param id internalId of entity
	 * @param field name of the field inside entity
	 * @param path if path not null save into filesystem under uploadFolder, and put path into field
	 * @return OK
	 */
	@POST
	@Path("/{id}/upload/{field}")
	public Response upload(
			@javax.ws.rs.core.Context HttpServletRequest req, @PathParam("id") long id, 
			@PathParam("field") String field,
			@QueryParam("path") String path) {

		ResponseBuilder responseBuilder = null;

		try {

			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			List<FileItem> items = upload.parseRequest(req);
			Iterator<FileItem> iter = items.iterator();

			InputStream inStr = null;
			String contentType = null,fileName =null;
			long size = 0;

			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					log.debug("Form field: " + item.getFieldName());
				} else {
					inStr = item.getInputStream();
					contentType = item.getContentType();
					fileName = item.getName();
					size = item.getSize();
				}
			}
			
			if (inStr == null) {
				throw new IllegalArgumentException("No file part");
			}
			
			//entity = ServiceDeliveryLocationAction.instance().getEntity();
			Context conv = Contexts.getConversationContext();
			if (id == 0) {
				entity = (T)conv.get(conversationName);
			}
			else {
				entity = ca.get(entityClass, id);
				
			}
			
			
			conv.set("lastUpload_contentType",contentType);
			conv.set("lastUpload_filename",fileName);
			conv.set("lastUpload_filesize",size);
			
			if (entity != null) {
				PropertyUtilsBean propBean = BeanUtilsBean.getInstance().getPropertyUtils();
				PropertyDescriptor pd = propBean.getPropertyDescriptor(entity, field);
			
				Method setter = pd.getWriteMethod();
				byte[] inBarr = IOUtils.toByteArray(inStr);
				if (path == null) {
					// Save in db
					setter.invoke(entity, inBarr);
				} else {
					//Save on disk
					File fileUploadPath = new File(getUploadPath() + path);
					if (!fileUploadPath.exists()) {
						fileUploadPath.mkdirs();
					}
					File file = new File(fileUploadPath, fileName);
					FileUtils.writeByteArrayToFile(file, inBarr);
					
					setter.invoke(entity, file.getAbsolutePath());
				}
			}

			responseBuilder = Response.ok();

		} catch (Exception e) {
			log.error("Error uploading file " + e.getMessage(), e);
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error uploading file " + e.getMessage());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}
		return responseBuilder.build();
	}
	
	/**
	 * Download file from entity.field
	 * if entity.field id a byte[] return that, else if entity.field is a string containing a valid path to file system, return file
	 * @param id internalId of entity
	 * @param field name of the field inside entity
	 * @return content of field
	 */
	@GET
	@Path("/{id}/download/{field}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response get(@PathParam("id") long id, @PathParam("field") String field) {

		ResponseBuilder responseBuilder = null;

		try {

			Object data = null;

			//entity = ServiceDeliveryLocationAction.instance().getEntity();
			entity = ca.get(entityClass, id);
			
			if (entity != null) {
				PropertyUtilsBean propBean = BeanUtilsBean.getInstance().getPropertyUtils();
				PropertyDescriptor pd = propBean.getPropertyDescriptor(entity, field);
				
				Method getter = pd.getReadMethod();
				data = getter.invoke(entity);
				if (getter.getReturnType() == byte[].class) {
					// Load from db
					responseBuilder = Response.ok(data);
				} else if (getter.getReturnType() == String.class) {
					// Load from disk
					File file = new File(data.toString());
					if (file.exists()) {
						data = FileUtils.readFileToByteArray(file);
						responseBuilder = Response.ok(data);
						responseBuilder.header("Content-Disposition", "attachment;filename=" + file.getName());
					} else {
						throw new FileNotFoundException("File " + file.getAbsolutePath() + " does not exsist!");
					}
				}
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error downloading file " + e.getMessage(), e);
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error downloading file " + e.getMessage());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}
		return responseBuilder.build();
	}
	
	/**
	 * 
	 *  UTILITY functions
	 *  
	 */
		
	// Function to decode the restrictions and add path parameter
	protected Map<String,List<String>> decodeResctrictions(PathSegment restrictions) throws UnsupportedEncodingException {
		
		LinkedHashMap<String,List<String>> restrictionOrderedMap = new LinkedHashMap<String,List<String>>();
		
		String restrictionString = restrictions.toString();
		
		if (restrictionString != null && !restrictionString.isEmpty()) {
			String[] restrictionArr = restrictionString.split(";");
			for (String rstr: restrictionArr) {
				String key = rstr;
				String value = "";
				if (rstr.contains("=")) {
					String[] keyValue = rstr.split("=");
					if (keyValue.length > 0) {
						key = keyValue[0];
						if (keyValue.length == 2) {
							value =	keyValue[1];
						}
					} else {
						continue;
					}
				}
				if (restrictionOrderedMap.containsKey(key)) {
					restrictionOrderedMap.get(key).add(value);
				} else {
					List<String> valueLst = new ArrayList<String>();
					valueLst.add(value);
					restrictionOrderedMap.put(key, valueLst);
				}
			}
		}

		String value = "";

		// Decode restrictionMap
		for (List<String> valueList : restrictionOrderedMap.values()) {
			for (int i = 0; i < valueList.size(); i++) {
				value = valueList.get(i);
				value = value.replaceAll("\\+", "%2b"); //FIX for URLDecoder but reserve the plus sign(+)
				value = URLDecoder.decode(value, "utf-8");
				valueList.set(i, value);
			}		
		}
		
		return restrictionOrderedMap;
	}
	
	// Function to decode date
	public Date decodeISO8601(String date) throws Exception {
		
		try {
			/*
			//JAVA EE
			Date date = DatatypeConverter.parseDateTime(src).getTime();
		
			return date;
			*/
			
			//JODA time
			DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
	    	Date d = fmt.parseLocalDateTime(date).toDate();
			
	    	return d;
	    	
		} catch (Exception e) {
			
			// Sometimes the server replaces the char '+' (used for the time zone) with the char ' '
			date = date.replace(" ", "+");
			
			try {
				/*
				//JAVA EE
				Date date = DatatypeConverter.parseDateTime(src).getTime();
			
				return date;
				*/
				
				//JODA time
				DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
		    	Date d = fmt.parseLocalDateTime(date).toDate();
				
		    	return d;
		    	
			} catch (Exception ex) {
				log.error("[cid="+Conversation.instance().getId()+"] Wrong format of date: " + date, ex);
				throw ex;
			}
						
		}
	}
		
	// Function to convert value from String		
	public Object convertValue(Class<?> propertyClass, String value) throws Exception {
		Object obj = new Object();
		
		if (propertyClass.isAssignableFrom(String.class)) {
			if (value.contains(",")) {
				obj = value.split(",");
			} else {
				obj = value;
			}			
		} else if ((propertyClass.isAssignableFrom(Boolean.class) || propertyClass.isAssignableFrom(boolean.class)) && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
			obj = Boolean.parseBoolean(value);
		} else if (propertyClass.isAssignableFrom(Date.class)) {
			obj = decodeISO8601(value);
		} else if (propertyClass.isAssignableFrom(Integer.class) || propertyClass.isAssignableFrom(int.class)) {
			obj = Integer.parseInt(value);
		} else if (propertyClass.isAssignableFrom(Long.class) || propertyClass.isAssignableFrom(long.class)) {
			obj = Long.parseLong(value);
		} else if (propertyClass.isAssignableFrom(Double.class) || propertyClass.isAssignableFrom(double.class)) {
			obj = Double.parseDouble(value);
		} else {
			throw new Exception("Only String, Boolean, Date or Number parameters are supported!");
		}
		 
		return obj;
	}
	
	// Function to deproxy attributes if required			
	public static void loadProxy(Object entity, String path) throws Exception {
				
		String childName = path;
		if (path.contains(".")) {
			childName = path.substring(0,path.indexOf("."));
		}
		
		PropertyDescriptor pd = null;
		pd = PropertyUtils.getPropertyDescriptor(entity, childName);
		if (pd != null) { //property found
			Class<?> childClass = pd.getPropertyType(); 
			if (childClass.isAssignableFrom(List.class)) {
				@SuppressWarnings("unchecked")
				List<Object> entityChildren = (List<Object>)(PropertyUtils.getProperty(entity, childName));
				if (entityChildren != null && entityChildren.size() > 0) {
					for (int z = 0; z < entityChildren.size(); z++) { //deproxy all elements in list
						if (entityChildren.get(z) instanceof HibernateProxy) {
							entityChildren.set(z, ((HibernateProxy)entityChildren.get(z)).getHibernateLazyInitializer().getImplementation());
						}
					}					
					String newPath = "";
					for (Object entityChild : entityChildren) {
						//iteration
						if (path.indexOf(".")!=-1) {
							newPath = path.substring(path.indexOf(".") + 1,path.length());
							loadProxy(entityChild, newPath);
						} 
					}
				}
			} else {
				Object entityChild = PropertyUtils.getProperty(entity, childName);
				if (entityChild != null) {
					if (entityChild instanceof HibernateProxy) { //deproxy
						entityChild = (((HibernateProxy)entityChild).getHibernateLazyInitializer().getImplementation());
						PropertyUtils.setProperty(entity, childName, entityChild);
					}
					//iteration
					if (path.indexOf(".")!=-1) {
						String newPath = path.substring(path.indexOf(".") + 1,path.length());
						loadProxy(entityChild, newPath);
					} 
				}				
			}
		} else {
			throw new Exception("Parameter load: "+childName+" does note exist for entity "+entity.getClass().getSimpleName());
		}
	}
	
	// Function to get the required URL 
	protected String getUrl4Pagination() {
		String readUrl = servletRequest.getRequestURI();
		readUrl = readUrl.substring(readUrl.indexOf("/", 1) + 1, readUrl.lastIndexOf("/") + 1);
		return readUrl;
	}
	
	// Function to search a property in a class given the property name
	private static Field getField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Field field = null;
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
           try {
        	   field = c.getDeclaredField(fieldName);
        	   break;
           } catch (NoSuchFieldException e) {
        	   log.debug("[cid="+Conversation.instance().getId()+"] Field " + fieldName + " not found in Class " + c.getName() + ". Searching in superclass.");        	   
           }       	
        }
        if (field != null)
        	return field;
        else
        	throw new NoSuchFieldException("Field " + fieldName + " not found in Class " + type.getName() + " nor superclasses.");
    }
		
	// Function to add restrictions to action
	protected void restrictions2action(RestAction action, Map<String,List<String>> restrictionMap) throws Exception {
		
		if (restrictionMap.containsKey("filterBySdl")) {
			action.setFilterBySdl(false);
		}
		
		for (String key: restrictionMap.keySet()) {
			
			List<String> values = restrictionMap.get(key);
			
			for(String value : values) {
				if (!key.isEmpty()){
											
					if (key.equals("readPageSize")) {						
						continue;
					}
					
					if (key.equals("filterBySdl")) {						
						continue;
					}

					// SELECT
					if (value == null || value.isEmpty()) {
						action.getSelect().add(key);
					} else {
					
						Class<?> propertyClass = null;
						
						if (key.contains(".")) {
							String[] parts = key.split("\\."); 
					        Class<?> currentEntityClass = entityClass;
					        
					        for (int i = 0; i < parts.length; i++) {
					        	propertyClass = getField(currentEntityClass, parts[i]).getType();
					        	if (i != parts.length - 1) 
					        	{
					        		if (propertyClass.isAssignableFrom(List.class)) {
					        			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(currentEntityClass);
					        			
					        			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					        				if ((parts[i]).equals(propertyDescriptor.getName())) {
					        					Type type = propertyDescriptor.getWriteMethod().getGenericParameterTypes()[0];
					        					currentEntityClass = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
					        					continue;
					        				}
					        			}
							        } else {
							        	currentEntityClass = propertyClass;
							        }
					        	}
					        }
						} else {
							propertyClass = getField(entityClass, key).getType();
						}
													
						// GREATER
						if (value.startsWith(">>")) {
							value = value.substring(2);
							action.getGreater().put(key, convertValue(propertyClass, value));
						// GREATER EQUAL
						} else if (value.startsWith(">")) {
							value = value.substring(1);
							action.getGreaterEqual().put(key, convertValue(propertyClass, value));
						// LESS
						} else if (value.startsWith("<<")) {
							value = value.substring(2);
							action.getLess().put(key, convertValue(propertyClass, value));								
						// LESS EQUAL
						} else if (value.startsWith("<")) {
							value = value.substring(1);
							action.getLessEqual().put(key, convertValue(propertyClass, value));							
						// NOT EQUAL
						} else if (value.startsWith("!")) {
							value = value.substring(1);
							action.getNotEqual().put(key, convertValue(propertyClass, value));	
						// LIKE
						} else if (value.startsWith("~")) {
							if (propertyClass.isAssignableFrom(String.class)) {
								if (value.startsWith("~~")) {//FULL LIKE
									action.setFullLike(true);
									action.getLike().put(key, value.substring(2));
									action.setFullLike(false);
								} else {
									action.getLike().put(key, value.substring(1));
								}
							} else {
								throw new Exception("Only String is supported for LIKE");
							}
						// IS NULL
						} else if (value.startsWith("*")) {
							value = value.substring(1);
							if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
								action.getIsNull().put(key, Boolean.parseBoolean(value));
							} else {
								throw new Exception("The value for IS NULL has to be a Boolean");
							}
						// ORDER BY	
						} else if (value.equals("ascending") || value.equals("descending")) {
					        action.getOrderBy().put(key, value);
					    // EQUAL
						} else {
							// If there is already the same property in equal, create a list and add values or add value to the existent list
							if (action.getEqual().get(key) != null) {
								// List already present: add new value to List
								if (action.getEqual().get(key) instanceof List<?>) {
									@SuppressWarnings("unchecked")
									List<Object> equals = (List<Object>) action.getEqual().get(key);
									equals.add(convertValue(propertyClass, value));
									action.getEqual().put(key, equals);
								// List not present: create new List and add the two values (the one already present and the new one)
								} else {
									List<Object> newEquals = new ArrayList<Object>();
									Object firstEqual = action.getEqual().get(key);
									newEquals.add(firstEqual);
									newEquals.add(convertValue(propertyClass, value));
									action.getEqual().put(key, newEquals);
								}
							} else {
								action.getEqual().put(key, convertValue(propertyClass, value));	
							}								
						}
					}
				}
			}
		}
	}
	
	protected void addBanner(InjectDatamodel<T> dm) {

		Banner b = Banner.instance();

		Map<String, Object> deproxyedEntities = new HashMap<String, Object>();
		
		for (String key : b.getEntitiesKeys()) {
			Object e = b.getEntity(key);
			if (e instanceof HibernateProxy) {
				if(!((HibernateProxy)e).getHibernateLazyInitializer().isUninitialized()) { //if is initialized, get the object
					deproxyedEntities.put( key, ((HibernateProxy)e).getHibernateLazyInitializer().getImplementation() );
				}
			} else {
				deproxyedEntities.put(key,e);
			}
		}
		
		dm.setBanner(b);
		dm.setBannerEntities(deproxyedEntities);
	}
	
	protected String getUploadPath() {
		String uploadFolder = uploadFolderDefault;
		try {
			ParameterManager pm = ParameterManager.instance();
			Object uploadFolderParameter = pm.getParameter("p.general.uploadFolder", "value");

			if (uploadFolderParameter != null) {
				uploadFolder = uploadFolderParameter.toString();
				if (!uploadFolder.endsWith("/")) {
					uploadFolder = uploadFolder + "/";
				}
			} else {
				log.warn("Parameter p.general.uploadFolder not exist! Using default path: " + uploadFolderDefault);
			}
		} catch (PhiException e) {
			log.error("Error getting parameter: p.general.documentPath", e);
		}
		return uploadFolder;
	}

}