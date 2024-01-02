package com.phi.entities.baseEntity;

import java.util.Date;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
@javax.persistence.Entity
@Table(name = "patentini")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Patentini extends BaseEntity {

	private static final long serialVersionUID = 1183077508L;

	/**
	*  javadoc for patRinDate
	*/
	private Date patRinDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_rin_date")
	public Date getPatRinDate(){
		return patRinDate;
	}

	public void setPatRinDate(Date patRinDate){
		this.patRinDate = patRinDate;
	}

	/**
	*  javadoc for gas
	*/
	private List<CodeValuePhi> gas;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="patentini_gas", joinColumns = { @JoinColumn(name="Patentini_id") }, inverseJoinColumns = { @JoinColumn(name="gas") })
	@ForeignKey(name="FK_gas_Patentini", inverseName="FK_Patentini_gas")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getGas(){
		return gas;
	}

	public void setGas(List<CodeValuePhi> gas){
		this.gas = gas;
	}

	/**
	*  javadoc for punteggioEsame
	*/
	private String punteggioEsame;

	@Column(name="punteggio_esame")
	public String getPunteggioEsame(){
		return punteggioEsame;
	}

	public void setPunteggioEsame(String punteggioEsame){
		this.punteggioEsame = punteggioEsame;
	}

	/**
	*  javadoc for dataEsame
	*/
	private Date dataEsame;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_esame")
	public Date getDataEsame(){
		return dataEsame;
	}

	public void setDataEsame(Date dataEsame){
		this.dataEsame = dataEsame;
	}

	/**
	*  javadoc for patExpDate
	*/
	private Date patExpDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_exp_date")
	public Date getPatExpDate(){
		return patExpDate;
	}

	public void setPatExpDate(Date patExpDate){
		this.patExpDate = patExpDate;
	}

	/**
	*  javadoc for patRilDate
	*/
	private Date patRilDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_ril_date")
	public Date getPatRilDate(){
		return patRilDate;
	}

	public void setPatRilDate(Date patRilDate){
		this.patRilDate = patRilDate;
	}

	/**
	*  javadoc for matricola
	*/
	private String matricola;

	@Column(name="matricola")
	public String getMatricola(){
		return matricola;
	}

	public void setMatricola(String matricola){
		this.matricola = matricola;
	}


	/**
	*  javadoc for parereTecnico
	*/
	private ParereTecnico parereTecnico;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="parere_tecnico_id")
	@ForeignKey(name="FK_Patentini_parereTecnico")
	//@Index(name="IX_Patentini_parereTecnico")
	public ParereTecnico getParereTecnico(){
		return parereTecnico;
	}

	public void setParereTecnico(ParereTecnico parereTecnico){
		this.parereTecnico = parereTecnico;
	}



	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_Patentini_person")
	//@Index(name="IX_Patentini_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Patentini_sequence")
	@SequenceGenerator(name = "Patentini_sequence", sequenceName = "Patentini_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
