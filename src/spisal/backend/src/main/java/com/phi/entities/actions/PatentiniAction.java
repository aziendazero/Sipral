package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.ParereTecnico;
import com.phi.entities.baseEntity.Patentini;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

@BypassInterceptors
@Name("PatentiniAction")
@Scope(ScopeType.CONVERSATION)
public class PatentiniAction extends BaseAction<Patentini, Long> {

	private static final long serialVersionUID = 1183073516L;

	public static PatentiniAction instance() {
		return (PatentiniAction) Component.getInstance(PatentiniAction.class, ScopeType.CONVERSATION);
	}

	public void filterPerson(Person pers){
		if(pers!=null){
			getEqual().remove("person.fiscalCode");
			getEqual().remove("person.mpi");
			
			if(pers.getMpi()!=null){
				getEqual().remove("person.internalId");
				getEqual().put("person.mpi",pers.getMpi());
			}else if(pers.getFiscalCode()!=null){
				getEqual().remove("person.internalId");
				getEqual().put("person.fiscalCode",pers.getFiscalCode());
			}
			
		}
	}
	
	public void managePatentini(ParereTecnico par) throws InstantiationException, IllegalAccessException{
		String code="";
		if(par!=null && par.getPatType()!=null){
			code=par.getPatType().getCode();
		}
		par.setIdoneita(null);
		par.setDataEsame(null);
		par.setPunteggioEsame(null);
		par.setParereData(null);
		
		if("TAPAT02".equals(code)){
			getEqual().remove("matricola");
			injectEmptyList();
			eject();
			par.setTariffa("18 €");
		}else if("TAPAT01".equals(code)){
			getEqual().remove("matricola");
			inject(newEntity());
			par.setTariffa("38 €");
		}else{
			inject(newEntity());
			par.setTariffa("");
		}
	}
	
	public void savePatentini(ParereTecnico par, Person person, Patentini pat, IdataModel<Patentini> rinnovi) throws PhiException{
		FunctionsBean fun = FunctionsBean.instance();
		if(par!=null && person!=null && par.getPatType()!=null && par.getType()!=null && fun.hasCodeIn(par.getType().getCode(),"TA08")){
			
			if(fun.hasCodeIn(par.getPatType().getCode(),"TAPAT01") && pat!=null){
				create();
				pat.setPerson(person);
				if(par.getPatentini()!=null){
					for(Patentini parPat : par.getPatentini()){
						parPat.setPerson(null);
						parPat.setParereTecnico(null);
					}
					par.setPatentini(null);
				}
				par.addPatentini(pat);
				pat.setParereTecnico(par);
				pat.setPerson(person);
				
			}else if(fun.hasCodeIn(par.getPatType().getCode(),"TAPAT02")){
				eject();
				if(par.getPatentini()!=null){
					for(Patentini parPat : par.getPatentini()){
						parPat.setPerson(null);
						parPat.setParereTecnico(null);
					}
					par.setPatentini(null);
				}
				if(rinnovi!=null && rinnovi.getList()!=null){
					for(Patentini rinnovo : rinnovi.getList()){
						create(rinnovo);
						rinnovo.setPerson(person);
						par.addPatentini(rinnovo);
						rinnovo.setParereTecnico(par);
					}
				}
			}else if(fun.hasCodeIn(par.getPatType().getCode(),"TAPAT03","TAPAT04") && pat!=null){
				create();
				pat.setPerson(person);
				if(par.getPatentini()!=null){
					for(Patentini parPat : par.getPatentini()){
						parPat.setPerson(null);
						parPat.setParereTecnico(null);
					}
					par.setPatentini(null);
				}
				par.addPatentini(pat);
				pat.setParereTecnico(par);
				
				Patentini oldPat = (Patentini)getTemporary().get("oldPat");
				if(oldPat!=null)
					oldPat.setIsActive(false);
			}
		}
	}

	public void injectCopy(Patentini oldPat,String code){
		if(oldPat!=null){
			getTemporary().put("oldPat",oldPat);
			Patentini newPat = new Patentini();

			List<CodeValuePhi> gas = new ArrayList<CodeValuePhi>();
			if(oldPat.getGas()!=null)
				gas.addAll(oldPat.getGas());
			
			newPat.setGas(gas);
			newPat.setMatricola(oldPat.getMatricola());
			
			if("TAPAT04".equals(code)){
				newPat.setPatRilDate(oldPat.getPatRilDate());
				newPat.setPatExpDate(oldPat.getPatExpDate());
			}
			inject(newPat);
			
			
		}
	}
}