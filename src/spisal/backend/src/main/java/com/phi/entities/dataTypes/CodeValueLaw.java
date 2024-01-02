package com.phi.entities.dataTypes;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.annotations.CodeValueExtension;

@Entity(name="CodeValueLaw")
@Table(name="code_value_law", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_law", indexes = { @Index(name="IX_Code_Value_Law", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class CodeValueLaw extends CodeValue implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 732727337497521375L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueLaw.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueLaw_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueLaw.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * Extension comma
	 */
	private String comma;

	@Column(name="comma")
	@CodeValueExtension(name="Comma")
	public String getComma() {
		return comma;
	}

	public void setComma(String comma) {
		this.comma = comma;
	}
	
	/**
	 * Extension lettera
	 */
	private String lettera;

	@Column(name="lettera")
	@CodeValueExtension(name="Lettera")
	public String getLettera() {
		return lettera;
	}

	public void setLettera(String lettera) {
		this.lettera = lettera;
	}
	
	/**
	 * Extension numero
	 */
	private String numero;

	@Column(name="numero")
	@CodeValueExtension(name="Numero")
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	/**
	 * Extension codice contravventore
	 */
	private String codiceContravventore;

	@Column(name="codice_contravventore")
	@CodeValueExtension(name="CodiceContravventore")
	public String getCodiceContravventore() {
		return codiceContravventore;
	}

	public void setCodiceContravventore(String codiceContravventore) {
		this.codiceContravventore = codiceContravventore;
	}
	
	/**
	 * Extension descrizione contravventore
	 */
	private String descrizioneContravventore;

	@Column(name="descrizione_contravventore")
	@CodeValueExtension(name="DescrizioneContravventore")
	public String getDescrizioneContravventore() {
		return descrizioneContravventore;
	}

	public void setDescrizioneContravventore(String descrizioneContravventore) {
		this.descrizioneContravventore = descrizioneContravventore;
	}
	
	/**
	 * Extension nota contravventore
	 */
	private String notaContravventore;

	@Column(name="nota_contravventore")
	@CodeValueExtension(name="NotaContravventore")
	public String getNotaContravventore() {
		return notaContravventore;
	}

	public void setNotaContravventore(String notaContravventore) {
		this.notaContravventore = notaContravventore;
	}
	
	/**
	 * Extension corpo
	 */
	private String corpo;

	@Column(name="corpo", length = 4000)
	@CodeValueExtension(name="Corpo")
	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}
	
	/**
	 * Extension sanzione
	 */
	private String sanzione;

	@Column(name="sanzione")
	@CodeValueExtension(name="Sanzione")
	public String getSanzione() {
		return sanzione;
	}

	public void setSanzione(String sanzione) {
		this.sanzione = sanzione;
	}
	
	/**
	 * Extension sanzionato da
	 */
	private String sanzionatoDa;

	@Column(name="sanzionato_da")
	@CodeValueExtension(name="SanzionatoDa")
	public String getSanzionatoDa() {
		return sanzionatoDa;
	}

	public void setSanzionatoDa(String sanzionatoDa) {
		this.sanzionatoDa = sanzionatoDa;
	}
		
	/**
	 * Extension nota prescrizione
	 */
	private String notaPrescrizione;

	@Column(name="nota_prescrizione")
	@CodeValueExtension(name="NotaPrescrizione")
	public String getNotaPrescrizione() {
		return notaPrescrizione;
	}

	public void setNotaPrescrizione(String notaPrescrizione) {
		this.notaPrescrizione = notaPrescrizione;
	}
	
	/**
	 * Extension nota violazione
	 */
	private String notaViolazione;

	@Column(name="nota_violazione")
	@CodeValueExtension(name="NotaViolazione")
	public String getNotaViolazione() {
		return notaViolazione;
	}

	public void setNotaViolazione(String notaViolazione) {
		this.notaViolazione = notaViolazione;
	}
	
	/**
	 * Extension classificazione
	 */
	private String classificazione;

	@Column(name="classificazione")
	@CodeValueExtension(name="Classificazione")
	public String getClassificazione() {
		return classificazione;
	}

	public void setClassificazione(String classificazione) {
		this.classificazione = classificazione;
	}
	
	/**
	 * Extension arresto
	 */
	private String arresto;

	@Column(name="arresto")
	@CodeValueExtension(name="Arresto")
	public String getArresto() {
		return arresto;
	}

	public void setArresto(String arresto) {
		this.arresto = arresto;
	}
	
	/**
	 * Extension importo min
	 */
	private String importoMin;

	@Column(name="importo_min")
	@CodeValueExtension(name="ImportoMin")
	public String getImportoMin() {
		return importoMin;
	}

	public void setImportoMin(String importoMin) {
		this.importoMin = importoMin;
	}
	
	/**
	 * Extension importo max
	 */
	private String importoMax;

	@Column(name="importo_max")
	@CodeValueExtension(name="ImportoMax")
	public String getImportoMax() {
		return importoMax;
	}

	public void setImportoMax(String importoMax) {
		this.importoMax = importoMax;
	}
}
