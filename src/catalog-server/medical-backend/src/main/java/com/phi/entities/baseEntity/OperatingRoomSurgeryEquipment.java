package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.SurgeryTools;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "operating_room_surg_equip")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class OperatingRoomSurgeryEquipment extends BaseEntity {

	private static final long serialVersionUID = 1642116599L;
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "operating_room_surg_equip_seq")
	@SequenceGenerator(name = "operating_room_surg_equip_seq", sequenceName = "operating_room_surg_equip_seq")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	protected Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	private SurgeryTools toolCharge;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="tool01", column=@Column(name="chrg_t01")),
		@AttributeOverride(name="tool02", column=@Column(name="chrg_t02")),
		@AttributeOverride(name="tool03", column=@Column(name="chrg_t03")),
		@AttributeOverride(name="tool04", column=@Column(name="chrg_t04")),
		@AttributeOverride(name="tool05", column=@Column(name="chrg_t05")),
		@AttributeOverride(name="tool06", column=@Column(name="chrg_t06")),
		@AttributeOverride(name="tool07", column=@Column(name="chrg_t07")),
		@AttributeOverride(name="tool08", column=@Column(name="chrg_t08")),
		@AttributeOverride(name="tool09", column=@Column(name="chrg_t09")),
		@AttributeOverride(name="tool10", column=@Column(name="chrg_t10")),
		@AttributeOverride(name="tool11", column=@Column(name="chrg_t11")),
		@AttributeOverride(name="tool12", column=@Column(name="chrg_t12")),
		@AttributeOverride(name="tool13", column=@Column(name="chrg_t13")),
		@AttributeOverride(name="tool14", column=@Column(name="chrg_t14")),
		@AttributeOverride(name="tool15", column=@Column(name="chrg_t15")),
		@AttributeOverride(name="tool16", column=@Column(name="chrg_t16")),
		@AttributeOverride(name="tool17", column=@Column(name="chrg_t17")),
		@AttributeOverride(name="tool18", column=@Column(name="chrg_t18")),
		@AttributeOverride(name="tool19", column=@Column(name="chrg_t19")),
		@AttributeOverride(name="tool20", column=@Column(name="chrg_t20")),
		@AttributeOverride(name="tool21", column=@Column(name="chrg_t21")),
		@AttributeOverride(name="tool22", column=@Column(name="chrg_t22")),
		@AttributeOverride(name="tool23", column=@Column(name="chrg_t23")),
		@AttributeOverride(name="tool24", column=@Column(name="chrg_t24")),
		@AttributeOverride(name="tool25", column=@Column(name="chrg_t25")),
		@AttributeOverride(name="tool26", column=@Column(name="chrg_t26")),
		@AttributeOverride(name="tool27", column=@Column(name="chrg_t27")),
		@AttributeOverride(name="tool28", column=@Column(name="chrg_t28")),
		@AttributeOverride(name="tool29", column=@Column(name="chrg_t29")),
		@AttributeOverride(name="tool30", column=@Column(name="chrg_t30"))
	})
	public SurgeryTools getToolCharge() {
		return toolCharge;
	}
	public void setToolCharge(SurgeryTools toolCharge) {
		this.toolCharge = toolCharge;
	}
	
	private SurgeryTools toolDischarge;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="tool01", column=@Column(name="dchrg_t01")),
		@AttributeOverride(name="tool02", column=@Column(name="dchrg_t02")),
		@AttributeOverride(name="tool03", column=@Column(name="dchrg_t03")),
		@AttributeOverride(name="tool04", column=@Column(name="dchrg_t04")),
		@AttributeOverride(name="tool05", column=@Column(name="dchrg_t05")),
		@AttributeOverride(name="tool06", column=@Column(name="dchrg_t06")),
		@AttributeOverride(name="tool07", column=@Column(name="dchrg_t07")),
		@AttributeOverride(name="tool08", column=@Column(name="dchrg_t08")),
		@AttributeOverride(name="tool09", column=@Column(name="dchrg_t09")),
		@AttributeOverride(name="tool10", column=@Column(name="dchrg_t10")),
		@AttributeOverride(name="tool11", column=@Column(name="dchrg_t11")),
		@AttributeOverride(name="tool12", column=@Column(name="dchrg_t12")),
		@AttributeOverride(name="tool13", column=@Column(name="dchrg_t13")),
		@AttributeOverride(name="tool14", column=@Column(name="dchrg_t14")),
		@AttributeOverride(name="tool15", column=@Column(name="dchrg_t15")),
		@AttributeOverride(name="tool16", column=@Column(name="dchrg_t16")),
		@AttributeOverride(name="tool17", column=@Column(name="dchrg_t17")),
		@AttributeOverride(name="tool18", column=@Column(name="dchrg_t18")),
		@AttributeOverride(name="tool19", column=@Column(name="dchrg_t19")),
		@AttributeOverride(name="tool20", column=@Column(name="dchrg_t20")),
		@AttributeOverride(name="tool21", column=@Column(name="dchrg_t21")),
		@AttributeOverride(name="tool22", column=@Column(name="dchrg_t22")),
		@AttributeOverride(name="tool23", column=@Column(name="dchrg_t23")),
		@AttributeOverride(name="tool24", column=@Column(name="dchrg_t24")),
		@AttributeOverride(name="tool25", column=@Column(name="dchrg_t25")),
		@AttributeOverride(name="tool26", column=@Column(name="dchrg_t26")),
		@AttributeOverride(name="tool27", column=@Column(name="dchrg_t27")),
		@AttributeOverride(name="tool28", column=@Column(name="dchrg_t28")),
		@AttributeOverride(name="tool29", column=@Column(name="dchrg_t29")),
		@AttributeOverride(name="tool30", column=@Column(name="dchrg_t30"))
	})
	public SurgeryTools getToolDischarge() {
		return toolDischarge;
	}
	public void setToolDischarge(SurgeryTools toolDischarge) {
		this.toolDischarge = toolDischarge;
	}
	
	protected Date availabilityTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "availability_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}

	private CodeValueRole authorRole;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_orse_auth_Role")
	@Index(name="IX_orse_auth_Role")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	
	private Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_orse_author")
	@Index(name="IX_orse_author")
	public Employee getAuthor(){
		return author;
	}
	
	public void setAuthor(Employee author){
		this.author = author;
	}

}