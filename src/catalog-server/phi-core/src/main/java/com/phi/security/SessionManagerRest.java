package com.phi.security;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("SessionManagerRest")
@Path("/websessions")
public class SessionManagerRest {

	private static final Logger log = Logger.getLogger(SessionManagerRest.class);
	
	private List<String> fileNameList=new ArrayList<String>();
	
	@GET
	@Path("/filenames")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileNames()  {
		//String json = "[\"server.log\",\"server.log.2015-08-03\"]";
		StringBuilder builder= new StringBuilder("[");
		String serverLogPath = "";
		//SSA applications use phi.deploy.dir (defined in /conf/custom.properties)
		//because deploy-apps dir is not inside jboss.server.home.dir
		if(System.getProperty("phi.deploy.dir")!=null){
			serverLogPath = System.getProperty("phi.deploy.dir")+File.separator+"log";
		}else{
			serverLogPath = System.getProperty("jboss.server.home.dir")+File.separator+"log";
		}	
		
		File folder = new File(serverLogPath);
		fileNameList.clear();
		boolean firstRound=true;
		for (final File fileEntry : folder.listFiles()) {
			if(!firstRound){
				builder.append(",");
			}
	        if (!fileEntry.isDirectory()) {
	        	builder.append("\"").append(fileEntry.getName()).append("\"");
	        	firstRound=false;
	        	fileNameList.add(fileEntry.getName());
	        }  
	    }
		builder.append("]");
		return Response.ok(builder.toString()).build();
	}
	
	@GET
	@Path("/file/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFile(@PathParam("name") String name)  {
		ResponseBuilder response;
		fileNameList=this.getFileList();
		if(fileNameList.contains(name)){
			String serverLogPath = "";
			//SSA applications use phi.deploy.dir (defined in /conf/custom.properties)
			//because deploy-apps dir is not inside jboss.server.home.dir
			if(System.getProperty("phi.deploy.dir")!=null){
				serverLogPath = System.getProperty("phi.deploy.dir")+File.separator+"log"+File.separator+name;
			}else{
				serverLogPath = System.getProperty("jboss.server.home.dir")+File.separator+"log"+File.separator+name;
			}	
			
			File serverLogFile = new File(serverLogPath);
			response = Response.ok((Object) serverLogFile);
		}
		else{
			log.error("Security exception. File "+name+" is not in the available list.");
			response = Response.ok();
		}
        return response.build();
	}

	private List<String> getFileList() {
		String serverLogPath = "";
		
		//SSA applications use phi.deploy.dir (defined in /conf/custom.properties)
		//because deploy-apps dir is not inside jboss.server.home.dir
		if(System.getProperty("phi.deploy.dir")!=null){
			serverLogPath = System.getProperty("phi.deploy.dir")+File.separator+"log";
		}else{
			serverLogPath = System.getProperty("jboss.server.home.dir")+File.separator+"log";
		}	
		
		File folder = new File(serverLogPath);
		List<String> fileNameList=new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	fileNameList.add(fileEntry.getName());
	        }  
	    }
		return fileNameList;
	}
}
