package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.dataTypes.CodeValueCity;

@BypassInterceptors
@Name("SediInstallazioneAction")
@Scope(ScopeType.CONVERSATION)
public class SediInstallazioneAction extends BaseAction<SediInstallazione, Long> {

	private static final long serialVersionUID = 510349294L;
    private static final Logger log = Logger.getLogger(SediInstallazioneAction.class);
	
	private static String ULSS = "SELECT o.name_giv, o.id FROM code_value_city cvc " +
			"LEFT JOIN organization o ON cvc.ulss = o.id " +
			"WHERE cvc.code=:code AND cvc.id LIKE 'comuni.%'";
	private static String FULL_LIST_FROM_PG = "SELECT si FROM SediInstallazione si " + 
			"INNER JOIN si.sede se WHERE se.personaGiuridica.internalId = :pgId AND se.isActive = true " + 
			"AND si.isActive = true AND (si.copy = false or si.copy is null)";
	private static String IMPIANTI = "SELECT imp FROM Impianto imp WHERE imp.sedeInstallazione.internalId = :siId";
	
	public static SediInstallazioneAction instance() {
		return (SediInstallazioneAction) Component.getInstance(SediInstallazioneAction.class, ScopeType.CONVERSATION);
	}

	public Boolean isDeletable(SediInstallazione si) throws PhiException {
		if (si!=null && si.getIsActive()) {
			/* Applico questa tecnica becera perché Impianto è un abstract e non mi viene in mente altro */
			
			ImpiantoCheckAction imp = ImpiantoCheckAction.instance();
			imp.getEqual().put("sedeInstallazione.internalId", si.getInternalId());
			
			List<Impianto> impList = imp.list();
			imp.cleanRestrictions();
			/*
			List<Impianto> impList = null;
			
			Query impianti = ca.createQuery(IMPIANTI);
			impianti.setParameter("siId", si.getInternalId());

			try {
				impList = (List<Impianto>) impianti.getResultList();
			} catch (NoResultException e) {
				//fullList = new ArrayList<SediInstallazione>();
			}*/
			
			if (impList != null && impList.size()>0) 
				return false;
		}

		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<SediInstallazione> getFullListFromPG(PersoneGiuridiche pg){

		//PhiDataModel<SediInstallazione> toInject = null;
		List<SediInstallazione> fullList = null;
		
		Query qOnlyInst = ca.createQuery(FULL_LIST_FROM_PG);
		qOnlyInst.setParameter("pgId", pg.getInternalId());

		try {
			fullList = (List<SediInstallazione>) qOnlyInst.getResultList();
		} catch (NoResultException e) {
			fullList = new ArrayList<SediInstallazione>();
		}
		
		//toInject = new PhiDataModel<SediInstallazione>(fullList, "SediInstallazioneList");
		
		return fullList;
	}
	
	public SediInstallazione copy(SediInstallazione toCopy){
		try{
			SediInstallazione copy = new SediInstallazione();
			copy.setCopy(true);
			copy.setSediInstallazioneOrig(toCopy);
			copy.setIsActive(false);
			
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setTipoSede(toCopy.getTipoSede());
			copy.setTipologiaSede(toCopy.getTipologiaSede());
			
			//this.create();

			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void copyFromSede(Sedi sede){
		try{
			SediInstallazione si = getEntity();
			
			if (sede!=null){
				//si.setSede(sede);
				si.setDenominazione(sede.getDenominazioneUnitaLocale());
				si.setAddr(sede.getAddr().cloneAd());
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getUlss(String code){
		String ret = "";
		
		try{
			if (code==null || "".equals(code))
				return ret;
			
			Query getUlss = ca.createNativeQuery(ULSS);

			getUlss.setParameter("code", code);
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultSet = getUlss.getResultList();
			
			if (resultSet.size()>0){
				Object[] ulss = resultSet.get(0);
				
				String ulssName = (String)ulss[0];
				String ulssCode = (String)ulss[1];
				
				if (ulssName!=null && !"".equals(ulssName))
					ret += ulssName;
				
				if (ulssCode!=null && !"".equals(ulssCode))
					ret += " (" + ulssCode + ")";
				
			}
				
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
			return ret;
		}	 
	}
	 /* Usa il codice del catalogo COMUNI per risalire allo stesso codice del catalogo COMUNIISTAT 
	  * Conversione necessaria per risalire alla regione dato il comune */
	public String getRegione(String istatComune){
		String ret = "";
		
		try{
			if (istatComune==null || "".equals(istatComune))
				return ret;
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			CodeValueCity cityInstat = (CodeValueCity)vocabularies.getCodeValueCsDomainCode("COMUNIISTAT", null, istatComune);
				
			if (cityInstat!=null && cityInstat.getParent()!=null && cityInstat.getParent().getParent()!=null)
				ret += cityInstat.getParent().getParent().getDisplayName();
						
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
			return ret;
		}	 
	}
	
	public void manageOldSiDeletable(SediInstallazione oldSi) throws PhiException{
		if(oldSi == null)
			return;
		
		oldSi.setDeletable(isDeletable(oldSi));
		
		create(oldSi);
	}
}