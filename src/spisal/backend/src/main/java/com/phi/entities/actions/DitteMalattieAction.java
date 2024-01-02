package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.DitteMalattie;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.dataTypes.CodeValueAteco;

@BypassInterceptors
@Name("DitteMalattieAction")
@Scope(ScopeType.CONVERSATION)
public class DitteMalattieAction extends BaseAction<DitteMalattie, Long> {

	private static final long serialVersionUID = 661143284L;

	public static DitteMalattieAction instance() {
		return (DitteMalattieAction) Component.getInstance(DitteMalattieAction.class, ScopeType.CONVERSATION);
	}

	public String getAttivita(DitteMalattie d) throws PhiException{
		if(d!=null){
			AttivitaIstatAction aAction = AttivitaIstatAction.instance();
			aAction.cleanRestrictions();
			aAction.getEqual().put("isActive",true);
			if(d.getPersoneGiuridiche()!=null){
				aAction.getEqual().put("personeGiuridiche", d.getPersoneGiuridiche());
				aAction.getEqual().put("importanza.code", "P");
				List<AttivitaIstat> list = aAction.select();
				if(list!=null && !list.isEmpty()){
					AttivitaIstat a = list.get(0);
					if(a.getCode()!=null){
						return (a.getCode().getCode()+" - "+a.getCode().getDescription());
					}
				}
			}
		}
		
		return "";
	}
	
	public String getCompSpec(DitteMalattie d) throws PhiException{
		String rtn = "";
		if(d!=null){
			PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
			pgs.setPersonaGiuridica(d.getPersoneGiuridiche());
			pgs.setSede(d.getSedi());
			rtn = PersonaGiuridicaSedeAction.instance().getAtecoWithSpecAsString(pgs);
		}
		
		return rtn;
	}
	
	public boolean hasPrincipale(List<DitteMalattie> lst){
		boolean rtn = false;
		if(lst!=null){
			for(DitteMalattie ditta : lst){
				if(ditta.getPrincipale())
					return true;
			}
		}
		
		return rtn;
	}
	
	public List<SelectItem> listDitte(List<DitteMalattie> lst){
		List<SelectItem> rtn = new ArrayList<SelectItem>();
		
		if(lst!=null && !lst.isEmpty()){
			for(DitteMalattie d : lst){
				if(d.getSedi()!=null){
					SelectItem s = new SelectItem();
					s.setValue(d.getSedi().getInternalId());
					if(d.getSedi().getAddr()!=null){
						s.setLabel(d.getSedi().getDenominazioneUnitaLocale()+ " - " + d.getSedi().getAddr().toString());
					}else{
						s.setLabel(d.getSedi().getDenominazioneUnitaLocale());
					}
					
					rtn.add(s);
				}
			}
		}
		
		return rtn;
	}
}