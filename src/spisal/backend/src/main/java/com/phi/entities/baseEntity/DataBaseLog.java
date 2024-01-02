package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@javax.persistence.Entity
@Table(name = "db_log")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DataBaseLog extends BaseEntity {

	private static final long serialVersionUID = 1476862848L;

	/**
	*  javadoc for messageTxt
	*/
	private String messageTxt;

	@Column(name="message_txt")
	public String getMessageTxt(){
		return messageTxt;
	}

	public void setMessageTxt(String messageTxt){
		this.messageTxt = messageTxt;
	}

	/**
	*  javadoc for objectIdentifier
	*/
	private String objectIdentifier;

	@Column(name="object_identifier")
	public String getObjectIdentifier(){
		return objectIdentifier;
	}

	public void setObjectIdentifier(String objectIdentifier){
		this.objectIdentifier = objectIdentifier;
	}

	/**
	*  javadoc for objectClass
	*/
	private String objectClass;

	@Column(name="object_class")
	public String getObjectClass(){
		return objectClass;
	}

	public void setObjectClass(String objectClass){
		this.objectClass = objectClass;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DataBaseLog_sequence")
	@SequenceGenerator(name = "DataBaseLog_sequence", sequenceName = "DataBaseLog_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
