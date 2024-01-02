package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

//import com.phi.entities.baseEntity.SediAddebito;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.role.Employee;
import com.phi.entities.baseEntity.PersoneGiuridiche;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Sedi;


@javax.persistence.Entity
@Table(name = "cessione_imp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CessioneImp extends BaseEntity {

	private static final long serialVersionUID = 2076715163L;

	/**
	*  javadoc for delReason
	*/
	private String delReason;

	@Column(name="del_reason")
	public String getDelReason(){
		return delReason;
	}

	public void setDelReason(String delReason){
		this.delReason = delReason;
	}

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
	*  javadoc for sediFrom
	*/
	private Sedi sediFrom;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_from_id")
	@ForeignKey(name="FK_CessioneImp_sediFrom")
	//@Index(name="IX_CessioneImp_sediFrom")
	public Sedi getSediFrom(){
		return sediFrom;
	}

	public void setSediFrom(Sedi sediFrom){
		this.sediFrom = sediFrom;
	}



	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_CessioneImp_sedi")
	//@Index(name="IX_CessioneImp_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}



	/**
	*  javadoc for dataUltimaModifica
	*/
	private Date dataUltimaModifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_modifica")
	public Date getDataUltimaModifica(){
		return dataUltimaModifica;
	}

	public void setDataUltimaModifica(Date dataUltimaModifica){
		this.dataUltimaModifica = dataUltimaModifica;
	}

	/**
	*  javadoc for personaGiuridicaFrom
	*/
	private PersoneGiuridiche personaGiuridicaFrom;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="persona_giuridica_from_id")
	@ForeignKey(name="FK_CssonImp_prsonaGurdcaFrm")
	//@Index(name="IX_CssonImp_prsonaGurdcaFrm")
	public PersoneGiuridiche getPersonaGiuridicaFrom(){
		return personaGiuridicaFrom;
	}

	public void setPersonaGiuridicaFrom(PersoneGiuridiche personaGiuridicaFrom){
		this.personaGiuridicaFrom = personaGiuridicaFrom;
	}

	/**
	*  javadoc for personaGiuridica
	*/
	private PersoneGiuridiche personaGiuridica;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="persona_giuridica_id")
	@ForeignKey(name="FK_CssionImp_prsonaGiuridca")
	//@Index(name="IX_CssionImp_prsonaGiuridca")
	public PersoneGiuridiche getPersonaGiuridica(){
		return personaGiuridica;
	}

	public void setPersonaGiuridica(PersoneGiuridiche personaGiuridica){
		this.personaGiuridica = personaGiuridica;
	}

	/**
	*  javadoc for utente
	*/
	private Employee utente;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="utente_id")
	@ForeignKey(name="FK_CessioneImp_utente")
	//@Index(name="IX_CessioneImp_utente")
	public Employee getUtente(){
		return utente;
	}

	public void setUtente(Employee utente){
		this.utente = utente;
	}

	/**
	*  javadoc for impTerra
	*/
	private ImpTerra impTerra;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_terra_id")
	@ForeignKey(name="FK_CessioneImp_impTerra")
	//@Index(name="IX_CessioneImp_impTerra")
	public ImpTerra getImpTerra(){
		return impTerra;
	}

	public void setImpTerra(ImpTerra impTerra){
		this.impTerra = impTerra;
	}

	/**
	*  javadoc for impSoll
	*/
	private ImpSoll impSoll;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_soll_id")
	@ForeignKey(name="FK_CessioneImp_impSoll")
	//@Index(name="IX_CessioneImp_impSoll")
	public ImpSoll getImpSoll(){
		return impSoll;
	}

	public void setImpSoll(ImpSoll impSoll){
		this.impSoll = impSoll;
	}

	/**
	*  javadoc for impRisc
	*/
	private ImpRisc impRisc;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_risc_id")
	@ForeignKey(name="FK_CessioneImp_impRisc")
	//@Index(name="IX_CessioneImp_impRisc")
	public ImpRisc getImpRisc(){
		return impRisc;
	}

	public void setImpRisc(ImpRisc impRisc){
		this.impRisc = impRisc;
	}

	/**
	*  javadoc for impPress
	*/
	private ImpPress impPress;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_press_id")
	@ForeignKey(name="FK_CessioneImp_impPress")
	//@Index(name="IX_CessioneImp_impPress")
	public ImpPress getImpPress(){
		return impPress;
	}

	public void setImpPress(ImpPress impPress){
		this.impPress = impPress;
	}

	/**
	*  javadoc for impMonta
	*/
	private ImpMonta impMonta;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_monta_id")
	@ForeignKey(name="FK_CessioneImp_impMonta")
	//@Index(name="IX_CessioneImp_impMonta")
	public ImpMonta getImpMonta(){
		return impMonta;
	}

	public void setImpMonta(ImpMonta impMonta){
		this.impMonta = impMonta;
	}

	/**
	*  javadoc for note
	*/
	private String note;
	
	@Column(name="note", length=4000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for dataCessione
	*/
	private Date dataCessione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cessione")
	public Date getDataCessione(){
		return dataCessione;
	}

	public void setDataCessione(Date dataCessione){
		this.dataCessione = dataCessione;
	}

	/**
	*  javadoc for indirizzoSped
	*/
	private IndirizzoSped indirizzoSped;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="indirizzo_sped_id")
	@ForeignKey(name="FK_CessioneImp_indirizzoSped")
	//@Index(name="IX_CessioneImp_indirizzoSped")
	public IndirizzoSped getIndirizzoSped(){
		return indirizzoSped;
	}

	public void setIndirizzoSped(IndirizzoSped indirizzoSped){
		this.indirizzoSped = indirizzoSped;
	}

	/**
	*  javadoc for sediInstallazione
	*/
	private SediInstallazione sediInstallazione;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_installazione_id")
	@ForeignKey(name="FK_CssionImp_sdiInstallazon")
	//@Index(name="IX_CssionImp_sdiInstallazon")
	public SediInstallazione getSediInstallazione(){
		return sediInstallazione;
	}

	public void setSediInstallazione(SediInstallazione sediInstallazione){
		this.sediInstallazione = sediInstallazione;
	}

	/**
	*  javadoc for sediAddebito
	*/
//	private SediAddebito sediAddebito;
//
//	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
//	@JoinColumn(name="sedi_addebito_id")
//	@ForeignKey(name="FK_CessioneImp_sediAddebito")
//	//@Index(name="IX_CessioneImp_sediAddebito")
//	public SediAddebito getSediAddebito(){
//		return sediAddebito;
//	}
//
//	public void setSediAddebito(SediAddebito sediAddebito){
//		this.sediAddebito = sediAddebito;
//	}
	
	/**
	*  javadoc for indirizzoSped
	*/
	private IndirizzoSped indirizzoSpedFrom;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="indirizzo_spedfrom_id")
	@ForeignKey(name="FK_CessImp_indSpedFrom")
	//@Index(name="IX_CessImp_indSpedFrom")
	public IndirizzoSped getIndirizzoSpedFrom(){
		return indirizzoSpedFrom;
	}

	public void setIndirizzoSpedFrom(IndirizzoSped indirizzoSpedFrom){
		this.indirizzoSpedFrom = indirizzoSpedFrom;
	}

	/**
	*  javadoc for sediInstallazione
	*/
	private SediInstallazione sediInstallazioneFrom;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_instfrom_id")
	@ForeignKey(name="FK_CssionImp_sdiInstFrom")
	//@Index(name="IX_CssionImp_sdiInstFrom")
	public SediInstallazione getSediInstallazioneFrom(){
		return sediInstallazioneFrom;
	}

	public void setSediInstallazioneFrom(SediInstallazione sediInstallazioneFrom){
		this.sediInstallazioneFrom = sediInstallazioneFrom;
	}

	/**
	*  javadoc for sediAddebito
	*/
//	private SediAddebito sediAddebitoFrom;
//
//	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
//	@JoinColumn(name="sedi_addfrom_id")
//	@ForeignKey(name="FK_CssImp_sediAddFrom")
//	//@Index(name="IX_CssImp_sediAddFrom")
//	public SediAddebito getSediAddebitoFrom(){
//		return sediAddebitoFrom;
//	}
//
//	public void setSediAddebitoFrom(SediAddebito sediAddebitoFrom){
//		this.sediAddebitoFrom = sediAddebitoFrom;
//	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CessioneImp_sequence")
	@SequenceGenerator(name = "CessioneImp_sequence", sequenceName = "CessioneImp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
