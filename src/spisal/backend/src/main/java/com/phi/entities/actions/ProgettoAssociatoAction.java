package com.phi.entities.actions;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.CostoDiretto;
import com.phi.entities.baseEntity.CostoIndiretto;
import com.phi.entities.baseEntity.CostoNomina;
import com.phi.entities.baseEntity.Miglioramenti;
import com.phi.entities.baseEntity.MonteOre;
import com.phi.entities.baseEntity.Pianificazione;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.ProgettoAssociato;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Sequestri;
import com.phi.entities.baseEntity.Sopralluoghi;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.cs.paging.LazyList;
import com.phi.cs.view.bean.ButtonBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.util.ProgettoAssociatoCounter;

import javax.persistence.Query;

@BypassInterceptors
@Name("ProgettoAssociatoAction")
@Scope(ScopeType.CONVERSATION)
public class ProgettoAssociatoAction extends BaseAction<ProgettoAssociato, Long> {

	private static final long serialVersionUID = 1101138568L;
    private static final Logger log = Logger.getLogger(ProgettoAssociatoAction.class);
   
    private String logEstrazione = "";
    
	public static ProgettoAssociatoAction instance() {
		return (ProgettoAssociatoAction) Component.getInstance(ProgettoAssociatoAction.class, ScopeType.CONVERSATION);
	}
	
	public void initCostoNomine(ProgettoAssociato pa) {
		try {
			pa.setCostoNomina01(this.initCostoNomina());
			pa.setCostoNomina02(this.initCostoNomina());
			pa.setCostoNomina03(this.initCostoNomina());
			pa.setCostoNomina04(this.initCostoNomina());
			pa.setCostoNomina05(this.initCostoNomina());
			pa.setCostoNomina06(this.initCostoNomina());
			pa.setCostoNomina07(this.initCostoNomina());
			pa.setCostoNomina08(this.initCostoNomina());
			pa.setCostoNomina09(this.initCostoNomina());
			pa.setCostoNomina10(this.initCostoNomina());
			pa.setCostoNomina11(this.initCostoNomina());
			pa.setCostoNomina12(this.initCostoNomina());
			pa.setCostoNomina13(this.initCostoNomina());
			pa.setCostoNomina14(this.initCostoNomina());
			pa.setCostoNomina15(this.initCostoNomina());
			pa.setCostoNomina16(this.initCostoNomina());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String translateStatusCode(ProgettoAssociato pa) {
		try {
			
			if (pa==null || pa.getStatusCode()==null)
				return "";
			
			String sc = pa.getStatusCode().getCode();
			
			if ("new".equals(sc))
				return "Nuovo";
			
			if ("verified".equals(sc))
				return "Verificato";
			
			if ("active".equals(sc))
				return "Attivo";
			
			if ("completed".equals(sc))
				return "Validato";
			
			else return "";
				
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public CostoNomina initCostoNomina() {
		try {
			CostoNomina cn = new CostoNomina();
			
			//cn.setDirettoNum(0);
			cn.setDirettoTot(0.0);
			cn.setDirettoMed(0.0);
			cn.setDirettoMedMod(0.0);
			
			//cn.setIndirettoRdpNum(0);
			cn.setIndirettoRdpTot(0.0);
			cn.setIndirettoRdpMed(0.0);
			cn.setIndirettoRdpMedMod(0.0);
			
			//cn.setIndirettoRfpNum(0);
			cn.setIndirettoRfpTot(0.0);
			cn.setIndirettoRfpMed(0.0);
			cn.setIndirettoRfpMedMod(0.0);

			return cn;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setPriorita(ProgettoAssociato progettoAssociato, List<ProgettoAssociato> paList) {
		
		try {
			if (progettoAssociato==null)
				return;
			
			if (paList==null || paList.size()<1){
				progettoAssociato.setPriorita(1);
				return;
			}
			
			int value = 1;
			for (ProgettoAssociato pa:paList){
				int priorita = pa.getPriorita();
				if (priorita > value)
					value = priorita;
			}
			
			progettoAssociato.setPriorita(value+1);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void initPeriodoDiRiferimento(Integer anno) {
		try {
			anno--;
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			
			getEntity().setPeriodoRiferimentoDa(cal.getTime());

			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.MONTH, 11);
			cal.set(Calendar.DAY_OF_MONTH, 31);

			getEntity().setPeriodoRiferimentoA(cal.getTime());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getLineaDiLavoroStr(ProgettoAssociato pa, boolean html) {
		String ret = "";
		try {
			if (pa==null || pa.getLineaDiLavoro()==null)
				return ret;
			
			CodeValuePhi linea = pa.getLineaDiLavoro();
			//for (CodeValuePhi linea:linee){
			ret += (html?"<b>":"") + linea.getCurrentTranslation() + (html?"</b>":"");
				if ("SUPERVISION".equals(linea.getCode()) && pa.getSubVigilanza()!=null)
					ret += " (" + pa.getSubVigilanza().getCurrentTranslation() + ")";
					 
				else if ("WORKMEDICINE".equals(linea.getCode()) && pa.getSubMdl()!=null)
					ret += " (" + pa.getSubMdl().getCurrentTranslation() + ")";
				
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setAssignments(LazyList list){
	try{
		ProgettoAssociato pa = getEntity();
		
		if (list != null && list.size() > 0 ) {
			if (pa.getPartecipanti() == null)
				pa.setPartecipanti(new ArrayList<Partecipanti>());
			
			List<Partecipanti> partecipanti = pa.getPartecipanti();
			
			boolean contain = false;
			
			for (Map operatore : (List<Map>)list) {
				if (operatore.get("isNew")!=null && (Boolean)operatore.get("isNew")){
					Long internalId = (Long)operatore.get("internalId");
					contain = false;
					for (Partecipanti partecipante : partecipanti)
						if (partecipante.getOperatore().getInternalId()==internalId){
							
							//Era stato rimosso dalla pianificazione e adesso viene riagganciato
							//delete from partecipanti where is_active=false;
							if (!partecipante.getIsActive())
								partecipante.setIsActive(true);

							contain=true;
						}
					
						if (!contain){
							Partecipanti partecipante = new Partecipanti();
							Operatore op = (Operatore)ca.get(Operatore.class, (Long)operatore.get("internalId"));
							
							partecipante.setOperatore(op);
							partecipante.setProgettoAssociato(pa);
							pa.getPartecipanti().add(partecipante);
							
							//ca.create(partecipante);
						}

						operatore.put("IsNew",false);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean contains(Long operatoreInternalId){
		try{
			ProgettoAssociato pa = getEntity();
			if (pa!=null && pa.getPartecipanti()!=null){
				List<Partecipanti> partecipanti = pa.getPartecipanti();
			
				for (Partecipanti partecipante : partecipanti) {
					if (partecipante.getOperatore().getInternalId()==operatoreInternalId && partecipante.getIsActive())
						return true;
				}
			}
			
			return false;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void estrazionePesi(){
		try{
			logEstrazione = "";
			
			ProgettoAssociato pa = getEntity();
			if (pa==null)
				return;
			
			if (pa.getPeriodoRiferimentoDa()!=null && pa.getPeriodoRiferimentoA()!=null){
				
				HashMap<String, Object> costiDiretti = estraiCostiDiretti();
				HashMap<String, Object> costiIndiretti = estraiCostiIndiretti();
				
				if (costiDiretti!=null && costiDiretti.size()>0 && costiIndiretti!=null && costiIndiretti.size()>0){
					List<Procpratiche> pratiche = this.estrazionePratiche(pa);
				
					if (pratiche!=null && pratiche.size()>0){
						Integer n = pratiche.size();
						
						Double pesoComplessivo = this.calcolaPesoComplessivo(pratiche, costiDiretti, costiIndiretti);
						
						if (pesoComplessivo!=null){
							pa.setNumeroPratiche(n);
							String logHeader = "N° Pratiche estratte: " + n + "<br>";
												
							pa.setPesoComplessivo(pesoComplessivo);
							logHeader += "Peso complessivo: " + pesoComplessivo + "<br>";
							
							pa.setPesoMedio(pesoComplessivo/n);
							logHeader += "Peso medio: " + pesoComplessivo/n + "<br>";
							logHeader += "Costi usati - Anno: " + pa.getPianificazione().getAnno() + " - ULSS: " + pa.getPianificazione().getServiceDeliveryLocation().getName().getGiv() + "<br>";
		
							logHeader +="<br>";
							this.addLogHeader(logHeader);
						}
					} else 
						this.addToLog("N° Pratiche estratte: 0<br>");
				} else {
					if (costiDiretti==null || costiDiretti.size()<1)
						this.addToLog("Non sono stati definiti <b>costi diretti</b> per la combinazione ulss/anno<br>");
					
					if (costiIndiretti==null || costiIndiretti.size()<1)
						this.addToLog("Non sono stati definiti <b>costi indiretti</b> per la combinazione ulss/anno<br>");
				}
			} else 
				this.addToLog("Periodo di riferimento non valido<br>");
			
			//Taglia a 19990 caratteri
			if (logEstrazione.length()>20000)
				logEstrazione = logEstrazione.substring(0, 19990) + "<b>...</b>";
			
			pa.setLog(logEstrazione);
				
			Vocabularies vocabularies = VocabulariesImpl.instance();
			pa.setStatusCode((CodeValueStatus)vocabularies.getCodeValueCsDomainCode("STATUS", "GENERIC", "active"));
				
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void resetCosti(){
		try{
			ProgettoAssociato pa = getEntity();
			
			pa.setPesoComplessivo(0.0);
			pa.setPesoMedio(0.0);
			pa.setNumeroPratiche(0);

			this.resetCosto(pa.getCostoNomina01());
			this.resetCosto(pa.getCostoNomina02());
			this.resetCosto(pa.getCostoNomina03());
			this.resetCosto(pa.getCostoNomina04());
			this.resetCosto(pa.getCostoNomina05());
			this.resetCosto(pa.getCostoNomina06());
			this.resetCosto(pa.getCostoNomina07());
			this.resetCosto(pa.getCostoNomina08());
			this.resetCosto(pa.getCostoNomina09());
			this.resetCosto(pa.getCostoNomina10());
			this.resetCosto(pa.getCostoNomina11());
			this.resetCosto(pa.getCostoNomina12());
			this.resetCosto(pa.getCostoNomina13());
			this.resetCosto(pa.getCostoNomina14());
			this.resetCosto(pa.getCostoNomina15());
			this.resetCosto(pa.getCostoNomina16());
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void resetCosto(CostoNomina cn){
		try{
			cn.setDirettoMed(0.0);
			cn.setDirettoMedMod(0.0);
			//cn.setDirettoNum(0);
			cn.setDirettoTot(0.0);
			
			cn.setIndirettoRdpMed(0.0);
			cn.setIndirettoRdpMedMod(0.0);
			//cn.setIndirettoRdpNum(0);
			cn.setIndirettoRdpTot(0.0);
			
			cn.setIndirettoRfpMed(0.0);
			cn.setIndirettoRfpMedMod(0.0);
			//cn.setIndirettoRfpNum(0);
			cn.setIndirettoRfpTot(0.0);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setMedie(){
		try{
			ProgettoAssociato pa = getEntity();
			Integer n = pa.getNumeroPratiche(); 
			
			this.setMedia(pa.getCostoNomina01(), n);
			this.setMedia(pa.getCostoNomina02(), n);
			this.setMedia(pa.getCostoNomina03(), n);
			this.setMedia(pa.getCostoNomina04(), n);
			this.setMedia(pa.getCostoNomina05(), n);
			this.setMedia(pa.getCostoNomina06(), n);
			this.setMedia(pa.getCostoNomina07(), n);
			this.setMedia(pa.getCostoNomina08(), n);
			this.setMedia(pa.getCostoNomina09(), n);
			this.setMedia(pa.getCostoNomina10(), n);
			this.setMedia(pa.getCostoNomina11(), n);
			this.setMedia(pa.getCostoNomina12(), n);
			this.setMedia(pa.getCostoNomina13(), n);
			this.setMedia(pa.getCostoNomina14(), n);
			this.setMedia(pa.getCostoNomina15(), n);
			this.setMedia(pa.getCostoNomina16(), n);
			
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setMedia(CostoNomina cn, Integer numeroPratiche){
		try{
			
			Double direttoTot  = cn.getDirettoTot();
			//Integer direttoNum = cn.getDirettoNum();
			
			//if (direttoTot>0 && direttoNum>0){
			//	Double med = direttoTot/direttoNum;
			if (direttoTot>0 && numeroPratiche>0){
				Double med = direttoTot/numeroPratiche;
				
				cn.setDirettoMed(med);
				cn.setDirettoMedMod(med);
			}
			
			Double indirettoRdpTot  = cn.getIndirettoRdpTot();
			//Integer indirettoRdpNum = cn.getIndirettoRdpNum();
			
			//if (indirettoRdpTot>0 && indirettoRdpNum>0){
				//Double med = indirettoRdpTot/indirettoRdpNum;
			if (indirettoRdpTot>0 && numeroPratiche>0){
				Double med = indirettoRdpTot/numeroPratiche;
				
				cn.setIndirettoRdpMed(med);
				cn.setIndirettoRdpMedMod(med);
			}
			
			Double indirettoRfpTot  = cn.getIndirettoRfpTot();
			//Integer indirettoRfpNum = cn.getIndirettoRfpNum();
			
			//if (indirettoRfpTot>0 && indirettoRfpNum>0){
			//	Double med = indirettoRfpTot/indirettoRfpNum;
			if (indirettoRfpTot>0 && numeroPratiche>0){
				Double med = indirettoRfpTot/numeroPratiche;
				
				cn.setIndirettoRfpMed(med);
				cn.setIndirettoRfpMedMod(med);
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void resetMedia(){
		try{
			ProgettoAssociato pa = getEntity();
			
			String status = pa.getStatusCode().getCode();
			if ("completed".equals(status))
				return;
			
			String value = "";
			ButtonBean button = ButtonBean.instance();
						
			if (button!=null)
				value = button.getValue();
			
			if (value!=null && !"".equals(value)){
				
				CostoNomina cn;
				
				if (value.contains("01"))
					cn = pa.getCostoNomina01();
				else if (value.contains("02"))
					cn = pa.getCostoNomina02();
				else if (value.contains("03"))
					cn = pa.getCostoNomina03();
				else if (value.contains("04"))
					cn = pa.getCostoNomina04();
				else if (value.contains("05"))
					cn = pa.getCostoNomina05();
				else if (value.contains("06"))
					cn = pa.getCostoNomina06();
				else if (value.contains("07"))
					cn = pa.getCostoNomina07();
				else if (value.contains("08"))
					cn = pa.getCostoNomina08();
				else if (value.contains("09"))
					cn = pa.getCostoNomina09();
				else if (value.contains("10"))
					cn = pa.getCostoNomina10();
				else if (value.contains("11"))
					cn = pa.getCostoNomina11();
				else if (value.contains("12"))
					cn = pa.getCostoNomina12();
				else if (value.contains("13"))
					cn = pa.getCostoNomina13();
				else if (value.contains("14"))
					cn = pa.getCostoNomina14();
				else if (value.contains("15"))
					cn = pa.getCostoNomina15();
				else if (value.contains("16"))
					cn = pa.getCostoNomina16();
				else 
					cn = new CostoNomina();
			
				this.resetValue(cn, value);
				
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void resetValue(CostoNomina cn, String value){
		try{
			if (value.startsWith("Rdp"))
				cn.setIndirettoRdpMedMod(cn.getIndirettoRdpMed());
			else if (value.startsWith("Rfp"))
				cn.setIndirettoRfpMedMod(cn.getIndirettoRfpMed());
			else if (value.startsWith("Dir"))
				cn.setDirettoMedMod(cn.getDirettoMed());
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void resetMedieAll(){
		try{
			ProgettoAssociato pa = getEntity();
			
			String status = pa.getStatusCode().getCode();
			if ("completed".equals(status))
				return;
			
			this.resetMedie(pa.getCostoNomina01());
			this.resetMedie(pa.getCostoNomina02());
			this.resetMedie(pa.getCostoNomina03());
			this.resetMedie(pa.getCostoNomina04());
			this.resetMedie(pa.getCostoNomina05());
			this.resetMedie(pa.getCostoNomina06());
			this.resetMedie(pa.getCostoNomina07());
			this.resetMedie(pa.getCostoNomina08());
			this.resetMedie(pa.getCostoNomina09());
			this.resetMedie(pa.getCostoNomina10());
			this.resetMedie(pa.getCostoNomina11());
			this.resetMedie(pa.getCostoNomina12());
			this.resetMedie(pa.getCostoNomina13());
			this.resetMedie(pa.getCostoNomina14());
			this.resetMedie(pa.getCostoNomina15());
			this.resetMedie(pa.getCostoNomina16());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void resetMedie(CostoNomina cn){
		try{
			
			cn.setDirettoMedMod(cn.getDirettoMed());
			
			cn.setIndirettoRdpMedMod(cn.getIndirettoRdpMed());
			cn.setIndirettoRfpMedMod(cn.getIndirettoRfpMed());
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setMassivo(ProgettoAssociato pa){
		try{
			if (pa==null)
				return;

			Double valIndirettoRdpMedMod = null;
			String indirettoRdpMedMod = this.getTemporary().get("indirettoRdpMedMod").toString();
			this.getTemporary().remove("indirettoRdpMedMod");
			
			if (indirettoRdpMedMod!=null && !"".equals(indirettoRdpMedMod) && this.isNumeric(indirettoRdpMedMod))
				valIndirettoRdpMedMod = Double.parseDouble(indirettoRdpMedMod);
			
			Double valIndirettoRfpMedMod = null;
			String indirettoRfpMedMod = this.getTemporary().get("indirettoRfpMedMod").toString();
			this.getTemporary().remove("indirettoRfpMedMod");
			
			if (indirettoRfpMedMod!=null && !"".equals(indirettoRfpMedMod) && this.isNumeric(indirettoRfpMedMod))
				valIndirettoRfpMedMod = Double.parseDouble(indirettoRfpMedMod);
			
			Double valDirettoMedMod = null;
			String direttoMedMod = this.getTemporary().get("direttoMedMod").toString();
			this.getTemporary().remove("direttoMedMod");
			
			if (direttoMedMod!=null && !"".equals(direttoMedMod) && this.isNumeric(direttoMedMod))
				valDirettoMedMod = Double.parseDouble(direttoMedMod);
				
			if (valIndirettoRdpMedMod!=null){
				pa.getCostoNomina01().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina02().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina03().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina04().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina05().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina06().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina07().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina08().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina09().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina10().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina11().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina12().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina13().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina14().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina15().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
				pa.getCostoNomina16().setIndirettoRdpMedMod(valIndirettoRdpMedMod);
			} 

			if (valIndirettoRfpMedMod!=null){
				pa.getCostoNomina01().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina02().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina03().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina04().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina05().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina06().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina07().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina08().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina09().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina10().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina11().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina12().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina13().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina14().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina15().setIndirettoRfpMedMod(valIndirettoRfpMedMod);
				pa.getCostoNomina16().setIndirettoRfpMedMod(valIndirettoRfpMedMod);	
			} 

			if (valDirettoMedMod!=null){
				pa.getCostoNomina01().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina02().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina03().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina04().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina05().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina06().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina07().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina08().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina09().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina10().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina11().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina12().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina13().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina14().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina15().setDirettoMedMod(valDirettoMedMod);
				pa.getCostoNomina16().setDirettoMedMod(valDirettoMedMod);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);  
		    return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}

	/*public void checkPratiche(ProgettoAssociato pa){
		try {
			if (pa==null)
				return;
			
			Date periodoRiferimentoDa = pa.getPeriodoRiferimentoDa();
			Date periodoRiferimentoA  = pa.getPeriodoRiferimentoA();
			
			if (periodoRiferimentoDa!= null && periodoRiferimentoA==null){
				ServiceDeliveryLocation ulss = pa.getProgetto().getServiceDeliveryLocation();
				if (ulss== null)
					return;
							
				CodeValuePhi ldl = pa.getLineaDiLavoro();
				CodeValuePhi subMdl = pa.getSubMdl();
				CodeValuePhi subVigilanza = pa.getSubVigilanza();
				if (ldl==null || ("SUPERVISION".equals(ldl.getCode()) && subVigilanza==null) || ("WORKMEDICINE".equals(ldl.getCode()) && subMdl==null))
					return;
	
				String qryPratiche = "SELECT COUNT(p) FROM Procpratiche p " +
						"LEFT JOIN p.serviceDeliveryLocation sdl " +
						"LEFT JOIN p.uoc uoc " +
						"LEFT JOIN uoc.parent ulss ";
				
				if ("SUPERVISION".equals(ldl.getCode()))
					qryPratiche += "LEFT JOIN p.vigilanza v ";
				else if ("WORKMEDICINE".equals(ldl.getCode()))
					qryPratiche += "LEFT JOIN p.medicinaLavoro mdl ";
				
				qryPratiche += "WHERE ulss.internalId=:ulssId AND " +
						"sdl.area.code = :ldl AND " +
						"p.data >= :periodoRiferimentoDa AND p.data <= :periodoRiferimentoA";
				
				if ("SUPERVISION".equals(ldl.getCode()))
					qryPratiche += " AND v.type.code = :subVigilanza";
				else if ("WORKMEDICINE".equals(ldl.getCode()))
					qryPratiche += " AND mdl.type.code = :subMdl";
				
				Query qPratiche = ca.createQuery(qryPratiche);
				qPratiche.setParameter("ulssId", ulss.getInternalId());
				qPratiche.setParameter("ldl", ldl.getCode());
				qPratiche.setParameter("periodoRiferimentoDa", periodoRiferimentoDa);
				qPratiche.setParameter("periodoRiferimentoA", periodoRiferimentoA);
				
				if (qryPratiche.contains(":subVigilanza"))
					qPratiche.setParameter("subVigilanza", subVigilanza.getCode());
				else if (qryPratiche.contains(":subMdl"))
					qPratiche.setParameter("subMdl", subMdl.getCode());
				
				Long numeroPratiche = (Long)qPratiche.getSingleResult();
				
				if(numeroPratiche==null || numeroPratiche<1)
					pa.setNumeroPratiche(0);
				else 
					pa.setNumeroPratiche(numeroPratiche.intValue());
			}
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			pa.setStatusCode((CodeValueStatus)vocabularies.getCodeValueCsDomainCode("STATUS", "GENERIC", "verified"));
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}*/
	
	public List<Procpratiche> estrazionePratiche(ProgettoAssociato pa){
		try {
			if (pa==null)
				return null;
			
			Date periodoRiferimentoDa = pa.getPeriodoRiferimentoDa();
			Date periodoRiferimentoA  = pa.getPeriodoRiferimentoA();
			if (periodoRiferimentoDa== null || periodoRiferimentoA==null)
				return null;
			
			ServiceDeliveryLocation ulss = pa.getProgetto().getServiceDeliveryLocation();
			if (ulss== null)
				return null;
						
			CodeValuePhi ldl = pa.getLineaDiLavoro();
			CodeValuePhi subMdl = pa.getSubMdl();
			CodeValuePhi subVigilanza = pa.getSubVigilanza();
			if (ldl==null || ("SUPERVISION".equals(ldl.getCode()) && subVigilanza==null) || ("WORKMEDICINE".equals(ldl.getCode()) && subMdl==null))
				return null;

			String qryPratiche = "SELECT DISTINCT p FROM Procpratiche p " +
					"LEFT JOIN p.serviceDeliveryLocation sdl " +
					"LEFT JOIN p.uoc uoc " +
					"LEFT JOIN uoc.parent ulss ";
			
			if ("SUPERVISION".equals(ldl.getCode()))
				qryPratiche += "LEFT JOIN p.vigilanza v ";
			else if ("WORKMEDICINE".equals(ldl.getCode()))
				qryPratiche += "LEFT JOIN p.medicinaLavoro mdl ";
			
			qryPratiche += "WHERE ulss.internalId=:ulssId AND " +
					"sdl.area.code = :ldl AND " +
					"p.data >= :periodoRiferimentoDa AND p.data <= :periodoRiferimentoA";
			
			if ("SUPERVISION".equals(ldl.getCode()))
				qryPratiche += " AND v.type.code = :subVigilanza";
			else if ("WORKMEDICINE".equals(ldl.getCode()))
				qryPratiche += " AND mdl.type.code = :subMdl";
			
			Query qPratiche = ca.createQuery(qryPratiche);
			qPratiche.setParameter("ulssId", ulss.getInternalId());
			qPratiche.setParameter("ldl", ldl.getCode());
			qPratiche.setParameter("periodoRiferimentoDa", periodoRiferimentoDa);
			qPratiche.setParameter("periodoRiferimentoA", periodoRiferimentoA);
			
			if (qryPratiche.contains(":subVigilanza"))
				qPratiche.setParameter("subVigilanza", subVigilanza.getCode());
			else if (qryPratiche.contains(":subMdl"))
				qPratiche.setParameter("subMdl", subMdl.getCode());
			
			List<Procpratiche> lst = qPratiche.getResultList();
			
			return lst;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Double calcolaPesoComplessivo(List<Procpratiche> pratiche, HashMap<String, Object> costiDiretti, HashMap<String, Object> costiIndiretti){
		try{
			Double pesoComplessivo = 0.0;

			String counter = "";
			ProgettoAssociatoCounter pac = (ProgettoAssociatoCounter)Component.getInstance("ProgettoAssociatoCounter");
			
			Integer i=1;
			Integer n = pratiche.size();
					
			for (Procpratiche p:pratiche){
				pesoComplessivo += this.calcolaPesoPratica(p, costiDiretti, costiIndiretti);
				
				Double percento = i.doubleValue()*100/n.doubleValue();
				counter = "Calcolo pesi complessivi - Analisi pratica " + i + " di " + n + " (" + this.format(percento) + "%)";
				
				pac.setCounter(counter);
				log.info(counter);
				
				i++;
			}
			
			pac.setCounter("");
			return pesoComplessivo;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Double calcolaPesoPratica(Procpratiche pratica, HashMap<String, Object> costiDiretti, HashMap<String, Object> costiIndiretti){
		try{
			this.addToLog("".equals(logEstrazione)?"":"<br>");
			
			this.addToLog("<b>Pratica numero " + pratica.getNumero() + "</b> (id: " + pratica.getInternalId() + ")<br>");
			Double pesoPratica = 0.0;
			
			/** Calcolo costi indiretti **/
			Double costoIndiretto = this.calcolaCostoIndiretto(pratica, costiIndiretti);
			this.addToLog(" Costo indiretto: " + costoIndiretto + "<br>");
			
			pesoPratica += costoIndiretto;
			
			/** Calcolo costi diretti attività e miglioramenti **/
			List<Attivita> attList = pratica.getAttivita();
			
			/* Itera le attività associate alla pratica e ne estrae i pesi. 
			 * Il peso dell'attività comprenderà gli eventuai miglioramenti associati */
			for (Attivita att:attList){
				if (att.getIsActive()){
					Double pesoAttivita =  this.calcolaPesoAttivita(att, costiDiretti); 
					this.addToLog("Attivita: " + att.getInternalId() + " - Peso:" + pesoAttivita + "<br>");
				
					pesoPratica += pesoAttivita;
				}
			}
			
			/** Calcolo costi diretti attività e miglioramenti **/
			List<Provvedimenti> provvList = pratica.getProvvedimenti();

			/* Itera i provvedimenti associati alla pratica e ne estrae i pesi */
			for (Provvedimenti provv:provvList){
				if (provv.getIsActive()){
					Double pesoProvv = this.calcolaPesoProvvedimento(pratica, provv, costiDiretti); 
					this.addToLog("Provvedimento: " + provv.getInternalId() + " - Peso:" + pesoProvv + "<br>");
					
					pesoPratica += pesoProvv;
				}
			}
			
			return pesoPratica;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	//Calcola il peso indiretto relativo agli operatori RDP ed RFP 	 
	public Double calcolaCostoIndiretto(Procpratiche pratica, HashMap<String, Object> costiIndiretti) {
		try{
			
			Double costoIndiretto = 0.0;
			
			Employee rdp = pratica.getRdp();
			Employee rfp = pratica.getRfp();
			
			if (rdp!=null || rfp!=null){
				String nominaRdp = "";
				String nominaRfp = "";
				
				String ldl = "";
				String subLdl = "";
				
				String keyRdp = "";
				String keyRfp = "";
				
				if (pratica.getServiceDeliveryLocation()!=null && pratica.getServiceDeliveryLocation().getArea()!=null)
					ldl = pratica.getServiceDeliveryLocation().getArea().getCode();
								
				if ("SUPERVISION".equals(ldl) && pratica.getVigilanza()!=null && pratica.getVigilanza().getType()!=null)
					subLdl = pratica.getVigilanza().getType().getCode();
					 
				else if ("WORKMEDICINE".equals(ldl) && pratica.getMedicinaLavoro()!=null && pratica.getMedicinaLavoro().getType()!=null)
					subLdl = pratica.getMedicinaLavoro().getType().getCode();

				//Itera gli operatori della pratica per risalire alla nomina degli Employee settati come RDP e/o RFP
				for (Operatore op:pratica.getOperatori()){
					if (op.getEmployee()==rdp && op.getCode()!=null)
						nominaRdp = op.getCode().getCode();
					if (op.getEmployee()==rfp && op.getCode()!=null)
						nominaRfp = op.getCode().getCode();
				}
				
				if (!"".equals(nominaRdp)){
					keyRdp = "RDP/" + ldl + "/" + subLdl + "/" + nominaRdp;
							
					if (costiIndiretti.containsKey(keyRdp.toUpperCase())){
						Double costoRdp = Double.parseDouble(costiIndiretti.get(keyRdp.toUpperCase()).toString());
						this.addToLog("Peso chiave " + keyRdp.toUpperCase() + ": " + costoRdp + "<br>");
						
						if (costoRdp!=null){
							costoIndiretto += costoRdp;
							
							this.updateCostoNomina(false, true, nominaRdp, costoRdp);//Boolean diretto, Boolean rdp, Double costo
						}
					} else 
						this.addToLog("Peso chiave " + keyRdp.toUpperCase() + " non trovato<br>"); 
				}
				
				if (!"".equals(nominaRfp)){
					keyRfp = "RFP/" + ldl + "/" + subLdl + "/" + nominaRfp;
					if (costiIndiretti.containsKey(keyRfp.toUpperCase())){
						Double costoRfp = Double.parseDouble(costiIndiretti.get(keyRfp.toUpperCase()).toString());
						this.addToLog("Peso chiave " + keyRfp.toUpperCase() + ": " + costoRfp + "<br>");
					
						if (costoRfp!=null){
							costoIndiretto += costoRfp;
						
							this.updateCostoNomina(false, false, nominaRdp, costoRfp);//Boolean diretto, Boolean rdp, Double costo
						}
					} else 
						this.addToLog("Peso chiave " + keyRfp.toUpperCase() + " non trovato<br>");
				}
			}
			
			return costoIndiretto;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Double calcolaPesoAttivita(Attivita attivita, HashMap<String, Object> costiDiretti){
		try{
			Double pesoAttivita = 0.0;
			
			 /* A/SOPRALLUOGO/PRIMOSOPRALLUOGO///DIRIGENTE
			  	A/SOPRALLUOGO/SOPRALLUOGOAMIANTOVISIVOFINALE///DIRIGENTE*/
			List<Partecipanti> partecipanti = attivita.getPartecipanti();
			
			String key = "";
			if (partecipanti.size()<1)
				return pesoAttivita;
			
			String tipoAttivita = "";
			String sottotipoAttivita = "";
			
			if (attivita.getCode()!=null)
				tipoAttivita = attivita.getCode().getCode();
			
			if (!"".equals(tipoAttivita)){
				if ("sopralluogo".equals(tipoAttivita)){
					Sopralluoghi s = attivita.getSopralluogo();
					if (s.getTipoSopralluogo()!=null)
						sottotipoAttivita = s.getTipoSopralluogo().getCode();
						
				} else if ("sequestro".equals(tipoAttivita)){
					Sequestri s = attivita.getSequestro();
					if (s.getTipoSequestro()!=null)
						sottotipoAttivita = s.getTipoSequestro().getCode();
				}
								
				key = "A/" + tipoAttivita + "/"+ sottotipoAttivita + "///";
				
				for(Partecipanti p:partecipanti){
					Operatore op = p.getOperatore();
					
					if (op!=null && op.getCode()!=null){
						String nomina = op.getCode().getCode();
						String keyOp = key + nomina;
						
						if (costiDiretti.containsKey(keyOp.toUpperCase())){
							Double peso = Double.parseDouble(costiDiretti.get(keyOp.toUpperCase()).toString());
							this.addToLog("Peso chiave " + keyOp.toUpperCase() + ": " + peso + "<br>");
							
							if (peso!=null){
								pesoAttivita += peso;
							
								this.updateCostoNomina(true, null, nomina, peso);//Boolean diretto, Boolean rdp, Double costo
							}
						} else 
							this.addToLog("Peso chiave " + keyOp.toUpperCase() + " non trovato<br>");
					}
				}
			}
			
			//Itera i miglioramenti associati alla pratica e ne estrae i pesi
			List<Miglioramenti> miglioramentiList = attivita.getMiglioramenti();
			
			for (Miglioramenti migl:miglioramentiList)
				if (migl.getIsActive())
					pesoAttivita += this.calcolaPesoMiglioramento(partecipanti, migl, costiDiretti); 
			
			return pesoAttivita;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double calcolaPesoProvvedimento(Procpratiche pratica, Provvedimenti provvedimento, HashMap<String, Object> costiDiretti){
		try{
			/* P/////758/DIRETTORE DEL SERVIZIO */
			Double pesoProvvediemnto = 0.0;
			List<Operatore> operatori = pratica.getOperatori();
			
			String key = "";
			if (operatori.size()<1)
				return pesoProvvediemnto;
			
			String tipoProvvedimento ="";
			if (provvedimento.getType()!=null)
				tipoProvvedimento = provvedimento.getType().getCode();
			
			if (!"".equals(tipoProvvedimento)){
				key = "P/////" + tipoProvvedimento + "/";
				for(Operatore op:operatori){
					if (op.getCode()!=null){
						String nomina = op.getCode().getCode();
						
						String keyOp = key + nomina;
						
						if (costiDiretti.containsKey(keyOp.toUpperCase())){
							Double peso = Double.parseDouble(costiDiretti.get(keyOp.toUpperCase()).toString());
							this.addToLog("Peso chiave " + keyOp.toUpperCase() + ": " + peso + "<br>");
							
							if (peso!=null){
								pesoProvvediemnto += peso;
							
								this.updateCostoNomina(true, null, nomina, peso);//Boolean diretto, Boolean rdp, Double costo
								
							}
						} else 
							this.addToLog("Peso chiave " + keyOp.toUpperCase() + " non trovato<br>");
					}
				}
			}
			
			return pesoProvvediemnto;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Double calcolaPesoMiglioramento(List<Partecipanti> partecipanti, Miglioramenti miglioramento, HashMap<String, Object> costiDiretti){
		try{
			Double pesoMiglioramento = 0.0;
			
			 /* M////1//ASSISTENTE SANITARIO
			  * M////2//PSICOLOGO DEL LAVORO
			  * M////21//INFORMATICO */
			
			String key = "";
			if (partecipanti.size()<1)
				return pesoMiglioramento;
			
			String articolo = "";
			
			if (miglioramento.getCodeLegge81()!=null)
				articolo = miglioramento.getCodeLegge81().getCode();
			
			if (!"".equals(articolo)){
				for(Partecipanti p:partecipanti){
					Operatore op = p.getOperatore();
					
					if (op!=null && op.getCode()!=null){
						do {
							String art = articolo;
							String nomina = op.getCode().getCode();
							
							key = "M////" + art + "//" + nomina;
							
							if (costiDiretti.containsKey(key.toUpperCase())){
								Double peso = Double.parseDouble(costiDiretti.get(key.toUpperCase()).toString());
								this.addToLog("Peso chiave " + key.toUpperCase() + ": " + peso + "<br>");
								
								if (peso!=null){
									pesoMiglioramento += peso;
									
									this.updateCostoNomina(true, null, nomina, peso);//Boolean diretto, Boolean rdp, Double costo
									
									art = "";
								}
							} else 
								this.addToLog("Peso chiave " + key.toUpperCase() + " non trovato<br>");
							
							if (!"".equals(art))
								art = art.substring(0, art.length()-1);
							
						} while ("".equals(articolo));
					}
				}
			}

			return pesoMiglioramento;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/** Estrae i costi diretti per anno/ulss, associandoli ad una chiave codificata dal seguente formato:
	 * 	Tipologia/Tipo attività/Sottotipo attività/Articolo/Provvediemnto/Qualifica
	 * 
	 * 	Es.: P/////758/DIRETTORE DEL SERVIZIO=0.5
	 * 		 A/SOPRALLUOGO/PRIMOSOPRALLUOGO///DIRIGENTE=0.1
	 * 		 A/SOPRALLUOGO/SOPRALLUOGOAMIANTOVISIVOFINALE///DIRIGENTE=0.3
	 * 		 M////1//ASSISTENTE SANITARIO=1.0
	 * 		 M////2//PSICOLOGO DEL LAVORO=0.3
	 * 		 M////21//INFORMATICO=0.2
	 **/
	public HashMap<String, Object> estraiCostiDiretti(){
		try{			
			ProgettoAssociato pa = getEntity();
			
			Integer anno = pa.getPianificazione().getAnno();
			ServiceDeliveryLocation ulss = pa.getPianificazione().getServiceDeliveryLocation();
			
			HashMap<String, Object> costiDiretti = new HashMap<String, Object>(1);
			
			if (anno==null || ulss==null)
				return null;
			/* I00080334 - punto 3 
			 * Si aggiunge l'attributo 'annoAl' e l'attributo 'anno' assume significato di 'annoDal'
			 * 
			 * Si modifica la query in modo che cerchi i costi (diretti) con annoDal <= anno della pianificazione'
			 * e annoAl = null o annoAl >= anno della pianificazione
			
			String qryPratiche = "SELECT DISTINCT cd FROM CostoDiretto cd " +
					"LEFT JOIN cd.serviceDeliveryLocation ulss " +
					"WHERE cd.anno=:anno AND " +
					"ulss.internalId=:ulssId"; */
			
			String qryPratiche = "SELECT DISTINCT cd FROM CostoDiretto cd " +
					"LEFT JOIN cd.serviceDeliveryLocation ulss " +
					"WHERE cd.anno <= :anno AND (cd.annoAl IS NULL OR cd.annoAl>= :anno) AND " +
					"ulss.internalId=:ulssId";
			
			Query qPratiche = ca.createQuery(qryPratiche);
			qPratiche.setParameter("anno", anno);
			qPratiche.setParameter("ulssId", ulss.getInternalId());
			
			List<CostoDiretto> cdList = qPratiche.getResultList();
			
			if (cdList==null || cdList.size()<1)
				return null;

			for (CostoDiretto cd:cdList){
				String key = "";
				
				CodeValuePhi tipologia = cd.getTipologia();
				
				CodeValuePhi tipoAtt = cd.getTipoAtt();
				CodeValuePhi sottotipoAtt = cd.getSottotipoAtt();
				
				CodeValueLaw articolo = cd.getArticolo();
				
				CodeValuePhi provvedimento = cd.getTipoProvv();
				
				CodeValuePhi qualifica = cd.getQualifica();
				
				String peso = cd.getPeso();
				
				if (tipologia!=null){
					key += tipologia.getCode() + "/";
					
					if ("A".equals(tipologia.getCode())){
						key += tipoAtt!=null?tipoAtt.getCode() + "/":"/";
						key += sottotipoAtt!=null?sottotipoAtt.getCode() + "///":"///";
					} else if ("P".equals(tipologia.getCode()))
						key += provvedimento!=null?"////" + provvedimento.getCode() + "/":"/////";
					else if ("M".equals(tipologia.getCode()))
						key += articolo!=null?"///" + articolo.getCode() + "//":"/////";						
					
					key += qualifica!=null?qualifica.getCode():"";
					
					if (!costiDiretti.containsKey(key.toUpperCase()))
						costiDiretti.put(key.toUpperCase(), peso);
				}
			}

			return costiDiretti;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	
	/**
	 *  Estrae i costi indiretti per anno/ulss, associandoli ad una chiave codificata dal seguente formato:
	 * 	Tipologia/Linea di lavoro/Sottotipo linea di lavoro/Qualifica
	 * 
	 * Es.:	RFP/WORKMEDICINE/01/DIRIGENTE=0.2
	 * 		RFP/GENERIC//DIRIGENTE=0.1
	 * 		RDP/SUPERVISION/YARD/DIRETTORE DEL SERVIZIO=0.1
	 * */
	public HashMap<String, Object> estraiCostiIndiretti(){
		try{			
			ProgettoAssociato pa = getEntity();
			
			Integer anno = pa.getPianificazione().getAnno();
			ServiceDeliveryLocation ulss = pa.getPianificazione().getServiceDeliveryLocation();
			
			HashMap<String, Object> costiIndiretti = new HashMap<String, Object>(1);
			
			if (anno==null || ulss==null)
				return null;
			
			/* I00080334 - punto 3 
			 * Si aggiunge l'attributo 'annoAl' e l'attributo 'anno' assume significato di 'annoDal'
			 * 
			 * Si modifica la query in modo che cerchi i costi (indiretti) con annoDal <= anno della pianificazione'
			 * e annoAl = null o annoAl >= anno della pianificazione

			String qryPratiche = "SELECT DISTINCT ci FROM CostoIndiretto ci " +
					"LEFT JOIN ci.serviceDeliveryLocation ulss " +
					"WHERE ci.anno=:anno AND " +
					"ulss.internalId=:ulssId"; */
			
			String qryPratiche = "SELECT DISTINCT ci FROM CostoIndiretto ci " +
			"LEFT JOIN ci.serviceDeliveryLocation ulss " +
			"WHERE ci.anno <= :anno AND (ci.annoAl IS NULL OR ci.annoAl>= :anno) AND " +
			"ulss.internalId=:ulssId";
			
			Query qPratiche = ca.createQuery(qryPratiche);
			qPratiche.setParameter("anno", anno);
			qPratiche.setParameter("ulssId", ulss.getInternalId());
			
			List<CostoIndiretto> ciList = qPratiche.getResultList();
			
			if (ciList==null || ciList.size()<1)
				return null;

			for (CostoIndiretto ci:ciList){
				String key = "";
				
				CodeValuePhi tipologia = ci.getTipologia();
				
				CodeValuePhi ldl = ci.getLineaDiLavoro();
				CodeValuePhi subLdl = ci.getTipoLineaDiLavoro();
				
				CodeValuePhi qualifica = ci.getQualifica();
				
				String peso = ci.getPeso();
				
				if (tipologia!=null){
					key += tipologia.getCode() + "/";
					key += ldl!=null?ldl.getCode() + "/":"/";
					key += subLdl!=null?subLdl.getCode() + "/":"/";
					key += qualifica!=null?qualifica.getCode():"";

					if (!costiIndiretti.containsKey(key.toUpperCase()))
						costiIndiretti.put(key.toUpperCase(), peso);
				}
			}

			return costiIndiretti;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void addToLog(String toAdd){
		//Aggiungere controllo su paramentro 
		this.logEstrazione += toAdd;
	}
	
	private void addLogHeader(String logHeader){
		//Aggiungere controllo su paramentro 
		this.logEstrazione = logHeader + this.logEstrazione;
	}
	
	/* Corrispondenza tra nomine e link costoNomina
	 * 
	 *	costoNomina01 - Assistente sanitario
	 *	costoNomina02 - Direttore del servizio
	 *	costoNomina03 - Dirigente
	 *	costoNomina04 - Dirigente chimico
	 *	costoNomina05 - Dirigente ingegnere
	 *	costoNomina06 - Dirigente medico
	 *	costoNomina07 - Informatico
	 *	costoNomina08 - Medico sumai
	 *	costoNomina09 - Operatore tecnico specializzato
	 * 	costoNomina10 - Personale amministrativo
	 *	costoNomina11 - Personale di vigilanza ed ispezione
	 *	costoNomina12 - Personale infermieristico
	 *	costoNomina13 - Psicologo del lavoro
	 *	costoNomina14 - Tecnico alimentarista
	 *	costoNomina15 - Tecnico della prevenzione
	 *	costoNomina16 - Tecnico generico */	
	private void updateCostoNomina(Boolean diretto, Boolean rdp, String nomina, Double costo){
		try{
			
			ProgettoAssociato pa = getEntity();
			CostoNomina cn;
			
			if ("Assistente sanitario".equals(nomina))
				cn = pa.getCostoNomina01();
			else if ("Direttore del servizio".equals(nomina))
				cn = pa.getCostoNomina02();
			else if ("Dirigente".equals(nomina))
				cn = pa.getCostoNomina03();
			else if ("Dirigente chimico".equals(nomina))
				cn = pa.getCostoNomina04();
			else if ("Dirigente ingegnere".equals(nomina))
				cn = pa.getCostoNomina05();
			else if ("Dirigente medico".equals(nomina))
				cn = pa.getCostoNomina06();
			else if ("Informatico".equals(nomina))
				cn = pa.getCostoNomina07();
			else if ("Medico sumai".equals(nomina))
				cn = pa.getCostoNomina08();
			else if ("Operatore tecnico specializzato".equals(nomina))
				cn = pa.getCostoNomina09();
			else if ("Personale amministrativo".equals(nomina))
				cn = pa.getCostoNomina10();
			else if ("Personale di vigilanza ed ispezione".equals(nomina))
				cn = pa.getCostoNomina11();
			else if ("Personale infermieristico".equals(nomina))
				cn = pa.getCostoNomina12();
			else if ("Psicologo del lavoro".equals(nomina))
				cn = pa.getCostoNomina13();
			else if ("Tecnico alimentarista".equals(nomina))
				cn = pa.getCostoNomina14();
			else if ("Tecnico della prevenzione".equals(nomina))
				cn = pa.getCostoNomina15();
			else if ("Tecnico generico".equals(nomina))
				cn = pa.getCostoNomina16();
			else 
				cn = new CostoNomina();
			
			cn.setNomina(nomina);
			
			if (diretto!=null && diretto){
				cn.setDirettoTot((cn.getDirettoTot()!=null?cn.getDirettoTot():0.0) + costo);

			} else if (diretto!=null && !diretto){
				if (rdp!=null && rdp){
					cn.setIndirettoRdpTot((cn.getIndirettoRdpTot()!=null?cn.getIndirettoRdpTot():0.0) + costo);

				} else if (rdp!=null && !rdp){
					cn.setIndirettoRfpTot((cn.getIndirettoRfpTot()!=null?cn.getIndirettoRfpTot():0.0) + costo);

				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}
	
	public long round(Double number){
		return Math.round(number);
	}
	
	public String format(Double number){
		return this.format(number, "0.00");
	}
	
	public String format(Double number, String path){
		DecimalFormat df = new DecimalFormat(path);
		return df.format(number);
	}
	
	public String resetTo(Double value){
		DecimalFormat df = new DecimalFormat("0.00");
		
		return "Valore calcolato: " + df.format(value) + " (Click per resettare)";
	}
	
	public Double getTotalePesoMedio(ProgettoAssociato pa){
		 try{
			 Double ret = 0.0;
			 //ProgettoAssociato pa = getEntity();
			 CostoNominaAction cna = (CostoNominaAction)Component.getInstance("CostoNominaAction");
			 
			 ret = cna.getTotalePesoMedio(pa.getCostoNomina01()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina02()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina03()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina04()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina05()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina06()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina07()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina08()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina09()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina10()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina11()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina12()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina13()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina14()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina15()) +
				   cna.getTotalePesoMedio(pa.getCostoNomina16());
			 
			 return ret;
			 
		 } catch (Exception ex) {
			 log.error(ex);
			 throw new RuntimeException(ex);
		 }
	 }
	
	public Double getCaricoAtteso(ProgettoAssociato pa){
		 try{
			 
			 return pa.getPraticheAttese()*this.getTotalePesoMedio(pa);
			 
		 } catch (Exception ex) {
			 log.error(ex);
			 throw new RuntimeException(ex);
		 }
	 }
	
	//Calcola e setta i carichi di tutti i partecipanti del progetto (associato)
	public void setCarichi(ProgettoAssociato pa){ 
		try {
			if (pa==null)
				return;
			
			List<Partecipanti> pList = pa.getPartecipanti();
			if (pList==null || pList.size()<1)
				return;
			
			for (Partecipanti p:pList)
				this.setCarico(p, pa);
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** 
	 *
	 * Carico = (Disponibilità operatore selezionato/Disponibilità totale nomina) * Carico atteso
	 * 
	 * Disponibilità operatore selezionato 	= 	Ore disponibili operatore alle quali va sottratta la sommatoria dei carichi per quell'operatore 
	 * 											in tutti i progetti con priorità maggiore (priorità 1> priorità 2).
	 * 
	 * Disponibilità totale nomina			=	Sommatoria delle ore disponibili di tutti gli operatori della ulss con la stessa nomina 
	 * 											dell'operatore selezionato.
	 * 
	 * Carico atteso						= 	Carico atteso sul progetto per la nomina selezionata.
	 * 
	 **/
	public void setCarico(Partecipanti part, ProgettoAssociato pa){
		try {
			if (part==null || part.getOperatore()==null || part.getOperatore().getCode()==null)
				return;
			
			if (pa==null)
				pa = getEntity();
			
			PianificazioneAction pianificazioneAction = (PianificazioneAction)Component.getInstance("PianificazioneAction");
			
			Operatore op = part.getOperatore();
			String nomina = part.getOperatore().getCode().getCode();

			Pianificazione pianificazione = pa.getPianificazione();
			Integer anno = pianificazione.getAnno();
			Integer numeroPraticheAttese = pa.getPraticheAttese();
			
			part.setNote("<b>Operatore:</b> " + op.getName() + " (" + nomina + ")<br>");
			
			/** Disponibilita operatore selezionato **/
			Double disponibilitaOperatore = 0.0;
			
			if (op!=null && pianificazione!=null && anno!=null)
				disponibilitaOperatore = this.getDisponibilitaOperatore(pa, part, op, anno);
			/*****************************************/
			
			/** Disponibilita totale nomina [vedi PianificazioneAction.updateDisponibilitaNomina(Pianificazione p)]
			 *  vengono registrate in una hash ogni volta che si seleziona/modifica una pianificazione          **/
			Double dispTotNomina = 0.0;
			Object dn = pianificazioneAction.getTemporary().get("disponibilitaNomine");
			
			if (dn!=null){
				Object dtn = ((HashMap<String,Double>)dn).get(nomina);
				
				if (dtn!=null){
					dispTotNomina = (Double)dtn;
					part.setNote(part.getNote() + "Disponibilita totale nomina (" + nomina + "): " + dispTotNomina + "<br>");
				}
			}
			/*******************************************************************************************************/

			/** Carico atteso - Carico atteso sul progetto per la nomina selezionata **/
			Boolean isRdp = part.getRdp();
			Boolean isRfp = part.getRfp();
			Double costoNomina = this.getCostoNomina(pa, part, nomina, isRdp, isRfp);
			part.setCostoNomina(costoNomina);
			
			part.setNote(part.getNote() + "Numero pratiche attese: " + numeroPraticheAttese + "<br>");
			
			Double caricoAtteso = costoNomina*numeroPraticheAttese;
			part.setCaricoAtteso(caricoAtteso);
			
			part.setNote(part.getNote() + "Carico atteso (Costo nomina * numero pratiche attese): " + caricoAtteso + "<br>");

			/*******************/			

			/** Calcolo carico e numeroPratiche **/
			Double carico = 0.0;
			Double numeroPratiche = 0.0;
			
			if (dispTotNomina!=0.0){
				carico = (disponibilitaOperatore/dispTotNomina)*caricoAtteso;
				part.setNote(part.getNote() + "<b>Carico</b> = (Disponibilita totale operatore/Disponibilita totale nomina)*Carico atteso: " + carico + "<br>");

				if (carico!=0.0){
					Double totaleCostoNomina = this.getCostoNomina(pa, part, nomina, true, true);
					
					if (totaleCostoNomina!=0.0){
						numeroPratiche = carico/totaleCostoNomina;
						part.setNote(part.getNote() + "<b>Numero pratiche</b> = Carico/Totale costo nomina: " + numeroPratiche + "<br>");
					}
				}
			}
			
			part.setCaricoProgetto(carico);
			part.setNumeroPratiche(numeroPratiche);
			
			if (part.getNumeroPraticheMod()==null)
				part.setNumeroPraticheMod((double)this.round(numeroPratiche));
				
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Double getCostoNomina(ProgettoAssociato pa, Partecipanti part, String nomina, Boolean isRdp, Boolean isRfp){
		try {
			Double ret = 0.0;
			isRdp=isRdp==null?false:isRdp;
			isRfp=isRfp==null?false:isRfp;
			
			if (pa==null)
				pa = getEntity();
			
			CostoNomina cn = null;
			
			if ("Assistente sanitario".equals(nomina))
				cn = pa.getCostoNomina01();
			else if ("Direttore del servizio".equals(nomina))
				cn = pa.getCostoNomina02();
			else if ("Dirigente".equals(nomina))
				cn = pa.getCostoNomina03();
			else if ("Dirigente chimico".equals(nomina))
				cn = pa.getCostoNomina04();
			else if ("Dirigente ingegnere".equals(nomina))
				cn = pa.getCostoNomina05();
			else if ("Dirigente medico".equals(nomina))
				cn = pa.getCostoNomina06();
			else if ("Informatico".equals(nomina))
				cn = pa.getCostoNomina07();
			else if ("Medico sumai".equals(nomina))
				cn = pa.getCostoNomina08();
			else if ("Operatore tecnico specializzato".equals(nomina))
				cn = pa.getCostoNomina09();
			else if ("Personale amministrativo".equals(nomina))
				cn = pa.getCostoNomina10();
			else if ("Personale di vigilanza ed ispezione".equals(nomina))
				cn = pa.getCostoNomina11();
			else if ("Personale infermieristico".equals(nomina))
				cn = pa.getCostoNomina12();
			else if ("Psicologo del lavoro".equals(nomina))
				cn = pa.getCostoNomina13();
			else if ("Tecnico alimentarista".equals(nomina))
				cn = pa.getCostoNomina14();
			else if ("Tecnico della prevenzione".equals(nomina))
				cn = pa.getCostoNomina15();
			else if ("Tecnico generico".equals(nomina))
				cn = pa.getCostoNomina16();
			else 
				cn = new CostoNomina();
			
			if (cn!=null){
				ret = cn.getDirettoMedMod();
				if (isRdp)
					ret += cn.getIndirettoRdpMedMod();
				if (isRfp)
					ret += cn.getIndirettoRfpMedMod();
			}
			
			part.setNote(part.getNote() + "Costo nomina (" + nomina + " - RDP (" + isRdp + ") - RFP (" + isRfp + ")): " + ret + "<br>");

			return ret;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* Ore disponibili operatore meno la sommatoria dei carichi su quell'operatore per 
	   tutti i progetti con priorità maggiore della priorità del progetto in elaborazione */	
	 private Double getDisponibilitaOperatore(ProgettoAssociato pa, Partecipanti part, Operatore op, Integer anno){
		try {
			Double disponibilitaIniziale = 0.0;
			
			for (MonteOre mo:op.getMonteOre()){
				if (mo.getAnno().equals(anno)){
					disponibilitaIniziale = mo.getHhDisponibili();

					part.setNote(part.getNote() + "Disponibilita iniziale operatore: " + disponibilitaIniziale + "<br>");
				}
			}
			
			Double disponibilitaOperatore = disponibilitaIniziale - this.getSommatoriaCarichi(pa, part, op);

			part.setDisponibilitaOperatore(disponibilitaOperatore);
			part.setNote(part.getNote() + "Disponibilita totale operatore (Disponibilita iniziale - sommatoria carichi): " + disponibilitaOperatore + "<br>");
			
			return disponibilitaOperatore;
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	 
	/* Sommatoria dei carichi su quell'operatore per in tutti i progetti *
	 * con priorità maggiore della priorità del progetto in elaborazione */ 
	public Double getSommatoriaCarichi(ProgettoAssociato pa, Partecipanti part, Operatore op){
		try {
			if (pa==null)
				pa = getEntity();
			
			Double sommatoriaCarichi = 0.0;
			
			List<ProgettoAssociato> progettiAssociatiList = pa.getPianificazione().getProgettiAssociati();
			
			//Itero la lista dei progetti associati alla pianificazione
			for (ProgettoAssociato progettoAssociato:progettiAssociatiList){
				if (!pa.equals(progettoAssociato)){
					//Se trovo un progetto con priorità maggiore (numericamente minore - priorità 1 > priorità 2)
					if (progettoAssociato.getPriorita() < pa.getPriorita()){
						//Cerco se il mio operatore è già associato al progetto
						for (Partecipanti partecipante:progettoAssociato.getPartecipanti()){
							if (partecipante.getOperatore().equals(op)){
								Double carico = partecipante.getCaricoProgetto();
								sommatoriaCarichi += (carico==null?0.0:carico);
							}
						}
					}
				}
			}
			
			part.setNote(part.getNote() + "Sommatoria carichi operatore: " + sommatoriaCarichi + "<br>");

			return sommatoriaCarichi;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
}