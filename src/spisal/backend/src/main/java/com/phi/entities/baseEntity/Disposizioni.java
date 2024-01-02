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

import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;

@javax.persistence.Entity
@Table(name = "disposizioni")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Disposizioni extends BaseEntity {

	private static final long serialVersionUID = 612653582L;

	/**
	*  javadoc for motivoProvvSrvSn
	*/
	private List<CodeValuePhi> motivoProvvSrvSn;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="disposizioni_motivoprovvsrvsn", joinColumns = { @JoinColumn(name="Disposizioni_id") }, inverseJoinColumns = { @JoinColumn(name="motivoProvvSrvSn") })
	@ForeignKey(name="FK_motivoProvvSrvSn_Disposizioni", inverseName="FK_Disposizioni_motivoProvvSrvSn")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getMotivoProvvSrvSn(){
		return motivoProvvSrvSn;
	}

	public void setMotivoProvvSrvSn(List<CodeValuePhi> motivoProvvSrvSn){
		this.motivoProvvSrvSn = motivoProvvSrvSn;
	}

	/**
	*  javadoc for motivoProvv
	*/
	private List<CodeValuePhi> motivoProvv;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="disposizioni_motivoprovv", joinColumns = { @JoinColumn(name="Disposizioni_id") }, inverseJoinColumns = { @JoinColumn(name="motivoProvv") })
	@ForeignKey(name="FK_motivoProvv_Disposizioni", inverseName="FK_Disposizioni_motivoProvv")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getMotivoProvv(){
		return motivoProvv;
	}

	public void setMotivoProvv(List<CodeValuePhi> motivoProvv){
		this.motivoProvv = motivoProvv;
	}

	/**
	*  javadoc for riferimentiNormativi
	*/
	private List<CodeValuePhi> riferimentiNormativi;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="disposizioni_riferimentinormativi", joinColumns = { @JoinColumn(name="Disposizioni_id") }, inverseJoinColumns = { @JoinColumn(name="riferimentiNormativi") })
	@ForeignKey(name="FK_riferimentiNormativi_Disposizioni", inverseName="FK_Disposizioni_riferimentiNormativi")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getRiferimentiNormativi(){
		return riferimentiNormativi;
	}

	public void setRiferimentiNormativi(List<CodeValuePhi> riferimentiNormativi){
		this.riferimentiNormativi = riferimentiNormativi;
	}

	/**
	*  javadoc for esito
	*/
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_Disposizioni_esito")
	//@Index(name="IX_Disposizioni_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
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
	*  javadoc for riferimentoNormativo
	*/
	private CodeValuePhi riferimentoNormativo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimentoNormativo")
	@ForeignKey(name="FK_Disposizioni_riferimentoNormativo")
	//@Index(name="IX_Disposizioni_riferimentoNormativo")
	public CodeValuePhi getRiferimentoNormativo(){
		return riferimentoNormativo;
	}

	public void setRiferimentoNormativo(CodeValuePhi riferimentoNormativo){
		this.riferimentoNormativo = riferimentoNormativo;
	}


	/**
	*  javadoc for provvedimenti
	*/
	private Provvedimenti provvedimenti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="provvedimenti_id")
	@ForeignKey(name="FK_Disposizioni_provvedment")
	//@Index(name="IX_Disposizioni_provvedment")
	public Provvedimenti getProvvedimenti(){
		return provvedimenti;
	}

	public void setProvvedimenti(Provvedimenti provvedimenti){
		this.provvedimenti = provvedimenti;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Disposizioni_sequence")
	@SequenceGenerator(name = "Disposizioni_sequence", sequenceName = "Disposizioni_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
