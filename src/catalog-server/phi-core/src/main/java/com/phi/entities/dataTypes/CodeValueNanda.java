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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.jboss.seam.core.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.annotations.CodeValueEquivalent;
import com.phi.annotations.CodeValueExtension;

/**
 * CodeValue for "NANDA" CodeSystem
 */

@Entity
@Table(name = "code_value_nanda", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_nanda", indexes = { @Index(name="IX_Code_Value_nanda", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueNanda extends CodeValue {

	private static final long serialVersionUID = -3796157763547657602L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueNanda.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueNanda_Parent")
	@Index(name="IX_CodeValueNanda_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueNanda.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

	@JsonIgnore(false)
	@Override
	@Transient
	public String getDescription() {
		return this.description;
	}

	@Override
	@Transient
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore(false)
	@Transient
	@Override
	public String getCurrentDescription() {
		String language = Locale.instance().getLanguage();
		if (language != null && !language.isEmpty()) {
			String description = getDescription(language);
			if (description != null && !description.isEmpty())
				return description;
		}
		return getDescription();
	}
	
	@Override
	public void setCurrentDescription(String currentDescription) {
		String language = Locale.instance().getLanguage();
		if (language != null && !language.isEmpty()) {
			setDescription(language, currentDescription);
		}
	}

	@Transient
	public String getDescription(String langCode) {
		String result = null;
		if ("it".equals(langCode)){
			result = getDescriptionIt();
		} else if ("en".equals(langCode)){
			result = getDescriptionEn();
		} else if ("de".equals(langCode)){
			result = getDescriptionDe();
		}
		return result;
	}

	public void setDescription(String langCode, String description) {
		if ("it".equals(langCode)){
			setDescriptionIt(description);
		} else if ("en".equals(langCode)){
			setDescriptionEn(description);
		} else if ("de".equals(langCode)){
			setDescriptionDe(description);
		}
	}

	/**
	 * Code Extension DESCRIPTIONS
	 */
	private String descriptionDe;

	@Column(name="description_de",length=1000)
	@CodeValueExtension(name="DescriptionDe")
	public String getDescriptionDe() {
		return descriptionDe;
	}

	public void setDescriptionDe(String descriptionDe) {
		this.descriptionDe = descriptionDe;
	}
	
	private String descriptionEn;

	@Column(name="description_en",length=1000)
	@CodeValueExtension(name="DescriptionEn")
	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}
	
	private String descriptionIt;

	@Column(name="description_it",length=1000)
	@CodeValueExtension(name="DescriptionIt")
	public String getDescriptionIt() {
		return descriptionIt;
	}

	public void setDescriptionIt(String descriptionIt) {
		this.descriptionIt = descriptionIt;
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
	 * Code Extension itmkviid
	 * Internal id for LEP\NANDA catalogues
	 * See: http://www.lep.ch/de/
	 */
	private String itmkviid;

	@Column(name="itmkviid")
	@CodeValueExtension(name="Itmkviid")
	public String getItmkviid() {
		return itmkviid;
	}

	public void setItmkviid(String itmkviid) {
		this.itmkviid = itmkviid;
	}
	
	/**
	 * Code Extension length
	 */
	private Integer length;

	@Column(name="length")
	@CodeValueExtension(name="Length")
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}
	
	/**
	 * RELATION WITH NANDA TYPE
	 */
	private Collection<CodeValueNanda> relationsNanda = new HashSet<CodeValueNanda>();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="code_equivalent_nanda_nanda",
		joinColumns = {@JoinColumn(name="cv_nanda1", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="cv_nanda2", nullable=false, updatable=false)})
	@ForeignKey(name="FK_CV_NANDA1", inverseName="FK_CV_NANDA2")
	@CodeValueEquivalent(targetType=CodeValueNanda.class)
	public Collection<CodeValueNanda> getRelationsNanda() {
		return this.relationsNanda;
	}

	public void setRelationsNanda(Collection<CodeValueNanda> relationsNanda) {
		this.relationsNanda = relationsNanda;
	}

	/**
	 * INVERSE RELATION WITH NANDA TYPE
	 */
	private Collection<CodeValueNanda> inverseRelationsNanda = new HashSet<CodeValueNanda>();
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="relationsNanda")
	@CodeValueEquivalent(targetType=CodeValueNanda.class, isInverse=true)
	public Collection<CodeValueNanda> getInverseRelationsNanda() {
		return this.inverseRelationsNanda;
	}

	public void setInverseRelationsNanda(Collection<CodeValueNanda> inverseRelationsNanda) {
		this.inverseRelationsNanda = inverseRelationsNanda;
	}

}