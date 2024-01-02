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
import com.phi.entities.baseEntity.MonteOre;
@javax.persistence.Entity
@Table(name = "disp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Disp extends BaseEntity {

	private static final long serialVersionUID = 141015398L;

	/**
	*  javadoc for al
	*/
	private Date al;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="al")
	public Date getAl(){
		return al;
	}

	public void setAl(Date al){
		this.al = al;
	}

	/**
	*  javadoc for dal
	*/
	private Date dal;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dal")
	public Date getDal(){
		return dal;
	}

	public void setDal(Date dal){
		this.dal = dal;
	}

	/**
	*  javadoc for hh
	*/
	private Double hh;

	@Column(name="hh")
	public Double getHh(){
		return hh;
	}

	public void setHh(Double hh){
		this.hh = hh;
	}


	/**
	*  javadoc for monteOre
	*/
	private MonteOre monteOre;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="monte_ore_id")
	@ForeignKey(name="FK_Disp_monteOre")
	@Index(name="IX_Disp_monteOre")
	public MonteOre getMonteOre(){
		return monteOre;
	}

	public void setMonteOre(MonteOre monteOre){
		this.monteOre = monteOre;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Disp_sequence")
	@SequenceGenerator(name = "Disp_sequence", sequenceName = "Disp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
