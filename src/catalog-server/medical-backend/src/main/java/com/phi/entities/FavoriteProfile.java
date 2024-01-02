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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.act.LEPActivity;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Prescription;
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name="favorite_profile")
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=FavoriteProfile.class)
public class FavoriteProfile extends BaseEntity
{	
	
	private static final long serialVersionUID = -1251859452735708361L;
	
	private String title;
	private Integer sortOrder;
	private FavoriteSection section;
	private List<Prescription> prescription;
	private List<LEPActivity> activity;
	
		
	@Id	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "FavoriteSection_sequence")
	@SequenceGenerator(name = "FavoriteSection_sequence", sequenceName = "FavoriteSection_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
		
		
	@Column(name="title")
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
		
	
	@Column(name="sort_order")
	public Integer getSortOrder()
	{
		return sortOrder;
	}
	
	public void setSortOrder(Integer sortOrder)
	{
		this.sortOrder = sortOrder;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="section")
	@ForeignKey(name="FK_FavProf_FavSect")
	@Index(name="IX_FavProf_FavSect")
	public FavoriteSection getSection() 
	{
		return section;
	}

	public void setSection(FavoriteSection section) 
	{
			this.section = section;
	}
	
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="profile", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@Fetch(FetchMode.SELECT)
	public List<Prescription> getPrescription() 
	{
	    return prescription;
	}

	public void setPrescription(List<Prescription> prescription) 
	{
	    this.prescription = prescription;
	}
	
	public void addPrescription(Prescription prescription) {
		if (this.prescription == null) {
			this.prescription = new ArrayList<Prescription>();
		}
		// add the association
		if(!this.prescription.contains(prescription)) {
			this.prescription.add(prescription);
			// make the inverse link
			prescription.setProfile(this);
		}
	}

	public void removePrescription(Prescription prescription) {
		if (this.prescription == null) {
			this.prescription = new ArrayList<Prescription>();
			return;
		}
		// remove the association
		if(this.prescription.contains(prescription)){
			this.prescription.remove(prescription);
			// remove the inverse link
			prescription.setProfile(null);
		}
	}

	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="profile", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})	
	@Fetch(FetchMode.SELECT)
	public List<LEPActivity> getActivity() 
	{
	    return activity;
	}

	public void setActivity(List<LEPActivity> activity) 
	{
	    this.activity = activity;
	}
	
	public void addActivity(LEPActivity activity) {
		if (this.activity == null) {
			this.activity = new ArrayList<LEPActivity>();
		}
		// add the association
		if(!this.activity.contains(activity)) {
			this.activity.add(activity);
			// make the inverse link
			activity.setProfile(this);
		}
	}

	public void removeActivity(LEPActivity activity) {
		if (this.activity == null) {
			this.activity = new ArrayList<LEPActivity>();
			return;
		}
		// remove the association
		if(this.activity.contains(activity)){
			this.activity.remove(activity);
			// remove the inverse link
			activity.setProfile(null);
		}
	}	
}