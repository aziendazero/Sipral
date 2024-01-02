package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Gruppi;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ArticoliAction")
@Scope(ScopeType.CONVERSATION)
public class ArticoliAction extends BaseAction<Articoli, Long> {

	private static final long serialVersionUID = 1218432429L;
	private static final Logger log = Logger.getLogger(ArticoliAction.class);

	public static ArticoliAction instance() {
		return (ArticoliAction) Component.getInstance(ArticoliAction.class, ScopeType.CONVERSATION);
	}

	public boolean articoliSelected(List<Articoli> list){
		try{
			if (list == null || list.size() <= 0 )
				return false;

			else 
				for (Articoli art : list) {
					if (art.getIsNew() != null && art.getIsNew())
						return true;
				}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setTmp(List<Articoli> list){
		try{
			if (list==null || list.size()<=0)
				return;
			else {
				boolean ottemperanza = false;
				boolean inottemperanza = false;
				boolean ottemperanzaModiDiversi = false;

				for (Articoli art : list) {
					if (art.getEsito()!= null){ 
						if (art.getEsito().getCode().equals("compliedPayed"))
							ottemperanza = true;

						else if (art.getEsito().getCode().equals("notComplayed"))
							inottemperanza = true;

						else if (art.getEsito().getCode().equals("complayedDifferent"))
							ottemperanzaModiDiversi = true;	
					}
				}

				getTemporary().put("ottemperanza", ottemperanza);
				getTemporary().put("inottemperanza", inottemperanza);
				getTemporary().put("ottemperanzaModiDiversi", ottemperanzaModiDiversi);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public Double getImportoSanzione(List<Articoli> artList, String nomeGruppo, String esito){
		try{
			if (artList == null || artList.isEmpty() || nomeGruppo==null || nomeGruppo.isEmpty())
				return null;

			Double importoSanzione=0.00;

			for (Articoli articolo : artList) {
				if (articolo.getEsito() != null && articolo.getEsito().getCode().equals(esito)
						&& articolo.getGruppo()!=null && nomeGruppo.equals(articolo.getGruppo().getName())) { 
					Double max = 0.00;
					if (articolo.getCode()!= null && articolo.getCode().getImportoMax()!=null && !"".equals(articolo.getCode().getImportoMax()))
						max = Double.parseDouble(articolo.getCode().getImportoMax().replace(',', '.'));

					if (max != null)
						importoSanzione += max/4.00;
				}						
			}

			String importo = String.format("%.2f", importoSanzione);
			return Double.parseDouble(importo.replace(',', '.'));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double getImportoSanzioneMin(List<Articoli> artList, String nomeGruppo, String esito){
		try{
			if (artList == null || artList.isEmpty() || nomeGruppo==null || nomeGruppo.isEmpty())
				return null;

			Double importoSanzione=0.00;

			for (Articoli articolo : artList) {
				if (articolo.getEsito() != null && articolo.getEsito().getCode().equals(esito)
						&& articolo.getGruppo()!=null && nomeGruppo.equals(articolo.getGruppo().getName())) { 
					Double min = 0.00;
					if (articolo.getCode()!= null && articolo.getCode().getImportoMin()!=null && !"".equals(articolo.getCode().getImportoMin()))
						min = Double.parseDouble(articolo.getCode().getImportoMin().replace(',', '.'));

					if (min != null)
						importoSanzione += min;
				}						
			}

			String importo = String.format("%.2f", importoSanzione);
			return Double.parseDouble(importo.replace(',', '.'));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double getImportoSanzione(Articoli art){
		try{
			if (art == null)
				return null;

			Double importoSanzione = 0.00;
			Double max = 0.00;

			if (art.getCode() != null && art.getCode().getImportoMax() != null && !"".equals(art.getCode().getImportoMax()))
				max = Double.parseDouble(art.getCode().getImportoMax().replace(',', '.'));

			if (max != null)
				importoSanzione += max/4.00;

			if (importoSanzione!=0.00){
				String importo = String.format("%.2f", importoSanzione);
				return Double.parseDouble(importo.replace(',', '.'));
			}

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double getImportoSanzioneMin(Articoli art){
		try{
			if (art == null)
				return null;

			Double min = 0.00;

			if (art.getCode() != null && art.getCode().getImportoMin() != null && !"".equals(art.getCode().getImportoMin()))
				min = Double.parseDouble(art.getCode().getImportoMin().replace(',', '.'));

			if (min!=0.00) {
				String importo = String.format("%.2f", min);
				return Double.parseDouble(importo.replace(',', '.'));
			}

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String getImportoSanzione(){

		if(getEntity()!=null && getEntity().getCode()!=null && getEntity().getCode().getImportoMax()!=null && !"".equals(getEntity().getCode().getImportoMax())){

			try{
				// "importo sanzione (1/4 del massimo)"
				double importoMax =  Double.parseDouble(getEntity().getCode().getImportoMax().replaceAll(",", "."))/4;
				return String.valueOf(importoMax);
			}catch (Exception e) {
				return null;
			}
		}

		return null;

	}

	public Double getImportoTotale(List<Articoli> artList, Double speseNotifica, String nomeGruppo, String esito){
		Double importoSanzione = getImportoSanzione(artList, nomeGruppo, esito);

		if (importoSanzione!= null){
			if (speseNotifica!= null)
				importoSanzione += speseNotifica;
			return importoSanzione;
		}

		return null;
	}

	public Double getImportoTotaleMin(List<Articoli> artList, Double speseNotifica, String nomeGruppo, String esito){
		Double importoSanzione = getImportoSanzioneMin(artList, nomeGruppo, esito);

		if (importoSanzione!= null)
			return importoSanzione + speseNotifica;

		return null;
	}

	public void initDefaultGroup(Articoli art, Provvedimenti prov) throws PersistenceException{
		if(art==null || prov==null || prov.getArticoli()==null || prov.getArticoli().isEmpty() || art.getGruppo()!=null)
			return;

		Gruppi defaultGroup = null;
		//IL GRUPPO DI DEFAULT E' SEMPRE QUELLO CHE SI CHIAMA "1"
		for(Articoli provArt : prov.getArticoli()){
			if(provArt.getGruppo()!=null && "1".equals(provArt.getGruppo().getName())){
				defaultGroup = provArt.getGruppo();
				break;
			}
		}
		if(defaultGroup==null){
			defaultGroup=new Gruppi();
			defaultGroup.setName("1");			
		}

		ca.create(defaultGroup);
		art.setGruppo(defaultGroup);
		defaultGroup.addArticoli(art);
	}

	public String getStato(Articoli art){
		try{
			if (art == null)
				return "";

			CodeValuePhi esitoCv = art.getEsito();

			if (esitoCv == null || esitoCv.getCode()==null)
				return "";

			Gruppi gruppo = art.getGruppo();
			if (gruppo == null)
				return "";

			String esito = esitoCv.getCode();

			//Se l'articolo è ottemperato (compliedPayed)
			if (esito.equals("compliedPayed")) {

				//Se la data pagamento è valorizzata
				if (gruppo.getDataPagamento()!=null)
					return "Pagato";

				//Se la data notifica ammissione al pagamento è valorizzata
				if (gruppo.getNotificaPagamento()!=null)
					return "Invio notificato";

				//Se la data invio ammissione al pagamento è valorizzata
				if (gruppo.getAmmissionePagamento()!=null)
					return "Invio inviato";

				//Se la data della verifica è valorizzata
				if (gruppo.getDataDellaVerifica()!=null)
					return "Verificato";

				else return "Da verificare";

				//Se l'articolo è con Ammissione al pagamento senza prescrizione(compliedNoPrescription)
			} else if (esito.equals("compliedNoPrescription")){

				//Se la data pagamento è valorizzata
				if (gruppo.getDataPagamentoNP()!=null)
					return "Pagato";

				//Se la data notifica ammissione al pagamento è valorizzata
				if (gruppo.getNotificaPagamentoNP()!=null)
					return "Invio notificato";

				//Se la data invio ammissione al pagamento è valorizzata
				if (gruppo.getAmmissionePagamentoNP()!=null)
					return "Invio inviato";

				//Se la data della verifica è valorizzata
				//if (gruppo.getDataDellaVerifica()!=null)
				//	return "Verificato";

				else return "Da verificare";

				//Se l'articolo è inottemperato (notComplied)
			} else if (esito.equals("notComplayed")){

				//Se la data comunicazione dell'inottemperanza è valorizzata
				if (gruppo.getComunicazioneInottemperanza()!=null)
					return "Com. inottemperanza";

				//Se la data della verifica è valorizzata
				if (gruppo.getDataDellaVerifica()!=null)
					return "Verificato";

				else return "Da verificare";

				//Se l'articolo è ottemperato con modi diversi (complayedDifferent)
			} else if (esito.equals("complayedDifferent")){

				//Se la data comunicazione dell'inottemperanza è valorizzata
				if (gruppo.getOttemperanzaModiDiversi()!=null)
					return "Com. ottemperanza con modi diversi";

				//Se la data della verifica è valorizzata
				if (gruppo.getDataDellaVerifica()!=null)
					return "Verificato";

				else return "Da verificare";


			}
			return "";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean isDuplicate(Provvedimenti prov){
		Articoli currentArt = getEntity();

		if(currentArt == null)
			return false;

		if(currentArt.getCode() == null)
			return false;

		List<Articoli> list = prov.getArticoli();

		if(list == null)
			return false;

		String toSearch = currentArt.getCode().getId();

		boolean isDuplicate = false;
		int iter = 0;
		while(iter < list.size() && !isDuplicate){
			Articoli iteratedArt = list.get(iter);
			CodeValueLaw cvl = iteratedArt.getCode();
			if(cvl!=null){
				String codeId = cvl.getId();
				if(toSearch.equals(codeId) && currentArt.getInternalId()!=iteratedArt.getInternalId())
					isDuplicate=true;
			}			
			iter++;
		}

		return isDuplicate;
	}

	public Double getImportoSanzioneSosp(List<Articoli> artList){
		try{
			if (artList == null || artList.isEmpty())
				return null;

			Double importoSanzione=0.00;

			PersoneArticoliAction paAction = PersoneArticoliAction.instance();

			for (Articoli articolo : artList) {
				Double min = 0.00;
				if (articolo.getCode()!= null && articolo.getCode().getImportoMin()!=null && !"".equals(articolo.getCode().getImportoMin()))
					min = Double.parseDouble(articolo.getCode().getImportoMin().replace(',', '.'));

				if(articolo.getCode()!=null && "lav".equals(articolo.getCode().getNotaContravventore())){
					int numLavoratori = paAction.getPersoneArticoli(articolo).size();
					min = min * numLavoratori;
				}

				if (min != null)
					importoSanzione += min;
			}

			String importo = String.format("%.2f", importoSanzione);
			return Double.parseDouble(importo.replace(',', '.'));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double getImportoTotaleSosp(List<Articoli> artList, Boolean raddoppia){
		Double importoSanzione = getImportoSanzioneSosp(artList);

		if (importoSanzione!= null && Boolean.TRUE.equals(raddoppia)){
			return importoSanzione * 2;
		}else{
			return importoSanzione;
		}
	}

	public Double getImportoDovutoSosp(List<Articoli> artList, Boolean raddoppia){
		Double importoTotale = getImportoTotaleSosp(artList, raddoppia);

		// pagamento con dilazione: 20% del totale
		if (importoTotale!= null)
			return importoTotale / 5;

		return null;
	}

	public Double getImportoResiduoSosp(List<Articoli> artList, Boolean raddoppia){
		Double importoTotale = getImportoTotaleSosp(artList, raddoppia);
		Double importoDovuto = getImportoDovutoSosp(artList, raddoppia);
		
		// pagamento con dilazione: importo restante maggiorato del 5%
		if (importoTotale!= null && importoDovuto!=null)
			return (importoTotale - importoDovuto) * 1.05 ;

		return null;
	}
}