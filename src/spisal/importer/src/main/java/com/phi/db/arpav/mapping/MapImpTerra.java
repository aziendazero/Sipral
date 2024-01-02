package com.phi.db.arpav.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mapping_imp_terra")
public class MapImpTerra extends GenericMapping {

	private String sigla;
	private String matricola;
	private String anno;
	private String subtype;
	private String dipartimento;
	
	/**
	 * Id entità nell'excel (copia)
	 * @return
	 */
	@Id
	@Override
	public String getIdexcel() {
		return idexcel;
	}
	@Override
	public void setIdexcel(String idexcel) {
		this.idexcel = idexcel;
	}

	@Column(length=4)
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	@Column(length=12)
	public String getMatricola() {
		return matricola;
	}
	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
	
	@Column(length=8)
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}

	@Column(length=2)
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	
	@Column(length=50)
	public String getDipartimento() {
		return dipartimento;
	}
	public void setDipartimento(String dipartimento) {
		this.dipartimento = dipartimento;
	}

	
}
