package com.phi.entities.baseEntity;

import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.TEL;


@javax.persistence.Entity
@Table(name = "administrative_org")
@Audited
/**
 * Entità Organizzazione - utilizzata nel progetto SSA - AAS
 * @author 510087
 *
 */
public class AdministrativeOrg extends BaseEntity {

	private static final long serialVersionUID = 1874678428L;
	
	/**
	*  Direzioni sanitarie
	*/
	private List<CodeValuePhi> direzioni;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="org_direzioni", joinColumns = { @JoinColumn(name="org_id") }, inverseJoinColumns = { @JoinColumn(name="direzioni") })
	@ForeignKey(name="FK_direzioni_org", inverseName="FK_org_direzioni")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getDirezioni(){
		return direzioni;
	}

	public void setDirezioni(List<CodeValuePhi> direzioni){
		this.direzioni = direzioni;
	}

	//methods needed for BaseEntity extension

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name="FK_adminOrg_sdloc")
	@Index(name="IX_adminOrg_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AdministrativeOrg_sequence")
	@SequenceGenerator(name = "AdministrativeOrg_sequence", sequenceName = "AdministrativeOrg_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	 *  idSSA
	 */
	private String idSSA;

	@Column(name="id_ssa")
	public String getIdSSA(){
		return idSSA;
	}

	public void setIdSSA(String idSSA){
		this.idSSA = idSSA;
	}
	
	/**
	 *  idAAS
	 */
	private String idAAS;

	@Column(name="id_aas")
	public String getIdAAS(){
		return idAAS;
	}

	public void setIdAAS(String idAAS){
		this.idAAS = idAAS;
	}
	
	/**
	 *  Natura giuridica
	 */
	private CodeValuePhi nature;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nature")
	@ForeignKey(name="FK_AdministrativeOrg_nature")
	@Index(name="IX_AdministrativeOrg_nature")
	public CodeValuePhi getNature(){
		return nature;
	}

	public void setNature(CodeValuePhi nature){
		this.nature = nature;
	}


	/**
	 *  Propriet�
	 */
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_AdministrativeOrg_type")
	@Index(name="IX_AdministrativeOrg_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	 *  Partita IVA
	 */
	private String piva;

	@Column(name="piva")
	public String getPiva(){
		return piva;
	}

	public void setPiva(String piva){
		this.piva = piva;
	}

	/**
	 *  Codice fiscale
	 */
	private String fiscalCode;

	@Column(name="fiscal_code")
	public String getFiscalCode(){
		return fiscalCode;
	}

	public void setFiscalCode(String fiscalCode){
		this.fiscalCode = fiscalCode;
	}

	/**
	 *  Ente gestore
	 */
	private boolean institution=false;

	@Column(name="institution")
	public boolean getInstitution(){
		return institution;
	}

	public void setInstitution(boolean institution){
		this.institution = institution;
	}

	/**
	 *  Domanda concordato
	 */
	private boolean question=false;

	@Column(name="question")
	public boolean getQuestion(){
		return question;
	}

	public void setQuestion(boolean question){
		this.question = question;
	}

	/**
	 *  Liquidazione / fallimento
	 */
	private boolean bankrupt=false;

	@Column(name="bankrupt")
	public boolean getBankrupt(){
		return bankrupt;
	}

	public void setBankrupt(boolean bankrupt){
		this.bankrupt = bankrupt;
	}

	/**
	 *  Iscrizione registro convenzionamento
	 */
	private boolean entryInRegister=false;

	@Column(name="entry_in_register")
	public boolean getEntryInRegister(){
		return entryInRegister;
	}

	public void setEntryInRegister(boolean entryInRegister){
		this.entryInRegister = entryInRegister;
	}

	/**
	 *  Onlus
	 */
	private boolean onlus=false;

	@Column(name="onlus")
	public boolean getOnlus(){
		return onlus;
	}

	public void setOnlus(boolean onlus){
		this.onlus = onlus;
	}

	/**
	 *  Personalit� giuridica
	 */
	private boolean legalPers=false;

	@Column(name="legal_pers")
	public boolean getLegalPers(){
		return legalPers;
	}

	public void setLegalPers(boolean legalPers){
		this.legalPers = legalPers;
	}

	/**
	 *  Anno cessazione
	 */
	private Integer yearStop;

	@Column(name="year_stop")
	public Integer getYearStop(){
		return yearStop;
	}

	public void setYearStop(Integer yearStop){
		this.yearStop = yearStop;
	}

	/**
	 *  Anno costituzione
	 */
	private Integer yearStart;

	@Column(name="year_start")
	public Integer getYearStart(){
		return yearStart;
	}

	public void setYearStart(Integer yearStart){
		this.yearStart = yearStart;
	}

	/**
	 *  Contatti sede legale
	 */
	private TEL telecom;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telecom_as")),
		@AttributeOverride(name="bad", column=@Column(name="telecom_bad")),
		@AttributeOverride(name="dir", column=@Column(name="telecom_dir")),
		@AttributeOverride(name="ec", column=@Column(name="telecom_ec")),
		@AttributeOverride(name="h", column=@Column(name="telecom_h")),
		@AttributeOverride(name="hp", column=@Column(name="telecom_hp")),
		@AttributeOverride(name="hv", column=@Column(name="telecom_hv")),
		@AttributeOverride(name="mail", column=@Column(name="telecom_mail")),
		@AttributeOverride(name="mc", column=@Column(name="telecom_mc")),
		@AttributeOverride(name="pg", column=@Column(name="telecom_pg")),
		@AttributeOverride(name="pub", column=@Column(name="telecom_pub")),
		@AttributeOverride(name="sip", column=@Column(name="telecom_sip")),
		@AttributeOverride(name="tmp", column=@Column(name="telecom_tmp")),
		@AttributeOverride(name="wp", column=@Column(name="telecom_wp"))
	})
	public TEL getTelecom() {
		return telecom;
	}

	public void setTelecom(TEL telecom) {
		this.telecom = telecom;
	}

	/**
	 *  Indirizzo sede legale
	 */
	private AD addr;

	@Embedded
	@AttributeOverrides({
	@AttributeOverride(name="adl", column=@Column(name="addr_adl")),
	@AttributeOverride(name="bnr", column=@Column(name="addr_bnr")),
	@AttributeOverride(name="cen", column=@Column(name="addr_cen")),
	@AttributeOverride(name="cnt", column=@Column(name="addr_cnt")),
	@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),
	@AttributeOverride(name="cty", column=@Column(name="addr_cty")),
	@AttributeOverride(name="sta", column=@Column(name="addr_sta")),
	@AttributeOverride(name="stb", column=@Column(name="addr_stb")),
	@AttributeOverride(name="str", column=@Column(name="addr_str")),
	@AttributeOverride(name="zip", column=@Column(name="addr_zip"))
	})
	public AD getAddr() {
		return addr;
	}

	public void setAddr(AD addr) {
		this.addr = addr;
	}

	/**
	 *  Contatti sede amministrativa
	 */
	private TEL telecomAmm;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telecomAmm_as")),
		@AttributeOverride(name="bad", column=@Column(name="telecomAmm_bad")),
		@AttributeOverride(name="dir", column=@Column(name="telecomAmm_dir")),
		@AttributeOverride(name="ec", column=@Column(name="telecomAmm_ec")),
		@AttributeOverride(name="h", column=@Column(name="telecomAmm_h")),
		@AttributeOverride(name="hp", column=@Column(name="telecomAmm_hp")),
		@AttributeOverride(name="hv", column=@Column(name="telecomAmm_hv")),
		@AttributeOverride(name="mail", column=@Column(name="telecomAmm_mail")),
		@AttributeOverride(name="mc", column=@Column(name="telecomAmm_mc")),
		@AttributeOverride(name="pg", column=@Column(name="telecomAmm_pg")),
		@AttributeOverride(name="pub", column=@Column(name="telecomAmm_pub")),
		@AttributeOverride(name="sip", column=@Column(name="telecomAmm_sip")),
		@AttributeOverride(name="tmp", column=@Column(name="telecomAmm_tmp")),
		@AttributeOverride(name="wp", column=@Column(name="telecomAmm_wp"))
	})
	public TEL getTelecomAmm(){
		return telecomAmm;
	}

	public void setTelecomAmm(TEL telecomAmm){
		this.telecomAmm = telecomAmm;
	}

	/**
	 *  Indirizzo sede amministrativa
	 */
	private AD addrAmm;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addrAmm_code"))
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="addrAmm_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="addrAmm_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="addrAmm_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="addrAmm_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="addrAmm_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="addrAmm_cty")),
		@AttributeOverride(name="sta", column=@Column(name="addrAmm_sta")),
		@AttributeOverride(name="stb", column=@Column(name="addrAmm_stb")),
		@AttributeOverride(name="str", column=@Column(name="addrAmm_str")),
		@AttributeOverride(name="zip", column=@Column(name="addrAmm_zip"))
	})
	public AD getAddrAmm(){
		return addrAmm;
	}

	public void setAddrAmm(AD addrAmm){
		this.addrAmm = addrAmm;
	}

	
	/**
	 *  Contatti altro recapito
	 */
	private TEL telecomOther;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telecomOther_as")),
		@AttributeOverride(name="bad", column=@Column(name="telecomOther_bad")),
		@AttributeOverride(name="dir", column=@Column(name="telecomOther_dir")),
		@AttributeOverride(name="ec", column=@Column(name="telecomOther_ec")),
		@AttributeOverride(name="h", column=@Column(name="telecomOther_h")),
		@AttributeOverride(name="hp", column=@Column(name="telecomOther_hp")),
		@AttributeOverride(name="hv", column=@Column(name="telecomOther_hv")),
		@AttributeOverride(name="mail", column=@Column(name="telecomOther_mail")),
		@AttributeOverride(name="mc", column=@Column(name="telecomOther_mc")),
		@AttributeOverride(name="pg", column=@Column(name="telecomOther_pg")),
		@AttributeOverride(name="pub", column=@Column(name="telecomOther_pub")),
		@AttributeOverride(name="sip", column=@Column(name="telecomOther_sip")),
		@AttributeOverride(name="tmp", column=@Column(name="telecomOther_tmp")),
		@AttributeOverride(name="wp", column=@Column(name="telecomOther_wp"))
	})
	public TEL getTelecomOther(){
		return telecomOther;
	}

	public void setTelecomOther(TEL telecomOther){
		this.telecomOther = telecomOther;
	}

	/**
	 *  Indirizzo altro recapito
	 */
	private AD addrOther;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addrOther_code"))
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="addrOther_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="addrOther_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="addrOther_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="addrOther_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="addrOther_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="addrOther_cty")),
		@AttributeOverride(name="sta", column=@Column(name="addrOther_sta")),
		@AttributeOverride(name="stb", column=@Column(name="addrOther_stb")),
		@AttributeOverride(name="str", column=@Column(name="addrOther_str")),
		@AttributeOverride(name="zip", column=@Column(name="addrOther_zip"))
	})
	public AD getAddrOther(){
		return addrOther;
	}

	public void setAddrOther(AD addrOther){
		this.addrOther = addrOther;
	}
	
	/**
	 *  Denominazione Organizzazione
	 */
	private String name;

	@Column(name="name")
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	/**
	 *  Identificativo dell'Organizzazione - valorizzato utilizzando la classe Identifiers di SSA
	 */
	private String orgid;

	@Column(name="orgid")
	public String getOrgid(){
		return orgid;
	}

	public void setOrgid(String orgid){
		this.orgid = orgid;
	}
	
	/**
	 *  Referente dell'Organizzazione
	 */
	private String refName;

	@Column(name="refname")
	public String getRefName(){
		return refName;
	}

	public void setRefName(String refName){
		this.refName = refName;
	}
	
	/**
	 *  Contatti referente Organizzazione
	 */
	private TEL telecomRef;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telecomRef_as")),
		@AttributeOverride(name="bad", column=@Column(name="telecomRef_bad")),
		@AttributeOverride(name="dir", column=@Column(name="telecomRef_dir")),
		@AttributeOverride(name="ec", column=@Column(name="telecomRef_ec")),
		@AttributeOverride(name="h", column=@Column(name="telecomRef_h")),
		@AttributeOverride(name="hp", column=@Column(name="telecomRef_hp")),
		@AttributeOverride(name="hv", column=@Column(name="telecomRef_hv")),
		@AttributeOverride(name="mail", column=@Column(name="telecomRef_mail")),
		@AttributeOverride(name="mc", column=@Column(name="telecomRef_mc")),
		@AttributeOverride(name="pg", column=@Column(name="telecomRef_pg")),
		@AttributeOverride(name="pub", column=@Column(name="telecomRef_pub")),
		@AttributeOverride(name="sip", column=@Column(name="telecomRef_sip")),
		@AttributeOverride(name="tmp", column=@Column(name="telecomRef_tmp")),
		@AttributeOverride(name="wp", column=@Column(name="telecomRef_wp"))
	})
	public TEL getTelecomRef(){
		return telecomRef;
	}

	public void setTelecomRef(TEL telecomRef){
		this.telecomRef = telecomRef;
	}

	/**
	 *  Data inizio validit�
	 */
	private Date startValidityOrig;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity_orig")
	public Date getStartValidityOrig(){
		return startValidityOrig;
	}

	public void setStartValidityOrig(Date startValidityOrig){
		this.startValidityOrig = startValidityOrig;
	}
	
	/**
	 *  Data inizio validit�
	 */
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}

	/**
	 *  Data fine validit�
	 */
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}
}
