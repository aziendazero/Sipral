package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

/**
 * Defines cx Extended Composite ID with Check Digit  
 * Simplified HL7 type.
 * To re enable some fields just uncomment. 
 * @author Zanutto
 */

/**
 * @author phidoc
 *
 */
@Embeddable
@Audited
public class CX implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9129701294216920981L;

	/**
	 * ID Number (ST)
	 */
	private String id;
	
	/**
	 * Check Digit (ST) 
	 */	
	private String ck;	
	
//	/**
//	 * Check Digit Scheme (ID)  
//	 */	
//	private String cks;	
	
//	/**
//	 * Assigning Authority (HD) 
//	 */		
//	private String aa;	
	
	/**
	 * Identifier Type Code (ID)  
	 */		
	private String itc;	
	
//	/**
//	 * Assigning Facility (HD)   
//	 */			
//	private String af;
	
//	/**
//	 * Effective Date (DT)   
//	 */		
//	private Date efd;	
	
//	/**
//	 * Expiration Date (DT)   
//	 */	
//	private Date exd;
	
//	/**
//	 * Assigning Jurisdiction (CWE)    
//	 */	 	
//	private String aj;
	
//	/**
//	 * Assigning Agency or Department (CWE)    
//	 */		
//	private String aad;

    @Column(name="cx_id")	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    @Column(name="cx_ck")	
	public String getCk() {
		return ck;
	}

	public void setCk(String ck) {
		this.ck = ck;
	}

//    @Column(name="cx_cks")	
//	public String getCks() {
//		return cks;
//	}
//
//	public void setCks(String cks) {
//		this.cks = cks;
//	}
//
//    @Column(name="cx_aa")		
//	public String getAa() {
//		return aa;
//	}
//
//	public void setAa(String aa) {
//		this.aa = aa;
//	}

    @Column(name="cx_itc")	
	public String getItc() {
		return itc;
	}

	public void setItc(String itc) {
		this.itc = itc;
	}

//    @Column(name="cx_af")	
//	public String getAf() {
//		return af;
//	}
//
//	public void setAf(String af) {
//		this.af = af;
//	}
//
//	@Temporal(TemporalType.TIMESTAMP)
//    @Column(name="cx_efd")	
//	public Date getEfd() {
//		return efd;
//	}
//
//	public void setEfd(Date efd) {
//		this.efd = efd;
//	}
//
//	@Temporal(TemporalType.TIMESTAMP)
//    @Column(name="cx_exd")	
//	public Date getExd() {
//		return exd;
//	}
//
//	public void setExd(Date exd) {
//		this.exd = exd;
//	}
//
//    @Column(name="cx_aj")		
//	public String getAj() {
//		return aj;
//	}
//
//	public void setAj(String aj) {
//		this.aj = aj;
//	}
//
//    @Column(name="cx_aad")		
//	public String getAad() {
//		return aad;
//	}
//
//	public void setAad(String aad) {
//		this.aad = aad;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((aa == null) ? 0 : aa.hashCode());
//		result = prime * result + ((aad == null) ? 0 : aad.hashCode());
//		result = prime * result + ((af == null) ? 0 : af.hashCode());
//		result = prime * result + ((aj == null) ? 0 : aj.hashCode());
		result = prime * result + ((ck == null) ? 0 : ck.hashCode());
//		result = prime * result + ((cks == null) ? 0 : cks.hashCode());
//		result = prime * result + ((efd == null) ? 0 : efd.hashCode());
//		result = prime * result + ((exd == null) ? 0 : exd.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((itc == null) ? 0 : itc.hashCode());
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
		CX other = (CX) obj;
//		if (aa == null) {
//			if (other.aa != null)
//				return false;
//		} else if (!aa.equals(other.aa))
//			return false;
//		if (aad == null) {
//			if (other.aad != null)
//				return false;
//		} else if (!aad.equals(other.aad))
//			return false;
//		if (af == null) {
//			if (other.af != null)
//				return false;
//		} else if (!af.equals(other.af))
//			return false;
//		if (aj == null) {
//			if (other.aj != null)
//				return false;
//		} else if (!aj.equals(other.aj))
//			return false;
		if (ck == null) {
			if (other.ck != null)
				return false;
		} else if (!ck.equals(other.ck))
			return false;
//		if (cks == null) {
//			if (other.cks != null)
//				return false;
//		} else if (!cks.equals(other.cks))
//			return false;
//		if (efd == null) {
//			if (other.efd != null)
//				return false;
//		} else if (!efd.equals(other.efd))
//			return false;
//		if (exd == null) {
//			if (other.exd != null)
//				return false;
//		} else if (!exd.equals(other.exd))
//			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (itc == null) {
			if (other.itc != null)
				return false;
		} else if (!itc.equals(other.itc))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (id != null) {
			builder.append(id).append(" ");
		}

		if (ck != null) {		
			builder.append(ck).append(" ");
		}

//		if (cks != null) {		
//			builder.append(cks).append(" ");
//		}
//
//		if (aa != null) {		
//			builder.append(aa).append(" ");
//		}

		if (itc != null) {		
			builder.append(itc).append(" ");
		}

//		if (af != null) {		
//			builder.append(af).append(" ");
//		}			
//
//		if (efd != null) {		
//			builder.append(efd).append(" ");
//		}
//
//		if (exd != null) {		
//			builder.append(exd).append(" ");
//		}
//
//		if (aj != null) {		
//			builder.append(aj).append(" ");
//		}
//
//		if (aad != null) {		
//			builder.append(aad).append(" ");
//		}

		return builder.toString();
	}

}
