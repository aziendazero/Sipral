package com.phi.ps;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.mail.AddressResolver;
import org.jbpm.util.ClassLoaderUtil;
import org.jbpm.util.XmlUtil;




// CLONE of jbpm Mail.class of package org.jbpm.mail. 
// used to override methods like getFromAddress and to change jbpm standard mail management behaviour.
// changes compared to original file regards sender address and possibility to use authentication during smtp session establishment
// to be used need to be inserted into jbpm.cfg.xml the following line:
//  <string name="jbpm.mail.class.name" value="com.phi.ps.Mail" />
//
// added Attachment management.
// to be used must be inserted in conversation: 
//     List<Object> with conversationName: "mail.attachments"
//     List<String> with conversationName: "mail.attachments.titles"
// 
// both lists can be managed by MailHelper.  List<Object> can be a list of File, or a list of byte[] 



public class Mail  extends org.jbpm.mail.Mail implements ActionHandler
{
  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(Mail.class);

  String template = null;
  String actors = null;
  String to = null;
  String bcc = null;
  String bccActors = null;
  String subject = null;
  String text = null;

  ExecutionContext executionContext = null;

  public Mail()
  {
  }

  public Mail(String template, String actors, String to, String subject, String text)
  {
    this.template = template;
    this.actors = actors;
    this.to = to;
    this.subject = subject;
    this.text = text;
  }

  public Mail(String template, String actors, String to, String bccActors, String bcc, String subject, String text)
  {
    this.template = template;
    this.actors = actors;
    this.to = to;
    this.bccActors = bccActors;
    this.bcc = bcc;
    this.subject = subject;
    this.text = text;
  }

  public void execute(ExecutionContext executionContext)
  {
    this.executionContext = executionContext;
    send();
  }

  public List getRecipients()
  {
    List recipients = new ArrayList();
    if (actors != null)
    {
      String evaluatedActors = evaluate(actors);
      List tokenizedActors = tokenize(evaluatedActors);
      if (tokenizedActors != null)
      {
        recipients.addAll(resolveAddresses(tokenizedActors));
      }
    }
    if (to != null)
    {
      String resolvedTo = evaluate(to);
      if (resolvedTo!=null)
    	  recipients.addAll(tokenize(resolvedTo));
    }
    return recipients;
  }

  public List getBccRecipients()
  {
    List recipients = new ArrayList();
    if (bccActors != null)
    {
      String evaluatedActors = evaluate(bccActors);
      List tokenizedActors = tokenize(evaluatedActors);
      if (tokenizedActors != null)
      {
        recipients.addAll(resolveAddresses(tokenizedActors));
      }
    }
    if (bcc != null)
    {
      String resolvedTo = evaluate(bcc);
      recipients.addAll(tokenize(resolvedTo));
    }
    if (JbpmConfiguration.Configs.hasObject("jbpm.mail.bcc.address"))
    {
      recipients.addAll(tokenize(JbpmConfiguration.Configs.getString("jbpm.mail.bcc.address")));
    }
    return recipients;
  }

  public String getSubject()
  {
    if (subject == null)
      return null;
    return evaluate(subject);
  }

  public String getText()
  {
    if (text == null)
      return null;
    return evaluate(text);
  }
  
  public static String getJbpmConfFromAddress() {
	  String ret ="";
	  if (JbpmConfiguration.Configs.hasObject("jbpm.mail.from.address")) {
	    	ret= JbpmConfiguration.Configs.getString("jbpm.mail.from.address");
	    }
	  return ret;
  }

  public String getFromAddress()
  {
	String mailAddress="";
    if (JbpmConfiguration.Configs.hasObject("jbpm.mail.from.address")) {
    	mailAddress= JbpmConfiguration.Configs.getString("jbpm.mail.from.address");
    }
    String conversationFromAddress = (String)Contexts.getConversationContext().get("mail.from.address");
    if (conversationFromAddress != null && !conversationFromAddress.isEmpty())
    	mailAddress=conversationFromAddress;
    
    if (mailAddress.isEmpty())
    	mailAddress ="jbpm@noreply";
    
    return mailAddress;
  }

  public void send()
  {
    if (template != null)
    {
      Properties properties = getMailTemplateProperties(template);
      if (actors == null)
      {
        actors = properties.getProperty("actors");
      }
      if (to == null)
      {
        to = properties.getProperty("to");
      }
      if (subject == null)
      {
        subject = properties.getProperty("subject");
      }
      if (text == null)
      {
        text = properties.getProperty("text");
      }
      if (bcc == null)
      {
        bcc = properties.getProperty("bcc");
      }
      if (bccActors == null)
      {
        bccActors = properties.getProperty("bccActors");
      }
    }

    send(getMailServerProperties(), getFromAddress(), getRecipients(), getBccRecipients(), getSubject(), getText());
  }

  public static void send(Properties mailServerProperties, String fromAddress, List recipients, String subject, String text)
  {
    send(mailServerProperties, fromAddress, recipients, null, subject, text);
  }

  public static void send(Properties mailServerProperties, String fromAddress, List recipients, List bccRecipients, String subject, String text)
  {
    if (((recipients == null) || (recipients.isEmpty())) && ((bccRecipients == null) || (bccRecipients.isEmpty())))
    {
    	String errorMessage = "Cannot send email because there are not recipients"; 
    	log.error(errorMessage);
    	JbpmException e = new JbpmException();
    	e.initCause(new MessagingException(errorMessage));
    	throw e;
    }

    try
    {
      int retries = 5;
      while (0 < retries)
      {
        retries--;
        try
        {
          sendMailInternal(mailServerProperties, fromAddress, recipients, bccRecipients, subject, text);
          break;
        }
        catch (MessagingException msgex)
        {
          if (retries == 0)
            throw msgex;

          System.out.println("Cannot send mail, now retrying: " + msgex);
          log.error("Cannot send mail, now retrying: " + msgex);
          Thread.sleep(1000);
        }
      }
    }
    catch (Exception e)
    {
      log.error("["+mailServerProperties.getProperty("mail.smtp.host")+":"+mailServerProperties.getProperty("mail.smtp.port")+"]: Unable to send mail from "+fromAddress+" to '" + recipients + "' " + (bccRecipients != null && 	!bccRecipients.isEmpty() ? "and in bcc to '" + bccRecipients + "' " : "") + "about '" + subject.replace("\n", "").replace("\t", "") + "'");
      Contexts.getConversationContext().set("MessagingException",e);
      throw new JbpmException("Cannot send email", e);
    }
  }

  private static void sendMailInternal(Properties mailServerProperties, String fromAddress, List recipients, List bccRecipients, String subject, String text)
      throws Exception
  {
	log.debug("sending email to '" + recipients + "' " + (bccRecipients != null ? "and in bcc to '" + bccRecipients + "' " : "") + "about '" + subject + "'");
    
    
    Session session = Session.getDefaultInstance(mailServerProperties, getAuthenticator()); 
    
    
    
    MimeMessage message = new MimeMessage(session);
    if (fromAddress != null)
    {
      message.setFrom(new InternetAddress(fromAddress));
    }
    Iterator iter = recipients.iterator();
    while (iter.hasNext())
    {
      InternetAddress recipient = new InternetAddress((String)iter.next());
      message.addRecipient(Message.RecipientType.TO, recipient);
    }
    if (bccRecipients != null)
    {
      iter = bccRecipients.iterator();
      while (iter.hasNext())
      {
        InternetAddress recipient = new InternetAddress((String)iter.next());
        message.addRecipient(Message.RecipientType.BCC, recipient);
      }
    }
    if (subject != null)
    {
      message.setSubject(subject);
    }
    if (text == null)
    {
    	log.warn ("empty body of mail text message!");
    	text = "";
    }
    
	List<Object> attachments = (List<Object>)Contexts.getConversationContext().get(MailHelper.ATTACHMENTS_CONVNAME);
	if (attachments == null || attachments.isEmpty()) {
		message.setText(text);
	}
	else {
		//if present, use this conversation list to get file names. If not passed, filenames will be miss extensions!
		List<String> attachmentsTitle = (List<String>)Contexts.getConversationContext().get(MailHelper.ATTACHMENTS_TITLE_CONVNAME);
		if (attachmentsTitle == null || attachments.size() != attachmentsTitle.size()) {
			log.error("attachments ("+attachments.size()+") and their file names ("+attachmentsTitle.size()+") are not the same number");
			//do not use multipart.
			message.setText(text);
		}
		else {
			//create mail message as multipart message
			Multipart multipart = new MimeMultipart();
			
			BodyPart messageTextPart = new MimeBodyPart();  
			messageTextPart.setText(text);  
			multipart.addBodyPart(messageTextPart); 
			
			for (int i=0; i<attachments.size(); i++) {
				
				Object attach = attachments.get(i);
				DataSource ds = null;
				if (attach instanceof File) {
					File attachment = (File)attach;
					ds = new FileDataSource(attachment);
				}
				else if (attach instanceof byte[]) {
					byte[] attachment = (byte[]) attach;
					ds = new ByteArrayDataSource(attachment, "application/octet-stream");
				}
				
				String title = attachmentsTitle.get(i);
				
				//for each attach, add them to multipart Message 
				MimeBodyPart attachmentPart = new MimeBodyPart();
				
				attachmentPart.setDataHandler(new DataHandler(ds));  
				attachmentPart.setFileName(title);  
				multipart.addBodyPart(attachmentPart);
			}
			
			//Finally set the composed multipart body as message content.
			message.setContent(multipart);
		}
	}
    
    message.setSentDate(new Date());

    //Transport transport = session.getTransport("smtp");
    //transport.connect();
    Transport.send(message);
    log.info("["+mailServerProperties.getProperty("mail.smtp.host")+"]: Sent email from "+fromAddress+" to '" + recipients + "' " + (bccRecipients != null && 	!bccRecipients.isEmpty() ? "and in bcc to '" + bccRecipients + "' " : "") + "about '" + subject.replace("\n", "").replace("\t", "") + "'");
  }

  protected List tokenize(String text)
  {
    if (text == null)
    {
      return null;
    }
    List list = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(text, ";:");
    while (tokenizer.hasMoreTokens())
    {
      list.add(tokenizer.nextToken());
    }
    return list;
  }

  protected Collection resolveAddresses(List actorIds)
  {
    List emailAddresses = new ArrayList();
    Iterator iter = actorIds.iterator();
    while (iter.hasNext())
    {
      String actorId = (String)iter.next();
      AddressResolver addressResolver = (AddressResolver)JbpmConfiguration.Configs.getObject("jbpm.mail.address.resolver");
      Object resolvedAddresses = addressResolver.resolveAddress(actorId);
      if (resolvedAddresses != null)
      {
        if (resolvedAddresses instanceof String)
        {
          emailAddresses.add((String)resolvedAddresses);
        }
        else if (resolvedAddresses instanceof Collection)
        {
          emailAddresses.addAll((Collection)resolvedAddresses);
        }
        else if (resolvedAddresses instanceof String[])
        {
          emailAddresses.addAll(Arrays.asList((String[])resolvedAddresses));
        }
        else
        {
          throw new JbpmException("Address resolver '" + addressResolver + "' returned '" + resolvedAddresses.getClass().getName()
              + "' instead of a String, Collection or String-array: " + resolvedAddresses);
        }
      }
    }
    return emailAddresses;
  }

  Properties getMailServerProperties()
  {
	//look for more configuration at https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
    Properties mailServerProperties = new Properties();

    if (JbpmConfiguration.Configs.hasObject("resource.mail.properties"))
    {
      String mailServerPropertiesResource = JbpmConfiguration.Configs.getString("resource.mail.properties");
      try
      {
        InputStream mailServerStream = ClassLoaderUtil.getStream(mailServerPropertiesResource);
        mailServerProperties.load(mailServerStream);
      }
      catch (Exception e)
      {
        throw new JbpmException("couldn't get configuration properties for jbpm mail server from resource '" + mailServerPropertiesResource + "'", e);
      }

    }
    else if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.host"))
    {
      String smtpServer = JbpmConfiguration.Configs.getString("jbpm.mail.smtp.host");
      mailServerProperties.put("mail.smtp.host", smtpServer);
      if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.port")) {
    	  String smtpServerPort = JbpmConfiguration.Configs.getString("jbpm.mail.smtp.port");
    	  mailServerProperties.put("mail.smpt.port", smtpServerPort);
      }
      if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.username") || Contexts.getConversationContext().get("mail.smtp.username") != null) {
    	  mailServerProperties.put("mail.smtp.auth", "true"); 
      }
      if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.useSSL") ) {
    	  mailServerProperties.put("mail.smtp.starttls.enable", "true"); 
      }
       
    }
    else
    {

      log.error("couldn't get mail properties");
    }

    return mailServerProperties;
  }

  static Map templates = null;
  static Map templateVariables = null;

  synchronized Properties getMailTemplateProperties(String templateName)
  {
    if (templates == null)
    {
      templates = new HashMap();
      String mailTemplatesResource = JbpmConfiguration.Configs.getString("resource.mail.templates");
      org.w3c.dom.Element mailTemplatesElement = XmlUtil.parseXmlResource(mailTemplatesResource, false).getDocumentElement();
      List mailTemplateElements = XmlUtil.elements(mailTemplatesElement, "mail-template");
      Iterator iter = mailTemplateElements.iterator();
      while (iter.hasNext())
      {
        org.w3c.dom.Element mailTemplateElement = (org.w3c.dom.Element)iter.next();

        Properties templateProperties = new Properties();
        addTemplateProperty(mailTemplateElement, "actors", templateProperties);
        addTemplateProperty(mailTemplateElement, "to", templateProperties);
        addTemplateProperty(mailTemplateElement, "subject", templateProperties);
        addTemplateProperty(mailTemplateElement, "text", templateProperties);
        addTemplateProperty(mailTemplateElement, "bcc", templateProperties);
        addTemplateProperty(mailTemplateElement, "bccActors", templateProperties);

        templates.put(mailTemplateElement.getAttribute("name"), templateProperties);
      }

      templateVariables = new HashMap();
      List variableElements = XmlUtil.elements(mailTemplatesElement, "variable");
      iter = variableElements.iterator();
      while (iter.hasNext())
      {
        org.w3c.dom.Element variableElement = (org.w3c.dom.Element)iter.next();
        templateVariables.put(variableElement.getAttribute("name"), variableElement.getAttribute("value"));
      }
    }
    return (Properties)templates.get(templateName);
  }

  void addTemplateProperty(org.w3c.dom.Element mailTemplateElement, String property, Properties templateProperties)
  {
    org.w3c.dom.Element element = XmlUtil.element(mailTemplateElement, property);
    if (element != null)
    {
      templateProperties.put(property, XmlUtil.getContentText(element));
    }
  }

  String evaluate(String expression)
  {
    if (expression == null)
    {
      return null;
    }
    VariableResolver variableResolver = JbpmExpressionEvaluator.getUsedVariableResolver();
    if (variableResolver != null)
    {
      variableResolver = new MailVariableResolver(templateVariables, variableResolver);
    }
    return (String)JbpmExpressionEvaluator.evaluate(expression, executionContext, variableResolver, JbpmExpressionEvaluator.getUsedFunctionMapper());
  }

  class MailVariableResolver implements VariableResolver, Serializable
  {
    private static final long serialVersionUID = 1L;
    Map templateVariables = null;
    VariableResolver variableResolver = null;

    public MailVariableResolver(Map templateVariables, VariableResolver variableResolver)
    {
      this.templateVariables = templateVariables;
      this.variableResolver = variableResolver;
    }

    public Object resolveVariable(String pName) throws ELException
    {
      if ((templateVariables != null) && (templateVariables.containsKey(pName)))
      {
        return templateVariables.get(pName);
      }
      return variableResolver.resolveVariable(pName);
    }
  }

  private static Authenticator getAuthenticator() {
	  String xmlUsername = "";
	  String xmlPassword = "";
	  if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.username")) 
		  xmlUsername = JbpmConfiguration.Configs.getString("jbpm.mail.smtp.username");
	  if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.username")) 
		  xmlPassword = JbpmConfiguration.Configs.getString("jbpm.mail.smtp.password");
	  
	  Context conv = Contexts.getConversationContext();
	  String convUsername = (String)conv.get("mail.from.address.username");
	  String convPassword = (String)conv.get("mail.from.address.password");
	  
	  String username="";
	  String password="";
	  
	  if (xmlUsername != null && xmlPassword != null && !xmlUsername.isEmpty() && !xmlPassword.isEmpty()) {
		  //if any xml username/password found on xml, use them.
		  username=xmlUsername;
		  password=xmlPassword;
	  }
	  if (convUsername != null && convPassword != null && !convUsername.isEmpty() && !convPassword.isEmpty()) {
		  //independently from xmlUser/Pass, if user/pass are found in conversation use them.
		  username=convUsername;
		  password=convPassword;
	  }
	  
	  log.info("xmlUsername:"+xmlUsername+" convUsername"+convUsername +" username used:"+username);
	  //finally if user/pass are not empty, generate an authenticator, and return it, otherwise return null (NO AUTHENTICATION!).
	  if (!username.isEmpty() && !password.isEmpty()) {
		  Authenticator auth = new Authenticator(username, password);
//		  Authenticator auth = new javax.mail.Authenticator() {  
//				protected PasswordAuthentication getPasswordAuthentication() {  
//					return new PasswordAuthentication(username, password);  
//				}  
//			};
		  return auth;
	  }
	  else {
		  return null;
	  }
  }
  
//  private static Log log = LogFactory.getLog(Mail.class);
  
  private static class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;

		public Authenticator(String user, String pass) {
			authentication = new PasswordAuthentication(user, pass);
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
  
}
