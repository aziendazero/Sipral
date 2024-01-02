package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tipo_specialista database table.
 * 
 */
@Entity
@Table(name="tipo_specialista")
@NamedQuery(name="TipoSpecialista.findAll", query="SELECT t FROM TipoSpecialista t")
public class TipoSpecialista implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String campo1;

	public TipoSpecialista() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(length=255)
	public String getCampo1() {
		return this.campo1;
	}

	public void setCampo1(String campo1) {
		this.campo1 = campo1;
	}

}