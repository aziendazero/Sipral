package com.phi.cs.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AESencrp")
@Scope(ScopeType.SESSION)
public class AESencrp {
	// valori da usare da Oracle10 in poi
	private final String ALGO = "AES/CBC/PKCS5Padding";
	private final String ivDefaultValue = "00000000000000000000000000000000";
	private final String keyAlgo = "AES"; 
	private final byte[] keyValue = "<T2(AKtN3M`mV5*<".getBytes();
	//

	private Key key;
	private IvParameterSpec iv;
	private Cipher decryptor;
	private Cipher encryptor;

	@Create
	public void init() throws Exception {
		key = new SecretKeySpec(keyValue, keyAlgo);
		iv = new IvParameterSpec(Hex.decodeHex(ivDefaultValue.toCharArray()));
		encryptor = Cipher.getInstance(ALGO);
		encryptor.init(Cipher.ENCRYPT_MODE, key, iv);
		decryptor = Cipher.getInstance(ALGO);
		decryptor.init(Cipher.DECRYPT_MODE, key, iv);
	}
	
    public static AESencrp instance(){
        return (AESencrp) Component.getInstance(AESencrp.class, ScopeType.SESSION);
    }
	
	private byte[] encrypt(String textToEncrypt) throws Exception {
		return encryptor.doFinal(textToEncrypt.getBytes());
	}

	public String encryptHex(String textToEncrypt) throws Exception {
		return new String(Hex.encodeHex(encrypt(textToEncrypt))).toUpperCase();
	}
	
	private String decrypt(byte[] baToDecrypt) throws Exception {
		byte[] decValue = decryptor.doFinal(baToDecrypt);
		return new String(decValue);
	}

	public String decryptHex(String encryptedHex) throws Exception {
		return decrypt(Hex.decodeHex(encryptedHex.toCharArray()));
	}
}
