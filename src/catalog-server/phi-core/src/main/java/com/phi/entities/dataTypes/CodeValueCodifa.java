package com.phi.entities.dataTypes;

import java.util.Collection;

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

/**
 * CodeValue for "CODIFA" CodeSystem
 */

@Entity
@Table(name = "code_value_codifa", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_codifa", indexes = { @Index(name="IX_Code_Value_codifa", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueCodifa extends CodeValue {

	private static final long serialVersionUID = -3796157763547657602L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueCodifa_Parent")
	@Index(name="IX_CodeValueCodifa_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * RELATION WITH PHI TYPE
	 */
	private Collection<CodeValuePhi> relationsPhi;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_codifa_phi",
		joinColumns = {@JoinColumn(name="cv_codifa", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="cv_phi", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_codifa_phi", inverseName="FK_CV_phi_codifa")
	@CodeValueEquivalent(targetType=CodeValuePhi.class)
	public Collection<CodeValuePhi> getRelationsPhi() {
		return this.relationsPhi;
	}

	public void setRelationsPhi(Collection<CodeValuePhi> relationsPhi) {
		this.relationsPhi = relationsPhi;
	}
}