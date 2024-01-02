
package com.phi.aur.json;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "mpi",
    "fiscalCode",
    "stp",
    "stpDateBegin",
    "stpDateEnd",
    "cs",
    "csRegion",
    "csDateBegin",
    "csDateEnd",
    "eni",
    "eniDateBegin",
    "eniDateEnd",
    "teamPers",
    "teamInst",
    "teamDateEnd",
    "teamIdent",
    "teamCode",
    "nameFam",
    "nameGiv",
    "birthTime",
    "genderCode",
    "birthplaceCode",
    "countryOfBirth",
    "addrStr",
    "addrStb",
    "addrBnr",
    "addrCode",
    "countryOfDom",
    "domAddrStb",
    "domAddrBnr",
    "domAddrCode",
    "countryOfAddr",
    "telecomH",
    "telecomHp",
    "telecomBad",
    "telecomMc",
    "telecomMail",
    "telecomTmp",
    "telecomEc",
    "telecomPg",
    "deathDate",
    "reliability"
})
public class Person {

    @JsonProperty("mpi")
    private String mpi;
    @JsonProperty("fiscalCode")
    private String fiscalCode;
    @JsonProperty("stp")
    private String stp;
    @JsonProperty("stpDateBegin")
    private String stpDateBegin;
    @JsonProperty("stpDateEnd")
    private String stpDateEnd;
    @JsonProperty("cs")
    private String cs;
    @JsonProperty("csRegion")
    private String csRegion;
    @JsonProperty("csDateBegin")
    private String csDateBegin;
    @JsonProperty("csDateEnd")
    private String csDateEnd;
    @JsonProperty("eni")
    private String eni;
    @JsonProperty("eniDateBegin")
    private String eniDateBegin;
    @JsonProperty("eniDateEnd")
    private String eniDateEnd;
    @JsonProperty("teamPers")
    private String teamPers;
    @JsonProperty("teamInst")
    private String teamInst;
    @JsonProperty("teamDateEnd")
    private String teamDateEnd;
    @JsonProperty("teamIdent")
    private String teamIdent;
    @JsonProperty("teamCode")
    private String teamCode;
    @JsonProperty("nameFam")
    private String nameFam;
    @JsonProperty("nameGiv")
    private String nameGiv;
    @JsonProperty("birthTime")
    private String birthTime;
    @JsonProperty("genderCode")
    private String genderCode;
    @JsonProperty("birthplaceCode")
    private String birthplaceCode;
    @JsonProperty("countryOfBirth")
    private String countryOfBirth;
    @JsonProperty("addrStb")
    private String addrStb;
    @JsonProperty("addrStr")
    private String addrStr;
    @JsonProperty("addrBnr")
    private String addrBnr;
    @JsonProperty("addrCode")
    private String addrCode;
    @JsonProperty("countryOfDom")
    private String countryOfDom;
    @JsonProperty("domAddrStb")
    private String domAddrStb;
    @JsonProperty("domAddrStr")
    private String domAddrStr;
    @JsonProperty("domAddrBnr")
    private String domAddrBnr;
    @JsonProperty("domAddrCode")
    private String domAddrCode;
    @JsonProperty("countryOfAddr")
    private String countryOfAddr;
    @JsonProperty("telecomH")
    private String telecomH;
    @JsonProperty("telecomHp")
    private String telecomHp;
    @JsonProperty("telecomBad")
    private String telecomBad;
    @JsonProperty("telecomMc")
    private String telecomMc;
    @JsonProperty("telecomMail")
    private String telecomMail;
    @JsonProperty("telecomTmp")
    private String telecomTmp;
    @JsonProperty("telecomEc")
    private String telecomEc;
    @JsonProperty("telecomPg")
    private String telecomPg;
    @JsonProperty("deathDate")
    private String deathDate;
    @JsonProperty("reliability")
    private String reliability;
    @JsonProperty("mmgRegionalCode")
    private String mmgRegionalCode;
    @JsonProperty("mmgNameFam")
    private String mmgNameFam;
    @JsonProperty("mmgNameGiv")
    private String mmgNameGiv;
    @JsonProperty("mmgDateBegin")
    private String mmgDateBegin;
    @JsonProperty("mmgDateEnd")
    private String mmgDateEnd;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The mpi
     */
    @JsonProperty("mpi")
    public String getMpi() {
        return mpi;
    }

    /**
     * 
     * @param mpi
     *     The mpi
     */
    @JsonProperty("mpi")
    public void setMpi(String mpi) {
        this.mpi = mpi;
    }

    /**
     * 
     * @return
     *     The fiscalCode
     */
    @JsonProperty("fiscalCode")
    public String getFiscalCode() {
        return fiscalCode;
    }

    /**
     * 
     * @param fiscalCode
     *     The fiscalCode
     */
    @JsonProperty("fiscalCode")
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    /**
     * 
     * @return
     *     The stp
     */
    @JsonProperty("stp")
    public String getStp() {
        return stp;
    }

    /**
     * 
     * @param stp
     *     The stp
     */
    @JsonProperty("stp")
    public void setStp(String stp) {
        this.stp = stp;
    }

    /**
     * 
     * @return
     *     The stpDateBegin
     */
    @JsonProperty("stpDateBegin")
    public String getStpDateBegin() {
        return stpDateBegin;
    }

    /**
     * 
     * @param stpDateBegin
     *     The stpDateBegin
     */
    @JsonProperty("stpDateBegin")
    public void setStpDateBegin(String stpDateBegin) {
        this.stpDateBegin = stpDateBegin;
    }

    /**
     * 
     * @return
     *     The stpDateEnd
     */
    @JsonProperty("stpDateEnd")
    public String getStpDateEnd() {
        return stpDateEnd;
    }

    /**
     * 
     * @param stpDateEnd
     *     The stpDateEnd
     */
    @JsonProperty("stpDateEnd")
    public void setStpDateEnd(String stpDateEnd) {
        this.stpDateEnd = stpDateEnd;
    }

    /**
     * 
     * @return
     *     The cs
     */
    @JsonProperty("cs")
    public String getCs() {
        return cs;
    }

    /**
     * 
     * @param cs
     *     The cs
     */
    @JsonProperty("cs")
    public void setCs(String cs) {
        this.cs = cs;
    }

    /**
     * 
     * @return
     *     The csRegion
     */
    @JsonProperty("csRegion")
    public String getCsRegion() {
        return csRegion;
    }

    /**
     * 
     * @param csRegion
     *     The csRegion
     */
    @JsonProperty("csRegion")
    public void setCsRegion(String csRegion) {
        this.csRegion = csRegion;
    }

    /**
     * 
     * @return
     *     The csDateBegin
     */
    @JsonProperty("csDateBegin")
    public String getCsDateBegin() {
        return csDateBegin;
    }

    /**
     * 
     * @param csDateBegin
     *     The csDateBegin
     */
    @JsonProperty("csDateBegin")
    public void setCsDateBegin(String csDateBegin) {
        this.csDateBegin = csDateBegin;
    }

    /**
     * 
     * @return
     *     The csDateEnd
     */
    @JsonProperty("csDateEnd")
    public String getCsDateEnd() {
        return csDateEnd;
    }

    /**
     * 
     * @param csDateEnd
     *     The csDateEnd
     */
    @JsonProperty("csDateEnd")
    public void setCsDateEnd(String csDateEnd) {
        this.csDateEnd = csDateEnd;
    }

    /**
     * 
     * @return
     *     The eni
     */
    @JsonProperty("eni")
    public String getEni() {
        return eni;
    }

    /**
     * 
     * @param eni
     *     The eni
     */
    @JsonProperty("eni")
    public void setEni(String eni) {
        this.eni = eni;
    }

    /**
     * 
     * @return
     *     The eniDateBegin
     */
    @JsonProperty("eniDateBegin")
    public String getEniDateBegin() {
        return eniDateBegin;
    }

    /**
     * 
     * @param eniDateBegin
     *     The eniDateBegin
     */
    @JsonProperty("eniDateBegin")
    public void setEniDateBegin(String eniDateBegin) {
        this.eniDateBegin = eniDateBegin;
    }

    /**
     * 
     * @return
     *     The eniDateEnd
     */
    @JsonProperty("eniDateEnd")
    public String getEniDateEnd() {
        return eniDateEnd;
    }

    /**
     * 
     * @param eniDateEnd
     *     The eniDateEnd
     */
    @JsonProperty("eniDateEnd")
    public void setEniDateEnd(String eniDateEnd) {
        this.eniDateEnd = eniDateEnd;
    }

    /**
     * 
     * @return
     *     The teamPers
     */
    @JsonProperty("teamPers")
    public String getTeamPers() {
        return teamPers;
    }

    /**
     * 
     * @param teamPers
     *     The teamPers
     */
    @JsonProperty("teamPers")
    public void setTeamPers(String teamPers) {
        this.teamPers = teamPers;
    }

    /**
     * 
     * @return
     *     The teamInst
     */
    @JsonProperty("teamInst")
    public String getTeamInst() {
        return teamInst;
    }

    /**
     * 
     * @param teamInst
     *     The teamInst
     */
    @JsonProperty("teamInst")
    public void setTeamInst(String teamInst) {
        this.teamInst = teamInst;
    }

    /**
     * 
     * @return
     *     The teamDateEnd
     */
    @JsonProperty("teamDateEnd")
    public String getTeamDateEnd() {
        return teamDateEnd;
    }

    /**
     * 
     * @param teamDateEnd
     *     The teamDateEnd
     */
    @JsonProperty("teamDateEnd")
    public void setTeamDateEnd(String teamDateEnd) {
        this.teamDateEnd = teamDateEnd;
    }

    /**
     * 
     * @return
     *     The teamIdent
     */
    @JsonProperty("teamIdent")
    public String getTeamIdent() {
        return teamIdent;
    }

    /**
     * 
     * @param teamIdent
     *     The teamIdent
     */
    @JsonProperty("teamIdent")
    public void setTeamIdent(String teamIdent) {
        this.teamIdent = teamIdent;
    }

    /**
     * 
     * @return
     *     The teamCode
     */
    @JsonProperty("teamCode")
    public String getTeamCode() {
        return teamCode;
    }

    /**
     * 
     * @param teamCode
     *     The teamCode
     */
    @JsonProperty("teamCode")
    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    /**
     * 
     * @return
     *     The nameFam
     */
    @JsonProperty("nameFam")
    public String getNameFam() {
        return nameFam;
    }

    /**
     * 
     * @param nameFam
     *     The nameFam
     */
    @JsonProperty("nameFam")
    public void setNameFam(String nameFam) {
        this.nameFam = nameFam;
    }

    /**
     * 
     * @return
     *     The nameGiv
     */
    @JsonProperty("nameGiv")
    public String getNameGiv() {
        return nameGiv;
    }

    /**
     * 
     * @param nameGiv
     *     The nameGiv
     */
    @JsonProperty("nameGiv")
    public void setNameGiv(String nameGiv) {
        this.nameGiv = nameGiv;
    }

    /**
     * 
     * @return
     *     The birthTime
     */
    @JsonProperty("birthTime")
    public String getBirthTime() {
        return birthTime;
    }

    /**
     * 
     * @param birthTime
     *     The birthTime
     */
    @JsonProperty("birthTime")
    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    /**
     * 
     * @return
     *     The genderCode
     */
    @JsonProperty("genderCode")
    public String getGenderCode() {
        return genderCode;
    }

    /**
     * 
     * @param genderCode
     *     The genderCode
     */
    @JsonProperty("genderCode")
    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    /**
     * 
     * @return
     *     The birthplaceCode
     */
    @JsonProperty("birthplaceCode")
    public String getBirthplaceCode() {
        return birthplaceCode;
    }

    /**
     * 
     * @param birthplaceCode
     *     The birthplaceCode
     */
    @JsonProperty("birthplaceCode")
    public void setBirthplaceCode(String birthplaceCode) {
        this.birthplaceCode = birthplaceCode;
    }

    /**
     * 
     * @return
     *     The countryOfBirth
     */
    @JsonProperty("countryOfBirth")
    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    /**
     * 
     * @param countryOfBirth
     *     The countryOfBirth
     */
    @JsonProperty("countryOfBirth")
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    /**
     * 
     * @return
     *     The addrStb
     */
    @JsonProperty("addrStb")
    public String getAddrStb() {
        return addrStb;
    }

    /**
     * 
     * @param addrStb
     *     The addrStb
     */
    @JsonProperty("addrStb")
    public void setAddrStb(String addrStb) {
        this.addrStb = addrStb;
    }
    
    /**
     * 
     * @return
     *     The addrStb
     */
    @JsonProperty("addrStr")
    public String getAddrStr() {
        return addrStr;
    }

    /**
     * 
     * @param addrStb
     *     The addrStb
     */
    @JsonProperty("addrStr")
    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }


    /**
     * 
     * @return
     *     The addrBnr
     */
    @JsonProperty("addrBnr")
    public String getAddrBnr() {
        return addrBnr;
    }

    /**
     * 
     * @param addrBnr
     *     The addrBnr
     */
    @JsonProperty("addrBnr")
    public void setAddrBnr(String addrBnr) {
        this.addrBnr = addrBnr;
    }

    /**
     * 
     * @return
     *     The addrCode
     */
    @JsonProperty("addrCode")
    public String getAddrCode() {
        return addrCode;
    }

    /**
     * 
     * @param addrCode
     *     The addrCode
     */
    @JsonProperty("addrCode")
    public void setAddrCode(String addrCode) {
        this.addrCode = addrCode;
    }

    /**
     * 
     * @return
     *     The countryOfDom
     */
    @JsonProperty("countryOfDom")
    public String getCountryOfDom() {
        return countryOfDom;
    }

    /**
     * 
     * @param countryOfDom
     *     The countryOfDom
     */
    @JsonProperty("countryOfDom")
    public void setCountryOfDom(String countryOfDom) {
        this.countryOfDom = countryOfDom;
    }

    /**
     * 
     * @return
     *     The domAddrStb
     */
    @JsonProperty("domAddrStb")
    public String getDomAddrStb() {
        return domAddrStb;
    }

    /**
     * 
     * @param domAddrStb
     *     The domAddrStb
     */
    @JsonProperty("domAddrStb")
    public void setDomAddrStb(String domAddrStb) {
        this.domAddrStb = domAddrStb;
    }

    
    /**
     * 
     * @return
     *     The domAddrStr
     */
    @JsonProperty("domAddrStr")
    public String getDomAddrStr() {
        return domAddrStr;
    }

    /**
     * 
     * @param domAddrStr
     *     The domAddrStr
     */
    @JsonProperty("domAddrStr")
    public void setDomAddrStr(String domAddrStr) {
        this.domAddrStr = domAddrStr;
    }

    /**
     * 
     * @return
     *     The domAddrBnr
     */
    @JsonProperty("domAddrBnr")
    public String getDomAddrBnr() {
        return domAddrBnr;
    }

    /**
     * 
     * @param domAddrBnr
     *     The domAddrBnr
     */
    @JsonProperty("domAddrBnr")
    public void setDomAddrBnr(String domAddrBnr) {
        this.domAddrBnr = domAddrBnr;
    }

    /**
     * 
     * @return
     *     The domAddrCode
     */
    @JsonProperty("domAddrCode")
    public String getDomAddrCode() {
        return domAddrCode;
    }

    /**
     * 
     * @param domAddrCode
     *     The domAddrCode
     */
    @JsonProperty("domAddrCode")
    public void setDomAddrCode(String domAddrCode) {
        this.domAddrCode = domAddrCode;
    }

    /**
     * 
     * @return
     *     The countryOfAddr
     */
    @JsonProperty("countryOfAddr")
    public String getCountryOfAddr() {
        return countryOfAddr;
    }

    /**
     * 
     * @param countryOfAddr
     *     The countryOfAddr
     */
    @JsonProperty("countryOfAddr")
    public void setCountryOfAddr(String countryOfAddr) {
        this.countryOfAddr = countryOfAddr;
    }

    /**
     * 
     * @return
     *     The telecomH
     */
    @JsonProperty("telecomH")
    public String getTelecomH() {
        return telecomH;
    }

    /**
     * 
     * @param telecomH
     *     The telecomH
     */
    @JsonProperty("telecomH")
    public void setTelecomH(String telecomH) {
        this.telecomH = telecomH;
    }

    /**
     * 
     * @return
     *     The telecomHp
     */
    @JsonProperty("telecomHp")
    public String getTelecomHp() {
        return telecomHp;
    }

    /**
     * 
     * @param telecomHp
     *     The telecomHp
     */
    @JsonProperty("telecomHp")
    public void setTelecomHp(String telecomHp) {
        this.telecomHp = telecomHp;
    }

    /**
     * 
     * @return
     *     The telecomBad
     */
    @JsonProperty("telecomBad")
    public String getTelecomBad() {
        return telecomBad;
    }

    /**
     * 
     * @param telecomBad
     *     The telecomBad
     */
    @JsonProperty("telecomBad")
    public void setTelecomBad(String telecomBad) {
        this.telecomBad = telecomBad;
    }

    /**
     * 
     * @return
     *     The telecomMc
     */
    @JsonProperty("telecomMc")
    public String getTelecomMc() {
        return telecomMc;
    }

    /**
     * 
     * @param telecomMc
     *     The telecomMc
     */
    @JsonProperty("telecomMc")
    public void setTelecomMc(String telecomMc) {
        this.telecomMc = telecomMc;
    }

    /**
     * 
     * @return
     *     The telecomMail
     */
    @JsonProperty("telecomMail")
    public String getTelecomMail() {
        return telecomMail;
    }

    /**
     * 
     * @param telecomMail
     *     The telecomMail
     */
    @JsonProperty("telecomMail")
    public void setTelecomMail(String telecomMail) {
        this.telecomMail = telecomMail;
    }

    /**
     * 
     * @return
     *     The telecomTmp
     */
    @JsonProperty("telecomTmp")
    public String getTelecomTmp() {
        return telecomTmp;
    }

    /**
     * 
     * @param telecomTmp
     *     The telecomTmp
     */
    @JsonProperty("telecomTmp")
    public void setTelecomTmp(String telecomTmp) {
        this.telecomTmp = telecomTmp;
    }

    /**
     * 
     * @return
     *     The telecomEc
     */
    @JsonProperty("telecomEc")
    public String getTelecomEc() {
        return telecomEc;
    }

    /**
     * 
     * @param telecomEc
     *     The telecomEc
     */
    @JsonProperty("telecomEc")
    public void setTelecomEc(String telecomEc) {
        this.telecomEc = telecomEc;
    }

    /**
     * 
     * @return
     *     The telecomPg
     */
    @JsonProperty("telecomPg")
    public String getTelecomPg() {
        return telecomPg;
    }

    /**
     * 
     * @param telecomPg
     *     The telecomPg
     */
    @JsonProperty("telecomPg")
    public void setTelecomPg(String telecomPg) {
        this.telecomPg = telecomPg;
    }

    /**
     * 
     * @return
     *     The deathDate
     */
    @JsonProperty("deathDate")
    public String getDeathDate() {
        return deathDate;
    }

    /**
     * 
     * @param deathDate
     *     The deathDate
     */
    @JsonProperty("deathDate")
    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * 
     * @return
     *     The reliability
     */
    @JsonProperty("reliability")
    public String getReliability() {
        return reliability;
    }

    /**
     * 
     * @param reliability
     *     The reliability
     */
    @JsonProperty("reliability")
    public void setReliability(String reliability) {
        this.reliability = reliability;
    }
    
    /**
     * 
     * @return The mmgDateBegin
     */
    @JsonProperty("mmgDateEnd")
    public String getMmgDateEnd() {
        return mmgDateEnd;
    }

    /**
     * @param mmgDateEnd
     */
    @JsonProperty("mmgDateEnd")
    public void setMmgDateEnd(String mmgDateEnd) {
        this.mmgDateEnd = mmgDateEnd;
    }
    
    /**
     * 
     * @return The mmgDateBegin
     */
    @JsonProperty("mmgDateBegin")
    public String getMmgDateBegin() {
        return mmgDateBegin;
    }

    /**
     * @param mmgDateBegin
     */
    @JsonProperty("mmgDateBegin")
    public void setMmgDateBegin(String mmgDateBegin) {
        this.mmgDateBegin = mmgDateBegin;
    }
    
    /**
     * 
     * @return
     *     The mmgNameGiv
     */
    @JsonProperty("mmgNameGiv")
    public String getMmgNameGiv() {
        return mmgNameGiv;
    }

    /**
     * 
     * @param mmgNameGiv
     *     The mmgNameGiv
     */
    @JsonProperty("mmgNameGiv")
    public void setMmgNameGiv(String mmgNameGiv) {
        this.mmgNameGiv = mmgNameGiv;
    }
    
    /**
     * 
     * @return
     *     The mmgNameFam
     */
    @JsonProperty("mmgNameFam")
    public String getMmgNameFam() {
        return mmgNameFam;
    }

    /**
     * 
     * @param mmgNameFam
     *     The mmgNameFam
     */
    @JsonProperty("mmgNameFam")
    public void setMmgNameFam(String mmgNameFam) {
        this.mmgNameFam = mmgNameFam;
    }
    
    /**
     * 
     * @return
     *     The mmgRegionalCode
     */
    @JsonProperty("mmgRegionalCode")
    public String getMmgRegionalCode() {
        return mmgRegionalCode;
    }

    /**
     * 
     * @param mmgRegionalCode
     *     The mmgRegionalCode
     */
    @JsonProperty("mmgRegionalCode")
    public void setMmgRegionalCode(String mmgRegionalCode) {
        this.mmgRegionalCode = mmgRegionalCode;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
