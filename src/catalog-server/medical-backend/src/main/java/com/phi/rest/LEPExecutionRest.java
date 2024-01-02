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
import com.phi.entities.act.LEPExecution;
import com.phi.events.PhiEvent;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("LEPExecutionRest")
@Path("/lepexecutions")
public class LEPExecutionRest extends BaseRest<LEPExecution> implements	Serializable {

	private static final long serialVersionUID = -6472901933992181862L;
	
	
	private static final String EXECUTION_DIARY_DETAILS_HQL =
			" SELECT" +
			" execution.executionDate.low as executionDate," +
			" execution.note as note," +
			" objective.internalId as objective_internalId," +
			" objectiveStatus.langIt as objective_status," +
			" objective.cancellationDate as objective_cancellationDate," +
			" lepActivity.effectiveDate.high as activity_endDate," +
			" lepActivity.validityPeriod.high as activity_invalidationDate," +
			" (SELECT clinicalDiary.internalId FROM ClinicalDiary clinicalDiary LEFT JOIN clinicalDiary.execution exe WHERE exe.internalId = execution.internalId AND clinicalDiary.isActive = true) as noteId" +
			" FROM" +
			" LEPExecution execution" +
			" LEFT JOIN execution.lepActivity lepActivity" +
			" LEFT JOIN lepActivity.objective objective" +
			" LEFT JOIN objective.objReached objectiveStatus" +
			" WHERE execution.internalId = :exeId";
	
	
	@GET
	@Path("executionDiaryDetails/{executionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response executionDiaryDetails(@PathParam("executionId") long executionId){
		try {			
			String currentLang = Locale.instance().getLanguage();
			
			String executionDiaryDetailsHql = EXECUTION_DIARY_DETAILS_HQL;
						
			if (!currentLang.equals("it")) {
				executionDiaryDetailsHql = executionDiaryDetailsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			Query qryExecutionDiaryDetails = ca.createHibernateQuery(executionDiaryDetailsHql);
			
			qryExecutionDiaryDetails.setParameter("exeId", executionId);
			
			qryExecutionDiaryDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryExecutionDiaryDetails.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error LEPExecutionRest executionDiaryDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error LEPExecutionRest executionDiaryDetails").build();
		}
	}
}
