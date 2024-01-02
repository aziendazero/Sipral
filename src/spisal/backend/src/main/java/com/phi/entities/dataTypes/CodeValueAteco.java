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

@Entity(name="CodeValueAteco")
@Table(name="code_value_ateco", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_ateco", indexes = { @Index(name="IX_Code_Value_Ateco", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueAteco extends CodeValue implements java.io.Serializable {

	private static final long serialVersionUID = 424448665900804336L;
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueAteco.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueAteco_Parent")
	//@Index(name="IX_CodeValueAteco_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueAteco.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
}
