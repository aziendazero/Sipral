package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
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

import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueInail;

@javax.persistence.Entity
@Table(name = "lavorazioni_correlate")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class LavorazioniCorrelate extends BaseEntity {

	private static final long serialVersionUID = 1383192760L;

	
	/**
	*  javadoc for inail
	*/
	private CodeValueInail inail;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="inail")
	@ForeignKey(name="FK_lavCorr_inail")
	//@Index(name="IX_lavCorr_inail")
	public CodeValueInail getInail(){
		return inail;
	}

	public void setInail(CodeValueInail inail){
		this.inail = inail;
	}

	/**
	*  javadoc for lavUnica
	*/
	private CodeValueAteco lavUnica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lavUnica")
	@ForeignKey(name="FK_lavCorr_lavUnica")
	//@Index(name="IX_lavCorr_lavUnica")
	public CodeValueAteco getLavUnica(){
		return lavUnica;
	}

	public void setLavUnica(CodeValueAteco lavUnica){
		this.lavUnica = lavUnica;
	}

	/**
	*  javadoc for schedaEsposti
	*/
	private SchedaEsposti schedaEsposti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="schedaEsp_id")
	@ForeignKey(name="FK_lavor_schedaEsp")
	//@Index(name="IX_lavor_schedaEsp")
	public SchedaEsposti getSchedaEsposti(){
		return schedaEsposti;
	}

	public void setSchedaEsposti(SchedaEsposti schedaEsposti){
		this.schedaEsposti = schedaEsposti;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "LavorazioniCorrelate_sequence")
	@SequenceGenerator(name = "LavorazioniCorrelate_sequence", sequenceName = "LavorazioniCorrelate_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
