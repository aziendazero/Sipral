package com.phi.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.json.HibernateModule;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("HoursTestRest")
@Path("/hours")
public class HoursTestRest {
	
	//Jackson parser
//	protected ObjectMapper mapper;
	
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	@GET
	@Path("/days/{from : \\d+}/{to : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response days(@PathParam("from") int from, @PathParam("to") int to) {
		try {
			List<Date> allDates = new ArrayList<Date>();
			
			Calendar cal = Calendar.getInstance();
			
			cal.set(Calendar.YEAR, from);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_YEAR,1);
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			
			while (cal.get(Calendar.YEAR)<to){
				allDates.add(cal.getTime());
				cal.set(Calendar.DAY_OF_YEAR,cal.get(Calendar.DAY_OF_YEAR)+1);
			}
			
			String json = mapper.writeValueAsString(allDates);

			return Response.ok(json).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok("noo").build();
		}
	}
	
	
	@GET
	@Path("/hours/{from : \\d+}/{to : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response hours(@PathParam("from") int from, @PathParam("to") int to) {
		try {
			List<Date> allDates = new ArrayList<Date>();
			
			Calendar cal = Calendar.getInstance();
			
			cal.set(Calendar.YEAR, from);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_YEAR,1);
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			
			while (cal.get(Calendar.YEAR)<to){
				allDates.add(cal.getTime());
				cal.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY)+1);
			}
			
			String json = mapper.writeValueAsString(allDates);

			return Response.ok(json).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok("noo").build();
		}
	}
}
