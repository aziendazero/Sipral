package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
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

import com.phi.entities.locatedEntity.LocatedEntity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import com.phi.entities.role.ServiceDeliveryLocation;

import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.Pianificazione;


import java.util.ArrayList;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.ProgettoAssociato;
@javax.persistence.Entity
@Table(name = "progetto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Progetto extends BaseEntity implements LocatedEntity, Serializable {

	private static final long serialVersionUID = 569966058L;


	/**
	*  javadoc for progettoAssociato
	*/
	private List<ProgettoAssociato> progettoAssociato;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="progetto", cascade=CascadeType.PERSIST)
	public List<ProgettoAssociato> getProgettoAssociato() {
		return progettoAssociato;
	}

	public void setProgettoAssociato(List<ProgettoAssociato>list){
		progettoAssociato = list;
	}

	public void addProgettoAssociato(ProgettoAssociato progettoAssociato) {
		if (this.progettoAssociato == null) {
			this.progettoAssociato = new ArrayList<ProgettoAssociato>();
		}
		// add the association
		if(!this.progettoAssociato.contains(progettoAssociato)) {
			this.progettoAssociato.add(progettoAssociato);
			// make the inverse link
			progettoAssociato.setProgetto(this);
		}
	}

	public void removeProgettoAssociato(ProgettoAssociato progettoAssociato) {
		if (this.progettoAssociato == null) {
			this.progettoAssociato = new ArrayList<ProgettoAssociato>();
			return;
		}
		//add the association
		if(this.progettoAssociato.contains(progettoAssociato)){
			this.progettoAssociato.remove(progettoAssociato);
			//make the inverse link
			progettoAssociato.setProgetto(null);
		}
	}


	/**
	*  javadoc for isSelected
	*/
	private Boolean isSelected;

	@Column(name="is_selected")
	public Boolean getIsSelected(){
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected){
		this.isSelected = isSelected;
	}


//	/**
//	*  javadoc for pianificazione
//	*/
//	private Pianificazione pianificazione;
//
//	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
//	@JoinColumn(name="pianificazione_id")
//	@ForeignKey(name="FK_Progetto_pianificazione")
//	@Index(name="IX_Progetto_pianificazione")
//	public Pianificazione getPianificazione(){
//		return pianificazione;
//	}
//
//	public void setPianificazione(Pianificazione pianificazione){
//		this.pianificazione = pianificazione;
//	}



	/**
	*  javadoc for praticheAttese
	*
	private Integer praticheAttese;

	@Column(name="pratiche_attese")
	public Integer getPraticheAttese(){
		return praticheAttese;
	}

	public void setPraticheAttese(Integer praticheAttese){
		this.praticheAttese = praticheAttese;
	}*/

//	/**
//	*  javadoc for subVigilanza
//	*/
//	private List<CodeValuePhi> subVigilanza;
//
//	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
//	@JoinTable(name="Progetto_subVigilanza", joinColumns = { @JoinColumn(name="Progetto_id") }, inverseJoinColumns = { @JoinColumn(name="subVigilanza") })
//	@ForeignKey(name="FK_subVigilanza_Progetto", inverseName="FK_Progetto_subVigilanza")
//	@IndexColumn(name="list_index")
//	public List<CodeValuePhi> getSubVigilanza(){
//		return subVigilanza;
//	}
//
//	public void setSubVigilanza(List<CodeValuePhi> subVigilanza){
//		this.subVigilanza = subVigilanza;
//	}
//
//	/**
//	*  javadoc for subMdl
//	*/
//	private List<CodeValuePhi> subMdl;
//
//	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
//	@JoinTable(name="Progetto_subMdl", joinColumns = { @JoinColumn(name="Progetto_id") }, inverseJoinColumns = { @JoinColumn(name="subMdl") })
//	@ForeignKey(name="FK_subMdl_Progetto", inverseName="FK_Progetto_subMdl")
//	@IndexColumn(name="list_index")
//	public List<CodeValuePhi> getSubMdl(){
//		return subMdl;
//	}
//
//	public void setSubMdl(List<CodeValuePhi> subMdl){
//		this.subMdl = subMdl;
//	}
//
//	/**
//	*  javadoc for lineeDiLavoro
//	*/
//	private List<CodeValuePhi> lineeDiLavoro;
//
//	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
//	@JoinTable(name="Progetto_lineeDiLavoro", joinColumns = { @JoinColumn(name="Progetto_id") }, inverseJoinColumns = { @JoinColumn(name="lineeDiLavoro") })
//	@ForeignKey(name="FK_lineeDiLavoro_Progetto", inverseName="FK_Progetto_lineeDiLavoro")
//	@IndexColumn(name="list_index")
//	public List<CodeValuePhi> getLineeDiLavoro(){
//		return lineeDiLavoro;
//	}
//
//	public void setLineeDiLavoro(List<CodeValuePhi> lineeDiLavoro){
//		this.lineeDiLavoro = lineeDiLavoro;
//	}

	/**
	*  javadoc for dataAl
	*/
	private Date dataAl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_al")
	public Date getDataAl(){
		return dataAl;
	}

	public void setDataAl(Date dataAl){
		this.dataAl = dataAl;
	}

	/**
	*  javadoc for dataDal
	*/
	private Date dataDal;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_dal")
	public Date getDataDal(){
		return dataDal;
	}

	public void setDataDal(Date dataDal){
		this.dataDal = dataDal;
	}

	/**
	*  javadoc for priorita
	*
	private Integer priorita;

	@Column(name="priorita")
	public Integer getPriorita(){
		return priorita;
	}

	public void setPriorita(Integer priorita){
		this.priorita = priorita;
	}*/

	/**
	*  javadoc for nome
	*/
	private String nome;

	@Column(name="nome")
	public String getNome(){
		return nome;
	}

	public void setNome(String nome){
		this.nome = nome;
	}


	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_Prgett_srvicDlivryLcatin")
	@Index(name="IX_Prgett_srvicDlivryLcatin")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Progetto_sequence")
	@SequenceGenerator(name = "Progetto_sequence", sequenceName = "Progetto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
