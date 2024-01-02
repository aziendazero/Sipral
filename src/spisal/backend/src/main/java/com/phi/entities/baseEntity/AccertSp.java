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
import javax.persistence.UniqueConstraint;
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
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

import com.phi.entities.baseEntity.AppointmentSp;

import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "accert_sp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AccertSp extends BaseEntity {

	private static final long serialVersionUID = 427712679L;

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
	*  javadoc for riferimento
	*/
	private CodeValuePhi riferimento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento")
	@ForeignKey(name="FK_AccertSp_riferimento")
	//@Index(name="IX_AccertSp_riferimento")
	public CodeValuePhi getRiferimento(){
		return riferimento;
	}

	public void setRiferimento(CodeValuePhi riferimento){
		this.riferimento = riferimento;
	}


	/**
	*  javadoc for riferimentoInterno
	*/
	private Employee riferimentoInterno;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="riferimento_interno_id")
	@ForeignKey(name="FK_ccertSp_riferimntoIntrno")
	//@Index(name="IX_ccertSp_riferimntoIntrno")
	public Employee getRiferimentoInterno(){
		return riferimentoInterno;
	}

	public void setRiferimentoInterno(Employee riferimentoInterno){
		this.riferimentoInterno = riferimentoInterno;
	}



	/**
	*  javadoc for appointmentSp
	*/
	private List<AppointmentSp> appointmentSp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="accertSp", cascade=CascadeType.PERSIST)
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
			appointmentSp.setAccertSp(this);
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
			appointmentSp.setAccertSp(null);
		}

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
	@ForeignKey(name="FK_AccertSp_pagamento")
	//@Index(name="IX_AccertSp_pagamento")
	public CodeValuePhi getPagamento(){
		return pagamento;
	}

	public void setPagamento(CodeValuePhi pagamento){
		this.pagamento = pagamento;
	}


	/**
	*  javadoc for accertaMdl
	*/
	private List<AccertaMdl> accertaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="accertSp", cascade=CascadeType.PERSIST)
	public List<AccertaMdl> getAccertaMdl(){
		return accertaMdl;
	}

	public void setAccertaMdl(List<AccertaMdl> list){
		accertaMdl = list;
	}

	public void addAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
		}
		// add the association
		if(!this.accertaMdl.contains(accertaMdl)) {
			this.accertaMdl.add(accertaMdl);
			// make the inverse link
			accertaMdl.setAccertSp(this);
		}
	}

	public void removeAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
			return;
		}
		//add the association
		if(this.accertaMdl.contains(accertaMdl)){
			this.accertaMdl.remove(accertaMdl);
			//make the inverse link
			accertaMdl.setAccertSp(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AccertSp_sequence")
	@SequenceGenerator(name = "AccertSp_sequence", sequenceName = "AccertSp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
