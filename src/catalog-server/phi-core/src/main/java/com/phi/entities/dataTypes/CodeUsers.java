package com.phi.entities.dataTypes;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

@Entity
@Table(name="code_users")
@Audited
public class CodeUsers implements java.io.Serializable {

	private static final long serialVersionUID = -4265429104545794557L;

	private Integer id;
	private String userName;
	private String password;
	private boolean isSuperUser = false;
	private boolean isObserverUser = false;
	private Collection<CodeValue> domains = new HashSet<CodeValue>(0);
	private String authorityName;
	private String authorityDescription;
	private String contactInformation;

	public CodeUsers() {
	}


	public CodeUsers(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public CodeUsers(String userName, String password, boolean isSuperUser, boolean isObserverUser, Collection<CodeValue> domains, String authorityName, String authorityDescription, String contactInformation) {
		this.userName = userName;
		this.password = password;
		this.isSuperUser = isSuperUser;
		this.isObserverUser = isObserverUser;
		this.domains = domains;
		this.authorityName = authorityName;
		this.authorityDescription = authorityDescription;
		this.contactInformation = contactInformation;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "code_users_sequence")
    @SequenceGenerator(name = "code_users_sequence", sequenceName = "code_users_sequence")
	@Column(name="id")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="user_namedb", unique=true, nullable=false, length=65)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
    @Column(name="password", nullable=false, length=65)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="is_super_user", nullable=false)
	public boolean isIsSuperUser() {
		return this.isSuperUser;
	}

	public void setIsSuperUser(boolean isSuperUser) {
		this.isSuperUser = isSuperUser;
	}
	
	@Column(name="is_observer_user", nullable=false)
	public boolean isIsObserverUser() {
		return isObserverUser;
	}

	public void setIsObserverUser(boolean isObserver) {
		this.isObserverUser = isObserver;
	}
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="code_value_users",
		joinColumns = {@JoinColumn(name="code_user_id", nullable=false, updatable=false) }, 
		inverseJoinColumns = {@JoinColumn(name="code_value_id", nullable=false, updatable=false)})
	@ForeignKey(name="FK_User_User", inverseName="FK_User_Value")
	public Collection<CodeValue> getDomains() {
		return this.domains;
	}

	public void setDomains(Collection<CodeValue> domains) {
		this.domains = domains;
	}
	
	@Column(name="authority_name", length=65)
	public String getAuthorityName() {
		return this.authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}
	
	@Column(name="authority_desc")
	public String getAuthorityDescription() {
		return this.authorityDescription;
	}

	public void setAuthorityDescription(String authorityDescription) {
		this.authorityDescription = authorityDescription;
	}
	
	@Column(name="contact_info")
	public String getContactInformation() {
		return this.contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorityDescription == null) ? 0 : authorityDescription.hashCode());
		result = prime * result + ((authorityName == null) ? 0 : authorityName.hashCode());
		result = prime * result + ((contactInformation == null) ? 0 : contactInformation.hashCode());
		result = prime * result + ((domains == null) ? 0 : domains.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isSuperUser ? 1231 : 1237);
		result = prime * result + (isObserverUser ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CodeUsers)) {
			return false;
		}
		CodeUsers other = (CodeUsers) obj;
		if (authorityDescription == null) {
			if (other.authorityDescription != null) {
				return false;
			}
		} else if (!authorityDescription.equals(other.authorityDescription)) {
			return false;
		}
		if (authorityName == null) {
			if (other.authorityName != null) {
				return false;
			}
		} else if (!authorityName.equals(other.authorityName)) {
			return false;
		}
		if (contactInformation == null) {
			if (other.contactInformation != null) {
				return false;
			}
		} else if (!contactInformation.equals(other.contactInformation)) {
			return false;
		}
		if (domains == null) {
			if (other.domains != null) {
				return false;
			}
		} else if (!domains.equals(other.domains)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (isSuperUser != other.isSuperUser) {
			return false;
		}
		if (isObserverUser != other.isObserverUser) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

}