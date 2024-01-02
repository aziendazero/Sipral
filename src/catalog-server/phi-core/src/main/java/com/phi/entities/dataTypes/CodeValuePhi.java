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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.annotations.CodeValueEquivalent;
import com.phi.annotations.CodeValueExtension;

/**
 * CodeValue for "PHIDIC" CodeSystem
 */

@Entity(name="CodeValuePhi")
@Table(name="code_value", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value", indexes = { @Index(name="IX_Code_Value", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValuePhi extends CodeValue implements java.io.Serializable {

	private static final long serialVersionUID = 424448665900804336L;
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValuePhi_Parent")
	@Index(name="IX_CodeValuePhi_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

	/**
	 * Code Extension score
	 */
	private Integer score;

	@Column(name="score")
	@CodeValueExtension(name="Score")
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	/**
	 * Code Extension abbreviation
	 */
	private String abbreviation;

	@Column(name="abbreviation")
	@CodeValueExtension(name="Abbreviation")
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Code Extension enableAnnotation.
	 * Sometimes is needed that when a codevalue is selected an extra input text is shown at runtime to add note.
	 * E.g.: a groupcheckbox with some options, last one is "other", if checked, the user must insert a note. 
	 */
	
	private Boolean enableAnnotation;
	
	@Column(name="enableAnnotation")
	@CodeValueExtension(name="Enable annotation")	
	public Boolean getEnableAnnotation() {
		return enableAnnotation;
	}

	public void setEnableAnnotation(Boolean enableAnnotation) {
		this.enableAnnotation = enableAnnotation;
	}


	
	/**
	 * RELATION WITH PHI TYPE
	 */
	private Collection<CodeValuePhi> relationsPhi = new HashSet<CodeValuePhi>();
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_phi_phi",
		joinColumns = {@JoinColumn(name="cv_phi1", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="cv_phi2", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_PHI1", inverseName="FK_CV_PHI2")
	@CodeValueEquivalent(targetType=CodeValuePhi.class)
	public Collection<CodeValuePhi> getRelationsPhi() {
		return this.relationsPhi;
	}

	public void setRelationsPhi(Collection<CodeValuePhi> relationsPhi) {
		this.relationsPhi = relationsPhi;
	}

	/**
	 * INVERSE RELATION WITH PHI TYPE
	 */
	private Collection<CodeValuePhi> inverseRelationsPhi = new HashSet<CodeValuePhi>();
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="relationsPhi")
	@CodeValueEquivalent(targetType=CodeValuePhi.class, isInverse=true)
	public Collection<CodeValuePhi> getInverseRelationsPhi() {
		return this.inverseRelationsPhi;
	}

	public void setInverseRelationsPhi(Collection<CodeValuePhi> inverseRelationsPhi) {
		this.inverseRelationsPhi = inverseRelationsPhi;
	}

	/**
	 * RELATION WITH CITY TYPE
	 */
	private Collection<CodeValueCity> relationsCity = new HashSet<CodeValueCity>();

	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_phi_city",
	joinColumns = {@JoinColumn(name="cv_phi", nullable=false, updatable=false) }, 
	inverseJoinColumns = {@JoinColumn(name="cv_nanda", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_PHI", inverseName="FK_CV_CITY")
	@CodeValueEquivalent(targetType=CodeValueCity.class)
	public Collection<CodeValueCity> getRelationsCity() {
		return this.relationsCity;
	}

	public void setRelationsCity(Collection<CodeValueCity> relationsCity) {
		this.relationsCity = relationsCity;
	}

//	/**
//	 * RELATION WITH STATUS TYPE
//	 */
//	private Collection<CodeValueStatus> relationsStatus = new HashSet<CodeValueStatus>();
//
//	@ManyToMany(fetch=FetchType.LAZY)
//	@JoinTable(name="code_equivalent_phi_status",
//	joinColumns = {@JoinColumn(name="cv_phi", nullable=false, updatable=false) }, 
//	inverseJoinColumns = {@JoinColumn(name="cv_status", nullable=false, updatable=false)})
//	@ForeignKey(name="FK_CV_PHI", inverseName="FK_CV_STATUS")
//	@CodeValueEquivalent(targetType=CodeValueCity.class)
//	public Collection<CodeValueStatus> getRelationsStatus() {
//		return this.relationsStatus;
//	}
//
//	public void setRelationsStatus(Collection<CodeValueStatus> relationsStatus) {
//		this.relationsStatus = relationsStatus;
//	}

//	/**
//	 * RELATION WITH ICD9 TYPE
//	 */
//	private Collection<CodeValueIcd9> relationsIcd9 = new HashSet<CodeValueIcd9>();
//	
//	@ManyToMany(fetch=FetchType.LAZY)
//	@JoinTable(name="code_equivalent_phi_icd9",
//		joinColumns = {@JoinColumn(name="cv_phi", nullable=false, updatable=false) }, 
//		inverseJoinColumns = {@JoinColumn(name="cv_icd9", nullable=false, updatable=false)})
//	@ForeignKey(name="FK_CV2_PHI", inverseName="FK_CV_ICD9")
//	@CodeValueEquivalent(targetType=CodeValueIcd9.class)
//	public Collection<CodeValueIcd9> getRelationsIcd9() {
//		return this.relationsIcd9;
//	}
//
//	public void setRelationsIcd9(Collection<CodeValueIcd9> relationsIcd9) {
//		this.relationsIcd9 = relationsIcd9;
//	}

}