
package com.phi.db.arpav.mapping;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mapping_indirizzo_sped")
public class MapIndirizzoSped extends GenericMapping {

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

}
