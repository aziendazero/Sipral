package com.phi.entities.baseEntity;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
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

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.AD;
import javax.persistence.Embedded;
import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
@javax.persistence.Entity
@Table(name = "destinatari")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Destinatari extends BaseEntity {

	private static final long serialVersionUID = 227054656L;

	/**
	*  javadoc for invioPc
	*/
	private Boolean invioPc;

	@Column(name="invio_pc")
	public Boolean getInvioPc(){
		return invioPc;
	}

	public void setInvioPc(Boolean invioPc){
		this.invioPc = invioPc;
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
	*  javadoc for email
	*/
	private String email;

	@Column(name="email")
	public String getEmail(){
		return email;
	}

	public void setEmail(String email){
		this.email = email;
	}

	/**
	*  javadoc for denominazione
	*/
	private String denominazione;

	@Column(name="denominazione")
	public String getDenominazione(){
		return denominazione;
	}

	public void setDenominazione(String denominazione){
		this.denominazione = denominazione;
	}

	/**
	*  javadoc for cognome
	*/
	private String cognome;

	@Column(name="cognome")
	public String getCognome(){
		return cognome;
	}

	public void setCognome(String cognome){
		this.cognome = cognome;
	}

	/**
	*  javadoc for nome
	*/
	private String nome;

	@Column(name="nome")
	public String getNome(){
		return nome;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	/**
	*  javadoc for tipo
	*/
	private CodeValuePhi tipo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo")
	@ForeignKey(name="FK_Destinatari_tipo")
	//@Index(name="IX_Destinatari_tipo")
	public CodeValuePhi getTipo(){
		return tipo;
	}

	public void setTipo(CodeValuePhi tipo){
		this.tipo = tipo;
	}

	/**
	*  javadoc for oggetto
	*/
	private String oggetto;

	@Column(name="oggetto")
	public String getOggetto(){
		return oggetto;
	}

	public void setOggetto(String oggetto){
		this.oggetto = oggetto;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Destinatari_sequence")
	@SequenceGenerator(name = "Destinatari_sequence", sequenceName = "Destinatari_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
