package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.Diagnosis;
import com.phi.entities.dataTypes.CodeValueIcd9;

@BypassInterceptors
@Name("DiagnosisAction")
@Scope(ScopeType.CONVERSATION)
public class DiagnosisAction extends BaseAction<Diagnosis, Long> {

	private static final long serialVersionUID = -309160076163L;
//	private static final Logger log = Logger.getLogger(DiagnosisAction.class);
		
	public static DiagnosisAction instance() {
		return (DiagnosisAction) Component.getInstance(DiagnosisAction.class, ScopeType.CONVERSATION);
	}
	
	public Boolean mainDiagnosisSelected(List<Diagnosis> diagnosisList) throws Exception {
			
		if (diagnosisList.isEmpty()) {
			return false;
		}

		for (Diagnosis diagnosis : diagnosisList) {
			if (diagnosis.getMainDiagnosis())
				return true;
		}

		return false;

	}
	/**
	 * Convert a CodeValue contained in a temporary
	 * diagCode conversation into a Diagnoses with that code
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Diagnosis convertCodeValueToDiag() throws InstantiationException, IllegalAccessException{
		Diagnosis diag = null;
		if(this.getTemporary().get("diagCode")!= null){
			CodeValueIcd9 codeIcd9 = (CodeValueIcd9)this.getTemporary().get("diagCode");
			String descr= (String) this.getTemporary().get("description");
			diag = newEntity();
			diag.setAvailabilityTime(diag.getCreationDate());
			diag.setCodeIcd9(codeIcd9);
			if(descr == null || descr.isEmpty()){
				diag.setDescription(codeIcd9.getDisplayName());
			}
			else{
				diag.setDescription(descr);
			}
		}
		return diag;
	}
	/**
	 * This method remove a Diagnosis in a list if the code is the same
	 * @param diagList
	 * @param diag: Diagnosis that you want remove
	 */
	public void removeDiagnosisByCodeIcd9(List<Diagnosis> diagList, Diagnosis diag){
		if(diagList.isEmpty() || diag== null){
			return;
		}
		if(diag.getCodeIcd9().getCode()!=null){
			String codeDiag = diag.getCodeIcd9().getCode() ;
			Iterator itr= diagList.iterator();
			while(itr.hasNext()){
				Diagnosis nextDiag= (Diagnosis)itr.next();
				if(codeDiag!=null && nextDiag.getCodeIcd9().getCode()!= null && codeDiag.equals(nextDiag.getCodeIcd9().getCode())){
					itr.remove();
					}
				}
			}
		}
		

	
	/**
	 * Add a diagnosis to a Diagnosis List with a given value for the Score
	 * @param diagList - List of diagnosis
	 * @param diag - Diagnosis to be added
	 * @param temporary - temporary score
	 */
	public void addDiagnosisWithScore(List<Diagnosis> diagList, Diagnosis diag, HashMap<String,Object> temporary){
		
		if(diag == null || diagList == null || temporary.get("tmpScore") == null){
//			log.error("Impossible to add the object diagnosis to the list.");
			return;
		}
		
		Integer tmpScore = (Integer)temporary.get("tmpScore");
		diag.setScore(tmpScore);
		diagList.add(diag);
		temporary.put("tmpScore",tmpScore+1);
		
	}
	
	/**
	 * Remove a diagnosis from a Diagnosis List sorted by score
	 * @param diagList - List of diagnosis
	 * @param diag - Diagnosis to be removed
	 * @param temporary - temporary score
	 */
	public void removeDiagnosisWithScore(List<Diagnosis> diagList, Diagnosis diag, HashMap<String,Object> temporary){
		
		if(diag == null || diagList == null || temporary.get("tmpScore") == null){
//			log.error("Impossible to add the object diagnosis to the list.");
			return;
		}
		
		Integer tmpScore = (Integer)temporary.get("tmpScore");
		Integer currentScore = diag.getScore();
		
		//if diag is the one with the highest score, just delete it from the list
		if(currentScore == tmpScore -1){
			diagList.remove(diag);
		}
		
		//scan the list and update the scores
		else {
			for(Diagnosis tmpDiag : diagList){
				if(tmpDiag.getScore()>currentScore){
					tmpDiag.setScore(tmpDiag.getScore()-1);
				}
			}
			diagList.remove(diag);
		}
		
		//update tmpScore
		temporary.put("tmpScore",tmpScore-1);
		
	}
	
	
	/**
	 * Decrease or Increase the score of a Diagnosis and then sort by Score the List.
	 * @param diagList - List of diagnosis
	 * @param diag - Diagnosis to be updated
	 * @param increase - the score of diagnosis will be increased if it true
	 * @param temporary - temporary score
	 */
	public void increaseDecreaseScore(List<Diagnosis> diagList, Diagnosis diag, boolean increase, HashMap<String,Object> temporary){
		
		if(diag == null || diagList == null){
//			log.error("Impossible to add the object diagnosis to the list.");
			return;
		}
		
		if(increase) {
			if(diag.getScore() == (Integer)temporary.get("tmpScore")-1){
				return;
			}
			//find the object with the score = diag.getScore() + 1 and swap the values
			else{
				for(Diagnosis tmpDiag : diagList){
//					FIXME : the solution provided is not taking in count of the internalId. See the original version which doesn't work when the
//					diagnosis are not persisted yet.
//					if( tmpDiag.getInternalId() != diag.getInternalId() && tmpDiag.getScore() == diag.getScore()+1){
					if( !tmpDiag.getCodeIcd9().getCode().equals(diag.getCodeIcd9().getCode()) && tmpDiag.getScore() == diag.getScore()+1){
						tmpDiag.setScore(tmpDiag.getScore()-1);
						diag.setScore(diag.getScore()+1);
						diagList = orderListByScore(diagList);
						return;
					}
				}
			}
		}
		else {
			if(diag.getScore() == 0){
				return;
			}
			//find the object with the score = diag.getScore() - 1 and swap the values
			else{
				for(Diagnosis tmpDiag : diagList){
//					FIXME : the solution provided is not taking in count of the internalId. See the original version which doesn't work when the
//					diagnosis are not persisted yet.
//					if( tmpDiag.getInternalId() != diag.getInternalId() && tmpDiag.getScore() == diag.getScore()-1){
					if( !tmpDiag.getCodeIcd9().getCode().equals(diag.getCodeIcd9().getCode()) && tmpDiag.getScore() == diag.getScore()-1){
						tmpDiag.setScore(tmpDiag.getScore()+1);
						diag.setScore(diag.getScore()-1);
						diagList = orderListByScore(diagList);
						return;
					}
				}
			}
		}
		
	}
	
	
	/**
	 * ComparatoByScore compare Diagnosis object by Score Value
	 * @author Vito
	 *
	 */
	private class ComparatorByScore implements Comparator<Diagnosis>{

		@Override
		public int compare(Diagnosis diag1, Diagnosis diag2) {

			Integer score1 = (diag1.getScore() == null ? null : diag1.getScore());
			Integer score2 = (diag2.getScore() == null ? null : diag2.getScore());

			if (score1 == null && score2 == null)
				return 0;

			if (score1 == null)
				return -1;

			if (score2 == null)
				return 1;

			return (score1 - score2);
		}
	}
	
	public List<Diagnosis> orderListByScore(List<Diagnosis> listToOrder){
		
		if(listToOrder == null || listToOrder.isEmpty())
			return listToOrder;
		
		Collections.sort(listToOrder, new ComparatorByScore());
		
		return listToOrder;
		
		
	}
	
	public List<Diagnosis> filterActive (List<Diagnosis> list) {
		
		List<Diagnosis> ret = new ArrayList<Diagnosis>();
		for (Diagnosis d : list) {
			if (d.getIsActive()) {
				ret.add(d);
			}
		}
		
		return ret;
	}
	
	public boolean checkMain (List<Diagnosis> list) {
		for (Diagnosis d: list) {
			if (d.getMainDiagnosis()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Metodo che controlla che non ci siano altre diagnosi principali escludendo quella corrente
	 * @param list
	 * @param currentDiagnosis
	 * @return
	 */
	public boolean checkMain (List<Diagnosis> list, Diagnosis currentDiagnosis) {
		for (Diagnosis d: list) {
			if (d.getMainDiagnosis() && d.getInternalId() != currentDiagnosis.getInternalId()) {
				return true;
			}
		}
		return false;
	}
	
}
	
	
	