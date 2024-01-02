package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
import javax.persistence.CascadeType;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.Formazione;
import com.phi.entities.baseEntity.Informazione;

@javax.persistence.Entity
@Table(name = "tipo_prod_finito")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TipoProdFinito extends BaseEntity {

	private static final long serialVersionUID = 834113717L;


	/**
	*  javadoc for informazione
	*/
	private Informazione informazione;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="informazione_id")
	@ForeignKey(name="FK_TipoProdFinto_nformazone")
	//@Index(name="IX_TipoProdFinto_nformazone")
	public Informazione getInformazione(){
		return informazione;
	}

	public void setInformazione(Informazione informazione){
		this.informazione = informazione;
	}



	/**
	*  javadoc for formazione
	*/
	private Formazione formazione;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="formazione_id")
	@ForeignKey(name="FK_TipoProdFinito_formazione")
	//@Index(name="IX_TipoProdFinito_formazione")
	public Formazione getFormazione(){
		return formazione;
	}

	public void setFormazione(Formazione formazione){
		this.formazione = formazione;
	}


	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for quantita
	*/
	private String quantita;

	@Column(name="quantita")
	public String getQuantita(){
		return quantita;
	}

	public void setQuantita(String quantita){
		this.quantita = quantita;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_TipoProdFinito_tipologia")
	//@Index(name="IX_TipoProdFinito_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TipoProdFinito_sequence")
	@SequenceGenerator(name = "TipoProdFinito_sequence", sequenceName = "TipoProdFinito_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
