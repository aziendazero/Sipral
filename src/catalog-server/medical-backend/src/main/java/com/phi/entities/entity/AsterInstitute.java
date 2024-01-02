package com.phi.entities.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;

import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.Date;

@javax.persistence.Entity
@Table(name = "aster_institute")
@Audited
public class AsterInstitute extends Organization {

	private static final long serialVersionUID = 594243844L;
	private CodeValuePhi codeType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code_type")
	@ForeignKey(name="FK_astr_inst_code_type")
	@Index(name="IX_astr_inst_code_type")	
	public CodeValuePhi getCodeType() {
		return codeType;
	}

	public void setCodeType(CodeValuePhi codeType) {
		this.codeType = codeType;
	}
	
}