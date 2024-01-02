package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.SediPersone;
@Entity
@Table(name = "cariche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Cariche extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6127577052576216261L;

	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="FK_Cariche_ruolo")
	//@Index(name="IX_Cariche_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
	}

	/**
	*  javadoc for provvedimenti
	*/
	private List<Provvedimenti> provvedimenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="carica", cascade=CascadeType.PERSIST)
	public List<Provvedimenti> getProvvedimenti(){
		return provvedimenti;
	}

	public void setProvvedimenti(List<Provvedimenti> list){
		provvedimenti = list;
	}

	public void addProvvedimenti(Provvedimenti provvedimenti) {
		if (this.provvedimenti == null) {
			this.provvedimenti = new ArrayList<Provvedimenti>();
		}
		// add the association
		if(!this.provvedimenti.contains(provvedimenti)) {
			this.provvedimenti.add(provvedimenti);
			// make the inverse link
			provvedimenti.setCarica(this);
		}
	}

	public void removeProvvedimenti(Provvedimenti provvedimenti) {
		if (this.provvedimenti == null) {
			this.provvedimenti = new ArrayList<Provvedimenti>();
			return;
		}
		//add the association
		if(this.provvedimenti.contains(provvedimenti)){
			this.provvedimenti.remove(provvedimenti);
			//make the inverse link
			provvedimenti.setCarica(null);
		}

	}


	/**
	*  javadoc for sediPersone
	*/
	private SediPersone sediPersone;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_persone_id")
	@ForeignKey(name="FK_Cariche_sediPersone")
	//@Index(name="IX_Cariche_sediPersone")
	public SediPersone getSediPersone(){
		return sediPersone;
	}

	public void setSediPersone(SediPersone sediPersone){
		this.sediPersone = sediPersone;
	}

	/**
	*  javadoc for sede
	*/
	private Sedi sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_Cariche_sede")
	//@Index(name="IX_Cariche_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}

	/**
	*  javadoc for internalId
	*/
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Cariche_sequence")
	@SequenceGenerator(name = "Cariche_sequence", sequenceName = "Cariche_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	 * javadoc for Carica
	 */
	private CodeValue carica;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "CARICA")
	@ForeignKey(name = "FK_Carica")
	//@Index(name="IX_Carica")
	public CodeValue getCarica() {
		return carica;
	}

	public void setCarica(CodeValue carica) {
		this.carica = carica;
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
	*  javadoc for Data inizio
	*/
	private Date dataInizio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_INIZIO")
	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	/**
	*  javadoc for Data fine
	*/
	private Date dataFine;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_FINE")
	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	
}
