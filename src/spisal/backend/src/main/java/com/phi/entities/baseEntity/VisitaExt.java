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


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.VisitaMdl;
@javax.persistence.Entity
@Table(name = "visita_ext")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VisitaExt extends BaseEntity {

	private static final long serialVersionUID = 427699323L;

	/**
	*  javadoc for esito
	*/
	private String esito;

	@Column(name="esito")
	public String getEsito(){
		return esito;
	}

	public void setEsito(String esito){
		this.esito = esito;
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
	*  javadoc for visitaMdl
	*/
	private List<VisitaMdl> visitaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaExt", cascade=CascadeType.PERSIST)
	public List<VisitaMdl> getVisitaMdl(){
		return visitaMdl;
	}

	public void setVisitaMdl(List<VisitaMdl> list){
		visitaMdl = list;
	}

	public void addVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
		}
		// add the association
		if(!this.visitaMdl.contains(visitaMdl)) {
			this.visitaMdl.add(visitaMdl);
			// make the inverse link
			visitaMdl.setVisitaExt(this);
		}
	}

	public void removeVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
			return;
		}
		//add the association
		if(this.visitaMdl.contains(visitaMdl)){
			this.visitaMdl.remove(visitaMdl);
			//make the inverse link
			visitaMdl.setVisitaExt(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VisitaExt_sequence")
	@SequenceGenerator(name = "VisitaExt_sequence", sequenceName = "VisitaExt_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
