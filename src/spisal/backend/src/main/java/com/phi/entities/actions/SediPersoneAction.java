package com.phi.entities.actions;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SpisalAddr;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;

@BypassInterceptors
@Name("SediPersoneAction")
@Scope(ScopeType.CONVERSATION)
public class SediPersoneAction extends BaseAction<SediPersone, Long> {

	private static final long serialVersionUID = 1164311943L;

	public static SediPersoneAction instance() {
		return (SediPersoneAction) Component.getInstance(SediPersoneAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Questo metodo gestisce la PERSONA FISICA selezionata in gestione dipendenti
	 * 1) Se la Person è null: non ho selezionato nulla da sottoprocesso, quindi non devo fare nulla.
	 * 2) Se la Person non è null...
	 * 		2a)...e c'è una SediPersone con lo stesso codice fiscale della Person sulla stessa sede (SediPersone.cariche.sede = Sedi in conversation) 
	 * 				allora recupero quella e aggiorno SediPersone sulla base di Person
	 * 		2b)...e non c'è una SediPersone con lo stesso codice fiscale della Person sulla stessa sede, allora ne creo una nuova e la riempio con Person 
	 * 3) Il link tra SediPersone e Person lo tiro solo alla fine, quando salvo..
	 */
	public void managePerson(Person pers){
		if(pers==null)
			return;
		
		Long sedeId = null;
		String cf = pers.getFiscalCode();
		SediAction sediAction = SediAction.instance();
		Sedi sede = sediAction.getEntity();
		
		if(sede!=null)
			sedeId=sede.getInternalId();
		
		//questo non dovrebbe mai verificarsi...
		if(sedeId==null || cf==null || cf.isEmpty())
			return;
		
		String hql = "SELECT sediPers FROM SediPersone sediPers " +
				"JOIN sediPers.cariche carica " +
				"JOIN carica.sede sede " +
				"WHERE sede.internalId = :sedeId AND sediPers.fiscalCode = :cf " +
				"AND sediPers.isActive = 1 AND carica.isActive = 1";
		
		Query q = ca.createQuery(hql);
		q.setParameter("sedeId", sedeId);
		q.setParameter("cf", cf);
		List<SediPersone> result = (List<SediPersone>)q.getResultList();
		if(result!=null && !result.isEmpty()){
			/*
			 * dovrebbe essercene sempre solo uno...ma per garantirlo dovrei aggiungere un controllo sul
			 * salvataggio di SediPersone...
			 */
			SediPersone first = result.get(0);
			if(first!=null){
				//aggiorno i dati del lavoratore coi dati in anagrafica
				writeOver(pers,first);
				
				//rimpiazzo la SediPersone in conversation con quella che ho trovato, revertando eventuali modifiche
				SediPersone current = getEntity();
				if(current.getInternalId() != first.getInternalId()){
					if(current.getInternalId()!=0){
						ca.refresh(current);
					}else{
						ca.evict(current);
					}
					inject(first);
				}
			}
		
		}else{
			SediPersone current = getEntity();
			writeOver(pers, current);
		}
	}
	
	public void managePersonSimple(Person pers){
		if(pers==null)
			return;
		
		Long sedeId = null;
		String cf = pers.getFiscalCode();
		SediAction sediAction = SediAction.instance();
		Sedi sede = sediAction.getEntity();
		
		if(sede!=null)
			sedeId=sede.getInternalId();
		
		if(cf!=null && !cf.isEmpty()){
			String hql = "SELECT sediPers FROM SediPersone sediPers " +
					"JOIN sediPers.cariche carica " +
					"JOIN carica.sede sede " +
					"WHERE sede.internalId = :sedeId AND sediPers.fiscalCode = :cf " +
					"AND sediPers.isActive = 1 AND carica.isActive = 1";
		
			Query q = ca.createQuery(hql);
			q.setParameter("sedeId", sedeId);
			q.setParameter("cf", cf);
			List<SediPersone> result = (List<SediPersone>)q.getResultList();
			if(result!=null && !result.isEmpty()){
				/*
				 * dovrebbe essercene sempre solo uno...ma per garantirlo dovrei aggiungere un controllo sul
				 * salvataggio di SediPersone...
				 */
				SediPersone first = result.get(0);
				if(first!=null){
					//aggiorno i dati del lavoratore coi dati in anagrafica
					writeOver(pers,first);
				
					//rimpiazzo la SediPersone in conversation con quella che ho trovato, revertando eventuali modifiche
					SediPersone current = getEntity();
					if(current.getInternalId() != first.getInternalId()){
						if(current.getInternalId()!=0){
							ca.refresh(current);
						}else{
							ca.evict(current);
						}
						inject(first);
					}
				}
			}else {
				SediPersone current = getEntity();
				writeOver(pers, current);
			}	
		} 
	}
	
	/**
	 * Questo metodo scrive sull'entità lav, tutto quello che sta in pers
	 * @param pers
	 * @param lav
	 */
	@SuppressWarnings("unchecked")
	public void writeOver(Person pers, SediPersone lav){		
		if(lav==null || pers==null)
			return;
		
		/*
		 * TODO: vanno copiati??cosa codificano??
		 * master : Person
		 * physician : Physician
		 * professionalSituation : SituazioneProfessionale
		 */
		
		if(pers.getAddr()!=null)
			lav.setAddr(pers.getAddr().cloneAd());

		if(pers.getBirthPlace()!=null)
			lav.setBirthPlace(pers.getBirthPlace().cloneAd());
		
		if(pers.getDomicileAddr()!=null)
			lav.setDomicileAddr(pers.getDomicileAddr().cloneAd());
		
		if(pers.getName()!=null)
			lav.setName(pers.getName().cloneEN());
		
		if(pers.getTelecom()!=null)
			lav.setTelecom(pers.getTelecom().cloneTel());

		if(pers.getCsDate()!=null)
			lav.setCsDate(pers.getCsDate().cloneIVL());
		
		if(pers.getEniDate()!=null)
			lav.setEniDate(pers.getEniDate().cloneIVL());
		
		if(pers.getStpDate()!=null)
			lav.setStpDate(pers.getStpDate().cloneIVL());
		
		if(pers.getTeamDate()!=null)
			lav.setTeamDate(pers.getTeamDate().cloneIVL());
		
		/*if(pers.getAlternativeAddr()!=null){
			SpisalAddr lavAlternativeAddr = pers.getAlternativeAddr().cloneAddr();
			lav.setAlternativeAddr(lavAlternativeAddr);
		}*/
			
		if(pers.getCurrentOrg()!=null)
			lav.setCurrentOrg(pers.getCurrentOrg());
		
		if(pers.getOriginalOrg()!=null)
			lav.setOriginalOrg(pers.getOriginalOrg());
		
		lav.setBirthTime(pers.getBirthTime());
		lav.setCategory(pers.getCategory());
		lav.setCitizen(pers.getCitizen());
		lav.setCode(pers.getCode());
		lav.setCountryOfAddr(pers.getCountryOfAddr());
		lav.setCountryOfBirth(pers.getCountryOfBirth());
		lav.setCountryOfDomicile(pers.getCountryOfDomicile());
		lav.setCs(pers.getCs());
		lav.setCsRegion(pers.getCsRegion());
		lav.setDeathDate(pers.getDeathDate());
		lav.setDeathIndicator(pers.getDeathIndicator());
		lav.setEni(pers.getEni());
		lav.setFiscalCode(pers.getFiscalCode());
		lav.setGenderCode(pers.getGenderCode());
		lav.setHL7MsgDate(pers.getHL7MsgDate());
		lav.setMaritalStatusCode(pers.getMaritalStatusCode());
		lav.setMpi(pers.getMpi());
		lav.setReliability(pers.getReliability());
		//lav.setRev(pers.getRev());
		lav.setStp(pers.getStp());
		lav.setTeamCode(pers.getTeamCode());
		lav.setTeamIdent(pers.getTeamIdent());
		lav.setTeamInst(pers.getTeamInst());
		lav.setTeamPers(pers.getTeamPers());
		lav.setToUpdate(pers.getToUpdate());
		
		lav.setPerson(pers);
	}
	
	/**
	 * Questo metodo crea un'entity Person con i dati dell'entity SediPersone
	 * @param sp
	 */
	@SuppressWarnings("unchecked")
	public Person getPerson(SediPersone sp){		
		if(sp==null)
			return null;
		
		Person p = new Person();
		
		if(sp.getAddr()!=null)
			p.setAddr(sp.getAddr().cloneAd());

		if(sp.getBirthPlace()!=null)
			p.setBirthPlace(sp.getBirthPlace().cloneAd());
		
		if(sp.getDomicileAddr()!=null)
			p.setDomicileAddr(sp.getDomicileAddr().cloneAd());
		
		if(sp.getName()!=null)
			p.setName(sp.getName().cloneEN());
		
		if(sp.getTelecom()!=null)
			p.setTelecom(sp.getTelecom().cloneTel());

		if(sp.getCsDate()!=null)
			p.setCsDate(sp.getCsDate().cloneIVL());
		
		if(sp.getEniDate()!=null)
			p.setEniDate(sp.getEniDate().cloneIVL());
		
		if(sp.getStpDate()!=null)
			p.setStpDate(sp.getStpDate().cloneIVL());
		
		if(sp.getTeamDate()!=null)
			p.setTeamDate(sp.getTeamDate().cloneIVL());
		
		if(sp.getAlternativeAddr()!=null){
			SpisalAddr lavAlternativeAddr = sp.getAlternativeAddr().cloneAddr();
			p.setAlternativeAddr(lavAlternativeAddr);
		}
			
		if(sp.getCurrentOrg()!=null)
			p.setCurrentOrg(sp.getCurrentOrg());
		
		if(sp.getOriginalOrg()!=null)
			p.setOriginalOrg(sp.getOriginalOrg());
		
		p.setBirthTime(sp.getBirthTime());
		p.setCategory(sp.getCategory());
		p.setCitizen(sp.getCitizen());
		p.setCode(sp.getCode());
		p.setCountryOfAddr(sp.getCountryOfAddr());
		p.setCountryOfBirth(sp.getCountryOfBirth());
		p.setCountryOfDomicile(sp.getCountryOfDomicile());
		p.setCs(sp.getCs());
		p.setCsRegion(sp.getCsRegion());
		p.setDeathDate(sp.getDeathDate());
		p.setDeathIndicator(sp.getDeathIndicator());
		p.setEni(sp.getEni());
		p.setFiscalCode(sp.getFiscalCode());
		p.setGenderCode(sp.getGenderCode());
		p.setHL7MsgDate(sp.getHL7MsgDate());
		p.setMaritalStatusCode(sp.getMaritalStatusCode());
		p.setMpi(sp.getMpi());
		p.setReliability(sp.getReliability());
		p.setStp(sp.getStp());
		p.setTeamCode(sp.getTeamCode());
		p.setTeamIdent(sp.getTeamIdent());
		p.setTeamInst(sp.getTeamInst());
		p.setTeamPers(sp.getTeamPers());
		p.setToUpdate(sp.getToUpdate());
		
		return p;
	}
	
}