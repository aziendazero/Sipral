package com.phi.entities.baseEntity;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.phi.entities.dataTypes.AD;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.ForeignKey;
//import com.phi.entities.baseEntity.SediAddebito;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.dataTypes.CodeValueCountry;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Sedi;
@javax.persistence.Entity
@Table(name = "indirizzo_sped")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class IndirizzoSped extends BaseEntity {

	private static final long serialVersionUID = 508684190L;


	/**
	*  javadoc for sedi
	*/
	private List<Sedi> sedi;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="indirizzoSped"/*, cascade=CascadeType.PERSIST*/)
	public List<Sedi> getSedi(){
		return sedi;
	}

	public void setSedi(List<Sedi> list){
		sedi = list;
	}

	public void addSedi(Sedi sedi) {
		if (this.sedi == null) {
			this.sedi = new ArrayList<Sedi>();
		}
		// add the association
		if(!this.sedi.contains(sedi)) {
			this.sedi.add(sedi);
			// make the inverse link
			if (sedi.getIndirizzoSped() == null || !sedi.getIndirizzoSped().contains(this))
				sedi.addIndirizzoSped(this);
		}
	}

	public void removeSedi(Sedi sedi) {
		if (this.sedi == null) {
			this.sedi = new ArrayList<Sedi>();
			return;
		}
		//add the association
		if(this.sedi.contains(sedi)){
			this.sedi.remove(sedi);
			//make the inverse link
			if (sedi.getIndirizzoSped() != null && sedi.getIndirizzoSped().contains(this))
				sedi.removeIndirizzoSped(this);
		}

	}


	/**
	*  javadoc for copy
	*/
	private Boolean copy;

	@Column(name="copy")
	public Boolean getCopy(){
		return copy;
	}

	public void setCopy(Boolean copy){
		this.copy = copy;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}


	/**
	*  javadoc for sediAddebito
	*/
//	private List<SediAddebito> sediAddebito;
//
//	@ManyToMany(fetch=FetchType.LAZY, mappedBy="indirizzoSped", cascade=CascadeType.PERSIST)
//	public List<SediAddebito> getSediAddebito(){
//		return sediAddebito;
//	}
//
//	public void setSediAddebito(List<SediAddebito> list){
//		sediAddebito = list;
//	}
//
//	public void addSediAddebito(SediAddebito sediAddebito) {
//		if (this.sediAddebito == null) {
//			this.sediAddebito = new ArrayList<SediAddebito>();
//		}
//		// add the association
//		if(!this.sediAddebito.contains(sediAddebito)) {
//			this.sediAddebito.add(sediAddebito);
//			// make the inverse link
//			if (sediAddebito.getIndirizzoSped() == null || !sediAddebito.getIndirizzoSped().contains(this))
//				sediAddebito.addIndirizzoSped(this);
//		}
//	}
//
//	public void removeSediAddebito(SediAddebito sediAddebito) {
//		if (this.sediAddebito == null) {
//			this.sediAddebito = new ArrayList<SediAddebito>();
//			return;
//		}
//		//add the association
//		if(this.sediAddebito.contains(sediAddebito)){
//			this.sediAddebito.remove(sediAddebito);
//			//make the inverse link
//			if (sediAddebito.getIndirizzoSped() != null && sediAddebito.getIndirizzoSped().contains(this))
//				sediAddebito.removeIndirizzoSped(this);
//		}
//
//	}


	/**
	*  javadoc for stato
	*/
	private CodeValueCountry stato;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="stato")
	@ForeignKey(name="FK_IndirizzoSped_stato")
	//@Index(name="IX_IndirizzoSped_stato")
	public CodeValueCountry getStato(){
		return stato;
	}

	public void setStato(CodeValueCountry stato){
		this.stato = stato;
	}

	/**
	*  javadoc for telecom
	*/
	private TEL telecom;

	@Embedded
	@AttributeOverrides({
	@AttributeOverride(name="as", column=@Column(name="telecom_as")),
	@AttributeOverride(name="bad", column=@Column(name="telecom_bad")),
	@AttributeOverride(name="dir", column=@Column(name="telecom_dir")),
	@AttributeOverride(name="ec", column=@Column(name="telecom_ec")),
	@AttributeOverride(name="h", column=@Column(name="telecom_h")),
	@AttributeOverride(name="hp", column=@Column(name="telecom_hp")),
	@AttributeOverride(name="hv", column=@Column(name="telecom_hv")),
	@AttributeOverride(name="mail", column=@Column(name="telecom_mail")),
	@AttributeOverride(name="mc", column=@Column(name="telecom_mc")),
	@AttributeOverride(name="pg", column=@Column(name="telecom_pg")),
	@AttributeOverride(name="pub", column=@Column(name="telecom_pub")),
	@AttributeOverride(name="sip", column=@Column(name="telecom_sip")),
	@AttributeOverride(name="tmp", column=@Column(name="telecom_tmp")),
	@AttributeOverride(name="wp", column=@Column(name="telecom_wp"))
	})
	public TEL getTelecom(){
		return telecom;
	}

	public void setTelecom(TEL telecom){
		this.telecom = telecom;
	}

	/**
	*  javadoc for denominazione
	*/
	private String denominazione;

	@Column(name="denominazione")
	public String getDenominazione(){
		return denominazione;
	}

	public void setDenominazione(String denominazione){
		this.denominazione = denominazione;
	}


//	/**
//	*  javadoc for sedeAddebito
//	*/
//	private SediAddebito sedeAddebito;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="sede_addebito_id")
//	@ForeignKey(name="FK_IndSped_sedeAddebito")
//	//@Index(name="IX_IndSped_sedeAddebito")
//	public SediAddebito getSedeAddebito(){
//		return sedeAddebito;
//	}
//
//	public void setSedeAddebito(SediAddebito sedeAddebito){
//		this.sedeAddebito = sedeAddebito;
//	}


	/**
	*  javadoc for principale
	*/
	private Boolean principale;

	@Column(name="principale")
	public Boolean getPrincipale(){
		return principale;
	}

	public void setPrincipale(Boolean principale){
		this.principale = principale;
	}

	/**
	*  javadoc for addr
	*/
	private AD addr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addr_code"))
	@AttributeOverrides({
	@AttributeOverride(name="adl", column=@Column(name="addr_adl")),
	@AttributeOverride(name="bnr", column=@Column(name="addr_bnr")),
	@AttributeOverride(name="cen", column=@Column(name="addr_cen")),
	@AttributeOverride(name="cnt", column=@Column(name="addr_cnt")),
	@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),
	@AttributeOverride(name="cty", column=@Column(name="addr_cty")),
	@AttributeOverride(name="sta", column=@Column(name="addr_sta")),
	@AttributeOverride(name="stb", column=@Column(name="addr_stb")),
	@AttributeOverride(name="str", column=@Column(name="addr_str")),
	@AttributeOverride(name="zip", column=@Column(name="addr_zip"))
	})
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "IndSped_sequence")
	@SequenceGenerator(name = "IndSped_sequence", sequenceName = "IndSped_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
