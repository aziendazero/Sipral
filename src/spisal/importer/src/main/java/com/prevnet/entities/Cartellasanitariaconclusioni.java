package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the CARTELLASANITARIACONCLUSIONI database table.
 * 
 */
@Entity
@NamedQuery(name="Cartellasanitariaconclusioni.findAll", query="SELECT c FROM Cartellasanitariaconclusioni c")
public class Cartellasanitariaconclusioni implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idcartellasanitariaconclusioni;

	private String chkidoneo;

	@Temporal(TemporalType.DATE)
	private Date datacompilazione;

	private String diagnosi;

	private String giudizio;

	private String provvedimenti;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	public Cartellasanitariaconclusioni() {
	}

	public long getIdcartellasanitariaconclusioni() {
		return this.idcartellasanitariaconclusioni;
	}

	public void setIdcartellasanitariaconclusioni(long idcartellasanitariaconclusioni) {
		this.idcartellasanitariaconclusioni = idcartellasanitariaconclusioni;
	}

	public String getChkidoneo() {
		return this.chkidoneo;
	}

	public void setChkidoneo(String chkidoneo) {
		this.chkidoneo = chkidoneo;
	}

	public Date getDatacompilazione() {
		return this.datacompilazione;
	}

	public void setDatacompilazione(Date datacompilazione) {
		this.datacompilazione = datacompilazione;
	}

	public String getDiagnosi() {
		return this.diagnosi;
	}

	public void setDiagnosi(String diagnosi) {
		this.diagnosi = diagnosi;
	}

	public String getGiudizio() {
		return this.giudizio;
	}

	public void setGiudizio(String giudizio) {
		this.giudizio = giudizio;
	}

	public String getProvvedimenti() {
		return this.provvedimenti;
	}

	public void setProvvedimenti(String provvedimenti) {
		this.provvedimenti = provvedimenti;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

}