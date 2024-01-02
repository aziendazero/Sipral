package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;

/**
 * L'ORDER BY della NamedQuery AttivitaIstat.getAteco viene usata sempre per riprendere il primo record:
 * essa fa in modo di considerare per primi i record associati solo alla sede (ditta.internalId = null) 
 * e poi quelli associati alla ditta
 * In presenza di più attività Principali sulla stessa sede viene presa quella con internalId minore
 * La query viene usata sia per recuperare l'attività / comparto di una vigilanza, sia per impostarne il valore
 * nella colonna denormalizzata "comparto" presente in vigilanza, infortuni ecc..
 * @author u07646
 *
 */

@Entity
@Table(name = "attivita_istat")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@NamedQueries(value = {
		@NamedQuery(name = "AttivitaIstat.getAtecoSede", 
					query = "SELECT DISTINCT att.code FROM AttivitaIstat att" +
							" JOIN att.importanza imp" +
							" JOIN att.sedi sede" +
							" WHERE att.isActive = 1 AND imp.code = 'P'" +
							" AND sede.internalId = :sedeId" +
							" ORDER BY att.internalId ASC"),
		@NamedQuery(name = "AttivitaIstat.getAtecoDitta",
					query = " SELECT DISTINCT att.code FROM AttivitaIstat att" +
							" JOIN att.importanza imp" +
							" JOIN att.personeGiuridiche ditta" +
							" WHERE att.isActive = 1 AND imp.code = 'P'" +
							" AND ditta.internalId = :dittaId" +
							" ORDER BY att.internalId ASC")
	})
public class AttivitaIstat extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7008462601615307096L;


	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_ttvtaIstt_personeGurdche")
	//@Index(name="IX_ttvtaIstt_personeGurdche")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}



	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_AttivitaIstat_sedi")
	//@Index(name="IX_AttivitaIstat_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}





	/**
	*  javadoc for cFonte
	*/
	private CodeValuePhi cfonte;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cFonte")
	@ForeignKey(name="FK_AttivitaIstat_cFonte")
	//@Index(name="IX_AttivitaIstat_cFonte")
	public CodeValuePhi getCfonte(){
		return cfonte;
	}

	public void setCfonte(CodeValuePhi cfonte){
		this.cfonte = cfonte;
	}

	/**
	*  javadoc for lavUnica
	*/
	private CodeValueAteco code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_attIstat_ateco")
	//@Index(name="IX_attIstat_ateco")
	public CodeValueAteco getCode(){
		return code;
	}

	public void setCode(CodeValueAteco code){
		this.code = code;
	}


	/**
	*  javadoc for internalId
	*/
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Attistat_sequence")
	@SequenceGenerator(name = "Attistat_sequence", sequenceName = "Attistat_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	 * javadoc for Attivitaa
	 */
	private CodeValue attivita;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "ATTIVITA")
	@ForeignKey(name = "FK_Attivita")
	//@Index(name="IX_Attivita")
	public CodeValue getAttivita() {
		return attivita;
	}

	public void setAttivita(CodeValue attivita) {
		this.attivita = attivita;
	}
	
	/**
	*  javadoc for Fonte
	*/
	private String fonte;

	@Column(name="FONTE")
	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}
	
	/**
	*  javadoc for Descrizione
	*/
	private String descrizione;

	@Column(name="DESCRIZIONE")
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	/**
	 * javadoc for Importanza (P/S/I)
	 */
	private CodeValue importanza;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="IMPORTANZA")
	@ForeignKey(name="FK_Importanza")
	//@Index(name="IX_Importanza")
	public CodeValue getImportanza() {
		return importanza;
	}
	
	public void setImportanza(CodeValue importanza) {
	this.importanza = importanza;
	}
	
	/**
	*  javadoc for Data inizio attivita
	*/
	private Date dataInizioAttivita;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_INIZIO_ATTIVITA")
	public Date getDataInizioAttivita() {
		return dataInizioAttivita;
	}

	public void setDataInizioAttivita(Date dataInizioAttivita) {
		this.dataInizioAttivita = dataInizioAttivita;
	}

}
