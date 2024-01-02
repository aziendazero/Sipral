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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;

import java.text.DecimalFormat;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import com.phi.entities.baseEntity.Progetto;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.baseEntity.Pianificazione;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.CostoNomina;

@javax.persistence.Entity
@Table(name = "progetto_associato")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ProgettoAssociato extends BaseEntity {

	private static final long serialVersionUID = 1101138340L;

	/**
	*  javadoc for costoNomina16
	*/
	private CostoNomina costoNomina16;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina16_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina16")
	@Index(name="IX_PrgettAssciat_cstNmina16")
	public CostoNomina getCostoNomina16(){
		return costoNomina16;
	}

	public void setCostoNomina16(CostoNomina costoNomina16){
		this.costoNomina16 = costoNomina16;
	}



	/**
	*  javadoc for costoNomina15
	*/
	private CostoNomina costoNomina15;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina15_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina15")
	@Index(name="IX_PrgettAssciat_cstNmina15")
	public CostoNomina getCostoNomina15(){
		return costoNomina15;
	}

	public void setCostoNomina15(CostoNomina costoNomina15){
		this.costoNomina15 = costoNomina15;
	}



	/**
	*  javadoc for costoNomina14
	*/
	private CostoNomina costoNomina14;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina14_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina14")
	@Index(name="IX_PrgettAssciat_cstNmina14")
	public CostoNomina getCostoNomina14(){
		return costoNomina14;
	}

	public void setCostoNomina14(CostoNomina costoNomina14){
		this.costoNomina14 = costoNomina14;
	}



	/**
	*  javadoc for costoNomina13
	*/
	private CostoNomina costoNomina13;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina13_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina13")
	@Index(name="IX_PrgettAssciat_cstNmina13")
	public CostoNomina getCostoNomina13(){
		return costoNomina13;
	}

	public void setCostoNomina13(CostoNomina costoNomina13){
		this.costoNomina13 = costoNomina13;
	}



	/**
	*  javadoc for costoNomina12
	*/
	private CostoNomina costoNomina12;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina12_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina12")
	@Index(name="IX_PrgettAssciat_cstNmina12")
	public CostoNomina getCostoNomina12(){
		return costoNomina12;
	}

	public void setCostoNomina12(CostoNomina costoNomina12){
		this.costoNomina12 = costoNomina12;
	}



	/**
	*  javadoc for costoNomina11
	*/
	private CostoNomina costoNomina11;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina11_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina11")
	@Index(name="IX_PrgettAssciat_cstNmina11")
	public CostoNomina getCostoNomina11(){
		return costoNomina11;
	}

	public void setCostoNomina11(CostoNomina costoNomina11){
		this.costoNomina11 = costoNomina11;
	}



	/**
	*  javadoc for costoNomina10
	*/
	private CostoNomina costoNomina10;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina10_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina10")
	@Index(name="IX_PrgettAssciat_cstNmina10")
	public CostoNomina getCostoNomina10(){
		return costoNomina10;
	}

	public void setCostoNomina10(CostoNomina costoNomina10){
		this.costoNomina10 = costoNomina10;
	}



	/**
	*  javadoc for costoNomina09
	*/
	private CostoNomina costoNomina09;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina09_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina09")
	@Index(name="IX_PrgettAssciat_cstNmina09")
	public CostoNomina getCostoNomina09(){
		return costoNomina09;
	}

	public void setCostoNomina09(CostoNomina costoNomina09){
		this.costoNomina09 = costoNomina09;
	}



	/**
	*  javadoc for costoNomina08
	*/
	private CostoNomina costoNomina08;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina08_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina08")
	@Index(name="IX_PrgettAssciat_cstNmina08")
	public CostoNomina getCostoNomina08(){
		return costoNomina08;
	}

	public void setCostoNomina08(CostoNomina costoNomina08){
		this.costoNomina08 = costoNomina08;
	}



	/**
	*  javadoc for costoNomina07
	*/
	private CostoNomina costoNomina07;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina07_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina07")
	@Index(name="IX_PrgettAssciat_cstNmina07")
	public CostoNomina getCostoNomina07(){
		return costoNomina07;
	}

	public void setCostoNomina07(CostoNomina costoNomina07){
		this.costoNomina07 = costoNomina07;
	}



	/**
	*  javadoc for costoNomina06
	*/
	private CostoNomina costoNomina06;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina06_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina06")
	@Index(name="IX_PrgettAssciat_cstNmina06")
	public CostoNomina getCostoNomina06(){
		return costoNomina06;
	}

	public void setCostoNomina06(CostoNomina costoNomina06){
		this.costoNomina06 = costoNomina06;
	}



	/**
	*  javadoc for costoNomina05
	*/
	private CostoNomina costoNomina05;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina05_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina05")
	@Index(name="IX_PrgettAssciat_cstNmina05")
	public CostoNomina getCostoNomina05(){
		return costoNomina05;
	}

	public void setCostoNomina05(CostoNomina costoNomina05){
		this.costoNomina05 = costoNomina05;
	}



	/**
	*  javadoc for costoNomina04
	*/
	private CostoNomina costoNomina04;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina04_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina04")
	@Index(name="IX_PrgettAssciat_cstNmina04")
	public CostoNomina getCostoNomina04(){
		return costoNomina04;
	}

	public void setCostoNomina04(CostoNomina costoNomina04){
		this.costoNomina04 = costoNomina04;
	}



	/**
	*  javadoc for costoNomina03
	*/
	private CostoNomina costoNomina03;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina03_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina03")
	@Index(name="IX_PrgettAssciat_cstNmina03")
	public CostoNomina getCostoNomina03(){
		return costoNomina03;
	}

	public void setCostoNomina03(CostoNomina costoNomina03){
		this.costoNomina03 = costoNomina03;
	}



	/**
	*  javadoc for costoNomina02
	*/
	private CostoNomina costoNomina02;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina02_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina02")
	@Index(name="IX_PrgettAssciat_cstNmina02")
	public CostoNomina getCostoNomina02(){
		return costoNomina02;
	}

	public void setCostoNomina02(CostoNomina costoNomina02){
		this.costoNomina02 = costoNomina02;
	}



	/**
	*  javadoc for costoNomina01
	*/
	private CostoNomina costoNomina01;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="costo_nomina01_id")
	@ForeignKey(name="FK_PrgettAssciat_cstNmina01")
	@Index(name="IX_PrgettAssciat_cstNmina01")
	public CostoNomina getCostoNomina01(){
		return costoNomina01;
	}

	public void setCostoNomina01(CostoNomina costoNomina01){
		this.costoNomina01 = costoNomina01;
	}


	/**
	*  javadoc for log
	*/
	private String log;

	@Column(name="log", length = 20000)
	public String getLog(){
		return log;
	}

	public void setLog(String log){
		this.log = log;
	}

	/**
	*  javadoc for pesoComplessivo
	*/
	private Double pesoComplessivo;

	@Column(name="peso_complessivo")
	public Double getPesoComplessivo(){
		return pesoComplessivo;
	}

	public void setPesoComplessivo(Double pesoComplessivo){
		this.pesoComplessivo = pesoComplessivo;
	}

	/**
	*  javadoc for pesoMedio
	*/
	private Double pesoMedio;

	@Column(name="peso_medio")
	public Double getPesoMedio(){
		return pesoMedio;
	}

	public void setPesoMedio(Double pesoMedio){
		this.pesoMedio = pesoMedio;
	}

	/**
	*  javadoc for statusCode
	*/
	private CodeValueStatus statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statusCode")
	@ForeignKey(name="FK_ProgettoAssociato_statusCode")
	@Index(name="IX_Prj_statusCode")
	public CodeValueStatus getStatusCode(){
		return statusCode;
	}

	public void setStatusCode(CodeValueStatus statusCode){
		this.statusCode = statusCode;
	}

	/**
	*  javadoc for numeroPratiche
	*/
	private Integer numeroPratiche;

	@Column(name="numero_pratiche")
	public Integer getNumeroPratiche(){
		return numeroPratiche;
	}

	public void setNumeroPratiche(Integer numeroPratiche){
		this.numeroPratiche = numeroPratiche;
	}

	/**
	*  javadoc for periodoRiferimentoA
	*/
	private Date periodoRiferimentoA;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="periodo_riferimento_a")
	public Date getPeriodoRiferimentoA(){
		return periodoRiferimentoA;
	}

	public void setPeriodoRiferimentoA(Date periodoRiferimentoA){
		this.periodoRiferimentoA = periodoRiferimentoA;
	}

	/**
	*  javadoc for periodoRiferimentoDa
	*/
	private Date periodoRiferimentoDa;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="periodo_riferimento_da")
	public Date getPeriodoRiferimentoDa(){
		return periodoRiferimentoDa;
	}

	public void setPeriodoRiferimentoDa(Date periodoRiferimentoDa){
		this.periodoRiferimentoDa = periodoRiferimentoDa;
	}


	/**
	*  javadoc for partecipanti
	*/
	private List<Partecipanti> partecipanti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="progettoAssociato", cascade=CascadeType.PERSIST)
	public List<Partecipanti> getPartecipanti() {
		return partecipanti;
	}

	public void setPartecipanti(List<Partecipanti>list){
		partecipanti = list;
	}

	public void addPartecipanti(Partecipanti partecipanti) {
		if (this.partecipanti == null) {
			this.partecipanti = new ArrayList<Partecipanti>();
		}
		// add the association
		if(!this.partecipanti.contains(partecipanti)) {
			this.partecipanti.add(partecipanti);
			// make the inverse link
			partecipanti.setProgettoAssociato(this);
		}
	}

	public void removePartecipanti(Partecipanti partecipanti) {
		if (this.partecipanti == null) {
			this.partecipanti = new ArrayList<Partecipanti>();
			return;
		}
		//add the association
		if(this.partecipanti.contains(partecipanti)){
			this.partecipanti.remove(partecipanti);
			//make the inverse link
			partecipanti.setProgettoAssociato(null);
		}
	}



	/**
	*  javadoc for pianificazione
	*/
	private Pianificazione pianificazione;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="pianificazione_id")
	@ForeignKey(name="FK_Prgttssciat_pianificazin")
	@Index(name="IX_Prgttssciat_pianificazin")
	public Pianificazione getPianificazione(){
		return pianificazione;
	}

	public void setPianificazione(Pianificazione pianificazione){
		this.pianificazione = pianificazione;
	}


	/**
	*  javadoc for priorita
	*/
	private Integer priorita;

	@Column(name="priorita")
	public Integer getPriorita(){
		return priorita;
	}

	public void setPriorita(Integer priorita){
		this.priorita = priorita;
	}
	
	/**
	*  javadoc for praticheAttese
	*/
	private Integer praticheAttese;

	@Column(name="pratiche_attese")
	public Integer getPraticheAttese(){
		return praticheAttese;
	}

	public void setPraticheAttese(Integer praticheAttese){
		this.praticheAttese = praticheAttese;
	}
	
	/**
	*  javadoc for subVigilanza
	*/
	private CodeValuePhi subVigilanza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subVigilanza")
	@ForeignKey(name="FK_Prj_subVigilanza")
	@Index(name="IX_Prj_subVigilanza")
	public CodeValuePhi getSubVigilanza(){
		return subVigilanza;
	}

	public void setSubVigilanza(CodeValuePhi subVigilanza){
		this.subVigilanza = subVigilanza;
	}

	/**
	*  javadoc for subMdl
	*/
	private CodeValuePhi subMdl;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subMdl")
	@ForeignKey(name="FK_ProgettoAssociato_subMdl")
	@Index(name="IX_ProgettoAssociato_subMdl")
	public CodeValuePhi getSubMdl(){
		return subMdl;
	}

	public void setSubMdl(CodeValuePhi subMdl){
		this.subMdl = subMdl;
	}

	/**
	*  javadoc for lineaDiLavoro
	*/
	private CodeValuePhi lineaDiLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lineaDiLavoro")
	@ForeignKey(name="FK_Prj_lineaLavoro")
	@Index(name="IX_Prj_lineaLavoro")
	public CodeValuePhi getLineaDiLavoro(){
		return lineaDiLavoro;
	}

	public void setLineaDiLavoro(CodeValuePhi lineaDiLavoro){
		this.lineaDiLavoro = lineaDiLavoro;
	}


	/**
	*  javadoc for progetto
	*/
	private Progetto progetto;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="progetto_id")
	@ForeignKey(name="FK_ProgettoAssociato_prgett")
	@Index(name="IX_ProgettoAssociato_prgett")
	public Progetto getProgetto(){
		return progetto;
	}

	public void setProgetto(Progetto progetto){
		this.progetto = progetto;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProgettoAssociato_sequence")
	@SequenceGenerator(name = "ProgettoAssociato_sequence", sequenceName = "ProgettoAssociato_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
