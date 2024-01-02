package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.CostoDiretto;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


@BypassInterceptors
@Name("CostoDirettoAction")
@Scope(ScopeType.CONVERSATION)
public class CostoDirettoAction extends BaseAction<CostoDiretto, Long> {

	private static final long serialVersionUID = 387573743L;
    private static final Logger log = Logger.getLogger(CostoDirettoAction.class);

	public static CostoDirettoAction instance() {
		return (CostoDirettoAction) Component.getInstance(CostoDirettoAction.class, ScopeType.CONVERSATION);
	}

	public void setUlss() {
		try {
			getEqual().remove("serviceDeliveryLocation.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.internalId", id);
				}
			}
			

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String getTipoStr(CostoDiretto cd) {
		String ret = "";
		try {
			if (cd==null || cd.getTipologia()==null)
				return ret;
			
			String tipologia = cd.getTipologia().getCode();
			if (tipologia==null || "".equals(tipologia))
				return ret;
				
			if ("A".equals(tipologia) && cd.getTipoAtt()!=null){
				ret += cd.getTipoAtt().getCurrentTranslation();
				
				if (cd.getSottotipoAtt()!=null)
					ret += " (" + cd.getSottotipoAtt().getCurrentTranslation() + ")";
				
			} else if ("P".equals(tipologia) && cd.getTipoProvv()!=null){
				ret += cd.getTipoProvv().getCurrentTranslation();
			
			} else if ("M".equals(tipologia) && cd.getArticolo()!=null){
				ret += cd.getArticolo().getCurrentTranslation();
			}
			
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Boolean check(CostoDiretto cd) {
		try {
			if (cd==null)
				return false;
			
			long internalId = cd.getInternalId();
			
			long ulssId = cd.getServiceDeliveryLocation()!=null?cd.getServiceDeliveryLocation().getInternalId():null;
			
			Integer annoDal = cd.getAnno();
			Integer annoAl = cd.getAnnoAl();
			
			if (annoAl!=null && annoAl!=null && annoDal>annoAl){
				String error = "<p><b>Attenzione!</b> L'attributo 'Anno al' non può essere minore dell'attributo 'Anno dal'";
				this.getTemporary().put("check", error);
				return true;
			}
			
			String qualifica = cd.getQualifica()!=null?cd.getQualifica().getCode():null;
			String tipologia = cd.getTipologia()!=null?cd.getTipologia().getCode():null;
			
			String tipoAtt = cd.getTipoAtt()!=null?cd.getTipoAtt().getCode():null;
			String sottotipoAtt = cd.getSottotipoAtt()!=null?cd.getSottotipoAtt().getCode():null;
			
			String tipoProvv = cd.getTipoProvv()!=null?cd.getTipoProvv().getCode():null;
			
			String articolo = cd.getArticolo()!=null?cd.getArticolo().getCode():null;
			
			String qryCD = "SELECT cd FROM CostoDiretto cd " +
			"LEFT JOIN cd.serviceDeliveryLocation ulss " +
			"WHERE ulss.internalId = :ulssId AND cd.qualifica.code = :qualifica AND cd.tipologia.code = :tipologia ";
			
			if ("A".equals(tipologia))
				qryCD += "AND cd.tipoAtt.code = :tipoAtt " + (sottotipoAtt!=null?"AND cd.sottotipoAtt.code = :sottotipoAtt ":"");
			
			else if ("P".equals(tipologia))
				qryCD += "AND cd.tipoProvv.code = :tipoProvv ";
				
			else if ("M".equals(tipologia))
				qryCD += "AND cd.articolo.code = :articolo ";
				
			if (annoAl==null)
				qryCD += "AND (cd.annoAl IS NULL OR cd.annoAl>= :annoDal) ";
			else
				qryCD += "AND ( (cd.annoAl IS NULL AND cd.anno <= :annoAl) OR cd.annoAl>= :annoDal ) ";
			
			if (internalId>0)
				qryCD += "AND cd.internalId <> :internalId";
			
			Query qCD = ca.createQuery(qryCD);
			
			if (qryCD.contains(":annoDal"))
				qCD.setParameter("annoDal", annoDal);
			if (qryCD.contains(":annoAl"))
				qCD.setParameter("annoAl", annoAl);
			if (qryCD.contains(":ulssId"))
				qCD.setParameter("ulssId", ulssId);
			if (qryCD.contains(":qualifica"))
				qCD.setParameter("qualifica", qualifica);
			if (qryCD.contains(":tipologia"))
				qCD.setParameter("tipologia", tipologia);
			if (qryCD.contains(":tipoAtt"))
				qCD.setParameter("tipoAtt", tipoAtt);
			if (qryCD.contains(":sottotipoAtt"))
				qCD.setParameter("sottotipoAtt", sottotipoAtt);
			if (qryCD.contains(":tipoProvv"))
				qCD.setParameter("tipoProvv", tipoProvv);
			if (qryCD.contains(":articolo"))
				qCD.setParameter("articolo", articolo);
			if (qryCD.contains(":internalId"))
				qCD.setParameter("internalId", internalId);
			
			List<CostoDiretto> cdList = qCD.getResultList();
			
			if (cdList!=null && cdList.size()>0){
				String error = "<p><b>Attenzione! Impossibile procedere con il salvataggio perché in collisione con ";
				if(cdList.size()==1)
					error += "il seguente costo:</b>";
				else
					error += "i seguenti costi:</b>";
					
				
				for (CostoDiretto c:cdList)
					error += "<br>" + this.getAsString(c);
				
				error += "</p>";
				
				this.getTemporary().put("check", error);
				return true;
			}
			
			this.getTemporary().remove("check");
			
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getAsString(CostoDiretto cd) {
		String ret = "";
		try {
			if (cd==null || cd.getTipologia()==null)
				return ret;
			
			if (cd.getServiceDeliveryLocation()!=null && cd.getServiceDeliveryLocation().getName()!=null){
				String name = cd.getServiceDeliveryLocation().getName().getGiv();
				if (name==null || "".equals(name))
					name = "N.D.";
				
				ret += name + " - "; 
			}
						
			Integer annoDal = cd.getAnno();
			Integer annoAl = cd.getAnnoAl();
			
			ret += "Valido dall'anno " + annoDal;
			if (annoAl!=null)
				ret += " all'anno " + annoAl;
			
			ret += " - ";
			
			String qualifica = cd.getQualifica()!=null?cd.getQualifica().getDisplayName():"N.D.";
			ret += "Qualifica: " + qualifica + " - ";
			
			String tipologia = cd.getTipologia()!=null?cd.getTipologia().getDisplayName():"N.D.";
			ret += "Tipologia: " + tipologia + " - " + this.getTipoStr(cd) + " - ";
	
			ret += "Peso: " + cd.getPeso() + " (id: " + cd.getInternalId() + ")";
						
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* Regola la visibilità dei box in costi_diretti_multi.mmgp */
	public Boolean showBox(String boxName) {
		try {
			if (boxName==null || "".equals(boxName))
				return false;
			
			//Per mostrare il box Provvedimenti..
			if (boxName.equals("Provvedimenti")){ 
				if (this.temporary.get("tipologie")!=null){
					String [] tipologie = (String [])this.temporary.get("tipologie");
					
					if (tipologie!=null){
						List<String> list = Arrays.asList(tipologie);
			        	
						//..controllo se nel temporary tipologie è settato il check "Provvedimenti"
						if(list.contains(boxName)){
							
							//Preseleziono tutti i check relativi alla tipologia di provvedimento 
							if (this.temporary.get("tipiProvv")==null){
			        			String [] tipiProvv = {"Iter 758", "Disposizioni", "Sanzioni amministrative/ex art. 301 bis", "Sospensione ex art. 14"};
			        			
			        			this.temporary.put("tipiProvv", tipiProvv);
			        		}
			        		
							return true;
						}
					}
				}
			//Per mostrare il box Attività..	
			} else if (boxName.equals("Attivita")){ 
				if (this.temporary.get("tipologie")!=null){
					String [] tipologie = (String [])this.temporary.get("tipologie");
					
					if (tipologie!=null){
						List<String> list = Arrays.asList(tipologie);
						
						//..controllo se nel temporary tipologie è settato il check "Attivita"
						if(list.contains(boxName))
			        		return true;
					}
				}
				
			//Per mostrare il box Miglioramenti..	
			} else if (boxName.equals("Miglioramenti")){ 
				if (this.temporary.get("tipologie")!=null){
					String [] tipologie = (String [])this.temporary.get("tipologie");
					
					if (tipologie!=null){
						List<String> list = Arrays.asList(tipologie);
			        	
						//..controllo se nel temporary tipologie è settato il check "Miglioramenti"
						if(list.contains(boxName))
			        		return true;
					}
				}
				
			//Per mostrare i box Sopralluogo, Sequestro, o Commissioni varie (sotto-tipi attività)..		
			} else if ("Sopralluogo;Sequestro;Commissioni varie;".contains(boxName)){ 
				if (this.temporary.get("tipoAtt")!=null){
					String [] tipoAtt = (String [])this.temporary.get("tipoAtt");
					
					if (tipoAtt!=null){
						List<String> list = Arrays.asList(tipoAtt);
			        	
						//..controllo se nel temporary tipoAtt è settato il relativo check
						if(list.contains(boxName)){
			        		
							//Preseleziono tutti i check relativi alla sotto-tipologia "Sequestro" 
							if ("Sequestro".equals(boxName) && this.temporary.get("sottotipiAttSequestro")==null){
			        			String [] sottotipiAttSequestro = {"Preventivo", "Probatorio"};
			        			
			        			this.temporary.put("sottotipiAttSequestro", sottotipiAttSequestro);
			        		
			        		//Preseleziono tutti i check relativi alla sotto-tipologia "Sopralluogo"
			        		} else if ("Sopralluogo".equals(boxName) && this.temporary.get("sottotipiAttSopralluogo")==null){
			        			String [] sottotipiAttSopralluogo = {"Primo sopralluogo", "Rivisita (sopralluogo di)", "Sopralluogo amianto bonifica in corso", 
			        					"Sopralluogo amianto post bonifica", "Sopralluogo amianto pre bonifica", "Sopralluogo amianto visivo finale", 
			        					"Sopralluogo per rilascio", "Verifica (sopralluogo di)", "Preventivo", "Probatorio"};
			        			
			        			this.temporary.put("sottotipiAttSopralluogo", sottotipiAttSopralluogo);
			        		
			        		//Preseleziono tutti i check relativi alla sotto-tipologia "Commissioni varie" 	
				        	} else if ("Commissioni varie".equals(boxName) && this.temporary.get("sottotipiAttCommissione")==null){
			        			String [] sottotipiAttCommissione = {"Altre commissioni", "Commissioni carburanti", "Commissioni ex art. 5 L. 300/70",
			        					"Commissioni intra-dipartimentali", "Committente", "Conferenza dei servizi", "Lavoratore autonomo"};
			        			
			        			this.temporary.put("sottotipiAttCommissione", sottotipiAttCommissione);
			        		}
			        		
							return true;
							
						}
					}
				}
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* Controllo di coerenza su in creazione multipla - costi_diretti_multi.mmgp */
	public Boolean checkCostiDirettiMulti() {
		try {
			Boolean ok = true;
			String tmp = "<p><b>Attenzione!</b>";
			
			HashMap<String, Object> temp = this.getTemporary();
			
			if (temp.get("serviceDeliveryLocation")==null){
				tmp += "<br> - selezionare una ULSS.";
				ok = false;
			}
			
			if (temp.get("anno")==null){
				tmp += "<br> - inserire l'attributo \"Valido dall'anno\".";
				ok = false;
			}
			
			if (temp.get("peso")==null || "".equals(temp.get("peso").toString())){
				tmp += "<br> - inserire l'attributo \"Peso\".";
				ok = false;
			}
			
			if (temp.get("qualifiche")==null || ((String [])temp.get("qualifiche")).length<1){
				tmp += "<br> - selezionare almeno una qualifica.";
				ok = false;
			}
			
			if (temp.get("tipologie")==null || ((String [])temp.get("tipologie")).length<1){
				tmp += "<br> - selezionare almeno una Tipologia.";
				ok = false;
			} else {
				List<String> tipologie =  Arrays.asList((String [])this.temporary.get("tipologie"));
				
				//Check sulle attività
				if (tipologie.contains("Attivita")){
					if (temp.get("tipoAtt")==null || ((String [])temp.get("tipoAtt")).length<1){
						tmp += "<br> - se hai selezionato la tipologia \"Attività\" è necessario selezionarne almeno un tipo.";
						ok = false;
					} else {
						List<String> tipoAtt =  Arrays.asList((String [])this.temporary.get("tipoAtt"));
						
						if (tipoAtt.contains("Sopralluogo")){
							if (temp.get("sottotipiAttSopralluogo")==null || ((String [])temp.get("sottotipiAttSopralluogo")).length<1){
								tmp += "<br> - se hai selezionato la tipologia \"Sopralluogo\" è necessario selezionarne almeno un tipo.";
								ok = false;
							}
						}
						
						if (tipoAtt.contains("Sequestro")){
							if (temp.get("sottotipiAttSequestro")==null || ((String [])temp.get("sottotipiAttSequestro")).length<1){
								tmp += "<br> - se hai selezionato la tipologia \"Sequestro\" è necessario selezionarne almeno un tipo.";
								ok = false;
							}
						}
						
						if (tipoAtt.contains("Commissioni varie")){
							if (temp.get("sottotipiAttCommissione")==null || ((String [])temp.get("sottotipiAttCommissione")).length<1){
								tmp += "<br> - se hai selezionato la tipologia \"Commissioni varie\" è necessario selezionarne almeno un tipo.";
								ok = false;
							}
							
						}
					}
				}
				
				//Check sui miglioramenti
				if (tipologie.contains("Miglioramenti")){
					CodeValueLaw articolo = (CodeValueLaw)Component.getInstance("CodeValueLaw");
					
					if (articolo==null){
						tmp += "<br> - se hai selezionato la tipologia \"Miglioramenti\" è necessario selezionare un articolo.";
						ok = false;
					}
				}
				
				//Check sui Provvedimenti
				if (tipologie.contains("Provvedimenti")){
					
					if (temp.get("tipiProvv")==null || ((String [])temp.get("tipiProvv")).length<1){
						tmp += "<br> - se hai selezionato la tipologia \"Provvedimenti\" è necessario selezionarne almeno un tipo.";
						ok = false;
					}
				}
			}
			
			if (!ok)
				this.temporary.put("checkMulti", tmp);
			else 
				this.temporary.remove("checkMulti");
			
			return ok;
			//return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* Creazione multipla - costi_diretti_multi.mmgp 
	 * 
	 * Per eliminare i Costi Diretti creati automaticamente: delete from costo_diretto where auto=true; */
	public void createMulti() {
		try {
			
			HashMap<String, Object> temp = this.getTemporary();
			ServiceDeliveryLocation ulss = (ServiceDeliveryLocation)temp.get("serviceDeliveryLocation");
			
			Integer anno = Integer.parseInt(temp.get("anno").toString());
			
			//Attributo "annoAl" non obbligatorio - richiede controllo altrimenti sbomba
			Integer annoAl = null;
			if (temp.get("annoAl")!=null && StringUtils.isNumeric(temp.get("annoAl").toString()))
				annoAl = Integer.parseInt(temp.get("annoAl").toString());
			
			String peso = temp.get("peso").toString();
			
			CodeValueLaw articolo = (CodeValueLaw)Component.getInstance("CodeValueLaw");

			List<String> qualifiche =  Arrays.asList((String [])temp.get("qualifiche"));
			List<String> tipologie =  Arrays.asList((String [])temp.get("tipologie"));
			
			//Creazione multipla - al variare delle qualifiche - del Costo diretto relativo ai Miglioramenti  
			if (tipologie.contains("Miglioramenti")){
				for (String qualifica:qualifiche)
					this.createMiglioramentiMulti(ulss, anno, annoAl, peso, articolo, qualifica);
			}
			
			//Creazione multipla - al variare delle qualifiche - al variare dei tipi di provvedimento - del Costo diretto relativo ai Provvediemnti  
			if (tipologie.contains("Provvedimenti")){
				List<String> tipiProvv =  Arrays.asList((String [])temp.get("tipiProvv"));
				
				for (String qualifica:qualifiche)
					for (String tipoProvv:tipiProvv)
						this.createProvvedimentiMulti(ulss, anno, annoAl, peso, qualifica, tipoProvv);
			}
			
			//Creazione multipla - al variare delle qualifiche - al variare del tipo attività - al variare del sottotipo attività - del Costo diretto relativo alle Attività  
			if (tipologie.contains("Attivita")){
				List<String> tipiAtt =  Arrays.asList((String [])temp.get("tipoAtt"));
				
				for (String qualifica:qualifiche)
					for (String tipoAtt:tipiAtt)
						this.createAttivitaMulti(ulss, anno, annoAl, peso, qualifica, tipoAtt);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createMiglioramentiMulti(ServiceDeliveryLocation ulss, Integer anno, Integer annoAl, String peso, CodeValueLaw articolo, String qualifica) {
		try {
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			
			CostoDiretto cd = new CostoDiretto();
			cd.setAuto(true);
			
			cd.setServiceDeliveryLocation(ulss);
			cd.setAnno(anno);
			cd.setAnnoAl(annoAl);
			cd.setPeso(peso);
			cd.setArticolo(articolo);
			
			cd.setQualifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Nomina", qualifica));
			cd.setTipologia((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Tipologia", "M"));
			
			this.create(cd);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createProvvedimentiMulti(ServiceDeliveryLocation ulss, Integer anno, Integer annoAl, String peso, String qualifica, String tipoProvv) {
		try {
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			
			CostoDiretto cd = new CostoDiretto();
			cd.setAuto(true);
			
			cd.setServiceDeliveryLocation(ulss);
			cd.setAnno(anno);
			cd.setAnnoAl(annoAl);
			cd.setPeso(peso);
			
			cd.setQualifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Nomina", qualifica));
			cd.setTipologia((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Tipologia", "P"));
			
			if ("Iter 758".equals(tipoProvv))
				cd.setTipoProvv((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "PevType", "758"));
			else if ("Disposizioni".equals(tipoProvv))
				cd.setTipoProvv((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "PevType", "Disp"));
			else if ("Sanzioni amministrative/ex art. 301 bis".equals(tipoProvv))
				cd.setTipoProvv((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "PevType", "301bis"));
			else if ("Sospensione ex art. 14".equals(tipoProvv))
				cd.setTipoProvv((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "PevType", "ex14"));
			else return;
			
			this.create(cd);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createAttivitaMulti(ServiceDeliveryLocation ulss, Integer anno, Integer annoAl, String peso, String qualifica, String tipoAtt) {
		try {
			HashMap<String, Object> temp = this.getTemporary();
			Vocabularies vocabularies = VocabulariesImpl.instance();
			
			CostoDiretto cd = new CostoDiretto();
			cd.setAuto(true);
			
			cd.setServiceDeliveryLocation(ulss);
			cd.setAnno(anno);
			cd.setAnnoAl(annoAl);
			cd.setPeso(peso);
			
			cd.setQualifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Nomina", qualifica));
			cd.setTipologia((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Tipologia", "A"));
			
			String code = "";
			
			if (tipoAtt.equals("Accertamento"))
				code = "accertamento";
			else if (tipoAtt.equals("Accesso agli atti"))
				code = "accessoatti";
			else if (tipoAtt.equals("Acquisizione e valutazione documentale"))
				code = "acquisizionedocumentale";
			else if (tipoAtt.equals("Somministrazione questionari"))
				code = "CheckList";
			else if (tipoAtt.equals("Commissioni varie"))
				code = "commissione";
			else if (tipoAtt.equals("Convocazioni come teste"))
				code = "convocazione";
			else if (tipoAtt.equals("Counselling"))
				code = "counselling";
			else if (tipoAtt.equals("Attività generica"))
				code = "generic";
			else if (tipoAtt.equals("Incontri e riunioni"))
				code = "incontri";
			else if (tipoAtt.equals("Rilevazione ambientale - campionamento e misurazione strumentale"))
				code = "rilevazione";
			else if (tipoAtt.equals("Sequestro"))
				code = "sequestro";
			else if (tipoAtt.equals("Assunzione sommarie informazioni testimoniali"))
				code = "sit";
			else if (tipoAtt.equals("Sopralluogo"))
				code = "sopralluogo";
			else if (tipoAtt.equals("Visita medica"))
				code = "visitaMedica";
										 
			if(!"".equals(code)){
				cd.setTipoAtt((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "ActivityTypes", code));
				
				if ("Sopralluogo".equals(tipoAtt)){
					List<String> sottotipiAttSopralluogo =  Arrays.asList((String [])temp.get("sottotipiAttSopralluogo"));
					for (String sottotipoAtt:sottotipiAttSopralluogo)
						this.createAttivitaSopralluogoMulti(cd, sottotipoAtt);
				
				} else 	if ("Sequestro".equals(tipoAtt)){
					List<String> sottotipiAttSequestro =  Arrays.asList((String [])temp.get("sottotipiAttSequestro"));
					for (String sottotipoAtt:sottotipiAttSequestro)
						this.createAttivitaSequestroMulti(cd, sottotipoAtt);
	
				} else 	if ("Commissioni varie".equals(tipoAtt)){
					List<String> sottotipiAttCommissione =  Arrays.asList((String [])temp.get("sottotipiAttCommissione"));
					for (String sottotipoAtt:sottotipiAttCommissione)
						this.createAttivitaCommissioneMulti(cd, sottotipoAtt);
	
				} else
	
					this.create(cd);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createAttivitaSopralluogoMulti(CostoDiretto cd, String sottotipoAtt) {
		try {
			CostoDiretto cdNew = new CostoDiretto();
			cdNew.setAuto(true);
			
			cdNew.setServiceDeliveryLocation(cd.getServiceDeliveryLocation());
			cdNew.setAnno(cd.getAnno());
			cdNew.setAnnoAl(cd.getAnnoAl());
			cdNew.setPeso(cd.getPeso());
			
			cdNew.setQualifica(cd.getQualifica());
			cdNew.setTipologia(cd.getTipologia());
			cdNew.setTipoAtt(cd.getTipoAtt());
			
			String code = "";
			
			if (sottotipoAtt.equals("Primo sopralluogo"))
				code = "primoSopralluogo";
			else if (sottotipoAtt.equals("Rivisita (sopralluogo di)"))
				code = "rivisita";
			else if (sottotipoAtt.equals("Sopralluogo amianto bonifica in corso"))
				code = "sopralluogoAmiantoBonificaInCorso";
			else if (sottotipoAtt.equals("Sopralluogo amianto post bonifica"))
				code = "sopralluogoAmiantoPostBonifica";
			else if (sottotipoAtt.equals("Sopralluogo amianto pre bonifica"))
				code = "sopralluogoAmiantoPreBonifica";
			else if (sottotipoAtt.equals("Sopralluogo amianto visivo finale"))
				code = "sopralluogoAmiantoVisivoFinale";
			else if (sottotipoAtt.equals("Sopralluogo per rilascio"))
				code = "sopralluogoRilascio";
			else if (sottotipoAtt.equals("Verifica (sopralluogo di)"))
				code = "verifica";

			if(!"".equals(code)){
				Vocabularies vocabularies = VocabulariesImpl.instance();
				cdNew.setSottotipoAtt((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "InspectionType", code));
				
				this.create(cdNew);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createAttivitaSequestroMulti(CostoDiretto cd, String sottotipoAtt) {
		try {
			CostoDiretto cdNew = new CostoDiretto();
			cdNew.setAuto(true);
			
			cdNew.setServiceDeliveryLocation(cd.getServiceDeliveryLocation());
			cdNew.setAnno(cd.getAnno());
			cdNew.setAnnoAl(cd.getAnnoAl());
			cdNew.setPeso(cd.getPeso());
			
			cdNew.setQualifica(cd.getQualifica());
			cdNew.setTipologia(cd.getTipologia());
			cdNew.setTipoAtt(cd.getTipoAtt());
			
			String code = "";
			
			if (sottotipoAtt.equals("Preventivo"))
				code = "preventivo";
			else if (sottotipoAtt.equals("Probatorio"))
				code = "probatorio";
			
			if(!"".equals(code)){
				Vocabularies vocabularies = VocabulariesImpl.instance();
				cdNew.setSottotipoAtt((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "SeizureType", code));
				
				this.create(cdNew);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void createAttivitaCommissioneMulti(CostoDiretto cd, String sottotipoAtt) {
		try {
			CostoDiretto cdNew = new CostoDiretto();
			cdNew.setAuto(true);
			
			cdNew.setServiceDeliveryLocation(cd.getServiceDeliveryLocation());
			cdNew.setAnno(cd.getAnno());
			cdNew.setAnnoAl(cd.getAnnoAl());
			cdNew.setPeso(cd.getPeso());
			
			cdNew.setQualifica(cd.getQualifica());
			cdNew.setTipologia(cd.getTipologia());
			cdNew.setTipoAtt(cd.getTipoAtt());
			
			String code = "";
			
			if (sottotipoAtt.equals("Altre commissioni"))
				code = "altro";
			else if (sottotipoAtt.equals("Commissioni carburanti"))
				code = "commissioneCarburanti";
			else if (sottotipoAtt.equals("Commissioni intra-dipartimentali"))
				code = "commissioneIntra";
			else if (sottotipoAtt.equals("Committente"))
				code = "committente";
			else if (sottotipoAtt.equals("Conferenza dei servizi"))
				code = "conferenzaServizi";
			else if (sottotipoAtt.equals("Commissioni ex art. 5 L. 300/70"))
				code = "exart";
			else if (sottotipoAtt.equals("Lavoratore autonomo"))
				code = "lavoratoreautonomo";
			
			if(!"".equals(code)){
				Vocabularies vocabularies = VocabulariesImpl.instance();
				cdNew.setSottotipoAtt((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "CommissionType", code));
			
				this.create(cdNew);
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
}