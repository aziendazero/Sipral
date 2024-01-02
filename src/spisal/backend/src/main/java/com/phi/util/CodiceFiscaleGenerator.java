package com.phi.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.role.Person;

/**
 * NOTA non vengono tenute in considerazione come vocali eventuali lettere accentate.
 * @author 510885
 *
 */

@BypassInterceptors
@Name("CodiceFiscaleGenerator")
@Scope(ScopeType.CONVERSATION)
public class CodiceFiscaleGenerator {

	public static CodiceFiscaleGenerator instance() {
		return (CodiceFiscaleGenerator) Component.getInstance(CodiceFiscaleGenerator.class, ScopeType.CONVERSATION);
	}
	
	
	private static final Logger log = Logger.getLogger(CodiceFiscaleGenerator.class);

	private HashMap<String,Integer> codingPari = new HashMap<String, Integer>();
	private HashMap<String,Integer> codingDispari = new HashMap<String, Integer>();
	private HashMap<Integer,String> resti = new HashMap<Integer, String>();
	
	public String calcoloCodiceFiscale(Person person)  {
		
		
		if(person.getBirthTime()==null
			|| person.getName()==null
			|| person.getName().getFam()==null
			|| person.getName().getGiv()==null
			|| ((person.getBirthPlace()==null || person.getBirthPlace().getCode()==null) && person.getCountryOfBirth()==null)
			|| person.getGenderCode() == null
			|| person.getGenderCode().getCode().equals("UN")
				) return null;
			
		
		String cognome = person.getName().getFam().toUpperCase();
		String nome = person.getName().getGiv().toUpperCase();
		Date nascita = person.getBirthTime();
		String sesso = person.getGenderCode().getCode();
		
		String comuneCod = "";
		
		Vocabularies voc = VocabulariesImpl.instance();
		
		try {
			if(person.getBirthPlace()!=null && person.getBirthPlace().getCode()!=null)
				comuneCod = ((CodeValueCity)voc.getCodeValue("COMUNIISTAT", "", person.getBirthPlace().getCode().getCode(), "C")).getLandRegistry();
		
			if (comuneCod == null || comuneCod.isEmpty() || comuneCod.equalsIgnoreCase("NULL")) {
				if(person.getCountryOfBirth()!=null)
					comuneCod = person.getCountryOfBirth().getLandRegistry();
			}
			if (comuneCod == null || comuneCod.isEmpty() || comuneCod.equalsIgnoreCase("NULL")) {
				return null;
			}
			
			return calcoloCodiceFiscale(nome, cognome, nascita, sesso, comuneCod);
		
		} catch (PersistenceException e) {
			log.error("error using vocabulariesImpl");
			e.printStackTrace();
			return null;
		} catch (DictionaryException e) {
			log.error("error reading COMUNIISTAT");
			e.printStackTrace();
			return null;
		}
	}

	/****
	 * 
	 * @param nome
	 * @param cognome
	 * @param nascita
	 * @param sesso :  "M" o "F"
	 * @param comuneCod  codice istat comune di nascita
	 * 
	 * NOTA: non vengono tenute in considerazione come vocali se accentate.
	 * 
	 * @return
	 */
	public String calcoloCodiceFiscale(String nome, String cognome, Date nascita, String sesso, String comuneCod){
		
		if (nome == null || cognome == null || nascita == null || sesso == null || comuneCod == null ||
			nome.isEmpty() || cognome.isEmpty() || sesso.isEmpty() || comuneCod.isEmpty()) {
			return null;
		}
		
		String cogn = get3Cognome(cognome);
		String nom = get3nome(nome);
		String dataSesso = calcolaDataSesso(nascita, sesso);
		
		String cf = cogn+nom+dataSesso+comuneCod;
		String codiceControllo = calcolaCodiceControllo (cf);
		
		cf = cf+codiceControllo;

		return cf;
	}
	
	
	private String calcolaCodiceControllo (String cf){
		prepareHashMap();
		int tot =0;
		for (int i=0; i<cf.length(); i++) {
			if ( (i+1)%2==0){
				tot+=codingPari.get(""+cf.charAt(i));
			}
			else{
				tot+=codingDispari.get(""+cf.charAt(i));
			}
		}
		
		String carattereControllo = resti.get(tot%26);
		return carattereControllo;
		
	}
	
	
	
	private String calcolaDataSesso(Date d, String sesso) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int mese = c.get(Calendar.MONTH);
		int giorno  = c.get(Calendar.DAY_OF_MONTH);
		if (sesso.equalsIgnoreCase("F")){
			giorno+=40;
		}
		int anno = c.get(Calendar.YEAR)%100;
		char lettaraMese = "ABCDEHLMPRST".charAt(mese);
		
		return String.format("%02d",anno)+lettaraMese+String.format("%02d", giorno);
		
		
	}
	
	private String get3Cognome(String cognome) {
		
		String consonanti = consonant(cognome);
		if (consonanti.length() >=3){
			return consonanti.substring(0, 3);
		}
		
		String ret =consonanti+ vocali(cognome)+"ZZ";
		return ret.substring(0, 3);
		
	}
	
	private String get3nome(String nome) {
		
		String consonanti = consonant(nome);
		if (consonanti.length() >=4){
			return consonanti.charAt(0) +consonanti.substring(2, 4);
		}
		else if (consonanti.length() == 3){
			return consonanti;
		}
		else {
			String ret =consonanti+ vocali(nome)+"ZZ";
			return ret.substring(0, 3);
		}
		
	}
	
	private String vocali(String parola) {
		parola=parola.toUpperCase();
		String ret = "";
		for (int i=0; i<parola.length(); i++){
			String carattere=parola.charAt(i)+"";
			if (isVocale(carattere)) {
				ret+=carattere;
			}
		}
		
		return ret;
	}
	
	private String consonant(String parola) {
		parola=parola.toUpperCase();
		String ret = "";
		for (int i=0; i<parola.length(); i++){
			String carattere=parola.charAt(i)+"";
			if (!isVocale(carattere)) {
				ret+=carattere;
			}
		}
		
		return ret;
	}
	
	private boolean isVocale(String charz) {
		return "AEIOU".contains(charz);
	}
	
	
	private void prepareHashMap(){
		codingDispari.put("0",1);
		codingDispari.put("1",0);
		codingDispari.put("2",5);
		codingDispari.put("3",7);
		codingDispari.put("4",9);
		codingDispari.put("5",13);
		codingDispari.put("6",15);
		codingDispari.put("7",17);
		codingDispari.put("8",19);
		codingDispari.put("9",21);
		codingDispari.put("A",1);
		codingDispari.put("B",0);
		codingDispari.put("C",5);
		codingDispari.put("D",7);
		codingDispari.put("E",9);
		codingDispari.put("F",13);
		codingDispari.put("G",15);
		codingDispari.put("H",17);
		codingDispari.put("I",19);
		codingDispari.put("J",21);
		codingDispari.put("K",2);
		codingDispari.put("L",4);
		codingDispari.put("M",18);
		codingDispari.put("N",20);
		codingDispari.put("O",11);
		codingDispari.put("P",3);
		codingDispari.put("Q",6);
		codingDispari.put("R",8);
		codingDispari.put("S",12);
		codingDispari.put("T",14);
		codingDispari.put("U",16);
		codingDispari.put("V",10);
		codingDispari.put("W",22);
		codingDispari.put("X",25);
		codingDispari.put("Y",24);
		codingDispari.put("Z",23);


		codingPari.put("0",0);
		codingPari.put("1",1);
		codingPari.put("2",2);
		codingPari.put("3",3);
		codingPari.put("4",4);
		codingPari.put("5",5);
		codingPari.put("6",6);
		codingPari.put("7",7);
		codingPari.put("8",8);
		codingPari.put("9",9);
		codingPari.put("A",0);
		codingPari.put("B",1);
		codingPari.put("C",2);
		codingPari.put("D",3);
		codingPari.put("E",4);
		codingPari.put("F",5);
		codingPari.put("G",6);
		codingPari.put("H",7);
		codingPari.put("I",8);
		codingPari.put("J",9);
		codingPari.put("K",10);
		codingPari.put("L",11);
		codingPari.put("M",12);
		codingPari.put("N",13);
		codingPari.put("O",14);
		codingPari.put("P",15);
		codingPari.put("Q",16);
		codingPari.put("R",17);
		codingPari.put("S",18);
		codingPari.put("T",19);
		codingPari.put("U",20);
		codingPari.put("V",21);
		codingPari.put("W",22);
		codingPari.put("X",23);
		codingPari.put("Y",24);
		codingPari.put("Z",25);

		resti.put(0,"A");
		resti.put(1,"B");
		resti.put(2,"C");
		resti.put(3,"D");
		resti.put(4,"E");
		resti.put(5,"F");
		resti.put(6,"G");
		resti.put(7,"H");
		resti.put(8,"I");;
		resti.put(9,"J");
		resti.put(10,"K");
		resti.put(11,"L");
		resti.put(12,"M");
		resti.put(13,"N");
		resti.put(14,"O");
		resti.put(15,"P");
		resti.put(16,"Q");
		resti.put(17,"R");;
		resti.put(18,"S");
		resti.put(19,"T");
		resti.put(20,"U");
		resti.put(21,"V");
		resti.put(22,"W");
		resti.put(23,"X");;
		resti.put(24,"Y");
		resti.put(25,"Z");


	}
	
	

}
