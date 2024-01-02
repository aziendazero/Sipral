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
import org.hibernate.envers.NotAudited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.PNCCantiere;
@javax.persistence.Entity
@Table(name = "pnc_pec_notifica_destinatari")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Audited
public class PNCPecNotificaDest extends BaseEntity {

	private static final long serialVersionUID = 1446793018L;


	/**
	*  javadoc for pNCCantiere
	*/
	private PNCCantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="pnc_pec_notifica_destinatari_FK")
	//@Index(name="IX_PNCPcNtificaDst_pNCCantr")
	public PNCCantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(PNCCantiere pNCCantiere){
		this.cantiere = pNCCantiere;
	}


	/**
	*  javadoc for checkOnit
	*/
	private Boolean checkOnit;

	@Column(name="check_ONIT")
	public Boolean getCheckOnit(){
		return checkOnit;
	}

	public void setCheckOnit(Boolean checkOnit){
		this.checkOnit = checkOnit;
	}

	/**
	*  javadoc for tipoInvio
	*/
	private CodeValuePhi tipoInvio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo_invio")
	@ForeignKey(name="FK_PNCPecNotDest_tipoInvio")
	@Index(name="IX_PNCPecNotDest_tipoInvio")
	public CodeValuePhi getTipoInvio(){
		return tipoInvio;
	}

	public void setTipoInvio(CodeValuePhi tipoInvio){
		this.tipoInvio = tipoInvio;
	}

	/**
	*  javadoc for pec
	*/
	private String pec;

	@Column(name="pec")
	public String getPec(){
		return pec;
	}

	public void setPec(String pec){
		this.pec = pec;
	}

	/**
	*  javadoc for idNotifica
	*/
	private String idNotifica;

	@Column(name="id_notifica")
	public String getIdNotifica(){
		return idNotifica;
	}

	public void setIdNotifica(String idNotifica){
		this.idNotifica = idNotifica;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCPecNotDest_sequence")
	@SequenceGenerator(name = "PNCPecNotDest_sequence", sequenceName = "PNCPecNotDest_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
