package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

//@Entity
//@Table(name = "equipment_check")
@Embeddable
@Audited
public class EquipmentCheck implements Serializable {

	private static final long serialVersionUID = -1946015702098681758L;
//	private Long internalId;
//    
//	@Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "equipment_check_sequence")
//    @SequenceGenerator(name = "equipment_check_sequence", sequenceName = "equipment_check_sequence")
//    @Column(name="internal_id")
//	public Long getInternalId() {
//		return internalId;
//	}
//
//	public void setInternalId(Long internalId) {
//		this.internalId = internalId;
//	}

	// CHARGE
	private Integer chargeP1;

	public Integer getChargeP1() {
		return chargeP1;
	}

	public void setChargeP1(Integer chargeP1) {
		this.chargeP1 = chargeP1;
	}
	
	private Integer chargeP2;

	public Integer getChargeP2() {
		return chargeP2;
	}

	public void setChargeP2(Integer chargeP2) {
		this.chargeP2 = chargeP2;
	}

	private Integer chargeP3;

	public Integer getChargeP3() {
		return chargeP3;
	}

	public void setChargeP3(Integer chargeP3) {
		this.chargeP3 = chargeP3;
	}

	private Integer chargeP4;

	public Integer getChargeP4() {
		return chargeP4;
	}

	public void setChargeP4(Integer chargeP4) {
		this.chargeP4 = chargeP4;
	}

	private Integer chargeP5;

	public Integer getChargeP5() {
		return chargeP5;
	}

	public void setChargeP5(Integer chargeP5) {
		this.chargeP5 = chargeP5;
	}

	// DISCHARGE
	private Integer dischargeP1;

	public Integer getDischargeP1() {
		return dischargeP1;
	}

	public void setDischargeP1(Integer dischargeP1) {
		this.dischargeP1 = dischargeP1;
	}
	
	private Integer dischargeP2;

	public Integer getDischargeP2() {
		return dischargeP2;
	}

	public void setDischargeP2(Integer dischargeP2) {
		this.dischargeP2 = dischargeP2;
	}

	private Integer dischargeP3;

	public Integer getDischargeP3() {
		return dischargeP3;
	}

	public void setDischargeP3(Integer dischargeP3) {
		this.dischargeP3 = dischargeP3;
	}

	private Integer dischargeP4;

	public Integer getDischargeP4() {
		return dischargeP4;
	}

	public void setDischargeP4(Integer dischargeP4) {
		this.dischargeP4 = dischargeP4;
	}

	private Integer dischargeP5;

	public Integer getDischargeP5() {
		return dischargeP5;
	}

	public void setDischargeP5(Integer dischargeP5) {
		this.dischargeP5 = dischargeP5;
	}

	// CONFIRM
	private Boolean confirmP1;

	public Boolean getConfirmP1() {
		return confirmP1;
	}

	public void setConfirmP1(Boolean confirmP1) {
		this.confirmP1 = confirmP1;
	}
	
	private Boolean confirmP2;

	public Boolean getConfirmP2() {
		return confirmP2;
	}

	public void setConfirmP2(Boolean confirmP2) {
		this.confirmP2 = confirmP2;
	}

	private Boolean confirmP3;

	public Boolean getConfirmP3() {
		return confirmP3;
	}

	public void setConfirmP3(Boolean confirmP3) {
		this.confirmP3 = confirmP3;
	}

	private Boolean confirmP4;

	public Boolean getConfirmP4() {
		return confirmP4;
	}

	public void setConfirmP4(Boolean confirmP4) {
		this.confirmP4 = confirmP4;
	}

	private Boolean confirmP5;

	public Boolean getConfirmP5() {
		return confirmP5;
	}

	public void setConfirmP5(Boolean confirmP5) {
		this.confirmP5 = confirmP5;
	}

	private Boolean confirmPf;

	public Boolean getConfirmPf() {
		return confirmPf;
	}

	public void setConfirmPf(Boolean confirmPf) {
		this.confirmPf = confirmPf;
	}

}
