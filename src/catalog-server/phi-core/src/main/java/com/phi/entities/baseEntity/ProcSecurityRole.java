package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValueRole;
@javax.persistence.Entity
@Table(name = "proc_security_role")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ProcSecurityRole extends BaseEntity {

	private static final long serialVersionUID = 977169028L;

	/**
	*  javadoc for readonly
	*/
	private Boolean readonly;

	@Column(name="readonly")
	public Boolean getReadonly(){
		return readonly;
	}

	public void setReadonly(Boolean readonly){
		this.readonly = readonly;
	}

	/**
	*  javadoc for role
	*/
	private CodeValueRole role;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role")
	@ForeignKey(name="FK_ProcSecurityRole_role")
	@Index(name="IX_ProcSecurityRole_role")
	public CodeValueRole getRole(){
		return role;
	}

	public void setRole(CodeValueRole role){
		this.role = role;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProcSecurityRole_sequence")
	@SequenceGenerator(name = "ProcSecurityRole_sequence", sequenceName = "ProcSecurityRole_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Override
	public String toString() {
		return role + " " + (Boolean.TRUE.equals(readonly) ? "readonly" : "");
	}

}