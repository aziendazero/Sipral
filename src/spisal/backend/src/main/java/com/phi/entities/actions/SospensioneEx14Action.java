package com.phi.entities.actions;

import java.util.Date;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.SospensioneEx14;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SospensioneEx14Action")
@Scope(ScopeType.CONVERSATION)
public class SospensioneEx14Action extends BaseAction<SospensioneEx14, Long> {

	private static final long serialVersionUID = 472993387L;

	public static SospensioneEx14Action instance() {
		return (SospensioneEx14Action) Component.getInstance(SospensioneEx14Action.class, ScopeType.CONVERSATION);
	}

	public void fillTemporaryFromDecorrenza(){
		
		if(getEntity()==null)
			return;
		
		String decorrenzaABC = getEntity().getDecorrenzaABC();
		Date decorrenzaTime = getEntity().getDecorrenzaTimestamp();
		Date decorrenzaHHMM = getEntity().getDecorrenzaHHMM();
		
		if("A".equals(decorrenzaABC)){
			getTemporary().put("A", true);
			getTemporary().put("B", false);
			getTemporary().put("C", false);
			getTemporary().put("decorrenzaB", null);
			getTemporary().put("decorrenzaC", null);
			getTemporary().put("decorrenzaHHMM", null);
		}else if("B".equals(decorrenzaABC)){
			getTemporary().put("A", false);
			getTemporary().put("B", true);
			getTemporary().put("C", false);
			getTemporary().put("decorrenzaB", decorrenzaTime);
			getTemporary().put("decorrenzaC", null);
			getTemporary().put("decorrenzaHHMM", decorrenzaHHMM);
		}else if("C".equals(decorrenzaABC)){
			getTemporary().put("A", false);
			getTemporary().put("B", false);
			getTemporary().put("C", true);
			getTemporary().put("decorrenzaB", null);
			getTemporary().put("decorrenzaC", decorrenzaTime);
			getTemporary().put("decorrenzaHHMM", null);
		}
		
		getTemporary().put("comunicazioneAutorita", getEntity().getComAutGiud());
	}

	public void resetOtherDecorrenza(String lettera){
		
		if(getEntity()==null)
			return;

		Date decorrenzaTime = getEntity().getDecorrenzaTimestamp();
		Date decorrenzaHHMM = getEntity().getDecorrenzaHHMM();

		if("A".equals(lettera)){
			getTemporary().put("A", true);
			getTemporary().put("B", false);
			getTemporary().put("C", false);
			getTemporary().put("decorrenzaB", null);
			getTemporary().put("decorrenzaC", null);
			getTemporary().put("decorrenzaHHMM", null);
		}else if("B".equals(lettera)){
			getTemporary().put("A", false);
			getTemporary().put("B", true);
			getTemporary().put("C", false);
			getTemporary().put("decorrenzaB", decorrenzaTime);
			getTemporary().put("decorrenzaC", null);
			getTemporary().put("decorrenzaHHMM", decorrenzaHHMM);
		}else if("C".equals(lettera)){
			getTemporary().put("A", false);
			getTemporary().put("B", false);
			getTemporary().put("C", true);
			getTemporary().put("decorrenzaB", null);
			getTemporary().put("decorrenzaC", decorrenzaTime);
			getTemporary().put("decorrenzaHHMM", null);
		}
	}
	
	public void setDecorrenzaABC(){
		
		if(getEntity()==null)
			return;

		if(Boolean.TRUE.equals(getTemporary().get("A"))){
			getEntity().setDecorrenzaABC("A");
		}else if(Boolean.TRUE.equals(getTemporary().get("B"))){
			getEntity().setDecorrenzaABC("B");
			getEntity().setDecorrenzaTimestamp((Date) getTemporary().get("decorrenzaB"));
			getEntity().setDecorrenzaHHMM((Date) getTemporary().get("decorrenzaHHMM"));
		}else if(Boolean.TRUE.equals(getTemporary().get("C"))){
			getEntity().setDecorrenzaABC("C");
			getEntity().setDecorrenzaTimestamp((Date) getTemporary().get("decorrenzaC"));
		}

		getEntity().setComAutGiud((Boolean) getTemporary().get("comunicazioneAutorita"));

	}

	public void udpateEsitoProvvedimento(Provvedimenti provv) throws PersistenceException, DictionaryException{
		
		boolean esitoA = Boolean.TRUE.equals((Boolean) getTemporary().get("esitoA"));
		boolean esitoB = Boolean.TRUE.equals((Boolean) getTemporary().get("esitoB"));
		boolean esitoC = Boolean.TRUE.equals((Boolean) getTemporary().get("esitoC"));
		boolean esitoD = Boolean.TRUE.equals((Boolean) getTemporary().get("esitoD"));
		
		Vocabularies vocabularies = VocabulariesImpl.instance();
		if(esitoA || esitoB || esitoC || esitoD){
			getTemporary().put("esitoSospensione", vocabularies.getCodeValueCsDomainCode("PHIDIC", "resultSosp", "rev"));
		}else{
			getTemporary().put("esitoSospensione", vocabularies.getCodeValueCsDomainCode("PHIDIC", "resultSosp", "ado"));
		}
	}
}