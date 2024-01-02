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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.Sedi;

@Entity
@Table(name = "ruoli")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Ruoli extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1811130948448513944L;

	/**
	*  javadoc for categoria
	*/
	private String categoria;

	@Column(name="categoria")
	public String getCategoria(){
		return categoria;
	}

	public void setCategoria(String categoria){
		this.categoria = categoria;
	}

	/**
	*  javadoc for forma
	*/
	private String forma;

	@Column(name="forma")
	public String getForma(){
		return forma;
	}

	public void setForma(String forma){
		this.forma = forma;
	}

	/**
	*  javadoc for ultDescrizione
	*/
	private String ultDescrizione;

	@Column(name="ult_descrizione")
	public String getUltDescrizione(){
		return ultDescrizione;
	}

	public void setUltDescrizione(String ultDescrizione){
		this.ultDescrizione = ultDescrizione;
	}

	/**
	*  javadoc for descrizione
	*/
	private String descrizione;

	@Column(name="descrizione")
	public String getDescrizione(){
		return descrizione;
	}

	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}

	/**
	*  javadoc for altroRuolo
	*/
	private CodeValuePhi altroRuolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="altroRuolo")
	@ForeignKey(name="FK_Ruoli_altroRuolo")
	//@Index(name="IX_Ruoli_altroRuolo")
	public CodeValuePhi getAltroRuolo(){
		return altroRuolo;
	}

	public void setAltroRuolo(CodeValuePhi altroRuolo){
		this.altroRuolo = altroRuolo;
	}

	/**
	*  javadoc for volume
	*/
	private String volume;

	@Column(name="volume")
	public String getVolume(){
		return volume;
	}

	public void setVolume(String volume){
		this.volume = volume;
	}

	/**
	*  javadoc for dataDenuncia
	*/
	private Date dataDenuncia;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_denuncia")
	public Date getDataDenuncia(){
		return dataDenuncia;
	}

	public void setDataDenuncia(Date dataDenuncia){
		this.dataDenuncia = dataDenuncia;
	}

	/**
	*  javadoc for fascia
	*/
	private String fascia;

	@Column(name="fascia")
	public String getFascia(){
		return fascia;
	}

	public void setFascia(String fascia){
		this.fascia = fascia;
	}

	/**
	*  javadoc for qualifica
	*/
	private String qualifica;

	@Column(name="qualifica")
	public String getQualifica(){
		return qualifica;
	}

	public void setQualifica(String qualifica){
		this.qualifica = qualifica;
	}

	/**
	*  javadoc for enteRilascio
	*/
	private String enteRilascio;

	@Column(name="ente_rilascio")
	public String getEnteRilascio(){
		return enteRilascio;
	}

	public void setEnteRilascio(String enteRilascio){
		this.enteRilascio = enteRilascio;
	}

	/**
	*  javadoc for dataIscrizione
	*/
	private Date dataIscrizione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_iscrizione")
	public Date getDataIscrizione(){
		return dataIscrizione;
	}

	public void setDataIscrizione(Date dataIscrizione){
		this.dataIscrizione = dataIscrizione;
	}

	/**
	*  javadoc for numero
	*/
	private String numero;

	@Column(name="numero")
	public String getNumero(){
		return numero;
	}

	public void setNumero(String numero){
		this.numero = numero;
	}

	/**
	*  javadoc for provincia
	*/
	private String provincia;

	@Column(name="provincia")
	public String getProvincia(){
		return provincia;
	}

	public void setProvincia(String provincia){
		this.provincia = provincia;
	}

	/**
	*  javadoc for lettera
	*/
	private CodeValuePhi lettera;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lettera")
	@ForeignKey(name="FK_Ruoli_lettera")
	//@Index(name="IX_Ruoli_lettera")
	public CodeValuePhi getLettera(){
		return lettera;
	}

	public void setLettera(CodeValuePhi lettera){
		this.lettera = lettera;
	}


	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_Ruoli_sedi")
	//@Index(name="IX_Ruoli_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}


	/**
	*  javadoc for internalId
	*/
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Ruoli_sequence")
	@SequenceGenerator(name = "Ruoli_sequence", sequenceName = "Ruoli_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	 * javadoc for Code
	 */
	private CodeValue code;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "CODE")
	@ForeignKey(name = "FK_Code")
	//@Index(name="IX_Code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	/**
	 * javadoc for Tipo
	 */
	private CodeValue tipo;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "TIPO")
	@ForeignKey(name = "FK_Tipo")
	//@Index(name="IX_Tipo")
	public CodeValue getTipo() {
		return tipo;
	}

	public void setTipo(CodeValue tipo) {
		this.tipo = tipo;
	}
	
	/**
	*  javadoc for Causale cessazione
	*/
	private String causaleCessazione;

	@Column(name="CAUSALE_CESSAZIONE")
	public String getCausaleCessazione() {
		return causaleCessazione;
	}

	public void setCausaleCessazione(String causaleCessazione) {
		this.causaleCessazione = causaleCessazione;
	}
	
	/**
	*  javadoc for Data domanda cessazione
	*/
	private Date dataDomandaCessazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_DOMANDA_CESSAZIONE")
	public Date getDataDomandaCessazione() {
		return dataDomandaCessazione;
	}

	public void setDataDomandaCessazione(Date dataDomandaCessazione) {
		this.dataDomandaCessazione = dataDomandaCessazione;
	}
	
	/**
	*  javadoc for Data delibera cessazione
	*/
	private Date dataDeliberaCessazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_DELIBERA_CESSAZIONE")
	public Date getDataDeliberaCessazione() {
		return dataDeliberaCessazione;
	}

	public void setDataDeliberaCessazione(Date dataDeliberaCessazione) {
		this.dataDeliberaCessazione = dataDeliberaCessazione;
	}
	
	/**
	*  javadoc for Data delibera cessazione
	*/
	private Date dataCessazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_CESSAZIONE")
	public Date getDataCessazione() {
		return dataCessazione;
	}

	public void setDataCessazione(Date dataCessazione) {
		this.dataCessazione = dataCessazione;
	}


}
