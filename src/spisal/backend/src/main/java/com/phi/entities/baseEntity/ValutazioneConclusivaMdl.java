package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import com.phi.entities.baseEntity.Procpratiche;

import com.phi.entities.dataTypes.CodeValuePhi;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import javax.persistence.JoinColumn;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.phi.entities.role.Operatore;
import com.phi.entities.baseEntity.DiagMdl;

@javax.persistence.Entity
@Table(name = "valutazione_conclusiva_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ValutazioneConclusivaMdl extends BaseEntity {

	private static final long serialVersionUID = 1623302644L;

	/**
	*  javadoc for ggControlli
	*/
	private Integer ggControlli;

	@Column(name="gg_controlli")
	public Integer getGgControlli(){
		return ggControlli;
	}

	public void setGgControlli(Integer ggControlli){
		this.ggControlli = ggControlli;
	}

	/**
	*  javadoc for compilazioneCertificato
	*/
	private Boolean compilazioneCertificato;

	@Column(name="compilazione_certificato")
	public Boolean getCompilazioneCertificato(){
		return compilazioneCertificato;
	}

	public void setCompilazioneCertificato(Boolean compilazioneCertificato){
		this.compilazioneCertificato = compilazioneCertificato;
	}

	/**
	*  javadoc for valutazione
	*/
	private String valutazione;

	@Column(name="valutazione")
	public String getValutazione(){
		return valutazione;
	}

	public void setValutazione(String valutazione){
		this.valutazione = valutazione;
	}

	/**
	*  javadoc for uscitaFollowUp
	*/
	private Date uscitaFollowUp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="uscita_follow_up")
	public Date getUscitaFollowUp(){
		return uscitaFollowUp;
	}

	public void setUscitaFollowUp(Date uscitaFollowUp){
		this.uscitaFollowUp = uscitaFollowUp;
	}

	/**
	*  javadoc for uscitaFollowUpTxt
	*/
	private String uscitaFollowUpTxt;

	@Column(name="uscita_follow_up_txt",length=4000)
	public String getUscitaFollowUpTxt(){
		return uscitaFollowUpTxt;
	}

	public void setUscitaFollowUpTxt(String uscitaFollowUpTxt){
		this.uscitaFollowUpTxt = uscitaFollowUpTxt;
	}

	/**
	*  javadoc for controllo
	*/
	private Date controllo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="controllo")
	public Date getControllo(){
		return controllo;
	}

	public void setControllo(Date controllo){
		this.controllo = controllo;
	}

	/**
	*  javadoc for conclusioneVisita
	*/
	private Date conclusioneVisita;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="conclusione_visita")
	public Date getConclusioneVisita(){
		return conclusioneVisita;
	}

	public void setConclusioneVisita(Date conclusioneVisita){
		this.conclusioneVisita = conclusioneVisita;
	}

	/**
	*  javadoc for idoneitaMdl
	*/
	private CodeValuePhi idoneitaMdl;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idoneitaMdl")
	@ForeignKey(name="FK_ValutazioneConclusivaMdl_idoneitaMdl")
	//@Index(name="IX_ValutazioneConclusivaMdl_idoneitaMdl")
	public CodeValuePhi getIdoneitaMdl(){
		return idoneitaMdl;
	}

	public void setIdoneitaMdl(CodeValuePhi idoneitaMdl){
		this.idoneitaMdl = idoneitaMdl;
	}


	/**
	*  javadoc for diagMdl
	*/
	private List<DiagMdl> diagMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="valutazioneConclusivaMdl", cascade=CascadeType.PERSIST)
	public List<DiagMdl> getDiagMdl() {
		return diagMdl;
	}

	public void setDiagMdl(List<DiagMdl>list){
		diagMdl = list;
	}

	public void addDiagMdl(DiagMdl diagMdl) {
		if (this.diagMdl == null) {
			this.diagMdl = new ArrayList<DiagMdl>();
		}
		// add the association
		if(!this.diagMdl.contains(diagMdl)) {
			this.diagMdl.add(diagMdl);
			// make the inverse link
			diagMdl.setValutazioneConclusivaMdl(this);
		}
	}

	public void removeDiagMdl(DiagMdl diagMdl) {
		if (this.diagMdl == null) {
			this.diagMdl = new ArrayList<DiagMdl>();
			return;
		}
		//add the association
		if(this.diagMdl.contains(diagMdl)){
			this.diagMdl.remove(diagMdl);
			//make the inverse link
			diagMdl.setValutazioneConclusivaMdl(null);
		}
	}


	/**
	*  javadoc for diagnosiTxt
	*/
	private String diagnosiTxt;

	@Column(name="diagnosi_txt", length=4000)
	public String getDiagnosiTxt(){
		return diagnosiTxt;
	}

	public void setDiagnosiTxt(String diagnosiTxt){
		this.diagnosiTxt = diagnosiTxt;
	}

	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_VltzoneConclsvMdl_pertre")
	//@Index(name="IX_VltzoneConclsvMdl_pertre")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
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
	*  javadoc for nota
	*/
	private String nota;

	@Column(name="nota", length=4000)
	public String getNota(){
		return nota;
	}

	public void setNota(String nota){
		this.nota = nota;
	}

	/**
	*  javadoc for parere
	*/
	private CodeValuePhi parere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parere")
	@ForeignKey(name="FK_ValutazioneConclusivaMdl_parere")
	//@Index(name="IX_ValutazioneConclusivaMdl_parere")
	public CodeValuePhi getParere(){
		return parere;
	}

	public void setParere(CodeValuePhi parere){
		this.parere = parere;
	}


	/**
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="conclusioniMdl"/*, cascade=CascadeType.PERSIST*/)
	public List<Procpratiche> getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(List<Procpratiche> list){
		procpratiche = list;
	}

	public void addProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
		}
		// add the association
		if(!this.procpratiche.contains(procpratiche)) {
			this.procpratiche.add(procpratiche);
			// make the inverse link
			procpratiche.setConclusioniMdl(this);
		}
	}

	public void removeProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
			return;
		}
		//add the association
		if(this.procpratiche.contains(procpratiche)){
			this.procpratiche.remove(procpratiche);
			//make the inverse link
			procpratiche.setConclusioniMdl(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "valconcmdl_sequence")
	@SequenceGenerator(name = "valconcmdl_sequence", sequenceName = "valconcmdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
