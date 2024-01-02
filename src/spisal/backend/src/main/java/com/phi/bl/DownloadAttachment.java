package com.phi.bl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;

@Name("downloadAttachment")
public class DownloadAttachment {

	protected static final Logger log = Logger.getLogger(DownloadAttachment.class);
	private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	@In(value="#{facesContext.externalContext}")
	private ExternalContext extCtx;

	@In(value="#{facesContext}")
	FacesContext facesContext;

	private CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

	@RequestParameter
	private String path;

	public String download() throws Exception {


		Path p = Paths.get(path);		

		String entityName = null;
		String entityId = null;
		String entityProp = null;

		if(!path.contains("//")){
			String[] pathParts = path.split("\\.");

			entityName = pathParts[0];
			entityId = pathParts[1];

			if(pathParts.length==3){
				entityProp = pathParts[2];
			}
		}


		long docId = -1L;

		try {
			if(entityId!=null && !entityId.isEmpty()){
				docId = Long.valueOf(entityId).longValue();
			}else{
				docId = Long.valueOf(path).longValue();
			}
		} catch (Exception e) {

		}


		HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();


		Method getter = null;

		Object entity = null;
		if(docId > 0L || docId == -1L){
			Class<?> clazz = null;
			try {
				clazz = Class.forName("com.phi.entities.baseEntity."+entityName);
			} catch (Exception e) {
				clazz = Class.forName("com.phi.entities.baseEntity."+entityName.replace("Last", ""));
			}

			if(docId == -1L){
				entity = Contexts.getConversationContext().get(entityName);
			}else{
				entity = ca.get(clazz, docId);
			}

			PropertyUtilsBean propBean = BeanUtilsBean.getInstance().getPropertyUtils();

			PropertyDescriptor pdContent = propBean.getPropertyDescriptor(entity, 
					(entityProp!=null && !entityProp.isEmpty() && !"posizionale".equals(entityProp)?entityProp:"content"));

			getter = pdContent.getReadMethod();

			PropertyDescriptor pdContentType = propBean.getPropertyDescriptor(entity, "contentType");
			if(pdContentType!=null){
				response.setContentType((String) pdContentType.getReadMethod().invoke(entity, null));
			}else{
				response.setContentType("application/pdf");
			}

			PropertyDescriptor pdFileName = propBean.getPropertyDescriptor(entity, "filename");
			if(pdFileName!=null){
				response.addHeader("Content-disposition", "attachment; filename=\"" + (String) pdFileName.getReadMethod().invoke(entity, null) +"\"");
			}else{
				response.addHeader("Content-disposition", "attachment; filename=\"" + "Cantiere" + docId +".pdf\"");
			}

		}else{
			response.setContentType(Files.probeContentType(p));
			response.addHeader("Content-disposition", "attachment; filename=\"" + p.getFileName().toString() +"\"");
		}

		try {

			ServletOutputStream os = response.getOutputStream();

			if(entity!=null && getter!=null){
				if("posizionale".equals(entityProp)){
					String decodedData = decodeUTF8((byte[])getter.invoke(entity, null));
					String txt = decodedData.replace(";", "");

					os.write(encodeUTF8(txt));
				}else{
					os.write((byte[])getter.invoke(entity, null));
				}
			}else{
				os.write(Files.readAllBytes(p));
			}

			os.flush();
			os.close();
			facesContext.responseComplete();
		} catch(Exception e) {
			log.error("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}

	private String decodeUTF8(byte[] bytes) {
		return new String(bytes, UTF8_CHARSET);
	}

	private byte[] encodeUTF8(String string) {
		return string.getBytes(UTF8_CHARSET);
	}

}