package com.phi.rest;

import java.beans.PropertyDescriptor;
import java.nio.charset.Charset;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;

import com.phi.entities.baseEntity.Fattura;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("FatturaRest")
@Path("/fatturas")
public class FatturaRest extends BaseRest<Fattura> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8660053946202297629L;
	private final Charset UTF8_CHARSET = Charset.forName("UTF-8");


	/**
	 * Download file from entity.field
	 * 
	 * Overload Metodo di BaseRest per poter eliminare il carattere di separazione ";" 
	 * 
	 * @param id internalId of entity
	 * @param field name of the field inside entity
	 * @return content of field
	 */
	@GET
	@Path("/{id}/download/{field}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response get(@PathParam("id") long id, @PathParam("field") String field) {

		ResponseBuilder responseBuilder = null;

		try {

			Object data = null;

			entity = ca.get(entityClass, id);
			
			if (entity != null) {
				Boolean csv = (field!=null && "csv".equals(field))?true:false;
				
				PropertyUtilsBean propBean = BeanUtilsBean.getInstance().getPropertyUtils();
				PropertyDescriptor pd = propBean.getPropertyDescriptor(entity, "content");
				
				Method getter = pd.getReadMethod();
				data = getter.invoke(entity);
				
				if (getter.getReturnType() == byte[].class) {
					if (!csv){
						String decodedData = decodeUTF8((byte[])data);
						String txt = decodedData.replace(";", "");
						
						responseBuilder = Response.ok(encodeUTF8(txt));
					} else 
						responseBuilder = Response.ok(data);
						
				}
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error downloading file " + e.getMessage(), e);
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error downloading file " + e.getMessage());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}
		return responseBuilder.build();
	}
	
	String decodeUTF8(byte[] bytes) {
	    return new String(bytes, UTF8_CHARSET);
	}

	byte[] encodeUTF8(String string) {
	    return string.getBytes(UTF8_CHARSET);
	}
		
}