package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediAddebito;

@BypassInterceptors
@Name("SediAddebitoAction")
@Scope(ScopeType.CONVERSATION)
public class SediAddebitoAction extends BaseAction<SediAddebito, Long> {

	private static final long serialVersionUID = 510934599L;
	private static final Logger log = Logger.getLogger(SediAddebitoAction.class);

	public static SediAddebitoAction instance() {
		return (SediAddebitoAction) Component.getInstance(SediAddebitoAction.class, ScopeType.CONVERSATION);
	}

	public SediAddebito copy(SediAddebito toCopy){
		try{
			SediAddebito copy = new SediAddebito();
			copy.setCopy(true);
			copy.setIsActive(false);
			
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setCig(toCopy.getCig());
			copy.setClasseEconomica(toCopy.getClasseEconomica());
			copy.setCodContabilita(toCopy.getCodContabilita());
			copy.setCodiceVia(toCopy.getCodiceVia());
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setEsenzione(toCopy.getEsenzione());
			copy.setImpSpesa(toCopy.getImpSpesa());
			copy.setNote(toCopy.getNote());
			copy.setPrincipale(toCopy.getPrincipale());
			copy.setSettore(toCopy.getSettore());
			copy.setStato(toCopy.getStato());
			
			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());
			
			copy.setTipoAttivita(toCopy.getTipoAttivita());
			copy.setTipoUtente(toCopy.getTipoUtente());
			copy.setZona(toCopy.getZona());
			
			List<IndirizzoSped> indirizzi = toCopy.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				copy.setIndirizzoSped(new ArrayList<IndirizzoSped>());
				
				for (IndirizzoSped ind:indirizzi){
					copy.getIndirizzoSped().add(IndirizzoSpedAction.instance().copy(ind));
				}
			}

			
			//I0066939 
			copy.setPersonaGiuridica(PersoneGiuridicheAction.instance().copyForArpav(toCopy.getPersonaGiuridica()));
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public SediAddebito injectFromExisting (Sedi sede) {
		
		if (sede == null || sede.getPersonaGiuridica() == null) {
			log.error("impossibile agganciare sedi di addebito senza persona giuridica");
			return null;
		}
		
		SediAddebito sa = new SediAddebito();
		sa.setDenominazione(sede.getDenominazioneUnitaLocale());
		
		if (sede.getAddr()!=null)
			sa.setAddr(sede.getAddr().cloneAd());
		
		/*
		 * la sede ha solo una mail che viene salvata in telecom.mail, ma è la PEC
		 * quindi cloneTel non va bene del tutto
		 */
		if (sede.getTelecom()!=null){
			sa.setTelecom(sede.getTelecom().cloneTel());
			sa.getTelecom().setMc(sa.getTelecom().getMail());
			sa.getTelecom().setMail(null);
		}
		
		sa.setStato(sede.getStato());
		inject(sa);
		
//		sa.setPersonaGiuridica(sede.getPersonaGiuridica());
//		sede.getPersonaGiuridica().addSediAddebito(sa);
		
		return sa;
	}
	
	
	public void updatePrincipaleIfRemoved() {
		
		List<IndirizzoSped> indirizzi = getEntity().getIndirizzoSped();
		if (indirizzi == null || indirizzi.size() == 0){
			entity.setIndirizzoSpedPrinc(null);
			return;
		}
		
		IndirizzoSped principale = entity.getIndirizzoSpedPrinc();
		if (indirizzi.contains(principale)){
			return;
		}
		else {
			entity.setIndirizzoSpedPrinc(null);
		}
	}
	
	public Boolean isDeletable(SediAddebito sa) throws PhiException {
		if (sa!=null && sa.getIsActive()) {
			ImpiantoCheckAction imp = ImpiantoCheckAction.instance();
			imp.getEqual().put("sedeAddebito.internalId", sa.getInternalId());
			
			List<Impianto> impList = imp.list();
			imp.cleanRestrictions();

			if (impList != null && impList.size()>0) 
				return false;
		}

		return true;
	}

	
}