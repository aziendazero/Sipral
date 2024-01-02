package com.phi.entities.baseEntity;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.EN;


/**
 * Entity implementation class for Entity: Manufacturer
 *
 */
@javax.persistence.Entity
@Table(name = "manufacturer")
@Audited


public class Manufacturer extends BaseEntity  {
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "manufacturer_sequence")
	@SequenceGenerator(name = "manufacturer_sequence", sequenceName = "manufacturer_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_manufacturer_sdloc")
	@Index(name="IX_manufacturer_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3828727461538157876L;
	private List<Medicine> medicine;
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "manufacturer")
	public List<Medicine> getMedicine() {
	    return medicine;
	}
	public void setMedicine(List<Medicine> param) {
	    this.medicine = param;
	}

	
	private AD addr;

	@Embedded
	public AD getAddr() {
		return addr;
	}

	public void setAddr(AD addr) {
		this.addr = addr;
	}
	
	private String vat;

	@Column(name="vat")
	public String getVat(){
		return vat;
	}

	public void setVat(String vat){
		this.vat = vat;
	}
	
	private EN name;
	private String externalId;

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
		return  name;
	}

	public void setName(EN name) {
		this.name = name;
	}
	
	@Column(name="external_id")
	@Index(name="IX_manufacturer_ext_id")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}
