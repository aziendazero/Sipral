package com.phi.entities.dataTypes;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

/**
 * CodeValue for "STATUS" CodeSystem
 */

@Entity
@Table(name="code_value_status", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_status", indexes = { @Index(name="IX_Code_Value_Status", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueStatus extends CodeValue implements java.io.Serializable {
	
	private static final long serialVersionUID = 4882052850318306810L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueStatus.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueStatus_Parent")
	@Index(name="IX_CodeValueStatus_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueStatus.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

//	/**
//	 * RELATION WITH PHI TYPE
//	 */
//	private Collection<CodeValuePhi> relationsPhi = new HashSet<CodeValuePhi>();
//	
//	@ManyToMany(fetch=FetchType.LAZY, mappedBy="relationsStatus")
//	@CodeValueEquivalent(targetType=CodeValuePhi.class)
//	public Collection<CodeValuePhi> getRelationsPhi() {
//		return this.relationsPhi;
//	}
//
//	public void setRelationsPhi(Collection<CodeValuePhi> relationsPhi) {
//		this.relationsPhi = relationsPhi;
//	}

}
