package com.phi.security;

import java.security.MessageDigest;

import org.jboss.seam.util.Hex;


public class Security {
	static String hashFunction = "MD5";
	static String charset = "UTF-8";

	public static String crypt(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance(hashFunction);
			md.update(password.getBytes(charset));
			byte[] raw = md.digest();
			return new String(Hex.encodeHex(raw));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getHashFunction() {
		return hashFunction;
	}

	public void setHashFunction(String hashFunction) {
		this.hashFunction = hashFunction;
	}

//	public static Security instance() {
//		return (Security) Component.getInstance(Security.class);
//	}

	public static boolean compare(String pwd, String retrivedPwd) {
		try {
			MessageDigest md = MessageDigest.getInstance(hashFunction);
			md.update(pwd.getBytes(charset));
			byte[] rawPwdB = md.digest();
			
			md.update(retrivedPwd.getBytes(charset));
			byte[] retrivedPwdB = md.digest();
			
		
			return MessageDigest.isEqual(rawPwdB, retrivedPwdB);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}