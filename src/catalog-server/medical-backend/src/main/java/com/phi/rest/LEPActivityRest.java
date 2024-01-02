package com.phi.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.hibernate.Query;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.exception.PhiException;
import com.phi.entities.act.LEPActivity;
import com.phi.entities.act.LEPExecution;
import com.phi.entities.actions.LEPActivityAction;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.events.PhiEvent;
import com.phi.security.UserBean;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("LEPActivityRest")
@Path("/lepactivitys")
public class LEPActivityRest extends BaseRest<LEPActivity> implements Serializable {

	private static final long serialVersionUID = 8887568292204757557L;
	
	public static final String VALIDATE = "validate";
	public static final String REVALIDATE = "revalidate";
	public static final String INVALIDATE = "invalidate";
	public static final String INVALIDATE_AND_MODIFY = "invalidate_and_modify";
	
	
	private static final String ACTIVITY_DETAILS_HQL =
			" SELECT" +
			" responsibleRole.code as responsibleCode," +
			" activity.supportNumber as supportNumber," +
			" supportRole.code as supportCode," +
			" lepCode.code as code," +
			" activity.note as note," +
			" activity.timeSpent as plannedTime," +
			" objective.text.string as objectiveTitle" +
			" FROM" +
			" LEPActivity activity" +
			" LEFT JOIN activity.responsibleRole responsibleRole" +
			" LEFT JOIN activity.supportRole supportRole" +
			" LEFT JOIN activity.nandaLep lepCode" +
			" LEFT JOIN activity.objective objective" +
			" WHERE" +
			" activity.internalId = :activityId";
	
	private static final String SUGGESTED_LEP_HQL =
			" SELECT" +
			" suggestedLep.id as id" +
			" FROM" +
			" Nanda nanda" +
			" LEFT JOIN nanda.nandaDiag nandaCode" +
			" LEFT JOIN nandaCode.relationsNanda suggestedLep" +			
			" WHERE nanda.internalId = :nandaId" +
			" AND suggestedLep.id LIKE '1.2.16.840.1.113883.3.20.K630%'" +
			" ORDER BY suggestedLep.langIt";
	
	
	/*
	 * 	FIXME - Custom create method
	 * 	Eliminabile? Non fa niente di diverso rispetto a quello di BaseRest.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
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
				LEPActivity entity = mapper.readValue(jSonEntity, entityClass);
					
				if (entity.getLepExecution() != null) {
					for (LEPExecution ex: entity.getLepExecution()) { //Fixme add proxy to non created object!
						ex.setLepActivity(entity);
					}
				}
				
				ca.create(entity);
				ca.flushSession();
				
				LEPActivityAction.instance().validateActivity(entity);
				
				String jsonId = mapper.writeValueAsString(entity.getInternalId());
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + entity.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, entity);
				
				return Response.ok(jsonId).build(); //FIXME change to created
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
	
	@PUT
	@POST
	@Path("/status/{activityId : \\d+}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
	public Response updateExecutionsStatus(@PathParam("activityId") long activityId, @FormParam("action") String action) throws PhiException {
		LEPActivity activity = (LEPActivity)ca.get(entityClass, activityId);				
		
		if(!(action.equals(VALIDATE) || action.equals(REVALIDATE) || action.equals(INVALIDATE))) {
			//FIXME: change to not valid
			return Response.ok("The action specified :" + action + "is not valid").build(); 
		}
		
		if(activity == null) {
			//FIXME: change to not found
			return Response.ok("Activity by Id :" + activityId + " not found").build(); 
		}
		
		LEPActivityAction lepActivityAction = new LEPActivityAction();	
		lepActivityAction.inject(activity);		
		lepActivityAction.refresh();
		
		try {
					
			if (action.equals(VALIDATE)) {
				lepActivityAction.validateActivity(activity);
			} else if (action.equals(REVALIDATE)) {
				lepActivityAction.revalidateActivity(activity);
			} else if (action.equals(INVALIDATE)) {
				lepActivityAction.invalidateActivity(activity);
			} 
			
		} catch (PhiException e) {
			log.error("Error updating status of executions of activity with id: " + activityId + " and action: " + action, e);
			return Response.ok("{\"error\":\"Error updating status of executions of activity with id: " + activityId + " and action: " + action + "\"}").build();
		}
		
		//FIXME: change to created
		return Response.ok(activity.getInternalId()).build(); 
	}
	
	@GET
	@Path("activityDetails/{activityId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activityDetails(@PathParam("activityId") long activityId){
		try {			
			String currentLang = Locale.instance().getLanguage();
			
			String  activityDetailsHql = ACTIVITY_DETAILS_HQL ;
						
			if (!currentLang.equals("it")) {
				activityDetailsHql =  activityDetailsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			Query qryActivityDetails = ca.createHibernateQuery(activityDetailsHql);
			
			qryActivityDetails.setParameter("activityId", activityId);
			
			qryActivityDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String,Object> result = (Map<String, Object>) qryActivityDetails.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error LEPActivityRest activityDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error LEPActivityRest activityDetails").build();
		}
	}
	
	@GET
	@Path("favoriteLep/{wardId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readFavoriteLEP(@PathParam("wardId") long wardId){	
		try {		
			
			ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, wardId);
			
			List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
			//FIXME: CHANGE RELATION!!
//			for (CodeValue cv : sdl.getFavoriteLep()) {
//				
//				Map<String,Object> code = new HashMap<String, Object>();
//				Class<CodeValue> cvClass = HibernateProxyHelper.getClassWithoutInitializingProxy(cv);
//				cv = ca.get(cvClass, cv.getId());
//				
//				code.put("id", cv.getId());
//				code.put("code", cv.getCode());	
//				if (code.get("code") == null || code.get("code") == "") {
//					code.put("code", cv.getDisplayName());	
//				}
//				code.put("type", cv.getType());				
//				code.put("langIt", cv.getLangIt());
//				code.put("langDe", cv.getLangDe());
//				code.put("langEn", cv.getLangEn());					
//													
//				results.add(code);
//			}
			
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
	
			return Response.ok(json).build();
		
		} catch (Exception e) {
			log.error("Error LEPActivityRest getFavoriteLEP", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error LEPActivityRest getFavoriteLEP").build();
		}
	}
		
	@GET
	@Path("suggestedLep/{nandaId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readSuggestedLEP(@PathParam("nandaId") long nandaId){	
		try {				
				String currentLang = Locale.instance().getLanguage();			
				String suggestedLepHql = SUGGESTED_LEP_HQL ;						
				if (!currentLang.equals("it")) {
					suggestedLepHql =  suggestedLepHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				}
				
				Query qrySuggestedLep = ca.createHibernateQuery(suggestedLepHql);
				qrySuggestedLep.setParameter("nandaId", nandaId);
				
				qrySuggestedLep.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
							
				List<Map<String,Object>> results = (List<Map<String,Object>>) qrySuggestedLep.list();
			
				for (Map<String,Object> code : results) {
					
					CodeValue cv = ca.get(CodeValue.class, (String) code.get("id"));
				
					code.put("code", cv.getCode());				
					code.put("type", cv.getType());				
					code.put("langIt", cv.getLangIt());
					code.put("langDe", cv.getLangDe());
					code.put("langEn", cv.getLangEn());						
				}
			
				String json = mapper.writeValueAsString(results);
				
				Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
	
				return Response.ok(json).build();
		
		} catch (Exception e) {
			log.error("Error LEPActivityRest getSuggestedLEP", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error LEPActivityRest getSuggestedLEP").build();
		}
	}
	
}