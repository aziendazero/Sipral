package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.dataTypes.CodeValueLaw;

@javax.persistence.Entity
@Table(name = "miglioramenti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Miglioramenti extends BaseEntity {

	private static final long serialVersionUID = 1047578665L;

	/**
	*  javadoc for codeLegge81
	*/
	private CodeValueLaw codeLegge81;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codeLegge81")
	@ForeignKey(name="FK_Miglioramenti_codeLegge81")
	//@Index(name="IX_Miglioramenti_codeLegge81")
	public CodeValueLaw getCodeLegge81(){
		return codeLegge81;
	}

	public void setCodeLegge81(CodeValueLaw codeLegge81){
		this.codeLegge81 = codeLegge81;
	}


	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Miglioramenti_attivita")
	//@Index(name="IX_Miglioramenti_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}


	/**
	*  javadoc for dataVerifica
	*/
	private Date dataVerifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_verifica")
	public Date getDataVerifica(){
		return dataVerifica;
	}

	public void setDataVerifica(Date dataVerifica){
		this.dataVerifica = dataVerifica;
	}

	/**
	*  javadoc for giorni
	*/
	private Integer giorni;

	@Column(name="giorni")
	public Integer getGiorni(){
		return giorni;
	}

	public void setGiorni(Integer giorni){
		this.giorni = giorni;
	}

	/**
	*  javadoc for dataEmissione
	*/
	private Date dataEmissione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_emissione")
	public Date getDataEmissione(){
		return dataEmissione;
	}

	public void setDataEmissione(Date dataEmissione){
		this.dataEmissione = dataEmissione;
	}

	/**
	*  javadoc for dataScadenza
	*/
	private Date dataScadenza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_scadenza")
	public Date getDataScadenza(){
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza){
		this.dataScadenza = dataScadenza;
	}

	/**
	*  javadoc for esito
	*/
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_Miglioramenti_esito")
	//@Index(name="IX_Miglioramenti_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
	}

	/**
	*  javadoc for miglioramento
	*/
	private CodeValuePhi miglioramento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="miglioramento")
	@ForeignKey(name="FK_Miglioramenti_miglioramento")
	//@Index(name="IX_Miglioramenti_miglioramento")
	public CodeValuePhi getMiglioramento(){
		return miglioramento;
	}

	public void setMiglioramento(CodeValuePhi miglioramento){
		this.miglioramento = miglioramento;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note",length=4000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}


	/**
	*  javadoc for provvedimento
	*/
	private Provvedimenti provvedimento;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="provvedimento_id")
	@ForeignKey(name="FK_Miglorament_provvedmento")
	//@Index(name="IX_Miglorament_provvedmento")
	public Provvedimenti getProvvedimento(){
		return provvedimento;
	}

	public void setProvvedimento(Provvedimenti provvedimento){
		this.provvedimento = provvedimento;
	}



	/**
	*  javadoc for articolo
	*/
	private Articoli articolo;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="articolo_id")
	@ForeignKey(name="FK_Miglioramenti_articolo")
	//@Index(name="IX_Miglioramenti_articolo")
	public Articoli getArticolo(){
		return articolo;
	}

	public void setArticolo(Articoli articolo){
		this.articolo = articolo;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Miglioramenti_sequence")
	@SequenceGenerator(name = "Miglioramenti_sequence", sequenceName = "Miglioramenti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
