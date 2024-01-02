package com.phi.entities.dataTypes;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Table(name="patient_id")
@Audited
public class II4Patient extends IIEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1021098563235376479L;

	

}
