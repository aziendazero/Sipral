package com.phi.rest.conversation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationEntry;
import org.jboss.seam.core.Manager;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.actions.CodeSystemAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.json.HibernateModule;
import com.phi.security.SessionManager;
import com.phi.security.UserBean;

/**
 * Manages conversation from rest.
 * 
 * Examples:
 * GET /conversations		: get list of active conversations
 * GET /conversations/10	: get conversation with id 10
 * POST /conversations		: create a new conversation, return id
 * PUT /conversations/8		: end conversation with id 8 flushing all the changes, begin a new conversation and return new id
 * DELETE /conversations/12	: end conversation with id 12 without flush, begin a new conversation and return new id
 * 
 * @author alex.zupan
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ConversationRest")
@Path("/conversations")
public class ConversationRest implements Serializable {
	
	private static final long serialVersionUID = -2311629177837665153L;
	protected static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
	private static final Logger log = Logger.getLogger(ConversationRest.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
//	//Jackson parser
//	protected ObjectMapper mapper;
//
//	public ConversationRest() {
//
//		mapper = new ObjectMapper();
//		mapper.setSerializationInclusion(Include.NON_EMPTY);
//		
//	}
	
	/**
	 * Get list of conversations
	 * @throws Exception
	 */
	
	@GET
	@Path("/list")
	@Produces(APPLICATION_JSON_UTF8)
	public String get() throws Exception {
		try {
			ConversationEntries conversationEntries = ConversationEntries.getInstance();
			
			Collection<ConversationEntry> results = conversationEntries.getConversationEntries();
			
			String json = mapper.writeValueAsString(results);
			
			return json;

		} catch (Exception e) {
			log.error("Error get conversations", e);
			throw e;
		}
	}

	/**
	 * Get a conversation by id
	 *  
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/") //id comes from ?cid
	@Produces(APPLICATION_JSON_UTF8)
	public String getCurrent() throws Exception {
		Context currentConv = Contexts.getConversationContext();
		 
		Map<String,Object> conversationObjects = new HashMap<String,Object>();
		Map<String,Object> conversationActions = new HashMap<String,Object>();
			
		String[] names = currentConv.getNames();
		Object object = null;
		SimplifiedAction action = null;
		for(String name:names) {
			object = currentConv.get(name);
			if (object instanceof BaseEntity || object instanceof IdataModel){
				if (object instanceof PagedDataModel) {
					conversationObjects.put(name, ((PagedDataModel) object).getFullList()); 
				} else {
					if (object instanceof HibernateProxy) {
						if(!((HibernateProxy)object).getHibernateLazyInitializer().isUninitialized()) { //if is initialized, get the object
							object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
						}
					}
					conversationObjects.put(name, object);
				}
			}
			if (object instanceof BaseAction && !(object instanceof CodeSystemAction)){
				action = new SimplifiedAction();
				action.entity = ((BaseAction)object).getEntity();
				action.temporary = ((BaseAction)object).getTemporary();
				
				action.select = ((BaseAction)object).getSelect();
				action.equal = ((BaseAction)object).getEqual();
				action.greaterEqual = ((BaseAction)object).getGreaterEqual();
				action.greater = ((BaseAction)object).getGreater();
				action.lessEqual = ((BaseAction)object).getLessEqual();
				action.like = ((BaseAction)object).getLike();
				action.less = ((BaseAction)object).getLess();
				action.notEqual = ((BaseAction)object).getNotEqual();
				action.isNull = ((BaseAction)object).getIsNull();
				action.in = ((BaseAction)object).getIn();
				action.notIn = ((BaseAction)object).getNotIn();
				action.orderBy = ((BaseAction)object).getOrderBy();
				action.fullLike = ((BaseAction)object).getFullLike();
				action.readPageSize = ((BaseAction)object).getReadPageSize();
				
				action.lockingEmployee = ((BaseAction)object).getLockingEmployee();
				action.lockingRole = ((BaseAction)object).getLockingRole();
				
				conversationActions.put(name, action);
			}else if(object instanceof CodeSystemAction){
				action = new SimplifiedAction();
				action.entity = ((BaseAction)object).getEntity();
				action.temporary = ((BaseAction)object).getTemporary();
				
				action.select = ((BaseAction)object).getSelect();
				action.equal = ((BaseAction)object).getEqual();
				action.greaterEqual = ((BaseAction)object).getGreaterEqual();
				action.greater = ((BaseAction)object).getGreater();
				action.lessEqual = ((BaseAction)object).getLessEqual();
				action.like = ((BaseAction)object).getLike();
				action.less = ((BaseAction)object).getLess();
				action.notEqual = ((BaseAction)object).getNotEqual();
				action.isNull = ((BaseAction)object).getIsNull();
				action.in = ((BaseAction)object).getIn();
				action.notIn = ((BaseAction)object).getNotIn();
				action.orderBy = ((BaseAction)object).getOrderBy();
				action.fullLike = ((BaseAction)object).getFullLike();
				action.readPageSize = ((BaseAction)object).getReadPageSize();
				
				conversationActions.put(name, action);				
			}
		}
		Map<String,Object> conversationCollection = new HashMap<String,Object>();
		conversationCollection.put("objects",conversationObjects);
		conversationCollection.put("actions",conversationActions);
		String json = mapper.writeValueAsString(conversationCollection);
		
		return json;
	}
	
	private class SimplifiedAction {
		public Object entity;
		public HashMap<String, Object> temporary;
		
		public List<String> select;
		public HashMap<String, Object> equal;
		public HashMap<String, Object> greaterEqual;
		public HashMap<String, Object> greater;
		public HashMap<String, Object> lessEqual;
		public HashMap<String, Object> like;
		public HashMap<String, Object> less;
		public HashMap<String, Object> notEqual;
		public HashMap<String, Object> isNull;
		public HashMap<String, Object> in;
		public HashMap<String, Object> notIn;
		public HashMap<String, Object> orderBy;
		public Boolean fullLike;
		public Integer readPageSize;
		
		public Object lockingEmployee;
		public String lockingRole;
	}
	/**
	 * Create new conversation, return id
	 * 
	 * If the custom header "x-method-override" is set to "UPDATE" then update() method is called. 
	 * Use it if PUT HTTP method isn't available.
	 * 
	 * If the custom header "x-method-override" is set to "DELETE" then delete() method is called. 
     * Use it if DELETE HTTP method isn't available.
	 * 
	 * @return id on the new conversation
	 * @throws Exception
	 */
	
	@POST
	@Path("/")
//	@Path("/{id : (/id)?}") //Optional parameter id for update
	@Produces(MediaType.TEXT_PLAIN)
	public String create(@FormParam("id") String id, @FormParam("description") String description, @HeaderParam("x-method-override") String methodOverride) throws Exception {
	
		try {			
			if (methodOverride != null) {
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					update(id);
					return ""; //FIXME
				} else if (methodOverride.equalsIgnoreCase("DELETE")) {
					delete(id);
					return id;
				} else {
					log.error("Error x-method-override contains unknown method: " + methodOverride);
					throw new IllegalArgumentException("Error x-method-override contains unknown method: " + methodOverride);
				}
			} else { // CREATE
				Conversation conv = Conversation.instance();
				conv.begin();
				conv.setDescription(description);
				Manager.instance().killAllOtherConversations();
				SessionManager.instance().updateConversation(UserBean.instance().getUsername(), Integer.parseInt(conv.getId()), Contexts.getConversationContext());
				return conv.getId();
			}
		} catch (Exception e) {
			log.error("Error create conversation ", e);
			throw e;
		}

	}
	
	/**
	 * Update a conversation
	 * 
	 * It may get invoked by the @POST equivalent if the "x-method-override" header is configured for "UPDATE"
	 *
	 * @throws Exception
	 */
	
	@PUT
	@Path("/{id}")
	@Consumes(APPLICATION_JSON_UTF8)
	public void update(@PathParam("id") String id) throws Exception {
		try {
			
			CatalogPersistenceManagerImpl.instance().flushSession();
			
			ConversationEntries conversationEntries = ConversationEntries.getInstance();
			ConversationEntry conversation = conversationEntries.getConversationEntry(id);
			
			conversation.end();

		} catch (Exception e) {
			log.error("Error delete conversation", e);
			throw e;
		}
	}

	/**
	 * Delete conversation
	 * Ends a conversation by id, witout flushing 
	 * 
	 * It may get invoked by the @POST equivalent if the "x-method-override" header is configured for "DELETE"
	 * 
	 * @param id
	 * @throws Exception
	 */
	
	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) throws Exception {
		try {
			ConversationEntries conversationEntries = ConversationEntries.getInstance();
			ConversationEntry conversation = conversationEntries.getConversationEntry(id);
			
			conversation.end();

		} catch (Exception e) {
			log.error("Error delete conversation", e);
			throw e;
		}
	}
	
}