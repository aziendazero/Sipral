package com.phi.ps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;



@Name("MailHelper")
public class MailHelper {
	
	public static final String ATTACHMENTS_CONVNAME = "mail.attachments";
	public static final String ATTACHMENTS_TITLE_CONVNAME = "mail.attachments.titles";

	private static final Logger log = Logger.getLogger(MailHelper.class);
	
	
	//conversation list of attachment, with name ATTACHMENTS_CONVNAME, is a list of object (see Mail.java)
	//object allowed are byte array or File.
	
	public void addAttachment (byte[] arr, String name) {
		addObjAttachment (arr, name);
	}
	
	public void addAttachment (File f, String name) {
		addObjAttachment (f, name);
	}
	
	private void addObjAttachment (Object attach, String name) {
		if(attach == null || name == null || name.isEmpty()) {
			log.error("Unable to add null attachment, or missing title, as mail attachment.");
			return;
		}
		
		Context conv = Contexts.getConversationContext();
		List<Object> attachments;
		List<String> titles;
		if (conv.get(ATTACHMENTS_CONVNAME) == null ) {
			 attachments = new ArrayList<Object>();
			 titles = new ArrayList<String>();
		}
		else {
			attachments =  (List<Object>) conv.get(ATTACHMENTS_CONVNAME);
			titles = (List<String>) conv.get(ATTACHMENTS_TITLE_CONVNAME);
		}
		
		attachments.add(attach);
		titles.add(name);
		
		conv.set(ATTACHMENTS_CONVNAME, attachments);
		conv.set(ATTACHMENTS_TITLE_CONVNAME, titles);
	}
	
	
	
	
	public void removeAttachment (byte[] arr) {
		removeObjAttachment (arr);
	}
	
	public void removeAttachment (File f) {
		removeObjAttachment (f);
	}
	
	private void removeObjAttachment (Object attach) {
		if (attach == null ) {
			log.error("nothing to remove");
			return;
		}
		
		Context conv = Contexts.getConversationContext();
		List<File> attachments = (List<File>)conv.get(ATTACHMENTS_CONVNAME);
		List<String> titles;
		if (attachments == null || attachments.isEmpty() ) {
			log.error("Attachments list is empty");
			return; 
		}
		if (!attachments.contains(attach)) {
			log.error("Attachment not present in the list");
			return;
		}
		
		titles = (List<String>) conv.get(ATTACHMENTS_TITLE_CONVNAME);
			
		int i;
		for (i=0; i<attachments.size(); i++) {
			if (attach.equals(attachments.get(i))){
				break;
			}
		}
		
		attachments.remove(i);
		titles.remove(i);
		
		conv.set(ATTACHMENTS_CONVNAME, attachments);
		conv.set(ATTACHMENTS_TITLE_CONVNAME, titles);
	}
	
	public void clearMessagingException() {
		Contexts.getConversationContext().remove("MessagingException");
	}
	
	/**
	 * clear attachments in conversation
	 */
	public void emptyAttachments(){
		Context conv = Contexts.getConversationContext();
		conv.remove(ATTACHMENTS_CONVNAME);
		conv.remove(ATTACHMENTS_TITLE_CONVNAME);
	}
}
