package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.ForeignKey;

import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Cantiere;

import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "ditte_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Ditte in cantiere: rappresenta le varie figure (persone giuridiche) che lavorano nel cantiere
 * @author 510087
 *
 */
public class DitteCantiere extends BaseEntity {

	private static final long serialVersionUID = 826909245L;

//	/**
//	*  javadoc for checked
//	*/
//	private Boolean checked;
//
//	@Column(name="checked")
//	public Boolean getChecked(){
//		return checked;
//	}
//
//	public void setChecked(Boolean checked){
//		this.checked = checked;
//	}

	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="FK_DitteCantiere_ruolo")
	//@Index(name="IX_DitteCantiere_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
	}


	/**
	*  javadoc for cantiere
	*/
	private Cantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_DitteCantiere_cantiere")
	//@Index(name="IX_DitteCantiere_cantiere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}



	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_DtteCantere_personGurdch")
	//@Index(name="IX_DtteCantere_personGurdch")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DitteCantiere_sequence")
	@SequenceGenerator(name = "DitteCantiere_sequence", sequenceName = "DitteCantiere_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
