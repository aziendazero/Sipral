package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tipologia_esame_biologico database table.
 * 
 */
@Entity
@Table(name="tipologia_esame_biologico")
@NamedQuery(name="TipologiaEsameBiologico.findAll", query="SELECT t FROM TipologiaEsameBiologico t")
public class TipologiaEsameBiologico implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String campo1;

	public TipologiaEsameBiologico() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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