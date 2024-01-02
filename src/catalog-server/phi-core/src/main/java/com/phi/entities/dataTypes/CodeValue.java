package com.phi.entities.dataTypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.core.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.phi.entities.PhiRevisionEntity;


/**
 *Abstract class for all CodeValue classes managed by Dictionary manager
 */

@Entity
@Audited
@Table(name="c1")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, include=As.PROPERTY, property="entityName", visible=true, defaultImpl=CodeValue.class)
public abstract class CodeValue implements Serializable {

	private static final long serialVersionUID = 4632729689711740491L;
	
	protected String id;
    protected String oid;
    protected Integer version = 1;
	protected CodeSystem codeSystem;
	protected CodeValue parent;
	protected Collection<CodeValue> children = new HashSet<CodeValue>();
	protected String code;
	protected String displayName;
	protected String type;
	protected int sequenceNumber;
	protected boolean defaultChild;
	protected Integer status;
	protected String description;
	protected String keywords;
	protected Date validFrom;
	protected Date validTo;
	protected Date revisedDate;
	protected String creator;
	protected String changeReason;
	
	// LANGUAGES
	public static final String[][] languages = new String[][]{ {"de","en","it"}, {"german","english","italian"} };
	private String langEn;
	private String langDe;
	private String langIt;
	@SuppressWarnings("unused")
	private String currentTranslation; // unused: needed to see property under Catalog Explorer
	
	private static HashMap<String, Class<CodeValue>> derivedClasses = new HashMap<String, Class<CodeValue>>();

	public static HashMap<String, Class<CodeValue>> getDerivedClasses() {
		return derivedClasses;
	}
	
	@SuppressWarnings("unchecked")
	public CodeValue() {
		Class<CodeValue> derivedClass = (Class<CodeValue>)this.getClass();
        if (!derivedClasses.containsKey(derivedClass.getSimpleName())) {
        	derivedClasses.put(derivedClass.getSimpleName(), derivedClass);
        }
	}

	@Id
	@Column(name="id")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	@Column(name="oid", nullable=false)
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	@JsonIgnore
	@Column(name="version", nullable=false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="code_system", nullable=false)
	@ForeignKey(name="FK_CV_CS")
	@Index(name="IX_CV_CS")
	public CodeSystem getCodeSystem() {
		return this.codeSystem;
	}

	public void setCodeSystem(CodeSystem codeSystem) {
		this.codeSystem = codeSystem;
	}

	//Parent/chid relations defined into subclass implementations
	
	@JsonIgnore
	@Transient
	public abstract CodeValue getParent();

	public abstract void setParent(CodeValue parent);

	@JsonIgnore
	@Transient
	public abstract Collection<CodeValue> getChildren();

	public abstract void setChildren(Collection<CodeValue> children);

	
    @Column(name="code", nullable=false, length=65)
    @Index(name="IX_CV_Code")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name="display_name", nullable=false)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonIgnore
	@Column(name="typeDB", nullable=false, length=1)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonIgnore
	@Column(name="sequence_number", nullable=false)
	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@JsonIgnore
	@Column(name="default_child", nullable=false)
	public boolean isDefaultChild() {
		return this.defaultChild;
	}

	@JsonIgnore
	public void setDefaultChild(boolean defaultChild) {
		this.defaultChild = defaultChild;
	}

	@JsonIgnore
	@Column(name="statusDB", nullable=false)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@JsonIgnore
	@Column(name="description", length=765)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	@Transient
	public String getCurrentDescription() {
		return getDescription();
	}

	@JsonIgnore
	public void setCurrentDescription(String currentDescription) {
		setDescription(currentDescription);
	}
	
	// @JsonIgnore
	@Column(name="keywords", length=65)
	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_from")
	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	@JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_to")
	public Date getValidTo() {
		return this.validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="revised_date")
	public Date getRevisedDate() {
		return this.revisedDate;
	}

	public void setRevisedDate(Date revisedDate) {
		this.revisedDate = revisedDate;
	}

	@JsonIgnore
	@Column(name="change_reason")
	public String getChangeReason() {
		return this.changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

	@JsonIgnore
	@Column(name="creator", length=65)
	public String getCreator() {
		return this.creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}


	// Translations

	@Transient
	public String getCurrentTranslation() {
		
		String language = Locale.instance().getLanguage();
		if (language != null && !language.isEmpty()) {
			String translation = getTranslation(language);
			if (translation != null && !translation.isEmpty())
				return translation;
		}
		return displayName;
	}
	
	public void setCurrentTranslation(String currentTranslation) {
		String language = Locale.instance().getLanguage();
		if (language != null && !language.isEmpty()) {
			setTranslation(language, currentTranslation);
		}
	}
	
	@JsonIgnore
	@Transient
	public HashMap<String, String> getTranslations() {
		HashMap<String, String> translations = new HashMap<String, String>();
		translations.put("it", getLangIt());
		translations.put("en", getLangEn());
		translations.put("de", getLangDe());
		return translations;
	}

	public void setTranslations(HashMap<String, String> translations) {
		setLangIt(translations.get("it"));
		setLangEn(translations.get("en"));
		setLangDe(translations.get("de"));
	}

	
	@Transient
	public String getTranslationPatient(String langCode) {
		String result = null;
	
		if ("ITA".equals(langCode)){
			result = getLangIt();
		} else if ("ENG".equals(langCode)){
			result = getLangEn();
		} else if ("GER".equals(langCode)){
			result = getLangDe();
		}
		return result;
	}
	
	public void setTranslationPatient(String langCode, String translation) {
		if ("ITA".equals(langCode)){
			setLangIt(translation);
		} else if ("ENG".equals(langCode)){
			setLangEn(translation);
		} else if ("GER".equals(langCode)){
			setLangDe(translation);
		}
	}
	
	
	
	@Transient
	public String getTranslation(String langCode) {
		String result = null;
		if ("it".equals(langCode)){
			result = getLangIt();
		} else if ("en".equals(langCode)){
			result = getLangEn();
		} else if ("de".equals(langCode)){
			result = getLangDe();
		}
		return result;
	}
	
	public void setTranslation(String langCode, String translation) {
		if ("it".equals(langCode)){
			setLangIt(translation);
		} else if ("en".equals(langCode)){
			setLangEn(translation);
		} else if ("de".equals(langCode)){
			setLangDe(translation);
		}
	}
	
//	@JsonIgnore
	@Column(name="lang_de", length=1000)
	public String getLangDe() {
		return this.langDe;
	}
	
	public void setLangDe(String translation) {
		this.langDe = translation;
	}

//	@JsonIgnore
	@Column(name="lang_en", length=1000)
	public String getLangEn() {
		return this.langEn;
	}
	
	public void setLangEn(String translation) {
		this.langEn = translation;
	}

//	@JsonIgnore
	@Column(name="lang_it", length=1000)
	public String getLangIt() {
		return this.langIt;
	}
	
	public void setLangIt(String translation) {
		this.langIt = translation;
	}
	
	protected PhiRevisionEntity revision;

	@Transient
	public PhiRevisionEntity getRevision() {
		return revision;
	}

	public void setRevision(PhiRevisionEntity revision) {
		this.revision = revision;
	}

	// Object methods

	@Override
	public String toString() {
		return getCurrentTranslation();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeReason == null) ? 0 : changeReason.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + (defaultChild ? 1231 : 1237);
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((revisedDate == null) ? 0 : revisedDate.hashCode());
		result = prime * result + sequenceNumber;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
		result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != HibernateProxyHelper.getClassWithoutInitializingProxy(obj))
			return false;
		final CodeValue other = (CodeValue) obj;
		
		//Added to grant proxy equals to object
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (id.equals(other.getId())) {
			return true;
		} else {
			return false;
		}
		
		//two objects without id check fields:
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (validFrom == null) {
			if (other.validFrom != null)
				return false;
		} else if (!validFrom.equals(other.validFrom))
			return false;
		if (validTo == null) {
			if (other.validTo != null)
				return false;
		} else if (!validTo.equals(other.validTo))
			return false;
		
		return true;
	}

}