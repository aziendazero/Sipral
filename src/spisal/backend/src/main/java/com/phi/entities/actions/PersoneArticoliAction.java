package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.PersoneArticoli;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("PersoneArticoliAction")
@Scope(ScopeType.CONVERSATION)
public class PersoneArticoliAction extends BaseAction<PersoneArticoli, Long> {

	private static final long serialVersionUID = 383966400L;

	public static PersoneArticoliAction instance() {
		return (PersoneArticoliAction) Component.getInstance(PersoneArticoliAction.class, ScopeType.CONVERSATION);
	}

	public List<PersoneArticoli> getPersoneArticoli(Articoli art) throws PhiException{
		if(art == null)
			return null;

		getEqual().put("isActive", true);
		getEqual().put("articoli", art);

		List<PersoneArticoli> ret = select();

		return ret;
	}

	public List<PersoneArticoli> getPersoneArticoliProvv(Articoli art) throws PhiException{
		if(art == null)
			return null;

		cleanRestrictions();
		getEqual().put("isActive", true);
		getEqual().put("articoli.provvedimento.internalId", art.getProvvedimento().getInternalId());

		List<PersoneArticoli> ret = select();

		return ret;
	}

	@SuppressWarnings("unchecked")
	public void injectPersoneArticoliProvvedimento(Articoli art) throws PhiException{
		if(art == null)
			return;

		//injectList(getPersoneArticoliProvv(art), "PersoneArticoliProvvList");

		cleanRestrictions();

		getSelect().add("person.internalId");
		getSelect().add("person.name.giv");
		getSelect().add("person.name.fam");
		getSelect().add("person.internalId");
		getSelect().add("person.birthPlace.cty");
		getSelect().add("person.birthPlace.cpa");
		getSelect().add("person.countryOfBirth.langIt");
		getSelect().add("person.birthTime");
		getSelect().add("tipoDoc");
		getSelect().add("rilasciatoIl");
		getSelect().add("numeroDoc");
		getSelect().add("rilasciatoDa");
		getSelect().add("articoli.code.oid");
		getSelect().add("articoli.violazione");
		getSelect().add("tesserino");
		getSelect().add("docObbligatoria");
		getSelect().add("altroBl");
		getSelect().add("altroTxt");

		getEqual().put("isActive", true);
		getEqual().put("articoli.provvedimento.internalId", art.getProvvedimento().getInternalId());

		List<HashMap<String, Object>> ret = select();
		List<HashMap<String, Object>> personeArticoliProvvList = new ArrayList<HashMap<String, Object>>();
		Set<Long> personIds = new HashSet<Long>();
		
		for(HashMap<String, Object> personArticolo : ret){
			Long personId = (Long) ((HashMap<String, Object>)personArticolo.get("person")).get("internalId");
			
			String articoloOid = (String)((HashMap<String, Object>)((HashMap<String, Object>)personArticolo.get("articoli")).get("code")).get("oid");
			String[] articoloParts = articoloOid.split("\\.");
			((HashMap<String, Object>)((HashMap<String, Object>)personArticolo.get("articoli")).get("code")).put("displayName", articoloParts[2]);
			
			if(personIds.add(personId)){
				personeArticoliProvvList.add(personArticolo);
			}else{
				for(HashMap<String, Object> existingPerson : personeArticoliProvvList){
				Long existingPersonId = (Long) ((HashMap<String, Object>)existingPerson.get("person")).get("internalId");;
					if(existingPersonId.equals(personId)){
						existingPerson.put("otherArticolo",personArticolo.get("articoli"));
					}
				}
			}
		}

		injectList(personeArticoliProvvList, "PersoneArticoliProvvList");
	}

	public void copyFromOtherArticolo() throws PhiException{
		PersoneArticoli toCopy = null;
		PersoneArticoli newPersoneArticoli = getEntity();

		cleanRestrictions();
		getEqual().put("isActive", true);
		getEqual().put("person.internalId", newPersoneArticoli.getPerson()!=null?newPersoneArticoli.getPerson().getInternalId():-1);

		List<PersoneArticoli> ret = select();
		if(ret!=null && !ret.isEmpty()){
			toCopy = ret.get(0);
		}else{
			return;
		}

		newPersoneArticoli.setAddr(toCopy.getAddr()!=null?toCopy.getAddr().cloneAd():null);
		newPersoneArticoli.setAltroBl(toCopy.getAltroBl());
		newPersoneArticoli.setAltroTxt(toCopy.getAltroTxt());
		newPersoneArticoli.setDocObbligatoria(toCopy.getDocObbligatoria());
		newPersoneArticoli.setRilasciatoDa(toCopy.getRilasciatoDa());
		newPersoneArticoli.setRilasciatoIl(toCopy.getRilasciatoIl());
		newPersoneArticoli.setTesserino(toCopy.getTesserino());
		newPersoneArticoli.setTipoDoc(toCopy.getTipoDoc());

	}
	
	public void injectLists(List<Articoli> articoli) throws PhiException{
		if(articoli==null || articoli.isEmpty())
			return;
		
		cleanRestrictions();
		getEqual().put("isActive", true);
		
		for(Articoli art : articoli){
			if(art==null)
				continue;
			
			getEqual().put("articoli", art);
			
			List<PersoneArticoli> personeArticoli = select();
			
			String listName = "PersoneArticoli" + art.getCode().getDisplayName().substring(0,2) +  "List";
			
			if(personeArticoli==null || personeArticoli.isEmpty())
				injectEmptyList(listName);
			
			injectList(personeArticoli, listName);
		}
	}
	
	public void injectArticolo(List<Articoli> articoli, String artName){
		if(articoli==null || articoli.isEmpty())
			return;
		
		ArticoliAction artAct = ArticoliAction.instance();
		
		for(Articoli art : articoli){
			if(art==null)
				continue;
			
			if(artName.equals(art.getCode().getDisplayName().substring(0,2)))
				artAct.inject(art);
		}
	}
	
	public boolean isArticoloContained(List<Articoli> articoli, String artName){
		if(articoli==null || articoli.isEmpty())
			return false;

		boolean present = false;
		
		for(Articoli art : articoli){
			if(art==null)
				continue;
			
			if(artName.equals(art.getCode().getDisplayName().substring(0,2))){
				present = true;
				break;
			}
		}
		
		return present;
	}

	public void multiDeletePersoneArticoli(Articoli art) throws PhiException{
		if(art == null)
			return;

		cleanRestrictions();
		getEqual().put("articoli", art);

		List<PersoneArticoli> ret = select();

		if(ret==null || ret.isEmpty())
			return;
		
		for(PersoneArticoli p : ret){
			p.setArticoli(null);
			delete();
		}

	}
}