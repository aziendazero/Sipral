package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueAteco;

@BypassInterceptors
@Name("PersonaGiuridicaSedeAction")
@Scope(ScopeType.CONVERSATION)
public class PersonaGiuridicaSedeAction extends BaseAction<PersonaGiuridicaSede, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4813160218002586578L;
	private static final Logger log = Logger.getLogger(PersonaGiuridicaSedeAction.class);
	
	public static PersonaGiuridicaSedeAction instance() {
		return (PersonaGiuridicaSedeAction) Component.getInstance(PersonaGiuridicaSedeAction.class, ScopeType.CONVERSATION);
	}

	public String getName(String type, List<PersonaGiuridicaSede> lst){
		if(lst!=null && !lst.isEmpty() && type!=null){
			for(PersonaGiuridicaSede p : lst){
				if(p.getType()!=null && type.equals(p.getType().getCode())){
					if(p.getSede()!=null){
						return p.getSede().getDenominazioneUnitaLocale();
					}else if(p.getPersonaGiuridica()!=null){
						return p.getPersonaGiuridica().getDenominazione();
					}
				}
			}
		}
		
		return "";
	}
	
	public AD getAddr(String type, List<PersonaGiuridicaSede> lst){
		if(lst!=null && !lst.isEmpty() && type!=null){
			for(PersonaGiuridicaSede p : lst){
				if(p.getType()!=null && type.equals(p.getType().getCode())){
					if(p.getSede()!=null){
						return p.getSede().getAddr();
					}else if(p.getPersonaGiuridica()!=null && p.getPersonaGiuridica().getSedi()!=null){
						for(Sedi s : p.getPersonaGiuridica().getSedi()){
							if(s.getSedePrincipale()){
								return s.getAddr();
							}
						}
					}
				}
			}
		}
		
		return new AD();
	}
	
	/**
	 * Usato in virtual page vigilanza (gestione pratiche)
	 * @return Descrizione e relativo comparto dell'attività ateco <em>principale</em> della sede o (in mancanza) della ditta
	 * @param pgs
	 * @return
	 */
	public String getAtecoAsString(PersonaGiuridicaSede pgs){
		return this.getAtecoAsString(pgs, false, false);
	}
	
	/**
	 * Usato in virtual page vigilanza (gestione pratiche)
	 * @return Descrizione, relativo comparto (con codice) dell'attività ateco <em>principale</em> della sede o (in mancanza) della ditta
	 * @param pgs
	 * @return
	 */
	public String getAtecoWithCodeAsString(PersonaGiuridicaSede pgs) {
		return this.getAtecoAsString(pgs, false, true);
	}
	
	public String getAtecoWithSpecAsString(PersonaGiuridicaSede pgs) {
		return this.getAtecoAsString(pgs, true, false);
	}

	private String getAtecoAsString(PersonaGiuridicaSede pgs, boolean withSpec, boolean withCode){
		if (pgs == null) 
			return "";
		try{
			StringBuilder sb = new StringBuilder();
			CodeValueAteco ateco = AttivitaIstatAction.instance().getImportantAteco(pgs.getPersonaGiuridica(), pgs.getSede());
			if(ateco!=null){
				sb.append(ateco.getDescription()).append(" (Comparto: ").append(CompartoAtecoAction.instance().getComparto(ateco));
				
				if(withSpec) {
					sb.append(", Specificazione: ").append(CompartoAtecoAction.instance().getSpecificazione(ateco));
				}
				
				if(withCode) {
					sb.append(", codice: ").append(ateco.getCode());
				}
				sb.append(")");
			}
			
			return sb.toString();
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

}