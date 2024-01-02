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

import com.phi.entities.role.Operatore;

import java.util.List;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import com.phi.entities.baseEntity.Disp;

@javax.persistence.Entity
@Table(name = "monte_ore")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MonteOre extends BaseEntity {

	private static final long serialVersionUID = 469938114L;


	/**
	*  javadoc for disp
	*/
	private List<Disp> disp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="monteOre", cascade=CascadeType.PERSIST)
	public List<Disp> getDisp() {
		return disp;
	}

	public void setDisp(List<Disp>list){
		disp = list;
	}

	public void addDisp(Disp disp) {
		if (this.disp == null) {
			this.disp = new ArrayList<Disp>();
		}
		// add the association
		if(!this.disp.contains(disp)) {
			this.disp.add(disp);
			// make the inverse link
			disp.setMonteOre(this);
		}
	}

	public void removeDisp(Disp disp) {
		if (this.disp == null) {
			this.disp = new ArrayList<Disp>();
			return;
		}
		//add the association
		if(this.disp.contains(disp)){
			this.disp.remove(disp);
			//make the inverse link
			disp.setMonteOre(null);
		}
	}

	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_MonteOre_operatore")
	@Index(name="IX_MonteOre_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}

	/**
	*  javadoc for anno
	*/
	private Integer anno;

	@Column(name="anno")
	public Integer getAnno(){
		return anno;
	}

	public void setAnno(Integer anno){
		this.anno = anno;
	}

	/**
	*  javadoc for motivoScomputo
	*/
	private String motivoScomputo;

	@Column(name="motivo_scomputo")
	public String getMotivoScomputo(){
		return motivoScomputo;
	}

	public void setMotivoScomputo(String motivoScomputo){
		this.motivoScomputo = motivoScomputo;
	}

	/**
	*  javadoc for hhDisponibili
	*/
	private Double hhDisponibili;

	@Column(name="hh_disponibili")
	public Double getHhDisponibili(){
		if (this.hhContrattuali!=null){
			if (this.hhScomputo!=null)
				setHhDisponibili(this.hhContrattuali-this.hhScomputo);
			else
				setHhDisponibili(this.hhContrattuali);
		} else 
			setHhDisponibili(0.0);
		
		return hhDisponibili;
	}

	public void setHhDisponibili(Double hhDisponibili){
		this.hhDisponibili = hhDisponibili;
	}

	/**
	*  javadoc for hhScomputo
	*/
	private Double hhScomputo;

	@Column(name="hh_scomputo")
	public Double getHhScomputo(){
		return hhScomputo;
	}

	public void setHhScomputo(Double hhScomputo){
		this.hhScomputo = hhScomputo;
	}

	/**
	*  javadoc for hhContrattuali
	*/
	private Double hhContrattuali;

	@Column(name="hh_contrattuali")
	public Double getHhContrattuali(){
		return hhContrattuali;
	}

	public void setHhContrattuali(Double hhContrattuali){
		this.hhContrattuali = hhContrattuali;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MonteOre_sequence")
	@SequenceGenerator(name = "MonteOre_sequence", sequenceName = "MonteOre_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
