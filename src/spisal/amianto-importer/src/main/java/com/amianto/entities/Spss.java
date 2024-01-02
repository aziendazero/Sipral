package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the spss database table.
 * 
 */
@Entity
@Table(name="spss")
@NamedQuery(name="Spss.findAll", query="SELECT s FROM Spss s")
public class Spss implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String f1;
	private Date f2;
	private Double f3;
	private Date f4;
	private Double f5;

	public Spss() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(length=255)
	public String getF1() {
		return this.f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getF2() {
		return this.f2;
	}

	public void setF2(Date f2) {
		this.f2 = f2;
	}


	public Double getF3() {
		return this.f3;
	}

	public void setF3(Double f3) {
		this.f3 = f3;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getF4() {
		return this.f4;
	}

	public void setF4(Date f4) {
		this.f4 = f4;
	}


	public Double getF5() {
		return this.f5;
	}

	public void setF5(Double f5) {
		this.f5 = f5;
	}

}