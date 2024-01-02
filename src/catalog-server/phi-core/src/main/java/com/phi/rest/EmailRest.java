package com.phi.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jbpm.JbpmConfiguration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.repository.RepositoryManager;
import com.phi.json.HibernateModule;
import com.phi.rest.mail.Mail;

/**
 * Rest method for emails
 * 
 * json string parameter list
 * 
 * from:String
 * to:String
 * cc
 * ccN
 * subject:String
 * text:String
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("EmailRest")
@Path("/emails")
public class EmailRest {

	protected static final Logger log = Logger.getLogger(EmailRest.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendMail(String jSonEntity) {
		
		try {
			Mail entity = mapper.readValue(jSonEntity, Mail.class);
			
			RepositoryManager repoz = RepositoryManager.instance();

			entity.setSmtp(repoz.getSeamProperty("mailReport.smpt.host"));
			entity.setPort(repoz.getSeamProperty("mailReport.smpt.port"));
			entity.setUser(repoz.getSeamProperty("mailReport.username"));
			entity.setPasswd(repoz.getSeamProperty("mailReport.password"));
			
			if (entity.getSmtp() == null 	|| entity.getSmtp().isEmpty()
			 || entity.getPort() == null 	|| entity.getPort().isEmpty()
			 || entity.getUser() == null 	|| entity.getUser().isEmpty()
			 || entity.getPasswd() == null 	|| entity.getPasswd().isEmpty()) {
				//log.error("Seam properties doesen't contain email configuration, email send failed!");
				//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Seam properties doesen't contain email configuration, email send failed!").build();

			
				Map<String, String> defaultConfig = getJbpmMailConfig();
				
				if (entity.getSmtp() == null || entity.getSmtp().isEmpty()) {
					entity.setSmtp(defaultConfig.get("host"));
				}
				if (entity.getPort() == null || entity.getPort().isEmpty()) {
					entity.setPort(defaultConfig.get("port"));
				}
				if (entity.getUser() == null || entity.getUser().isEmpty()) {
					entity.setUser(defaultConfig.get("user"));
				}
				if (entity.getPasswd() == null || entity.getPasswd().isEmpty()) {
					entity.setPasswd(defaultConfig.get("password"));
				}
				if (entity.getFrom() == null || entity.getFrom().isEmpty()) {
					entity.setFrom(defaultConfig.get("from"));
				}
			
			}
			
			Properties props = new Properties();
		    props.put("mail.transport.protocol","smtp"); 
		    props.put("mail.smtp.host", entity.getSmtp());
		    props.put("mail.smtp.port", entity.getPort());
		    
		    if (entity.getUser() != null) { 
		    	props.put("mail.smtp.auth", "true");
		    } else {
		    	props.put("mail.smtp.auth", "false");
		    }


		    Session session = Session.getInstance(props);  
		    MimeMessage message = new MimeMessage(session);
		    
		    InternetAddress from = new InternetAddress(entity.getFrom());
	        message.setSubject(entity.getSubject());
	        message.setFrom(from);
	        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(entity.getTo()));
	        
	        if(entity.getCc() != null && entity.getCc().length()>0){
	        	for(String ccAddress:entity.getCc().split(" ")){
	        		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress));	
	        	}
	        }
	        
	        if(entity.getCcn() != null && entity.getCcn().length()>0){
	        	for(String ccnAddress:entity.getCcn().split(" ")){
	        		message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(ccnAddress));	
	        	}
	        }

	        // Create a multi-part to combine the parts
	        Multipart multipart = new MimeMultipart("alternative");
	
	        // Create your text message part
	        BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(entity.getText());
	
	        // Add the text part to the multipart
	        multipart.addBodyPart(messageBodyPart);
	
	        // Create the html part
	        messageBodyPart = new MimeBodyPart();
	        String htmlMessage = entity.getText();
	        messageBodyPart.setContent(htmlMessage, "text/html");

	        // Add html part to multi part
	        multipart.addBodyPart(messageBodyPart);
	
	        // Associate multi-part with message
	        message.setContent(multipart);
	
	        // Send message
	        Transport transport = session.getTransport("smtp");
	        if (entity.getUser() != null) {
	        	transport.connect(entity.getUser(),entity.getPasswd());
	        } else {
	        	transport.connect();
	        }
	        message.saveChanges();
	        transport.sendMessage(message, message.getAllRecipients());
		    transport.close();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error sending mail " + Mail.class.getSimpleName() , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error sending mail " + Mail.class.getSimpleName() + " by restrictions: ").build();
		}
		
		return Response.ok("esito").build(); //FIXME change to created
		
	}
	
	private Map<String, String> getJbpmMailConfig() {
		
		Map<String, String> emailConf = new HashMap<String, String>();
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.host")) {
			emailConf.put("host", JbpmConfiguration.Configs.getString("jbpm.mail.smtp.host"));
		}
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.port")) {
			emailConf.put("port", JbpmConfiguration.Configs.getString("jbpm.mail.smtp.port"));
		}
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.username")) {
			emailConf.put("username", JbpmConfiguration.Configs.getString("jbpm.mail.smtp.username"));
		}
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.password")) {
			emailConf.put("username", JbpmConfiguration.Configs.getString("jbpm.mail.smtp.password"));
		}
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.from.address")) {
			emailConf.put("from", JbpmConfiguration.Configs.getString("jbpm.mail.from.address"));
		}

		return emailConf;
	}

}