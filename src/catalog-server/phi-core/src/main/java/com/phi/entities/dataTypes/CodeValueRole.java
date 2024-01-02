package com.phi.entities.dataTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.jboss.seam.core.Locale;

import com.phi.annotations.CodeValueExtension;
import com.phi.entities.role.ServiceDeliveryLocation;

/**
 * CodeValue for EmployeeRole.code CodeSystem
 */

@javax.persistence.Entity
@Table(name = "code_value_role", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_role", indexes = { @Index(name="IX_Code_Value_Role", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@Audited
public class CodeValueRole extends CodeValue implements java.io.Serializable {

	private static final long serialVersionUID = 113684395148L;
	
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_code_value_role_Parent")
	@Index(name="IX_code_value_role_Parent")
	public CodeValue getParent() {
		return parent;
	}
	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}
	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}
	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * Code Extension abbreviation
	 */
	/* Translated abbreviation.
	private String abbreviation;

	@Column(name="abbreviation")
	@CodeValueExtension(name="Abbreviation")
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	} */

	@Transient
	public String getAbbreviation() {
		String language = Locale.instance().getLanguage();
		if (language != null && !language.isEmpty()) {
			if ("it".equals(language)){
				return abbreviationIt;
			}
			if ("en".equals(language)){
				return abbreviationEn;
			}
			if ("de".equals(language)){
				return abbreviationDe;
			}
		}
		return abbreviationIt;
	}
	
	
	private String abbreviationIt;

	@Column(name="abbreviation_it")
	@CodeValueExtension(name="AbbreviationIt")
	public String getAbbreviationIt() {
		return abbreviationIt;
	}

	public void setAbbreviationIt(String abbreviationIt) {
		this.abbreviationIt = abbreviationIt;
	}

	
	private String abbreviationEn;

	@Column(name="abbreviation_en")
	@CodeValueExtension(name="AbbreviationEn")
	public String getAbbreviationEn() {
		return abbreviationEn;
	}

	public void setAbbreviationEn(String abbreviationEn) {
		this.abbreviationEn = abbreviationEn;
	}

	
	private String abbreviationDe;

	@Column(name="abbreviation_de")
	@CodeValueExtension(name="AbbreviationDe")
	public String getAbbreviationDe() {
		return abbreviationDe;
	}

	public void setAbbreviationDe(String abbreviationDe) {
		this.abbreviationDe = abbreviationDe;
	}
	
	private List<ServiceDeliveryLocation> enabledServiceDeliveryLocations;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="code_value_role_enabledsdloc", 
	joinColumns = { @JoinColumn(name="codeValueRole_id") }, 
	inverseJoinColumns = { @JoinColumn(name="sdloc_id") })
	@ForeignKey(name="FK_cdvlrl_cdvlrlId", inverseName="FK_cdvlrl_sdlocId")
	@CodeValueExtension(name="EnabledServiceDeliveryLocations")
	public List<ServiceDeliveryLocation> getEnabledServiceDeliveryLocations() {
		return enabledServiceDeliveryLocations;
	}

	public void setEnabledServiceDeliveryLocations(List<ServiceDeliveryLocation> enabledServiceDeliveryLocations) {
		this.enabledServiceDeliveryLocations=enabledServiceDeliveryLocations;
	}

	public void addEnabledServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
		// create the association set if it doesn't exist already
		if (this.enabledServiceDeliveryLocations == null) {
			this.enabledServiceDeliveryLocations = new ArrayList<ServiceDeliveryLocation>();
		}
		// add the association to the association set
		if(!this.enabledServiceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
			this.enabledServiceDeliveryLocations.add(enabledServiceDeliveryLocations);
		}
	}

	public void removeEnabledServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
		// if it doesn't exist already return
		if (this.enabledServiceDeliveryLocations == null) {
			return;
		}
		// remove the association to the association set
		if(this.enabledServiceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
			this.enabledServiceDeliveryLocations.remove(enabledServiceDeliveryLocations);
		}
	}
	
}