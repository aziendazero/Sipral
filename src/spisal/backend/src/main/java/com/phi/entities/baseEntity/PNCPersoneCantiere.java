package com.phi.entities.baseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.baseEntity.PNCCantiere;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;

@javax.persistence.Entity
@Table(name = "pnc_persone_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Audited
public class PNCPersoneCantiere extends BaseEntity {

	private static final long serialVersionUID = 818331245L;

	/**
	*  javadoc for ruolo
	*/
//	private CodeValuePhi ruolo;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="ruolo")
//	@ForeignKey(name="FK_PNC_ruolo")
//	//@Index(name="IX_PNC_ruolo")
//	public CodeValuePhi getRuolo(){
//		return ruolo;
//	}
//
//	public void setRuolo(CodeValuePhi ruolo){
//		this.ruolo = ruolo;
//	}

	private String ruolo;

	@Column(name="ruolo")
	public String getRuolo() {
		return ruolo;
	}

	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	
	/**
	 *  Name
	 */
	private EN name;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="fam", column=@Column(name="name_fam")),
		@AttributeOverride(name="giv", column=@Column(name="name_giv")),
		@AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
		@AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
		@AttributeOverride(name="del", column=@Column(name="name_del")),
		@AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
	})
	public EN getName(){
		return name;
	}

	public void setName(EN name){
		this.name = name;
	}
	
	/**
	 *  Address
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
	
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
	}
	
	/**
	*  javadoc for cantiere
	*/
	private PNCCantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_PNCPC_cantiere")
	//@Index(name="IX_PNCPC_cantiere")
	public PNCCantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(PNCCantiere cantiere){
		this.cantiere = cantiere;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCPC_sequence")
	@SequenceGenerator(name = "PNCPC_sequence", sequenceName = "PNCPC_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  Fiscal Code
	*/
	private String codiceFiscale;

	@Column(name="codice_fiscale")
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	
	/**
	 *  Country Of Address
	 */
	private CodeValueCountry countryOfAddr;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country_of_addr")
	@ForeignKey(name="FK_Per_countryOfAddr")
	//@Index(name="IX_Per_countryOfAddr")
	public CodeValueCountry getCountryOfAddr(){
		return countryOfAddr;
	}

	public void setCountryOfAddr(CodeValueCountry countryOfAddr){
		this.countryOfAddr = countryOfAddr;
	}

}
