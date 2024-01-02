package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.web.Locale;

import com.phi.cs.exception.PersistenceException;
import com.phi.entities.act.Nanda;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueNanda;

/**
 * From PHI CI
 * TODO fix query GET_CODE_EQUIVALENT_FOR_NANDA with CodeValue for Nanda and its 
 * equivalent as soon as it is available
 */
@BypassInterceptors
@Name("NandaAction")
@Scope(ScopeType.CONVERSATION)
public class NandaAction extends BaseAction<Nanda, Long> {

	private final class CodeValueComparator implements Comparator<CodeValue> {
		public int compare(CodeValue f1, CodeValue f2)
		{
			return (f1.getDisplayName()).compareTo(f2.getDisplayName());
		}
	}

	private static final long serialVersionUID = 240023864L;
	private static final Logger log = Logger.getLogger(NandaAction.class);

	private static String COUNT_NANDA_QUESTIONNAIRE = "select count(nanda) from Nanda nanda where nanda.patientEncounter.internalId =:internalID and nanda.isActive = 1  and nanda.nandaDiag.id=:nandaDiag";
	
	public static String GET_CODE_EQUIVALENT_FOR_NANDA = "SELECT Distinct target FROM CodeValueNanda target JOIN target.inverseRelationsNanda source " +
			"WHERE source.id = :id AND target.code like :code";
	
	private static String HQL = "SELECT distinct target FROM CodeValueNanda target " +
			"JOIN target.children firstLevelChilds " +
			"JOIN firstLevelChilds.children secondLevelChilds " +
			"WHERE  target.displayName like 'Domane%' AND target.id like '1.2.16.840.1.113883.3.20.K825.Th%' " +
			"AND (firstLevelChilds.id IN (:ids) OR secondLevelChilds.id IN (:ids))";
	public static NandaAction instance() {
		return (NandaAction) Component.getInstance(NandaAction.class, ScopeType.CONVERSATION);
	}

	public boolean risk() throws PersistenceException  {

		Nanda currentnanda = getEntity();
		if (currentnanda.getNandaRF() == null ){
			return false;
		}else if(currentnanda.getNandaRF() != null){
			return true;
		} 
		return false;
	}	

	public boolean activeNanda(PatientEncounter enc,CodeValue nandaCode ) throws PersistenceException  {
		if (enc == null || nandaCode==null)
			return true;

		HashMap<String, Object> parameters= new HashMap<String, Object>(2);
		parameters.put("internalID", enc.getInternalId());
		parameters.put("nandaDiag", nandaCode.getId());
		List<Long> NandaList = (List<Long>) ca.executeHQLwithParameters(COUNT_NANDA_QUESTIONNAIRE, parameters);

		if ((Long)NandaList.get(0)==0)
			return false;
		else
			return true;	
	}


	public List<CodeValue> getNandaBFSign(CodeValue nandaCode) {
		return this.getNandaEquivalence(nandaCode, "K1555");
	}

	public List<CodeValue> getNandaBM(CodeValue nandaCode) {
		return this.getNandaEquivalence(nandaCode, "K1550");
	}

	public List<CodeValue> getNandaRF(CodeValue nandaCode) {
		return this.getNandaEquivalence(nandaCode, "K1560");
	}	

	public List<CodeValue> getNandaEquivalence(CodeValue nandaCode,String code) {
		List<CodeValue> nandaCodeList = new ArrayList<CodeValue>();
		String lang = Locale.instance().getLanguage();

		if (nandaCode==null){
			return nandaCodeList;
		}
		else{	
			HashMap<String,Object> parameters = new HashMap<String, Object>();

			parameters.put("id", nandaCode.getId());
			parameters.put("code", code+"%");

			try {
				nandaCodeList = (List<CodeValue>)ca.executeHQLwithParameters(GET_CODE_EQUIVALENT_FOR_NANDA, parameters);
			} catch (PersistenceException e) {
				log.error("Error getting !", e);
			}

			return nandaCodeList;}
	}
	/**
	 * method used for rendering different virtual page on diagnosis type(risk or real) it risk if the nanda selected code has at least 1 risk element
	 * @param nandaCode
	 * @return
	 * @throws PersistenceException
	 */
	public boolean isRisk(CodeValue nandaCode ) throws PersistenceException  {
		List<Object> NandaRFList = new ArrayList<Object>();
		if ( nandaCode==null)
			return false;
		HashMap<String,Object> parameters = new HashMap<String, Object>();

		parameters.put("id", nandaCode.getId());
		parameters.put("code", "K1560%");
		
		NandaRFList = ca.executeHQLwithParameters(GET_CODE_EQUIVALENT_FOR_NANDA.replace("Distinct target", "count(*)"), parameters);

		if ((Long)NandaRFList.get(0)==0)
			return false;
		else
			return true;
	}

	public Date thatTime(){


		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,17);
		cal.set(Calendar.MINUTE,30);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);

		Date d = cal.getTime();
		return d;


	}

	public void CountProgNumber(PatientEncounter enc ) throws PersistenceException  {
		if (enc == null )
			return;
		if (this.getEntity().getProgNumber()==null) {


			List<Long> NandaNum = new ArrayList<Long>();
			HashMap<String,Object> parameters = new HashMap<String, Object>();

			parameters.put("id", enc.getInternalId());
			String hql = "select count(*) from Nanda nanda where nanda.patientEncounter.internalId =:id ";

			NandaNum = ca.executeHQLwithParameters(hql, parameters);
			Long y=NandaNum.get(0);
			Integer x = (int) (y.intValue());
			x+=1;	
			this.getEntity().setProgNumber(x);

		}
		else{
			return;
		}
	}

	// list of all diagnosis for the selected PatientEncounter
	private List<CodeValue> favoriteDiagnosesCache = new ArrayList<CodeValue>();
	private HashMap<String, List<CodeValue>> favoriteDiagnosesHash = new HashMap<String, List<CodeValue>>();

	public List<CodeValue> getFilteredDiagnosis (List<CodeValue> favoriteDiagnoses, String reqDomain) {

		if (favoriteDiagnoses == null || reqDomain== null || reqDomain.isEmpty())
			return favoriteDiagnosesCache;

		if (favoriteDiagnosesCache == null)
			favoriteDiagnosesCache = favoriteDiagnoses;

		if (favoriteDiagnosesCache.equals(favoriteDiagnoses)) {
			HashSet hs = new HashSet();
			hs.addAll(favoriteDiagnosesHash.get(reqDomain));
			(favoriteDiagnosesHash.get(reqDomain)).clear();
			(favoriteDiagnosesHash.get(reqDomain)).addAll(hs);

			//orderby display name
			Collections.sort((favoriteDiagnosesHash.get(reqDomain)),new CodeValueComparator());

			return  favoriteDiagnosesHash.get(reqDomain);
		}

		favoriteDiagnosesCache = favoriteDiagnoses;

		for (CodeValue cv : favoriteDiagnoses) {

			CodeValue grandParent = cv.getParent(); 
			while (grandParent != null && !grandParent.getCode().startsWith("Domane")) {
				grandParent = grandParent.getParent();
			}


			String domain = grandParent.getCode();
			List<CodeValue> tmpList = new ArrayList<CodeValue>();

			if (favoriteDiagnosesHash.containsKey(domain)) {
				tmpList = favoriteDiagnosesHash.get(domain);
			}
			tmpList.add(cv);
			favoriteDiagnosesHash.put(domain, tmpList);
		}
		//order by displayname and delete duplicate
		List<CodeValue> favor=favoriteDiagnosesHash.get(reqDomain);
		HashSet<CodeValue> hs = new HashSet<CodeValue>();
		hs.addAll(favor);
		favor.clear();
		favor.addAll(hs);


		Collections.sort(favor,new CodeValueComparator());
		return favor;
	}

	private List<CodeValue> SUGDiagnosesCache = new ArrayList<CodeValue>();
	private HashMap<String, List<CodeValue>> SUGDiagnosesHash = new HashMap<String, List<CodeValue>>();

	public List<CodeValue> getSUGDiagnosis (List<CodeValue> SUGDiagnoses, String reqDomain) {

		if (SUGDiagnoses == null || reqDomain== null || reqDomain.isEmpty())
			return SUGDiagnosesCache;

		if (SUGDiagnosesCache == null)
			SUGDiagnosesCache = SUGDiagnoses;

		if (SUGDiagnosesCache.equals(SUGDiagnoses)) {
			HashSet hs = new HashSet();
			hs.addAll(SUGDiagnosesHash.get(reqDomain));
			(SUGDiagnosesHash.get(reqDomain)).clear();
			(SUGDiagnosesHash.get(reqDomain)).addAll(hs);


			//orderby display name
			Collections.sort((SUGDiagnosesHash.get(reqDomain)),new CodeValueComparator());


			return  SUGDiagnosesHash.get(reqDomain);
		}

		SUGDiagnosesCache = SUGDiagnoses;

		for (CodeValue cv : SUGDiagnoses) {

			CodeValue grandParent = cv.getParent(); 
			while (grandParent != null && !grandParent.getCode().startsWith("Domane")) {
				grandParent = grandParent.getParent();
			}


			String domain = grandParent.getCode();
			List<CodeValue> tmpList = new ArrayList<CodeValue>();

			if (SUGDiagnosesHash.containsKey(domain)) {
				tmpList = SUGDiagnosesHash.get(domain);
			}
			tmpList.add(cv);
			SUGDiagnosesHash.put(domain, tmpList);
		}
		//order by displayname and delete duplicate
		List<CodeValue> favor=SUGDiagnosesHash.get(reqDomain);
		HashSet<CodeValue> hs = new HashSet<CodeValue>();
		hs.addAll(favor);
		favor.clear();
		favor.addAll(hs);

		Collections.sort(favor,new CodeValueComparator());

		return favor;
	}

	public List<CodeValue> getNandaDomains (List<CodeValue> favoriteDiagnoses) {
		List<CodeValue> domList = new ArrayList<CodeValue>();
		if (favoriteDiagnoses==null || favoriteDiagnoses.size()==0 )
			return domList;


		Collection<String> idCodeValueList=new HashSet<String>(favoriteDiagnoses.size());
		for (CodeValue favorite : favoriteDiagnoses) {
			idCodeValueList.add(favorite.getId());
		}

		HashMap<String,Object> parameters = new HashMap<String, Object>();

		parameters.put("ids", idCodeValueList);
		
		try {
			domList = (List<CodeValue>)ca.executeHQLwithParameters(HQL, parameters);

			HashSet<CodeValue> hs = new HashSet<CodeValue>(domList.size());
			hs.addAll(domList);
			domList.clear();
			domList.addAll(hs);

			Collections.sort(domList,
					new CodeValueComparator());

		} catch (PersistenceException e) {
			log.error("Error getting !", e);
		}

		return domList;
	}


}
