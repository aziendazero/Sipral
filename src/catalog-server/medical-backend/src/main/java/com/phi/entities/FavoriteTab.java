package com.phi.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OrderBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name="favorite_tab")
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=FavoriteTab.class)
public class FavoriteTab extends BaseEntity implements LocatedEntity 
{
	
	private static final long serialVersionUID = 7134011749947706083L;
		
	protected ServiceDeliveryLocation serviceDeliveryLocation;
		
	private String title;
	private Integer sortOrder;
	private Integer numColumn;		
	private CodeValuePhi typeCode;
	private CodeValuePhi subTypeCode;
	private List<FavoriteSection> section;
	
	
	@Id	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "FavoriteTab_sequence")
	@SequenceGenerator(name = "FavoriteTab_sequence", sequenceName = "FavoriteTab_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	

	@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_FavSect_sdloc")
	@Index(name="IX_FavSect_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
		
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@Column(name="sort_order")
	public Integer getSortOrder() {
		return sortOrder;
	}
	
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	
	@Column(name="num_column")
	public Integer getNumColumn() {
		return numColumn;
	}
	
	public void setNumColumn(Integer numColumn)	{
		this.numColumn = numColumn;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="typeCode")
	@ForeignKey(name="FK_FavTab_TypeCode")
	@Index(name="IX_FavTab_TypeCode")	
	public CodeValuePhi getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(CodeValuePhi typeCode) {
		this.typeCode = typeCode;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeCode")
	@ForeignKey(name="FK_FavTab_SubTypeCode")
	@Index(name="IX_FavTab_SubTypeCode")	
	public CodeValuePhi getSubTypeCode() {
		return subTypeCode;
	}

	public void setSubTypeCode(CodeValuePhi subTypeCode) {
		this.subTypeCode = subTypeCode;
	}
		
	 
	@OneToMany(fetch=FetchType.LAZY, mappedBy="tab", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	@OrderBy(clause="sort_order")
	public List<FavoriteSection> getSection() {
		return section;
	}
	
	public void setSection(List<FavoriteSection> section) {
		this.section = section;
	}
	
	public void addSection(FavoriteSection section) {
		if (this.section == null) {
			this.section = new ArrayList<FavoriteSection>();
		}
		// add the association
		if(!this.section.contains(section)) {
			this.section.add(section);
			// make the inverse link
			section.setTab(this);
		}
	}

	public void removeSection(FavoriteSection section) {
		if (this.section == null) {
			this.section = new ArrayList<FavoriteSection>();
			return;
		}
		// remove the association
		if(this.section.contains(section)){
			this.section.remove(section);
			// remove the inverse link
			section.setTab(null);
		}
	}	
}