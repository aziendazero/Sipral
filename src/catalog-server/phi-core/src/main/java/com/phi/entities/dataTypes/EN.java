package com.phi.entities.dataTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Embeddable
@Audited
public class EN extends ANY implements Serializable {

	private static final long serialVersionUID = -8396501930867837116L;

	private Set<CodeValue> use;
	private IVL<Date> useablePeriod;
	private String formatted;

	private String fam;
	private String giv;
	private String pfx;
	private String sfx;
	private String del;

	public EN() {
	}

	public EN(String fam, String giv) {
		this.fam=fam;
		this.giv=giv;
	}
	
	public EN(String fam, String giv, String pfx, String sfx, String del) {
		this.fam=fam;
		this.giv=giv;
		this.pfx=pfx;
		this.sfx=sfx;
		this.del=del;
	}
	
	@Transient
	public Set<CodeValue> getUse() {
		return use;
	}

	public void setUse(Set<CodeValue> use) {
		this.use = use;
	}

	@Transient
	public IVL<Date> getUseablePeriod() {
		return useablePeriod;
	}

	public void setUseablePeriod(IVL<Date> useablePeriod) {
		this.useablePeriod = useablePeriod;
	}

	public String getGiv() {
		return giv;
	}

	public void setGiv(String giv) {
		this.giv = giv;
	}
	
	public String getFam() {
		return fam;
	}

	public void setFam(String fam) {
		this.fam = fam;
	}

	public String getPfx() {
		return pfx;
	}

	public void setPfx(String pfx) {
		this.pfx = pfx;
	}

	public String getSfx() {
		return sfx;
	}

	public void setSfx(String sfx) {
		this.sfx = sfx;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getFormatted() {
		return formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}

	@Override
	public String toString() {
		return giv + " " + fam; 
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((del == null) ? 0 : del.hashCode());
		result = prime * result + ((fam == null) ? 0 : fam.hashCode());
		result = prime * result
				+ ((formatted == null) ? 0 : formatted.hashCode());
		result = prime * result + ((giv == null) ? 0 : giv.hashCode());
		result = prime * result + ((pfx == null) ? 0 : pfx.hashCode());
		result = prime * result + ((sfx == null) ? 0 : sfx.hashCode());
		result = prime * result + ((use == null) ? 0 : use.hashCode());
		result = prime * result
				+ ((useablePeriod == null) ? 0 : useablePeriod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EN other = (EN) obj;
		if (del == null) {
			if (other.del != null)
				return false;
		} else if (!del.equals(other.del))
			return false;
		if (fam == null) {
			if (other.fam != null)
				return false;
		} else if (!fam.equals(other.fam))
			return false;
		if (formatted == null) {
			if (other.formatted != null)
				return false;
		} else if (!formatted.equals(other.formatted))
			return false;
		if (giv == null) {
			if (other.giv != null)
				return false;
		} else if (!giv.equals(other.giv))
			return false;
		if (pfx == null) {
			if (other.pfx != null)
				return false;
		} else if (!pfx.equals(other.pfx))
			return false;
		if (sfx == null) {
			if (other.sfx != null)
				return false;
		} else if (!sfx.equals(other.sfx))
			return false;
		if (use == null) {
			if (other.use != null)
				return false;
		} else if (!use.equals(other.use))
			return false;
		if (useablePeriod == null) {
			if (other.useablePeriod != null)
				return false;
		} else if (!useablePeriod.equals(other.useablePeriod))
			return false;
		return true;
	}

	public EN cloneEN () {
		
		EN e = new EN();
		e.fam=this.fam;
		e.giv=this.giv;
		e.pfx=this.pfx;
		e.sfx=this.sfx;
		e.del=this.del;
		return e;
	}
	
}