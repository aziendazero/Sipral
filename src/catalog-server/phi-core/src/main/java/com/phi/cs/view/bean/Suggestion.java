package com.phi.cs.view.bean;

import java.io.Serializable;

public class Suggestion implements Serializable {
	
	private static final long serialVersionUID = 5383375945866098199L;

	private String label;
	private String id;
	private String code;
	private int codeValueIndex;
	private String zip;
	private String province;
	
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public int getCodeValueIndex() {
		return codeValueIndex;
	}

	public void setCodeValueIndex(int codeValueIndex) {
		this.codeValueIndex = codeValueIndex;
	}
	
	public int getValue() {
		return codeValueIndex;
	}

	public void setValue(int codeValueIndex) {
		this.codeValueIndex = codeValueIndex;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Suggestion (int codeValueIndex, String id, String label, String code) {
		this(codeValueIndex, id, label, code, null, null);
	}

	public Suggestion (int codeValueIndex, String id, String label, String code, String zip, String province) {
		setCodeValueIndex(codeValueIndex);
		setId(id);
		setLabel(label);
		setCode(code);
		setZip(zip);
		setProvince(province);
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
