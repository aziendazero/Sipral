package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "articoli")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Articoli extends BaseEntity {

	private static final long serialVersionUID = 1218432897L;

	/**
	*  javadoc for codeLegge81
	
	private CodeValueLaw codeLegge81;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codeLegge81")
	@ForeignKey(name="FK_Art_codeLegge81")
	//@Index(name="IX_Art_codeLegge81")
	public CodeValueLaw getCodeLegge81(){
		return codeLegge81;
	}

	public void setCodeLegge81(CodeValueLaw codeLegge81){
		this.codeLegge81 = codeLegge81;
	}*/

	/**
	*  javadoc for gruppo
	*/
	private Gruppi gruppo;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="gruppo_id")
	@ForeignKey(name="FK_Articoli_gruppo")
	//@Index(name="IX_Articoli_gruppo")
	public Gruppi getGruppo(){
		return gruppo;
	}

	public void setGruppo(Gruppi gruppo){
		this.gruppo = gruppo;
	}


	/**
	*  javadoc for esito
	*/
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_Articoli_esito")
	//@Index(name="IX_Articoli_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
	}

	/**
	*  javadoc for miglioramenti
	*/
	private List<Miglioramenti> miglioramenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="articolo", cascade=CascadeType.PERSIST)
	public List<Miglioramenti> getMiglioramenti() {
		return miglioramenti;
	}

	public void setMiglioramenti(List<Miglioramenti>list){
		miglioramenti = list;
	}

	public void addMiglioramenti(Miglioramenti miglioramenti) {
		if (this.miglioramenti == null) {
			this.miglioramenti = new ArrayList<Miglioramenti>();
		}
		// add the association
		if(!this.miglioramenti.contains(miglioramenti)) {
			this.miglioramenti.add(miglioramenti);
			// make the inverse link
			miglioramenti.setArticolo(this);
		}
	}

	public void removeMiglioramenti(Miglioramenti miglioramenti) {
		if (this.miglioramenti == null) {
			this.miglioramenti = new ArrayList<Miglioramenti>();
			return;
		}
		//add the association
		if(this.miglioramenti.contains(miglioramenti)){
			this.miglioramenti.remove(miglioramenti);
			//make the inverse link
			miglioramenti.setArticolo(null);
		}
	}

	/**
	*  javadoc for scadenzaArticolo
	*/
	private Date scadenzaArticolo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scadenza_articolo")
	public Date getScadenzaArticolo(){
		return scadenzaArticolo;
	}

	public void setScadenzaArticolo(Date scadenzaArticolo){
		this.scadenzaArticolo = scadenzaArticolo;
	}
	
	/**
	*  javadoc for durataLavori
	*/
	private Integer giorniPrescrizione;

	@Column(name="giorni_presc")
	public Integer getGiorniPrescrizione(){
		return giorniPrescrizione;
	}

	public void setGiorniPrescrizione(Integer giorniPrescrizione){
		this.giorniPrescrizione = giorniPrescrizione;
	}

	/**
	*  javadoc for prescrizione
	*/
	private String prescrizione;

	@Column(name="prescrizione", length=4000)
	public String getPrescrizione(){
		return prescrizione;
	}

	public void setPrescrizione(String prescrizione){
		this.prescrizione = prescrizione;
	}

	/**
	*  javadoc for violazione
	*/
	private String violazione;

	@Column(name="violazione", length=4000)
	public String getViolazione(){
		return violazione;
	}

	public void setViolazione(String violazione){
		this.violazione = violazione;
	}

	/**
	*  javadoc for code
	*/
	private CodeValueLaw code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Articoli_code")
	//@Index(name="IX_Articoli_code")
	public CodeValueLaw getCode(){
		
		return code;
	}

	public void setCode(CodeValueLaw code){
		this.code = code;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
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
	@ForeignKey(name="FK_Articoli_provvedimento")
	//@Index(name="IX_Articoli_provvedimento")
	public Provvedimenti getProvvedimento(){
		return provvedimento;
	}

	public void setProvvedimento(Provvedimenti provvedimento){
		this.provvedimento = provvedimento;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Articoli_sequence")
	@SequenceGenerator(name = "Articoli_sequence", sequenceName = "Articoli_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for isNew
	*/
	private Boolean isNew;

	@Column(name="is_new")
	public Boolean getIsNew(){
		return isNew;
	}

	public void setIsNew(Boolean isNew){
		this.isNew = isNew;
	}
	
	
	@Transient
	public String getPrintImportoSanzione(){
		String ret = "";
		
		try{
			
			
			Double importoSanzione = 0.00;
			Double max = 0.00;
			Double min = 0.00;
			
			if(!getProvvedimento().getType().getCode().equalsIgnoreCase("301bis")){
			
				if (getCode() != null && getCode().getImportoMax() != null)
					max = Double.parseDouble(getCode().getImportoMax().replace(',', '.'));
							
				if (max != null)
					importoSanzione += max/4.00;
			}else{
				
				if (getCode() != null && getCode().getImportoMin() != null)
					min = Double.parseDouble(getCode().getImportoMin().replace(',', '.'));
							
				if (min != null)
					importoSanzione += min;
				
			}
			
			
				if (importoSanzione!=0.00){
					ret = String.format("%.2f", importoSanzione);
					
				}
			
			
			
		} catch (Exception ex) {} 
		
		return ret;
	}

}
