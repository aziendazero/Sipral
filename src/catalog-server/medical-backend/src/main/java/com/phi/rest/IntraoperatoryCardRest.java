package com.phi.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.act.VitalSign;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.IntraoperatoryCard;
import com.phi.entities.baseEntity.Medicine;
import com.phi.events.PhiEvent;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("IntraoperatoryCardRest")
@Path("/intraoperatorycards")
public class IntraoperatoryCardRest extends BaseRest<IntraoperatoryCard> {

	private static final long serialVersionUID = 5373792996799105528L;


	@GET
	@Path("{id : \\d+}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") long id) {
		try {

			Context conversation = Contexts.getConversationContext();
			
			BaseAction<?, ?> action = (BaseAction<?, ?>)Component.getInstance(conversationName+"Action");
			
			if (id > 0) {
	
				entity = ca.get(entityClass, id);
				
				if (entity == null) {
					return Response.status(Response.Status.NOT_FOUND).entity("Entity " + entityClass.getSimpleName() + " not found for id: " + id).build();
				}
				
				if (entity instanceof HibernateProxy) { //de proxy
					entity = ((IntraoperatoryCard)((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation());
				}
				
				
				List<VitalSign> vs = entity.getVitalSign();
					
				if (vs != null) {
					for (int z = 0; z<vs.size(); z++) {
						if (vs.get(z) instanceof HibernateProxy) {
							vs.set(z, ((VitalSign)((HibernateProxy)vs.get(z)).getHibernateLazyInitializer().getImplementation()));
						}
					}
				}

				if (entity.getMedicine1() instanceof HibernateProxy) { //de proxy
					entity.setMedicine1(((Medicine)((HibernateProxy)entity.getMedicine1()).getHibernateLazyInitializer().getImplementation()));
				}
				if (entity.getMedicine2() instanceof HibernateProxy) { //de proxy
					entity.setMedicine2(((Medicine)((HibernateProxy)entity.getMedicine2()).getHibernateLazyInitializer().getImplementation()));
				}
				if (entity.getMedicine3() instanceof HibernateProxy) { //de proxy
					entity.setMedicine3(((Medicine)((HibernateProxy)entity.getMedicine3()).getHibernateLazyInitializer().getImplementation()));
				}
				if (entity.getMedicine4() instanceof HibernateProxy) { //de proxy 
					entity.setMedicine4(((Medicine)((HibernateProxy)entity.getMedicine4()).getHibernateLazyInitializer().getImplementation()));
				}
				if (entity.getMedicine5() instanceof HibernateProxy) { //de proxy
					entity.setMedicine5(((Medicine)((HibernateProxy)entity.getMedicine5()).getHibernateLazyInitializer().getImplementation()));
				}
				if (entity.getMedicine6() instanceof HibernateProxy) { //de proxy
					entity.setMedicine6(((Medicine)((HibernateProxy)entity.getMedicine6()).getHibernateLazyInitializer().getImplementation()));
				}

				if (action == null) {
					conversation.set(conversationName, entity);
				} else {
					action.inject(entity);
				}
	
				String jsonEntity = mapper.writeValueAsString(entity);
				
				log.info("[cid="+Conversation.instance().getId()+"] Object injected: " + entityClass.getSimpleName() + ", id: " + id);
				
				Events.instance().raiseEvent(PhiEvent.INJECT, entity);
				
				return Response.ok(jsonEntity).build();
				
			} else { //eject
				
				if (action == null) {
					conversation.remove(conversationName);
				} else {
					action.eject();
				}
				
				log.info("[cid="+Conversation.instance().getId()+"] Object ejected: " + entityClass.getSimpleName() + ", id: " + id);
				return Response.ok().build();				
			}

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		}
	}
	
}