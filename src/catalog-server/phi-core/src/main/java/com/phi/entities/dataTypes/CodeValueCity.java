package com.phi.entities.dataTypes;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.annotations.CodeValueEquivalent;
import com.phi.annotations.CodeValueExtension;

/**
 * CodeValue for "COMUNI" CodeSystem
 */

@Entity
@Table(name="code_value_city", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_city", indexes = { @Index(name="IX_Code_Value_city", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueCity extends CodeValue implements java.io.Serializable {

	private static final long serialVersionUID = 424448665900804336L;
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCity.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueCity_Parent")
	@Index(name="IX_CodeValueCity_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueCity.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * Code Extension Provincia
	 */
	private String province;

	@Column(name="province")
	@CodeValueExtension(name="Province")
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	
	/**
	 * Code Extension Zip
	 */
	private String zip;
	
	@Column(name="zip")
	@CodeValueExtension(name="Zip")
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	/**
	 * Code Extension Distretto sanitario
	 */
	private String healthDistrict;

	@Column(name="health_district")
	@CodeValueExtension(name="HealthDistrict")
	public String getHealthDistrict() {
		return healthDistrict;
	}

	public void setHealthDistrict(String healthDistrict) {
		this.healthDistrict = healthDistrict;
	}
	
	/**
	 * Code Extension Codice catastale
	 */
	private String landRegistry;

	@Column(name="land_registry")
	@CodeValueExtension(name="LandRegistry")
	public String getLandRegistry() {
		return landRegistry;
	}

	public void setLandRegistry(String landRegistry) {
		this.landRegistry = landRegistry;
	}
	
	/**
	 * Code Extension ISTAT
	 */
	private String istat;

	@Column(name="istat")
	@CodeValueExtension(name="Istat")
	public String getIstat() {
		return istat;
	}

	public void setIstat(String istat) {
		this.istat = istat;
	}
	
	/**
	 * Code Extension ISO
	 */
	private String iso;

	@Column(name="iso")
	@CodeValueExtension(name="Iso")
	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	/**
	 * Code Extension ULSS
	 */
	private String ulss;

	@Column(name="ulss")
	@CodeValueExtension(name="Ulss")
	public String getUlss() {
		return ulss;
	}

	public void setUlss(String ulss) {
		this.ulss = ulss;
	}

	/**
	 * RELATION WITH CITY TYPE
	 */
	private Collection<CodeValueCity> relationsCity = new HashSet<CodeValueCity>();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_city_city",
		joinColumns = {@JoinColumn(name="cv_city1", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="cv_city2", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_CITY1", inverseName="FK_CV_CITY2")
	@CodeValueEquivalent(targetType=CodeValueCity.class)
	public Collection<CodeValueCity> getRelationsCity() {
		return this.relationsCity;
	}

	public void setRelationsCity(Collection<CodeValueCity> relationsCity) {
		this.relationsCity = relationsCity;
	}

	/**
	 * INVERSE RELATION WITH CITY TYPE
	 */
	private Collection<CodeValueCity> inverseRelationsCity = new HashSet<CodeValueCity>();
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="relationsCity")
	@CodeValueEquivalent(targetType=CodeValueCity.class, isInverse=true)
	public Collection<CodeValueCity> getInverseRelationsCity() {
		return this.inverseRelationsCity;
	}

	public void setInverseRelationsCity(Collection<CodeValueCity> inverseRelationsCity) {
		this.inverseRelationsCity = inverseRelationsCity;
	}

	/**
	 * RELATION WITH PHI TYPE
	 */
	private Collection<CodeValuePhi> relationsPhi = new HashSet<CodeValuePhi>();
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="relationsCity")
	@CodeValueEquivalent(targetType=CodeValuePhi.class)
	public Collection<CodeValuePhi> getRelationsPhi() {
		return this.relationsPhi;
	}

	public void setRelationsPhi(Collection<CodeValuePhi> relationsPhi) {
		this.relationsPhi = relationsPhi;
	}

}