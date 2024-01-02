package com.prevnet.mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MAPPING_PERSONEFISICHE")
public class MapPersoneFisiche extends GenericMapping {
	private static final long serialVersionUID = 1L;
	
	private String mpi;
	private String migPersonaInt;
	
	@Column(name="MPI")
	public String getMpi() {
		return mpi;
	}
	public void setMpi(String mpi) {
		this.mpi = mpi;
	}
	@Column(name="MIG_PERSONA_INT")
	public String getMigPersonaInt() {
		return migPersonaInt;
	}
	public void setMigPersonaInt(String migPersonaInt) {
		this.migPersonaInt = migPersonaInt;
	}
	
	
	/**
	 * Id entità di origine
	 * @return
	 */
	@Override
	@Id
	public long getIdprevnet() {
		return idprevnet;
	}
	@Override
	public void setIdprevnet(long idprevnet) {
		this.idprevnet = idprevnet;
	}
}
