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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "comparto_ateco")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries(value = {
		@NamedQuery(name = "CompartoAteco.getCompartoOrSpecificazione", 
					query = "SELECT ca FROM CompartoAteco ca WHERE ca.ateco = :cvAteco")
	})
public class CompartoAteco extends BaseEntity {

	private static final long serialVersionUID = 1722498710L;

	/**
	*  javadoc for ateco
	*/
	private CodeValueAteco ateco;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ateco")
	@ForeignKey(name="FK_CompartoAteco_ateco")
	//@Index(name="IX_CompartoAteco_ateco")
	public CodeValueAteco getAteco(){
		return ateco;
	}

	public void setAteco(CodeValueAteco ateco){
		this.ateco = ateco;
	}

	/**
	*  javadoc for comparto
	*/
	private CodeValuePhi comparto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comparto")
	@ForeignKey(name="FK_CompartoAteco_comparto")
	//@Index(name="IX_CompartoAteco_comparto")
	public CodeValuePhi getComparto(){
		return comparto;
	}

	public void setComparto(CodeValuePhi comparto){
		this.comparto = comparto;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CompartoAteco_sequence")
	@SequenceGenerator(name = "CompartoAteco_sequence", sequenceName = "CompartoAteco_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
