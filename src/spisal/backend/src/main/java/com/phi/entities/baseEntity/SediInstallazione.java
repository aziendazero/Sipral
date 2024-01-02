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


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.SediInstallazione;
@javax.persistence.Entity
@Table(name = "sedi_installazione")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SediInstallazione extends BaseEntity {

	private static final long serialVersionUID = 510349491L;

	/**
	*  javadoc for deletable
	*/
	private Boolean deletable;

	@Column(name="deletable")
	public Boolean getDeletable(){
		return deletable;
	}

	public void setDeletable(Boolean deletable){
		this.deletable = deletable;
	}


	/**
	*  sede originale - usata solo se copy = true
	*/
	private SediInstallazione sediInstallazioneOrig;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_installazione_orig_id")
	@ForeignKey(name="FK_sedeinst_sedeinst_orig")
	//@Index(name="IX_Sdnstllzon_sdnstllzonOrg")
	public SediInstallazione getSediInstallazioneOrig(){
		return sediInstallazioneOrig;
	}

	public void setSediInstallazioneOrig(SediInstallazione sediInstallazioneOrig){
		this.sediInstallazioneOrig = sediInstallazioneOrig;
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
	*  javadoc for tipologiaSede
	*/
	private CodeValuePhi tipologiaSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaSede")
	@ForeignKey(name="FK_SediInstallazione_tipologiaSede")
	//@Index(name="IX_SediInstallazione_tipologiaSede")
	public CodeValuePhi getTipologiaSede(){
		return tipologiaSede;
	}

	public void setTipologiaSede(CodeValuePhi tipologiaSede){
		this.tipologiaSede = tipologiaSede;
	}

	/**
	*  javadoc for tipoSede
	*/
	private CodeValuePhi tipoSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoSede")
	@ForeignKey(name="FK_SediInstallazione_tipoSede")
	//@Index(name="IX_SediInstallazione_tipoSede")
	public CodeValuePhi getTipoSede(){
		return tipoSede;
	}

	public void setTipoSede(CodeValuePhi tipoSede){
		this.tipoSede = tipoSede;
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
	*  javadoc for tipoUtente
	*
	private CodeValuePhi tipoUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoUtente")
	@ForeignKey(name="FK_SediInstallazione_tipoUtente")
	//@Index(name="IX_SediInstallazione_tipoUtente")
	public CodeValuePhi getTipoUtente(){
		return tipoUtente;
	}

	public void setTipoUtente(CodeValuePhi tipoUtente){
		this.tipoUtente = tipoUtente;
	}*/

	/**
	*  javadoc for territorio
	
	private CodeValuePhi territorio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="territorio")
	@ForeignKey(name="FK_SediInstallazione_territorio")
	//@Index(name="IX_SediInstallazione_territorio")
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

//	/**
//	*  javadoc for codiceFiscale
//	*/
//	private String codiceFiscale;
//
//	@Column(name="codice_fiscale")
//	public String getCodiceFiscale(){
//		return codiceFiscale;
//	}
//
//	public void setCodiceFiscale(String codiceFiscale){
//		this.codiceFiscale = codiceFiscale;
//	}
//
//	/**
//	*  javadoc for partitaIva
//	*/
//	private String partitaIva;
//
//	@Column(name="partita_iva")
//	public String getPartitaIva(){
//		return partitaIva;
//	}
//
//	public void setPartitaIva(String partitaIva){
//		this.partitaIva = partitaIva;
//	}

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
	*  javadoc for impianti
	*/
	private List<Impianto> impianti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sedeInstallazione", cascade=CascadeType.PERSIST)
	public List<Impianto> getImpianti() {
		return impianti;
	}

	public void setImpianti(List<Impianto>list){
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
			impianti.setSedeInstallazione(this);
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
			impianti.setSedeInstallazione(null);
		}
	}



	/**
	*  javadoc for sede
	*/
	private Sedi sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_SediInstallazione_sede")
	//@Index(name="IX_SediInstallazione_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SediInstallazione_sequence")
	@SequenceGenerator(name = "SediInstallazione_sequence", sequenceName = "SediInstallazione_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
