package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;

import com.phi.entities.baseEntity.Attivita;

@Entity
@Table(name = "oggetto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Oggetto extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 92149860482454746L;

	/**
	*  javadoc for codeLegge81
	*/
	private CodeValueLaw codeLegge81;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codeLegge81")
	@ForeignKey(name="FK_Oggetto_codeLegge81")
	//@Index(name="IX_Oggetto_codeLegge81")
	public CodeValueLaw getCodeLegge81(){
		return codeLegge81;
	}

	public void setCodeLegge81(CodeValueLaw codeLegge81){
		this.codeLegge81 = codeLegge81;
	}


	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Oggetto_attivita")
	//@Index(name="IX_Oggetto_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	/**
	*  javadoc for supervisionCode
	*/
	private CodeValuePhi supervisionCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supervisionCode")
	@ForeignKey(name="FK_Oggetto_supervisionCode")
	//@Index(name="IX_Oggetto_supervisionCode")
	public CodeValuePhi getSupervisionCode(){
		return supervisionCode;
	}

	public void setSupervisionCode(CodeValuePhi supervisionCode){
		this.supervisionCode = supervisionCode;
	}


	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Oggetto_procpratiche")
	//@Index(name="IX_Oggetto_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Oggetto_sequence")
	@SequenceGenerator(name = "Oggetto_sequence", sequenceName = "Oggetto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Ogg_code")
	//@Index(name="IX_Ogg_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private String note;

	@Column(name = "note")
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	private List<CodeValue> luoghiLavoro;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,targetEntity=CodeValuePhi.class)
	@JoinTable(name = "ogg_luoghi_lavoro", joinColumns = { @JoinColumn(name = "oggetto_id") }, inverseJoinColumns = { @JoinColumn(name = "luoghi_lavoro") })
	@ForeignKey(name = "FK_ogg_luoghiLavoro_ogg", inverseName = "FK_ogg_luoghiLavoro_cv")
	public List<CodeValue> getLuoghiLavoro() {
		return luoghiLavoro;
	}

	public void setLuoghiLavoro(List<CodeValue> luoghiLavoro) {
		this.luoghiLavoro = luoghiLavoro;
	}
	
	private List<CodeValue> siti;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,targetEntity=CodeValuePhi.class)
	@JoinTable(name = "ogg_siti", joinColumns = { @JoinColumn(name = "oggetto_id") }, inverseJoinColumns = { @JoinColumn(name = "siti") })
	@ForeignKey(name = "FK_ogg_siti_ogg", inverseName = "FK_ogg_siti_cv")
	public List<CodeValue> getSiti() {
		return siti;
	}

	public void setSiti(List<CodeValue> siti) {
		this.siti = siti;
	}
	
	private List<CodeValue> dpi;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,targetEntity=CodeValuePhi.class)
	@JoinTable(name = "ogg_dpi", joinColumns = { @JoinColumn(name = "oggetto_id") }, inverseJoinColumns = { @JoinColumn(name = "dpi") })
	@ForeignKey(name = "FK_ogg_dpi_ogg", inverseName = "FK_ogg_dpi_cv")
	public List<CodeValue> getDpi() {
		return dpi;
	}

	public void setDpi(List<CodeValue> dpi) {
		this.dpi = dpi;
	}
	
}
