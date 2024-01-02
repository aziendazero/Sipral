package com.phi.rest;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.view.banner.Banner;
// import com.phi.entities.act.PatientEncounter;
import com.phi.json.HibernateModule;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("BannerRest")
@Path("/banner")
public class BannerRest implements Serializable {
	
	private static final Logger log = Logger.getLogger(BannerRest.class);
	
	private static final long serialVersionUID = 1132234933280346934L;
	protected static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	@GET
	@Path("/")
	@Produces(APPLICATION_JSON_UTF8)
	public Response get() throws JsonProcessingException {
		ResponseBuilder responseBuilder = null;
		String result;
		result = mapper.writeValueAsString(getBanner());
		responseBuilder = Response.ok(result);
		return responseBuilder.build();
	}
	
	public Map<String, Object> getBanner() {

		Banner b = Banner.instance();

		Map<String, Object> deproxyedEntities = new HashMap<String, Object>();
		
		for (String key : b.getEntitiesKeys()) {
			Object entityOrProxy = b.getEntity(key);
			Object deproxyedEntity = null;
			
			if (entityOrProxy instanceof HibernateProxy) {
				LazyInitializer lazyInit = ((HibernateProxy)entityOrProxy).getHibernateLazyInitializer();
				if(lazyInit.isUninitialized()) {
					lazyInit.initialize();
				}
				deproxyedEntity = lazyInit.getImplementation();
			} else {
				deproxyedEntity = entityOrProxy;
			}
		
			deproxyedEntities.put(key, deproxyedEntity);

			if ("Patient".equals(key)) {
				try {
					BaseRest.loadProxy(deproxyedEntity, "consent");
					BaseRest.loadProxy(deproxyedEntity, "doctor");
					BaseRest.loadProxy(deproxyedEntity, "genderCode");
					BaseRest.loadProxy(deproxyedEntity, "language");
				} catch (Exception ex) {
					log.error("Error deproxing patient", ex);
				}
			}
		}
		
		Map<String, Object> banner = new HashMap<String, Object>();
		banner.put("banner", b);
		banner.put("bannerEntities", deproxyedEntities);
		return banner;
	}

}
