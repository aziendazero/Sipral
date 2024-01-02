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
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueIcd9;

import java.util.ArrayList;
import java.util.List;
import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.baseEntity.MisMdl;
import com.phi.entities.role.Operatore;

import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "prest_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PrestMdl extends BaseEntity {

	private static final long serialVersionUID = 1366967510L;

	/**
	*  javadoc for prest
	*/
	private CodeValuePhi prest;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prest")
	@ForeignKey(name="FK_PrestMdl_prest")
	//@Index(name="IX_PrestMdl_prest")
	public CodeValuePhi getPrest(){
		return prest;
	}

	public void setPrest(CodeValuePhi prest){
		this.prest = prest;
	}

	/**
	*  javadoc for referto
	*/
	private String referto;

	@Column(name="referto")
	public String getReferto(){
		return referto;
	}

	public void setReferto(String referto){
		this.referto = referto;
	}

	/**
	*  javadoc for dataReferto
	*/
	private Date dataReferto;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_referto")
	public Date getDataReferto(){
		return dataReferto;
	}

	public void setDataReferto(Date dataReferto){
		this.dataReferto = dataReferto;
	}


	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_PrestMdl_operatore")
	//@Index(name="IX_PrestMdl_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}



	/**
	*  javadoc for misMdl
	*/
	private List<MisMdl> misMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="prestMdl", cascade=CascadeType.PERSIST)
	public List<MisMdl> getMisMdl() {
		return misMdl;
	}

	public void setMisMdl(List<MisMdl>list){
		misMdl = list;
	}

	public void addMisMdl(MisMdl misMdl) {
		if (this.misMdl == null) {
			this.misMdl = new ArrayList<MisMdl>();
		}
		// add the association
		if(!this.misMdl.contains(misMdl)) {
			this.misMdl.add(misMdl);
			// make the inverse link
			misMdl.setPrestMdl(this);
		}
	}

	public void removeMisMdl(MisMdl misMdl) {
		if (this.misMdl == null) {
			this.misMdl = new ArrayList<MisMdl>();
			return;
		}
		//add the association
		if(this.misMdl.contains(misMdl)){
			this.misMdl.remove(misMdl);
			//make the inverse link
			misMdl.setPrestMdl(null);
		}
	}



	/**
	*  javadoc for accertaMdl
	*/
	private AccertaMdl accertaMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="accerta_mdl_id")
	@ForeignKey(name="FK_PrestMdl_accertaMdl")
	//@Index(name="IX_PrestMdl_accertaMdl")
	public AccertaMdl getAccertaMdl(){
		return accertaMdl;
	}

	public void setAccertaMdl(AccertaMdl accertaMdl){
		this.accertaMdl = accertaMdl;
	}


//	/**
//	*  javadoc for prestazione
//	*/
//	private CodeValueIcd9 prestazione;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="prestazione")
//	@ForeignKey(name="FK_PrestMdl_prestazione")
//	//@Index(name="IX_PrestMdl_prestazione")
//	public CodeValueIcd9 getPrestazione(){
//		return prestazione;
//	}
//
//	public void setPrestazione(CodeValueIcd9 prestazione){
//		this.prestazione = prestazione;
//	}

	/**
	*  javadoc for esitoTxt
	*/
	private String esitoTxt;

	@Column(name="esito_txt",length=4000)
	public String getEsitoTxt(){
		return esitoTxt;
	}

	public void setEsitoTxt(String esitoTxt){
		this.esitoTxt = esitoTxt;
	}

	/**
	*  javadoc for esito
	*/
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_PrestMdl_esito")
	//@Index(name="IX_PrestMdl_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_PrestMdl_code")
	//@Index(name="IX_PrestMdl_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PrestMdl_sequence")
	@SequenceGenerator(name = "PrestMdl_sequence", sequenceName = "PrestMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
