package com.phi.entities.dataTypes;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;

/**
 * Defines an address. 
 * Simplified HL7 type. 
 * To re enable some fields just uncomment.
 * @author Zupan
 */

@Embeddable
@Audited
public class AD extends ANY {

	private static final long serialVersionUID = -8664962948738787624L;

	public AD (){
	}
	
	public AD (String adl, String bnr, String cen, String cnt, String cpa, String cty, String sta, String stb, String str, String zip) {
		this.adl=adl;
		this.bnr=bnr;
		this.cen=cen;
		this.cnt=cnt;
		this.cpa=cpa;
		this.cty=cty;
		this.sta=sta;
		this.stb=stb;
		this.str=str;
		this.zip=zip;
	}
	
	/**
	 * Additional locator: This can be a unit designator, such as apartment number, suite number, or floor. There may be several unit designators in an address (e.g., "3rd floor, Appt. 342"). This can also be a designator pointing away from the location, rather than specifying a smaller location within some larger one (e.g., Dutch "t.o." means "opposite to" for house boats located across the street facing houses).
	 */
	private String adl;
	
//	/**
//	 * Building number numeric: The numeric portion of a building number
//	 */
//	private String bnn;
	
	/**
	 * Building number: The number of a building, house or lot alongside the street. Also known as "primary street number". This does not number the street but rather the building.
	 */
	private String bnr;
	
//	/**
//	 * Building number suffix: Any alphabetic character, fraction or other text that may appear after the numeric portion of a building number
//	 */
//	private String bns;
	
//	/**
//	 * Care of: The name of the party who will take receipt at the specified address, and will take on responsibility for ensuring delivery to the target recipient
//	 */
//	private String car;
	
	/**
	 * Census tract: A geographic sub-unit delineated for demographic purposes.
	 */
	private String cen;
	
	/**
	 * Country
	 */
	private String cnt;
	
	/**
	 * County or parish: A sub-unit of a state or province
	 */
	private String cpa;
	
	/**
	 * Municipality: The name of the city, town, village, or other community or delivery center
	 */
	private String cty;
	
//	/**
//	 * Delivery address line: A delivery address line is frequently used instead of breaking out delivery mode, delivery installation, etc. An address generally has only a delivery address line or a street address line, but not both.
//	 */
//	private String dal;
	
//	/**
//	 * Delimiter: Delimiters are printed without framing white space. If no value component is provided, the delimiter appears as a line break.
//	 */
//	private String del;
	
//	/**
//	 * Delivery installation type: Indicates the type of delivery installation (the facility to which the mail will be delivered prior to final shipping via the delivery mode.) Example: post office, letter carrier depot, community mail center, station, etc.
//	 */
//	private String dinst;
	
//	/**
//	 * Delivery installation area: The location of the delivery installation, usually a town or city, and is only required if the area is different from the municipality. Area to which mail delivery service is provided from any postal facility or service such as an individual letter carrier, rural route, or postal route.
//	 */
//	private String dinsta;
	
//	/**
//	 * Delivery installation qualifier: A number, letter or name identifying a delivery installation. E.g., for Station A, the delivery installation qualifier would be 'A'.
//	 */
//	private String dinstq;
	
//	/**
//	 * Direction: Direction (e.g., N, S, W, E)
//	 */
//	private String dir;
	
//	/**
//	 * Delivery mode: Indicates the type of service offered, method of delivery. For example: post office box, rural route, general delivery, etc.
//	 */
//	private String dmod;
	
//	/**
//	 * Delivery mode identifier: Represents the routing information such as a letter carrier route number. It is the identifying number of the designator (the box number or rural route number).
//	 */
//	private String dmodid;
	
//	/**
//	 * Post box: A numbered box located in a post station.
//	 */
//	private String pob;
	
//	/**
//	 * Precinct: A subsection of a municipality
//	 */
//	private String pre;
	
//	/**
//	 * Street address line 
//	 */
//	private String sal;
	
	/**
	 * State or province: A sub-unit of a country with limited sovereignty in a federally organized country.
	 */
	private String sta;
	
	/**
	 * Street name base: The base name of a roadway or artery recognized by a municipality (excluding street type and direction)
	 */
	private String stb;
	
	/**
	 * Street name 
	 */
	private String str;
	
//	/**
//	 * Street type: The designation given to the street. (e.g. Street, Avenue, Crescent, etc.)
//	 */
//	private String sttyp;
	
//	/**
//	 * Unit identifier: The number or name of a specific unit contained within a building or complex, as assigned by that building or complex.
//	 */
//	private String unid;
	
//	/**
//	 * Unit designator: Indicates the type of specific unit contained within a building or complex. E.g. Appartment, Floor
//	 */
//	private String unit;
	
	/**
	 * Postal code: A postal code designating a region defined by the postal service.
	 */
	private String zip;
	
	private CodeValueCity code;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="addr_code")
	//@ForeignKey(name="FK_AD_code") //duplicate name if defined
	public CodeValueCity getCode() {
		return code;
	}

	public void setCode(CodeValueCity code) {
		this.code = code;
	}

	
	
	
//	@Transient
//	public Set<CodeValue> getUse() {
//		return use;
//	}
//
//	public void setUse(Set<CodeValue> use) {
//		this.use = use;
//	}
//
//	@Transient
//	public Set<Date> getValidTime() {
//		return validTime;
//	}
//
//	public void setValidTime(Set<Date> validTime) {
//		this.validTime = validTime;
//	}
//	
//	@Transient
//	public Boolean getIsNotOrdered() {
//		return isNotOrdered;
//	}
//
//	public void setIsNotOrdered(Boolean isNotOrdered) {
//		this.isNotOrdered = isNotOrdered;
//	}

    @Column(name="addr_adl")
	public String getAdl() {
		return adl;
	}

	public void setAdl(String adl) {
		this.adl = adl;
	}

//	@Column(name="addr_bnn")
//	public String getBnn() {
//		return bnn;
//	}
//
//	public void setBnn(String bnn) {
//		this.bnn = bnn;
//	}

	@Column(name="addr_bnr")
	public String getBnr() {
		return bnr;
	}

	public void setBnr(String bnr) {
		this.bnr = bnr;
	}

//	@Column(name="addr_bns")
//	public String getBns() {
//		return bns;
//	}
//
//	public void setBns(String bns) {
//		this.bns = bns;
//	}

//	@Column(name="addr_car")
//	public String getCar() {
//		return car;
//	}
//
//	public void setCar(String car) {
//		this.car = car;
//	}

	@Column(name="addr_cen")
	public String getCen() {
		return cen;
	}

	public void setCen(String cen) {
		this.cen = cen;
	}

	@Column(name="addr_cnt")
	public String getCnt() {
		return cnt;
	}

	public void setCnt(String cnt) {
		this.cnt = cnt;
	}

	@Column(name="addr_cpa")
	public String getCpa() {
		return cpa;
	}

	public void setCpa(String cpa) {
		this.cpa = cpa;
	}

	@Column(name="addr_cty")
	public String getCty() {
		return cty;
	}

	public void setCty(String cty) {
		this.cty = cty;
	}

//	@Column(name="addr_dal")
//	public String getDal() {
//		return dal;
//	}
//
//	public void setDal(String dal) {
//		this.dal = dal;
//	}

//	@Column(name="addr_del")
//	public String getDel() {
//		return del;
//	}
//
//	public void setDel(String del) {
//		this.del = del;
//	}

//	@Column(name="addr_dinst")
//	public String getDinst() {
//		return dinst;
//	}
//
//	public void setDinst(String dinst) {
//		this.dinst = dinst;
//	}

//	@Column(name="addr_dinsta")
//	public String getDinsta() {
//		return dinsta;
//	}
//
//	public void setDinsta(String dinsta) {
//		this.dinsta = dinsta;
//	}

//	@Column(name="addr_dinstq")
//	public String getDinstq() {
//		return dinstq;
//	}
//
//	public void setDinstq(String dinstq) {
//		this.dinstq = dinstq;
//	}

//	@Column(name="addr_dir")
//	public String getDir() {
//		return dir;
//	}
//
//	public void setDir(String dir) {
//		this.dir = dir;
//	}

//	@Column(name="addr_dmod")
//	public String getDmod() {
//		return dmod;
//	}
//
//	public void setDmod(String dmod) {
//		this.dmod = dmod;
//	}

//	@Column(name="addr_dmodid")
//	public String getDmodid() {
//		return dmodid;
//	}
//
//	public void setDmodid(String dmodid) {
//		this.dmodid = dmodid;
//	}

//	@Column(name="addr_pob")
//	public String getPob() {
//		return pob;
//	}
//
//	public void setPob(String pob) {
//		this.pob = pob;
//	}

//	@Column(name="addr_pre")
//	public String getPre() {
//		return pre;
//	}
//
//	public void setPre(String pre) {
//		this.pre = pre;
//	}

//	@Column(name="addr_sal")
//	public String getSal() {
//		return sal;
//	}
//
//	public void setSal(String sal) {
//		this.sal = sal;
//	}

	@Column(name="addr_sta")
	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}

	@Column(name="addr_stb")
	public String getStb() {
		return stb;
	}

	public void setStb(String stb) {
		this.stb = stb;
	}

	@Column(name="addr_str")
	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

//	@Column(name="addr_sttyp")	
//	public String getSttyp() {
//		return sttyp;
//	}
//
//	public void setSttyp(String sttyp) {
//		this.sttyp = sttyp;
//	}

//	@Column(name="addr_unid")	
//	public String getUnid() {
//		return unid;
//	}
//
//	public void setUnid(String unid) {
//		this.unid = unid;
//	}

//	@Column(name="addr_unit")	
//	public String getUnit() {
//		return unit;
//	}
//
//	public void setUnit(String unit) {
//		this.unit = unit;
//	}

	@Column(name="addr_zip")
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

	
		//street address
		if (str != null && !str.isEmpty()) {
			sb.append(str).append(" ");
		}
//		sb.append(bnn).append(" ");
		//street number
		if (bnr != null && !bnr.isEmpty()) {
			sb.append(bnr).append(", ");
		}
		//street number (interno)
		if (stb != null && !stb.isEmpty()) {
			sb.append(stb).append(", ");
		}
		if (cen != null && !cen.isEmpty()) {
			sb.append(cen).append(" ");
		}
		//zip code
		if (zip != null && !zip.isEmpty()) {
			sb.append(zip).append(" - ");
		}
		
		//location
		if (adl != null && !adl.isEmpty()) {
			sb.append(adl).append(" ");
		}
		
		if (cty != null && !cty.isEmpty()) {
			sb.append(cty).append(" ");
		}
		if (cpa != null && !cpa.isEmpty()) {
			sb.append("(").append(cpa).append(") ");
		}
//		sb.append(bns).append(" ");
//		sb.append(car).append(" ");
		
		if (cnt != null && !cnt.isEmpty()) {
			sb.append(cnt).append(" ");
		}
		if (sta != null && !sta.isEmpty()) {
			sb.append(sta).append(" ");
		}
		
//		sb.append(dal).append(" ");
//		sb.append(del).append(" ");
//		sb.append(dinst).append(" ");
//		sb.append(dinsta).append(" ");
//		sb.append(dinstq).append(" ");
//		sb.append(dir).append(" ");
//		sb.append(dmod).append(" ");
//		sb.append(dmodid).append(" ");
//		sb.append(pob).append(" ");
//		sb.append(pre).append(" ");
//		sb.append(sal).append(" ");
//		sb.append(sttyp).append(" ");
//		sb.append(unid).append(" ");
//		sb.append(unit).append(" ");

		return sb.toString();
	}
	
	public String toString(String language) {
		
		if (language == null || language.isEmpty()){
			return this.toString();
		}
		
		StringBuffer sb = new StringBuffer();

		//street address
		if (str != null && !str.isEmpty()) {
			sb.append(str).append(" ");
		}
//		sb.append(bnn).append(" ");
		//street number
		if (bnr != null && !bnr.isEmpty()) {
			sb.append(bnr).append(", ");
		}
		//street number (interno)
		if (stb != null && !stb.isEmpty()) {
			sb.append(stb).append(", ");
		}
		if (cen != null && !cen.isEmpty()) {
			sb.append(cen).append(" ");
		}
		//zip code
		if (zip != null && !zip.isEmpty()) {
			sb.append(zip).append(" - ");
		}
//		if (cty != null) {
//			sb.append(cty).append(" ");
//		}
		
		//location
		if (adl != null && !adl.isEmpty()) {
			sb.append(adl).append(" ");
		}
		//traduzione nella lingua 
		if (code != null) {
			String transCity = code.getTranslation(language);
			if (transCity == null || transCity.isEmpty()){
				// se non c'è traduzione, uso cty
				transCity = cty;
			}
			if (transCity != null){
				sb.append(transCity).append(" ");
			}
		} else if (cty != null){
			sb.append(cty).append(" ");
		}
		if (cpa != null && !cpa.isEmpty()) {
			sb.append("(").append(cpa).append(") ");
		}
//		sb.append(bns).append(" ");
//		sb.append(car).append(" ");
		
		if (cnt != null && !cnt.isEmpty()) {
			sb.append(cnt).append(" ");
		}		
		if (sta != null && !sta.isEmpty()) {
			String transState = stateToString(language);
			sb.append(transState).append(" ");
		}
		
		
//		sb.append(dal).append(" ");
//		sb.append(del).append(" ");
//		sb.append(dinst).append(" ");
//		sb.append(dinsta).append(" ");
//		sb.append(dinstq).append(" ");
//		sb.append(dir).append(" ");
//		sb.append(dmod).append(" ");
//		sb.append(dmodid).append(" ");
//		sb.append(pob).append(" ");
//		sb.append(pre).append(" ");
//		sb.append(sal).append(" ");
//		sb.append(sttyp).append(" ");
//		sb.append(unid).append(" ");
//		sb.append(unit).append(" ");

		return sb.toString();
	}
	
	public String streetToString(){
		return streetToString(null);
	}
	
	public String streetToString(String separator){
		String ret ="";
		if (str != null && !str.isEmpty()){
			ret = str;
		}
		if (bnr != null && !bnr.isEmpty() /*&& !str.isEmpty()*/) {
			if (separator == null || separator.isEmpty()) {
				ret += " n. "+bnr;
			}
			else {
				ret+= separator+bnr;
			}
		}
		return ret;
	}
	
	public String cityToString(String language) {
		if (cty != null && !cty.isEmpty()){
			if (language == null || language.isEmpty()){
				return cty;
			}
			if (code != null) {
				String transCity = code.getTranslation(language);
				if (transCity == null || transCity.isEmpty()){
					// se non c'è traduzione, uso cty
					return cty;
				}
				if (transCity != null){
					return transCity;
				}
			} else {
				return cty;
			}
		} else if (cnt != null && !cnt.isEmpty()){ //città estere
				return cnt;
				
		} else if (code != null) { //solo code se uso il metodo pre-submit della form
			String transCity = code.getTranslation(language);
			if (transCity != null){
				return transCity;
			}	
		} 
		return "-";
	}
	
	public String stateToString(String language) {
		if (sta != null && !sta.isEmpty()) {
			Vocabularies vocabularies = VocabulariesImpl.instance();
			try {
				CodeValue statoCv = vocabularies.getCodeValueCsDomainCode("StatiEsteriIstat", null, sta);
				if (statoCv != null){
					String transState = statoCv.getTranslation(language);
					if (transState == null || transState.isEmpty()){
						//uso l'italiano
						return statoCv.getLangIt();
					} else {
						return transState;
					}
				} else {
					return sta;
				}
					
			} catch (PersistenceException e) {		
				e.printStackTrace();
			} catch (DictionaryException e) {
				e.printStackTrace();

			}
		} else {
			return "";
		}
		return "";
			
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adl == null) ? 0 : adl.hashCode());
//		result = prime * result + ((bnn == null) ? 0 : bnn.hashCode());
		result = prime * result + ((bnr == null) ? 0 : bnr.hashCode());
//		result = prime * result + ((bns == null) ? 0 : bns.hashCode());
//		result = prime * result + ((car == null) ? 0 : car.hashCode());
		result = prime * result + ((cen == null) ? 0 : cen.hashCode());
		result = prime * result + ((cnt == null) ? 0 : cnt.hashCode());
		result = prime * result + ((cpa == null) ? 0 : cpa.hashCode());
		result = prime * result + ((cty == null) ? 0 : cty.hashCode());
//		result = prime * result + ((dal == null) ? 0 : dal.hashCode());
//		result = prime * result + ((del == null) ? 0 : del.hashCode());
//		result = prime * result + ((dinst == null) ? 0 : dinst.hashCode());
//		result = prime * result + ((dinsta == null) ? 0 : dinsta.hashCode());
//		result = prime * result + ((dinstq == null) ? 0 : dinstq.hashCode());
//		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
//		result = prime * result + ((dmod == null) ? 0 : dmod.hashCode());
//		result = prime * result + ((dmodid == null) ? 0 : dmodid.hashCode());
//		result = prime * result
//				+ ((isNotOrdered == null) ? 0 : isNotOrdered.hashCode());
//		result = prime * result + ((pob == null) ? 0 : pob.hashCode());
//		result = prime * result + ((pre == null) ? 0 : pre.hashCode());
//		result = prime * result + ((sal == null) ? 0 : sal.hashCode());
		result = prime * result + ((sta == null) ? 0 : sta.hashCode());
		result = prime * result + ((stb == null) ? 0 : stb.hashCode());
		result = prime * result + ((str == null) ? 0 : str.hashCode());
//		result = prime * result + ((sttyp == null) ? 0 : sttyp.hashCode());
//		result = prime * result + ((unid == null) ? 0 : unid.hashCode());
//		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
//		result = prime * result + ((use == null) ? 0 : use.hashCode());
//		result = prime * result
//				+ ((validTime == null) ? 0 : validTime.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
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
		AD other = (AD) obj;
		if (adl == null || adl.isEmpty()) {
			if (other.adl != null && !other.adl.isEmpty())
				return false;
		} else if (!adl.equals(other.adl))
			return false;
//		if (bnn == null) {
//			if (other.bnn != null)
//				return false;
//		} else if (!bnn.equals(other.bnn))
//			return false;
		if (bnr == null || bnr.isEmpty()) {
			if (other.bnr != null && !other.bnr.isEmpty())
				return false;
		} else if (!bnr.equals(other.bnr))
			return false;
//		if (bns == null) {
//			if (other.bns != null)
//				return false;
//		} else if (!bns.equals(other.bns))
//			return false;
//		if (car == null) {
//			if (other.car != null)
//				return false;
//		} else if (!car.equals(other.car))
//			return false;
		if (cen == null || cen.isEmpty()) {
			if (other.cen != null && !other.cen.isEmpty())
				return false;
		} else if (!cen.equals(other.cen))
			return false;
		if (cnt == null || cnt.isEmpty()) {
			if (other.cnt != null && !other.cnt.isEmpty())
				return false;
		} else if (!cnt.equals(other.cnt))
			return false;
		if (cpa == null || cpa.isEmpty()) {
			if (other.cpa != null && !other.cpa.isEmpty())
				return false;
		} else if (!cpa.equals(other.cpa))
			return false;
		if (cty == null || cty.isEmpty()) {
			if (other.cty != null && !other.cty.isEmpty())
				return false;
		} else if (!cty.equals(other.cty))
			return false;
//		if (dal == null) {
//			if (other.dal != null)
//				return false;
//		} else if (!dal.equals(other.dal))
//			return false;
//		if (del == null) {
//			if (other.del != null)
//				return false;
//		} else if (!del.equals(other.del))
//			return false;
//		if (dinst == null) {
//			if (other.dinst != null)
//				return false;
//		} else if (!dinst.equals(other.dinst))
//			return false;
//		if (dinsta == null) {
//			if (other.dinsta != null)
//				return false;
//		} else if (!dinsta.equals(other.dinsta))
//			return false;
//		if (dinstq == null) {
//			if (other.dinstq != null)
//				return false;
//		} else if (!dinstq.equals(other.dinstq))
//			return false;
//		if (dir == null) {
//			if (other.dir != null)
//				return false;
//		} else if (!dir.equals(other.dir))
//			return false;
//		if (dmod == null) {
//			if (other.dmod != null)
//				return false;
//		} else if (!dmod.equals(other.dmod))
//			return false;
//		if (dmodid == null) {
//			if (other.dmodid != null)
//				return false;
//		} else if (!dmodid.equals(other.dmodid))
//			return false;
//		if (isNotOrdered == null) {
//			if (other.isNotOrdered != null)
//				return false;
//		} else if (!isNotOrdered.equals(other.isNotOrdered))
//			return false;
//		if (pob == null) {
//			if (other.pob != null)
//				return false;
//		} else if (!pob.equals(other.pob))
//			return false;
//		if (pre == null) {
//			if (other.pre != null)
//				return false;
//		} else if (!pre.equals(other.pre))
//			return false;
//		if (sal == null) {
//			if (other.sal != null)
//				return false;
//		} else if (!sal.equals(other.sal))
//			return false;
		if (sta == null || sta.isEmpty()) {
			if (other.sta != null && !other.sta.isEmpty())
				return false;
		} else if (!sta.equals(other.sta))
			return false;
		if (stb == null || stb.isEmpty()) {
			if (other.stb != null && !other.stb.isEmpty())
				return false;
		} else if (!stb.equals(other.stb))
			return false;
		if (str == null || str.isEmpty()) {
			if (other.str != null && !other.str.isEmpty())
				return false;
		} else if (!str.equals(other.str))
			return false;
//		if (sttyp == null) {
//			if (other.sttyp != null)
//				return false;
//		} else if (!sttyp.equals(other.sttyp))
//			return false;
//		if (unid == null) {
//			if (other.unid != null)
//				return false;
//		} else if (!unid.equals(other.unid))
//			return false;
//		if (unit == null) {
//			if (other.unit != null)
//				return false;
//		} else if (!unit.equals(other.unit))
//			return false;
//		if (use == null) {
//			if (other.use != null)
//				return false;
//		} else if (!use.equals(other.use))
//			return false;
//		if (validTime == null) {
//			if (other.validTime != null)
//				return false;
//		} else if (!validTime.equals(other.validTime))
//			return false;
		if (zip == null || zip.isEmpty()) {
			if (other.zip != null && !other.zip.isEmpty())
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	public AD cloneAd() {

		AD cloned = new AD();
		cloned.code=code;
		cloned.adl=adl;
		cloned.bnr=bnr;
		cloned.cen=cen;
		cloned.cnt=cnt;
		cloned.cpa=cpa;
		cloned.cty=cty;
		cloned.sta=sta;
		cloned.stb=stb;
		cloned.str=str;
		cloned.zip=zip;
		return cloned;
	}
	
}