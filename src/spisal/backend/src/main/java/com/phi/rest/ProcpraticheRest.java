package com.phi.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.entities.baseEntity.DitteMalattie;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValue;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProcpraticheRest")
@Path("/procpratiches")
public class ProcpraticheRest extends BaseRest<Procpratiche> implements Serializable {

	private static final long serialVersionUID = 5655769207661290353L;
	
	@GET
	@Path("/list")
	@Produces(BaseRest.APPLICATION_JSON_UTF8)
	public String get() throws Exception {
		try {
			IdataModel<Procpratiche> listFromConversation = (IdataModel<Procpratiche>)Contexts.getConversationContext().get("ProcpraticheList");
			
			String json = "";
			if (listFromConversation instanceof PhiDataModel) {
				json = mapper.writeValueAsString(listFromConversation.getList());
			} else if (listFromConversation instanceof PagedDataModel) {
				List<Map<String, Object>> lst = (List)((PagedDataModel) listFromConversation).getEntities().getWrappedData();
				
				for (Map<String, Object> procpratiche: lst) {
					Map sdl = (Map)procpratiche.get("serviceDeliveryLocation");
					if (sdl != null) {
						CodeValue area = (CodeValue)sdl.get("area");
						if (area instanceof HibernateProxy) {
							sdl.put("area", ((HibernateProxy)area).getHibernateLazyInitializer().getImplementation());
						}
						if (area != null) {
							if ("WORKACCIDENT".equals(area.getCode())) {
								Criteria infCrit = ca.createCriteria(Infortuni.class);
										// .setProjection(Projections.projectionList().add(Projections.property("infortuniExt")));
								infCrit.add(Restrictions.eq("procpratiche.internalId", procpratiche.get("internalId")));
								List<Infortuni> infortuni = infCrit.list();
								for (Infortuni infortunio: infortuni) {
									if(infortunio.getInfortuniExt()!=null && (infortunio.getInfortuniExt() instanceof HibernateProxy)) {
										infortunio.setInfortuniExt(((InfortuniExt)((HibernateProxy)infortunio.getInfortuniExt()).getHibernateLazyInitializer().getImplementation()));
									}
									if(infortunio.getPlace()!=null && (infortunio.getPlace() instanceof HibernateProxy)) {
										infortunio.setPlace(((CodeValue)((HibernateProxy)infortunio.getPlace()).getHibernateLazyInitializer().getImplementation()));
									}
								}
								procpratiche.put("infortuni", infortuni);
							} else if ("SUPERVISION".equals(area.getCode())) {
								Map vigilanza = (Map)procpratiche.get("vigilanza");
								Map type = (Map)vigilanza.get("type");
								if ("Generic".equals(type.get("code")) && vigilanza.get("internalId") != null) {
									Criteria pgCrit = ca.createCriteria(PersonaGiuridicaSede.class);
									pgCrit.add(Restrictions.eq("vigilanza.internalId", vigilanza.get("internalId")));
									List<PersonaGiuridicaSede> pgs = pgCrit.list();
									for (PersonaGiuridicaSede pg: pgs) {
										if(pg.getSede()!=null && (pg.getSede() instanceof HibernateProxy)) {
											pg.setSede(((Sedi)((HibernateProxy)pg.getSede()).getHibernateLazyInitializer().getImplementation()));
										}
									}
									vigilanza.put("personaGiuridicaSede", pgs);
								}
							} else if ("WORKDISEASE".equals(area.getCode())) {
								Map malattiaProfessionale = (Map)procpratiche.get("malattiaProfessionale");
								if (malattiaProfessionale != null && malattiaProfessionale.get("internalId") != null) {
									Criteria dmCrit = ca.createCriteria(DitteMalattie.class);
									dmCrit.add(Restrictions.eq("malattiaProfessionale.internalId", malattiaProfessionale.get("internalId")));
									List<DitteMalattie> dms = dmCrit.list();
									for (DitteMalattie dm: dms) {
										if(dm.getSedi()!=null && (dm.getSedi() instanceof HibernateProxy)) {
											dm.setSedi(((Sedi)((HibernateProxy)dm.getSedi()).getHibernateLazyInitializer().getImplementation()));
										}
									}
									malattiaProfessionale.put("ditteMalattie", dms);
								}
								
							}
						}
					}
				}

				json = mapper.writeValueAsString(lst);
			}
			
			return json;

		} catch (Exception e) {
			log.error("Error get ProcpraticheList", e);
			throw e;
		}
	}
}