package com.phi.rest;

import java.io.Serializable;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.hibernate.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.entities.act.SubstanceAdministration;
import com.phi.events.PhiEvent;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("SubstanceAdministrationRest")
@Path("/substanceadministrations")
public class SubstanceAdministrationRest extends BaseRest<SubstanceAdministration> implements Serializable {

	private static final long serialVersionUID = -653748969886019734L;
	
	
	private static final String ADMINISTRATION_AS_NEEDED_DETAILS_HQL =
			" SELECT" +
			" administration.administratedDate.low as administratedDate," +
			" administration.text.string as note," +
			" (SELECT admin.internalId FROM SubstanceAdministration admin LEFT JOIN admin.prescription prescription WHERE prescription.internalId = administration.prescription.internalId AND admin.administratedDate.low IS null AND prescription.needsBased = true) as originalId" +
			" FROM" +
			" SubstanceAdministration administration" +
			" WHERE" +
			" administration.internalId = :adminId";
	
	
	@GET
	@Path("administrationAsNeededDetails/{administrationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response administrationAsNeededDetails(@PathParam("administrationId") long administrationId){
		try {			
			String currentLang = Locale.instance().getLanguage();
			
			String  administrationAsNeededDetailsHql = ADMINISTRATION_AS_NEEDED_DETAILS_HQL;
						
			if (!currentLang.equals("it")) {
				administrationAsNeededDetailsHql =  administrationAsNeededDetailsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			Query qryAdministrationAsNeededDetails = ca.createHibernateQuery(administrationAsNeededDetailsHql);
			
			qryAdministrationAsNeededDetails.setParameter("adminId", administrationId);
			
			qryAdministrationAsNeededDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryAdministrationAsNeededDetails.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error SubstanceAdministrationRest administrationAsNeededDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error SubstanceAdministrationRest administrationAsNeededDetails").build();
		}
	}
			
}
