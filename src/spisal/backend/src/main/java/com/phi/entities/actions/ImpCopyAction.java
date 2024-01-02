package com.phi.entities.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.CessioneImp;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.SchedaGeneratori;
import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;
import com.phi.entities.baseEntity.SchedaRecipientiIndiv;
import com.phi.entities.baseEntity.SchedaTubazioniIndiv;
import com.phi.entities.baseEntity.SchedaVasi;
import com.phi.entities.baseEntity.Sedi;
//import com.phi.entities.baseEntity.SediAddebito;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.actions.BaseAction;

import org.hibernate.Query;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.apache.log4j.Logger;


@BypassInterceptors
@Name("ImpCopyAction")
@Scope(ScopeType.CONVERSATION)
public class ImpCopyAction extends BaseAction<Impianto, Long> {

	private static final long serialVersionUID = 4827431288492297210L;
	private static final Logger log = Logger.getLogger(ImpCopyAction.class);


	public static ImpCopyAction instance() {
		return (ImpCopyAction) Component.getInstance(ImpCopyAction.class, ScopeType.CONVERSATION);
	}

	public ImpCopyAction() {
		super();
		conversationName = "ImpCopy";
	}

	public void updateLinkSchede(Impianto orig, Impianto copy){

		if(orig == null || copy == null)
			return;

		try{
			String code = orig.getCode().getCode();

			if("01".equals(code)){
				ImpPress impPressCopy = (ImpPress) copy;
				List<SchedaGeneratoriIndiv> generatoriCopy = impPressCopy.getSchedaGeneratoriIndiv();
				List<SchedaRecipientiIndiv> recipientiCopy = impPressCopy.getSchedaRecipientiIndiv();
				List<SchedaTubazioniIndiv> tubazioniCopy = impPressCopy.getSchedaTubazioniIndiv();

				if(generatoriCopy != null && generatoriCopy.size() > 0)
					return;
				if(recipientiCopy != null && recipientiCopy.size() > 0)
					return;
				if(tubazioniCopy != null && tubazioniCopy.size() > 0)
					return;

				ImpPress impPressOrig = (ImpPress) orig;
				List<SchedaGeneratoriIndiv> generatoriToCopy = impPressOrig.getSchedaGeneratoriIndiv();

				if(generatoriToCopy != null && generatoriToCopy.size() > 0 ){
					for(SchedaGeneratoriIndiv sg : generatoriToCopy){
						SchedaGeneratoriIndiv newSg = new SchedaGeneratoriIndiv();
						newSg.setNumeroFabbrica(sg.getNumeroFabbrica());
						newSg.setCostruttore(sg.getCostruttore());
						newSg.setPressBar1(sg.getPressBar1());
						newSg.setCapacita(sg.getCapacita());
						newSg.setSuperficie(sg.getSuperficie());
						newSg.setProducibilita(sg.getProducibilita());

						newSg.setImpPress(impPressCopy);
						//generatoriCopy.add(newSg);
						impPressCopy.addSchedaGeneratoriIndiv(newSg);
						ca.create(newSg);
					}
					//copy.setSchedaGeneratoriIndiv(generatoriCopy);				
				}

				List<SchedaRecipientiIndiv> recipientiToCopy = impPressOrig.getSchedaRecipientiIndiv();

				if(recipientiToCopy != null && recipientiToCopy.size() > 0 ){
					for(SchedaRecipientiIndiv sr : recipientiToCopy){
						SchedaRecipientiIndiv newSr = new SchedaRecipientiIndiv();
						newSr.setNumeroFabbrica(sr.getNumeroFabbrica());
						newSr.setCostruttore(sr.getCostruttore());
						newSr.setPressBar1(sr.getPressBar1());
						newSr.setCapacita(sr.getCapacita());

						newSr.setImpPress(impPressCopy);
						//generatoriCopy.add(newSg);
						impPressCopy.addSchedaRecipientiIndiv(newSr);
						ca.create(newSr);
					}
					//copy.setSchedaGeneratoriIndiv(generatoriCopy);				
				}

				List<SchedaTubazioniIndiv> tubazioniToCopy = impPressOrig.getSchedaTubazioniIndiv();

				if(tubazioniToCopy != null && tubazioniToCopy.size() > 0 ){
					for(SchedaTubazioniIndiv st : tubazioniToCopy){
						SchedaTubazioniIndiv newSt = new SchedaTubazioniIndiv();
						newSt.setNumeroFabbrica(st.getNumeroFabbrica());
						newSt.setCostruttore(st.getCostruttore());

						newSt.setImpPress(impPressCopy);
						//generatoriCopy.add(newSg);
						impPressCopy.addSchedaTubazioniIndiv(newSt);
						ca.create(newSt);
					}
					//copy.setSchedaGeneratoriIndiv(generatoriCopy);				
				}

			}else if("02".equals(code)){

				ImpRisc impRiscCopy = (ImpRisc) copy;
				List<SchedaGeneratori> generatoriCopy = impRiscCopy.getSchedaGeneratori();
				List<SchedaVasi> vasiCopy = impRiscCopy.getSchedaVasi();

				if(generatoriCopy != null && generatoriCopy.size() > 0)
					return;
				if(vasiCopy != null && vasiCopy.size() > 0)
					return;

				ImpRisc impRiscOrig = (ImpRisc) orig;
				List<SchedaGeneratori> generatoriToCopy = impRiscOrig.getSchedaGeneratori();

				if(generatoriToCopy != null && generatoriToCopy.size() > 0 ){
					for(SchedaGeneratori sn : generatoriToCopy){
						SchedaGeneratori newSn = new SchedaGeneratori();
						newSn.setCodiceComb(sn.getCodiceComb());
						newSn.setCodiceCombCv(sn.getCodiceCombCv());
						newSn.setCostruttore(sn.getCostruttore());
						newSn.setNumero(sn.getNumero());
						newSn.setNumeroFabbrica(sn.getNumeroFabbrica());
						newSn.setPotGlob(sn.getPotGlob());
						newSn.setPotGlobNom(sn.getPotGlobNom());
						newSn.setPressMax(sn.getPressMax());
						newSn.setType(sn.getType());

						//ca.create(newSn);

						newSn.setImpRisc(impRiscCopy);
						//generatoriCopy.add(newSg);
						impRiscCopy.addSchedaGeneratori(newSn);
					}
					//copy.setSchedaGeneratoriIndiv(generatoriCopy);				
				}

				List<SchedaVasi> vasiToCopy = impRiscOrig.getSchedaVasi();

				if(vasiToCopy != null && vasiToCopy.size() > 0 ){
					for(SchedaVasi sv : vasiToCopy){
						SchedaVasi newSv = new SchedaVasi();
						newSv.setCapacita(sv.getCapacita());
						newSv.setClasse(sv.getClasse());
						newSv.setCodiceVaso1(sv.getCodiceVaso1());
						newSv.setCodiceVaso2(sv.getCodiceVaso2());
						newSv.setCodiceVaso3(sv.getCodiceVaso3());
						newSv.setNumero(sv.getNumero());
						newSv.setPress(sv.getPress());

						//ca.create(newSv);

						newSv.setImpRisc(impRiscCopy);
						//generatoriCopy.add(newSg);
						impRiscCopy.addSchedaVasi(newSv);
					}
					//copy.setSchedaGeneratoriIndiv(generatoriCopy);				
				}

			}
		}catch(PersistenceException ex){
			log.error(ex);
			throw new RuntimeException();
		}
	}

	public boolean checkImpianto(Impianto imp){

		boolean isSaveable = false;

		String code = imp.getCode().getCode();
		if ("01".equals(code)){
			ImpPressAction ipa = ImpPressAction.instance();
			return ipa.checkImpPress((ImpPress) imp);
		}else if("02".equals(code)){
			ImpRiscAction ira = ImpRiscAction.instance();
			return ira.checkImpRisc((ImpRisc) imp);			
		}else if("03".equals(code)){
			ImpMontaAction ima = ImpMontaAction.instance();
			return ima.checkImpMonta((ImpMonta) imp);
		}else if("04".equals(code)){
			ImpSollAction isa = ImpSollAction.instance();
			return isa.checkImpSoll((ImpSoll) imp);
		}else if("05".equals(code)){
			ImpTerraAction ita = ImpTerraAction.instance();
			return ita.checkImpTerra((ImpTerra) imp);
		}

		FacesErrorUtils.addErrorMessage("commError", "Impianto privo di tipologia.", "Impianto privo di tipologia.");
		return isSaveable;
	}

	public List<Sedi> getSediAddebitoList(Impianto imp, Date dVerif, Date dAdd){

		//		List<Sedi> sedi = null;
		//		if(imp != null){
		//			SediInstallazione si = this.getSedeInstallazione(imp, cal);
		//			sedi = si.getSede().getPersonaGiuridica().getSedi();
		//
		//		}else{
		//			SediInstallazione si = this.getEntity().getSedeInstallazione();
		//			PersoneGiuridiche pg = si.getSede().getPersonaGiuridica();
		//			sedi = pg.getSedi();
		//		}
		//
		//		List<Sedi> out = new ArrayList<Sedi>();
		//		if(sedi != null && sedi.size() > 0){
		//			for(Sedi se : sedi){
		//				if(se.getSedeAddebito()==null)
		//					continue;
		//				if(se.getSedeAddebito()){
		//					out.add(se);
		//				}
		//			}
		//
		//			//PhiDataModel<Sedi> sediList = new PhiDataModel<Sedi>(out, "SediList");
		//			return out;
		//		}		
		//		return null;

		// Post test ARPAV 21/02/2019
		List<Sedi> out = null;
		SediInstallazione si = null;

		Date cal = null;
		if(dVerif!=null)
			cal = dVerif;
		if(dAdd!=null)
			cal = dAdd;

		if(imp != null){
			si = this.getSedeInstallazione(imp, cal);
		}else{
			si = this.getEntity().getSedeInstallazione();
		}

		Sedi se = si.getSede();
		PersoneGiuridiche pg = se.getPersonaGiuridica();

		String qSediAdd = "SELECT sa FROM Sedi sa " + 
				"WHERE sa.personaGiuridica.internalId = :pgId AND sa.isActive = true AND sa.sedeAddebito = true";

		Query query = ca.createHibernateQuery(qSediAdd);
		query.setParameter("pgId", pg.getInternalId());
		out = (List<Sedi>)query.list();		

		return out;
	}

	public void manageCopy(Sedi se, IndirizzoSped is) {
		try{
			if (se==null)
				return;

			this.manageCopy(is);

			//Copia se nella sede di istallazione dell'impianto copia
			if (se.getAddr()==null)
				getEntity().getSedi().setAddr(new AD());
			else 
				getEntity().getSedi().setAddr(se.getAddr().cloneAd());

			getEntity().getSedi().setSedeAddebito(se.getSedeAddebito());

			getEntity().getSedi().setCig(se.getCig());
			getEntity().getSedi().setClasseEconomica(se.getClasseEconomica());
			getEntity().getSedi().setCodContabilita(se.getCodContabilita());
			getEntity().getSedi().setCodiceUnivoco(se.getCodiceUnivoco());
			getEntity().getSedi().setCodiceVia(se.getCodiceVia());
			getEntity().getSedi().setDenominazioneUnitaLocale(se.getDenominazioneUnitaLocale());
			getEntity().getSedi().setEsenzione(se.getEsenzione());
			getEntity().getSedi().setImpSpesa(se.getImpSpesa());
			getEntity().getSedi().setNote(se.getNote());
			getEntity().getSedi().setPrincipaleAdd(se.getPrincipaleAdd());
			getEntity().getSedi().setSedePrincipale(se.getSedePrincipale());
			getEntity().getSedi().setSettore(se.getSettore());
			getEntity().getSedi().setStato(se.getStato());

			if (se.getTelecom()==null)
				getEntity().getSedi().setTelecom(new TEL());
			else 
				getEntity().getSedi().setTelecom(se.getTelecom().cloneTel());

			getEntity().getSedi().setTipoAttivita(se.getTipoAttivita());
			getEntity().getSedi().setTipoUtente(se.getTipoUtente());
			getEntity().getSedi().setZona(se.getZona());
			getEntity().getSedi().setCopy(true);
			getEntity().getSedi().setIsActive(false);

			//Copia gli indirizzi di spedizione di se negli indirizzi di spedizione della sede di istallazione dell'impianto copia
			List<IndirizzoSped> indirizzi = se.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				getEntity().getSedi().setIndirizzoSped(new ArrayList<IndirizzoSped>());

				for (IndirizzoSped ind:indirizzi){
					IndirizzoSped indCpy = IndirizzoSpedAction.instance().copy(ind);
					ca.create(indCpy);
					getEntity().getSedi().getIndirizzoSped().add(indCpy);
				}
			}

			//Copia la persona giuridica il cui cf e piva comparirà nel tracciato di fatturazione
			getEntity().getSedi().setPersonaGiuridica(se.getPersonaGiuridica());	

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	public Impianto copy(Impianto imp, Date dataVerifica) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;

			//Cessione (più prossima) successiva alla data delle verifica 
			CessioneImp cessione = getNearestCessione(imp, dataVerifica);

			String code = imp.getCode().getCode();

			if ("".equals(code)) {
				return null;

				//Apparecchi a pressione
			} else if ("01".equals(code)) {
				Impianto ret = ImpPressAction.instance().copy((ImpPress)imp, cessione);
				ca.persist(ret);
				return ret;
				//Impianti di riscaldamento
			} else if ("02".equals(code)) {
				Impianto ret = ImpRiscAction.instance().copy((ImpRisc)imp, cessione);
				ca.persist(ret);
				return ret;
				//Ascensori e montacarichi
			} else if ("03".equals(code)) {
				Impianto ret = ImpMontaAction.instance().copy((ImpMonta)imp, cessione);
				ca.persist(ret);
				return ret;
				//Apparecchi di sollevamento	
			} else if ("04".equals(code)) {
				Impianto ret = ImpSollAction.instance().copy((ImpSoll)imp, cessione);
				ca.persist(ret);
				return ret;
				//Impianti elettrici	
			} else if ("05".equals(code)) {
				Impianto ret = ImpTerraAction.instance().copy((ImpTerra)imp, cessione);
				ca.persist(ret);
				return ret;
			} else {
				return null;
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//SediInstallazione storica
	public SediInstallazione getSedeInstallazione(Impianto imp, Date dataVerifica) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;

			//Cessione (più prossima) successiva alla data delle verifica 
			CessioneImp cessione = getNearestCessione(imp, dataVerifica);

			if (cessione==null)
				return imp.getSedeInstallazione();
			else 
				return cessione.getSediInstallazioneFrom();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//SediAddebito storica
	//	public SediAddebito getSedeAddebito(Impianto imp, Date dataVerifica) {
	//		try{
	//			if (imp==null || imp.getCode()==null)
	//				return null;
	//			
	//			//Cessione (più prossima) successiva alla data delle verifica 
	//			CessioneImp cessione = getNearestCessione(imp, dataVerifica);
	//			
	//			if (cessione==null)
	//				return imp.getSedeAddebito();
	//			else 
	//				return cessione.getSediAddebitoFrom();
	//			
	//		} catch (Exception ex) {
	//			log.error(ex);
	//			throw new RuntimeException(ex);
	//		} 
	//	}

	//SediAddebito storica dopo I0070276
	public Sedi getSedeAddebito(Impianto imp, Date dataVerifica) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;

			//Cessione (più prossima) successiva alla data delle verifica 
			CessioneImp cessione = getNearestCessione(imp, dataVerifica);

			if (cessione==null)
				return imp.getSedi();
			else 
				return cessione.getSediFrom();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	//IndirizzoSped storico
	public IndirizzoSped getIndirizzoSped(Impianto imp, Date dataVerifica) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;

			//Cessione (più prossima) successiva alla data delle verifica 
			CessioneImp cessione = getNearestCessione(imp, dataVerifica);

			if (cessione==null)
				return imp.getIndirizzoSped();
			else 
				return cessione.getIndirizzoSped();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/* Recupera la cessione (più prossima) successiva alla data della verifica */
	public CessioneImp getNearestCessione(Impianto imp, Date dataVerifica) {
		try{
			String code = imp.getCode().getCode();
			CessioneImp cess = null;
			List<CessioneImp> cessioni =null;

			if ("".equals(code))
				return null;

			//Apparecchi a pressione
			if ("01".equals(code))
				cessioni = ((ImpPress)imp).getCessioneImp();

			//Impianti di riscaldamento
			else if ("02".equals(code))
				cessioni = ((ImpRisc)imp).getCessioneImp();

			//Ascensori e montacarichi
			else if ("03".equals(code))
				cessioni = ((ImpMonta)imp).getCessioneImp();

			//Apparecchi di sollevamento	
			else if ("04".equals(code))
				cessioni = ((ImpSoll)imp).getCessioneImp();

			//Impianti elettrici	
			else if ("05".equals(code))
				cessioni = ((ImpTerra)imp).getCessioneImp();

			if (cessioni==null || cessioni.size()<1)
				return null;

			for (CessioneImp c: cessioni){
				if (c!=null && c.getDataCessione()!=null && c.getIsActive()){
					if (c.getDataCessione().after(dataVerifica)){
						if (cess == null || cess.getDataCessione().after(c.getDataCessione())){
							cess = c;
						}
					}
				}
			}

			return cess;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	// I0070276 sostituito da metodo omonimo che gestisce sedi con flag addebito
	//Invocato quando viene associata una nuova sede di addebito all'impianto storico associato alla verifica
	/*
	public void manageCopy(SediAddebito sa, IndirizzoSped is) {
		try{
			if (sa==null)
				return;

			this.manageCopy(is);

			//Copia sa nella sede di istallazione dell'impianto copia
			if (sa.getAddr()==null)
				getEntity().getSedeAddebito().setAddr(new AD());
			else 
				getEntity().getSedeAddebito().setAddr(sa.getAddr().cloneAd());

			getEntity().getSedeAddebito().setCig(sa.getCig());
			getEntity().getSedeAddebito().setClasseEconomica(sa.getClasseEconomica());
			getEntity().getSedeAddebito().setCodContabilita(sa.getCodContabilita());
			getEntity().getSedeAddebito().setCodiceVia(sa.getCodiceVia());
			getEntity().getSedeAddebito().setDenominazione(sa.getDenominazione());
			getEntity().getSedeAddebito().setEsenzione(sa.getEsenzione());
			getEntity().getSedeAddebito().setImpSpesa(sa.getImpSpesa());
			getEntity().getSedeAddebito().setNote(sa.getNote());
			getEntity().getSedeAddebito().setPrincipale(sa.getPrincipale());
			getEntity().getSedeAddebito().setSettore(sa.getSettore());
			getEntity().getSedeAddebito().setStato(sa.getStato());

			if (sa.getTelecom()==null)
				getEntity().getSedeAddebito().setTelecom(new TEL());
			else 
				getEntity().getSedeAddebito().setTelecom(sa.getTelecom().cloneTel());

			getEntity().getSedeAddebito().setTipoAttivita(sa.getTipoAttivita());
			getEntity().getSedeAddebito().setTipoUtente(sa.getTipoUtente());
			getEntity().getSedeAddebito().setZona(sa.getZona());
			getEntity().getSedeAddebito().setCopy(true);
			getEntity().getSedeAddebito().setIsActive(false);

			//Copia gli indirizzi di spedizione di sa negli indirizzi di spedizione della sede di istallazione dell'impianto copia
			List<IndirizzoSped> indirizzi = sa.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				getEntity().getSedeAddebito().setIndirizzoSped(new ArrayList<IndirizzoSped>());

				for (IndirizzoSped ind:indirizzi){
					getEntity().getSedeAddebito().getIndirizzoSped().add(IndirizzoSpedAction.instance().copy(ind));
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}*/

	public void manageCopy(IndirizzoSped is) {

		if (is==null)
			is = new IndirizzoSped();

		//Copia is nell'indirizzo di spedizione dell'impianto copia
		if (is.getAddr()==null)
			getEntity().getIndirizzoSped().setAddr(new AD());
		else
			getEntity().getIndirizzoSped().setAddr(is.getAddr().cloneAd());

		if (is.getTelecom()==null)
			getEntity().getIndirizzoSped().setTelecom(new TEL());
		else 
			getEntity().getIndirizzoSped().setTelecom(is.getTelecom().cloneTel());

		getEntity().getIndirizzoSped().setDenominazione(is.getDenominazione());
		getEntity().getIndirizzoSped().setNote(is.getNote());
		getEntity().getIndirizzoSped().setPrincipale(is.getPrincipale());
		getEntity().getIndirizzoSped().setStato(is.getStato());
		getEntity().getIndirizzoSped().setCopy(true);
		getEntity().getIndirizzoSped().setIsActive(false);

	}

	public void manageCopy(SediInstallazione si){

		if(si==null)
			si = new SediInstallazione();

		// Copia si nella sede d'installazione dell'impianto copia
		if (si.getAddr()==null)
			getEntity().getSedeInstallazione().setAddr(new AD());
		else
			getEntity().getSedeInstallazione().setAddr(si.getAddr().cloneAd());

		getEntity().getSedeInstallazione().setDenominazione(si.getDenominazione());
		getEntity().getSedeInstallazione().setNote(si.getNote());
		getEntity().getSedeInstallazione().setSede(si.getSede());
		getEntity().getSedeInstallazione().setTipologiaSede(si.getTipologiaSede());
		getEntity().getSedeInstallazione().setTipoSede(si.getTipoSede());
		getEntity().getSedeInstallazione().setCopy(true);
		getEntity().getSedeInstallazione().setIsActive(false);

		getEntity().getSedeInstallazione().setSediInstallazioneOrig(si);

	}

	/* Recupera la cessione (più prossima) precedente la data della verifica */
	public CessioneImp getPreviousCessione(Impianto imp, Date dataVerifica) {
		try{
			String code = imp.getCode().getCode();
			CessioneImp cess = null;
			List<CessioneImp> cessioni =null;

			if ("".equals(code))
				return null;

			//Apparecchi a pressione
			if ("01".equals(code))
				cessioni = ((ImpPress)imp).getCessioneImp();

			//Impianti di riscaldamento
			else if ("02".equals(code))
				cessioni = ((ImpRisc)imp).getCessioneImp();

			//Ascensori e montacarichi
			else if ("03".equals(code))
				cessioni = ((ImpMonta)imp).getCessioneImp();

			//Apparecchi di sollevamento	
			else if ("04".equals(code))
				cessioni = ((ImpSoll)imp).getCessioneImp();

			//Impianti elettrici	
			else if ("05".equals(code))
				cessioni = ((ImpTerra)imp).getCessioneImp();

			if (cessioni==null || cessioni.size()<1)
				return null;

			for (CessioneImp c: cessioni){
				if (c!=null && c.getDataCessione()!=null && c.getIsActive()){
					if (c.getDataCessione().before(dataVerifica)){
						if (cess == null || cess.getDataCessione().before(c.getDataCessione())){
							cess = c;
						}
					}
				}
			}

			return cess;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/*Recupera la cessione precedente o futura a seconda se la data della verifica 
	 * è stata spostata in avanti o indietro*/
	public CessioneImp getClosestCessione(Impianto imp, Date dataVerifica, Date oldDataVerifica){

		if(oldDataVerifica==null)
			return null;

		if(dataVerifica.before(oldDataVerifica))
			return getNearestCessione(imp, dataVerifica);

		if(dataVerifica.after(oldDataVerifica))
			return getPreviousCessione(imp, dataVerifica);

		return null;

	}

	public List<Sedi> getSAEsterneList(Impianto imp, Date dVerif, Date dAdd){

		List<Sedi> out = null;
		SediInstallazione si = null;

		Date cal = null;
		if(dVerif!=null)
			cal = dVerif;
		if(dAdd!=null)
			cal = dAdd;

		if(imp != null){
			si = this.getSedeInstallazione(imp, cal);
		}else{
			si = this.getEntity().getSedeInstallazione();
		}

		Sedi se = si.getSede();
		PersoneGiuridiche pg = se.getPersonaGiuridica();

		String qSediAdd = "SELECT se FROM Sedi se " + 
				"JOIN se.pgEsterne pg " + 
				"WHERE pg.internalId = :pgId";

		Query query = ca.createHibernateQuery(qSediAdd);
		query.setParameter("pgId", pg.getInternalId());
		out = (List<Sedi>)query.list();		

		return out;
	}

	@SuppressWarnings("unchecked")
	public List<Sedi> getSediAddebitoIntExtList(Impianto imp, Date dVerif, Date dAdd){

		// Post test ARPAV 30/09/2020
		List<Sedi> out = null;
		SediInstallazione si = null;

		Date cal = null;
		if(dVerif!=null)
			cal = dVerif;
		if(dAdd!=null)
			cal = dAdd;

		if(imp != null){
			si = this.getSedeInstallazione(imp, cal);
		}else{
			si = this.getEntity().getSedeInstallazione();
		}

		Sedi se = si.getSede();
		PersoneGiuridiche pg = se.getPersonaGiuridica();

		String qSediAdd = "SELECT sa FROM Sedi sa " + 
				"WHERE sa.personaGiuridica.internalId = :pgId AND sa.isActive = true AND sa.sedeAddebito = true";

		Query query = ca.createHibernateQuery(qSediAdd);
		query.setParameter("pgId", pg.getInternalId());
		out = (List<Sedi>)query.list();		

		String qSediAddExt = "SELECT se FROM Sedi se " + 
				"JOIN se.pgEsterne pg " + 
				"WHERE pg.internalId = :pgId";

		Query queryExternal = ca.createHibernateQuery(qSediAddExt);
		queryExternal.setParameter("pgId", pg.getInternalId());

		List<Sedi> esterne = null;
		try {
			esterne = (List<Sedi>) queryExternal.list();
		} catch (NoResultException e) {

		}

		out.addAll(esterne);

		return out;
	}

	public boolean setPropertyNull(String property) {
		return setPropertyNull(property, false);
	}

	public boolean setPropertyNull(String property, boolean returnValue) {
		Impianto spd = getEntity();
		
		if("01".equals(spd.getCode())){
			spd = (ImpPress) spd;
		}else if("02".equals(spd.getCode())){
			spd = (ImpRisc) spd;
		}else if("03".equals(spd.getCode())){
			spd = (ImpMonta) spd;
		}else if("04".equals(spd.getCode())){
			spd = (ImpSoll) spd;
		}else if("05".equals(spd.getCode())){
			spd = (ImpTerra) spd;
		}
		
		if (spd != null && property != null && !property.isEmpty()) {
			try {
				
				Method[] methods = spd.getClass().getMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					String propMethod = "set" + 
							property.substring(0, 1).toUpperCase() + 
							(property.length()>1 ? property.substring(1) : "");
					if (propMethod.equals(methodName)) {
						method.invoke(spd, new Object[]{null});
						break;
					}
				}
			} catch (Exception e)  {
				log.error("Error executing method", e);
			}
		}
		return returnValue;
	}

}