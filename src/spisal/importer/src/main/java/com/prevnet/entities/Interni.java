package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


/**
 * The persistent class for the INTERNI database table.
 * 
 */
@Entity
public class Interni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idanagrafica;
	private String email;
	private String fax;
	private String telefono;
	private Anagrafiche anagrafiche;

	public Interni() {
	}


	@Id
	public long getIdanagrafica() {
		return this.idanagrafica;
	}

	public void setIdanagrafica(long idanagrafica) {
		this.idanagrafica = idanagrafica;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	//bi-directional one-to-one association to Anagrafiche
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}

}