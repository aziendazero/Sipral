package com.phi.entities.dataTypes;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;
import org.hibernate.proxy.HibernateProxyHelper;

@Entity
@Table(name = "code_system", uniqueConstraints = {@UniqueConstraint(columnNames={"oid","version"}),@UniqueConstraint(columnNames={"name","version"})})
@Audited
public class CodeSystem implements java.io.Serializable {

	private static final long serialVersionUID = 1467190504574154655L;

	private Integer idx;
	private String id;
	private Integer version = 1;
	private String name;
	private String displayName;
	private String description;
	private Integer status = 1;
	private Date creationDate;
	private Date validFrom;
	private Date validTo;
	private Date revisedDate;
	private String isoCodeLang;
	private String authorityName;
	private String authorityDescription;
	private String contactInformation;
   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "code_system_sequence")
    @SequenceGenerator(name = "code_system_sequence", sequenceName = "code_system_sequence")
    @Column(name="id")
    public Integer getIdx() {
        return this.idx;
    }
    
    public void setIdx(Integer id) {
        this.idx = id;
    }
    
    @Column(name="oid", nullable=false)
    public String getId() {
        return this.id;
    }
    
    public void setId(String oid) {
        this.id = oid;
    }
    
    @Column(name="version", nullable=false)
    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name="name", nullable=false, length=45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Column(name="display_name", nullable=false, length=45)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    @Column(name="description", nullable=false)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="statusDB", nullable=false)
	public Integer getStatus() {
		return this.status;
	}
	
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_date", nullable=false)
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_from")
	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_to")
	public Date getValidTo() {
		return this.validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="revised_date")
	public Date getRevisedDate() {
		return this.revisedDate;
	}

	public void setRevisedDate(Date revisedDate) {
		this.revisedDate = revisedDate;
	}

	@Column(name="iso_code_lang", nullable=false, length=2)
	public String getIsoCodeLang() {
		return this.isoCodeLang;
	}

	public void setIsoCodeLang(String isoCodeLang) {
		this.isoCodeLang = isoCodeLang;
	}

	@Column(name="authority_name", nullable=false)
	public String getAuthorityName() {
		return this.authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	@Column(name="authority_description", nullable=false)
	public String getAuthorityDescription() {
		return this.authorityDescription;
	}

	public void setAuthorityDescription(String authorityDescription) {
		this.authorityDescription = authorityDescription;
	}

	@Column(name="contact_information", nullable=false, length=45)
	public String getContactInformation() {
		return this.contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	
	// CLASS NAME
	private String codeValueClass;

	@Column(name="code_value_class", nullable=false, length=45)
	public String getCodeValueClass() {
		return codeValueClass;
	}

	public void setCodeValueClass(String codeValueClass) {
		this.codeValueClass = codeValueClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((idx == null) ? 0 : idx.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final CodeSystem other = (CodeSystem) obj;
		if (idx == null) {
			if (other.getIdx() != null)
				return false;
		} else if (idx.equals(other.getIdx())) {
			return true;
		} else {
			return false;
		}
		
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}