package com.phi.entities.dataTypes;

import java.util.Collection;

import javax.persistence.Column;
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

import com.phi.annotations.CodeValueExtension;

/**
 * CodeValue for "StatiEsteriBDPA" CodeSystem
 */

@Entity
@Table(name = "code_value_country", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_country", indexes = { @Index(name="IX_Code_Value_country", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueCountry extends CodeValue {

	private static final long serialVersionUID = -3796157763547657602L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCountry.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueCountry_Parent")
	@Index(name="IX_CodeValueCountry_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueCountry.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

	/**
	 * Codice Catastale
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

//	/**
//	 * Cittadinanza
//	 */
//
//	private String citizenship;
//	
//	@Column(name="citizenship")
//	public String getCitizenship() {
//		return citizenship;
//	}
//
//	public void setCitizenship(String citizenship) {
//		this.citizenship = citizenship;
//	}
}
