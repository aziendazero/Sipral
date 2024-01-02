package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.List;
import com.phi.entities.role.ServiceDeliveryLocation;


import java.util.ArrayList;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
@javax.persistence.Entity
@Table(name = "scadenza_tipo_com")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ScadenzaTipoCom extends BaseEntity {

	private static final long serialVersionUID = 918796467L;


	/**
	*  javadoc for ulss
	*/
	private ServiceDeliveryLocation ulss;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ulss_id")
	@ForeignKey(name="FK_ScadenzaTipoCom_ulss")
	//@Index(name="IX_ScadenzaTipoCom_ulss")
	public ServiceDeliveryLocation getUlss(){
		return ulss;
	}

	public void setUlss(ServiceDeliveryLocation ulss){
		this.ulss = ulss;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_ScadenzaTipoCom_code")
	//@Index(name="IX_ScadenzaTipoCom_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ScadenzaTipoCom_sequence")
	@SequenceGenerator(name = "ScadenzaTipoCom_sequence", sequenceName = "ScadenzaTipoCom_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	 * Code Extension score
	 */
	private Integer score;

	@Column(name="score")
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
