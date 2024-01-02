package com.phi.dictionaryManager;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeUsers;
import com.phi.entities.dataTypes.CodeValue;

@Entity
@Table(name="code_value_users")
public class CodeUsersValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "code_value_user_sequence")
    @SequenceGenerator(name = "code_value_user_sequence", sequenceName = "code_value_user_sequence")
    @Column(name="id")
    private int id;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="code_user_id", nullable=false)
	@ForeignKey(name="FK_User_User")
	@OnDelete(action=OnDeleteAction.CASCADE)
	@Index(name="IX_User_User")
	private CodeUsers codeUser;

	public CodeUsers getCodeUser() {
		return codeUser;
	}

	public void setCodeUser(CodeUsers codeUser) {
		this.codeUser = codeUser;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinColumn(name="code_value_id", nullable=false)
	@ForeignKey(name="FK_User_Value")
	@Index(name="IX_User_Value")
	private CodeValue codeValue;

	public CodeValue getCodeValueId() {
		return codeValue;
	}

	public void setCodeValueId(CodeValue codeValue) {
		this.codeValue = codeValue;
	}
	
}
