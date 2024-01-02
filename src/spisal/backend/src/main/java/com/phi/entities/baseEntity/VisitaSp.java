package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.baseEntity.AppointmentSp;

@javax.persistence.Entity
@Table(name = "visita_sp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VisitaSp extends BaseEntity {

	private static final long serialVersionUID = 427689930L;

	/**
	*  javadoc for aod
	*/
	private String aod;

	@Column(name="aod")
	public String getAod(){
		return aod;
	}

	public void setAod(String aod){
		this.aod = aod;
	}

	/**
	*  javadoc for aos
	*/
	private String aos;

	@Column(name="aos")
	public String getAos(){
		return aos;
	}

	public void setAos(String aos){
		this.aos = aos;
	}

	/**
	*  javadoc for anamnesiProssima
	*/
	private String anamnesiProssima;

	@Column(name="anamnesi_prossima", length=2000)
	public String getAnamnesiProssima(){
		return anamnesiProssima;
	}

	public void setAnamnesiProssima(String anamnesiProssima){
		this.anamnesiProssima = anamnesiProssima;
	}


	/**
	*  javadoc for appointmentSp
	*/
	private List<AppointmentSp> appointmentSp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaSp", cascade=CascadeType.PERSIST)
	public List<AppointmentSp> getAppointmentSp(){
		return appointmentSp;
	}

	public void setAppointmentSp(List<AppointmentSp> list){
		appointmentSp = list;
	}

	public void addAppointmentSp(AppointmentSp appointmentSp) {
		if (this.appointmentSp == null) {
			this.appointmentSp = new ArrayList<AppointmentSp>();
		}
		// add the association
		if(!this.appointmentSp.contains(appointmentSp)) {
			this.appointmentSp.add(appointmentSp);
			// make the inverse link
			appointmentSp.setVisitaSp(this);
		}
	}

	public void removeAppointmentSp(AppointmentSp appointmentSp) {
		if (this.appointmentSp == null) {
			this.appointmentSp = new ArrayList<AppointmentSp>();
			return;
		}
		//add the association
		if(this.appointmentSp.contains(appointmentSp)){
			this.appointmentSp.remove(appointmentSp);
			//make the inverse link
			appointmentSp.setVisitaSp(null);
		}

	}


	/**
	*  javadoc for riferimento
	*/
	/*private CodeValuePhi riferimento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento")
	@ForeignKey(name="FK_VisitaSp_riferimento")
	//@Index(name="IX_VisitaSp_riferimento")
	public CodeValuePhi getRiferimento(){
		return riferimento;
	}

	public void setRiferimento(CodeValuePhi riferimento){
		this.riferimento = riferimento;
	}*/


	private Employee riferimentoInterno;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="riferimento_interno_id")
	@ForeignKey(name="FK_VistaSp_rfermentoInterno")
	//@Index(name="IX_VistaSp_rfermentoInterno")
	public Employee getRiferimentoInterno(){
		return riferimentoInterno;
	}

	public void setRiferimentoInterno(Employee riferimentoInterno){
		this.riferimentoInterno = riferimentoInterno;
	}

	/**
	*  javadoc for richiedente
	*/
	/*private CodeValuePhi richiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente")
	@ForeignKey(name="FK_VisitaSp_richiedente")
	//@Index(name="IX_VisitaSp_richiedente")
	public CodeValuePhi getRichiedente(){
		return richiedente;
	}

	public void setRichiedente(CodeValuePhi richiedente){
		this.richiedente = richiedente;
	}*/
	
	/**
	*  javadoc for richiedenteMedico
	*/
	/*private Physician richiedenteMedico;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_medico_id")
	@ForeignKey(name="FK_VisitaSp_ricMedico")
	//@Index(name="IX_VisitaSp_ricMedico")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Physician getRichiedenteMedico(){
		return richiedenteMedico;
	}

	public void setRichiedenteMedico(Physician richiedenteMedico){
		this.richiedenteMedico = richiedenteMedico;
	}*/
	
	/**
	*  javadoc for altroTxt
	*/
	private String altroTxt;

	@Column(name="altro_txt",length=500)
	public String getAltroTxt(){
		return altroTxt;
	}

	public void setAltroTxt(String altroTxt){
		this.altroTxt = altroTxt;
	}

	/**
	*  javadoc for altroBl
	*/
	private Boolean altroBl;

	@Column(name="altro_bl")
	public Boolean getAltroBl(){
		return altroBl;
	}

	public void setAltroBl(Boolean altroBl){
		this.altroBl = altroBl;
	}

	/**
	*  javadoc for artiInfTxt
	*/
	private String artiInfTxt;

	@Column(name="arti_inf_txt",length=500)
	public String getArtiInfTxt(){
		return artiInfTxt;
	}

	public void setArtiInfTxt(String artiInfTxt){
		this.artiInfTxt = artiInfTxt;
	}

	/**
	*  javadoc for artiInfBl
	*/
	private Boolean artiInfBl;

	@Column(name="arti_inf_bl")
	public Boolean getArtiInfBl(){
		return artiInfBl;
	}

	public void setArtiInfBl(Boolean artiInfBl){
		this.artiInfBl = artiInfBl;
	}

	/**
	*  javadoc for artiSupTxt
	*/
	private String artiSupTxt;

	@Column(name="arti_sup_txt",length=500)
	public String getArtiSupTxt(){
		return artiSupTxt;
	}

	public void setArtiSupTxt(String artiSupTxt){
		this.artiSupTxt = artiSupTxt;
	}

	/**
	*  javadoc for artiSupBl
	*/
	private Boolean artiSupBl;

	@Column(name="arti_sup_bl")
	public Boolean getArtiSupBl(){
		return artiSupBl;
	}

	public void setArtiSupBl(Boolean artiSupBl){
		this.artiSupBl = artiSupBl;
	}

	/**
	*  javadoc for addomeTxt
	*/
	private String addomeTxt;

	@Column(name="addome_txt",length=500)
	public String getAddomeTxt(){
		return addomeTxt;
	}

	public void setAddomeTxt(String addomeTxt){
		this.addomeTxt = addomeTxt;
	}

	/**
	*  javadoc for addomeBl
	*/
	private Boolean addomeBl;

	@Column(name="addome_bl")
	public Boolean getAddomeBl(){
		return addomeBl;
	}

	public void setAddomeBl(Boolean addomeBl){
		this.addomeBl = addomeBl;
	}

	/**
	*  javadoc for toraceTxt
	*/
	private String toraceTxt;

	@Column(name="torace_txt",length=500)
	public String getToraceTxt(){
		return toraceTxt;
	}

	public void setToraceTxt(String toraceTxt){
		this.toraceTxt = toraceTxt;
	}

	/**
	*  javadoc for toraceBl
	*/
	private Boolean toraceBl;

	@Column(name="torace_bl")
	public Boolean getToraceBl(){
		return toraceBl;
	}

	public void setToraceBl(Boolean toraceBl){
		this.toraceBl = toraceBl;
	}

	/**
	*  javadoc for cuoreTxt
	*/
	private String cuoreTxt;

	@Column(name="cuore_txt",length=500)
	public String getCuoreTxt(){
		return cuoreTxt;
	}

	public void setCuoreTxt(String cuoreTxt){
		this.cuoreTxt = cuoreTxt;
	}

	/**
	*  javadoc for cuoreBl
	*/
	private Boolean cuoreBl;

	@Column(name="cuore_bl")
	public Boolean getCuoreBl(){
		return cuoreBl;
	}

	public void setCuoreBl(Boolean cuoreBl){
		this.cuoreBl = cuoreBl;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note",length=500)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for hr
	*/
	private String hr;

	@Column(name="hr")
	public String getHr(){
		return hr;
	}

	public void setHr(String hr){
		this.hr = hr;
	}

	/**
	*  javadoc for paMin
	*/
	private String paMin;

	@Column(name="pa_min")
	public String getPaMin(){
		return paMin;
	}

	public void setPaMin(String paMin){
		this.paMin = paMin;
	}

	/**
	*  javadoc for paMax
	*/
	private String paMax;

	@Column(name="pa_max")
	public String getPaMax(){
		return paMax;
	}

	public void setPaMax(String paMax){
		this.paMax = paMax;
	}

	/**
	*  javadoc for peso
	*/
	private String peso;

	@Column(name="peso")
	public String getPeso(){
		return peso;
	}

	public void setPeso(String peso){
		this.peso = peso;
	}

	/**
	*  javadoc for altezza
	*/
	private String altezza;

	@Column(name="altezza")
	public String getAltezza(){
		return altezza;
	}

	public void setAltezza(String altezza){
		this.altezza = altezza;
	}

	/**
	*  javadoc for cromStereo
	*/
	private String cromStereo;

	@Column(name="crom_stereo",length=500)
	public String getCromStereo(){
		return cromStereo;
	}

	public void setCromStereo(String cromStereo){
		this.cromStereo = cromStereo;
	}

	/**
	*  javadoc for attFis
	*/
	private String attFis;

	@Column(name="att_fis")
	public String getAttFis(){
		return attFis;
	}

	public void setAttFis(String attFis){
		this.attFis = attFis;
	}

	/**
	*  javadoc for alcol
	*/
	private String alcol;

	@Column(name="alcol")
	public String getAlcol(){
		return alcol;
	}

	public void setAlcol(String alcol){
		this.alcol = alcol;
	}

	/**
	*  javadoc for fumo
	*/
	private String fumo;

	@Column(name="fumo")
	public String getFumo(){
		return fumo;
	}

	public void setFumo(String fumo){
		this.fumo = fumo;
	}

	/**
	*  javadoc for cilOs
	*/
	private String cilOs;

	@Column(name="cil_os")
	public String getCilOs(){
		return cilOs;
	}

	public void setCilOs(String cilOs){
		this.cilOs = cilOs;
	}

	/**
	*  javadoc for cilOd
	*/
	private String cilOd;

	@Column(name="cil_od")
	public String getCilOd(){
		return cilOd;
	}

	public void setCilOd(String cilOd){
		this.cilOd = cilOd;
	}

	/**
	*  javadoc for lentiOs
	*/
	private String lentiOs;

	@Column(name="lenti_os")
	public String getLentiOs(){
		return lentiOs;
	}

	public void setLentiOs(String lentiOs){
		this.lentiOs = lentiOs;
	}

	/**
	*  javadoc for lentiOd
	*/
	private String lentiOd;

	@Column(name="lenti_od")
	public String getLentiOd(){
		return lentiOd;
	}

	public void setLentiOd(String lentiOd){
		this.lentiOd = lentiOd;
	}

	/**
	*  javadoc for visus
	*/
	private CodeValuePhi visus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="visus")
	@ForeignKey(name="FK_VisitaSp_visus")
	//@Index(name="IX_VisitaSp_visus")
	public CodeValuePhi getVisus(){
		return visus;
	}

	public void setVisus(CodeValuePhi visus){
		this.visus = visus;
	}

	/**
	*  javadoc for visusOs
	*/
	private String visusOs;

	@Column(name="visus_os")
	public String getVisusOs(){
		return visusOs;
	}

	public void setVisusOs(String visusOs){
		this.visusOs = visusOs;
	}

	/**
	*  javadoc for visusOd
	*/
	private String visusOd;

	@Column(name="visus_od")
	public String getVisusOd(){
		return visusOd;
	}

	public void setVisusOd(String visusOd){
		this.visusOd = visusOd;
	}

	/**
	*  javadoc for altro
	*/
	private String altro;

	@Column(name="altro",length=500)
	public String getAltro(){
		return altro;
	}

	public void setAltro(String altro){
		this.altro = altro;
	}

	/**
	*  javadoc for accertamenti
	*/
	private List<CodeValuePhi> accertamenti;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="VisitaSp_accertamenti", joinColumns = { @JoinColumn(name="VisitaSp_id") }, inverseJoinColumns = { @JoinColumn(name="accertamenti") })
	@ForeignKey(name="FK_accertamenti_VisitaSp", inverseName="FK_VisitaSp_accertamenti")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getAccertamenti(){
		return accertamenti;
	}

	public void setAccertamenti(List<CodeValuePhi> accertamenti){
		this.accertamenti = accertamenti;
	}

	/**
	*  javadoc for terapia
	*/
	private String terapia;

	@Column(name="terapia", length=2000)
	public String getTerapia(){
		return terapia;
	}

	public void setTerapia(String terapia){
		this.terapia = terapia;
	}

	/**
	*  javadoc for malattie
	*/
	private String malattie;

	@Column(name="malattie", length=2000)
	public String getMalattie(){
		return malattie;
	}

	public void setMalattie(String malattie){
		this.malattie = malattie;
	}

	/**
	*  javadoc for pagato
	*/
	private Boolean pagato;

	@Column(name="pagato")
	public Boolean getPagato(){
		return pagato;
	}

	public void setPagato(Boolean pagato){
		this.pagato = pagato;
	}

	/**
	*  javadoc for pagamento
	*/
	private CodeValuePhi pagamento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pagamento")
	@ForeignKey(name="FK_VisitaSp_pagamento")
	//@Index(name="IX_VisitaSp_pagamento")
	public CodeValuePhi getPagamento(){
		return pagamento;
	}

	public void setPagamento(CodeValuePhi pagamento){
		this.pagamento = pagamento;
	}

	/**
	*  javadoc for data
	*/
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}


	/**
	*  javadoc for visitaMdl
	*/
	private List<VisitaMdl> visitaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaSp", cascade=CascadeType.PERSIST)
	public List<VisitaMdl> getVisitaMdl(){
		return visitaMdl;
	}

	public void setVisitaMdl(List<VisitaMdl> list){
		visitaMdl = list;
	}

	public void addVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
		}
		// add the association
		if(!this.visitaMdl.contains(visitaMdl)) {
			this.visitaMdl.add(visitaMdl);
			// make the inverse link
			visitaMdl.setVisitaSp(this);
		}
	}

	public void removeVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
			return;
		}
		//add the association
		if(this.visitaMdl.contains(visitaMdl)){
			this.visitaMdl.remove(visitaMdl);
			//make the inverse link
			visitaMdl.setVisitaSp(null);
		}

	}


	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VisitaSp_sequence")
	@SequenceGenerator(name = "VisitaSp_sequence", sequenceName = "VisitaSp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
