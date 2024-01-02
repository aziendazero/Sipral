package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the conclusioni database table.
 * 
 */
@Entity
@Table(name="conclusioni")
@NamedQuery(name="Conclusioni.findAll", query="SELECT c FROM Conclusioni c")
public class Conclusioni implements Serializable {
	private static final long serialVersionUID = 1L;
	private String campo1;

	public Conclusioni() {
	}

	@Id
	@Column(length=250)
	public String getCampo1() {
		return this.campo1;
	}

	public void setCampo1(String campo1) {
		this.campo1 = campo1;
	}

}