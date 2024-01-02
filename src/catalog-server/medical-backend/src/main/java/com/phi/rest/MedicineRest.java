package com.phi.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.entities.baseEntity.Medicine;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.ListDatamodel;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("MedicineRest")
@Path("/medicines")
public class MedicineRest extends BaseRest<Medicine> {

	private static final long serialVersionUID = 5092588099283470209L;
	
	private static final String hqlByName = "SELECT medicine.internalId as internalId, " +
			"medicine.name.giv as name, " +
			"substance.name.giv as substance, " +
			"organization.name.giv as organization, " +
			"medicine.reference as reference, " +
			"atc.code as atc, " +
			"aifa.id as aifa_id, " +
			"aifa.langIt as aifa_name " +
			"FROM Medicine medicine " +
			"LEFT JOIN medicine.substance substance " +
			"LEFT JOIN medicine.manufacturer organization " +
			"LEFT JOIN medicine.atcCode atc " +
			"LEFT JOIN medicine.aifaNote aifa " +
			"WHERE   medicine.isActive = true " +
			"AND medicine.reference = true " +
			"AND medicine.usageType.code <> 'C' " +
			"AND upper(medicine.name.giv) LIKE :nameGiv";
	
	/**
	 * Search Medicine by name
	 * @param nameGiv
	 * @param page
	 * @return
	 */
	
	@GET
	@Path("byname/{nameGiv}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@PathParam("nameGiv") String nameGiv, @DefaultValue("1") @PathParam("page") int page) {	
		try {
			
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}

			Query qryMedicinesByName = ca.createHibernateQuery(hqlByName);
			
			qryMedicinesByName.setParameter("nameGiv", "%" + nameGiv.toUpperCase() + "%");
			
			qryMedicinesByName.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
			qryMedicinesByName.setFirstResult((page - 1) * readPageSize);
			qryMedicinesByName.setMaxResults(readPageSize + 1);
						
			List<Map<String, Object>> result = qryMedicinesByName.list();
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(result, getUrl4Pagination(), readPageSize, page);
			
			String jsonEntity = mapper.writeValueAsString(dm);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("Error search Medicine by name", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error search Medicine by name" + e.getMessage()).build();
		}
	}
	
	
	private static final String hqlFavorite = "SELECT tab.internalId as tabId, " +
			"tab.title as tabTitle, " +
			"tab.numColumn as tabNumColumn, " +
			"tab.sortOrder as tabSortOrder, " +
			"tab.typeCode.code as tabTypeCode, " +
			"sec.internalId as secId, " +
			"sec.title as secTitle, " +
			"sec.sortOrder as secSortOrder, " +
			"sec.color as secColor, " +
			"sec.columnIndex as secColumnIndex, " +
			"profile.internalId as profileId, " +
			"profile.title as profileTitle, " +
			"profile.sortOrder as profileSortOrder, " +
			
			"med.internalId as internalId " + 
			
			"FROM FavoriteTab tab " +
			"LEFT JOIN tab.serviceDeliveryLocation sdl " +
			"LEFT JOIN tab.section sec WITH sec.isActive = true " +
			"LEFT JOIN sec.profile profile WITH profile.isActive = true " +
			
			"LEFT JOIN profile.prescription pres " +
			"LEFT JOIN pres.prescriptionMedicine pmed " +
			"LEFT JOIN pmed.medicine med " +
			
			"WHERE tab.isActive = true " +
			"AND sdl.internalId = :sdlocId " +
			"ORDER BY tab.typeCode.code, tab.sortOrder, sec.columnIndex, sec.sortOrder, profile.sortOrder";
	
	/**
	 * Find favorite medicines by sdloc
	 * @param sdlocId
	 * @return
	 */
	
	@GET
	@Path("favorite/{sdlocId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response favorite(@PathParam("sdlocId") long sdlocId) {
		
		try {			

			Query qryFavoritePrescriptions = ca.createHibernateQuery(hqlFavorite);
			
			qryFavoritePrescriptions.setParameter("sdlocId", sdlocId);
			
			qryFavoritePrescriptions.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			List result = qryFavoritePrescriptions.list();
			
			String jsonEntity = mapper.writeValueAsString(result);
	
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("Error IntraoperatoryCardRest favoritePrescriptions", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error IntraoperatoryCardRest favoritePrescriptions").build();
		}
	}

}