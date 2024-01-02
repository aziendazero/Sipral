package com.phi.sign;

public enum SignType {
	CAdES,
	PAdES,
	XAdES;

	public static SignType find(String type) {
		for (SignType signType : values()) {
			if (signType.toString().equals(type)) {
				return signType;
			}
		}
		return null;
	}
}
