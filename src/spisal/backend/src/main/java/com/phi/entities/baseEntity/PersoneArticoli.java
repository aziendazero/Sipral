package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.actions.PersoneArticoliAction;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.role.Person;
@javax.persistence.Entity
@Table(name = "persone_articoli")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PersoneArticoli extends BaseEntity {

	private static final long serialVersionUID = 383964962L;

	/**
	*  javadoc for numeroDoc
	*/
	private String numeroDoc;

	@Column(name="numero_doc")
	public String getNumeroDoc(){
		return numeroDoc;
	}

	public void setNumeroDoc(String numeroDoc){
		this.numeroDoc = numeroDoc;
	}

	/**
	*  javadoc for altroTxt
	*/
	private String altroTxt;

	@Column(name="altro_txt")
	public String getAltroTxt(){
		return altroTxt;
	}

	public void setAltroTxt(String altroTxt){
		this.altroTxt = altroTxt;
	}

	/**
	*  javadoc for altroBl
	*/
	private Boolean altroBl;

	@Column(name="altro_bl")
	public Boolean getAltroBl(){
		return altroBl;
	}

	public void setAltroBl(Boolean altroBl){
		this.altroBl = altroBl;
	}

	/**
	*  javadoc for docObbligatoria
	*/
	private Boolean docObbligatoria;

	@Column(name="doc_obbligatoria")
	public Boolean getDocObbligatoria(){
		return docObbligatoria;
	}

	public void setDocObbligatoria(Boolean docObbligatoria){
		this.docObbligatoria = docObbligatoria;
	}

	/**
	*  javadoc for tesserino
	*/
	private Boolean tesserino;

	@Column(name="tesserino")
	public Boolean getTesserino(){
		return tesserino;
	}

	public void setTesserino(Boolean tesserino){
		this.tesserino = tesserino;
	}

	/**
	*  javadoc for addr
	*/
	private AD addr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addr_code"))
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
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
	}

	/**
	*  javadoc for rilasciatoDa
	*/
	private String rilasciatoDa;

	@Column(name="rilasciato_da")
	public String getRilasciatoDa(){
		return rilasciatoDa;
	}

	public void setRilasciatoDa(String rilasciatoDa){
		this.rilasciatoDa = rilasciatoDa;
	}

	/**
	*  javadoc for rilasciatoIl
	*/
	private Date rilasciatoIl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="rilasciato_il")
	public Date getRilasciatoIl(){
		return rilasciatoIl;
	}

	public void setRilasciatoIl(Date rilasciatoIl){
		this.rilasciatoIl = rilasciatoIl;
	}

	/**
	*  javadoc for tipoDoc
	*/
	private String tipoDoc;

	@Column(name="tipo_doc")
	public String getTipoDoc(){
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc){
		this.tipoDoc = tipoDoc;
	}


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_PersArt_person")
	@Index(name="IX_PersArt_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}



	/**
	*  javadoc for articoli
	*/
	private Articoli articoli;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="articoli_id")
	@ForeignKey(name="FK_PersArt_articoli")
	@Index(name="IX_PersArt_articoli")
	public Articoli getArticoli(){
		return articoli;
	}

	public void setArticoli(Articoli articoli){
		this.articoli = articoli;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PersArt_sequence")
	@SequenceGenerator(name = "PersArt_sequence", sequenceName = "PersArt_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
