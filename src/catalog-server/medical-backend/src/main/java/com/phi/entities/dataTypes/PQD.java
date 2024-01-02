package com.phi.entities.dataTypes;

import java.util.Date;

/**
 * Defines a Phisical quantity (value, unit) with insertion date
 * @author Alex
 */
public class PQD extends PQ {
	
	private static final long serialVersionUID = 6528821609262586460L;
	
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	//Override of PQ methods because @Embeddable doesen't support inheritance

	@Override
	public Double getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Double value) {
		super.setValue(value);
	}

	@Override
	public String getUnit() {
		return super.getUnit();
	}

	@Override
	public void setUnit(String unit) {
		super.setUnit(unit);
	}

}