package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Sostanze;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("SostanzeAction")
@Scope(ScopeType.CONVERSATION)
public class SostanzeAction extends BaseAction<Sostanze, Long> {

	private static final long serialVersionUID = 1383288154L;

	public static SostanzeAction instance() {
		return (SostanzeAction) Component.getInstance(SostanzeAction.class, ScopeType.CONVERSATION);
	}

	public Sostanze copy(Sostanze oldSost){
		if(oldSost==null)
			return null;

		Sostanze newSost = new Sostanze();
		newSost.setBio(oldSost.getBio());
		newSost.setCodiceCE(oldSost.getCodiceCE());
		newSost.setGruppo3(oldSost.getGruppo3());
		newSost.setQuantita(oldSost.getQuantita());
		newSost.setSchedaEsposti(oldSost.getSchedaEsposti());
		newSost.setSostanza(oldSost.getSostanza());
		newSost.setTipologia(oldSost.getTipologia());
		newSost.setAgente(oldSost.getAgente());
		
		return newSost;
	}

	public void setDomain(Sostanze sost){
		getAutocomplete().get("sostanza").setContentType(4);
		getAutocomplete().get("sostanza").setCodeSystem("PHIDIC");
		getAutocomplete().get("sostanza").setFullLike(true);
		if(sost.getAgente()!=null)
			getAutocomplete().get("sostanza").setDomain(sost.getTipologia().getDisplayName());
		else
			getAutocomplete().get("sostanza").setDomain("NCAS");
	}
	
	public void resetValues(Sostanze sost) throws PersistenceException, DictionaryException{
		getAutocomplete().get("sostanza").setContentType(4);
		getAutocomplete().get("sostanza").setCodeSystem("PHIDIC");
		getAutocomplete().get("sostanza").setFullLike(true);
		if(sost!=null){
			sost.setTipologia(null);
			sost.setSostanza(null);
			if(sost.getAgente()!=null){
				Vocabularies voc = VocabulariesImpl.instance();
				if("01".equals(sost.getAgente().getCode()) || "02".equals(sost.getAgente().getCode())){
					CodeValue ncas = voc.getCodeValue("PHIDIC", "FattoriRischio", "ncas", "S");
					if(ncas!=null){
						sost.setTipologia((CodeValuePhi) ncas);
						getAutocomplete().get("sostanza").setDomain(ncas.getDisplayName());
					}

				}else if("03".equals(sost.getAgente().getCode())){
					CodeValue nall = voc.getCodeValue("PHIDIC", "FattoriRischio", "nall", "S");
					if(nall!=null){
						sost.setTipologia((CodeValuePhi) nall);
						getAutocomplete().get("sostanza").setDomain(nall.getDisplayName());
					}
				}
			}
		}
	}
}