/**
 * 
 */
package com.phi.util.geolocation;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.parameters.ParameterManager;
import com.phi.security.UserBean;
import com.phi.util.geolocation.json.AddressComponent;
import com.phi.util.geolocation.json.LookupResult;
import com.phi.util.geolocation.json.Result;

/**
 * @author russian
 *
 */
@BypassInterceptors
@Name("geolocation")
@Scope(ScopeType.CONVERSATION)
public class Geolocation implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 85174276439239875L;
	private static final String APPLICATION_JSON_MIME_TYPE = "application/json";
	private static final Logger log = Logger.getLogger(Geolocation.class);
	private final static String GET = "GET";
	private final static String TOKEN_STREET_NUMBER = "street_number";
	private final static String TOKEN_ROUTE = "route";
	private final static String TOKEN_LOCALITY = "locality";
	private final static String TOKEN_POLITICAL = "political";
	private final static String TOKEN_COUNTRY = "country";


	private final static String TOKEN_POSTAL_CODE = "postal_code";
	private final static String TOKEN_CITY_NAME = "administrative_area_level_3";
	private final static String TOKEN_SIGLA_PROVINCIA = "administrative_area_level_2";
	private final static String TOKEN_ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";



	public String googleMapsApiKey(){

		ParameterManager pm = ParameterManager.instance();
		String googleApiKey = null;
		
		try {
			if(pm.getParameter("p.general.googleapikey", "value")!=null)
				googleApiKey = pm.getParameter("p.general.googleapikey", "value").toString();
		} catch (PhiException e) {
			log.error(e.getMessage());
		}

		return googleApiKey;

	}
	
	public String googleMapsApiKeyFromUrl(){

		ParameterManager pm = ParameterManager.instance();
		String googleApiKeyUrl = null;
		String apiKey=null;
		
		Vocabularies voc = VocabulariesImpl.instance();
		
		String apiKeyOverride = googleMapsApiKey();
		if(!apiKeyOverride.isEmpty()){
			apiKey = apiKeyOverride;
			log.warn("Google API key manual override: using custom api key from parameter! "+apiKey);
			
			return apiKey;
		}

		
		try {
			googleApiKeyUrl = pm.getParameter("p.general.apikeyurl", "value").toString();
			
			String readOnly = pm.getParameter("p.general.apikeyurl", "readOnly").toString();
			boolean enableSearch = (readOnly!=null && "true".equals(readOnly))?false:true;
			
			if(!googleApiKeyUrl.isEmpty() && enableSearch){
				
				HttpURLConnection conn = null;
				String im_proxy = "";
				URL url = new URL(googleApiKeyUrl);

				try {
					CodeValue proxyUrl = voc.getCodeValue("PHIDIC", "Configuration", "proxyUrl", "C");
					if(proxyUrl!=null && proxyUrl.getDescription()!=null && !proxyUrl.getDescription().isEmpty())
						im_proxy = proxyUrl.getDescription();
				} catch (PersistenceException e) {
					e.printStackTrace();
				} catch (DictionaryException e) {
					e.printStackTrace();
				}
				log.info("retrieving google api key from url: "+url);
				im_proxy="";
				if(!im_proxy.isEmpty()){
						
						Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(im_proxy, 8080));
						conn = (HttpURLConnection) url.openConnection(proxy); 
						conn.setReadTimeout(10000); 
					} else {
						
						conn = (HttpURLConnection) url.openConnection(); 
						conn.setReadTimeout(10000);
					}



					conn.setDoOutput(true);
					conn.setRequestMethod(GET);
					conn.setRequestProperty("Accept", APPLICATION_JSON_MIME_TYPE);

					Integer statusCode = conn.getResponseCode();
					
					if (statusCode == 200) {
						ObjectMapper mapper = new ObjectMapper();
						mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
						JsonNode apiKeyNode = mapper.readValue(conn.getInputStream(), JsonNode.class);
						apiKey = apiKeyNode.get("apiKey").textValue();
						
					}
					
			}
				
			
			
			
		} catch (PhiException e) {
			log.error(e.getMessage());
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		log.info("google api key retrieved: "+apiKey);
		return apiKey;

	}


	public String reverseGeolocationUrl(){

		ParameterManager pm = ParameterManager.instance();
		String reversegeocodingurl = null;
		try {
			if(pm.getParameter("p.general.reversegeocodingurl", "value")!=null)
				reversegeocodingurl = pm.getParameter("p.general.reversegeocodingurl", "value").toString();
		} catch (PhiException e) {
			log.error(e.getMessage());
		}

		log.info("reverse geolocation url:"+reversegeocodingurl);
		return reversegeocodingurl;

	}


	public void unwrapDeviceLocation(String coords, HashMap<String, Object> temporary){
		String latitude  = coords.split("\\|")[0];
		String longitude = coords.split("\\|")[1];
		temporary.put("latitude",latitude);
		temporary.put("longitude",longitude);
	}

	public String ulssCode(){

		String ulssCode = "";

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String countULSS = "SELECT sdl.organization.id FROM ServiceDeliveryLocation sdl " +
				"WHERE sdl.code.code = 'ULSS' AND sdl.internalId IN (:sdLocs)";

		try {
			List<Long> sdLocs = UserBean.instance().getSdLocs();

			if (sdLocs != null && sdLocs.size()>1) {
				org.hibernate.Query qry = ca.createHibernateQuery(countULSS);

				qry.setParameterList("sdLocs", sdLocs);
				List<Object[]> res = qry.list();



				if (res.size()>0 && res.get(0)!=null){
					ulssCode=String.valueOf(res.get(0));

				}
			}

			return ulssCode;

		}catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}



	public AD reverseAddress(String lat,String lon,String str,String bnr,String cty, String cpa, HashMap<String, Object> temporary) throws MalformedURLException{


		temporary.remove("geolocation_error");
		temporary.remove("reversedAddress");
		temporary.remove("reversedAddressLocalita");
		temporary.remove("reversedLat");
		temporary.remove("reversedLng");
		

		AD resolvedAddress = new AD();

		//------
		HttpURLConnection conn = null;
		LookupResult lookupResult = null;
		String im_proxy = "";


		URL url = null;



		Vocabularies voc = VocabulariesImpl.instance();
		try {
			CodeValue proxyUrl = voc.getCodeValue("PHIDIC", "Configuration", "proxyUrl", "C");
			if(proxyUrl!=null && proxyUrl.getDescription()!=null && !proxyUrl.getDescription().isEmpty())
				im_proxy = proxyUrl.getDescription();
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (DictionaryException e) {
			e.printStackTrace();
		}



		boolean reverseFromAddress=false;
		String reversedLat;
		String reversedLng;
		String apiKey = googleMapsApiKeyFromUrl();
		
		
		if(apiKey==null || apiKey.isEmpty()){
			log.error("Google API key error: no key found in PHI dictionary nor on the external provider.");
			temporary.put("geolocation_error", "Errore: impossibile recuperare una chiave per accedere ai servizi Google.");
			return null;
		}
		
		

		
		if(!lat.isEmpty() && !lon.isEmpty()){
			//coordinates prevail on addresses
			url = new URL(reverseGeolocationUrl()+"?latlng="+lat+","+lon+"&key="+apiKey+"&ulss="+ulssCode()+"&language=it&region=IT");
			
		}else if(!str.isEmpty()){
			//use addresses when coordinates are missing
		
			reverseFromAddress=true;
			String tokens="";
			tokens+=str.trim().replaceAll(" ","+");
			
			if(!bnr.isEmpty()){
				tokens+=","+bnr.trim().replaceAll(" ","");
			}
			
			if(!cty.isEmpty()){
				tokens+=","+cty.trim().replaceAll(" ","+")+","+cpa.trim();
			}
			

			tokens=tokens.trim().replaceAll(" ", "+");
			tokens=tokens.replaceAll(",\\+", ",");

			
			url = new URL(reverseGeolocationUrl()+"?address="+tokens+"&key="+apiKey+"&ulss="+ulssCode()+"&language=it&region=IT");
			
        }else{
        	temporary.put("geolocation_error", "Errore: inserire un indirizzo o delle coordinate prima di poterne verificare la correttezza.");
			return null;
		}


		
		try {

			
			
			
			if(!im_proxy.isEmpty()){
				log.warn("reverse geolocation url request [PROXIED]: \n"+url);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(im_proxy, 8080));
				conn = (HttpURLConnection) url.openConnection(proxy); 
				conn.setReadTimeout(10000);
			} else {
				log.warn("reverse geolocation url request: \n"+url);
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setReadTimeout(10000);
				
			}
			



			conn.setDoOutput(true);
			conn.setRequestMethod(GET);
			conn.setRequestProperty("Accept", APPLICATION_JSON_MIME_TYPE);

			Integer statusCode = conn.getResponseCode();

			if (statusCode != 200) {

				switch (statusCode) {
				case 404:
					temporary.put("geolocation_error", "Errore: l'indirizzo del servizio di geocodifica inversa è sbagliato o cambiato.");
					break;

				case 503:
					temporary.put("geolocation_error", "Errore: il servizio di geocodifica inversa non risponde.");
					break;

				case 400:
					temporary.put("geolocation_error", "Errore: formato delle coordinate non valido");
					break;

				default:
					temporary.put("geolocation_error", "Errore non specificato ("+statusCode+").");
					break;
				}

				log.error("Geocoding service answered with statusCode: " + statusCode);
				return null;
			}

			lookupResult = new LookupResult();
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

			lookupResult = mapper.readValue(conn.getInputStream(), LookupResult.class);


		} catch (MalformedURLException e) {
			temporary.put("geolocation_error", e);
			log.error(null, e);
			return null;
		} catch (SocketTimeoutException e) {
			temporary.put("geolocation_error", e);
			log.error(null, e);
			return null;
		} catch (ProtocolException e) {
			temporary.put("geolocation_error", e);
			log.error(null, e);
			return null;
		} catch (IOException e) {

			if(e.toString().toLowerCase().contains("connection refused")){
				temporary.put("geolocation_error", "Errore: la connessione al servizio di geocodifica inversa è stata rifiutata.");
			}else if(e.toString().toLowerCase().contains("unknownhostexception")){
				temporary.put("geolocation_error", "Errore: impossibile connettersi al proxy.");
			}else{
				temporary.put("geolocation_error", e);
			}

			log.error(null, e);
			return null;
		} finally {
			if(conn!=null){
				try {
					conn.disconnect();
				} catch (Exception ex) {
					temporary.put("geolocation_error", ex);
					log.error(null, ex);
					return null;
				}
			}
		}


		if(lookupResult!=null){

			if(lookupResult.getStatus().equals("OK")){

				if(lookupResult.getResults().isEmpty()){
					temporary.put("geolocation_error","Non è stato trovato alcun indirizzo.");
					return null;
				}   

				Result result = lookupResult.getResults().get(0);

				Boolean hasApprox0 = false;
				Boolean hasApprox1 = false;
				
				for(AddressComponent addressComponent : result.getAddressComponents()){

					if(addressComponent.getTypes().contains(TOKEN_ROUTE)){
						resolvedAddress.setStr(addressComponent.getLongName());
						if(reverseFromAddress){
							reversedLat=result.getGeometry().getLocation().getLat().toString();
							reversedLng=result.getGeometry().getLocation().getLng().toString();
							
							
							temporary.put("reversedLat", reversedLat);
							temporary.put("reversedLng", reversedLng);	
						
						}
						hasApprox0=true;
						hasApprox1=true;
					}else if(addressComponent.getTypes().contains(TOKEN_STREET_NUMBER)){
						resolvedAddress.setBnr(addressComponent.getLongName());
					}else if(addressComponent.getTypes().contains(TOKEN_LOCALITY)){
						temporary.put("reversedAddressLocalita", addressComponent.getLongName());
					}else if(addressComponent.getTypes().contains(TOKEN_POSTAL_CODE)){
						resolvedAddress.setZip(addressComponent.getShortName());
					}else if(addressComponent.getTypes().contains(TOKEN_CITY_NAME)){
						resolvedAddress.setCty(addressComponent.getShortName().toUpperCase());

						String codiceComune = "select code from code_value_city where code_system = 4 and display_name = (:nomeCitta)";
						CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
						try {
							Query codiceComuneQuery = ca.createNativeQuery(codiceComune);

							codiceComuneQuery.setParameter("nomeCitta", addressComponent.getShortName().toUpperCase());
							List<String> resultSet = codiceComuneQuery.getResultList();
							if(resultSet!=null && !resultSet.isEmpty()){

								String codiceComuneResult = resultSet.get(0);
								CodeValueCity c = (CodeValueCity)voc.getCodeValue("Comuni", "", codiceComuneResult, "C");
								resolvedAddress.setCode(c);
							}

						} catch (PersistenceException e) {
							log.error(e.getMessage());
						} catch (DictionaryException e) {
							log.error(e.getMessage());
						}


					}else if(addressComponent.getTypes().contains(TOKEN_SIGLA_PROVINCIA)){
						resolvedAddress.setCpa(addressComponent.getShortName());

					}else if(addressComponent.getTypes().contains(TOKEN_COUNTRY)){
						resolvedAddress.setSta(addressComponent.getLongName());
						setCountryTemporary(addressComponent.getLongName(),temporary);
						
					}




				}


				if(!hasApprox0){
					for(AddressComponent addressComponent : result.getAddressComponents()){
						if(addressComponent.getTypes().contains(TOKEN_POLITICAL) && addressComponent.getTypes().contains(TOKEN_ADMINISTRATIVE_AREA_LEVEL_1)){
							resolvedAddress.setStr(addressComponent.getLongName());
							hasApprox1=true;
						}

						if(addressComponent.getTypes().contains(TOKEN_POLITICAL) && addressComponent.getTypes().contains(TOKEN_COUNTRY)){
							resolvedAddress.setSta(addressComponent.getLongName());
							setCountryTemporary(addressComponent.getLongName(),temporary);
						}
					}
				}

				if(!hasApprox1){
					for(AddressComponent addressComponent : result.getAddressComponents()){
						if(addressComponent.getTypes().contains(TOKEN_POLITICAL) && addressComponent.getTypes().contains(TOKEN_COUNTRY)){
							resolvedAddress.setStr(addressComponent.getLongName());
						}
					}
				}

				temporary.put("reversedAddress", resolvedAddress);	
			}else{




				if(lookupResult.getStatus().equals("REQUEST_DENIED")){
					temporary.put("geolocation_error","Errore: il servizio di geocodifica inversa ha rifiutato la richiesta. "+lookupResult.getErrorMessage());	
				}else if(lookupResult.getStatus().equals("INVALID_REQUEST")){
					temporary.put("geolocation_error","Errore: il formato delle coordinate o dell'indirizzo non è corretto.");	
				}else if(lookupResult.getStatus().equals("OVER_QUERY_LIMIT")){
					temporary.put("geolocation_error","Errore: il numero di richieste giornaliere/orarie consentito è stato oltrepassato.");	
				}else if(lookupResult.getStatus().equals("UNKNOWN_ERROR")){
					temporary.put("geolocation_error","Errore: il servizio ha incontrato un errore non specificato.");	
				}else if(lookupResult.getStatus().equals("ZERO_RESULTS")){
					temporary.put("geolocation_error","Non è stato trovato alcun indirizzo.");
				}else{
					temporary.put("geolocation_error","Errore: " + lookupResult.getErrorMessage());
				}

				return null;



			}
		}else{
			temporary.put("geolocation_error","Errore: il servizio ha risposto in modo anomalo.");
		}


		log.info("resolved address: "+resolvedAddress); 
		return resolvedAddress;

	}
	
	
	private void setCountryTemporary(String countryName,HashMap<String, Object> temporary){
		Vocabularies voc = VocabulariesImpl.instance();
		String codiceStato = "select code from code_value_country where display_name = (:nomeStato)";
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		try {
			Query codiceStatoQuery = ca.createNativeQuery(codiceStato);

			codiceStatoQuery.setParameter("nomeStato", countryName.toUpperCase());
			List<String> resultSet = codiceStatoQuery.getResultList();
			if(resultSet!=null && !resultSet.isEmpty()){

				String codiceStatoResult = resultSet.get(0);
				CodeValueCountry c = (CodeValueCountry)voc.getCodeValue("Stati", "", codiceStatoResult, "C");
				temporary.put("reversedCountry", c);
			}

		} catch (PersistenceException e) {
			log.error(e.getMessage());
		} catch (DictionaryException e) {
			log.error(e.getMessage());
		}
		
	}
	
	public boolean sameAddress(AD addr1, AD addr2){
		
		if(addr1==null || addr2==null)return false;
		
		return addr1.toString().equalsIgnoreCase(addr2.toString());
		
	}


}
