package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.phi.entities.act.Procedure;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.json.JsonProxyGenerator;


@javax.persistence.Entity
@Table(name = "procedure_request")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=ProcedureRequest.class)
public class ProcedureRequest extends BaseEntity {

	private static final long serialVersionUID = 2104489791L;
	
	/**
	*  javadoc for text
	*/
	private String text;

	@Column(name="text")
	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}

	/**
	*  javadoc for flag
	*/
	private Boolean flag;

	@Column(name="flag")
	public Boolean getFlag(){
		return flag;
	}

	public void setFlag(Boolean flag){
		this.flag = flag;
	}
	
	/**
	*  javadoc for appointmentGrouper
	*/
	private AppointmentGrouper appointmentGrouper;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="appointment_grouper_id")
	@ForeignKey(name="FK_PrcdurRqst_appintmntGrpr")
	@Index(name="IX_PrcdurRqst_appintmntGrpr")
	public AppointmentGrouper getAppointmentGrouper(){
		return appointmentGrouper;
	}

	public void setAppointmentGrouper(AppointmentGrouper appointmentGrouper){
		this.appointmentGrouper = appointmentGrouper;
	}
	
	/**
	 *  javadoc for ssnCode
	 */
	private CodeValuePhi ssnCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="ssnCode")
	@ForeignKey(name="FK_ProcedureRequest_ssnCode")
	@Index(name="IX_ProcedureRequest_ssnCode")
	public CodeValuePhi getSsnCode(){
		return ssnCode;
	}

	public void setSsnCode(CodeValuePhi ssnCode){
		this.ssnCode = ssnCode;
	}

	/**
	*  javadoc for requestNumber
	*/
	private String requestNumber;

	@Column(name="request_number")
	public String getRequestNumber(){
		return requestNumber;
	}

	public void setRequestNumber(String requestNumber){
		this.requestNumber = requestNumber;
	}

	/**
	 *  javadoc for requestType
	 */
	private CodeValuePhi requestType;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="request_type")
	@ForeignKey(name="FK_ProcedureRequest_requestTyp")
	@Index(name="IX_ProcedureRequest_requestTyp")
	public CodeValuePhi getRequestType(){
		return requestType;
	}

	public void setRequestType(CodeValuePhi requestType){
		this.requestType = requestType;
	}

	/**
	 *  javadoc for author
	 */
	private Employee author;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_ProcedureRequest_author")
	@Index(name="IX_ProcedureRequest_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}


	/**
	 *  javadoc for appointment
	 */
	private Appointment appointment;
	
	@JsonBackReference
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="appointment_id")
	@ForeignKey(name="FK_PrcedureRequst_appintmnt")
	@Index(name="IX_PrcedureRequst_appintmnt")
	public Appointment getAppointment(){
		return appointment;
	}

	public void setAppointment(Appointment appointment){
		this.appointment = appointment;
	}


	/**
	 *  javadoc for procedure
	 */
	private List<Procedure> procedure;
	
	@JsonManagedReference(value="procedure")
	@OneToMany(mappedBy="procedureRequest", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//	@JoinColumn(name="ProcReq_id")
//	@ForeignKey(name="FK_proc_ProcReq")
//	@Index(name="IX_prcedure_PrcReq")
	public List<Procedure> getProcedure() {
		return procedure;
	}

	public void setProcedure(List<Procedure>list){
		procedure = list;
	}
	
	public void addProcedure(Procedure procedure) {
		if (this.procedure == null) {
			this.procedure = new ArrayList<Procedure>();
		}
		// add the association
		if(!this.procedure.contains(procedure)) {
			this.procedure.add(procedure);
			// make the inverse link
		}
		procedure.setProcedureRequest(this);
	}

	public void removeProcedure(Procedure procedure) {
		if (this.procedure == null) {
			this.procedure = new ArrayList<Procedure>();
			return;
		}
		//add the association
		if(this.procedure.contains(procedure)){
			this.procedure.remove(procedure);
		}
		// remove the inverse link
		procedure.setProcedureRequest(null);
	}


	/**
	 *  javadoc for priority
	 */
	private CodeValuePhi priority;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="priority")
	@ForeignKey(name="FK_ProcedureRequest_priority")
	@Index(name="IX_ProcedureRequest_priority")
	public CodeValuePhi getPriority(){
		return priority;
	}

	public void setPriority(CodeValuePhi priority){
		this.priority = priority;
	}
	
	/**
	 *  javadoc for priority
	 */
	private CodeValuePhi urgency;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="urgency")
	@ForeignKey(name="FK_ProcedureRequest_urgency")
	@Index(name="IX_ProcedureRequest_urgency")
	public CodeValuePhi getUrgency(){
		return urgency;
	}

	public void setUrgency(CodeValuePhi urgency){
		this.urgency = urgency;
	}
	/**
	 *  javadoc for codeExemption
	 */
	private CodeValuePhi codeExemption;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="codeExemption")
	@ForeignKey(name="FK_ProcReq_codeExemp")
	@Index(name="IX_ProcReq_codeExemp")
	public CodeValuePhi getCodeExemption(){
		return codeExemption;
	}

	public void setCodeExemption(CodeValuePhi codeExemption){
		this.codeExemption = codeExemption;
	}

	/**
	 *  javadoc for exemption
	 */
	private CodeValuePhi exemption;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="exemption")
	@ForeignKey(name="FK_ProcedureRequest_exemption")
	@Index(name="IX_ProcedureRequest_exemption")
	public CodeValuePhi getExemption(){
		return exemption;
	}

	public void setExemption(CodeValuePhi exemption){
		this.exemption = exemption;
	}



	//methods needed for BaseEntity extension

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_ProcedureRequest_sdloc")
	@Index(name = "IX_ProcedureRequest_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProcedureRequest_sequence")
	@SequenceGenerator(name = "ProcedureRequest_sequence", sequenceName = "ProcedureRequest_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
