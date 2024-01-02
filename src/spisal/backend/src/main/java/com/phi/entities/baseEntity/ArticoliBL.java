package com.phi.entities.baseEntity;

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
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueLaw;
@javax.persistence.Entity
@Table(name = "articoli_bl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ArticoliBL extends BaseEntity {

	private static final long serialVersionUID = 23086847L;

	/**
	*  javadoc for code
	*/
	private CodeValueLaw code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_ArticoliBL_code")
	//@Index(name="IX_ArticoliBL_code")
	public CodeValueLaw getCode(){
		return code;
	}

	public void setCode(CodeValueLaw code){
		this.code = code;
	}

	/**
	*  javadoc for maxIter
	*/
	private Integer maxIter;

	@Column(name="max_iter")
	public Integer getMaxIter(){
		return maxIter;
	}

	public void setMaxIter(Integer maxIter){
		this.maxIter = maxIter;
	}

	
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ArticoliBL_sequence")
	@SequenceGenerator(name = "ArticoliBL_sequence", sequenceName = "ArticoliBL_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
