package com.phi.entities.baseEntity;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.EN;


/**
 * Entity implementation class for Entity: Substance
 *
 */
@javax.persistence.Entity
@Table(name = "substance")
@Audited
public class Substance extends BaseEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6936421586397234215L;
	

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "substance_sequence")
	@SequenceGenerator(name = "substance_sequence", sequenceName = "substance_sequence")
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
	@ForeignKey(name = "FK_substance_sdloc")
	@Index(name="IX_substance_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	private List<Medicine> medicine;
	private String externalId;
	
	
	@ManyToMany(mappedBy = "substance", cascade=CascadeType.PERSIST)
	public List<Medicine> getMedicine() {
	    return medicine;
	}
	public void setMedicine(List<Medicine> param) {
	    this.medicine = param;
	}

	@Column(name="external_id")
	@Index(name="IX_substance_ext_id")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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
		return  name;
	}

	public void setName(EN name) {
		this.name = name;
	}
}
