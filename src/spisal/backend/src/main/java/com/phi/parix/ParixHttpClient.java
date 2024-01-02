package com.phi.parix;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.aur.json.Error;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.parix.json.ParixResponse;
import com.phi.parix.json.detail.DatiImpresa;
import com.phi.parix.json.detail.EstremiImpresa;
import com.phi.parix.json.search.Riga;
import com.phi.parix.json.search.SchedaPersona;

@BypassInterceptors
@Name("ParixHttpClient")
@Scope(ScopeType.SESSION)
public class ParixHttpClient {
	private static final String APPLICATION_JSON_MIME_TYPE = "application/json";
	private final static Logger log = Logger.getLogger(ParixHttpClient.class);
	private static String PARIX_URL = "http://altmirth01.websanita.intra.rve:8090/parix/"; //default value
	private static String IM_PROXY = "";
	//private static String IM_PROXY = "im-proxy.insielmercato.it";

	private final static String DITTE_NON_CESSATE_CF="RicercaImpreseNonCessatePerCodiceFiscale";
	private final static String DITTE_CF="RicercaImpresePerCodiceFiscale";
	private final static String DITTE_CF_QUERYSTRING="?cf=";

	private final static String DITTE_NON_CESSATE_DENOMINAZIONE="RicercaImpreseNonCessatePerDenominazione";
	private final static String DITTE_DENOMINAZIONE="RicercaImpresePerDenominazione";
	private final static String DITTE_DENOMINAZIONE_QUERYSTRING="?denominazioneSede=";

	//private final static String RICERCAIMPRESEPERPERIODOVARIAZIONE="RicercaImpresePerPeriodoVariazione";

	private final static String PERSONE_NON_CESSATE_CF="RicercaPersoneNonCessatePerCodiceFiscale";
	private final static String PERSONE_CF="RicercaPersonePerCodiceFiscale";
	private final static String PERSONE_CF_QUERYSTRING="?cf=";
	private final static String DITTE_IVA_LOOKUP_QS = "ListaDatiCamerali";
	private final static String DITTE_DETTAGLIO_IVA_LOOKUP_QS = "DettaglioDatiCamerali";
	private final static String PIVA_QUERYSTRING="?PIVA=";
	private final static String PIVA_DETAILS_QUERYSTRING="?ID=";
	
	private final static String DETTAGLIO_DITTA_COMPLETO="DettaglioCompletoImpresa";
	//private final static String DETTAGLIO_DITTA_RIDOTTO="RicercaPersonePerCodiceFiscale";
	private final static String SPS_QUERYSTRING="?siglaProvinciaSede=";
	private final static String REA_QUERYSTRING="&numeroReaSede=";

	private final static String GET = "GET";
	private static int timeout = 10000;


	//private final static String POST = "POST";
	
	@Create
	public void init(){
		//get parix url from configuration code
		Vocabularies voc = VocabulariesImpl.instance();
		try {
			CodeValuePhi parixUrl = (CodeValuePhi)voc.getCodeValue("PHIDIC", "Configuration", "parixUrl", "C");
			if(parixUrl!=null){
				PARIX_URL = parixUrl.getDescription();
				
				int score = parixUrl.getScore();
				if (score>0)
					timeout = score;	
			}

			CodeValue proxyUrl = voc.getCodeValue("PHIDIC", "Configuration", "proxyUrl", "C");
			//if(proxyUrl!=null)
			if(proxyUrl!=null && proxyUrl.getDescription()!=null && proxyUrl.getDescription().isEmpty())
				IM_PROXY = proxyUrl.getDescription();
			
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ParixHttpClient instance() {
		return (ParixHttpClient) Component.getInstance(ParixHttpClient.class, ScopeType.SESSION);
	}

	public Object ricercaImpresePerCodiceFiscale(Boolean nonCessate, String codice_fiscale){
		List<EstremiImpresa> listaDitte = new ArrayList<EstremiImpresa>();
		try{
			URL url = null;
			if(codice_fiscale!=null)
				codice_fiscale = codice_fiscale.replace(" ","%20");
			if(Boolean.TRUE.equals(nonCessate)){
				url = new URL(PARIX_URL+DITTE_NON_CESSATE_CF+DITTE_CF_QUERYSTRING+codice_fiscale);
			}else{
				url = new URL(PARIX_URL+DITTE_CF+DITTE_CF_QUERYSTRING+codice_fiscale);
			}
			log.info("Asking Parix: "+url);

			ParixResponse resp = askParix(url);
			if(resp!=null){
				if (resp.getRisposta()!=null && resp.getRisposta().getDati()!=null) { 
					if (resp.getRisposta().getDati().getListaImprese()!=null && resp.getRisposta().getDati().getListaImprese().getEstremiImpresa()!=null){
						listaDitte = resp.getRisposta().getDati().getListaImprese().getEstremiImpresa();
					}else if (resp.getRisposta().getDati().getErrore()!=null){
						return resp.getRisposta().getDati().getErrore().getMsgerr();
					}
				
				} else if (resp.getErrors()!=null && resp.getErrors().size()>0){
	     		    String errorMsg = "Problemi di connessione al servizio Parix: [http status code: " + resp.getErrors().get(0).getCode() + " (" + resp.getErrors().get(0).getDescription() + ")]";
					return errorMsg;
				}
						
			}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		}
		return listaDitte;
	}
	
	public Object ricercaImpresePerPartitaiva(Boolean nonCessate, String iva){
		Riga ditta = new Riga();
		try{
			URL url = null;
			if(iva!=null)
				iva = iva.replace(" ","%20");
			
				url = new URL(PARIX_URL+DITTE_IVA_LOOKUP_QS+PIVA_QUERYSTRING+iva);
			
			log.info("Asking Parix: "+url);

			ParixResponse resp = askParix(url);
			if(resp!=null){
				if (resp.getRisposta()!=null && resp.getRisposta().getDati()!=null) { 
					if (resp.getRisposta().getDati().getRighe()!=null && resp.getRisposta().getDati().getRighe().getRiga()!=null && resp.getRisposta().getDati().getRighe().getRiga().size()>0){
						ditta = resp.getRisposta().getDati().getRighe().getRiga().get(0);
					}else if (resp.getRisposta().getDati().getErrore()!=null){
						return resp.getRisposta().getDati().getErrore().getMsgerr();
				
				} else if (resp.getErrors()!=null && resp.getErrors().size()>0){
	     		    String errorMsg = "Problemi di connessione al servizio Parix: [http status code: " + resp.getErrors().get(0).getCode() + " (" + resp.getErrors().get(0).getDescription() + ")]";
					return errorMsg;
				}
						
			}
		}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		}
		return ditta;
	}

	public Object ricercaImpresePerDenominazione(Boolean nonCessate, String denominazione){
		List<EstremiImpresa> listaDitte = new ArrayList<EstremiImpresa>();
		try{
			URL url = null;
			if(denominazione!=null)
				denominazione = denominazione.replace(" ","%20");
			if(Boolean.TRUE.equals(nonCessate)){
				url = new URL(PARIX_URL+DITTE_NON_CESSATE_DENOMINAZIONE+DITTE_DENOMINAZIONE_QUERYSTRING+denominazione);
			}else{
				url = new URL(PARIX_URL+DITTE_DENOMINAZIONE+DITTE_DENOMINAZIONE_QUERYSTRING+denominazione);
			}
			log.info("Asking Parix: "+url);
			
			ParixResponse resp = askParix(url);
			
			if(resp!=null) {
				if (resp.getRisposta()!=null && resp.getRisposta().getDati()!=null) { 
					if (resp.getRisposta().getDati().getListaImprese()!=null && resp.getRisposta().getDati().getListaImprese().getEstremiImpresa()!=null)
						listaDitte = resp.getRisposta().getDati().getListaImprese().getEstremiImpresa();
				
					else if (resp.getRisposta().getDati().getErrore()!=null)
						return resp.getRisposta().getDati().getErrore().getMsgerr();
				
				} else if (resp.getErrors()!=null && resp.getErrors().size()>0){
	     		    String errorMsg = "ERRORE! Parix non disponibile: " + resp.getErrors().get(0).getDescription();

	     		    return errorMsg;
				}	
			}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		}
		return listaDitte;
	}

	public List<SchedaPersona> ricercaPersonePerCodiceFiscale(Boolean nonCessate, String codice_fiscale){
		List<SchedaPersona> listaPersone = new ArrayList<SchedaPersona>();
		try{
			URL url = null;
			if(codice_fiscale!=null)
				codice_fiscale = codice_fiscale.replace(" ","%20");
			if(Boolean.TRUE.equals(nonCessate)){
				url = new URL(PARIX_URL+PERSONE_NON_CESSATE_CF+PERSONE_CF_QUERYSTRING+codice_fiscale);
			}else{
				url = new URL(PARIX_URL+PERSONE_CF+PERSONE_CF_QUERYSTRING+codice_fiscale);
			}
			log.info("Asking Parix: "+url);
			
			ParixResponse resp = askParix(url);
			
			if(resp!=null && resp.getRisposta()!=null && resp.getRisposta().getDati()!=null 
					&& resp.getRisposta().getDati().getListaPersone()!=null && resp.getRisposta().getDati().getListaPersone().getSchedaPersona()!=null) {
				
				listaPersone = resp.getRisposta().getDati().getListaPersone().getSchedaPersona();
				
			}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		} 
		
		return listaPersone;
	
	}

	public Object dettaglioDittaCompleto(String siglaProvinciaSede, String numeroReaSede){
		DatiImpresa dettaglioCompleto = null;
		try{
			if(siglaProvinciaSede!=null)
				siglaProvinciaSede = siglaProvinciaSede.replace(" ","%20");
			
			if(numeroReaSede!=null)
				numeroReaSede = numeroReaSede.replace(" ","%20");
			
			
			URL url = null;
			url = new URL(PARIX_URL+DETTAGLIO_DITTA_COMPLETO+SPS_QUERYSTRING + siglaProvinciaSede + REA_QUERYSTRING + numeroReaSede);
			log.info("Asking Parix: " + url);
			
			ParixResponse resp = askParix(url);
			
			if(resp!=null) {
				if(resp.getRisposta()!=null && resp.getRisposta().getDati()!=null && resp.getRisposta().getDati().getDatiImpresa()!=null) {
					dettaglioCompleto = resp.getRisposta().getDati().getDatiImpresa();
			
				} else if (resp.getErrors()!=null && resp.getErrors().size()>0) {
					String errorMsg = "Problema di connessione col servizio Parix: [http status code: " + resp.getErrors().get(0).getCode() + " (" + resp.getErrors().get(0).getDescription() + ")]";
					return errorMsg;
				} 
			} else {
				String errorMsg = "Problema di connessione col servizio Parix: [http status code: 504 (Gateway Time-out)]";
				return errorMsg;
			}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		} catch (Exception e) {
			log.error(null, e);
		}
		
		return dettaglioCompleto;
	
	}
	
/*	public Object dettaglioDittaCompletoID(String id){
		Riga dettaglioCompleto = null;
		try{
			if(id!=null)
				id = id.replace(" ","%20");
			
			
			
			URL url = null;
			url = new URL(PARIX_URL+DITTE_DETTAGLIO_IVA_LOOKUP_QS + PIVA_DETAILS_QUERYSTRING + id);
			log.info("Asking Parix: " + url);
			
			ParixResponse resp = askParix(url);
			
			if(resp!=null) {
				if(resp.getRisposta()!=null && resp.getRisposta().getDati()!=null &&resp.getRisposta().getDati().getRighe()!=null && resp.getRisposta().getDati().getRighe().getRiga()!=null) {
					dettaglioCompleto = resp.getRisposta().getDati().getRighe().getRiga(); 
			
				} else if (resp.getErrors()!=null && resp.getErrors().size()>0) {
					String errorMsg = "Problema di connessione col servizio Parix: [http status code: " + resp.getErrors().get(0).getCode() + " (" + resp.getErrors().get(0).getDescription() + ")]";
					return errorMsg;
				} 
			} else {
				String errorMsg = "Problema di connessione col servizio Parix: [http status code: 504 (Gateway Time-out)]";
				return errorMsg;
			}
			
		} catch (MalformedURLException e) {
			log.error(null, e);
		} catch (Exception e) {
			log.error(null, e);
		}
		
		return dettaglioCompleto;
	
	} */
	
	private ParixResponse askParix(URL url){
		HttpURLConnection conn = null;
		ParixResponse resp = null;
		
		try {
			//	use proxy..
			if(!IM_PROXY.isEmpty()){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(IM_PROXY, 8080));
				conn = (HttpURLConnection) url.openConnection(proxy); 
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			} else {
				conn = (HttpURLConnection) url.openConnection(); 
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			}
			
			conn.setDoOutput(true);
			conn.setRequestMethod(GET);
			conn.setRequestProperty("Accept", APPLICATION_JSON_MIME_TYPE);
			
			Integer statusCode = conn.getResponseCode();
			
			if (statusCode != 200) {
				log.error("Parix service answered with statusCode: " + statusCode);
             	
				resp = new ParixResponse();
            	
            	Error e = new Error();
     		    e.setCode(statusCode.toString());
     		    e.setDescription(conn.getResponseMessage());
     		    
         	    List<Error> errors = resp.getErrors();
         	    errors.add(e);
     		    resp.setErrors(errors);
     		    
				return resp;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			
			resp = mapper.readValue(conn.getInputStream(), ParixResponse.class);

		} catch (Exception e) {
			log.error(null, e);
			
        	
        	Error err = new Error();
			err.setCode("Non disponibile");
 		    err.setDescription(e.getMessage());
 		    
     	    List<Error> errors = new ArrayList<Error>();
     	    errors.add(err);

     	    resp = new ParixResponse();
     	    resp.setErrors(errors);
 		    
			return resp;
			
		} finally {
			if(conn!=null) {
				try {
					conn.disconnect();
				} catch (Exception ex) {
					log.error(null, ex);
				}
			}
		}
		
		return resp;
	}
}
