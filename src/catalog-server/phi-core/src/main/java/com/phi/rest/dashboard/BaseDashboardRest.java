package com.phi.rest.dashboard;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.json.HibernateModule;

public abstract class BaseDashboardRest implements Serializable {

	private static final long serialVersionUID = -1557725669815835733L;

	protected static final Logger log = Logger.getLogger(BaseDashboardRest.class);
	
	protected CatalogAdapter ca;
	
	protected static final String BASE_REST_URL = "resource/rest/";
	protected static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	@Context
	HttpServletRequest servletRequest;
	
	//Page size
	protected Integer readPageSize = 20;
	
	
	public BaseDashboardRest() {
		ca = CatalogPersistenceManagerImpl.instance();
	}
	
	
	public abstract Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page);
	
	public abstract Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page);
	
	public abstract Response printReport(@PathParam("restrictions") PathSegment restrictions);
		
	protected MultivaluedMap<String, String> decodeResctrictions(PathSegment restrictions) throws UnsupportedEncodingException{
		MultivaluedMap<String, String> restrictionMap = restrictions.getMatrixParameters();

		// Add path to restrictionMap
		String path = restrictions.getPath();
	
		if (path.contains("=")) {
			String[] keyValue = path.split("=");
			
			if (restrictionMap.containsKey(keyValue[0])) {
				restrictionMap.get(keyValue[0]).add(keyValue[1]);
			} else {
					List<String> valueLst = new ArrayList<String>();
					valueLst.add(keyValue[1]);
					restrictionMap.put(keyValue[0], valueLst);
			}			
		} else {
			throw new IllegalArgumentException("First parameter doesn't contain =");			
		}
		
		// Decode restrictionMap
		for (List<String> valueList : restrictionMap.values()) {
			for (int i = 0; i < valueList.size(); i++) {
				String value = valueList.get(i);
				value = URLDecoder.decode(value, "UTF-8");
				valueList.set(i, value);
			}		
		}
		
		return restrictionMap;
	}
	
	public Date decodeISO8601(String date) throws Exception {
		
		try {
			/*
			//JAVA EE
			Date date = DatatypeConverter.parseDateTime(src).getTime();
		
			return date;
			*/
			
			//JODA time
			DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
	    	Date d = fmt.parseLocalDateTime(date).toDate();
			
	    	return d;
	    	
		} catch (Exception e) {
			
			// Sometimes the server replaces the char '+' (used for the time zone) with the char ' '
			date = date.replace(" ", "+");
			
			try {
				/*
				//JAVA EE
				Date date = DatatypeConverter.parseDateTime(src).getTime();
			
				return date;
				*/
				
				//JODA time
				DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
		    	Date d = fmt.parseLocalDateTime(date).toDate();
				
		    	return d;
		    	
			} catch (Exception ex) {
				log.error("Wrong format of date: " + date, ex);
				throw ex;
			}
						
		}
	}
	
	protected String getUrl4Pagination() {
		String readUrl = servletRequest.getRequestURI();
		readUrl = readUrl.substring(readUrl.indexOf("/", 1) + 1, readUrl.lastIndexOf("/") + 1);
		return readUrl;
	}
}
