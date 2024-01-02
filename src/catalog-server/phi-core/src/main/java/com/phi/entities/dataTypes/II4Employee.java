package com.phi.entities.dataTypes;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Table(name="employee_id")
@Audited
public class II4Employee extends IIEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7816128046847360422L;

	

}
