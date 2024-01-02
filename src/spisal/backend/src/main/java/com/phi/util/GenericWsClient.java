package com.phi.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericWsClient {
	
	private static RequestConfig requestConfig;

	public CloseableHttpClient connect(String url, HashMap<String, String> params) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException{
		if(url == null)
			return null;
		
		String keyStoreType = null;
		String keyStoreLoc = null;
		String certificatePassword = null;
		
		if(params!=null){
			keyStoreType = params.get("keyStoreType");
			keyStoreLoc = params.get("keyStoreLoc");
			certificatePassword = params.get("certificatePassword");
		}
		
		if(keyStoreType!=null && !keyStoreType.isEmpty() && keyStoreLoc!=null && !keyStoreLoc.isEmpty() &&
				certificatePassword!=null && !certificatePassword.isEmpty()){
			
//			try{
				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
				keyStore.load(new FileInputStream(keyStoreLoc), certificatePassword.toCharArray());
				
				SSLContext sslContext = SSLContexts.custom()
				        .loadKeyMaterial(keyStore, certificatePassword.toCharArray())
				        .build();
				
				System.out.println(keyStoreType);
				System.out.println(keyStoreLoc);
				System.out.println(certificatePassword);
				return HttpClients.custom().setSSLContext(sslContext).build();
//			}catch(Exception e){}finally{}
		}else{
			return HttpClients.createDefault();
		}
		
	}

	public HttpResponse doPost(String postSuffix, String connectUrl, HashMap<String, String> params, Object body) throws ClientProtocolException, IOException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException{
		HttpPost request = new HttpPost(connectUrl+postSuffix);
		
		Integer timeout = 50000;
		
		if(params!=null && params.get("timeout")!=null && !params.get("timeout").isEmpty()){
			timeout = Integer.valueOf(params.get("timeout"));
		}
		
		requestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.build();

		request.setConfig(requestConfig);
		
		CloseableHttpClient httpClient = connect(connectUrl, params);

		ObjectMapper mapper = new ObjectMapper();

		StringEntity se = new StringEntity(mapper.writeValueAsString(body));  

		request.addHeader("content-type", "application/json");
		//request.addHeader("Accept","application/json");
		request.addHeader("Accept","*/*");
		request.setEntity(se); 

		HttpResponse response = httpClient.execute(request);

		return response;
	}
}
