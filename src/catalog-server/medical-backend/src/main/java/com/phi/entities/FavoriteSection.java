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
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name="favorite_section")
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=FavoriteSection.class)
public class FavoriteSection extends BaseEntity
{

	private static final long serialVersionUID = 205516838535401913L;
		
	private String title;
	private Integer sortOrder;
	private Integer columnIndex;
	private String color;
	private FavoriteTab tab;
	private List<FavoriteProfile> profile;
	

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
	
	
	@Column(name="column_index")
	public Integer getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	
	@Column(name="color")
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="tab")
	@ForeignKey(name="FK_FavSect_FavTab")
	@Index(name="IX_FavSect_FavTab")
	public FavoriteTab getTab() {
		return tab;
	}

	public void setTab(FavoriteTab tab) {
		this.tab = tab;
	}
	
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="section", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	@OrderBy(clause="sort_order")
	public List<FavoriteProfile> getProfile() {
		return profile;
	}
	
	public void setProfile(List<FavoriteProfile> profile) {
		this.profile = profile;
	}
	
	public void addProfile(FavoriteProfile profile) {
		if (this.profile == null) {
			this.profile = new ArrayList<FavoriteProfile>();
		}
		// add the association
		if(!this.profile.contains(profile)) {
			this.profile.add(profile);
			// make the inverse link
			profile.setSection(this);
		}
	}

	public void removeProfile(FavoriteProfile profile) {
		if (this.profile == null) {
			this.profile = new ArrayList<FavoriteProfile>();
			return;
		}
		// remove the association
		if(this.profile.contains(profile)){
			this.profile.remove(profile);
			// remove the inverse link
			profile.setSection(null);
		}
	}	
}