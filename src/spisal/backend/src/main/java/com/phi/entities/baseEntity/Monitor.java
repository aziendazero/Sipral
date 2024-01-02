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

import java.util.List;
import javax.persistence.OneToMany;
import com.phi.entities.role.Operatore;
import com.phi.entities.baseEntity.Partecipanti;

@javax.persistence.Entity
@Table(name = "monitor")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Monitor extends BaseEntity {

	private static final long serialVersionUID = 1824188629L;

	/**
	*  javadoc for totale
	*/
	private Boolean totale;

	@Column(name="totale")
	public Boolean getTotale(){
		return (totale==null?false:totale);
	}

	public void setTotale(Boolean totale){
		this.totale = totale;
	}

	/**
	*  javadoc for media
	*/
	private Boolean media;

	@Column(name="media")
	public Boolean getMedia(){
		return (media==null?false:media);
	}

	public void setMedia(Boolean media){
		this.media = media;
	}

	/**
	*  javadoc for mediaScostamento
	*/
	private Double mediaScostamento;

	@Column(name="media_scostamento")
	public Double getMediaScostamento(){
		if (this.getTotale() || this.getMedia())//Se è l'oggetto Monitor dei Totali o delle Medie
			return null;
		
		return mediaScostamento;
	}

	public void setMediaScostamento(Double mediaScostamento){
		this.mediaScostamento = mediaScostamento;
	}

	/**
	*  javadoc for caricoAperto
	*/
	private Double caricoAperto;

	@Column(name="carico_aperto")
	public Double getCaricoAperto(){
		if (caricoAperto==null)
			return 0.0;
		
		return caricoAperto;
	}

	public void setCaricoAperto(Double caricoAperto){
		this.caricoAperto = caricoAperto;
	}

	/**
	*  javadoc for praticheAperte
	*/
	private Double praticheAperte;

	@Column(name="pratiche_aperte")
	public Double getPraticheAperte(){
		if (praticheAperte==null)
			return 0.0;
		
		return praticheAperte;
	}

	public void setPraticheAperte(Double praticheAperte){
		this.praticheAperte = praticheAperte;
	}

	/**
	*  javadoc for caricoChiusoPerc
	*/
	private Double caricoChiusoPerc;

	@Column(name="carico_chiuso_perc")
	public Double getCaricoChiusoPerc(){
		if (caricoChiusoPerc==null)
			return 0.0;
		
		return caricoChiusoPerc;
	}

	public void setCaricoChiusoPerc(Double caricoChiusoPerc){
		this.caricoChiusoPerc = caricoChiusoPerc;
	}

	/**
	*  javadoc for caricoChiuso
	*/
	private Double caricoChiuso;

	@Column(name="carico_chiuso")
	public Double getCaricoChiuso(){
		if (caricoChiuso==null)
			return 0.0;
		
		return caricoChiuso;
	}

	public void setCaricoChiuso(Double caricoChiuso){
		this.caricoChiuso = caricoChiuso;
	}

	/**
	*  javadoc for caricoAtteso
	*/
	private Double caricoAtteso;

	@Column(name="carico_atteso")
	public Double getCaricoAtteso(){
		if (caricoAtteso==null)
			return 0.0;
		
		return caricoAtteso;
	}

	public void setCaricoAtteso(Double caricoAtteso){
		this.caricoAtteso = caricoAtteso;
	}

	/**
	*  javadoc for praticheChiusePerc
	*/
	private Double praticheChiusePerc;

	@Column(name="pratiche_chiuse_perc")
	public Double getPraticheChiusePerc(){
		if (this.getMedia())
			return null;
		
		return praticheChiusePerc;
	}

	public void setPraticheChiusePerc(Double praticheChiusePerc){
		this.praticheChiusePerc = praticheChiusePerc;
	}

	/**
	*  javadoc for praticheChiuse
	*/
	private Double praticheChiuse;

	@Column(name="pratiche_chiuse")
	public Double getPraticheChiuse(){
		if (praticheChiuse==null)
			return 0.0;
		
		return praticheChiuse;
	}

	public void setPraticheChiuse(Double praticheChiuse){
		this.praticheChiuse = praticheChiuse;
	}

	/**
	*  javadoc for praticheAttese
	*/
	private Double praticheAttese;

	@Column(name="pratiche_attese")
	public Double getPraticheAttese(){
		if (praticheAttese==null)
			return 0.0;
		
		return praticheAttese;
	}

	public void setPraticheAttese(Double praticheAttese){
		this.praticheAttese = praticheAttese;
	}

	/**
	*  javadoc for infoProgetti
	*/
	private String infoProgetti;

	@Column(name="info_progetti")
	public String getInfoProgetti(){
		return infoProgetti;
	}

	public void setInfoProgetti(String infoProgetti){
		this.infoProgetti = infoProgetti;
	}


	/**
	*  Lista delle partecipazioni (ai relativi Progetti Associati) dell'operatore
	*/
	private List<Partecipanti> partecipazioni;

	@OneToMany(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="Monitor_id")
	@ForeignKey(name="FK_partecipanti_Monitor")
	@Index(name="IX_partecipanti_Monitor")
	public List<Partecipanti> getPartecipazioni() {
		return partecipazioni;
	}

	public void setPartecipazioni(List<Partecipanti>list){
		partecipazioni = list;
	}

	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_Monitor_operatore")
	@Index(name="IX_Monitor_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}

	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Monitor_sequence")
	@SequenceGenerator(name = "Monitor_sequence", sequenceName = "Monitor_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
