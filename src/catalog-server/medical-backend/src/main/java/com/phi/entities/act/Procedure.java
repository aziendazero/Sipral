package com.phi.entities.act;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.phi.entities.baseEntity.ProcedureRequest;
import com.phi.entities.baseEntity.Report;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

@Entity
@Table(name = "procedure_db")
@Audited
public class Procedure extends AbstractProcedure implements LocatedEntity {

	private static final long serialVersionUID = 1733967757754732711L;
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Procedure_sdloc")
	@Index(name="IX_Procedure_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}


	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	/**
	*  javadoc for patientEncounter
	*/
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="pat_enc_id")
	@ForeignKey(name="FK_procDisch_PatEnc")
	@Index(name="FK_procDisch_PatEnc")
	public PatientEncounter getPatientEncounter(){
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}
	
	/**
	*  javadoc for procedureRequest
	*/
	private ProcedureRequest procedureRequest;
	@JsonBackReference(value="procedure")
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ProcReq_id")
	@ForeignKey(name="FK_proc_ProcReq")
	@Index(name="IX_prcedure_PrcReq")
	public ProcedureRequest getProcedureRequest(){
		return procedureRequest;
	}

	public void setProcedureRequest(ProcedureRequest procedureRequest){
		this.procedureRequest = procedureRequest;
	}

	/**
	 * javadoc executeDate
	 */
	private Date executeDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="execute_date")
	public Date getExecuteDate() {
		return executeDate;
	}

	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}

	/**
	 * javadoc requestDate
	 */
	private Date requestDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="request_date")
	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	

	/**
	 * javadoc Report
	 */
	private Report report;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="report")
	@ForeignKey(name="FK_Proc_Report")
	@Index(name="IX_Proc_Report")
	public Report getReport() {
	    return report;
	}

	public void setReport(Report report) {
	    this.report = report;
	}
	
	
	

	/**
	 *  placerOrderNumber used by integrations
	 *  (like ORC segment in HL7v2)
	 *  Used in PHI_KLINIK to store "numero di ordinamento anatomico".
	 */
	private String placerOrderNumber;

	@Column(name="placer_order_num")
	@Index(name="IX_Placer_Order_Num")
	public String getPlacerOrderNumber(){
		return placerOrderNumber;
	}

	public void setPlacerOrderNumber(String placerOrderNumber){
		this.placerOrderNumber = placerOrderNumber;
	}
	
	/**
	 *  placerOrderNumberRoot used by integrations
	 *  to specify the source of the order
	 *  
	 */
	private String placerOrderNumberRoot;

	@Column(name="placer_order_num_root")
	@Index(name="IX_Placer_Order_Num_R")
	public String getPlacerOrderNumberRoot(){
		return placerOrderNumberRoot;
	}

	public void setPlacerOrderNumberRoot(String placerOrderNumberRoot){
		this.placerOrderNumberRoot = placerOrderNumberRoot;
	}
	
	/**
	*  javadoc for billed
	*  per tenere traccia delle procedure fatturate
	*/
	private Boolean billed;

	@Column(name="billed")
	public Boolean getBilled(){
		return billed;
	}

	public void setBilled(Boolean billed){
		this.billed = billed;
	}

	/**
	*  english translation of Text property. Used by final user
	*/
	private String textEn;

	@Column(name="text_en")
	public String getTextEn(){
		return textEn;
	}

	public void setTextEn(String textEn){
		this.textEn = textEn;
	}

	/**
	*  german translation of Text property. Used by final user
	*/
	private String textDe;

	@Column(name="text_de")
	public String getTextDe(){
		return textDe;
	}

	public void setTextDe(String textDe){
		this.textDe = textDe;
	}
	/**
	*  german translation of Text property. Used by final user
	*/
	private String diagnostics;

	@Column(name="diagnostics")
	public String getDiagnostics(){
		return diagnostics;
	}

	public void setDiagnostics(String diagnostics){
		this.diagnostics = diagnostics;
	}

	/**
	 * Following 2 properties are used in dentistry report
	 */
	private boolean current = false;

	@Transient
	public boolean getCurrent(){
		return current;
	}

	public void setCurrent(boolean current){
		this.current = current;
	}
}