package com.phi.rest.ps;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.json.HibernateModule;
import com.phi.ps.Node;
import com.phi.ps.ProcessManager;
import com.phi.ps.ProcessManagerImpl;
import com.phi.ps.TreeBean;
import com.phi.ps.ViewManager;

/**
 * Rest Process Manager
 * http://localhost:8080/PHI_AMB/resource/rest/processmanager/processlist
 */
@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProcessManagerRest")
@Path("/processmanager")
public class ProcessManagerRest {

	protected static final Logger log = Logger.getLogger(ProcessManagerRest.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		//mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	@javax.ws.rs.core.Context
	HttpServletRequest servletRequest;

	@GET
	@Path("processlist")
	@Produces("application/json; charset=utf-8")
	public Response getProcessList() {
		
		try {
			
			TreeBean treeBean = TreeBean.instance();
			
			List<Node> processList = treeBean.getProcessList();
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<String,HashMap<String, Object>> param = (Map)Contexts.getSessionContext().get("Param");
			
			Map<String,Object> results = new HashMap<String, Object>();
			
			ProcessManager pm = ProcessManagerImpl.instance();
			
			pm.beginConversation("HOME");
			
			results.put("processList", processList);
			
			results.put("version", RepositoryManager.instance().getVersionNumber());
		
			results.put("cid", Conversation.instance().getId());

			String result = mapper.writeValueAsString(results);
			
			return Response.ok(result).build();

		} catch (Exception e) {
			log.error("Error getetting process list" , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getetting process list " + e.getMessage()).build();
		}

	}
	
	@POST
	@Path("/")
	//@Consumes("application/json; charset=utf-8")
	@Produces("application/json; charset=utf-8")
	//@Produces(MediaType.TEXT_PLAIN)
	public Response executeProcess(@FormParam("processName") String processName) {
		
		try {
			
			ProcessManager pm = ProcessManagerImpl.instance();
			
			pm.startProcessFromName(processName);
			
			String cid = Conversation.instance().getId();
			
			ViewManager vm = ViewManager.instance();
			String viewId = vm.getViewIdWithoutExtension();
			
			if (viewId != null && viewId.startsWith("/")) {
				viewId = viewId.substring(1);
			}
			
			//viewId = "." + viewId + ".seam?cid=" + cid; 
			
			
			Map<String, Object> retData = getConversationContext();
			
//GET CONVERSATION CONTEXT
//			//FIXME COPIED FROM ConversationRest -> unify
//			Map<String, Object> retData = new HashMap<String, Object>();
//			
//			Context currentConv = Contexts.getConversationContext();
//
//			String[] names = currentConv.getNames();
//			Object object = null;
//
//			for(String name:names) {
//				object = currentConv.get(name);
//				if (object instanceof BaseEntity || object instanceof IdataModel){
//					if (object instanceof IdataModel) {
//						retData.put(name, ((IdataModel) object).getList()); 
//					} else {
//						if (object instanceof HibernateProxy) {
//							if(!((HibernateProxy)object).getHibernateLazyInitializer().isUninitialized()) { //if is initialized, get the object
//								object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
//							}
//						}
//						retData.put(name, object);
//					}
//				}
//				if (object instanceof BaseAction){
//					retData.put(name, object);
//				}
//			}
			
//END
			
			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("viewId", viewId);
			result.put("cid", Integer.parseInt(cid));
			result.put("data", retData);

			String resultStr = mapper.writeValueAsString(result);
			
			return Response.ok(resultStr).build();

		} catch (Exception e) {
			log.error("Error executeProcess " + processName, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getetting process list " + e.getMessage()).build();
		}

	}
	
//FIXME: to use @Consumes("application/json; charset=utf-8") browser sends options prefight and server must respond to OPTIONS req, but doesn't work
//	@OPTIONS
//	@Path("managetask")
//	@Produces("application/json; charset=UTF-8")
//	public Response manageTaskOptions() {
//	    return Response.ok().build();
//	}

//	@Consumes("application/json; charset=utf-8") //FIXME: use this
//	public Response manageTask(String jSonEntity) {
	//@Consumes("application/x-www-form-urlencoded; charset=UTF-8")
	//public Response manageTask(MultivaluedMap<String, String> formParams) {
	
	@POST
	@Path("managetask")
	@Produces("application/json; charset=utf-8")
	public Response manageTask(
			@FormParam("btnId") String btnId, 
			@FormParam("btnMnemonic") String btnMnemonic, 
			@FormParam("inject") String inject,
			@FormParam("fields") String jsonFields, 
			@FormParam("values") String jsonValues) {
		String task = "";
		try {

			ProcessManager pm = ProcessManagerImpl.instance();

			List<Map<String, String>> fields;
			HashMap<String, List<String>> entities;

			task = btnId + ";" + btnMnemonic;

			fields = mapper.readValue(jsonFields, List.class);

			entities = mapper.readValue(jsonValues, HashMap.class);

			PropertyUtilsBean propBean = BeanUtilsBean.getInstance().getPropertyUtils();
			
			for (String entityName: entities.keySet()) {
				Object entityOrList = entities.get(entityName);
				if (entityOrList instanceof Map) {
					Map<String, Object> values = (Map)entities.get(entityName);
					Object entity = Component.getInstance(entityName);
					if (entity != null) {
						for (String valueName: values.keySet()) {
							PropertyDescriptor pd = propBean.getPropertyDescriptor(entity, valueName);
							if (pd != null) {
								try {
									Object propzObj = pd.getReadMethod().invoke(entity);
									if (propzObj instanceof Map) {
										Map propz = (Map)propzObj;
										Map<String, Object> valuez = (Map)values.get(valueName);
										for (String valuezName: valuez.keySet()) {
											Object valuezz = valuez.get(valuezName);
											propz.put(valuezName, valuezz);
										}
									} else {
										Object value = values.get(valueName);
										Class<?> type = pd.getPropertyType();
										Method setMethod = pd.getWriteMethod();
										if (setMethod != null) {
											//FIXME: change deserialization
											if (Long.TYPE.equals(type) && value instanceof Integer) {
												value = ((Integer)value).longValue();
											}
											if (Date.class.equals(type) && value instanceof String) {
												DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
												value = fmt.parseLocalDateTime(((String)value)).toDate();
											}
											if (AD.class.equals(type) || IVL.class.equals(type) || EN.class.equals(type) || TEL.class.equals(type)) {
												log.error("SKIPPING " + entityName + " . " + valueName + " -> to be implemented");
												continue;
											}
											setMethod.invoke(entity, value);
										}
									}
								} catch (Exception e) {
									log.error("ERROR " + entityName + " . " + valueName + " -> ", e);
								}
							} else {
								log.error("ENTITY " + entityName + " doesen't contain property: " + valueName);
							}
						}
					} else {
						log.error("ENTITY " + entityName + " Not in conversation!!!");
					}
				} else if (entityOrList instanceof List) {
					//FIXME
					log.error("ENTITY " + entityName + " is list -> to be implemented");
				}
			}
				
			Context conversation = Contexts.getConversationContext();
			
			//Inject and proceed FIXME change format to avoid substring
			if (inject != null) {
				int indexOfArr = inject.indexOf("[");
				String listName = inject.substring(0, indexOfArr);
				String entityNameToInject = listName.substring(0, listName.length() - 4);
				String indexStr = inject.substring(indexOfArr + 1, inject.length() - 1);
				int index = Integer.parseInt(indexStr);
				IdataModel idm = (IdataModel)conversation.get(listName);
				if (idm != null && idm.getList() != null) {
					Object entityToInject = idm.getList().get(index);
					conversation.set(entityNameToInject, entityToInject);
				}
			}
			//Inject and proceed END
			
			
			pm.manageTask(task);

			
			Map<String, Object> retData = getConversationContext();
			
//			Map<String, Object> retData = new HashMap<String, Object>();
//			
//			for (Map<String,String> fieldDescriptor : fields) {
//				if ("FieldArray".equals(fieldDescriptor.get("type"))) {
//					IdataModel dm = (IdataModel)conversation.get(fieldDescriptor.get("name"));
//					if (dm != null) {
//						retData.put(fieldDescriptor.get("name"), dm.getList());
//					}
//				} else { //Field
//					//FIXME
//					log.warn("FIELD " + fieldDescriptor.get("name") + " TO BE IMPLEMENTED");
//				}
//			}

			ViewManager vm = ViewManager.instance();
			String viewId = vm.getViewIdWithoutExtension();

			if (viewId != null && viewId.startsWith("/")) {
				viewId = viewId.substring(1);
			}

			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("viewId", viewId);
			result.put("data", retData);
			//FIXME REMOVE
//			Object emp = retData.get("Employee");
			String resultStr;
//			if (emp != null) {
//				resultStr = mapper.writeValueAsString(emp);
//			} else {
				resultStr = mapper.writeValueAsString(result);
//			}

			return Response.ok(resultStr).build();

		} catch (Exception e) {
			log.error("Error manageTask " + task, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error manageTask " + task + " error: " + e.getMessage()).build();
		}

	}
	
	
	
	@DELETE
	@Path("/{processName}")
	@Produces("application/json; charset=utf-8")
	public Response endProcess(@PathParam("processName") String processName) {
		
		try {
			
			ViewManager.instance().goHome();
			
			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("cid", Conversation.instance().getId());

			String resultStr = mapper.writeValueAsString(result);
			
			return Response.ok(resultStr).build();

		} catch (Exception e) {
			log.error("Error endProcess " + processName, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error ending process " + processName + " " + e.getMessage()).build();
		}

	}
	
	private Map<String, Object> getConversationContext() {
		//FIXME COPIED FROM ConversationRest -> unify
		Map<String, Object> retData = new HashMap<String, Object>();
		
		Context currentConv = Contexts.getConversationContext();

		String[] names = currentConv.getNames();
		Object object = null;

		for(String name:names) {
			object = currentConv.get(name);
			if (object instanceof BaseEntity || object instanceof IdataModel){
				if (object instanceof PhiDataModel) {
					retData.put(name, ((PhiDataModel) object).getList()); 
				} else if (object instanceof PagedDataModel) {
					retData.put(name, ((PagedDataModel) object).getEntities().getWrappedData());
				} else {
					if (object instanceof HibernateProxy) {
						if(!((HibernateProxy)object).getHibernateLazyInitializer().isUninitialized()) { //if is initialized, get the object
							object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
						}
					}
					retData.put(name, object);
				}
			}
			if (object instanceof BaseAction){
				retData.put(name, object);
			}
		}
		
		return retData;
		
	}
}