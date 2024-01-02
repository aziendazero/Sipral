package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

@BypassInterceptors
@Name("SoggettoAction")
@Scope(ScopeType.CONVERSATION)
public class SoggettoAction extends BaseAction<Soggetto, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5002956161559858644L;
    private static final Logger log = Logger.getLogger(SoggettoAction.class);
    
    public static SoggettoAction instance() {
        return (SoggettoAction) Component.getInstance(SoggettoAction.class, ScopeType.CONVERSATION);
    }
	
    public String getDetails(Soggetto soggetto) {
		try{
			if (soggetto==null)
				return "";
			
			if (soggetto instanceof HibernateProxy) { //de proxy
				soggetto = (((Soggetto)((HibernateProxy)soggetto).getHibernateLazyInitializer().getImplementation()));
			}
			
			CodeValue cv = soggetto.getCode();
			
			if (cv == null)
				return "";
			
			String ret = "";
			

			String code = cv.getCode();
			
			if (code.equals("Ditta")){
				PersoneGiuridiche pg = soggetto.getDitta();
				Sedi sede = soggetto.getSede();
				
				if (pg != null && pg.getDenominazione() !=null)
					ret = pg.getDenominazione().toUpperCase();
				
				if (sede != null && sede.getDenominazioneUnitaLocale()!=null)
					ret += " (Sede: " + sede.getDenominazioneUnitaLocale().toUpperCase() + ")";
				
			} else if (code.equals("Utente")){
				Person person = soggetto.getUtente();
				
				if (person != null && person.getName()!=null && person.getName().getFam()!=null && person.getName().getGiv()!=null){
					ret = person.getName().getFam().toUpperCase() + " " + person.getName().getGiv().toUpperCase();
					
					if (person.getFiscalCode()!=null)
						ret += " (CF:" + person.getFiscalCode().toUpperCase() + ")";
				}
			} else if (code.equals("Medico")){
				Physician medico = soggetto.getMedico();
				
				if (medico != null && medico.getName()!=null && medico.getName().getFam()!=null && medico.getName().getGiv()!=null){
					ret = medico.getName().getFam().toUpperCase() + " " + medico.getName().getGiv().toUpperCase();
					
					if (medico.getRegionalCode()!=null)
						ret += " (Cod. Reg.:" + medico.getRegionalCode().toUpperCase() + ")";
				}
			} else if (code.equals("Cantiere")){
				Cantiere cantiere = soggetto.getCantiere();
				
				if (cantiere != null && cantiere.getTipologiaNotifica()!=null)
					ret = cantiere.getTipologiaNotifica().toString();
						
			} else if (code.equals("Altro")){
				ret = soggetto.getRiferimentoDenominazione();				
			
			}
			
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}
	
	public Soggetto copy(Soggetto toCopy){
		try{
			Soggetto soggetto = new Soggetto();
			
			//soggetto.setAttivita(toCopy.getAttivita());
			
			soggetto.setDitta(toCopy.getDitta());
			soggetto.setSede(toCopy.getSede());
			soggetto.setUtente(toCopy.getUtente());
			soggetto.setCantiere(toCopy.getCantiere());

			if (toCopy.getAddr()!=null)
				soggetto.setAddr(toCopy.getAddr().cloneAd());
			
			soggetto.setCode(toCopy.getCode());
			soggetto.setCodiceIMO(toCopy.getCodiceIMO());
			
			soggetto.setRiferimentoDenominazione(toCopy.getRiferimentoDenominazione());
			soggetto.setRiferimentoEntita(toCopy.getRiferimentoEntita());
			soggetto.setRuolo(toCopy.getRuolo());
			soggetto.setTarga(toCopy.getTarga());
			soggetto.setTipoCantiere(toCopy.getTipoCantiere());
			soggetto.setTipoDitta(toCopy.getTipoDitta());
			
			return soggetto;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void removeFromProvvedimenti(Soggetto soggetto){
		try{
			List<Provvedimenti> provvedimenti = soggetto.getProvvedimenti();
			if (provvedimenti != null) {
				for (Provvedimenti provvedimento : provvedimenti) {
					if (provvedimento.getSoggetto()==soggetto) {
						provvedimento.setSoggetto(null);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}