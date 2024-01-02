package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.TEL;
import javax.persistence.ManyToMany;
import com.phi.entities.baseEntity.IndirizzoSped;

@javax.persistence.Entity
@Table(name = "sedi_addebito")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SediAddebito extends BaseEntity {

	private static final long serialVersionUID = 510934813L;

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
	*  javadoc for cig
	*/
	private String cig;

	@Column(name="cig")
	public String getCig(){
		return cig;
	}

	public void setCig(String cig){
		this.cig = cig;
	}

	/**
	*  javadoc for impSpesa
	*/
	private String impSpesa;

	@Column(name="imp_spesa")
	public String getImpSpesa(){
		return impSpesa;
	}

	public void setImpSpesa(String impSpesa){
		this.impSpesa = impSpesa;
	}

	/**
	*  javadoc for tipoUtente
	*/
	private CodeValuePhi tipoUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoUtente")
	@ForeignKey(name="FK_SediAddebito_tipoUtente")
	@Index(name="IX_SediAddebito_tipoUtente")
	public CodeValuePhi getTipoUtente(){
		return tipoUtente;
	}

	public void setTipoUtente(CodeValuePhi tipoUtente){
		this.tipoUtente = tipoUtente;
	}

	/**
	*  javadoc for esenzione
	*/
	private CodeValuePhi esenzione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esenzione")
	@ForeignKey(name="FK_SediAddebito_esenzione")
	@Index(name="IX_SediAddebito_esenzione")
	public CodeValuePhi getEsenzione(){
		return esenzione;
	}

	public void setEsenzione(CodeValuePhi esenzione){
		this.esenzione = esenzione;
	}

	/**
	*  javadoc for settore
	*/
	private CodeValuePhi settore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settore")
	@ForeignKey(name="FK_SediAddebito_settore")
	@Index(name="IX_SediAddebito_settore")
	public CodeValuePhi getSettore(){
		return settore;
	}

	public void setSettore(CodeValuePhi settore){
		this.settore = settore;
	}

	/**
	*  javadoc for tipoAttivita
	*/
	private CodeValuePhi tipoAttivita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoAttivita")
	@ForeignKey(name="FK_SediAddebito_tipoAttivita")
	@Index(name="IX_SediAddebito_tipoAttivita")
	public CodeValuePhi getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(CodeValuePhi tipoAttivita){
		this.tipoAttivita = tipoAttivita;
	}

	/**
	*  javadoc for classeEconomica
	*/
	private CodeValuePhi classeEconomica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="classeEconomica")
	@ForeignKey(name="FK_SediAddebito_classeEconomica")
	@Index(name="IX_SediAddebito_classeEconomica")
	public CodeValuePhi getClasseEconomica(){
		return classeEconomica;
	}

	public void setClasseEconomica(CodeValuePhi classeEconomica){
		this.classeEconomica = classeEconomica;
	}

	/**
	*  javadoc for codContabilita
	*/
	private String codContabilita;

	@Column(name="cod_contabilita")
	public String getCodContabilita(){
		return codContabilita;
	}

	public void setCodContabilita(String codContabilita){
		this.codContabilita = codContabilita;
	}


	/**
	*  javadoc for indirizzoSpedPrinc
	*/
	private IndirizzoSped indirizzoSpedPrinc;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="indirizzo_sped_princ_id")
	@ForeignKey(name="FK_SdiAddbito_ndrzzoSpdPrnc")
	@Index(name="IX_SdiAddbito_ndrzzoSpdPrnc")
	public IndirizzoSped getIndirizzoSpedPrinc(){
		return indirizzoSpedPrinc;
	}

	public void setIndirizzoSpedPrinc(IndirizzoSped indirizzoSpedPrinc){
		this.indirizzoSpedPrinc = indirizzoSpedPrinc;
	}



	/**
	*  javadoc for indirizzoSped
	*/
	private List<IndirizzoSped> indirizzoSped;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	public List<IndirizzoSped> getIndirizzoSped() {
		return indirizzoSped;
	}

	public void setIndirizzoSped(List<IndirizzoSped>list){
		indirizzoSped = list;
	}

	public void addIndirizzoSped(IndirizzoSped indirizzoSped) {
		if (this.indirizzoSped == null) {
			this.indirizzoSped = new ArrayList<IndirizzoSped>();
		}
		// add the association
		if(!this.indirizzoSped.contains(indirizzoSped)) {
			this.indirizzoSped.add(indirizzoSped);
			// make the inverse link
			if (indirizzoSped.getSediAddebito() == null || !indirizzoSped.getSediAddebito().contains(this))
				indirizzoSped.addSediAddebito(this);
		}
	}

	public void removeIndirizzoSped(IndirizzoSped indirizzoSped) {
		if (this.indirizzoSped == null) {
			this.indirizzoSped = new ArrayList<IndirizzoSped>();
			return;
		}
		//add the association
		if(this.indirizzoSped.contains(indirizzoSped)){
			this.indirizzoSped.remove(indirizzoSped);
			//make the inverse link
			if (indirizzoSped.getSediAddebito() != null && indirizzoSped.getSediAddebito().contains(this))
			indirizzoSped.removeSediAddebito(this);
		}
	}

	
	
//	/**
//	*  javadoc for indirizzi spedizione
//	*/
//	private List<IndirizzoSped> indirizzoSped;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="sedeAddebito", cascade=CascadeType.PERSIST)
//	public List<IndirizzoSped> getIndirizzoSped() {
//		return indirizzoSped;
//	}
//
//	public void setIndirizzoSped(List<IndirizzoSped>list){
//		indirizzoSped = list;
//	}
//
//	public void addIndirizzoSped(IndirizzoSped indirizzi) {
//		if (this.indirizzoSped == null) {
//			this.indirizzoSped = new ArrayList<IndirizzoSped>();
//		}
//		// add the association
//		if(!this.indirizzoSped.contains(indirizzi)) {
//			this.indirizzoSped.add(indirizzi);
//			// make the inverse link
//			indirizzi.setSedeAddebito(this);
//		}
//	}
//
//	public void removeIndirizzoSped(IndirizzoSped indirizzi) {
//		if (this.indirizzoSped == null) {
//			this.indirizzoSped = new ArrayList<IndirizzoSped>();
//			return;
//		}
//		//add the association
//		if(this.indirizzoSped.contains(indirizzi)){
//			this.indirizzoSped.remove(indirizzi);
//			//make the inverse link
//			indirizzi.setSedeAddebito(null);
//		}
//	}
	
	

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
	*  javadoc for stato
	*/
	private CodeValueCountry stato;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="stato")
	@ForeignKey(name="FK_SediAddebito_stato")
	@Index(name="IX_SediAddebito_stato")
	public CodeValueCountry getStato(){
		return stato;
	}

	public void setStato(CodeValueCountry stato){
		this.stato = stato;
	}

	/**
	*  javadoc for codiceVia
	*/
	private CodeValuePhi codiceVia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codiceVia")
	@ForeignKey(name="FK_SediAddebito_codiceVia")
	@Index(name="IX_SediAddebito_codiceVia")
	public CodeValuePhi getCodiceVia(){
		return codiceVia;
	}

	public void setCodiceVia(CodeValuePhi codiceVia){
		this.codiceVia = codiceVia;
	}

	/**
	*  javadoc for zona
	*/
	private CodeValuePhi zona;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="zona")
	@ForeignKey(name="FK_SediAddebito_zona")
	@Index(name="IX_SediAddebito_zona")
	public CodeValuePhi getZona(){
		return zona;
	}

	public void setZona(CodeValuePhi zona){
		this.zona = zona;
	}

	/**
	*  javadoc for territorio
	*
	private CodeValuePhi territorio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="territorio")
	@ForeignKey(name="FK_SediAddebito_territorio")
	@Index(name="IX_SediAddebito_territorio")
	public CodeValuePhi getTerritorio(){
		return territorio;
	}

	public void setTerritorio(CodeValuePhi territorio){
		this.territorio = territorio;
	}*/

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
	*  javadoc for impianti
	*/
	private List<Impianto> impianti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sedeAddebito", cascade=CascadeType.PERSIST)
	public List<Impianto> getImpianti(){
		return impianti;
	}

	public void setImpianti(List<Impianto> list){
		impianti = list;
	}

	public void addImpianti(Impianto impianti) {
		if (this.impianti == null) {
			this.impianti = new ArrayList<Impianto>();
		}
		// add the association
		if(!this.impianti.contains(impianti)) {
			this.impianti.add(impianti);
			// make the inverse link
			impianti.setSedeAddebito(this);
		}
	}

	public void removeImpianti(Impianto impianti) {
		if (this.impianti == null) {
			this.impianti = new ArrayList<Impianto>();
			return;
		}
		//add the association
		if(this.impianti.contains(impianti)){
			this.impianti.remove(impianti);
			//make the inverse link
			impianti.setSedeAddebito(null);
		}

	}



	/**
	*  javadoc for personaGiuridica
	*/
	private PersoneGiuridiche personaGiuridica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persona_giuridica_id")
	@ForeignKey(name="FK_SdiAddbito_prsonaGiurdca")
	@Index(name="IX_SdiAddbito_prsonaGiurdca")
	public PersoneGiuridiche getPersonaGiuridica(){
		return personaGiuridica;
	}

	public void setPersonaGiuridica(PersoneGiuridiche personaGiuridica){
		this.personaGiuridica = personaGiuridica;
	}




	/**
	*  javadoc for sede
	*/
	/*private Sedi sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_SediAddebito_sede")
	@Index(name="IX_SediAddebito_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}*/


	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SediAddebito_sequence")
	@SequenceGenerator(name = "SediAddebito_sequence", sequenceName = "SediAddebito_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
