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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.annotations.CodeValueEquivalent;
import com.phi.annotations.CodeValueExtension;
import com.phi.json.CodeValueIcd9Deserializer;

/**
 * CodeValue for "ICD9DIAG" CodeSystem
 */

@Entity
@Table(name = "code_value_icd9", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_icd9", indexes = { @Index(name="IX_Code_Value_icd9", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@JsonDeserialize(using = CodeValueIcd9Deserializer.class)
public class CodeValueIcd9 extends CodeValue {

	private static final long serialVersionUID = -3796157763547657602L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueIcd9_Parent")
	@Index(name="IX_CodeValueIcd9_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * Code Extension : classification: it can be an alias too
	 */
	private String classification;

	@Column(name="classification",length=50)
	@CodeValueExtension(name="classification")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
//	//FIXME: DE-COMMENT TO USE EQUIVALENCES
//	/**
//	 * RELATION WITH PHI TYPE
//	 */
	private Collection<CodeValuePhi> relationsPhi = new HashSet<CodeValuePhi>();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_phi_icd9",
		joinColumns = {@JoinColumn(name="cv_icd9", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="cv_phi", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_ICD9", inverseName="FK_CV2_PHI")
	@CodeValueEquivalent(targetType=CodeValueIcd9.class)
	public Collection<CodeValuePhi> getRelationsPhi() {
		return this.relationsPhi;
	}

	public void setRelationsPhi(Collection<CodeValuePhi> relationsPhi) {
		this.relationsPhi = relationsPhi;
	}

}