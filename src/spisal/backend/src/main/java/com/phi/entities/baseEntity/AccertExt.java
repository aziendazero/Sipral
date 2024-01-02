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
import org.hibernate.envers.NotAudited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.role.Physician;

@javax.persistence.Entity
@Table(name = "accert_ext")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AccertExt extends BaseEntity {

	private static final long serialVersionUID = 427724535L;


	/**
	*  javadoc for medico
	*/
	private Physician medico;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medico_id")
	@ForeignKey(name="FK_AccertExt_medico")
	//@Index(name="IX_AccertExt_medico")
	@NotAudited
	public Physician getMedico(){
		return medico;
	}

	public void setMedico(Physician medico){
		this.medico = medico;
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


	/**
	*  javadoc for accertaMdl
	*/
	private List<AccertaMdl> accertaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="accertExt", cascade=CascadeType.PERSIST)
	public List<AccertaMdl> getAccertaMdl(){
		return accertaMdl;
	}

	public void setAccertaMdl(List<AccertaMdl> list){
		accertaMdl = list;
	}

	public void addAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
		}
		// add the association
		if(!this.accertaMdl.contains(accertaMdl)) {
			this.accertaMdl.add(accertaMdl);
			// make the inverse link
			accertaMdl.setAccertExt(this);
		}
	}

	public void removeAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
			return;
		}
		//add the association
		if(this.accertaMdl.contains(accertaMdl)){
			this.accertaMdl.remove(accertaMdl);
			//make the inverse link
			accertaMdl.setAccertExt(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AccertExt_sequence")
	@SequenceGenerator(name = "AccertExt_sequence", sequenceName = "AccertExt_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
