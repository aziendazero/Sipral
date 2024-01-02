package com.phi.entities.role;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.actions.OperatoreAction;
import com.phi.entities.actions.ServiceDeliveryLocationAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.entity.Organization;
import com.phi.entities.locatedEntity.LocatedEntity;



import java.util.ArrayList;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.VigilanzaRdpType;

import com.phi.entities.baseEntity.MonteOre;

@javax.persistence.Entity
@Table(name = "operatore")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Operatore extends BaseEntity implements LocatedEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5390630242341967476L;


	/**
	*  javadoc for monteOre
	*/
	private List<MonteOre> monteOre;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="operatore", cascade=CascadeType.PERSIST)
	public List<MonteOre> getMonteOre(){
		return monteOre;
	}

	public void setMonteOre(List<MonteOre> list){
		monteOre = list;
	}

	public void addMonteOre(MonteOre monteOre) {
		if (this.monteOre == null) {
			this.monteOre = new ArrayList<MonteOre>();
		}
		// add the association
		if(!this.monteOre.contains(monteOre)) {
			this.monteOre.add(monteOre);
			// make the inverse link
			monteOre.setOperatore(this);
		}
	}

	public void removeMonteOre(MonteOre monteOre) {
		if (this.monteOre == null) {
			this.monteOre = new ArrayList<MonteOre>();
			return;
		}
		//add the association
		if(this.monteOre.contains(monteOre)){
			this.monteOre.remove(monteOre);
			//make the inverse link
			monteOre.setOperatore(null);
		}

	}



	/**
	*  javadoc for monteOre
	*/
	/*private List<MonteOre> monteOre;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Operatore_id")
	@ForeignKey(name="FK_monteOre_Operatore")
	@Index(name="IX_monteOre_Operatore")
	public List<MonteOre> getMonteOre() {
		return monteOre;
	}

	public void setMonteOre(List<MonteOre>list){
		monteOre = list;
	}*/



	/**
	*  javadoc for vigilanzaRdpType
	*/
	private List<VigilanzaRdpType> vigilanzaRdpType;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="operatore", cascade=CascadeType.PERSIST)
	public List<VigilanzaRdpType> getVigilanzaRdpType(){
		return vigilanzaRdpType;
	}

	public void setVigilanzaRdpType(List<VigilanzaRdpType> list){
		vigilanzaRdpType = list;
	}

	public void addVigilanzaRdpType(VigilanzaRdpType vigilanzaRdpType) {
		if (this.vigilanzaRdpType == null) {
			this.vigilanzaRdpType = new ArrayList<VigilanzaRdpType>();
		}
		// add the association
		if(!this.vigilanzaRdpType.contains(vigilanzaRdpType)) {
			this.vigilanzaRdpType.add(vigilanzaRdpType);
			// make the inverse link
			vigilanzaRdpType.setOperatore(this);
		}
	}

	public void removeVigilanzaRdpType(VigilanzaRdpType vigilanzaRdpType) {
		if (this.vigilanzaRdpType == null) {
			this.vigilanzaRdpType = new ArrayList<VigilanzaRdpType>();
			return;
		}
		//add the association
		if(this.vigilanzaRdpType.contains(vigilanzaRdpType)){
			this.vigilanzaRdpType.remove(vigilanzaRdpType);
			//make the inverse link
			vigilanzaRdpType.setOperatore(null);
		}

	}



	


	

	/**
	*  javadoc for rdpOf
	*/
	private List<ServiceDeliveryLocation> rdpOf;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Operatore_id")
	@ForeignKey(name="FK_rdpOf_Operatore")
	//@Index(name="IX_rdpOf_Operatore")
	public List<ServiceDeliveryLocation> getRdpOf() {
		return rdpOf;
	}

	public void setRdpOf(List<ServiceDeliveryLocation>list){
		rdpOf = list;
	}

	/**
	*  javadoc for directorOf
	*/
	private List<ServiceDeliveryLocation> directorOf;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Operatore_id")
	@ForeignKey(name="FK_directorOf_Operatore")
	//@Index(name="IX_directorOf_Operatore")
	public List<ServiceDeliveryLocation> getDirectorOf() {
		return directorOf;
	}

	public void setDirectorOf(List<ServiceDeliveryLocation>list){
		directorOf = list;
	}

	/**
	*  javadoc for rdp
	*/
	/*private ServiceDeliveryLocation rdp;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="rdp_id")
	@ForeignKey(name="FK_Operatore_rdp")
	//@Index(name="IX_Operatore_rdp")
	public ServiceDeliveryLocation getRdp(){
		return rdp;
	}

	public void setRdp(ServiceDeliveryLocation rdp){
		this.rdp = rdp;
	}*/

	/**
	*  javadoc for director
	*/
	/*private ServiceDeliveryLocation director;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="director_id")
	@ForeignKey(name="FK_Operatore_director")
	//@Index(name="IX_Operatore_director")
	public ServiceDeliveryLocation getDirector(){
		return director;
	}

	public void setDirector(ServiceDeliveryLocation director){
		this.director = director;
	}*/



	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_prtor_srvicDlivryLoction")
	//@Index(name="IX_prtor_srvicDlivryLoction")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}



	/**
	*  javadoc for employee
	*/
	private Employee employee;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_Operatore_employee")
	//@Index(name="IX_Operatore_employee")
	public Employee getEmployee(){
		return employee;
	}

	public void setEmployee(Employee employee){
		this.employee = employee;
	}

	private EN name;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="fam", column=@Column(name="name_fam")),
	       @AttributeOverride(name="giv", column=@Column(name="name_giv")),
	       @AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
	       @AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
	       @AttributeOverride(name="del", column=@Column(name="name_del")),
	       @AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
	})
	public EN getName() {
		if ((this.employee != null)&&(this.employee.getName() != null))
			return this.employee.getName();
		
		return name;
	}

	public void setName(EN name) {
		this.name = name;
	}
	
	//methods needed for Nomina
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Operatore_code")
	//@Index(name="IX_Operatore_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Operatore_sequence")
	@SequenceGenerator(name = "Operatore_sequence", sequenceName = "Operatore_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue ente;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="ente")
	@ForeignKey(name="FK_Operatore_ente")
	//@Index(name="IX_Operatore_ente")
	public CodeValue getEnte() {
		return ente;
	}

	public void setEnte(CodeValue ente) {
		this.ente = ente;
	}

	private Organization organization;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
	@ForeignKey(name="FK_Operat_Org")
	//@Index(name="IX_Operat_Org")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	/**
	*  javadoc for isNew
	*/
	private Boolean isNew;

	@Column(name="is_new")
	public Boolean getIsNew(){
		return isNew;
	}

	public void setIsNew(Boolean isNew){
		this.isNew = isNew;
	}
	
	@Transient
	public String getDirectorForTemplate(){
		
		String result = "";

		OperatoreAction oa = OperatoreAction.instance();
		result = oa.getDirector(ServiceDeliveryLocationAction.instance().getEntity());
		
		return result;
		
	}
	
}
