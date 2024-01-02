package com.phi.entities.dataTypes;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.json.CodeValueExemptionDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

/**
 * CodeValue for "EXEMPTION" CodeSystem
 */

@Entity
@Table(name="code_value_exemption", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_exemption", indexes = { @Index(name="IX_Code_Value_exemption", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@JsonDeserialize(using = CodeValueExemptionDeserializer.class)
public class CodeValueExemption extends CodeValue {

	private static final long serialVersionUID = -7845723472588050941L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueExemption.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueExemption_Parent")
	@Index(name="IX_CodeValueExemption_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueExemption.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

}