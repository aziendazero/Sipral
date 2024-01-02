package com.phi.entities.baseEntity;

import javax.persistence.Column;
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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.Sedi;

@javax.persistence.Entity
@Table(name = "ditte_malattie")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DitteMalattie extends BaseEntity {

	private static final long serialVersionUID = 661143088L;


	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_DitteMalattie_sedi")
	//@Index(name="IX_DitteMalattie_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}



	/**
	*  javadoc for malattiaProfessionale
	*/
	private MalattiaProfessionale malattiaProfessionale;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="malattia_professionale_id")
	@ForeignKey(name="FK_DttMalatt_malttProfssonl")
	//@Index(name="IX_DttMalatt_malttProfssonl")
	public MalattiaProfessionale getMalattiaProfessionale(){
		return malattiaProfessionale;
	}

	public void setMalattiaProfessionale(MalattiaProfessionale malattiaProfessionale){
		this.malattiaProfessionale = malattiaProfessionale;
	}



	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_DtteMal_personGurdch")
	//@Index(name="IX_DtteMal_personGurdch")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}


	/**
	*  javadoc for principale
	*/
	private boolean principale = false;

	@Column(name="principale")
	public boolean getPrincipale(){
		return principale;
	}

	public void setPrincipale(boolean principale){
		this.principale = principale;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DitteMalattie_sequence")
	@SequenceGenerator(name = "DitteMalattie_sequence", sequenceName = "DitteMalattie_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
