package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Gruppi;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("GruppiAction")
@Scope(ScopeType.CONVERSATION)
public class GruppiAction extends BaseAction<Gruppi, Long> {

	private static final long serialVersionUID = 627274567L;
	private static final Logger log = Logger.getLogger(GruppiAction.class);

	public static GruppiAction instance() {
		return (GruppiAction) Component.getInstance(GruppiAction.class, ScopeType.CONVERSATION);
	}

	public void refreshGroups(Provvedimenti prov){
		if(prov==null || prov.getArticoli()==null || prov.getArticoli().isEmpty())
			return;

		List<Gruppi> lista = new ArrayList<Gruppi>();
		for(Articoli art : prov.getArticoli()){
			if(art.getGruppo()!=null && !lista.contains(art.getGruppo()))
				lista.add(art.getGruppo());
		}

		refresh(lista);
	}

	public void setArticoli(List<Articoli> artList, List<Gruppi> groupList){
		try{

			String name = getTemporary().get("name").toString();
			getTemporary().remove("name");

			if (name==null || name.isEmpty())
				return;

			Gruppi gruppo = null;

			if (groupList != null && groupList.size() > 0 ) { 
				for (Gruppi grp : groupList) {
					if (grp.getName().equals(name)){
						gruppo=grp;
						break;
					}
				}
			}

			if (gruppo==null){
				gruppo = new Gruppi();
				gruppo.setName(name);
				ca.create(gruppo);
			}

			if (artList != null && artList.size() > 0 ) { 
				for (Articoli articolo : artList) {
					if (Boolean.TRUE.equals(articolo.getIsNew())){

						if(articolo.getGruppo()!=null){
							Gruppi aGroup = articolo.getGruppo();
							articolo.getGruppo().removeArticoli(articolo);
							resetGroup(aGroup);
						}

						articolo.setGruppo(gruppo);
						gruppo.addArticoli(articolo);
						articolo.setIsNew(null);
						resetGroup(gruppo);
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public  Integer add(Integer ...integers) {
		Integer res=0;
		try {
			for(int i=0;i<integers.length;i++){
				if (integers[i]!=null)
					res = res + integers[i];
			}
		} catch (NumberFormatException e) {
			log.error(e);
			return null;
		}

		return res;		
	}

	public  Date scadenzaPerOttemperare(Integer gi, Integer pp, Date dn) {
		return scadenzaPerOttemperare(gi, pp, 0, dn);

	}

	public  Date scadenzaPerOttemperare(Integer gi, Integer pp, Integer sp, Date dn) {

		try {			
			Integer tg = add(gi, pp, sp);

			if (tg != null)
				return addDays(dn, tg);

			return null;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	public  Date scadenzaDellaVerifica(Integer gi, Integer pp, Integer sp, Date dn) {
		return addDays(scadenzaPerOttemperare(gi, pp, sp, dn), 60);
	}

	public  Date scadenzaDellaVerifica(Integer gi, Integer pp, Date dn, Date der, boolean ricorso, String esito) {
		//Se è stato fatto un ricorso e il ricorso non è stato accolto
		if (ricorso && esito!=null && "NO".equals(esito))
			//Ricalcola partendo dalla data esito ricorso (der)
			return addDays(scadenzaPerOttemperare(gi, pp, der), 60);
		//Altrimenti calcola partendo da data notifica (dn)
		return addDays(scadenzaPerOttemperare(gi, pp, dn), 60);
	}

	public static Date addDays(Date date, Integer days) {
		if (date==null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	public Date getScadenzaPerOttemperareMin(List<Articoli> artList){
		try{
			Date scadenzaPerOttemperareMin = null;

			if (artList == null || artList.size() <= 0) 
				return scadenzaPerOttemperareMin;
			else
				for (Articoli articolo : artList) {
					Gruppi gruppo = articolo.getGruppo();
					if (gruppo != null) { 
						Date scadenzaPerOttemperare = scadenzaPerOttemperare(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), articolo.getProvvedimento().getDataNotifica());
						if (scadenzaPerOttemperare!=null)
							if (scadenzaPerOttemperareMin==null || scadenzaPerOttemperare.before(scadenzaPerOttemperareMin))
								scadenzaPerOttemperareMin=scadenzaPerOttemperare;
					}
				}

			return scadenzaPerOttemperareMin;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
			//return null;
		} 
	}

	public boolean hasArticoloWithEsito(Gruppi gruppo, String esito){
		if(gruppo!=null && gruppo.getArticoli()!=null && !gruppo.getArticoli().isEmpty() && esito!=null){
			for(Articoli art : gruppo.getArticoli()){
				if(art.getEsito()!=null && esito.equals(art.getEsito().getCode()))
					return true;
			}

		}
		return false;
	}

	public void resetGroup(Gruppi aGroup){
		if(!hasArticoloWithEsito(aGroup, "compliedPayed")){
			aGroup.setAmmissionePagamento(null);
			aGroup.setNotificaPagamento(null);
			aGroup.setImportoVersato(null);
			aGroup.setSpeseNotifica(null);
			aGroup.setDataPagamento(null);
		}
		if(!hasArticoloWithEsito(aGroup, "complayedDifferent")){
			aGroup.setDettagli(null);
		}
	}

	public Boolean allArticoliWithEsito(Gruppi gruppo, String esito) {
		if (gruppo == null || gruppo.getArticoli() == null || gruppo.getArticoli().isEmpty() || esito == null)
			return false;

		for(Articoli art : gruppo.getArticoli()){
			//Se ne trova uno senza esito o con esito diverso da quello richiesto, restituisce false
			if(art.getEsito()==null || !esito.equals(art.getEsito().getCode()))
				return false;
		}

		return true;

	}

	@SuppressWarnings("static-access")
	public void updateScadenze(Provvedimenti provv){
		try {
			String type = "";
			Gruppi gruppo = null;
			List<Articoli> articoli = provv.getArticoli();
			List<String> esiti = new ArrayList<String>();

			if (articoli!=null && articoli.size()>0) {
				gruppo = articoli.get(0).getGruppo();

				for (Articoli articolo : articoli) {
					String esito = null;
					if (articolo.getEsito()!=null)
						esito = articolo.getEsito().getCode();

					if (!esiti.contains(esito))
						esiti.add(esito);
				}
			}

			if (gruppo!=null && provv!=null && provv.getType()!=null) {
				type = provv.getType().getCode();

				//Date da settare in Articoli.gruppo per Provvedimenti.type.code (758 e 301bis)
				if (type.equals("758") || type.equals("301bis")) {
					//Scadenza della verifica
					gruppo.setScadenzaDellaVerifica(this.scadenzaDellaVerifica(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), provv.getDataNotifica()));

					//Se esito = Ammissione al pagamento senza prescrizione
					if (esiti.contains("compliedNoPrescription")) {
						//Data scadenza pagamento (dataScadenzaPagamentoNP)
						gruppo.setDataScadenzaPagamentoNP(this.addDays(gruppo.getNotificaPagamentoNP(), 30));

						//Data scadenza comunicazione al PM (dataScadenzaComPMNP)
						gruppo.setDataScadenzaComPMNP(this.addDays(gruppo.getNotificaPagamentoNP(), 150));
					} else {
						gruppo.setDataScadenzaPagamentoNP(null);
						gruppo.setDataScadenzaComPMNP(null);

					}	

					//Se esito = Inottemperato
					if (esiti.contains("notComplayed") && type.equals("758")) {
						//Data scadenza comunicazione dell'inottemperanza al contravventore (scadComInottemperanza)
						gruppo.setScadComInottemperanza(this.addDays(this.scadenzaPerOttemperare(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), provv.getDataNotifica()), 90));

						//Data scadenza comunicazione dell'inottemperanza al PM (scadComInottemperanzaPM)
						gruppo.setScadComInottemperanzaPM(this.addDays(this.scadenzaPerOttemperare(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), provv.getDataNotifica()), 90));

					} else {
						gruppo.setScadComInottemperanza(null);
						gruppo.setScadComInottemperanzaPM(null);

					}	

					//Se esito = Ottemperato
					if (esiti.contains("compliedPayed")) {
						//Data scadenza pagamento (dataScadenzaPagamento)
						gruppo.setDataScadenzaPagamento(this.addDays(gruppo.getNotificaPagamento(), 30));

						if (type.equals("758")){
							//Data scadenza comunicazione al PM (scadenzaComPM)
							gruppo.setScadenzaComPM(this.addDays(gruppo.getNotificaPagamento(), 150));
						} else if (type.equals("301bis")){
							//Data scadenza comunicazione al comune  
							//Articoli.gruppo.scadComunicazioneComune
						}

					} else {
						gruppo.setDataScadenzaPagamento(null);
						gruppo.setScadenzaComPM(null);
					}

					//Se esito = Ottemperato con modi diversi	
					if (esiti.contains("complayedDifferent")) {
						//Data scadenza comunicazione dell'ottemperanza con modi diversi al contravventore (scadOttModiDiversi)
						gruppo.setScadOttModiDiversi(this.addDays(this.scadenzaPerOttemperare(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), provv.getDataNotifica()), 90));

						//Data scadenza comunicazione dell'ottemperanza con modi diversi al PM (scadOttModiDiversiPM)
						gruppo.setScadOttModiDiversiPM(this.addDays(this.scadenzaPerOttemperare(gruppo.getGiorniIniziali(), gruppo.getPrimaProroga(), gruppo.getSecondaProroga(), provv.getDataNotifica()), 90));
					} else {
						gruppo.setScadOttModiDiversi(null);
						gruppo.setScadOttModiDiversiPM(null);
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String computeClass(String importo, Double min){
		String rtn = "";

		try {
			Double imp = Double.parseDouble(importo);
			if(imp!=null && !imp.equals(min)){
				rtn = "c-text-red";
			}
		} catch (NumberFormatException e) {
			//nothing to do
		}

		return rtn;
	}

	public String computeClass(Boolean notPayed){
		String rtn = "";

		if(!Boolean.TRUE.equals(notPayed)){
			rtn = "layoutRequired";
		}
		return rtn;
	}

	public void setImportoVersato(Gruppi g, String importo){
		try {
			Double imp = Double.parseDouble(importo);
			g.setImportoVersato(imp);
		} catch (NumberFormatException e) {
			g.setImportoVersato(null);
		}
	}

	public void setImportoVersatoNP(Gruppi g, String importo){
		try {
			Double imp = Double.parseDouble(importo);
			g.setImportoVersatoNP(imp);
		} catch (NumberFormatException e) {
			g.setImportoVersatoNP(null);
		}
	}

	public void setTempImportoVersato(Gruppi g){
		if(g!=null && g.getImportoVersato()!=null){
			getTemporary().put("importoVersato", g.getImportoVersato().toString());
		}else{
			getTemporary().put("importoVersato", null);
		}
	}

	public void setTempImportoVersatoNP(Gruppi g){
		if(g!=null && g.getImportoVersatoNP()!=null){
			getTemporary().put("importoVersatoNP", g.getImportoVersatoNP().toString());
		}else{
			getTemporary().put("importoVersatoNP", null);
		}
	}

	public boolean isUlssEnabledToDateArchiv(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter dateArchivVisible = ca.get(CodeValueParameter.class, "p.home.procpratiche.datearchiv");

		if (dateArchivVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(dateArchivVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}

}