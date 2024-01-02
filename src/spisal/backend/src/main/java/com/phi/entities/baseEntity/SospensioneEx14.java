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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.phi.cs.datamodel.IdataModel;
import com.phi.entities.actions.ArticoliAction;
import com.phi.entities.actions.PersoneArticoliAction;
import com.phi.entities.actions.SospensioneEx14Action;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import org.hibernate.annotations.Index;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
@javax.persistence.Entity
@Table(name = "sosp_ex14")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SospensioneEx14 extends BaseEntity {

	private static final long serialVersionUID = 472992775L;

	/**
	*  javadoc for comAutGiud
	*/
	private Boolean comAutGiud;

	@Column(name="com_aut_giud")
	public Boolean getComAutGiud(){
		return comAutGiud;
	}

	public void setComAutGiud(Boolean comAutGiud){
		this.comAutGiud = comAutGiud;
	}

	/**
	*  javadoc for dateDecorRevoca
	*/
	private Date dateDecorRevoca;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_dec_revoca")
	public Date getDateDecorRevoca(){
		return dateDecorRevoca;
	}

	public void setDateDecorRevoca(Date dateDecorRevoca){
		this.dateDecorRevoca = dateDecorRevoca;
	}

	/**
	*  javadoc for dateRevoca
	*/
	private Date dateRevoca;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_revoca")
	public Date getDateRevoca(){
		return dateRevoca;
	}

	public void setDateRevoca(Date dateRevoca){
		this.dateRevoca = dateRevoca;
	}

	/**
	*  javadoc for esitoD
	*/
	private Boolean esitoD;

	@Column(name="esito_d")
	public Boolean getEsitoD(){
		return esitoD;
	}

	public void setEsitoD(Boolean esitoD){
		this.esitoD = esitoD;
	}

	/**
	*  javadoc for esitoC
	*/
	private Boolean esitoC;

	@Column(name="esito_c")
	public Boolean getEsitoC(){
		return esitoC;
	}

	public void setEsitoC(Boolean esitoC){
		this.esitoC = esitoC;
	}

	/**
	*  javadoc for esitoB
	*/
	private Boolean esitoB;

	@Column(name="esito_b")
	public Boolean getEsitoB(){
		return esitoB;
	}

	public void setEsitoB(Boolean esitoB){
		this.esitoB = esitoB;
	}

	/**
	*  javadoc for esitoA
	*/
	private Boolean esitoA;

	@Column(name="esito_a")
	public Boolean getEsitoA(){
		return esitoA;
	}

	public void setEsitoA(Boolean esitoA){
		this.esitoA = esitoA;
	}

	/**
	*  javadoc for modPagamento
	*/
	private CodeValuePhi modPagamento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="modPagamento")
	@ForeignKey(name="FK_Sosp_modPagamento")
	@Index(name="IX_Sosp_modPagamento")
	public CodeValuePhi getModPagamento(){
		return modPagamento;
	}

	public void setModPagamento(CodeValuePhi modPagamento){
		this.modPagamento = modPagamento;
	}

	/**
	*  javadoc for decorrenzaHHMM
	*/
	private Date decorrenzaHHMM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="decorrenza_hhmm")
	public Date getDecorrenzaHHMM(){
		return decorrenzaHHMM;
	}

	public void setDecorrenzaHHMM(Date decorrenzaHHMM){
		this.decorrenzaHHMM = decorrenzaHHMM;
	}

	/**
	*  javadoc for decorrenzaTimestamp
	*/
	private Date decorrenzaTimestamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="decorrenza_ts")
	public Date getDecorrenzaTimestamp(){
		return decorrenzaTimestamp;
	}

	public void setDecorrenzaTimestamp(Date decorrenzaTimestamp){
		this.decorrenzaTimestamp = decorrenzaTimestamp;
	}

	/**
	*  javadoc for decorrenzaABC
	*/
	private String decorrenzaABC;

	@Column(name="decorrenza_abc")
	public String getDecorrenzaABC(){
		return decorrenzaABC;
	}

	public void setDecorrenzaABC(String decorrenzaABC){
		this.decorrenzaABC = decorrenzaABC;
	}


	/**
	*  javadoc for provvedimenti
	*/
	private Provvedimenti provvedimenti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="provvedimenti_id")
	@ForeignKey(name="FK_SospEx14_provv")
	//@Index(name="IX_SospEx14_provv")
	public Provvedimenti getProvvedimenti(){
		return provvedimenti;
	}

	public void setProvvedimenti(Provvedimenti provvedimenti){
		this.provvedimenti = provvedimenti;
	}


	/**
	*  javadoc for motivoRaddoppio
	*/
	private String motivoRaddoppio;

	@Column(name="mot_raddoppio")
	public String getMotivoRaddoppio(){
		return motivoRaddoppio;
	}

	public void setMotivoRaddoppio(String motivoRaddoppio){
		this.motivoRaddoppio = motivoRaddoppio;
	}

	/**
	*  javadoc for aggiornaImporto
	*/
	private Boolean aggiornaImporto;

	@Column(name="agg_importo")
	public Boolean getAggiornaImporto(){
		return aggiornaImporto;
	}

	public void setAggiornaImporto(Boolean aggiornaImporto){
		this.aggiornaImporto = aggiornaImporto;
	}

	@Transient
	public boolean getIsImmediata(){
		return Boolean.TRUE.equals((Boolean) SospensioneEx14Action.instance().getTemporary().get("A"));
	}

	@Transient
	public boolean getIsCessazione(){
		return Boolean.TRUE.equals((Boolean) SospensioneEx14Action.instance().getTemporary().get("B"));
	}

	@Transient
	public boolean getIsOre(){
		return Boolean.TRUE.equals((Boolean) SospensioneEx14Action.instance().getTemporary().get("C"));
	}

	@Transient
	public Date getPrintDecorrenzaHhMm(){
		return (Date) SospensioneEx14Action.instance().getTemporary().get("decorrenzaHHMM");
	}

	@Transient
	public Date getPrintDecorrenzaB(){
		return (Date) SospensioneEx14Action.instance().getTemporary().get("decorrenzaB");
	}

	@Transient
	public Date getPrintDecorrenzaC(){
		return (Date) SospensioneEx14Action.instance().getTemporary().get("decorrenzaC");
	}

	@SuppressWarnings("unchecked")
	@Transient
	public String getPrintImportoFinale(){
		ArticoliAction artAction = ArticoliAction.instance();
		Context conversationContext = Contexts.getConversationContext();
		List<Articoli> articoli = (List<Articoli>) ((IdataModel<Articoli>) conversationContext.get("ArticoliList")).getFullList();
		return String.format("%.2f", artAction.getImportoTotaleSosp(articoli,(Boolean) SospensioneEx14Action.instance().getTemporary().get("aggImporto")));
	}

	@SuppressWarnings("unchecked")
	@Transient
	public String getPrintImportoDovuto(){
		ArticoliAction artAction = ArticoliAction.instance();
		Context conversationContext = Contexts.getConversationContext();
		List<Articoli> articoli = (List<Articoli>) ((IdataModel<Articoli>) conversationContext.get("ArticoliList")).getFullList();
		return String.format("%.2f", artAction.getImportoDovutoSosp(articoli,(Boolean) SospensioneEx14Action.instance().getTemporary().get("aggImporto")));
	}

	@SuppressWarnings("unchecked")
	@Transient
	public String getPrintImportoResiduo(){
		ArticoliAction artAction = ArticoliAction.instance();
		Context conversationContext = Contexts.getConversationContext();
		List<Articoli> articoli = (List<Articoli>) ((IdataModel<Articoli>) conversationContext.get("ArticoliList")).getFullList();
		return String.format("%.2f", artAction.getImportoResiduoSosp(articoli,(Boolean) SospensioneEx14Action.instance().getTemporary().get("aggImporto")));
	}
	
	@SuppressWarnings("unchecked")
	@Transient
	public String getPrintImportoSanzione(){
		ArticoliAction artAction = ArticoliAction.instance();
		Context conversationContext = Contexts.getConversationContext();
		List<Articoli> articoli = (List<Articoli>) ((IdataModel<Articoli>) conversationContext.get("ArticoliList")).getFullList();
		return String.format("%.2f", artAction.getImportoSanzioneSosp(articoli));
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SospEx14_sequence")
	@SequenceGenerator(name = "SospEx14_sequence", sequenceName = "SospEx14_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
