package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

import java.util.ArrayList;
import java.util.List;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.baseEntity.AccertExt;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.PrestMdl;

@javax.persistence.Entity
@Table(name = "accerta_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AccertaMdl extends BaseEntity {

	private static final long serialVersionUID = 427629001L;

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_AccertaMdl_code")
	//@Index(name="IX_AccertaMdl_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}


	/**
	*  javadoc for prestMdl
	*/
	private List<PrestMdl> prestMdl = new ArrayList<PrestMdl>();

	@OneToMany(fetch=FetchType.LAZY, mappedBy="accertaMdl", cascade=CascadeType.PERSIST)
	public List<PrestMdl> getPrestMdl() {
		return prestMdl;
	}

	public void setPrestMdl(List<PrestMdl>list){
		prestMdl = list;
	}

	public void addPrestMdl(PrestMdl prestMdl) {
		if (this.prestMdl == null) {
			this.prestMdl = new ArrayList<PrestMdl>();
		}
		// add the association
		if(!this.prestMdl.contains(prestMdl)) {
			this.prestMdl.add(prestMdl);
			// make the inverse link
			prestMdl.setAccertaMdl(this);
		}
	}

	public void removePrestMdl(PrestMdl prestMdl) {
		if (this.prestMdl == null) {
			this.prestMdl = new ArrayList<PrestMdl>();
			return;
		}
		//add the association
		if(this.prestMdl.contains(prestMdl)){
			this.prestMdl.remove(prestMdl);
			//make the inverse link
			prestMdl.setAccertaMdl(null);
		}
	}


	/**
	*  javadoc for accertExt
	*/
	private AccertExt accertExt;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="accert_ext_id")
	@ForeignKey(name="FK_AccertaMdl_accertExt")
	//@Index(name="IX_AccertaMdl_accertExt")
	public AccertExt getAccertExt(){
		return accertExt;
	}

	public void setAccertExt(AccertExt accertExt){
		this.accertExt = accertExt;
	}



	/**
	*  javadoc for accertSp
	*/
	private AccertSp accertSp;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="accert_sp_id")
	@ForeignKey(name="FK_AccertaMdl_accertSp")
	//@Index(name="IX_AccertaMdl_accertSp")
	public AccertSp getAccertSp(){
		return accertSp;
	}

	public void setAccertSp(AccertSp accertSp){
		this.accertSp = accertSp;
	}



	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_AccertaMdl_attivita")
	//@Index(name="IX_AccertaMdl_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AccertaMdl_sequence")
	@SequenceGenerator(name = "AccertaMdl_sequence", sequenceName = "AccertaMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
