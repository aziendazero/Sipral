package com.phi.entities.actions;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.CessioneImp;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("CessioneImpAction")
@Scope(ScopeType.CONVERSATION)
public class CessioneImpAction extends BaseAction<CessioneImp, Long> {

	private static final long serialVersionUID = 2076716269L;
	private static final Logger log = Logger.getLogger(CessioneImpAction.class); 


	public static CessioneImpAction instance() {
		return (CessioneImpAction) Component.getInstance(CessioneImpAction.class, ScopeType.CONVERSATION);
	}

	public void setDeletableInList(List<CessioneImp> cessList, Impianto imp){

		if(cessList==null || cessList.size()<1 || imp==null)
			return;

		CessioneImp last = cessList.get(cessList.size()-1);
		Date lastDate = last.getDataCessione();

		for(CessioneImp c : cessList){
			c.setDeletable(false);

			Date currDate = c.getDataCessione();
			if(currDate.after(lastDate)){
				last = c;
				lastDate = currDate;
			}
		}

		//Impianto imp = (Impianto) Contexts.getConversationContext().get("Impianto");

		boolean noVerif = searchVerif(imp, lastDate);

		if(noVerif)
			last.setDeletable(true);
	}

	@SuppressWarnings("unchecked")
	private boolean searchVerif(Impianto imp, Date dateFrom) {

		boolean noVerif = true;

		String code = imp.getCode().getCode();

		String q = "SELECT v FROM VerificaImp v WHERE v.isActive = 1 AND ";

		if("01".equals(code)){
			q += "v.impPress = :impPress";
		}else if("02".equals(code)){
			q += "v.impRisc = :impRisc";
		}else if("03".equals(code)){
			q += "v.impMonta = :impMonta";
		}else if("04".equals(code)){
			q += "v.impSoll = :impSoll";
		}else if("05".equals(code)){
			q += "v.impTerra = :impTerra";
		}
		q += " AND v.data >= :dt";

		Query qVerif = ca.createQuery(q);

		if("01".equals(code)){
			qVerif.setParameter("impPress", (ImpPress) imp);
		}else if("02".equals(code)){
			qVerif.setParameter("impRisc", (ImpRisc) imp);
		}else if("03".equals(code)){
			qVerif.setParameter("impMonta", (ImpMonta) imp);
		}else if("04".equals(code)){
			qVerif.setParameter("impSoll", (ImpSoll) imp);
		}else if("05".equals(code)){
			qVerif.setParameter("impTerra", (ImpTerra) imp);
		}
		qVerif.setParameter("dt", dateFrom);

		List<VerificaImp> verifList = null;
		try {
			verifList = (List<VerificaImp>) qVerif.getResultList();
		} catch (NoResultException e) {
			return false;
		}

		if(verifList!=null && verifList.size()>0)
			noVerif = false;

		return noVerif;
	}

	public Date getPreviousOwnershipEnd(CessioneImp ci){

		if(ci == null)
			return null;

		//		String q = "SELECT MAX(c.dataCessione) FROM CessioneImp c WHERE c.isActive = 1 AND "
		//				+ "c.dataCessione < :dt AND c.sediInstallazione = :siFrom AND c.sediAddebito = :saFrom AND "
		//				+ "c.indirizzoSped = :isFrom AND c.personaGiuridica = :pgFrom";

		//		Query qDate = ca.createQuery(q);
		//		qDate.setParameter("dt", ci.getDataCessione());
		//		qDate.setParameter("siFrom", ci.getSediInstallazioneFrom());
		//		qDate.setParameter("saFrom", ci.getSediAddebitoFrom());
		//		qDate.setParameter("isFrom", ci.getIndirizzoSpedFrom());
		//		qDate.setParameter("pgFrom", ci.getPersonaGiuridicaFrom());

		String q = "SELECT MAX(c.dataCessione) FROM CessioneImp c WHERE c.isActive = 1 AND "
				+ "c.dataCessione < :dt AND c.sediInstallazione = :siFrom AND c.sedi = :saFrom AND "
				+ "c.indirizzoSped = :isFrom AND c.personaGiuridica = :pgFrom";

		Query qDate = ca.createQuery(q);
		qDate.setParameter("dt", ci.getDataCessione());
		qDate.setParameter("siFrom", ci.getSediInstallazioneFrom());
		qDate.setParameter("saFrom", ci.getSediFrom());
		qDate.setParameter("isFrom", ci.getIndirizzoSpedFrom());
		qDate.setParameter("pgFrom", ci.getPersonaGiuridicaFrom());

		Date dateAcq = null;
		try {
			dateAcq = (Date) qDate.getSingleResult();
		} catch (NoResultException e) {
			//dateAcq = null;
		}

		return dateAcq;

	}

	public void fillOtherFieldsFromSedeInst(CessioneImp ci){
		try {
			if(ci == null || ci.getPersonaGiuridica()==null)
				return;
			
			List<Sedi> sediList = ci.getPersonaGiuridica().getSedi();
			
			if(sediList == null)
				return;

			Sedi sedePrincipaleAdd = null;
			List<Sedi> sediAddebito = new ArrayList<Sedi>();
			
			//Itero la lista di sedi
			for (Sedi s:sediList){
				if(s.getIsActive()){					
					//Cerco tutte le sedi di addebito
					if (Boolean.TRUE.equals(s.getSedeAddebito())){
						//Cerco e memorizzo la sede principale se è anche sede di addebito
						if (s.getSedePrincipale())
							sedePrincipaleAdd = s;
						
						sediAddebito.add(s);
					}
				}
			}
			
			Sedi sede = null;
			
			//Se ho trovato la sede principale, la salvo nella variabile "sede"
			if (sedePrincipaleAdd!=null)
				sede = sedePrincipaleAdd;
			
			//Altrimenti, se ho trovato solo una sede di addebito, la salvo nella variabile "sede"
			else if (sediAddebito.size()==1)
				sede = sediAddebito.get(0);
			
			//Se ho trovato la sede principale OPPURE una sola sede di addebito
			if (sede!=null){
				//la associo alla cessione
				ci.setSedi(sede);
				
				if (sede.getIndirizzoSped()==null)
					return;
				
				//Se la sede che sto usando ha un solo indirizzo di spedizione 
				if(sede.getIndirizzoSped().size() == 1) {
					if(sede.getIndirizzoSped().get(0).getIsActive()){
						//lo associo alla cessione
						ci.setIndirizzoSped(sede.getIndirizzoSped().get(0));
					}
				} else{
					//cerco e associo (se esiste) l'indirizzo di spedizione principale
					for (IndirizzoSped i:sede.getIndirizzoSped()) {
						if(i.getIsActive() && sede.getIndirizzoSpedPrinc()!=null && sede.getIndirizzoSpedPrinc().getInternalId() == i.getInternalId()) {
							ci.setIndirizzoSped(i);
						}
					}
				}
			}
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Date getEndOwnership(CessioneImp ci){

		if(ci == null)
			return null;

		Date dataCessione = ci.getDataCessione();

		if(dataCessione == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dataCessione);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Date ret = cal.getTime();

		return ret;
	}

	public String getTipo(CessioneImp ci){
		if (ci==null)
			return "";

		CodeValuePhi code = null;

		try {

			if (ci.getImpPress()!=null){
				code = ci.getImpPress().getCode();

			} else if (ci.getImpRisc()!=null){
				code = ci.getImpRisc().getCode();

			} else if (ci.getImpMonta()!=null){
				code = ci.getImpMonta().getCode();

			} else if (ci.getImpSoll()!=null){
				code = ci.getImpSoll().getCode();

			} else if (ci.getImpTerra()!=null){
				code = ci.getImpTerra().getCode();
			}

			if (code!=null)
				return code.getCurrentTranslation();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

		return "";
	}

	public String getSottotipo(CessioneImp ci){
		if (ci==null)
			return "";

		try {

			if (ci.getImpSoll()!=null){
				CodeValuePhi type = ci.getImpSoll().getSubTypeSoll();
				if (type!=null)
					return type.getCurrentTranslation();
			} else if (ci.getImpTerra()!=null){
				CodeValuePhi type = ci.getImpTerra().getSubTypeTerra();
				if (type!=null)
					return type.getCurrentTranslation();
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
		
		return "";
	}

	public String getSigla(CessioneImp ci){
		if (ci==null)
			return "";

		try {

			if (ci.getImpPress()!=null)
				return ci.getImpPress().getSigla();

			if (ci.getImpRisc()!=null)
				return ci.getImpRisc().getSigla();

			if (ci.getImpMonta()!=null)
				return ci.getImpMonta().getSigla();

			if (ci.getImpSoll()!=null)
				return ci.getImpSoll().getSigla();

			if (ci.getImpTerra()!=null)
				return ci.getImpTerra().getSigla();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		return "";
	}

	public String getMatricola(CessioneImp ci){
		if (ci==null)
			return "";

		try {

			if (ci.getImpPress()!=null)
				return ci.getImpPress().getMatricola();

			if (ci.getImpRisc()!=null)
				return ci.getImpRisc().getMatricola();

			if (ci.getImpMonta()!=null)
				return ci.getImpMonta().getMatricola();

			if (ci.getImpSoll()!=null)
				return ci.getImpSoll().getMatricola();

			if (ci.getImpTerra()!=null)
				return ci.getImpTerra().getMatricola();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		return "";
	}

	public String getAnno(CessioneImp ci){
		if (ci==null)
			return "";

		try {

			if (ci.getImpPress()!=null)
				return ci.getImpPress().getAnno();

			if (ci.getImpRisc()!=null)
				return ci.getImpRisc().getAnno();

			if (ci.getImpMonta()!=null)
				return ci.getImpMonta().getAnno();

			if (ci.getImpSoll()!=null)
				return ci.getImpSoll().getAnno();

			if (ci.getImpTerra()!=null)
				return ci.getImpTerra().getAnno();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		return "";
	}
}