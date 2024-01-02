package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
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
import com.phi.entities.baseEntity.PrestMdl;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "mis_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MisMdl extends BaseEntity {

	private static final long serialVersionUID = 1374489122L;

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
	*  javadoc for um
	*/
	private CodeValuePhi um;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="um")
	@ForeignKey(name="FK_MisMdl_um")
	//@Index(name="IX_MisMdl_um")
	public CodeValuePhi getUm(){
		return um;
	}

	public void setUm(CodeValuePhi um){
		this.um = um;
	}

	/**
	*  javadoc for val
	*/
	private String val;

	@Column(name="val")
	public String getVal(){
		return val;
	}

	public void setVal(String val){
		this.val = val;
	}

	/**
	*  javadoc for typePar
	*/
	private CodeValuePhi typePar;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="typePar")
	@ForeignKey(name="FK_MisMdl_typePar")
	//@Index(name="IX_MisMdl_typePar")
	public CodeValuePhi getTypePar(){
		return typePar;
	}

	public void setTypePar(CodeValuePhi typePar){
		this.typePar = typePar;
	}

	/**
	*  javadoc for typeVal
	*/
	private CodeValuePhi typeVal;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="typeVal")
	@ForeignKey(name="FK_MisMdl_typeVal")
	//@Index(name="IX_MisMdl_typeVal")
	public CodeValuePhi getTypeVal(){
		return typeVal;
	}

	public void setTypeVal(CodeValuePhi typeVal){
		this.typeVal = typeVal;
	}


	/**
	*  javadoc for prestMdl
	*/
	private PrestMdl prestMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="prest_mdl_id")
	@ForeignKey(name="FK_MisMdl_prestMdl")
	//@Index(name="IX_MisMdl_prestMdl")
	public PrestMdl getPrestMdl(){
		return prestMdl;
	}

	public void setPrestMdl(PrestMdl prestMdl){
		this.prestMdl = prestMdl;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MisMdl_sequence")
	@SequenceGenerator(name = "MisMdl_sequence", sequenceName = "MisMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
