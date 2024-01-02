

package com.phi.db.arpav.mapping;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mapping_verifica_imp")
public class MapVerificaImp extends GenericMapping {


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
