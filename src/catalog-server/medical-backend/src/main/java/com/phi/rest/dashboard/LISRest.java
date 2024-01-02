package com.phi.rest.dashboard;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;

/**
 * LIS Dashboard rest methods init and refresh
 * 
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("LISRest")
@Path("/lis")
public class LISRest extends BaseDashboardRest {

	private static final long serialVersionUID = 5997108853652524016L;
	
	protected Integer readPageSize = 10000;
	
	private static final String qry = "SELECT " +
			"labres.patient_id AS patient_id, " +
			"labgrp.patient_encounter_id AS encounter_id, " +
			"labgrp.internal_id AS group_id, " +
			"labres.internal_id AS exam_id, " +
			"labres.value AS exam_value, " +
			"cvdp.display_name AS group_name, " +
			"cvd.display_name AS exam_name, " +
			"labres.availability_time AS exam_date, " +
			"labgrp.DNWEB4_HIS_TDF AS dianoema_code, " +
			"labgrp.DNWEB4_HIS_REPORT AS report_code, " +
			"labgrp.availability_time AS group_date, " +
			"cve_status.code AS encounter_statusCode, " +
			"cvg_status.code AS group_statusCode, " +
			"labgrp.DNWEB4_HIS AS dnweb4hisCode, " +
			"labgrp.effective_time AS group_editDate, " +
			"cvd.typedb AS exam_type, " +
			"labgrp.anonymous_request AS exam_isAnonymous " +

			"FROM lab_result labres " + 
			"LEFT JOIN lab_request_group labgrp ON labres.lab_request_group_id=labgrp.internal_id " + 
			"LEFT JOIN code_value_dianoema cvd ON labres.exam=cvd.id " +
			"LEFT JOIN patient_encounter enc ON labres.patient_encounter_id=enc.internal_id " +
			"LEFT JOIN code_value cve_status ON enc.status_code=cve_status.id " +
			"LEFT JOIN code_value cvg_status ON labgrp.statusCode=cvg_status.id " +
			"LEFT JOIN code_value_dianoema cvdp ON cvd.code_value_parent=cvdp.id " + 
			"WHERE labgrp.patient_encounter_id=:encounterId " + 
			"and labgrp.availability_time >= :startDate and labgrp.availability_time <= :endDate " + 
			"ORDER BY labgrp.creation_date, labres.creation_date";
	
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			ListDatamodel<Map<String, Object>> dm = readLaboratoryList(restrictionMap, page);

			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			
			res.setMain(dm);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] LIS Init");
			log.debug("[cid="+Conversation.instance().getId()+"] LIS Init details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in LIS Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in lis init").build();
		}
	}
	
	//Refresh Exam List
	@GET
	@Path("refresh/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {	
		try {
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			ListDatamodel<Map<String, Object>> dm = readLaboratoryList(restrictionMap, page);
			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			
			res.setMain(dm);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] LIS Refresh");
			log.debug("[cid="+Conversation.instance().getId()+"] LIS Refresh details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in LIS Refresh with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in lis refresh").build();
		}
	}

	@Override
	public Response printReport(PathSegment restrictions) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	private ListDatamodel<Map<String, Object>> readLaboratoryList(MultivaluedMap<String, String> restrictionMap, int page){
		try {
			
			SQLQuery qry = ca.createHibernateNativeQuery(this.qry);
		
			qry.setParameter("encounterId", restrictionMap.get("encounterId").get(0));
		
			Date startDate = null;
			if (restrictionMap.get("startDate") != null) {
				startDate = decodeISO8601((String)restrictionMap.get("startDate").get(0));
				qry.setParameter("startDate", startDate);
			}
			
			Date endDate = null;
			if (restrictionMap.get("endDate") != null) {
				endDate = decodeISO8601((String)restrictionMap.get("endDate").get(0));
				qry.setParameter("endDate", endDate);
			}

			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
			qry.setFirstResult((page - 1) * this.readPageSize);
			qry.setMaxResults(this.readPageSize + 1);
		
			List<Map<String, Object>> resultsExecutions = qry.list();
		
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(resultsExecutions, getUrl4Pagination(), this.readPageSize, page);
		
			return dm;
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error readLaboratoryList", e);
			return null;
		}
	}
	

}
