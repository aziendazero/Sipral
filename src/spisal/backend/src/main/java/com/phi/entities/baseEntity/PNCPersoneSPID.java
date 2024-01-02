package com.phi.entities.baseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.EN;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@Table(name = "pnc_persone_spid")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PNCPersoneSPID extends BaseEntity {

	private static final long serialVersionUID = 277604573L;

	/**
	*  javadoc for dtlogin
	*/
	private Date dtlogin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dtlogin")
	public Date getDtlogin(){
		return dtlogin;
	}

	public void setDtlogin(Date dtlogin){
		this.dtlogin = dtlogin;
	}

	/**
	*  javadoc for pec
	*/
	private String pec;

	@Column(name="pec")
	public String getPec(){
		return pec;
	}

	public void setPec(String pec){
		this.pec = pec;
	}

	/**
	*  javadoc for username
	*/
	private String username;

	@Column(name="username")
	public String getUsername(){
		return username;
	}

	public void setUsername(String username){
		this.username = username;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCPersoneSPID_sequence")
	@SequenceGenerator(name = "PNCPersoneSPID_sequence", sequenceName = "PNCPersoneSPID_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	 *  Name
	 */
	private EN name;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="fam", column=@Column(name="name_fam")),
		@AttributeOverride(name="giv", column=@Column(name="name_giv")),
		@AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
		@AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
		@AttributeOverride(name="del", column=@Column(name="name_del")),
		@AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
	})
	public EN getName(){
		return name;
	}

	public void setName(EN name){
		this.name = name;
	}

}
