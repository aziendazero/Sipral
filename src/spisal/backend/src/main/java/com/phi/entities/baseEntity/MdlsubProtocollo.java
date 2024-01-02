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

@javax.persistence.Entity
@Table(name = "mdlsub_prot")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MdlsubProtocollo extends BaseEntity {

	private static final long serialVersionUID = 548120844L;

	/**
	*  javadoc for codiceSottotipo
	*/
	private String codiceSottotipo;

	@Column(name="codice_subtype")
	public String getCodiceSottotipo(){
		return codiceSottotipo;
	}

	public void setCodiceSottotipo(String codiceSottotipo){
		this.codiceSottotipo = codiceSottotipo;
	}

	/**
	*  javadoc for codiceProtocollo
	*/
	private String codiceProtocollo;

	@Column(name="codice_prot")
	public String getCodiceProtocollo(){
		return codiceProtocollo;
	}

	public void setCodiceProtocollo(String codiceProtocollo){
		this.codiceProtocollo = codiceProtocollo;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MdlsubProtocollo_sequence")
	@SequenceGenerator(name = "MdlsubProtocollo_sequence", sequenceName = "MdlsubProtocollo_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
