package com.phi.notifications;

import com.phi.entities.baseEntity.Infortuni;

public class InfMortaliMessage {
	
	private String oggetto;
	
	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	private String destinatari;

	public String getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}
	
	private String CC;

	public String getCC() {
		return CC;
	}

	public void setCC(String cC) {
		CC = cC;
	}

	
	private String bodyMessage;
	
	public String getBodyMessage() {
		return bodyMessage;
	}

	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}

	private Infortuni infortuni;

	public Infortuni getInfortuni() {
		return infortuni;
	}

	public void setInfortuni(Infortuni infortuni) {
		this.infortuni = infortuni;
	}
}
