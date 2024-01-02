package com.phi.sign;

public class CertificateID {
    private boolean found;
    private String issuerDN;
    private String serialNum;
    
    public CertificateID (it.pkbox.server.xsd.CertificateID certificateID) {
    	this.found = certificateID.isFound();
    	this.issuerDN = certificateID.getIssuerDN();
    	this.serialNum = certificateID.getSerialNum();
    }
    
    public CertificateID (it.pkbox.client.CertificateID certificateID) {
    	this.found = true;
    	this.issuerDN = certificateID.GetIssuerDN();
    	this.serialNum = certificateID.GetSerialNum();
    }    

    public CertificateID (com.intesi.pknet.CertificateID certificateID) {
    	this.found = true;
    	this.issuerDN = certificateID.GetIssuerDN();
    	//this.serialNum = certificateID.GetSerialNum(); //FIXME
    }    

	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}
	public String getIssuerDN() {
		return issuerDN;
	}
	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
    
    
}
