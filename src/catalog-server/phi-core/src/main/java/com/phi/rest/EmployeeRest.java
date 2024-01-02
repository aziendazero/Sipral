package com.phi.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.cs.error.FacesErrorUtils;
import com.phi.entities.role.Employee;
import com.phi.security.AccessControl;
import com.phi.security.BaseAccessControlAction;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("EmployeeRest")
@Path("/employees")
public class EmployeeRest extends BaseRest<Employee> implements Serializable {

	private static final long serialVersionUID = -8339713943291895334L;
	
	private static final Logger log = Logger.getLogger(EmployeeRest.class);
	
	@PUT
	@Path("/password")
	//@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response updatePassword(@FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword, @FormParam("checkPassword") String checkPassword) {
		try {
			FacesErrorUtils.errorMessages = new ArrayList<String>();
			AccessControl ac = BaseAccessControlAction.instance();
			ac.changePassword(oldPassword, newPassword, checkPassword);
			if (FacesErrorUtils.errorMessages.size() > 0) {
				Map<String,Object> results = new HashMap<String, Object>();
				results.put("error", FacesErrorUtils.errorMessages);
				String json = mapper.writeValueAsString(results);
				//clean errors
				FacesErrorUtils.errorMessages = new ArrayList<String>();
				return Response.status(Response.Status.FORBIDDEN).entity(json).build();		
				//return Response.ok(json).build();		
			}
			return Response.ok().build();		
		} catch (Exception e) {
			log.error("Error changing password" , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error changing password " + e.getMessage()).build();
		}
	}
	
}