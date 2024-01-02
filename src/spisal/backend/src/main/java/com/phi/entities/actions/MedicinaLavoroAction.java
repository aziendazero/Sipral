package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.AnamnesisMdl;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.MdlsubProtocollo;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.ps.ProcessManagerImpl;

@BypassInterceptors
@Name("MedicinaLavoroAction")
@Scope(ScopeType.CONVERSATION)
public class MedicinaLavoroAction extends BaseAction<MedicinaLavoro, Long> {

	private static final long serialVersionUID = 428748426L;
    private static final Logger log = Logger.getLogger(MedicinaLavoroAction.class);


	public static MedicinaLavoroAction instance() {
		return (MedicinaLavoroAction) Component.getInstance(MedicinaLavoroAction.class, ScopeType.CONVERSATION);
	}
	
	public void copyAteco(PersoneGiuridiche p, Sedi s) throws PhiException{
		MedicinaLavoro mdl = getEntity();
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		mdl.setComparto(aAction.getImportantAteco(p, s));
	}
	
	private static final List<String> type1 =  Arrays.asList("14.1","14.2","14.13","14.14","7.10");
	private static final String type2 = "14.3";
	private static final String type3 = "6";
	private static final String type4 = "14.15";
	
	/**
	 * Return protocol subtype, also used by css to define icon
	 * @param protocollo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getType(String code) {
		if(code != null){
//			if (type1.contains(code)) {
//				return "01";
//			} else if(type2.equals(code)){
//				return "02";
//			} else if(type3.equals(code)){
//				return "03";
//			} else if(type4.equals(code)){
//				return "04";
//			}
			String hqlMdlSub = "SELECT m.codiceSottotipo FROM MdlsubProtocollo m WHERE m.codiceProtocollo = :code";
			Query qMdlSub = ca.createQuery(hqlMdlSub);
			qMdlSub.setParameter("code", code);
			List<String> result = (List<String>) qMdlSub.getResultList();
			if(result!=null && !result.isEmpty()){
				return result.get(0);
			}
		}
		return null;
	}

	public void presetData() throws DictionaryException, PhiException{
		MedicinaLavoro m = getEntity();
		
		if(!ca.contains(m)){
			ProcpraticheAction pratAction = ProcpraticheAction.instance();
			Procpratiche prat = pratAction.getEntity();
			if(prat!=null){
				Protocollo prot = null;
				if(prat.getProtocollo()!=null && !prat.getProtocollo().isEmpty()){
					for(Protocollo p : prat.getProtocollo()){
						if(p.getIsMaster()){
							prot = p;
							break;
						}
					}
				}
				if(prot!=null){
					if(prot.getRiferimento()!=null && "Utente".equals(prot.getRiferimento().getCode())){
						m.setPatient(prot.getRiferimentoUtente());
					}
					if(prot.getRichiedente()!=null && "Ditta".equals(prot.getRichiedente().getCode())){
						m.setDittaAttuale(prot.getRichiedenteDitta());
						m.setSedeAttuale(prot.getRichiedenteSede());
					}
					//SE VALORIZZATA, LA DITTA ATTUALE UTENTE NELLA COMUNICAZIONE HA LA PRECEDENZA (M.Canciani 23/01/2017)
					if(prot.getDittaAttualeUtente()!=null){
						m.setDittaAttuale(prot.getDittaAttualeUtente());
						m.setSedeAttuale(prot.getSedeAttualeUtente());
					}
					
					if(prot.getCode() != null) {
						setCodeValue("type", "PHIDIC", "MdlType", getType(prot.getCode().getCode()));
						if(m.getType()!=null && "03".equals(m.getType().getCode())){
							m.setRicorsoDa(prot.getRicorsoDa());
						}
					}
					
					if(prot.getMedicinaLavoro()!=null){
						MedicinaLavoro protMdl = prot.getMedicinaLavoro();
						m.setDiagCode(protMdl.getDiagCode());
						m.setDiagText(protMdl.getDiagText());
						m.setSedeText(protMdl.getSedeText());
						m.setGravita(protMdl.getGravita());
					}

				}
			
				/*
				 * I0053668
				 */
				if(m.getPatient()!=null && prat.getServiceDeliveryLocation()!=null && m.getType()!=null){
					String mpi = m.getPatient().getMpi();
					if(mpi==null || mpi.isEmpty())
						mpi="-1";
					String cf = m.getPatient().getFiscalCode();
					if(cf==null || cf.isEmpty())
						cf="-1";
					
					String hqlPat = "SELECT m.anamnesiPatologica, prat.numero " +
							"FROM MedicinaLavoro m JOIN m.procpratiche prat JOIN prat.serviceDeliveryLocation s JOIN m.patient pers " +
							"WHERE s.internalId=:sdlocId AND m.type=:mdlType AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) AND m.anamnesiPatologica IS NOT NULL " +
							"ORDER BY prat.data DESC";
					
					String hqlFis = "SELECT m.anamnesiFisiologica, prat.numero " +
							"FROM MedicinaLavoro m JOIN m.procpratiche prat JOIN prat.serviceDeliveryLocation s JOIN m.patient pers " +
							"WHERE s.internalId=:sdlocId AND m.type=:mdlType AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) AND m.anamnesiFisiologica IS NOT NULL " +
							"ORDER BY prat.data DESC";
					
					String hqlMdl = "SELECT m.internalId, prat.numero " +
							"FROM MedicinaLavoro m JOIN m.procpratiche prat JOIN prat.serviceDeliveryLocation s JOIN m.patient pers JOIN m.anamnesisMdl a " +
							"WHERE s.internalId=:sdlocId AND m.type=:mdlType AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) " +
							"ORDER BY prat.data DESC";
					
					Query qPat = ca.createQuery(hqlPat);
					qPat.setParameter("sdlocId", prat.getServiceDeliveryLocation().getInternalId());
					qPat.setParameter("mdlType", m.getType());
					qPat.setParameter("mpi", mpi);
					qPat.setParameter("cf", cf);
					List<Object[]> aPatList = (List<Object[]>) qPat.setMaxResults(1).getResultList();
					if(aPatList!=null && !aPatList.isEmpty()){
						Object[] aPat = aPatList.get(0);
						m.setAnamnesiPatologica((String) aPat[0]);
						m.setApatPratNumber((String) aPat[1]);
					}
					
					Query qFis = ca.createQuery(hqlFis);
					qFis.setParameter("sdlocId", prat.getServiceDeliveryLocation().getInternalId());
					qFis.setParameter("mdlType", m.getType());
					qFis.setParameter("mpi", mpi);
					qFis.setParameter("cf", cf);
					List<Object[]> aFisList = (List<Object[]>) qFis.setMaxResults(1).getResultList();
					if(aFisList!=null && !aFisList.isEmpty()){
						Object[] aFis = aFisList.get(0);
						m.setAnamnesiFisiologica((String) aFis[0]);
						m.setAfisPratNumber((String) aFis[1]);
					}
					
					Query qMdl = ca.createQuery(hqlMdl);
					qMdl.setParameter("sdlocId", prat.getServiceDeliveryLocation().getInternalId());
					qMdl.setParameter("mdlType", m.getType());
					qMdl.setParameter("mpi", mpi);
					qMdl.setParameter("cf", cf);
					List<Object[]> aMdlList = (List<Object[]>) qMdl.setMaxResults(1).getResultList();
					if(aMdlList!=null && !aMdlList.isEmpty()){
						Object[] aMdl = aMdlList.get(0);
						MedicinaLavoro oldMdl = ca.get(entityClass, (Long) aMdl[0]);
						if(oldMdl!=null && oldMdl.getAnamnesisMdl()!=null && !oldMdl.getAnamnesisMdl().isEmpty()){
							AnamnesisMdlAction aAction = AnamnesisMdlAction.instance();
							List<AnamnesisMdl> newAnamneses = new ArrayList<AnamnesisMdl>();
							for(AnamnesisMdl oldAn : oldMdl.getAnamnesisMdl()){
								AnamnesisMdl newAn = aAction.copy(oldAn);
								if(newAn!=null){
									newAn.setMedicinaLavoro(m);
									newAnamneses.add(newAn);
								}
							}
							m.setAnamnesisMdl(newAnamneses);
							m.setAmdlPratNumber((String) aMdl[1]);
						}
					}
				}
			}
		}
	}
	
	public void checkProtocollo(Protocollo prot){
		try{
			//Controlla che la comunicazione oggetto di modifica sia la comunicazione principale
			if (prot==null || !prot.getIsMaster())
				return;
			
			//Controlla che la comunicazione sia di tipo SUPERVISION (Vigilanza Spisal)
			ServiceDeliveryLocation uos = prot.getUos();
			if (uos == null)
				return;
					
			CodeValue area = uos.getArea();
			if (area == null || !"WORKMEDICINE".equals(area.getCode()))
				return;
			
			//Controlla che la comunicazione oggetto di modifica sia in stato assegnata (completed)
			if (prot.getStatusCode()==null || prot.getStatusCode().getCode()==null || !"completed".equals(prot.getStatusCode().getCode()))
				return;
			
			//Aggiorna i riferimenti come this.presetData()
			Procpratiche prat = prot.getProcpratiche();
			if (prat==null)
				return;
			
			MedicinaLavoro m = prat.getMedicinaLavoro();
			if (m==null)
				return;
			
			if(prot!=null){
				if(prot.getRiferimento()!=null && "Utente".equals(prot.getRiferimento().getCode())){
					m.setPatient(prot.getRiferimentoUtente());
				}
				if(prot.getRichiedente()!=null && "Ditta".equals(prot.getRichiedente().getCode())){
					m.setDittaAttuale(prot.getRichiedenteDitta());
					m.setSedeAttuale(prot.getRichiedenteSede());
				}
				//SE VALORIZZATA, LA DITTA ATTUALE UTENTE NELLA COMUNICAZIONE HA LA PRECEDENZA (M.Canciani 23/01/2017)
				if(prot.getDittaAttualeUtente()!=null){
					m.setDittaAttuale(prot.getDittaAttualeUtente());
					m.setSedeAttuale(prot.getSedeAttualeUtente());
				}
				
				if(prot.getCode() != null) {
					setCodeValue("type", "PHIDIC", "MdlType", getType(prot.getCode().getCode()));
				}
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	

	public void changeSubType() throws DictionaryException, PhiException{
		MedicinaLavoro m = getEntity();
		ProcpraticheAction pAction = ProcpraticheAction.instance();
		Procpratiche p = pAction.getEntity();
		
		Person pat = m.getPatient();
		PersoneGiuridiche az = m.getDittaAttuale();
		
		if(m!=null && p!=null && m.getType()!=null){
			CodeValue type = m.getType();
			MedicinaLavoro newMed = new MedicinaLavoro();
			inject(newMed);
			
			setCodeValue("type", "PHIDIC", "MdlType", type.getCode());
			m.setProcpratiche(null);
			newMed.setPatient(pat);
			newMed.setDittaAttuale(az);
			p.setMedicinaLavoro(newMed);
			
			/* I0053668 - Per le pratiche di Medicina del Lavoro, all'atto della selezione della sottotipologia, vengono cercate pratiche dello stesso Tipo e Sottotipo 
			 * per il paziente selezionato. Qualora si trovi riscontro (una o più pratiche già inserite, che siano aperte o concluse) vanno ricopiati i dati relativi a:
			 * 
			 * a. Anamnesi patologica remota
			 * b. Anamnesi fisiologica e familiare
			 * c. Anamnesi lavorativa
			 * 
			 * I dati ricopiati saranno modificabili in modo che l'operatore possa controllare, confermare o integrare il dato proposto. 
			 * Non è necessario che siano riproposti anche i dati relativi all'anamnesi prossima. */
			if(m.getPatient()!=null && p.getServiceDeliveryLocation()!=null && m.getType()!=null){
				
				String mpi = m.getPatient().getMpi();
				if(mpi==null || mpi.isEmpty())
					mpi="-1";
				
				String cf = m.getPatient().getFiscalCode();
				if(cf==null || cf.isEmpty())
					cf="-1";
				
				/* Recupero Anamnesi patologica remota */
				String hqlPat = "SELECT m.anamnesiPatologica, prat.numero " +
						"FROM MedicinaLavoro m " +
						"JOIN m.procpratiche prat " +
						"JOIN prat.serviceDeliveryLocation s " +
						"JOIN m.patient pers " +
						"WHERE s.internalId=:sdlocId " +
						"AND m.type=:mdlType " +
						"AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) " +
						"AND m.anamnesiPatologica IS NOT NULL " +
						"AND prat.internalId!=:idPrat " +
						"ORDER BY prat.data DESC";
				
				Query qPat = ca.createQuery(hqlPat);
				qPat.setParameter("sdlocId", p.getServiceDeliveryLocation().getInternalId());
				qPat.setParameter("mdlType", m.getType());
				qPat.setParameter("mpi", mpi);
				qPat.setParameter("cf", cf);
				qPat.setParameter("idPrat", p.getInternalId());
				List<Object[]> aPatList = (List<Object[]>) qPat.setMaxResults(1).getResultList();
				if(aPatList!=null && !aPatList.isEmpty()){
					Object[] aPat = aPatList.get(0);
					newMed.setAnamnesiPatologica((String) aPat[0]);
					newMed.setApatPratNumber((String) aPat[1]);
				}
				
				/* Recupero Anamnesi fisiologica e familiare */
				String hqlFis = "SELECT m.anamnesiFisiologica, prat.numero " +
						"FROM MedicinaLavoro m " +
						"JOIN m.procpratiche prat " +
						"JOIN prat.serviceDeliveryLocation s " +
						"JOIN m.patient pers " +
						"WHERE s.internalId=:sdlocId " +
						"AND m.type=:mdlType " +
						"AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) " +
						"AND m.anamnesiFisiologica IS NOT NULL " +
						"AND prat.internalId!=:idPrat " +
						"ORDER BY prat.data DESC";
				
				Query qFis = ca.createQuery(hqlFis);
				qFis.setParameter("sdlocId", p.getServiceDeliveryLocation().getInternalId());
				qFis.setParameter("mdlType", m.getType());
				qFis.setParameter("mpi", mpi);
				qFis.setParameter("cf", cf);
				qFis.setParameter("idPrat", p.getInternalId());
				List<Object[]> aFisList = (List<Object[]>) qFis.setMaxResults(1).getResultList();
				if(aFisList!=null && !aFisList.isEmpty()){
					Object[] aFis = aFisList.get(0);
					newMed.setAnamnesiFisiologica((String) aFis[0]);
					newMed.setAfisPratNumber((String) aFis[1]);
				}
				
				/* Recupero Anamnesi lavorativa */
				String hqlMdl = "SELECT m.internalId, prat.numero " +
						"FROM MedicinaLavoro m " +
						"JOIN m.procpratiche prat " +
						"JOIN prat.serviceDeliveryLocation s " +
						"JOIN m.patient pers JOIN m.anamnesisMdl a " +
						"WHERE s.internalId=:sdlocId " +
						"AND m.type=:mdlType " +
						"AND (pers.mpi=:mpi OR pers.fiscalCode=:cf) " +
						"AND prat.internalId!=:idPrat " +
						"ORDER BY prat.data DESC";
				
				Query qMdl = ca.createQuery(hqlMdl);
				qMdl.setParameter("sdlocId", p.getServiceDeliveryLocation().getInternalId());
				qMdl.setParameter("mdlType", m.getType());
				qMdl.setParameter("mpi", mpi);
				qMdl.setParameter("cf", cf);
				qMdl.setParameter("idPrat", p.getInternalId());
				List<Object[]> aMdlList = (List<Object[]>) qMdl.setMaxResults(1).getResultList();
				if(aMdlList!=null && !aMdlList.isEmpty()){
					Object[] aMdl = aMdlList.get(0);
					MedicinaLavoro oldMdl = ca.get(entityClass, (Long) aMdl[0]);
					
					if(oldMdl!=null && oldMdl.getAnamnesisMdl()!=null && !oldMdl.getAnamnesisMdl().isEmpty()){
						AnamnesisMdlAction aAction = AnamnesisMdlAction.instance();
						
						List<AnamnesisMdl> newAnamneses = new ArrayList<AnamnesisMdl>();
						for(AnamnesisMdl oldAn : oldMdl.getAnamnesisMdl()){
							AnamnesisMdl newAn = aAction.copy(oldAn);
							if(newAn!=null){
								newAn.setMedicinaLavoro(newMed);
								newAnamneses.add(newAn);
							}
						}
						
						newMed.setAnamnesisMdl(newAnamneses);
						newMed.setAmdlPratNumber((String) aMdl[1]);
					}
				}
			}
		}
		
	}

	public Double sumCVM(List<AnamnesisMdl> lst){
		Double rtn = 0D;
		if(lst!=null){
			for(AnamnesisMdl a : lst){
				if(a!=null && a.getExpCVM()!=null)
					rtn+=a.getExpCVM();
			}
		}
		return rtn;
	}
	
	public Double sumAmi(List<AnamnesisMdl> lst){
		Double rtn = 0D;
		if(lst!=null){
			for(AnamnesisMdl a : lst){
				if(a!=null && a.getExpAmi()!=null)
					rtn+=a.getExpAmi();
			}
		}
		return rtn;
	}
	
	/**
	 * per le pratiche di medicina del lavoro, se imposto il tipo "sopralluogo" per l'attivita
	 * devo prevalorizzare il luogo con la ditta attuale del paziente
	 * @throws DictionaryException 
	 * @throws PersistenceException 
	 */
	public void updateLuogo() throws PersistenceException, DictionaryException{
		Attivita attivita = AttivitaAction.instance().getEntity();
		Procpratiche pratica = ProcpraticheAction.instance().getEntity();
		MedicinaLavoro medLavoro = null;
		if(pratica!=null)
			medLavoro=pratica.getMedicinaLavoro();
		if(medLavoro!=null && attivita!=null && attivita.getCode()!=null && "sopralluogo".equals(attivita.getCode().getCode())){
			
			if(pratica.getServiceDeliveryLocation()!=null && pratica.getServiceDeliveryLocation().getArea()!=null 
					&& "WORKMEDICINE".equals(pratica.getServiceDeliveryLocation().getArea().getCode())
					&& medLavoro.getType()!=null && "03".equals(medLavoro.getType().getCode())){
				
				Vocabularies vocabularies = VocabulariesImpl.instance();
				attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
				attivita.setLuogoDitta(medLavoro.getDittaAttuale());
				attivita.setLuogoSede(medLavoro.getSedeAttuale());
				if(medLavoro.getSedeAttuale()!=null && medLavoro.getSedeAttuale().getAddr()!=null){
					attivita.setAddr(medLavoro.getSedeAttuale().getAddr().cloneAd());
				
				}else{
					attivita.setAddr(null);
				}
			}
		}
		try {
			ProcessManagerImpl.instance().manageTask("initSopralluogo;initSopralluogo");
		} catch (PhiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}