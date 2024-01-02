package com.phi.odt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import net.sf.jooreports.templates.image.ImageSource;
import net.sf.jooreports.templates.image.RenderedImageSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parameters.ParameterManager;

@BypassInterceptors
@Name("OdtEngine")
@Scope(ScopeType.CONVERSATION)
@Install(precedence=Install.DEPLOYMENT)
public class SpisalOdtEngine extends OdtEngine implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SpisalOdtEngine.class);
	private int maxListSize = 500;

	public static SpisalOdtEngine instance() {
		return (SpisalOdtEngine) Component.getInstance(SpisalOdtEngine.class, ScopeType.CONVERSATION);
	}

	/**
	 * Generate freemarker model from conversation, see: http://freemarker.org/
	 * @param sdlCode code of enabled sdl, added to model as Sdl.
	 * @return Map of objects in conversation
	 * @throws PhiException 
	 */
	protected Map<String, Object> getModel(String sdlCode)  {
		Map<String, Object> pdmBackup = new HashMap<String, Object>();
		//Conversation
		Context currentConv = Contexts.getConversationContext();
		String[] names = currentConv.getNames();
		Object object = null;
		PagedDataModel pdm = null;
		ParameterManager pm = ParameterManager.instance();
		Object val = null;
		try {
			val = pm.getParameter("p.general.maxlistsize", "value");
			if (val != null) {
				maxListSize = Integer.parseInt(val.toString());
			} else {
				log.info("getModel: No maxListSize parameter found, using default value: "+maxListSize);
			}
		} catch (PhiException e1) {
			log.error("getModel: Error retrieving maxListSize from parameter, using default value: "+maxListSize);
		}

		//rimuovo dalla conversation le pageddatamodel il cui fullList pianterebbe il sistema
		for(String name:names) {
			object = currentConv.get(name);
			if (object instanceof BaseEntity || object instanceof IdataModel || object instanceof BaseAction){
				if (object instanceof PagedDataModel) {
					pdm = (PagedDataModel)object;
					if (pdm.getSize()>maxListSize) {
						pdmBackup.put(name, pdm);
						currentConv.remove(name);
						log.info("getModel: skipping list: "+name);
					}
				}
			}
		}

		Map<String, Object> model = super.getModel(sdlCode);

		ServiceDeliveryLocation ulss = null;
		ServiceDeliveryLocation uoc = null;

		Procpratiche proc = (Procpratiche)currentConv.get("Procpratiche");

		if (proc!=null)
			uoc = proc.getUoc();

		else {
			Protocollo prot = (Protocollo)currentConv.get("Protocollo");
			if (prot!=null)
				uoc = prot.getServiceDeliveryLocation();	
		}

		if (uoc!=null){
			model.put("Uoc", uoc);

			ulss = uoc.getParent();

			if (ulss!=null) {
				model.put("Ulss", ulss);
				//Logo
				if (ulss.getLogo() != null) {
					try {
						ImageSource headeriImg = new RenderedImageSource(ImageIO.read(new ByteArrayInputStream(ulss.getLogo())));
						model.put("currentHeader", headeriImg);
					} catch (IOException e) {
						log.error("Error loading header image " + e.getMessage(), e);
					}
				} else {
					log.warn("ServiceDeliveryLocation " + ulss.getName() + " has no logo!");
				}
			}else { 
				log.info("No ULSS selected from conversation objects (Procpratiche or Protocollo)");
			}
		} else {
			log.info("No ULSS/UOC selected from conversation objects (Procpratiche or Protocollo)");
		}

		//ripristino le pageddatamodel backuppate
		for(String name:pdmBackup.keySet()) {
			currentConv.set(name, pdmBackup.get(name));
		}

		pdmBackup.clear();

		return model;
	}

	private final Pattern freeMarkerVariablePattern = Pattern.compile("(\\$\\{[^\\}]*\\})");
	private final Pattern freeMarkerCodePattern = Pattern.compile("(\\[#if[^<>]*/#if\\])|(\\[#list [^<>]*/#list\\])");
	private final Pattern freeMarkerPunctuationPattern = Pattern.compile("(text:p text:style-name[^/>]*>)([^<>\\w]*[,;-][^<>\\w]*)<");


	public byte[] removeBindingsFromBody(byte[] modelBarr) throws Exception {
		ZipInputStream modelZis = null;
		ZipOutputStream newModelZos = null;
		ByteArrayOutputStream newModelBaos = null;

		try {

			modelZis = new ZipInputStream(new ByteArrayInputStream(modelBarr));
			ZipEntry modelZe = modelZis.getNextEntry();

			StringBuffer newStyles = new StringBuffer();
			StringBuffer newContent = new StringBuffer();

			while(modelZe != null) {
				if ("styles.xml".equals(modelZe.getName())) {
					String styles = IOUtils.toString(modelZis, "UTF-8"); 

					Matcher matcherv = freeMarkerVariablePattern.matcher(styles);
					while (matcherv.find()) {
						String variable = matcherv.group(1);
						String space = String.valueOf('\u0020');
						if(variable.contains("sigla")){
							matcherv.appendReplacement(newStyles, "&#32;&#32;");
						}else if(variable.contains("subType")){
							matcherv.appendReplacement(newStyles, "&#32;");
						}else if(variable.contains("matricola")){
							matcherv.appendReplacement(newStyles, "&#32;&#32;&#32;&#32;&#32;&#32;");
						}else if(variable.contains("anno")){
							matcherv.appendReplacement(newStyles, "&#32;&#32;&#32;&#32;");
						}else{
							matcherv.appendReplacement(newStyles, "");
						}

					}
					matcherv.appendTail(newStyles);

					Matcher matcherc = freeMarkerCodePattern.matcher(newStyles.toString());
					newStyles = new StringBuffer();
					while (matcherc.find()) {
						String code1 = matcherc.group(1);
						String code2 = matcherc.group(2);
						String checkedBox = String.valueOf('\u2611');
						String uncheckedBox = String.valueOf('\u2610');
						if(code1!=null){
							if(code1.contains(checkedBox) || code1.contains(uncheckedBox)){
								matcherc.appendReplacement(newStyles, "&#9744;");
							}else{
								matcherc.appendReplacement(newStyles, "");
							}
						}
						if(code2!=null){
							if(code2.contains(checkedBox) || code2.contains(uncheckedBox)){
								matcherc.appendReplacement(newStyles, "&#9744;");
							}else{
								matcherc.appendReplacement(newStyles, "");
							}
						}
					}
					matcherc.appendTail(newStyles);
				}

				if ("content.xml".equals(modelZe.getName())) {
					String content = IOUtils.toString(modelZis, "UTF-8"); 

					Matcher matcherv = freeMarkerVariablePattern.matcher(content);
					newContent = new StringBuffer();
					while (matcherv.find()) {
						String variable = matcherv.group(1);
						matcherv.appendReplacement(newContent, "");
					}
					matcherv.appendTail(newContent);

					Matcher matcherc = freeMarkerCodePattern.matcher(newContent.toString());
					newContent = new StringBuffer();
					while (matcherc.find()) {
						String code1 = matcherc.group(1);
						String code2 = matcherc.group(2);
						String checkedBox = String.valueOf('\u2611');
						String uncheckedBox = String.valueOf('\u2610');
						if(code1!=null){
							if(code1.contains(checkedBox) || code1.contains(uncheckedBox)){
								matcherc.appendReplacement(newContent, "&#9744;");
							}else{
								matcherc.appendReplacement(newContent, "");
							}
						}
						if(code2!=null){
							if(code2.contains(checkedBox) || code2.contains(uncheckedBox)){
								matcherc.appendReplacement(newContent, "&#9744;");
							}else{
								matcherc.appendReplacement(newContent, "");
							}
						}
					}
					matcherc.appendTail(newContent);

					Matcher matcherp = freeMarkerPunctuationPattern.matcher(newContent.toString());
					newContent = new StringBuffer();
					while (matcherp.find()) {
						String punctuation = matcherp.group(2);
						matcherp.appendReplacement(newContent, matcherp.group(1)+"<");
					}
					matcherp.appendTail(newContent);

					break;
				}
				modelZis.closeEntry();
				modelZe = modelZis.getNextEntry();
			}

			//model
			modelZis.close();
			modelZis = new ZipInputStream(new ByteArrayInputStream(modelBarr));
			ZipEntry documentZe = modelZis.getNextEntry();

			//new model
			newModelBaos = new ByteArrayOutputStream();
			newModelZos = new ZipOutputStream(newModelBaos);

			byte[] buf = new byte[4096];

			while(documentZe!=null){
				newModelZos.putNextEntry(new ZipEntry(documentZe.getName()));
				if("styles.xml".equals(documentZe.getName())){
					newModelZos.write(newStyles.toString().getBytes("UTF-8"));
				}else if ("content.xml".equals(documentZe.getName())) {
					newModelZos.write(newContent.toString().getBytes("UTF-8"));
				} else {
					//IOUtils.copy(documentZis, newDocumentZos); 
					int len;
					while ((len = modelZis.read(buf)) > 0) {
						newModelZos.write(buf, 0, len);
					}
				}
				newModelZos.closeEntry();

				modelZis.closeEntry();
				documentZe = modelZis.getNextEntry();
			}
			newModelZos.close();
			newModelBaos.close();
			byte[] newModelBarr = newModelBaos.toByteArray();

			return newModelBarr;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (modelZis != null) {
					modelZis.close();
				}				
				if (newModelZos != null) {
					newModelZos.close();
				}
				if (newModelBaos != null) {
					newModelBaos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public byte[] generateUnfilledBody(byte[] templateBytes, String sdlCode) throws PhiException {

		byte[] refurbishedBody = null;
		try {
			refurbishedBody = removeBindingsFromBody(templateBytes);
			templateBytes = refurbishedBody;
		} catch (Exception e) {

		}

		return super.generateFromTemplate(templateBytes, sdlCode);
	}
}