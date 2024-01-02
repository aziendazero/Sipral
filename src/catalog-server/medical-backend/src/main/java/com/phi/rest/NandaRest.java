package com.phi.rest;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.act.Nanda;
import com.phi.entities.actions.PatientEncounterAction;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.events.PhiEvent;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("NandaRest")
@Path("/nandas")
public class NandaRest extends BaseRest<Nanda> implements Serializable {

	private static final long serialVersionUID = -6285003381100369164L;
	
	//etiologic factors
	private static final String NANDABF 	= "1.2.16.840.1.113883.3.20.K1550%";
	private static final String NANDABFIT 	= "1.2.16.840.1.113883.3.20.K1552%";
	//symptoms
	private static final String NANDABM 	= "1.2.16.840.1.113883.3.20.K1555%";
	private static final String NANDABMIT 	= "1.2.16.840.1.113883.3.20.K1557%";
	//risk factors
	private static final String NANDARF 	= "1.2.16.840.1.113883.3.20.K1560%";
	private static final String NANDARFIT 	= "1.2.16.840.1.113883.3.20.K1562%";

	private static final String NANDA_ETIOLOGIC_FACTORS_HQL = 
			" SELECT DISTINCT" +
			" etiologicFactor.id as id," +
			" etiologicFactor.langIt as currentTranslation" +		
			" FROM" +
			" CodeValueNanda nanda" +
			" LEFT JOIN nanda.relationsNanda etiologicFactor" +
			" WHERE etiologicFactor.id LIKE :equivalentIn" +									
			" AND nanda.id = :nandaId" +
			" ORDER BY etiologicFactor.langIt";
	
	private static final String NANDA_SYMPTOMS_HQL = 				
			" SELECT DISTINCT" +
			" symptom.id as id," +
			" symptom.langIt as currentTranslation" +			
			" FROM" +
			" CodeValueNanda nanda" +
			" LEFT JOIN nanda.relationsNanda symptom" +
			" WHERE symptom.id LIKE :equivalentIn" +									
			" AND nanda.id = :nandaId" +
			" ORDER BY symptom.langIt";
	
	private static final String NANDA_RISK_FACTORS_HQL = 	
			" SELECT DISTINCT" +
			" riskFactor.id as id," +
			" riskFactor.langIt as currentTranslation" +			
			" FROM" +
			" CodeValueNanda nanda" +
			" LEFT JOIN nanda.relationsNanda riskFactor" +
			" WHERE riskFactor.id LIKE :equivalentIn" +									
			" AND nanda.id = :nandaId" +
			" ORDER BY riskFactor.langIt";
	
	private static final String SUGGESTED_NANDA_HQL = 
			" SELECT" +
			" nanda.id as id" +
			" FROM" +
			" Checking check" +
			" JOIN check.suggestedDiagnoses nanda" +
			" JOIN check.patientEncounter encounter" +
			" WHERE encounter.internalId = :encounterId" +
			" AND check.confirmed = true" +
			" ORDER BY nanda.langIt";
	
	private static final String CHECKING_RESOURCES_HQL = 		
			" SELECT" +
			" CASE WHEN healthCheck.resHealth IS NOT NULL THEN ('label_Domain 1) ' || healthCheck.resHealth) ELSE NULL END as healthCheck," +
			" CASE WHEN nutritionCheck.resNutrition IS NOT NULL THEN ('label_Domain 2) ' || nutritionCheck.resNutrition) ELSE NULL END as nutritionCheck," +
			" CASE WHEN eliminationCheck.resEl IS NOT NULL THEN ('label_Domain 3) ' || eliminationCheck.resEl) ELSE NULL END as eliminationCheck," +
			" CASE WHEN activityCheck.resSleep IS NOT NULL THEN ('label_Domain 4) ' || activityCheck.resSleep) ELSE NULL END as activityCheck," +
			" CASE WHEN perceptionCheck.resPerception IS NOT NULL THEN ('label_Domain 5) ' || perceptionCheck.resPerception) ELSE NULL END as perceptionCheck," +
			" CASE WHEN selfPerceptionCheck.resPerc IS NOT NULL THEN ('label_Domain 6) ' || selfPerceptionCheck.resPerc) ELSE NULL END as selfPerceptionCheck," +
			" CASE WHEN rolesCheck.resRoles IS NOT NULL THEN ('label_Domain 7) ' || rolesCheck.resRoles) ELSE NULL END as rolesCheck," +
			" CASE WHEN sexualityCheck.resSex IS NOT NULL THEN ('label_Domain 8) ' || sexualityCheck.resSex) ELSE NULL END as sexualityCheck," +
			" CASE WHEN copingCheck.resCoping IS NOT NULL THEN ('label_Domain 9) ' || copingCheck.resCoping) ELSE NULL END as copingCheck," +
			" CASE WHEN principleCheck.resPrinciple IS NOT NULL THEN ('label_Domain 10) ' || principleCheck.resPrinciple) ELSE NULL END as principleCheck," +
			" CASE WHEN securityCheck.resSecurity IS NOT NULL THEN ('label_Domain 11) ' || securityCheck.resSecurity) ELSE NULL END as securityCheck," +
			" CASE WHEN welfareCheck.resWelfare IS NOT NULL THEN ('label_Domain 12) ' || welfareCheck.resWelfare) ELSE NULL END as welfareCheck," +
			" CASE WHEN growthCheck.resGrowth IS NOT NULL THEN ('label_Domain 13) ' || growthCheck.resGrowth) ELSE NULL END as growthCheck" +									
			" FROM" +
			" Checking check" +
			" LEFT JOIN check.healthCheck healthCheck" +
			" LEFT JOIN check.nutritionCheck nutritionCheck" +
			" LEFT JOIN check.eliminationCheck eliminationCheck" +
			" LEFT JOIN check.activityCheck activityCheck" +
			" LEFT JOIN check.perceptionCheck perceptionCheck" +
			" LEFT JOIN check.selfPerceptionCheck selfPerceptionCheck" +
			" LEFT JOIN check.rolesCheck rolesCheck" +
			" LEFT JOIN check.sexualityCheck sexualityCheck" +
			" LEFT JOIN check.copingCheck copingCheck" +
			" LEFT JOIN check.principleCheck principleCheck" +
			" LEFT JOIN check.securityCheck securityCheck" +
			" LEFT JOIN check.welfareCheck welfareCheck" +
			" LEFT JOIN check.growthCheck growthCheck" +
			" LEFT JOIN check.patientEncounter encounter" +									
			" WHERE encounter.internalId = :encounterId" +
			" AND check.confirmed = true";
	
	
	@Override
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			Response response;
			
			if (methodOverride != null) {
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					response = update(jSonEntity);
				} else if (methodOverride.equalsIgnoreCase("DELETE")) {
					Long id = Long.parseLong(jSonEntity);
					response = delete(id);
				} else {
					log.error("Error x-method-override contains unknown method: " + methodOverride);
					throw new IllegalArgumentException("Error x-method-override contains unknown method: " + methodOverride);
				}
			} else { // CREATE
				Nanda entity = mapper.readValue(jSonEntity, entityClass);
				
				ca.create(entity);
				ca.flushSession();
				
				this.entity = entity;
				
				String jsonId = mapper.writeValueAsString(entity.getInternalId());
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + entity.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, entity);
				
				response = Response.ok(jsonId).build(); //FIXME change to created
			}
			
			if (entity.getNandaDiag() != null && "00155".equals(entity.getNandaDiag().getCode())) {
				 PatientEncounterAction encounterAction = PatientEncounterAction.instance();				 
				 encounterAction.updateFallRisk();
			}
			
			return response;
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
	
	@GET
	@Path("nandaFactors/{nandaId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response nandaFactors(@PathParam("nandaId") String nandaId, @QueryParam("lang") @DefaultValue("it") String lang) {
		try {	
			Map<String,Object> results = new HashMap<String,Object>();
			
			String nandaEtiologicFactorsHql = NANDA_ETIOLOGIC_FACTORS_HQL;
			String nandaSymptomsHql = NANDA_SYMPTOMS_HQL;
			String nandaRiskFactorsHql = NANDA_RISK_FACTORS_HQL;
	        
			String currentLang = Locale.instance().getLanguage();
			
			if (!currentLang.equals("it")) {
				nandaEtiologicFactorsHql = nandaEtiologicFactorsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				nandaSymptomsHql = nandaSymptomsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				nandaRiskFactorsHql = nandaRiskFactorsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			// Etiologic factors
			Query qryNandaEtiologicFactors = ca.createHibernateQuery(nandaEtiologicFactorsHql);			
			qryNandaEtiologicFactors.setParameter("nandaId", nandaId);
			if ("it".equals(lang)) {
				qryNandaEtiologicFactors.setParameter("equivalentIn", NANDABFIT);
			} else {
				qryNandaEtiologicFactors.setParameter("equivalentIn", NANDABF);
			}
			qryNandaEtiologicFactors.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );						
			List<Map<String, Object>> etiologicFactors = qryNandaEtiologicFactors.list();
			
			for (Map<String,Object> code : etiologicFactors) {
				
				CodeValue cv = ca.get(CodeValue.class, (String) code.get("id"));
			
				code.put("code", cv.getCode());				
				code.put("type", cv.getType());				
				code.put("langIt", cv.getLangIt());
				code.put("langDe", cv.getLangDe());
				code.put("langEn", cv.getLangEn());						
			}
			
			results.put("etiologicFactors", etiologicFactors);
						
			// Symptoms
			Query qryNandaSymptoms = ca.createHibernateQuery(nandaSymptomsHql);			
			qryNandaSymptoms.setParameter("nandaId", nandaId);
			if ("it".equals(lang)) {
				qryNandaSymptoms.setParameter("equivalentIn", NANDABMIT);
			} else {
				qryNandaSymptoms.setParameter("equivalentIn", NANDABM);
			}
			qryNandaSymptoms.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );						
			List<Map<String, Object>> symptoms = qryNandaSymptoms.list();
			
			for (Map<String,Object> code : symptoms) {
				
				CodeValue cv = ca.get(CodeValue.class, (String) code.get("id"));
			
				code.put("code", cv.getCode());				
				code.put("type", cv.getType());				
				code.put("langIt", cv.getLangIt());
				code.put("langDe", cv.getLangDe());
				code.put("langEn", cv.getLangEn());						
			}
			
			results.put("symptoms", symptoms);
			
			// Risk factors
			Query qryNandaRiskFactors = ca.createHibernateQuery(nandaRiskFactorsHql);			
			qryNandaRiskFactors.setParameter("nandaId", nandaId);
			if ("it".equals(lang)) {
				qryNandaRiskFactors.setParameter("equivalentIn", NANDARFIT);
			} else {
				qryNandaRiskFactors.setParameter("equivalentIn", NANDARF);
			}
			qryNandaRiskFactors.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );						
			List<Map<String, Object>> riskFactors = qryNandaRiskFactors.list();
			
			for (Map<String,Object> code : riskFactors) {
				
				CodeValue cv = ca.get(CodeValue.class, (String) code.get("id"));
			
				code.put("code", cv.getCode());				
				code.put("type", cv.getType());				
				code.put("langIt", cv.getLangIt());
				code.put("langDe", cv.getLangDe());
				code.put("langEn", cv.getLangEn());						
			}
			
			results.put("riskFactors", riskFactors);
									
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error NandaRest nandaEtiologicFactors", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NandaRest nandaEtiologicFactors").build();
		}
	}
	
	
	@GET
	@Path("favoriteNanda/{wardId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readFavoriteNanda(@PathParam("wardId") long wardId){	
		try {		
			
			ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, wardId);
			
			List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
			
			//FIXME change relation!
//			for (CodeValue codeValue : sdl.getFavoritedDiagnoses()) {
//				
//				Map<String,Object> codeValueObj = new HashMap<String, Object>();
//				Class<CodeValue> cvClass = HibernateProxyHelper.getClassWithoutInitializingProxy(codeValue);
//				codeValue = ca.get(cvClass, codeValue.getId());
//				
//				codeValueObj.put("id", codeValue.getId());
//				codeValueObj.put("code", codeValue.getCode());
//				codeValueObj.put("displayName", codeValue.getDisplayName());
//				codeValueObj.put("type", codeValue.getType());				
//				codeValueObj.put("langIt", codeValue.getLangIt());
//				codeValueObj.put("langDe", codeValue.getLangDe());
//				codeValueObj.put("langEn", codeValue.getLangEn());		
//				
//				Map<String,Object> domainObj = new HashMap<String, Object>();				
//				CodeValue domainCodeValue = codeValue.getParent().getParent();
//				String domainId = domainCodeValue.getId();
//				int index = -1;
//				
//				for (Map<String,Object> currentDomainObj : results) {
//					if (currentDomainObj.get("id") == domainId) {
//						index = results.indexOf(currentDomainObj);
//						break;
//					}
//				}
//				
//				if (index == -1) {
//					domainObj.put("id", domainCodeValue.getId());
//					domainObj.put("code", domainCodeValue.getCode());	
//					domainObj.put("displayName", domainCodeValue.getDisplayName());
//					domainObj.put("type", domainCodeValue.getType());				
//					domainObj.put("langIt", domainCodeValue.getLangIt());
//					domainObj.put("langDe", domainCodeValue.getLangDe());
//					domainObj.put("langEn", domainCodeValue.getLangEn());	
//					domainObj.put("children", new ArrayList<Map<String,Object>>());
//					
//					((List<Map<String,Object>>) domainObj.get("children")).add(codeValueObj);
//					results.add(domainObj);			
//				} else {
//					Map<String,Object> currentDomainObj = results.get(index);
//					((List<Map<String,Object>>) currentDomainObj.get("children")).add(codeValueObj);
//				}				
//			}
			
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
	
			return Response.ok(json).build();
		
		} catch (Exception e) {
			log.error("Error NandaRest getFavoriteNanda", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NandaRest getFavoriteNanda").build();
		}
	}
	
	@GET
	@Path("suggestedNandaAndResources/{encounterId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readSuggestedNandaAndResources(@PathParam("encounterId") long encounterId) {
	
		try {		
			
			Map<String,Object> results = new HashMap<String,Object>();
			String currentLang = Locale.instance().getLanguage();
			
			String suggestedNandaHql = SUGGESTED_NANDA_HQL;
				        		
			if (!currentLang.equals("it")) {
				suggestedNandaHql = suggestedNandaHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			Query qrySuggestedNanda = ca.createHibernateQuery(suggestedNandaHql);			
			qrySuggestedNanda.setParameter("encounterId", encounterId);			
			qrySuggestedNanda.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );						
			List<Map<String, Object>> suggestedNanda = qrySuggestedNanda.list();
			
			List<Map<String,Object>> fixedSuggestedNanda = new ArrayList<Map<String,Object>>();
			
			for (Map<String,Object> codeValueObj : suggestedNanda) {
				
				if (codeValueObj.get("id") != null) {
					CodeValue codeValue = ca.get(CodeValue.class, (String) codeValueObj.get("id"));
			
					codeValueObj.put("code", codeValue.getCode());				
					codeValueObj.put("type", codeValue.getType());	
					codeValueObj.put("displayName", codeValue.getDisplayName());
					codeValueObj.put("langIt", codeValue.getLangIt());
					codeValueObj.put("langDe", codeValue.getLangDe());
					codeValueObj.put("langEn", codeValue.getLangEn());		
				
					Map<String,Object> domainObj = new HashMap<String, Object>();				
					CodeValue domainCodeValue = codeValue.getParent().getParent();
					String domainId = domainCodeValue.getId();
					int index = -1;
				
					for (Map<String,Object> currentDomainObj : fixedSuggestedNanda) {
						if (currentDomainObj.get("id") == domainId) {
							index = fixedSuggestedNanda.indexOf(currentDomainObj);
							break;
						}
					}
					
					if (index == -1) {
						domainObj.put("id", domainCodeValue.getId());
						domainObj.put("code", domainCodeValue.getCode());
						domainObj.put("displayName", domainCodeValue.getDisplayName());
						domainObj.put("type", domainCodeValue.getType());				
						domainObj.put("langIt", domainCodeValue.getLangIt());
						domainObj.put("langDe", domainCodeValue.getLangDe());
						domainObj.put("langEn", domainCodeValue.getLangEn());	
						domainObj.put("children", new ArrayList<Map<String,Object>>());
						
						((List<Map<String,Object>>) domainObj.get("children")).add(codeValueObj);
						fixedSuggestedNanda.add(domainObj);			
					} else {
						Map<String,Object> currentDomainObj = fixedSuggestedNanda.get(index);
						((List<Map<String,Object>>) currentDomainObj.get("children")).add(codeValueObj);
					}
				}
			}
			
			results.put("suggestedNanda", fixedSuggestedNanda);
			
			String checkingResourcesHql = CHECKING_RESOURCES_HQL;
			
			Pattern labelPattern = Pattern.compile("label_[^'\\s]*");
			
			Matcher matcher = labelPattern.matcher(checkingResourcesHql);
			
			while (matcher.find()) {
				String label = matcher.group();
				String key = label.substring(6);
				String translation = FunctionsBean.instance().getStaticTranslation(key);
				checkingResourcesHql = checkingResourcesHql.replace(label, translation);
				matcher.reset(checkingResourcesHql);
			}
			
			Query qryCheckingResources = ca.createHibernateQuery(checkingResourcesHql);			
			qryCheckingResources.setParameter("encounterId", encounterId);			
			qryCheckingResources.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );						
			Map<String, String> result = (Map<String, String>) qryCheckingResources.uniqueResult();			
			String[] checkingResourcesArray = new String[13];
			List<String> checkingResources = new ArrayList<String>();
			
			if (result != null) {
				// Order
				for (String checkingResource : result.values()) {
					if (checkingResource != null && checkingResource != "")	{
						String positionString = checkingResource.substring(checkingResource.indexOf(")") - 2, checkingResource.indexOf(")"));
						int position = Integer.parseInt(positionString.trim());
						checkingResourcesArray[position] = checkingResource;
					}
				}
				
				// Exclude null
				for (String checkingResource : checkingResourcesArray) {
					if (checkingResource != null && checkingResource != "")	{
						checkingResources.add(checkingResource);
					}
				}
			}
				
			results.put("checkingResources", checkingResources);
		
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());

			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error NandaRest getSuggestedNandaAndResources", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NandaRest getSuggestedNandaAndResources").build();
		}
	}
		
}
