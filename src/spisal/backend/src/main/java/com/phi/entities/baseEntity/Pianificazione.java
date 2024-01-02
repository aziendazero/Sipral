package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.phi.entities.role.Employee;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.Progetto;
import com.phi.entities.baseEntity.Partecipanti;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.ProgettoAssociato;
@javax.persistence.Entity
@Table(name = "pianificazione")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Pianificazione extends BaseEntity implements LocatedEntity, Serializable {

	private static final long serialVersionUID = 735652032L;


	/**
	*  javadoc for progettiAssociati
	*/
	private List<ProgettoAssociato> progettiAssociati;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="pianificazione", cascade=CascadeType.PERSIST)
	public List<ProgettoAssociato> getProgettiAssociati(){
		return progettiAssociati;
	}

	public void setProgettiAssociati(List<ProgettoAssociato> list){
		progettiAssociati = list;
	}

	public void addProgettiAssociati(ProgettoAssociato progettoAssociato) {
		if (this.progettiAssociati == null) {
			this.progettiAssociati = new ArrayList<ProgettoAssociato>();
		}
		// add the association
		if(!this.progettiAssociati.contains(progettoAssociato)) {
			this.progettiAssociati.add(progettoAssociato);
			// make the inverse link
			progettoAssociato.setPianificazione(this);
		}
	}

	public void removeProgettiAssociati(ProgettoAssociato progettoAssociato) {
		if (this.progettiAssociati == null) {
			this.progettiAssociati = new ArrayList<ProgettoAssociato>();
			return;
		}
		//add the association
		if(this.progettiAssociati.contains(progettoAssociato)){
			this.progettiAssociati.remove(progettoAssociato);
			//make the inverse link
			progettoAssociato.setPianificazione(null);
		}

	}


//
//	/**
//	*  javadoc for partecipanti
//	*/
//	private List<Partecipanti> partecipanti;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="pianificazione", cascade=CascadeType.PERSIST)
//	public List<Partecipanti> getPartecipanti() {
//		return partecipanti;
//	}
//
//	public void setPartecipanti(List<Partecipanti>list){
//		partecipanti = list;
//	}
//
//	public void addPartecipanti(Partecipanti partecipanti) {
//		if (this.partecipanti == null) {
//			this.partecipanti = new ArrayList<Partecipanti>();
//		}
//		// add the association
//		if(!this.partecipanti.contains(partecipanti)) {
//			this.partecipanti.add(partecipanti);
//			// make the inverse link
//			partecipanti.setPianificazione(this);
//		}
//	}
//
//	public void removePartecipanti(Partecipanti partecipanti) {
//		if (this.partecipanti == null) {
//			this.partecipanti = new ArrayList<Partecipanti>();
//			return;
//		}
//		//add the association
//		if(this.partecipanti.contains(partecipanti)){
//			this.partecipanti.remove(partecipanti);
//			//make the inverse link
//			partecipanti.setPianificazione(null);
//		}
//	}
//
//

//	/**
//	*  javadoc for progetti
//	*/
//	private List<Progetto> progetti;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="pianificazione", cascade=CascadeType.PERSIST)
//	public List<Progetto> getProgetti() {
//		return progetti;
//	}
//
//	public void setProgetti(List<Progetto>list){
//		progetti = list;
//	}
//
//	public void addProgetti(Progetto progetti) {
//		if (this.progetti == null) {
//			this.progetti = new ArrayList<Progetto>();
//		}
//		// add the association
//		if(!this.progetti.contains(progetti)) {
//			this.progetti.add(progetti);
//			// make the inverse link
//			progetti.setPianificazione(this);
//		}
//	}
//
//	public void removeProgetti(Progetto progetti) {
//		if (this.progetti == null) {
//			this.progetti = new ArrayList<Progetto>();
//			return;
//		}
//		//add the association
//		if(this.progetti.contains(progetti)){
//			this.progetti.remove(progetti);
//			//make the inverse link
//			progetti.setPianificazione(null);
//		}
//	}



	/**
	*  javadoc for modificatoDa
	*/
	private Employee modificatoDa;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="modificato_da_id")
	@ForeignKey(name="FK_Pianificazone_modfcatoDa")
	@Index(name="IX_Pianificazone_modfcatoDa")
	public Employee getModificatoDa(){
		return modificatoDa;
	}

	public void setModificatoDa(Employee modificatoDa){
		this.modificatoDa = modificatoDa;
	}



	/**
	*  javadoc for creatoDa
	*/
	private Employee creatoDa;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="creato_da_id")
	@ForeignKey(name="FK_Pianificazione_creatoDa")
	@Index(name="IX_Pianificazione_creatoDa")
	public Employee getCreatoDa(){
		return creatoDa;
	}

	public void setCreatoDa(Employee creatoDa){
		this.creatoDa = creatoDa;
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
	*  javadoc for dataCreazione
	*/
	private Date dataCreazione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_creazione")
	public Date getDataCreazione(){
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione){
		this.dataCreazione = dataCreazione;
	}

	/**
	*  javadoc for anno
	*/
	private Integer anno;

	@Column(name="anno")
	public Integer getAnno(){
		return anno;
	}

	public void setAnno(Integer anno){
		this.anno = anno;
	}

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
	@ForeignKey(name="FK_Pnfczne_servceDelvryLctn")
	@Index(name="IX_Pnfczne_servceDelvryLctn")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Pianificazione_sequence")
	@SequenceGenerator(name = "Pianificazione_sequence", sequenceName = "Pianificazione_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
