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

import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "exposition_coefficients")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ExpositionCoefficients extends BaseEntity {

	private static final long serialVersionUID = 771528757L;

	/**
	*  javadoc for multiplier
	*/
	private Double multiplier;

	@Column(name="multiplier")
	public Double getMultiplier(){
		return multiplier;
	}

	public void setMultiplier(Double multiplier){
		this.multiplier = multiplier;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_ExpCoeffs_code")
	//@Index(name="IX_ExpCoeffs_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "expcoeffs_sequence")
	@SequenceGenerator(name = "expcoeffs_sequence", sequenceName = "expcoeffs_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
