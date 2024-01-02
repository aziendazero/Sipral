package com.phi.sign;

import java.util.ArrayList;
import java.util.List;

public class VerifyInfo {
    private byte[] data;
    private int dataLen;
    private int invalidSignCount;
    private List<Signer> signer;
    private int signerCount;
    
    public VerifyInfo (it.pkserver.remote.soap.xsd.VerifyInfo verifyInfo) {
    	this.data = verifyInfo.getData();
    	this.dataLen = verifyInfo.getDataLen();
    	this.invalidSignCount = verifyInfo.getInvalidSignCount();
    	this.signerCount = verifyInfo.getSignerCount();
    	for (it.pkbox.server.xsd.Signer signer : verifyInfo.getSigner()) {
    		if (signer != null) {
    			getSigner().add(new Signer(signer));
    		}
    	}
    }

    public VerifyInfo (it.pkbox.client.VerifyInfo verifyInfo) {
    	this.data = verifyInfo.getData();
    	if (this.data != null) {
    		this.dataLen = this.data.length;
    	}
    	this.invalidSignCount = verifyInfo.getInvalidSignCount();
    	this.signerCount = verifyInfo.getSignerCount();
    	for (int index = 0; index < verifyInfo.getSignerCount(); index ++) {
    		it.pkbox.client.Signer signer = verifyInfo.getSigner(index);
    		if (signer != null) {
    			getSigner().add(new Signer(signer));
    		}
    	}
    }    

    public VerifyInfo (com.intesi.pknet.VerifyInfo verifyInfo) {
    	this.data = verifyInfo.getData();
    	if (this.data != null) {
    		this.dataLen = this.data.length;
    	}
    	this.invalidSignCount = verifyInfo.getInvalidSignCount();
    	this.signerCount = verifyInfo.getSignerCount();
    	for (int index = 0; index < verifyInfo.getSignerCount(); index ++) {
    		com.intesi.pknet.Signer signer = verifyInfo.getSigner(index);
    		if (signer != null) {
    			getSigner().add(new Signer(signer));
    		}
    	}
    }    
    
    public VerifyInfo (String xmlVerifyInfo) {
    	
    }

	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}
	public int getInvalidSignCount() {
		return invalidSignCount;
	}
	public void setInvalidSignCount(int invalidSignCount) {
		this.invalidSignCount = invalidSignCount;
	}
	public List<Signer> getSigner() {
		if (signer == null) {
			signer = new ArrayList<Signer>();
		}
		return signer;
	}
	public void setSigner(List<Signer> signer) {
		this.signer = signer;
	}
	public int getSignerCount() {
		return signerCount;
	}
	public void setSignerCount(int signerCount) {
		this.signerCount = signerCount;
	}
}
