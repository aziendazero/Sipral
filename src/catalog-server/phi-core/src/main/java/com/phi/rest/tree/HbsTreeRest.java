package com.phi.rest.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.actions.CodeValueRoleAction;
import com.phi.entities.actions.EmployeeRoleAction;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.rest.datamodel.HBSManager;
import com.phi.rest.datamodel.TreeNodeObject;
import com.phi.security.UserBean;

@Path("/tree/hbs")
public class HbsTreeRest {

	private static final Logger log = Logger.getLogger(HbsTreeRest.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		//mapper.registerModule(new HibernateModule());
	}
	
	private static final String QRY_SDL_ALL_CHILDREN = 
			"SELECT child.internalId " +
			"FROM " +
				"ServiceDeliveryLocation child, " +
				"ServiceDeliveryLocation parent " +
			"WHERE " +
				"child.nodeInfo.nsLeft between parent.nodeInfo.nsLeft and parent.nodeInfo.nsRight " +
				"and parent.internalId = :sdlId";

	@POST
	@Path("inject")
	@Produces("application/json; charset=utf-8")
	public Response inject(
			@FormParam("id") String id, 
			@FormParam("class") String clazz) {

		try{
			Serializable serialId;
			if (id.matches("\\d*")) {
				serialId = Long.parseLong(id);
			} else {
				serialId = id;
			}

			Object obj = CatalogPersistenceManagerImpl.instance().load(clazz, serialId);
			Contexts.getConversationContext().set(obj.getClass().getSimpleName(), obj);
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("getTree")
	@Produces("application/json; charset=utf-8")
	public Response getTree(
			@FormParam("levelOfDepth") String levelOfDepthString, 
			@FormParam("parentId") String parentIdString) {

		try{
			HBSManager hbs = new HBSManager();
			
			Integer levelOfDepth = null;
			Long parentId = null;
			
			if (levelOfDepthString != null) {
				levelOfDepth = Integer.parseInt(levelOfDepthString);
			}
			if (parentIdString != null && parentIdString.matches("\\d*")) {
				parentId = Long.parseLong(parentIdString);
			}
			
			String json = hbs.getJsonTree(null, false, false, levelOfDepth, parentId, null);
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("getTree4CurrentEmployeeRole")
	@Produces("application/json; charset=utf-8")
	public Response getTree4CurrentEmployeeRole(
			@FormParam("levelOfDepth") String levelOfDepthString, 
			@FormParam("parentId") String parentIdString) {

		try{
			HBSManager hbs = new HBSManager();
			
			EmployeeRole eRole = EmployeeRoleAction.instance().getEntity();
			Integer levelOfDepth = null;
			Long parentId = null;
			
			if (levelOfDepthString != null) {
				levelOfDepth = Integer.parseInt(levelOfDepthString);
			}
			if (parentIdString != null && parentIdString.matches("\\d*")) {
				parentId = Long.parseLong(parentIdString);
			}
			
			String json = hbs.getJsonTree(eRole.getInternalId(), false, false, levelOfDepth, parentId, null);
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("getTree4CurrentUser")
	@Produces("application/json; charset=utf-8")
	public Response getTree4CurrentUser(
			@FormParam("levelOfDepth") String levelOfDepthString, 
			@FormParam("parentId") String parentIdString,
			@FormParam("selectedRole") String selectedRoleString) {

		try{
			HBSManager hbs = new HBSManager();
			
			Integer levelOfDepth = null;
			Long parentId = null;
			
			if (levelOfDepthString != null) {
				levelOfDepth = Integer.parseInt(levelOfDepthString);
			}
			if (parentIdString != null && parentIdString.matches("\\d*")) {
				parentId = Long.parseLong(parentIdString);
			}
			
			String loginSdlocFromCondevalueRole =  RepositoryManager.instance().getSeamProperty("loginSdlocFromCondevalueRole");
			
			Long selectedRole = Long.parseLong(selectedRoleString);
			boolean sdlocFromCondevalueRole = false;
			
			if ("true".equals(loginSdlocFromCondevalueRole)) {
				sdlocFromCondevalueRole = true;
			}

			String json = hbs.getJsonTree(selectedRole, sdlocFromCondevalueRole, true, levelOfDepth, parentId, null);
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("getTree4CodeValueRole")
	@Produces("application/json; charset=utf-8")
	public Response getTree4CodeValueRole(
			@FormParam("levelOfDepth") String levelOfDepthString, 
			@FormParam("parentId") String parentIdString) {

		try{
			HBSManager hbs = new HBSManager();
			
			Integer levelOfDepth = null;
			Long parentId = null;
			
			if (levelOfDepthString != null) {
				levelOfDepth = Integer.parseInt(levelOfDepthString);
			}
			if (parentIdString != null && parentIdString.matches("\\d*")) {
				parentId = Long.parseLong(parentIdString);
			}
			
			CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();

			List<Long> cvrEnabledSdl = new ArrayList<Long>();

			if (codeValueRole.getEnabledServiceDeliveryLocations() != null) {

				for (ServiceDeliveryLocation sdl : codeValueRole.getEnabledServiceDeliveryLocations()) {
					cvrEnabledSdl.add(sdl.getInternalId());
				}
			}
			String json = hbs.getJsonTree(null, false, false, levelOfDepth, parentId, cvrEnabledSdl);
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("create")
	@Produces("application/json; charset=utf-8")
	public Response create(
			@FormParam("id") String id, 
			@FormParam("name") String name,
			@FormParam("node_type") String node_type) {

		try{
			HBSManager hbs = new HBSManager();
			
			hbs.setId_node(Long.parseLong(id));
			hbs.setNode_name(name);
			hbs.setNode_code(node_type);

			TreeNodeObject treeNode = hbs.add();

			String json = mapper.writeValueAsString(treeNode);
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("rename")
	@Produces("application/json; charset=utf-8")
	public Response rename(
			@FormParam("id") String id, 
			@FormParam("name") String name) {

		try{
			HBSManager hbs = new HBSManager();
			hbs.setId_node(Long.parseLong(id));
			hbs.setNode_name(name);

			hbs.rename();
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("disable")
	@Produces("application/json; charset=utf-8")
	public Response disable(
			@FormParam("id") String id, 
			@FormParam("name") String name) {

		try{
			HBSManager hbs = new HBSManager();
			hbs.setId_node(Long.parseLong(id));
			hbs.setNode_name(name);

			hbs.disable();
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("reEnable")
	@Produces("application/json; charset=utf-8")
	public Response reEnable(
			@FormParam("id") String id, 
			@FormParam("name") String name) {

		try{
			HBSManager hbs = new HBSManager();
			hbs.setId_node(Long.parseLong(id));
			hbs.setNode_name(name);

			hbs.reEnable();
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("delete")
	@Produces("application/json; charset=utf-8")
	public Response delete(
			@FormParam("id") String id) {

		try{
			HBSManager hbs = new HBSManager();
			hbs.setId_node(Long.parseLong(id));

			hbs.delete();
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("select")
	@Produces("application/json; charset=utf-8")
	public Response select(
			@FormParam("selected") String selected,
			@FormParam("parents[]") String parents[],
			@FormParam("class") String clazz) {

		try{

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			EmployeeRole empRole = EmployeeRoleAction.instance().getEntity();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("sdlId", Long.parseLong(selected));

			@SuppressWarnings("unchecked")
			List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
			
			List<Long> parentAndChilds = new ArrayList<Long>();
			if (parents != null) {
				for (String parent : parents) {
					parentAndChilds.add(Long.parseLong(parent));
				}
			}
			parentAndChilds.addAll(ids);
			
			for (int i=0; i < parentAndChilds.size(); i++ ) {
				
				ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, parentAndChilds.get(i));
				empRole.addEnabledServiceDeliveryLocations(sdloc);
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("select4CodeValueRole")
	@Produces("application/json; charset=utf-8")
	public Response select4CodeValueRole(
			@FormParam("selected") String selected,
			@FormParam("parents[]") String parents[],
			@FormParam("class") String clazz) {

		try{

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();

			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("sdlId", Long.parseLong(selected));

			@SuppressWarnings("unchecked")
			List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
			
			List<Long> parentAndChilds = new ArrayList<Long>();
			if (parents != null) {
				for (String parent : parents) {
					parentAndChilds.add(Long.parseLong(parent));
				}
			}
			parentAndChilds.addAll(ids);
			
			for (int i=0; i < parentAndChilds.size(); i++ ) {
				
				ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, parentAndChilds.get(i));
				codeValueRole.addEnabledServiceDeliveryLocations(sdloc);
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("select4CurrentUser")
	@Produces("application/json; charset=utf-8")
	public Response select4CurrentUser(
			@FormParam("id[]") String ids[]) {

		try{

			List<Long> sdLocs = UserBean.instance().getSdLocs();

			for (String id : ids) {
				Long longId = Long.parseLong(id);
				if (!sdLocs.contains(longId)) {
					sdLocs.add(longId);
				}
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("unselect")
	@Produces("application/json; charset=utf-8")
	public Response unselect(
			@FormParam("unselected") String unselected,
			@FormParam("class") String clazz) {

		try{

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			EmployeeRole empRole = EmployeeRoleAction.instance().getEntity();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("sdlId", Long.parseLong(unselected));

			@SuppressWarnings("unchecked")
			List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
			
			for (int i=0; i <ids.size(); i++ ) {

				ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, ids.get(i));
				empRole.removeEnabledServiceDeliveryLocations(sdloc);
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("unselect4CodeValueRole")
	@Produces("application/json; charset=utf-8")
	public Response unselect4CodeValueRole(
			@FormParam("unselected") String unselected,
			@FormParam("class") String clazz) {

		try{

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("sdlId", Long.parseLong(unselected));

			@SuppressWarnings("unchecked")
			List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
			
			for (int i=0; i <ids.size(); i++ ) {

				ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, ids.get(i));
				codeValueRole.removeEnabledServiceDeliveryLocations(sdloc);   
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("unselect4CurrentUser")
	@Produces("application/json; charset=utf-8")
	public Response unselect4CurrentUser(
			@FormParam("id[]") String ids[]) {

		try{

			List<Long> sdLocs = UserBean.instance().getSdLocs();

			for (String id : ids) {
				Long longId = Long.parseLong(id);
				if (sdLocs.contains(longId)) {
					sdLocs.remove(longId);
				}
			}
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
}
