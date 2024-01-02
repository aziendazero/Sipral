package com.phi.entities.baseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.actions.ArticoliAction;
import com.phi.entities.actions.ProcpraticheAction;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.SospensioneEx14;
@javax.persistence.Entity
@Table(name = "provvedimenti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Provvedimenti extends BaseEntity {

	private static final long serialVersionUID = 559233066L;


	/**
	*  javadoc for sospensioneEx14
	*/
	private List<SospensioneEx14> sospensioneEx14;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimenti", cascade=CascadeType.PERSIST)
	public List<SospensioneEx14> getSospensioneEx14(){
		return sospensioneEx14;
	}

	public void setSospensioneEx14(List<SospensioneEx14> list){
		sospensioneEx14 = list;
	}

	public void addSospensioneEx14(SospensioneEx14 sospensioneEx14) {
		if (this.sospensioneEx14 == null) {
			this.sospensioneEx14 = new ArrayList<SospensioneEx14>();
		}
		// add the association
		if(!this.sospensioneEx14.contains(sospensioneEx14)) {
			this.sospensioneEx14.add(sospensioneEx14);
			// make the inverse link
			sospensioneEx14.setProvvedimenti(this);
		}
	}

	public void removeSospensioneEx14(SospensioneEx14 sospensioneEx14) {
		if (this.sospensioneEx14 == null) {
			this.sospensioneEx14 = new ArrayList<SospensioneEx14>();
			return;
		}
		//add the association
		if(this.sospensioneEx14.contains(sospensioneEx14)){
			this.sospensioneEx14.remove(sospensioneEx14);
			//make the inverse link
			sospensioneEx14.setProvvedimenti(null);
		}

	}


	/**
	 *  javadoc for soggettoInSolido
	 */
	private Soggetto soggettoInSolido;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="soggetto_in_solido_id")
	@ForeignKey(name="FK_Prvvedimenti_sggttInSlid")
	//@Index(name="IX_Prvvedimenti_sggttInSlid")
	public Soggetto getSoggettoInSolido(){
		return soggettoInSolido;
	}

	public void setSoggettoInSolido(Soggetto soggettoInSolido){
		this.soggettoInSolido = soggettoInSolido;
	}

	/**
	 *  javadoc for dettagli
	 */
	private String dettagli;
	@Column(name="dettagli", length=4000)
	public String getDettagli(){
		return dettagli;
	}

	public void setDettagli(String dettagli){
		this.dettagli = dettagli;
	}

	/**
	 *  javadoc for ex301Bis
	 */
	private List<Ex301Bis> ex301Bis;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimento", cascade=CascadeType.PERSIST)
	public List<Ex301Bis> getEx301Bis() {
		return ex301Bis;
	}

	public void setEx301Bis(List<Ex301Bis>list){
		ex301Bis = list;
	}

	public void addEx301Bis(Ex301Bis ex301Bis) {
		if (this.ex301Bis == null) {
			this.ex301Bis = new ArrayList<Ex301Bis>();
		}
		// add the association
		if(!this.ex301Bis.contains(ex301Bis)) {
			this.ex301Bis.add(ex301Bis);
			// make the inverse link
			ex301Bis.setProvvedimento(this);
		}
	}

	public void removeEx301Bis(Ex301Bis ex301Bis) {
		if (this.ex301Bis == null) {
			this.ex301Bis = new ArrayList<Ex301Bis>();
			return;
		}
		//add the association
		if(this.ex301Bis.contains(ex301Bis)){
			this.ex301Bis.remove(ex301Bis);
			//make the inverse link
			ex301Bis.setProvvedimento(null);
		}
	}

	/**
	 *  javadoc for documenti
	 */
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Provvedimenti_id")
	@ForeignKey(name="FK_documenti_Provvedimenti")
	//@Index(name="IX_documenti_Provvedimenti")
	public List<AlfrescoDocument> getDocumenti() {
		return documenti;
	}

	public void setDocumenti(List<AlfrescoDocument>list){
		documenti = list;
	}

	public void addDocumenti(AlfrescoDocument alfrescoDocument) {
		if (this.documenti == null) {
			this.documenti = new ArrayList<AlfrescoDocument>();
		}
		if(!this.documenti.contains(alfrescoDocument)) {
			this.documenti.add(alfrescoDocument);
			//Add also to parent entity
			if (this.attivita != null) {
				this.attivita.addDocumenti(alfrescoDocument);
			}
		}
	}

	public void removeDocumenti(AlfrescoDocument alfrescoDocument) {
		if (this.documenti == null) {
			return;
		}
		if(this.documenti.contains(alfrescoDocument)) {
			this.documenti.remove(alfrescoDocument);
			//Add also to parent entity
			if (this.attivita != null) {
				this.attivita.removeDocumenti(alfrescoDocument);
			}
		}
	}

	/**
	 *  javadoc for disposizioni
	 */
	private List<Disposizioni> disposizioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimenti", cascade=CascadeType.PERSIST)
	public List<Disposizioni> getDisposizioni() {
		return disposizioni;
	}

	public void setDisposizioni(List<Disposizioni>list){
		disposizioni = list;
	}

	public void addDisposizioni(Disposizioni disposizioni) {
		if (this.disposizioni == null) {
			this.disposizioni = new ArrayList<Disposizioni>();
		}
		// add the association
		if(!this.disposizioni.contains(disposizioni)) {
			this.disposizioni.add(disposizioni);
			// make the inverse link
			disposizioni.setProvvedimenti(this);
		}
	}

	public void removeDisposizioni(Disposizioni disposizioni) {
		if (this.disposizioni == null) {
			this.disposizioni = new ArrayList<Disposizioni>();
			return;
		}
		//add the association
		if(this.disposizioni.contains(disposizioni)){
			this.disposizioni.remove(disposizioni);
			//make the inverse link
			disposizioni.setProvvedimenti(null);
		}
	}

	/**
	 *  javadoc for iter758
	 */
	private List<Iter758> iter758;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimento", cascade=CascadeType.PERSIST)
	public List<Iter758> getIter758() {
		return iter758;
	}

	public void setIter758(List<Iter758>list){
		iter758 = list;
	}

	public void addIter758(Iter758 iter758) {
		if (this.iter758 == null) {
			this.iter758 = new ArrayList<Iter758>();
		}
		// add the association
		if(!this.iter758.contains(iter758)) {
			this.iter758.add(iter758);
			// make the inverse link
			iter758.setProvvedimento(this);
		}
	}

	public void removeIter758(Iter758 iter758) {
		if (this.iter758 == null) {
			this.iter758 = new ArrayList<Iter758>();
			return;
		}
		//add the association
		if(this.iter758.contains(iter758)){
			this.iter758.remove(iter758);
			//make the inverse link
			iter758.setProvvedimento(null);
		}
	}

	/**
	 *  javadoc for miglioramenti
	 */
	private List<Miglioramenti> miglioramenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimento"/*, cascade=CascadeType.PERSIST*/)
	public List<Miglioramenti> getMiglioramenti(){
		return miglioramenti;
	}

	public void setMiglioramenti(List<Miglioramenti> list){
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
			miglioramenti.setProvvedimento(this);
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
			miglioramenti.setProvvedimento(null);
		}
	}

	/**
	 *  javadoc for note
	 */
	private String note;

	@Column(name="note", length=4000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	 *  javadoc for articoli
	 */
	private List<Articoli> articoli;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="provvedimento"/*, cascade=CascadeType.PERSIST*/)
	public List<Articoli> getArticoli() {
		return articoli;
	}

	public void setArticoli(List<Articoli>list){
		articoli = list;
	}

	public void addArticoli(Articoli articoli) {
		if (this.articoli == null) {
			this.articoli = new ArrayList<Articoli>();
		}
		// add the association
		if(!this.articoli.contains(articoli)) {
			this.articoli.add(articoli);
			// make the inverse link
			articoli.setProvvedimento(this);
		}
	}

	public void removeArticoli(Articoli articoli) {
		if (this.articoli == null) {
			this.articoli = new ArrayList<Articoli>();
			return;
		}
		//add the association
		if(this.articoli.contains(articoli)){
			this.articoli.remove(articoli);
			//make the inverse link
			articoli.setProvvedimento(null);
		}
	}


	/**
	 *  javadoc for violationType
	 */
	private CodeValuePhi violationType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="violationType")
	@ForeignKey(name="FK_Provvedimenti_violationType")
	//@Index(name="IX_Provvedimenti_violationType")
	public CodeValuePhi getViolationType(){
		return violationType;
	}

	public void setViolationType(CodeValuePhi violationType){
		this.violationType = violationType;
	}

	/**
	 *  javadoc for province
	 */
	private AD province;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="province_code"))
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="province_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="province_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="province_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="province_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="province_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="province_cty")),
		@AttributeOverride(name="sta", column=@Column(name="province_sta")),
		@AttributeOverride(name="stb", column=@Column(name="province_stb")),
		@AttributeOverride(name="str", column=@Column(name="province_str")),
		@AttributeOverride(name="zip", column=@Column(name="province_zip"))
	})
	public AD getProvince(){
		return province;
	}

	public void setProvince(AD province){
		this.province = province;
	}

	/**
	 *  javadoc for country
	 */
	private CodeValueCountry country;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country")
	@ForeignKey(name="FK_Provvedimenti_country")
	//@Index(name="IX_Provvedimenti_country")
	public CodeValueCountry getCountry(){
		return country;
	}

	public void setCountry(CodeValueCountry country){
		this.country = country;
	}

	/**
	 *  javadoc for city
	 */
	private AD city;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="city_code"))
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="city_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="city_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="city_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="city_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="city_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="city_cty")),
		@AttributeOverride(name="sta", column=@Column(name="city_sta")),
		@AttributeOverride(name="stb", column=@Column(name="city_stb")),
		@AttributeOverride(name="str", column=@Column(name="city_str")),
		@AttributeOverride(name="zip", column=@Column(name="city_zip"))
	})
	public AD getCity(){
		return city;
	}

	public void setCity(AD city){
		this.city = city;
	}

	/**
	 *  javadoc for releasedDate
	 */
	private Date releasedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="released_date")
	public Date getReleasedDate(){
		return releasedDate;
	}

	public void setReleasedDate(Date releasedDate){
		this.releasedDate = releasedDate;
	}

	/**
	 *  javadoc for releasedBy
	 */
	private CodeValuePhi releasedBy;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="releasedBy")
	@ForeignKey(name="FK_Provvedimenti_releasedBy")
	//@Index(name="IX_Provvedimenti_releasedBy")
	public CodeValuePhi getReleasedBy(){
		return releasedBy;
	}

	public void setReleasedBy(CodeValuePhi releasedBy){
		this.releasedBy = releasedBy;
	}

	/**
	 *  javadoc for documentNumber
	 */
	private String documentNumber;

	@Column(name="document_number")
	public String getDocumentNumber(){
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber){
		this.documentNumber = documentNumber;
	}

	/**
	 *  javadoc for documentType
	 */
	private CodeValuePhi documentType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="documentType")
	@ForeignKey(name="FK_Provvedimenti_documentType")
	//@Index(name="IX_Provvedimenti_documentType")
	public CodeValuePhi getDocumentType(){
		return documentType;
	}

	public void setDocumentType(CodeValuePhi documentType){
		this.documentType = documentType;
	}

	/**
	 *  javadoc for attivita
	 */
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Provvedimenti_attivita")
	//@Index(name="IX_Provvedimenti_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	/**
	 *  javadoc for carica
	 */
	private Cariche carica;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="carica_id")
	@ForeignKey(name="FK_Provvedimenti_carica")
	//@Index(name="IX_Provvedimenti_carica")
	public Cariche getCarica(){
		return carica;
	}

	public void setCarica(Cariche carica){
		this.carica = carica;
	}

	/**
	 *  javadoc for soggetto
	 */
	private Soggetto soggetto;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="soggetto_id")
	@ForeignKey(name="FK_Provvedimenti_soggetto")
	//@Index(name="IX_Provvedimenti_soggetto")
	public Soggetto getSoggetto(){
		return soggetto;
	}

	public void setSoggetto(Soggetto soggetto){
		this.soggetto = soggetto;
	}

	/**
	 *  javadoc for procpratiche
	 */
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Prvvedimenti_prcpratiche")
	//@Index(name="IX_Prvvedimenti_prcpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Prv_status")
	//@Index(name="IX_Prv_status") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 *  javadoc for esito
	 */
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_Provvedimenti_esito")
	//@Index(name="IX_Provvedimenti_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
	}

	/**
	 *  javadoc for dataNotifica
	 */
	private Date dataNotifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_notifica")
	public Date getDataNotifica(){
		return dataNotifica;
	}

	public void setDataNotifica(Date dataNotifica){
		this.dataNotifica = dataNotifica;
	}

	/**
	 *  javadoc for type
	 */
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_Provvedimenti_type")
	//@Index(name="IX_Provvedimenti_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
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
	 *  javadoc for data
	 */
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Provvedimenti_sequence")
	@SequenceGenerator(name = "Provvedimenti_sequence", sequenceName = "Provvedimenti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	public String toString() {
		String typeTransl = "";
		if (type != null) {
			typeTransl = type.getCurrentTranslation();
		}
		return "Provvedimento " + typeTransl + " " + internalId;
	}


	@Transient
	public String getPrintAttenzioneName(){
		String ret = "";

		try {
			if(this.getSoggetto()!=null && this.getSoggetto().getCode()!=null) {

				if ("Utente".equalsIgnoreCase(this.getSoggetto().getCode().getCurrentTranslation())) 
					ret = getSoggetto().getUtente().getName().toString();

				else if ("Ditta/Ente".equalsIgnoreCase(this.getSoggetto().getCode().getCurrentTranslation())){
					//stampa il nome del destinatario del provvedimento ed il nome della sua ditta se esiste, altrimenti stampa solo il nome della ditta
					if (carica!=null && carica.getSediPersone()!=null){
						if (carica.getSediPersone().getPerson()!=null)
							ret+=carica.getSediPersone().getPerson().getName().toString()+"\n";
						else 
							ret+=carica.getSediPersone().getName().toString()+"\n";
					}

					if(carica.getRuolo()!=null)
						ret+=carica.getRuolo()+" della ditta \n";

					if(soggetto.getSede()!=null)
						ret+=soggetto.getSede().getDenominazioneUnitaLocale();

				}
			}

		} catch(Exception e){
			return "";
		}

		return ret;

	}

	@Transient
	public String getPrintAttenzioneNameOnly(){
		String ret = "";

		try {
			if(this.getSoggetto()!=null && this.getSoggetto().getCode()!=null) {

				if ("Utente".equalsIgnoreCase(this.getSoggetto().getCode().getCurrentTranslation())) 
					ret = getSoggetto().getUtente().getName().toString();

				else if ("Ditta/Ente".equalsIgnoreCase(this.getSoggetto().getCode().getCurrentTranslation())){
					//stampa il nome del destinatario del provvedimento
					if (carica != null && carica.getSediPersone() != null){
						if (carica.getSediPersone().getPerson() != null)
							ret+=carica.getSediPersone().getPerson().getName().toString();
						else 
							ret+=carica.getSediPersone().getName().toString();
					}
				}
			}

		} catch(Exception e){
			return "";
		}

		return ret;

	}


	@Transient
	public String getPrintAttenzioneAddr(){

		String ret = "";
		try{
			if(getSoggetto().getCode().getCurrentTranslation().equalsIgnoreCase("Utente")){
				ret = getSoggetto().getUtente().getAddr().toString();
			}else if(getSoggetto().getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				Sedi sede = getSoggetto().getSede();
				if(sede!=null){
					ret = sede.getAddr().toString();	
				}else{
					for(Sedi s:getSoggetto().getDitta().getSedi()){
						if(s.getSedePrincipale()){
							ret = s.getAddr().toString();
							break;
						}					
					}
				}

			}
		}catch(Exception e){}
		return ret;
	}


	@Transient
	public String getPrintLegRapprName(){

		String ret = "";
		try{

			if(getSoggetto()!=null && getSoggetto().getCode()!=null && getSoggetto().getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){

				//stampa il nome del legale rappresentante della ditta soggetto del provvedimento
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				String query = "SELECT c FROM Cariche c where c.sede = :sede";
				Query q = ca.createQuery(query);
				q.setParameter("sede", soggetto.getSede());
				List<Cariche> ruoList = q.getResultList();

				for(Cariche r:ruoList){
					if(r.getCarica()!=null && r.getCarica().getCode()!=null && r.getCarica().getCode().equals("ler")){
						SediPersone s = r.getSediPersone();
						ret+=s.getName();
						break;
					}

				}

			}
		}catch(Exception e){}
		return ret;
	}
	
	//I00113534 - EVOLUZIONE MODELLI ULSS EUGANEA
	@Transient
	public String getSoggettoIntExtAddr(){
		String ret = "";
		String cf = null;
		AD personalAddr = null;
		
		try{//Se il soggetto del provvedimento è una ditta
			if(getSoggetto()!=null && getSoggetto().getCode()!=null && getSoggetto().getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				
				if (getCarica()!=null && getCarica().getSediPersone()!=null){
					//Recupero il cf del destinatario del provvedimento
					if (getCarica().getSediPersone().getFiscalCode()!=null)
						cf = getCarica().getSediPersone().getFiscalCode();
					
					//Recupero l'indirizzo del destinatario del provvedimento
					if (getCarica().getSediPersone().getAddr()!=null)
						personalAddr = getCarica().getSediPersone().getAddr();
				}
				
				if (cf!=null && !"".equals(cf) && getSoggetto().getSede()!=null){
					CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
					String query = "SELECT c FROM Cariche c where c.sede = :sede";
					Query q = ca.createQuery(query);
					q.setParameter("sede", getSoggetto().getSede());
					List<Cariche> ruoList = q.getResultList();
	
					for(Cariche r:ruoList){
						//Se il destinatario del provvedimento è interno alla ditta, restituisco l'indirizzo della ditta
						if (r.getSediPersone()!=null && r.getSediPersone().getFiscalCode()!=null && r.getSediPersone().getFiscalCode().equalsIgnoreCase(cf)){
							AD addr = getSoggetto().getSede().getAddr();
							
							String denominazione = "";
							if (getSoggetto().getDitta()!=null && getSoggetto().getDitta().getDenominazione()!=null)
								denominazione = getSoggetto().getDitta().getDenominazione();
							
							ret = "c/o Ditta \"" + denominazione + "\"\n";
							
							if (addr!=null){
								if (addr.getStr()!=null)
									ret += addr.getStr() + " ";
								
								if (addr.getBnr()!=null)
									ret += addr.getBnr();
								
								ret += "\n";
								
								if (addr.getZip()!=null)
									ret += addr.getZip() + " ";
								
								if (addr.getCty()!=null && !"".equals(addr.getCty()))
									ret += addr.getCty().substring(0, 1).toUpperCase() + addr.getCty().substring(1).toLowerCase() + " ";
								
								if (addr.getCpa()!=null)
									ret += addr.getCpa();
							}
							
							System.out.println(ret);
							return ret;
						}
					}
				}
				
				//Se il destinatario del provvedimento è esterno alla ditta, restituisco l'indirizzo del destinatario
				if (personalAddr!=null){
					if (personalAddr.getStr()!=null)
						ret += personalAddr.getStr() + " ";
					
					if (personalAddr.getBnr()!=null)
						ret +=personalAddr.getBnr();
						
					ret += "\n";
					
					if (personalAddr.getZip()!=null)
						ret += personalAddr.getZip() + " ";
					
					if (personalAddr.getCty()!=null && !"".equals(personalAddr.getCty()))
						ret += personalAddr.getCty().substring(0, 1).toUpperCase() + personalAddr.getCty().substring(1).toLowerCase() + " ";
					
					if (personalAddr.getCpa()!=null)
						ret += personalAddr.getCpa();
				}
			}
			
		} catch(Exception e){}
		
		System.out.println(ret);
		return ret;
	}

	@Transient
	public String getPrintLegRapprDatiAnagr(){

		String ret = "nato il ";
		try{

			if(getSoggetto()!=null && getSoggetto().getCode()!=null && getSoggetto().getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){

				//stampa i dati anagrafici del legale rappresentante della ditta soggetto del provvedimento
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				String query = "SELECT c FROM Cariche c where c.sede = :sede";
				Query q = ca.createQuery(query);
				q.setParameter("sede", soggetto.getSede());
				List<Cariche> ruoList = q.getResultList();

				SediPersone rappr = null;
				for(Cariche r:ruoList){
					if(r.getCarica()!=null && r.getCarica().getCode()!=null && r.getCarica().getCode().equals("ler")){
						rappr = r.getSediPersone();
						break;
					}

				}

				//Data di nascita
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				ret += sdf.format(rappr.getBirthTime()) + " ";

				//Luogo di nascita
				String luogoNascita = "";
				if(rappr.getBirthPlace()!=null){
					if(!"".equals(rappr.getBirthPlace().getCty())){
						luogoNascita = "a " + rappr.getBirthPlace().getCty() + " (" + rappr.getBirthPlace().getCpa() + ") ";
					}
				}else{
					if(rappr.getCountryOfBirth()!=null){
						luogoNascita = "in " + rappr.getCountryOfBirth().getCurrentTranslation() + " ";
					}
				}
				
				//Residenza
				ret += luogoNascita + "e residente a ";
				ret += (rappr.getAddr()!=null?rappr.getAddr().getCty():"") + " ";
				ret += "(" + (rappr.getAddr()!=null?rappr.getAddr().getCpa():"") + ") ";
				ret += "in " + (rappr.getAddr()!=null?rappr.getAddr().getStr():"") + " " + (rappr.getAddr()!=null?(rappr.getAddr().getBnr()!=null?rappr.getAddr().getBnr():""):"");
			}
		}catch(Exception e){}
		return ret;
	}
	
	@Transient
	public String getPrintLegRapprAddr(){

		String ret = "";
		try{
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

			Sedi sede = getSoggetto().getSede();
			if(sede==null && getSoggetto().getDitta()!=null && getSoggetto().getDitta().getSedi()!=null){
				for(Sedi s:getSoggetto().getDitta().getSedi()){
					if(s.getSedePrincipale()){
						sede = s;
						break;
					}					
				}
			}		

			if(sede!=null){
				String query = "SELECT ca FROM Cariche ca where ca.sede = :sp and carica.code='ler'";
				Query q = ca.createQuery(query);
				q.setParameter("sp", sede);
				List<Cariche> lst = q.getResultList();

				for(Cariche c:lst){
					if(c.getCarica()!=null){
						//if( c.getSede()!=null &&  c.getSede().getPersonaGiuridica()!=null) ret += c.getSede().getPersonaGiuridica().getDenominazione()+"\n";
						if( c.getSede()!=null) 
							ret += c.getSede().getDenominazioneUnitaLocale()+"\n";
						
						ret += c.getSediPersone().getAddr().toString();
						
						if(c.getSede().getTelecom()!=null)
							//ret += c.getSede().getTelecom().getMail();
							ret += c.getSede().getTelecom().getMc();
						break;
					}
				}
			}
			
			if(sede==null){
				ret="";
			}
		}catch(Exception e){}
		return ret;
	}

	@Transient
	public String getPrintLegRapprAddrEug(){

		String ret = "";
		try{
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

			Sedi sede = getSoggetto().getSede();
			if(sede==null && getSoggetto().getDitta()!=null && getSoggetto().getDitta().getSedi()!=null){
				for(Sedi s:getSoggetto().getDitta().getSedi()){
					if(s.getSedePrincipale()){
						sede = s;
						break;
					}					
				}
			}		

			if(sede!=null){
				String query = "SELECT ca FROM Cariche ca where ca.sede = :sp and carica.code='ler'";
				Query q = ca.createQuery(query);
				q.setParameter("sp", sede);
				List<Cariche> lst = q.getResultList();

				for(Cariche c:lst){
					if(c.getCarica()!=null){
						if( c.getSede()!=null) 
							ret += c.getSede().getDenominazioneUnitaLocale() + "\n";
						
						ret += c.getSediPersone().getAddr().getStr() + " " + c.getSediPersone().getAddr().getBnr()+ "\n";
						ret += c.getSediPersone().getAddr().getZip() + " " + c.getSediPersone().getAddr().getCty() + " " + c.getSediPersone().getAddr().getCpa() + "\n";
											      
						if(c.getSede().getTelecom()!=null)
							ret += c.getSede().getTelecom().getMc();
						break;
					}
				}
			}
			
			if(sede==null){
				ret="";
			}
		}catch(Exception e){}
		return ret;
	}

	@Transient
	public String getPrintImportoTotale(){
		String ret = "";

		try{
			List<Articoli>articoli = getArticoli();

			if(articoli!=null && articoli.size()>0){

				Double importoSanzione=0.00;

				for (Articoli articolo : articoli) {
					//if (articolo.getEsito() != null && articolo.getEsito().getCode().equals("compliedPayed")
					if(articolo.getEsito().getCode().equals("compliedPayed")){ 
						Double max = 0.00;
						if (articolo.getCode()!= null && articolo.getCode().getImportoMax()!=null){
							max = Double.parseDouble(articolo.getCode().getImportoMax().replace(',', '.'));
						}

						if (max != null){
							importoSanzione += max;
						}
					}

				}
				importoSanzione = importoSanzione/4;
				ret = String.format("%.2f", importoSanzione);
				return ret;

			}


		} catch(Exception e) { 
			return "";
		}

		return ret;
	}

	@Transient
	public String getPrintImportoTotaleNC(){
		String ret = "";
		try{
			List<Articoli>articoli = getArticoli();

			if(articoli!=null && articoli.size()>0){

				Double importoSanzione=0.00;

				for (Articoli articolo : articoli) {
					//if (articolo.getEsito() != null && articolo.getEsito().getCode().equals("compliedPayed")
					if(!articolo.getEsito().getCode().equals("compliedPayed")){ 
						Double max = 0.00;
						if (articolo.getCode()!= null && articolo.getCode().getImportoMax()!=null){
							max = Double.parseDouble(articolo.getCode().getImportoMax().replace(',', '.'));

						}

						if (max != null){
							importoSanzione += max;

						}
					}

				}

				importoSanzione = importoSanzione/4;

				ret = String.format("%.2f", importoSanzione);

				return ret;

			}

		}catch(Exception e){}

		return ret;
	}


	@Transient
	public String getPrintImportoTotaleAll(){
		String ret = "";
		try{
			List<Articoli>articoli = getArticoli();

			if(articoli!=null && articoli.size()>0){

				Double importoSanzioneMin=0.00;
				Double importoSanzioneMax=0.00;

				for (Articoli articolo : articoli) {


					Double max = 0.00;
					Double min = 0.00;
					if (articolo.getCode()!= null && articolo.getCode().getImportoMax()!=null){
						max = Double.parseDouble(articolo.getCode().getImportoMax().replace(',', '.'));
						min = Double.parseDouble(articolo.getCode().getImportoMin().replace(',', '.'));

					}

					if (max != null){
						importoSanzioneMax += max;
					}
					if(min != null){
						importoSanzioneMin +=min;
					}



				}

				if(!getType().getCode().equalsIgnoreCase("301bis")){
					ret = String.format("%.2f", importoSanzioneMax/4);
				}else{
					ret = String.format("%.2f", importoSanzioneMin);
				}




				return ret;

			}

		}catch(Exception e){}

		return ret;
	}

	@Transient
	public String getPrintLegRapprAddrInline(){

		String ret = "";
		try{
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

			Sedi sede = getSoggetto().getSede();
			if(sede==null && getSoggetto().getDitta()!=null && getSoggetto().getDitta().getSedi()!=null){

				ret+=getSoggetto().getDitta().getDenominazione()+", ";

				for(Sedi s:getSoggetto().getDitta().getSedi()){
					if(s.getSedePrincipale()){
						sede = s;
						break;
					}					
				}
			}		

			if(sede!=null){
				String query = "SELECT ca FROM Cariche ca where ca.sede = :sp and carica.code='ler'";
				Query q = ca.createQuery(query);
				q.setParameter("sp", sede);
				List<Cariche> lst = q.getResultList();



				for(Cariche c:lst){
					if(c.getCarica()!=null){
						if( c.getSede()!=null &&  c.getSede().getPersonaGiuridica()!=null) {
							//						
							if(getSoggetto()!=null &&
									getSoggetto().getDitta()!=null && 
									getSoggetto().getDitta().getDenominazione()!=null){
								ret+= getSoggetto().getDitta().getDenominazione()+", ";
							}

							ret+= "sede " + c.getSede().getDenominazioneUnitaLocale()+", ";
							ret+= c.getSede().getPersonaGiuridica().getPivaOrCf()+", ";
						}
						ret += c.getSediPersone().getAddr().toString();

						break;
					}
				}

			}
			if(sede==null){
				ret="Non disponibile";
			}
		}catch(Exception e){}
		return ret;
	}


	@Transient
	public String getPrintGroupDates(){
		String ret = "";
		try{
			HashMap<Gruppi,List<Articoli>> groupMap = new HashMap<Gruppi,List<Articoli>>();

			List<Articoli>articoli = getArticoli();
			if(articoli!=null && articoli.size()>0){
				for (Articoli a : articoli) {

					Gruppi g = a.getGruppo();
					if(g!=null){
						if(groupMap.containsKey(g)){
							groupMap.get(g).add(a);
						}else{
							List<Articoli> al = new ArrayList<Articoli>();
							al.add(a);
							groupMap.put(g, al);
						}
					}
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");

				for (Gruppi g : groupMap.keySet()) {
					if(g!=null && g.getDataDellaVerifica()!=null){
						ret+=sdf.format(g.getDataDellaVerifica())+", ";
					}else{
						ret+=" N.D., ";
					}
				}
			}

		}catch(Exception e){}
		return ret;
	}

	@Transient
	public String getPrintGroupDetails(){
		String ret = "";
		try{
			HashMap<Gruppi,List<Articoli>> groupMap = new HashMap<Gruppi,List<Articoli>>();
			ArticoliAction aa = new ArticoliAction();

			List<Articoli>articoli = getArticoli();
			int counter=0;
			if(articoli!=null && articoli.size()>0){
				for (Articoli a : articoli) {
					counter++;
					ret+="�?  il punto \""+a.getCode().getDisplayName()+"\" risulta " + aa.getStato(a);
					if(counter<articoli.size()) ret+="\n";

				}

			}
		}catch(Exception e){}	
		return ret;
	}

	@Transient
	public Boolean getHasAdempiute(){
		Boolean ret = false;
		try{
			List<Articoli>articoli = getArticoli();

			if(articoli!=null && articoli.size()>0){
				for (Articoli a : articoli) {
					if(a.getEsito().getCode().equalsIgnoreCase("complayedDifferent")){
						ret = true;
						break;
					}
				}

			}
		}catch(Exception e){}
		return ret;
	}

	@Transient
	public Boolean getHasNonAdempiute(){
		Boolean ret = false;
		try{
			List<Articoli>articoli = getArticoli();

			if(articoli!=null && articoli.size()>0){
				for (Articoli a : articoli) {
					if(a.getEsito().getCode().equalsIgnoreCase("notComplayed")){
						ret = true;
						break;
					}
				}

			}
		}catch(Exception e){}
		return ret;
	}

	@Transient
	public String getPersonaDenunciata(){
		String ret = "";
		try{
			Person p = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");

			if(soggetto.getCode().getCurrentTranslation().equalsIgnoreCase("Utente")){
				p = soggetto.getUtente(); 
			}else if(soggetto.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				if(getCarica()!=null){
					SediPersone sp = getCarica().getSediPersone();
					if(sp!=null){
						p = sp.getPerson();
						if(p!=null){
							//ret = p.getName().toString();			
						}else{
							ret = "Destinatario non disponibile";
						}
					}else{
						ret = "Destinatario non disponibile";
					}
				}else{
					ret = "Destinatario non disponibile";
				}

			}

			if(p!=null){
				ret+= p.getName().toString()+", nato/a il ";
				ret+= sdf.format(p.getBirthTime());

				if(p.getBirthPlace()!=null){
					if(p.getBirthPlace().getCty()!=null && !("".equals(p.getBirthPlace().getCty()))){
						ret+= ", a " + p.getBirthPlace().getCty();
					}else{
						ret+= ", in " + p.getCountryOfBirth();
					}
				}else{
					ret+= ", in " + p.getCountryOfBirth();
				}

				ret+=", residente in ";
				ret+= p.getAddr()+", C.F.: ";
				ret+= (p.getFiscalCode()!=null ? p.getFiscalCode() : "   ");

				if(soggetto!=null && soggetto.getRuolo()!=null){
					ret+= " con la qualifica di "+ soggetto.getRuolo().getCurrentTranslation();
					if(soggetto.getDittaUtente()!=null){
						ret+=" della ditta "+ soggetto.getDittaUtente().getDenominazione();
					}

				}else if(getCarica()!=null && getCarica().getRuolo()!=null){
					ret+= " con la qualifica di "+ getCarica().getRuolo().getCurrentTranslation();
				}


			}

		}catch(Exception e){}




		return ret;
	}


	@Transient
	public String getNumeroVerbale(){
		String ret = "";

		if(numero!=null && numero.length()>0){  

			String[] splitted = numero.split("_");
			if(splitted.length<3) 
				return "[numero non riconosciuto]";

			ret+=splitted[1]+"/"+splitted[2];
		}

		return ret;

	}

	@Transient
	public String getRiferimentiNormativiDisposizioni(){

		String ret = "";
		List<Disposizioni> disp = getDisposizioni();

		if(disp!=null && !disp.isEmpty()){

			for(Disposizioni d:disp){

				List<CodeValuePhi> listRiferimenti = d.getRiferimentiNormativi(); 
				for(CodeValuePhi cv1:listRiferimenti){

					if(!cv1.getDisplayName().equalsIgnoreCase("altro")){
						ret+=cv1.getDisplayName()+", ";
					}else{

						List<CodeValuePhi> listRiferimenti2 = d.getMotivoProvv();

						for(CodeValuePhi cv2:listRiferimenti2){

							if(!cv2.getDisplayName().equalsIgnoreCase("SrvSan")){
								ret+=cv2.getDisplayName()+", ";
							}else{

								List<CodeValuePhi> listRiferimenti3 = d.getMotivoProvvSrvSn();
								for(CodeValuePhi cv3:listRiferimenti3){
									ret+=cv3.getDisplayName()+", ";
								}

							}
						}
					}
				}



			}

		}
		if(ret.length()>1){	
			ret=ret.substring(0, ret.length()-2);
		}else{
			ret="nessun riferimento selezionato";
		}
		return ret;
	}

	@PostPersist
	@PostUpdate
	public void updateAssociations() throws NamingException{

		try {
			Procpratiche p = ProcpraticheAction.instance().getEntity();
			if(p!=null)
				p.updateAssociations();
		}catch(IllegalStateException e) {
			//sto eseguendo l'importer
		}
	}

}
