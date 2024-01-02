package com.phi;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailTest {


//	private static String host = "smtp.gmail.com";
//	private static String port = "587";
//	private static String username="francesco.bragagna@gmail.com" ;
//	private static String password="segreto";
	
	private static String host = "172.22.25.39";
	private static String port = "25";
	private static String username="mario.rossi" ;
	private static String password="segreto";
	
	private static String subject="zzz4";

	private static String auth = "true";
	
	public static void main(String[] args){
		
		
		
		Properties props = new Properties();
	    props.put("mail.smtp.starttls.enable", "true"); // added this line
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", port);
	    props.put("mail.smtp.auth", auth);
	
	
	
	    Session session = Session.getInstance(props,getAuthenticator());  
	    MimeMessage message = new MimeMessage(session);
	
	    System.out.println("Port: "+session.getProperty("mail.smtp.port"));
	
	    // Create the email addresses involved
	    try {
	        InternetAddress from = new InternetAddress("username");
	        message.setSubject(subject);
	        message.setFrom(from);
	        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("francesco.bragagna@tbsgroup.com"));
	
	        // Create a multi-part to combine the parts
	        Multipart multipart = new MimeMultipart("alternative");
	
	        // Create your text message part
	        BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText("some text to send");
	
	        // Add the text part to the multipart
	        multipart.addBodyPart(messageBodyPart);
	
	        // Create the html part
	        messageBodyPart = new MimeBodyPart();
	        String htmlMessage = "Our html text";
	        messageBodyPart.setContent(htmlMessage, "text/html");
	
	
	        // Add html part to multi part
	        multipart.addBodyPart(messageBodyPart);
	
	        // Associate multi-part with message
	        message.setContent(multipart);
	
	        // Send message
	        Transport transport = session.getTransport();
	        transport.connect();
	        System.out.println("Transport: "+transport.toString());
	        transport.sendMessage(message, message.getAllRecipients());
	
	
	    } catch (AddressException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (MessagingException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
	private static Authenticator getAuthenticator() {
		if (!username.isEmpty() && !password.isEmpty()) {
		  
		   Authenticator au = new javax.mail.Authenticator() {  
		        protected PasswordAuthentication getPasswordAuthentication() {  
		            return new PasswordAuthentication(username, password);  
		        }  
		    };
		    
		    return au;
		  
	  }
	  else {
		  return null;
	  }
	}
}