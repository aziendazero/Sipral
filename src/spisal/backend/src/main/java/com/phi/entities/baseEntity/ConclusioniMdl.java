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
import com.phi.entities.baseEntity.VisitaMdl;

import com.phi.entities.baseEntity.DiagMdl;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "conclusioni_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ConclusioniMdl extends BaseEntity {

	private static final long serialVersionUID = 1010808586L;

	/**
	*  javadoc for durataMesi
	*/
	private Integer durataMesi;

	@Column(name="durata_mesi")
	public Integer getDurataMesi(){
		return durataMesi;
	}

	public void setDurataMesi(Integer durataMesi){
		if (durataMesi==null)
			this.durataMesi=0;
		
		else
			this.durataMesi = durataMesi;
	}

	/**
	*  javadoc for idoneitaMdl
	*/
	private CodeValuePhi idoneitaMdl;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idoneitaMdl")
	@ForeignKey(name="FK_ConclusioniMdl_idoneitaMdl")
	//@Index(name="IX_ConclusioniMdl_idoneitaMdl")
	public CodeValuePhi getIdoneitaMdl(){
		return idoneitaMdl;
	}

	public void setIdoneitaMdl(CodeValuePhi idoneitaMdl){
		this.idoneitaMdl = idoneitaMdl;
	}

	/**
	*  javadoc for scadenza
	*/
	private Date scadenza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scadenza")
	public Date getScadenza(){
		return scadenza;
	}

	public void setScadenza(Date scadenza){
		this.scadenza = scadenza;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note",length=4000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
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
	*  javadoc for diagMdl
	*/
	private List<DiagMdl> diagMdl = new ArrayList<DiagMdl>();

	@OneToMany(fetch=FetchType.LAZY, mappedBy="conclusioniMdl", cascade=CascadeType.PERSIST)
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
			diagMdl.setConclusioniMdl(this);
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
			diagMdl.setConclusioniMdl(null);
		}
	}


	/**
	*  javadoc for diagnosiTxt
	*/
	private String diagnosiTxt;

	@Column(name="diagnosi_txt",length=4000)
	public String getDiagnosiTxt(){
		return diagnosiTxt;
	}

	public void setDiagnosiTxt(String diagnosiTxt){
		this.diagnosiTxt = diagnosiTxt;
	}


	/**
	*  javadoc for visitaMdl
	*/
	private List<VisitaMdl> visitaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="conclusioniMdl", cascade=CascadeType.PERSIST)
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
			visitaMdl.setConclusioniMdl(this);
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
			visitaMdl.setConclusioniMdl(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ConclusioniMdl_sequence")
	@SequenceGenerator(name = "ConclusioniMdl_sequence", sequenceName = "ConclusioniMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
