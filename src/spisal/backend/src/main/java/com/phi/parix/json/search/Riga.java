package com.phi.parix.json.search;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"ADD_TOT",
"CAPS",
"DESVIAUL",
"DENOM",
"PIVA",
"CAU_CESS",
"CODCOM",
"STADIT",
"DTAPERU",
"VALCAP",
"LOCALUL",
"TLOCUL",
"CVIAS",
"PRVSED",
"RAGSOC",
"CAPSOC",
"ANNO_ADD",
"CVIAUL",
"DTISRI",
"ID",
"DESVIAS",
"DT_CESS",
"COMUNES",
"CAPUL",
"PROVUL",
"PROVINCIA",
"NCIVS",
"NCIVUL",
"DTDCESU",
"TIPOUL",
"DTISREA",
"NATGIU1",
"OGGSOC",
"STAATTU",
"PROLOC",
"NRI",
"C_FISC1",
"NREA",
"TELEFNU"
})
public class Riga {

@JsonProperty("ADD_TOT")
private String aDDTOT;
@JsonProperty("CAPS")
private String cAPS;
@JsonProperty("DESVIAUL")
private String dESVIAUL;
@JsonProperty("DENOM")
private String dENOM;
@JsonProperty("PIVA")
private String pIVA;
@JsonProperty("CAU_CESS")
private String cAUCESS;
@JsonProperty("CODCOM")
private String cODCOM;
@JsonProperty("STADIT")
private String sTADIT;
@JsonProperty("DTAPERU")
private String dTAPERU;
@JsonProperty("VALCAP")
private String vALCAP;
@JsonProperty("LOCALUL")
private String lOCALUL;
@JsonProperty("TLOCUL")
private String tLOCUL;
@JsonProperty("CVIAS")
private String cVIAS;
@JsonProperty("PRVSED")
private String pRVSED;
@JsonProperty("RAGSOC")
private String rAGSOC;
@JsonProperty("CAPSOC")
private String cAPSOC;
@JsonProperty("ANNO_ADD")
private String aNNOADD;
@JsonProperty("CVIAUL")
private String cVIAUL;
@JsonProperty("DTISRI")
private String dTISRI;
@JsonProperty("ID")
private String iD;
@JsonProperty("DESVIAS")
private String dESVIAS;
@JsonProperty("DT_CESS")
private String dTCESS;
@JsonProperty("COMUNES")
private String cOMUNES;
@JsonProperty("CAPUL")
private String cAPUL;
@JsonProperty("PROVUL")
private String pROVUL;
@JsonProperty("PROVINCIA")
private String pROVINCIA;
@JsonProperty("NCIVS")
private String nCIVS;
@JsonProperty("NCIVUL")
private String nCIVUL;
@JsonProperty("DTDCESU")
private String dTDCESU;
@JsonProperty("TIPOUL")
private String tIPOUL;
@JsonProperty("DTISREA")
private String dTISREA;
@JsonProperty("NATGIU1")
private String nATGIU1;
@JsonProperty("OGGSOC")
private String oGGSOC;
@JsonProperty("STAATTU")
private String sTAATTU;
@JsonProperty("PROLOC")
private String pROLOC;
@JsonProperty("NRI")
private String nRI;
@JsonProperty("C_FISC1")
private String cFISC1;
@JsonProperty("NREA")
private String nREA;
@JsonProperty("TELEFNU")
private String tELEFNU;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("ADD_TOT")
public String getADDTOT() {
return aDDTOT;
}

@JsonProperty("ADD_TOT")
public void setADDTOT(String aDDTOT) {
this.aDDTOT = aDDTOT;
}

@JsonProperty("CAPS")
public String getCAPS() {
return cAPS;
}

@JsonProperty("CAPS")
public void setCAPS(String cAPS) {
this.cAPS = cAPS;
}

@JsonProperty("DESVIAUL")
public String getDESVIAUL() {
return dESVIAUL;
}

@JsonProperty("DESVIAUL")
public void setDESVIAUL(String dESVIAUL) {
this.dESVIAUL = dESVIAUL;
}

@JsonProperty("DENOM")
public String getDENOM() {
return dENOM;
}

@JsonProperty("DENOM")
public void setDENOM(String dENOM) {
this.dENOM = dENOM;
}

@JsonProperty("PIVA")
public String getPIVA() {
return pIVA;
}

@JsonProperty("PIVA")
public void setPIVA(String pIVA) {
this.pIVA = pIVA;
}

@JsonProperty("CAU_CESS")
public String getCAUCESS() {
return cAUCESS;
}

@JsonProperty("CAU_CESS")
public void setCAUCESS(String cAUCESS) {
this.cAUCESS = cAUCESS;
}

@JsonProperty("CODCOM")
public String getCODCOM() {
return cODCOM;
}

@JsonProperty("CODCOM")
public void setCODCOM(String cODCOM) {
this.cODCOM = cODCOM;
}

@JsonProperty("STADIT")
public String getSTADIT() {
return sTADIT;
}

@JsonProperty("STADIT")
public void setSTADIT(String sTADIT) {
this.sTADIT = sTADIT;
}

@JsonProperty("DTAPERU")
public String getDTAPERU() {
return dTAPERU;
}

@JsonProperty("DTAPERU")
public void setDTAPERU(String dTAPERU) {
this.dTAPERU = dTAPERU;
}

@JsonProperty("VALCAP")
public String getVALCAP() {
return vALCAP;
}

@JsonProperty("VALCAP")
public void setVALCAP(String vALCAP) {
this.vALCAP = vALCAP;
}

@JsonProperty("LOCALUL")
public String getLOCALUL() {
return lOCALUL;
}

@JsonProperty("LOCALUL")
public void setLOCALUL(String lOCALUL) {
this.lOCALUL = lOCALUL;
}

@JsonProperty("TLOCUL")
public String getTLOCUL() {
return tLOCUL;
}

@JsonProperty("TLOCUL")
public void setTLOCUL(String tLOCUL) {
this.tLOCUL = tLOCUL;
}

@JsonProperty("CVIAS")
public String getCVIAS() {
return cVIAS;
}

@JsonProperty("CVIAS")
public void setCVIAS(String cVIAS) {
this.cVIAS = cVIAS;
}

@JsonProperty("PRVSED")
public String getPRVSED() {
return pRVSED;
}

@JsonProperty("PRVSED")
public void setPRVSED(String pRVSED) {
this.pRVSED = pRVSED;
}

@JsonProperty("RAGSOC")
public String getRAGSOC() {
return rAGSOC;
}

@JsonProperty("RAGSOC")
public void setRAGSOC(String rAGSOC) {
this.rAGSOC = rAGSOC;
}

@JsonProperty("CAPSOC")
public String getCAPSOC() {
return cAPSOC;
}

@JsonProperty("CAPSOC")
public void setCAPSOC(String cAPSOC) {
this.cAPSOC = cAPSOC;
}

@JsonProperty("ANNO_ADD")
public String getANNOADD() {
return aNNOADD;
}

@JsonProperty("ANNO_ADD")
public void setANNOADD(String aNNOADD) {
this.aNNOADD = aNNOADD;
}

@JsonProperty("CVIAUL")
public String getCVIAUL() {
return cVIAUL;
}

@JsonProperty("CVIAUL")
public void setCVIAUL(String cVIAUL) {
this.cVIAUL = cVIAUL;
}

@JsonProperty("DTISRI")
public String getDTISRI() {
return dTISRI;
}

@JsonProperty("DTISRI")
public void setDTISRI(String dTISRI) {
this.dTISRI = dTISRI;
}

@JsonProperty("ID")
public String getID() {
return iD;
}

@JsonProperty("ID")
public void setID(String iD) {
this.iD = iD;
}

@JsonProperty("DESVIAS")
public String getDESVIAS() {
return dESVIAS;
}

@JsonProperty("DESVIAS")
public void setDESVIAS(String dESVIAS) {
this.dESVIAS = dESVIAS;
}

@JsonProperty("DT_CESS")
public String getDTCESS() {
return dTCESS;
}

@JsonProperty("DT_CESS")
public void setDTCESS(String dTCESS) {
this.dTCESS = dTCESS;
}

@JsonProperty("COMUNES")
public String getCOMUNES() {
return cOMUNES;
}

@JsonProperty("COMUNES")
public void setCOMUNES(String cOMUNES) {
this.cOMUNES = cOMUNES;
}

@JsonProperty("CAPUL")
public String getCAPUL() {
return cAPUL;
}

@JsonProperty("CAPUL")
public void setCAPUL(String cAPUL) {
this.cAPUL = cAPUL;
}

@JsonProperty("PROVUL")
public String getPROVUL() {
return pROVUL;
}

@JsonProperty("PROVUL")
public void setPROVUL(String pROVUL) {
this.pROVUL = pROVUL;
}

@JsonProperty("PROVINCIA")
public String getPROVINCIA() {
return pROVINCIA;
}

@JsonProperty("PROVINCIA")
public void setPROVINCIA(String pROVINCIA) {
this.pROVINCIA = pROVINCIA;
}

@JsonProperty("NCIVS")
public String getNCIVS() {
return nCIVS;
}

@JsonProperty("NCIVS")
public void setNCIVS(String nCIVS) {
this.nCIVS = nCIVS;
}

@JsonProperty("NCIVUL")
public String getNCIVUL() {
return nCIVUL;
}

@JsonProperty("NCIVUL")
public void setNCIVUL(String nCIVUL) {
this.nCIVUL = nCIVUL;
}

@JsonProperty("DTDCESU")
public String getDTDCESU() {
return dTDCESU;
}

@JsonProperty("DTDCESU")
public void setDTDCESU(String dTDCESU) {
this.dTDCESU = dTDCESU;
}

@JsonProperty("TIPOUL")
public String getTIPOUL() {
return tIPOUL;
}

@JsonProperty("TIPOUL")
public void setTIPOUL(String tIPOUL) {
this.tIPOUL = tIPOUL;
}

@JsonProperty("DTISREA")
public String getDTISREA() {
return dTISREA;
}

@JsonProperty("DTISREA")
public void setDTISREA(String dTISREA) {
this.dTISREA = dTISREA;
}

@JsonProperty("NATGIU1")
public String getNATGIU1() {
return nATGIU1;
}

@JsonProperty("NATGIU1")
public void setNATGIU1(String nATGIU1) {
this.nATGIU1 = nATGIU1;
}

@JsonProperty("OGGSOC")
public String getOGGSOC() {
return oGGSOC;
}

@JsonProperty("OGGSOC")
public void setOGGSOC(String oGGSOC) {
this.oGGSOC = oGGSOC;
}

@JsonProperty("STAATTU")
public String getSTAATTU() {
return sTAATTU;
}

@JsonProperty("STAATTU")
public void setSTAATTU(String sTAATTU) {
this.sTAATTU = sTAATTU;
}

@JsonProperty("PROLOC")
public String getPROLOC() {
return pROLOC;
}

@JsonProperty("PROLOC")
public void setPROLOC(String pROLOC) {
this.pROLOC = pROLOC;
}

@JsonProperty("NRI")
public String getNRI() {
return nRI;
}

@JsonProperty("NRI")
public void setNRI(String nRI) {
this.nRI = nRI;
}

@JsonProperty("C_FISC1")
public String getCFISC1() {
return cFISC1;
}

@JsonProperty("C_FISC1")
public void setCFISC1(String cFISC1) {
this.cFISC1 = cFISC1;
}

@JsonProperty("NREA")
public String getNREA() {
return nREA;
}

@JsonProperty("NREA")
public void setNREA(String nREA) {
this.nREA = nREA;
}

@JsonProperty("TELEFNU")
public String getTELEFNU() {
return tELEFNU;
}

@JsonProperty("TELEFNU")
public void setTELEFNU(String tELEFNU) {
this.tELEFNU = tELEFNU;
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