package com.phi.aur;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.aur.json.AurResponse;
import com.phi.aur.json.Error;
import com.phi.aur.json.Person;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("AurHttpClient")
@Scope(ScopeType.SESSION)
public class AurHttpClient implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6509956014967208948L;
	private static String mirthUrl,mirthUrlImport;
	private static HttpHost proxy;
	private final static Logger log = Logger.getLogger(AurHttpClient.class);
	private static RequestConfig requestConfig;
	private static int timeout = 10000;

	@Create
	public void create(){

		mirthUrl = "http://172.31.227.45:8090/patient/";
		mirthUrlImport = "http://172.31.227.45:8090/patient/new/";

		Vocabularies voc = VocabulariesImpl.instance();

		try {
			CodeValuePhi mirthUrlcv = (CodeValuePhi)voc.getCodeValue("PHIDIC", "Configuration", "mirthUrl", "C");
			if(mirthUrlcv!=null){
				mirthUrl = mirthUrlcv.getDescription();

				int score = mirthUrlcv.getScore();
				if (score>0)
					timeout = score;
			}

			if(mirthUrl.endsWith("/")){
				mirthUrlImport = mirthUrl+"new";
			}else{
				mirthUrlImport = mirthUrl+"/new";
			}

			CodeValue proxyUrl = voc.getCodeValue("PHIDIC", "Configuration", "proxyUrl", "C");
			if(proxyUrl!=null && proxyUrl.getDescription()!=null && !proxyUrl.getDescription().isEmpty() ){
				proxy = new HttpHost(proxyUrl.getDescription(), 8080);
			} else
				proxy = null;

		} catch (PersistenceException e) {
			log.error("AurHttpClient initialization error: "+ e.getMessage());
		} catch (DictionaryException e) {
			log.error("AurHttpClient initialization error: "+ e.getMessage());
		}

		if(proxy==null){
			requestConfig = RequestConfig.custom()
					.setSocketTimeout(timeout)
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.build();
		} else {
			requestConfig = RequestConfig.custom()
					.setSocketTimeout(timeout)
					.setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout)
					.setProxy(proxy)
					.build();
		}
	}

	public AurResponse aurSearch(String nameFam, String nameGiv, String codiceFiscale, String mpi, String cs, String stp,
			String eni, String team, Date dob, String birthPlaceCode){

		AurResponse result = new AurResponse();
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			//httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy); //no proxy needed
			HttpPost request = new HttpPost(mirthUrl);
			request.setConfig(requestConfig);
			HashMap<String, Object> body = new HashMap<String, Object>();
			HashMap<String, Object> filters = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();

			if(codiceFiscale!=null && !codiceFiscale.isEmpty()) filters.put("fiscalCode",  codiceFiscale);
			if(nameFam!=null && !nameFam.isEmpty()) filters.put("nameFam",  nameFam+"%");
			if(nameGiv!=null && !nameGiv.isEmpty()) filters.put("nameGiv",  nameGiv+"%");
			if(mpi!=null && !mpi.isEmpty()) filters.put("mpi",  mpi);
			if(cs!=null && !cs.isEmpty()) filters.put("cs",  cs);
			if(stp!=null && !stp.isEmpty()) filters.put("stp",  stp);
			if(eni!=null && !eni.isEmpty()) filters.put("eni",  eni);
			if(team!=null && !team.isEmpty()) filters.put("team",  team);
			if(birthPlaceCode!=null && !birthPlaceCode.isEmpty()) filters.put("birthplaceCode",  birthPlaceCode);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if(dob!=null) filters.put("birthTime",  df.format(dob));

			body.put("filter", filters);
			log.info(filters);

			StringEntity se = new StringEntity( mapper.writeValueAsString(body));  

			request.addHeader("content-type", "application/json");
			request.addHeader("Accept","application/json");
			request.setEntity(se); 

			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();

			if (response != null && statusCode==HttpStatus.SC_OK) {

				InputStream in = response.getEntity().getContent();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int nRead;
				int rows = 0;
				byte[] buffer = new byte[1024];
				while ((nRead = in.read(buffer, 0, buffer.length)) != -1) {
					out.write(buffer, 0, nRead);
					rows++;
				}
				out.close();
				log.info(mirthUrl+" responded with "+(rows*buffer.length)+" length content.");

				mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
				result = mapper.readValue(out.toByteArray(), AurResponse.class);

				List<Error> errors = result.getErrors();


				if(errors.size()>0){

					Error e = errors.get(0);
					log.error(e.getCode());
					log.error(e.getDescription());

				}

				return result;

			} else {
				log.error("Mirth service answered with statusCode: " + statusCode);
				Error e = new Error();
				e.setCode("non disponibile");
				e.setDescription("Problema di connessione con l'anagrafica regionale: [ http status code " + statusCode+" or null response]");
				List<Error> errs = result.getErrors();
				errs.add(e);
				result.setErrors(errs);
			}

		} catch (Exception ex) {
			log.error("Mirth connection error: " + ex.getMessage());

			Error e = new Error();
			e.setCode("Non disponibile");
			e.setDescription("Problema di connessione con l'anagrafica regionale: [" + ex.getMessage()+"]");
			List<Error> errs = result.getErrors();
			errs.add(e);
			result.setErrors(errs);

		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return result;	
	}

	public AurResponse aurUpdate(String nameFam, String nameGiv, String codiceFiscale, Date dob){

		AurResponse result = new AurResponse();
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost request = new HttpPost(mirthUrl);
			request.setConfig(requestConfig);
			HashMap<String, Object> body = new HashMap<String, Object>();
			HashMap<String, Object> filters = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();

			if(codiceFiscale!=null && !codiceFiscale.isEmpty()) 
				filters.put("fiscalCode",  codiceFiscale);

			if(nameFam!=null && !nameFam.isEmpty()) 
				filters.put("nameFam",  nameFam);

			if(nameGiv!=null && !nameGiv.isEmpty()) 
				filters.put("nameGiv",  nameGiv);

			if(dob!=null) { 
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				filters.put("birthTime",  df.format(dob));
			}

			body.put("filter", filters);

			StringEntity se = new StringEntity( mapper.writeValueAsString(body));  

			request.addHeader("content-type", "application/json");
			request.addHeader("Accept","application/json");
			request.setEntity(se); 

			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();

			if (response != null && statusCode==HttpStatus.SC_OK) {
				InputStream in = response.getEntity().getContent();

				mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
				result = mapper.readValue(in, AurResponse.class);

				List<Error> errors = result.getErrors();

				if(errors.size()>0){
					Error e = errors.get(0);
					log.error(e.getCode());
					log.error(e.getDescription());
				}

				return result;

			} else {
				log.error("Mirth service answered with statusCode: " + statusCode);
				Error e = new Error();
				e.setCode("non disponibile");
				e.setDescription("Problema di connessione con l'anagrafica regionale: [ http status code " + statusCode+" or null response]");
				List<Error> errs = result.getErrors();
				errs.add(e);
				result.setErrors(errs);
			}

		} catch (Exception ex) {
			log.error("Mirth connection error: " + ex.getMessage());

			Error e = new Error();
			e.setCode("Non disponibile");
			e.setDescription("Problema di connessione con l'anagrafica regionale: [" + ex.getMessage()+"]");
			List<Error> errs = result.getErrors();
			errs.add(e);
			result.setErrors(errs);

		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return result;	
	}

	public AurResponse aurImport(String mpi) {

		AurResponse result = new AurResponse();
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			//httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

			HttpPost request = new HttpPost(mirthUrl);
			request.setConfig(requestConfig);

			HashMap<String, Object> body = new HashMap<String, Object>();
			HashMap<String, Object> filters = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();

			filters.put("mpi",  mpi);
			body.put("filter", filters);
			StringEntity se = new StringEntity(mapper.writeValueAsString(body));  

			request.addHeader("content-type", "application/json");
			request.addHeader("Accept","application/json");
			request.setEntity(se); 
			//ResponseHandler<String> handler = new BasicResponseHandler();

			HttpResponse response = httpClient.execute(request);

			if (response != null) {

				InputStream in = response.getEntity().getContent();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int nRead;
				int rows = 0;
				byte[] buffer = new byte[1024];
				while ((nRead = in.read(buffer, 0, buffer.length)) != -1) {
					out.write(buffer, 0, nRead);
					rows++;
				}
				out.close();
				log.info(mirthUrl+" responded with "+(rows*buffer.length)+" length content.");

				mapper = new ObjectMapper();
				mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
				AurResponse resp = mapper.readValue(out.toByteArray(), AurResponse.class);

				if(resp.getPersons()!=null && resp.getPersons().size()>0){

					Person personToImport = resp.getPersons().get(0);

					HttpPost importRequest = new HttpPost(mirthUrlImport);
					body = new HashMap<String, Object>();

					body.put("mpi", personToImport.getMpi());
					body.put("fiscalCode", personToImport.getFiscalCode());
					body.put("stp", personToImport.getStp());
					body.put("stpDateBegin", personToImport.getStpDateBegin());
					body.put("stpDateEnd", personToImport.getStpDateEnd());
					body.put("cs", personToImport.getCs());
					body.put("csRegion", personToImport.getCsRegion());
					body.put("csDateBegin", personToImport.getCsDateBegin());
					body.put("csDateEnd", personToImport.getCsDateEnd());
					body.put("eni", personToImport.getEni());
					body.put("eniDateBegin", personToImport.getEniDateBegin());
					body.put("eniDateEnd", personToImport.getEniDateEnd());
					body.put("teamPers", personToImport.getTeamPers());
					body.put("teamInst", personToImport.getTeamInst());
					body.put("teamDateEnd", personToImport.getTeamDateEnd());
					body.put("teamIdent", personToImport.getTeamIdent());
					body.put("teamCode", personToImport.getTeamCode());
					body.put("nameFam", personToImport.getNameFam());
					body.put("nameGiv", personToImport.getNameGiv());
					body.put("birthTime", personToImport.getBirthTime());
					body.put("genderCode", personToImport.getGenderCode());
					body.put("birthplaceCode", personToImport.getBirthplaceCode());
					body.put("countryOfBirth", personToImport.getCountryOfBirth());
					body.put("addrStr", personToImport.getAddrStr());
					body.put("addrStb", personToImport.getAddrStb());
					body.put("addrBnr", personToImport.getAddrBnr());
					body.put("addrCode", personToImport.getAddrCode());
					body.put("countryOfDom", personToImport.getCountryOfDom());
					body.put("domAddrStr", personToImport.getDomAddrStr());
					body.put("domAddrStb", personToImport.getDomAddrStb());
					body.put("domAddrBnr", personToImport.getDomAddrBnr());
					body.put("domAddrCode", personToImport.getDomAddrCode());
					body.put("countryOfAddr", personToImport.getCountryOfAddr());
					body.put("telecomH", personToImport.getTelecomH());
					body.put("telecomHp", personToImport.getTelecomHp());    				
					body.put("telecomBad", personToImport.getTelecomBad());
					body.put("telecomMc", personToImport.getTelecomMc());
					body.put("telecomMail", personToImport.getTelecomMail());
					body.put("telecomTmp", personToImport.getTelecomTmp());
					body.put("telecomEc", personToImport.getTelecomEc());
					body.put("telecomPg", personToImport.getTelecomPg());
					body.put("deathDate", personToImport.getDeathDate());
					body.put("reliability", personToImport.getReliability());

					body.put("mmgRegionalCode", personToImport.getMmgRegionalCode());
					body.put("mmgNameFam", personToImport.getMmgNameFam());
					body.put("mmgNameGiv", personToImport.getMmgNameGiv());
					body.put("mmgDateBegin", personToImport.getMmgDateBegin());
					body.put("mmgDateEnd", personToImport.getMmgDateEnd());

					ObjectMapper mapper2 = new ObjectMapper();

					String sourceRequest = "{";

					for (Entry<String, Object> entry : body.entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();

						if(value!=null && value!="null")
							sourceRequest+="\""+key+"\":\""+value+"\",";
					}

					sourceRequest=sourceRequest.substring(0,sourceRequest.length()-1)+"}"; 
					StringEntity se2 = new StringEntity(sourceRequest);

					importRequest.addHeader("content-type", "application/json");
					importRequest.addHeader("Accept","application/json");
					importRequest.setEntity(se2); 

					HttpResponse response2 = httpClient.execute(importRequest);

					InputStream in2 = response2.getEntity().getContent();
					//    				mapper2 = new ObjectMapper();


					ByteArrayOutputStream out2 = new ByteArrayOutputStream();
					int nRead2;
					int rows2 = 0;
					byte[] buffer2 = new byte[1024];
					while ((nRead2 = in2.read(buffer2, 0, buffer2.length)) != -1) {
						out2.write(buffer2, 0, nRead2);
						rows2++;
					}
					out2.close();
					log.info(mirthUrlImport+" responded with "+(rows2*buffer2.length)+" length content.");

					mapper2.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
					AurResponse resp2 = mapper2.readValue(out2.toByteArray(), AurResponse.class);

					if(resp2.getResult()!=null && resp2.getResult().equals("AE")){
						log.error("Import from aur failed");

						Error e = new Error();
						e.setCode("Importazione da anagrafica regionale");
						e.setDescription("operazione fallita.");
						List<Error> errs = resp2.getErrors();
						errs.add(e);

						if(resp.getAdditionalProperties()!=null){
							for (Map.Entry<String, Object> entry : resp2.getAdditionalProperties().entrySet()) {
								String key = entry.getKey();
								Object value = entry.getValue();
								log.error(key+": "+value);

								e = new Error();
								e.setCode("non disponibile");
								e.setDescription(key+": "+value);
								errs.add(e);
							}
						}

						result.setErrors(errs);
					} else if(resp2.getResult()!=null && resp2.getResult().equals("AA")){
						long internalIdNew=Long.parseLong(resp2.getInternalId());
						log.info(this.getClass().getCanonicalName()+" Richiesta di import dall'anagrafica regionale ricevuta: è stato creato una nuova Person con internalId:"+internalIdNew);
						result=resp2;
					}
				}
			} else {
				log.error("no response from "+mirthUrl);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return result;
	}

	public static AurHttpClient instance() {
		return (AurHttpClient) Component.getInstance(AurHttpClient.class, ScopeType.SESSION);
	}

}
