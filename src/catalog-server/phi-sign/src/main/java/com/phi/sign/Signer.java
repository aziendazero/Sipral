package com.phi.sign;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class Signer {

    private Date crlNextUpdateTime;
    private String crlurl;
    private Date crlUpdateTime;
    private int certLevel;
    private CertificateID certificateID;
    private String certificateSN;
    /*private int certificateStatus;
    private int certificateStatusCode;
    private String certificateStatusInfo;*/
    private byte[] certificateValue;
    /*private String contact;
    private String dateOfBirth;*/
    private byte[] digest;
    private int digestAlg;
    private String digestAlgName;
    /*private Date endDate;
    private String fiscalCode;
    private int invalidSignCount;
    private String issuerDN;
    private int keyType;
    private int keyUsage;
    private String location;
    private byte[] md5Fingerprint;
    private List<String> policies;
    private String qcCompliance;
    private int qcLimitValue;
    private String qcLimitValueCurency;
    private int qcRetentionPeriod;
    private String qcSSCD;
    private String reason;
    private int revision;
    private byte[] shaFingerprint;*/
    private int signatureAlg;
    private String signatureAlgName;
    private int signatureFormat;
    private byte[] signedDigest;
    private int signerCount;
    private List<Signer> signers;
    private Date signingTime;
    private Date startDate;
    private String subjectDN;
    private Date timeStamp;
    
    public Signer (it.pkbox.server.xsd.Signer signer) {
    	
    	if (signer.getCRLNextUpdateTime() != null) {
    		this.crlNextUpdateTime = signer.getCRLNextUpdateTime().toGregorianCalendar().getTime();
    	}
    	this.crlurl = signer.getCRLURL();
    	if (signer.getCRLUpdateTime() != null) {
    		this.crlUpdateTime = signer.getCRLUpdateTime().toGregorianCalendar().getTime();
    	}
    	this.certLevel = signer.getCertLevel();
    	if (signer.getCertificateID() != null && signer.getCertificateID().getValue() != null) {
    		this.certificateID = new CertificateID (signer.getCertificateID().getValue());
    	}
    	this.certificateSN = signer.getCertificateSN();
    	/*this.certificateStatus = signer.getCertificateStatus();
    	this.certificateStatusCode = signer.getCertificateStatusCode();
    	this.certificateStatusInfo = signer.getCertificateStatusInfo();*/
    	this.certificateValue = signer.getCertificateValue();
    	/*this.contact = signer.getContact();
    	this.dateOfBirth = signer.getDateOfBirth();*/
    	this.digest = signer.getDigest();
    	this.digestAlg = signer.getDigestAlg();
    	this.digestAlgName = signer.getDigestAlgName();
    	/*if (signer.getEndDate() != null) {
    		this.endDate = signer.getEndDate().toGregorianCalendar().getTime();
    	}
    	this.fiscalCode = signer.getFiscalCode();
    	this.invalidSignCount = signer.getInvalidSignCount();
    	this.issuerDN = signer.getIssuerDN();
    	this.keyType = signer.getKeyType();
    	this.keyUsage = signer.getKeyUsage();
    	this.location = signer.getLocation();
    	this.md5Fingerprint = signer.getMd5Fingerprint();
    	if (signer.getPolicies() != null) {
    		for (String policie : signer.getPolicies()) {
    			getPolicies().add(policie);
    		}
    	}
    	this.qcCompliance = signer.getQcCompliance();
    	this.qcLimitValue = signer.getQcLimitValue();
    	this.qcLimitValueCurency = signer.getQcLimitValueCurency();
    	this.qcRetentionPeriod = signer.getQcRetentionPeriod();
    	this.qcSSCD = signer.getQcSSCD();
    	this.reason = signer.getReason();
    	this.revision = signer.getRevision();
    	this.shaFingerprint = signer.getShaFingerprint();*/
    	this.signatureAlg = signer.getSignatureAlg();
    	this.signatureAlgName = signer.getSignatureAlgName();
    	this.signatureFormat = signer.getSignatureFormat();
    	this.signedDigest = signer.getSignedDigest();
    	this.signerCount = signer.getSignerCount();
    	if (signer.getSigningTime() != null) {
    		this.signingTime = signer.getSigningTime().toGregorianCalendar().getTime();
    	}
    	if (signer.getStartDate() != null) {
    		this.startDate = signer.getStartDate().toGregorianCalendar().getTime();
    	}   
    	this.subjectDN = signer.getSubjectDN();
    	for (it.pkbox.server.xsd.Signer s : signer.getSigners()) {
    		if (s != null) {
    			getSigners().add(new Signer(s));
    		}
    	}
    }
    
    public Signer (it.pkbox.client.Signer signer) {
    	this.crlNextUpdateTime = signer.getCRLNextUpdateTime();
    	this.crlurl = signer.getCRLURL();
    	this.crlUpdateTime = signer.getCRLUpdateTime();
    	this.certLevel = signer.getCertLevel();
    	if (signer.getCertificateID() != null && signer.getCertificateID() != null) {
    		this.certificateID = new CertificateID(signer.getCertificateID());
    	}
    	this.certificateSN = signer.getCertificateSN();
    	/*this.certificateStatus = signer.getCertificateStatus();
    	this.certificateStatusCode = signer.getCertificateStatusCode();
    	this.certificateStatusInfo = signer.getCertificateStatusInfo();*/
    	this.certificateValue = signer.getCertificateValue();
    	/*this.contact = signer.getContact();
    	this.dateOfBirth = signer.getDateOfBird();*/
    	this.digest = signer.getDigest();
    	this.digestAlg = signer.getDigestAlg();
    	this.digestAlgName = signer.getDigestAlgName();
    	/*this.endDate = signer.getEndDate();
    	this.fiscalCode = signer.getFiscalCode();
    	this.invalidSignCount = signer.getInvalidSignCount();
    	this.issuerDN = signer.getIssuerDN();
    	this.keyType = signer.getKeyType();
    	this.keyUsage = signer.getKeyUsage();
    	this.location = signer.getLocation();
    	if (signer.getMd5Fingerprint() != null) {
    		this.md5Fingerprint = signer.getMd5Fingerprint().getBytes();
    	}
    	if (signer.getPolicies() != null) {
    		for (String policie : signer.getPolicies()) {
    			getPolicies().add(policie);
    		}
    	}
    	this.qcCompliance = signer.getQcCompliance();
    	this.qcLimitValue = signer.getQcLimitValue();
    	this.qcLimitValueCurency = signer.getQcLimitValueCurency();
    	this.qcRetentionPeriod = signer.getQcRetentionPeriod();
    	this.qcSSCD = signer.getQcSSCD();
    	this.reason = signer.getReason();
    	this.revision = signer.getRevision();
    	if (signer.getShaFingerprint() != null) {
    		this.shaFingerprint = signer.getShaFingerprint().getBytes();
    	}*/
    	this.signatureAlg = signer.getSignatureAlg();
    	this.signatureAlgName = signer.getSignatureAlgName();
    	this.signatureFormat = signer.getSignatureFormat();
    	this.signedDigest = signer.getSignedDigest();
    	this.signerCount = signer.getSignerCount();
    	this.signingTime = signer.getSigningTime();
    	this.startDate = signer.getStartDate();  
    	this.subjectDN = signer.getSubjectDN();
    	for (int index = 0; index < signer.getSignerCount(); index ++) {
    		getSigners().add(new Signer(signer.getSigner(index)));	
    	}   	
    }

    public Signer (com.intesi.pknet.Signer signer) {
    	/*this.crlNextUpdateTime = signer.getCRLNextUpdateTime();
    	this.crlurl = signer.getCRLURL();
    	this.crlUpdateTime = signer.getCRLUpdateTime();
    	this.certLevel = signer.getCertLevel();*/
    	if (signer.getCertificateID() != null && signer.getCertificateID() != null) {
    		this.certificateID = new CertificateID(signer.getCertificateID());
    	}
    	this.certificateSN = signer.getCertificateSN();
    	/*this.certificateStatus = signer.getCertificateStatus();
    	this.certificateStatusCode = signer.getCertificateStatus();
    	this.certificateStatusInfo = signer.getCertificateStatusInfo();*/
    	this.certificateValue = BigInteger.valueOf(signer.getCertificateValue()).toByteArray();
    	/*this.contact = signer.getContact();
    	this.dateOfBirth = signer.getDateOfBird();*/
//    	this.digest = signer.getget.getDigest();
//    	this.digestAlg = signer.getDigestAlg();
//    	this.digestAlgName = signer.getDigestAlgName();
    	/*this.endDate = signer.getEndDate();
    	this.fiscalCode = signer.getFiscalCode();
    	this.invalidSignCount = signer.getInvalidSignCount();
    	this.issuerDN = signer.getIssuerDN();
    	this.keyType = signer.getKeyType();
    	this.keyUsage = signer.getKeyUsage();
    	this.location = signer.getLocation();
    	if (signer.getMd5Fingerprint() != null) {
    		this.md5Fingerprint = signer.getMd5Fingerprint();
    	}
    	if (signer.getPolicies() != null) {
    		for (String policie : signer.getPolicies()) {
    			getPolicies().add(policie);
    		}
    	}
    	this.qcCompliance = signer.getQcCompliance();
    	this.qcLimitValue = signer.getQcLimitValue();
    	this.qcLimitValueCurency = signer.getQcLimitValueCurency();
    	this.qcRetentionPeriod = signer.getQcRetentionPeriod();
    	this.qcSSCD = signer.getQcSSCD();
    	this.reason = signer.getReason();
    	this.revision = signer.getRevision();
    	if (signer.getShaFingerprint() != null) {
    		this.shaFingerprint = signer.getShaFingerprint();
    	}*/
//    	this.signatureAlg = signer.get.getSignatureAlg();
//    	this.signatureAlgName = signer.getSignatureAlgName();
    	this.signatureFormat = signer.getSignatureFormat();
//    	this.signedDigest = signer.getSignedDigest();
    	this.signerCount = signer.getSignerCount();
    	this.signingTime = signer.getSigningTime();
    	this.startDate = signer.getStartDate();  
    	this.subjectDN = signer.getSubjectDN();
    	for (int index = 0; index < signer.getSignerCount(); index ++) {
    		getSigners().add(new Signer(signer.getSigner(index)));	
    	}   	
    }
	public Date getCrlNextUpdateTime() {
		return crlNextUpdateTime;
	}
	public void setCrlNextUpdateTime(Date crlNextUpdateTime) {
		this.crlNextUpdateTime = crlNextUpdateTime;
	}
	public String getCrlurl() {
		return crlurl;
	}
	public void setCrlurl(String crlurl) {
		this.crlurl = crlurl;
	}
	public Date getCrlUpdateTime() {
		return crlUpdateTime;
	}
	public void setCrlUpdateTime(Date crlUpdateTime) {
		this.crlUpdateTime = crlUpdateTime;
	}
	public int getCertLevel() {
		return certLevel;
	}
	public void setCertLevel(int certLevel) {
		this.certLevel = certLevel;
	}
	public CertificateID getCertificateID() {
		return certificateID;
	}
	public void setCertificateID(CertificateID certificateID) {
		this.certificateID = certificateID;
	}
	public String getCertificateSN() {
		return certificateSN;
	}
	public void setCertificateSN(String certificateSN) {
		this.certificateSN = certificateSN;
	}
	/*public int getCertificateStatus() {
		return certificateStatus;
	}
	public void setCertificateStatus(int certificateStatus) {
		this.certificateStatus = certificateStatus;
	}
	public int getCertificateStatusCode() {
		return certificateStatusCode;
	}
	public void setCertificateStatusCode(int certificateStatusCode) {
		this.certificateStatusCode = certificateStatusCode;
	}
	public String getCertificateStatusInfo() {
		return certificateStatusInfo;
	}
	public void setCertificateStatusInfo(String certificateStatusInfo) {
		this.certificateStatusInfo = certificateStatusInfo;
	}*/
	public byte[] getCertificateValue() {
		return certificateValue;
	}
	public void setCertificateValue(byte[] certificateValue) {
		this.certificateValue = ArrayUtils.clone(certificateValue);
	}
	/*public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}*/
	public byte[] getDigest() {
		return digest;
	}
	public void setDigest(byte[] digest) {
		this.digest = ArrayUtils.clone(digest);
	}
	public int getDigestAlg() {
		return digestAlg;
	}
	public void setDigestAlg(int digestAlg) {
		this.digestAlg = digestAlg;
	}
	public String getDigestAlgName() {
		return digestAlgName;
	}
	public void setDigestAlgName(String digestAlgName) {
		this.digestAlgName = digestAlgName;
	}
	/*public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getFiscalCode() {
		return fiscalCode;
	}
	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}
	public int getInvalidSignCount() {
		return invalidSignCount;
	}
	public void setInvalidSignCount(int invalidSignCount) {
		this.invalidSignCount = invalidSignCount;
	}
	public String getIssuerDN() {
		return issuerDN;
	}
	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}
	public int getKeyType() {
		return keyType;
	}
	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}
	public int getKeyUsage() {
		return keyUsage;
	}
	public void setKeyUsage(int keyUsage) {
		this.keyUsage = keyUsage;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public byte[] getMd5Fingerprint() {
		return md5Fingerprint;
	}
	public void setMd5Fingerprint(byte[] md5Fingerprint) {
		this.md5Fingerprint = ArrayUtils.clone(md5Fingerprint);
	}
	public List<String> getPolicies() {
		if (policies == null) {
			policies = new ArrayList<String> ();
		}		
		return policies;
	}
	public void setPolicies(List<String> policies) {
		this.policies = policies;
	}
	public String getQcCompliance() {
		return qcCompliance;
	}
	public void setQcCompliance(String qcCompliance) {
		this.qcCompliance = qcCompliance;
	}
	public int getQcLimitValue() {
		return qcLimitValue;
	}
	public void setQcLimitValue(int qcLimitValue) {
		this.qcLimitValue = qcLimitValue;
	}
	public String getQcLimitValueCurency() {
		return qcLimitValueCurency;
	}
	public void setQcLimitValueCurency(String qcLimitValueCurency) {
		this.qcLimitValueCurency = qcLimitValueCurency;
	}
	public int getQcRetentionPeriod() {
		return qcRetentionPeriod;
	}
	public void setQcRetentionPeriod(int qcRetentionPeriod) {
		this.qcRetentionPeriod = qcRetentionPeriod;
	}
	public String getQcSSCD() {
		return qcSSCD;
	}
	public void setQcSSCD(String qcSSCD) {
		this.qcSSCD = qcSSCD;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public byte[] getShaFingerprint() {
		return shaFingerprint;
	}
	public void setShaFingerprint(byte[] shaFingerprint) {
		this.shaFingerprint = ArrayUtils.clone(shaFingerprint);
	}*/
	public int getSignatureAlg() {
		return signatureAlg;
	}
	public void setSignatureAlg(int signatureAlg) {
		this.signatureAlg = signatureAlg;
	}
	public String getSignatureAlgName() {
		return signatureAlgName;
	}
	public void setSignatureAlgName(String signatureAlgName) {
		this.signatureAlgName = signatureAlgName;
	}
	public int getSignatureFormat() {
		return signatureFormat;
	}
	public void setSignatureFormat(int signatureFormat) {
		this.signatureFormat = signatureFormat;
	}
	public byte[] getSignedDigest() {
		return signedDigest;
	}
	public void setSignedDigest(byte[] signedDigest) {
		this.signedDigest = ArrayUtils.clone(signedDigest);
	}
	public int getSignerCount() {
		return signerCount;
	}
	public void setSignerCount(int signerCount) {
		this.signerCount = signerCount;
	}
	public List<Signer> getSigners() {
		if (signers == null) {
			signers = new ArrayList<Signer> ();
		}
		return signers;
	}
	public void setSigners(List<Signer> signers) {
		this.signers = signers;
	}
	public Date getSigningTime() {
		return signingTime;
	}
	public void setSigningTime(Date signingTime) {
		this.signingTime = signingTime;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getSubjectDN() {
		return subjectDN;
	}
	public void setSubjectDN(String subjectDN) {
		this.subjectDN = subjectDN;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
    
    
}
