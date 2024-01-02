package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Esposti;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("EspostiAction")
@Scope(ScopeType.CONVERSATION)
public class EspostiAction extends BaseAction<Esposti, Long> {

	private static final long serialVersionUID = 1383243236L;

	public static EspostiAction instance() {
		return (EspostiAction) Component.getInstance(EspostiAction.class, ScopeType.CONVERSATION);
	}

	public void filterDates(){
		Date fromDate = (Date)getTemporary().get("fromDate");
		Date toDate = (Date)getTemporary().get("toDate");
		
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		LogicalExpression from = null;
		if(fromDate!=null)
			from = Restrictions.or(Restrictions.ge("startDate", fromDate), Restrictions.ge("endDate", fromDate));
		
		LogicalExpression to = null;
		if(toDate!=null)
			to = Restrictions.or(Restrictions.le("startDate", toDate), Restrictions.le("endDate", toDate));
		
		if(from!=null && to!=null)
			entityCriteria.add(Restrictions.or(from, to));
		else if(from!=null)
			entityCriteria.add(from);
		else if(to!=null)
			entityCriteria.add(to);
	}
	
	public Esposti copy(Esposti oldEsp){
		if(oldEsp==null)
			return null;
		
		Esposti newEsp = new Esposti();
		
		if(oldEsp.getCancerogeno()!=null){
			List<CodeValuePhi> newCancerogeno = new ArrayList<CodeValuePhi>();
			newCancerogeno.addAll(oldEsp.getCancerogeno());
			newEsp.setCancerogeno(newCancerogeno);
		}
		newEsp.setAgenti(oldEsp.getAgenti());
		newEsp.setAttivita(oldEsp.getAttivita());
		newEsp.setBio(oldEsp.getBio());
		newEsp.setCas(oldEsp.getCas());
		newEsp.setCessazioneDate(oldEsp.getCessazioneDate());
		newEsp.setCodClass(oldEsp.getCodClass());
		newEsp.setCodiceCE(oldEsp.getCodiceCE());
		newEsp.setEndDate(oldEsp.getEndDate());
		newEsp.setGruppo3(oldEsp.getGruppo3());
		newEsp.setMansione(oldEsp.getMansione());
		newEsp.setMetodo(oldEsp.getMetodo());
		newEsp.setNotInAll5(oldEsp.getNotInAll5());
		newEsp.setPerson(oldEsp.getPerson());
		newEsp.setStartDate(oldEsp.getStartDate());
		newEsp.setTempo(oldEsp.getTempo());
		newEsp.setTipo(oldEsp.getTipo());
		newEsp.setUm(oldEsp.getUm());
		newEsp.setValore(oldEsp.getValore());
		newEsp.setSostanza(oldEsp.getSostanza());
		newEsp.setTipologia(oldEsp.getTipologia());
		
		return newEsp;
	}

	public void filterCas(){
		CodeValuePhi cas1 = (CodeValuePhi)getTemporary().get("sostanze1");
		CodeValuePhi cas2 = (CodeValuePhi)getTemporary().get("sostanze2");
		
		//clean entityCriteria
		getEqual().remove("sostanza");
		
		if(cas1!=null && cas2!=null){
			((FilterMap)getEqual()).putOr("sostanza", cas1, cas2);
		}else if(cas1!=null){
			getEqual().put("sostanza", cas1);
		}else if(cas2!=null){
			getEqual().put("sostanza", cas2);
		}	
	}

	public void filterGruppo34(){
		CodeValuePhi bio1 = (CodeValuePhi)getTemporary().get("bio1");
		CodeValuePhi bio2 = (CodeValuePhi)getTemporary().get("bio2");
					
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		//clean entityCriteria
		removeExpression("this", "bio");
		removeExpression("this", "gruppo3");
		
		if(bio1!=null && bio2!=null){
			LogicalExpression sub1 = Restrictions.and(Restrictions.eq("bio",bio1), Restrictions.eq("gruppo3",true));
			LogicalExpression sub2 = Restrictions.and(Restrictions.eq("bio",bio2), Restrictions.eq("gruppo3",false));
			entityCriteria.add(Restrictions.or(sub1, sub2));
		}else if(bio1!=null){
			LogicalExpression sub1 = Restrictions.and(Restrictions.eq("bio",bio1), Restrictions.eq("gruppo3",true));
			entityCriteria.add(sub1);
		}else if(bio2!=null){
			LogicalExpression sub2 = Restrictions.and(Restrictions.eq("bio",bio2), Restrictions.eq("gruppo3",false));
			entityCriteria.add(sub2);
		}	
	}

	public List<SelectItem> createGroup34() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		SelectItem yes = new SelectItem(new Boolean(true), (String)app.evaluateExpressionGet(fc, "Gruppo 3", String.class));
		SelectItem no = new SelectItem(new Boolean(false), (String)app.evaluateExpressionGet(fc, "Gruppo 4", String.class));
		
		options.add(yes);
		options.add(no);
		
		return options;
	}
	
	public void setDomain(Esposti esp){
		getAutocomplete().get("sostanza").setContentType(4);
		getAutocomplete().get("sostanza").setCodeSystem("PHIDIC");
		getAutocomplete().get("sostanza").setFullLike(true);
		if(esp.getAgente()!=null)
			getAutocomplete().get("sostanza").setDomain(esp.getTipologia().getDisplayName());
		else
			getAutocomplete().get("sostanza").setDomain("NCAS");
	}

	public void resetValues(Esposti esp) throws PersistenceException, DictionaryException{
		getAutocomplete().get("sostanza").setContentType(4);
		getAutocomplete().get("sostanza").setCodeSystem("PHIDIC");
		getAutocomplete().get("sostanza").setFullLike(true);
		if(esp!=null){
			esp.setTipologia(null);
			esp.setSostanza(null);
			if(esp.getAgente()!=null){
				Vocabularies voc = VocabulariesImpl.instance();
				if("01".equals(esp.getAgente().getCode()) || "02".equals(esp.getAgente().getCode())){
					CodeValue ncas = voc.getCodeValue("PHIDIC", "FattoriRischio", "ncas", "S");
					if(ncas!=null){
						esp.setTipologia((CodeValuePhi) ncas);
						getAutocomplete().get("sostanza").setDomain(ncas.getDisplayName());
					}

				}else if("03".equals(esp.getAgente().getCode())){
					CodeValue nall = voc.getCodeValue("PHIDIC", "FattoriRischio", "nall", "S");
					if(nall!=null){
						esp.setTipologia((CodeValuePhi) nall);
						getAutocomplete().get("sostanza").setDomain(nall.getDisplayName());
					}
				}
			}
		}
	}

	public void resetFilters() throws PersistenceException, DictionaryException{
		String autocomplete = "sostanze.sostanza";
		if(autocomplete!=null && !autocomplete.isEmpty()){
			getAutocomplete().get(autocomplete).setContentType(4);
			getAutocomplete().get(autocomplete).setCodeSystem("PHIDIC");
			getAutocomplete().get(autocomplete).setFullLike(true);

			getTemporary().remove("sostanze1");
			getTemporary().remove("sostanze2");
			
			if(getTemporary().get("tipologia") instanceof CodeValue){
				CodeValue agente = (CodeValue)getTemporary().get("tipologia");
				getAutocomplete().get(autocomplete).setDomain(agente.getDisplayName());
			}
		}
	}
}