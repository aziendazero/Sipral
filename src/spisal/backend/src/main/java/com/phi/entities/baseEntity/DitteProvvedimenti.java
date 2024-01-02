package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

/**
 * Entità non storicizzata via ENVERS perchè non serve
 * @author 510087
 *
 */

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.PersoneGiuridiche;
@javax.persistence.Entity
@Table(name = "ditte_provvedimenti")
public class DitteProvvedimenti extends BaseEntity {

	private static final long serialVersionUID = 2145147781L;


	/**
	*  javadoc for ditte
	*/
	private PersoneGiuridiche ditte;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_id")
	@ForeignKey(name="FK_DitteProvvedimenti_ditte")
	//@Index(name="IX_DitteProvvedimenti_ditte")
	public PersoneGiuridiche getDitte(){
		return ditte;
	}

	public void setDitte(PersoneGiuridiche ditte){
		this.ditte = ditte;
	}

	
	/**
	*  la linea di lavoro della pratica
	*/
	private Long sdlId;

	@Column(name="sdl_id")
	//@Index(name="IX_ditteprovv_sdl")
	public Long getSdlId(){
		return sdlId;
	}

	public void setSdlId(Long sdlId){
		this.sdlId = sdlId;
	}
	
	/**
	*  il provvedimento
	*/
	private Long provId;

	@Column(name="prov_id")
	//@Index(name="IX_ditteprovv_provv")
	public Long getProvId(){
		return provId;
	}

	public void setProvId(Long provId){
		this.provId = provId;
	}
	
	/**
	*  la pratica del provvedimento
	*/
	private Long praticaId;

	@Column(name="pratica_id")
	//@Index(name="IX_ditteprovv_pratica")
	public Long getPraticaId(){
		return praticaId;
	}

	public void setPraticaId(Long praticaId){
		this.praticaId = praticaId;
	}

	/**
	*  il cf della ditta
	*/
	private String cf;

	@Column(name="cf")
	@Index(name="IX_ditteprovv_cf")
	public String getCf(){
		return cf;
	}

	public void setCf(String cf){
		this.cf = cf;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DitteProvvedimenti_sequence")
	@SequenceGenerator(name = "DitteProvvedimenti_sequence", sequenceName = "DitteProvvedimenti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
