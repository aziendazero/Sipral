package com.sorves.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;


/**
 * The persistent class for the VISITA database table.
 * 
 */
@Entity
@Table(name="visita")
public class Visita implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal alcolper;
	private BigDecimal alcolqta;
	private BigDecimal altezza;
	private Date dataprel;
	private BigDecimal freqcard;
	private BigDecimal fumoper;
	private BigDecimal fumoqta;
	private long id;
	private String noteabvoluttuarie;
	private String noteanamnesi;
	private String noteesameobiettivo;
	private BigDecimal pamax;
	private BigDecimal pamin;
	private BigDecimal peso;
	private BigDecimal straordinaria;

	private Anagrafica anagrafica;

	private Tabfumo tabfumo;

	private List<Tabobiettivita> tabobiettivita;

	public Visita() {
	}

	public BigDecimal getAlcolper() {
		return this.alcolper;
	}

	public void setAlcolper(BigDecimal alcolper) {
		this.alcolper = alcolper;
	}

	public BigDecimal getAlcolqta() {
		return this.alcolqta;
	}

	public void setAlcolqta(BigDecimal alcolqta) {
		this.alcolqta = alcolqta;
	}

	public BigDecimal getAltezza() {
		return this.altezza;
	}

	public void setAltezza(BigDecimal altezza) {
		this.altezza = altezza;
	}

	@Temporal(TemporalType.DATE)
	public Date getDataprel() {
		return this.dataprel;
	}

	public void setDataprel(Date dataprel) {
		this.dataprel = dataprel;
	}

	public BigDecimal getFreqcard() {
		return this.freqcard;
	}

	public void setFreqcard(BigDecimal freqcard) {
		this.freqcard = freqcard;
	}

	public BigDecimal getFumoper() {
		return this.fumoper;
	}

	public void setFumoper(BigDecimal fumoper) {
		this.fumoper = fumoper;
	}

	public BigDecimal getFumoqta() {
		return this.fumoqta;
	}

	public void setFumoqta(BigDecimal fumoqta) {
		this.fumoqta = fumoqta;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Type(type="text")
	public String getNoteabvoluttuarie() {
		return this.noteabvoluttuarie;
	}

	public void setNoteabvoluttuarie(String noteabvoluttuarie) {
		this.noteabvoluttuarie = noteabvoluttuarie;
	}

	@Type(type="text")
	public String getNoteanamnesi() {
		return this.noteanamnesi;
	}

	public void setNoteanamnesi(String noteanamnesi) {
		this.noteanamnesi = noteanamnesi;
	}

	@Type(type="text")
	public String getNoteesameobiettivo() {
		return this.noteesameobiettivo;
	}

	public void setNoteesameobiettivo(String noteesameobiettivo) {
		this.noteesameobiettivo = noteesameobiettivo;
	}

	public BigDecimal getPamax() {
		return this.pamax;
	}

	public void setPamax(BigDecimal pamax) {
		this.pamax = pamax;
	}

	public BigDecimal getPamin() {
		return this.pamin;
	}

	public void setPamin(BigDecimal pamin) {
		this.pamin = pamin;
	}

	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getStraordinaria() {
		return this.straordinaria;
	}

	public void setStraordinaria(BigDecimal straordinaria) {
		this.straordinaria = straordinaria;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idanagrafe", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}

	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idfumo", referencedColumnName = "id")
	public Tabfumo getTabfumo() {
	    return tabfumo;
	}

	public void setTabfumo(Tabfumo param) {
	    this.tabfumo = param;
	}

	@ManyToMany
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinTable(
			name="visitaesame"
			, joinColumns={
				@JoinColumn(name="idvisita")
				}
			, inverseJoinColumns={
				@JoinColumn(name="idobiettivita")
				}
			)
	public List<Tabobiettivita> getTabobiettivita() {
	    return tabobiettivita;
	}

	public void setTabobiettivita(List<Tabobiettivita> param) {
	    this.tabobiettivita = param;
	}

}