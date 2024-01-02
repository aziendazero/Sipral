package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.AnamnesisMdl;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.FattoreRischio;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("AnamnesisMdlAction")
@Scope(ScopeType.CONVERSATION)
public class AnamnesisMdlAction extends BaseAction<AnamnesisMdl, Long> {

	private static final long serialVersionUID = 432711624L;

	public static AnamnesisMdlAction instance() {
		return (AnamnesisMdlAction) Component.getInstance(AnamnesisMdlAction.class, ScopeType.CONVERSATION);
	}
	
	public void copyAteco(PersoneGiuridiche p, Sedi s) throws PhiException{
		AnamnesisMdl mdl = getEntity();
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		mdl.setComparto(aAction.getImportantAteco(p, s));
	}

	public String printFattoriRischio(AnamnesisMdl a){
		MedicinaLavoro m = MedicinaLavoroAction.instance().getEntity();
		
		String rtn = "";
		if(a!=null && a.getFattoreRischio()!=null && !a.getFattoreRischio().isEmpty()){
			PhiDataModel<FattoreRischio> pdm = new PhiDataModel<FattoreRischio>(a.getFattoreRischio(), null);
			pdm.orderBy("startValidity","descending");
			if(m!=null && m.getType()!=null && "02".equals(m.getType().getCode())){
				for(FattoreRischio f : pdm.getList()){
					if(f.getCode()!=null) {
						rtn+=f.getCode().getCurrentTranslation(); 
						if(f.getExpType()!=null)
							rtn+=("("+f.getExpType().getCurrentTranslation()+")");
					} else if(f.getExpType()!=null)
						rtn+=f.getExpType().getCurrentTranslation();
						
					//Legge 81
					CodeValueLaw legge81 = f.getLegge81code();		
					if (legge81!=null) {
						if (rtn!="")
							rtn += " / ";
						
						rtn += legge81.getCurrentTranslation();
					}
					
					rtn += " dal " + f.getYearStart() + 
							" al " + (f.getYearStop()!=null?f.getYearStop():"") + 
							"<br/>";
				}
			} else {
				for(FattoreRischio f : pdm.getList()){
					if(f.getCode()!=null)
						rtn+=(f.getCode().getCurrentTranslation());
						
						//Legge 81
						CodeValueLaw legge81 = f.getLegge81code();		
						if (legge81!=null) {
							if (rtn!="")
								rtn += " / ";
							
							rtn += legge81.getCurrentTranslation();
						}
						
						rtn +=	" dal " + f.getYearStart() + 
								" al " + (f.getYearStop()!=null?f.getYearStop():"") + 
								"<br/>";
				}
			}
		}

		return rtn;
	}

	public void calculateExpositions(){
		MedicinaLavoro m = MedicinaLavoroAction.instance().getEntity();

		AnamnesisMdl a = getEntity();
		if(a!=null){
			a.setExpCVM(null);
			a.setExpAmi(null);

			//il totale lo calcolo solo se il sottotipo è 02
			if(m!=null && m.getType()!=null && "02".equals(m.getType().getCode())){
				Double expCVM = 0D;
				Double expAMI = 0D;
				if(a.getFattoreRischio()!=null && !a.getFattoreRischio().isEmpty()){
					for(FattoreRischio f : a.getFattoreRischio()){
						if(f.getExpType()!=null && f.getIntensityQuant()!=null){
							if("CVM".equals(f.getExpType().getCode())){
								expCVM+=f.getIntensityQuant();
							}else if("AMI".equals(f.getExpType().getCode())){
								expAMI+=f.getIntensityQuant();
							}
						}
					}
				}
				a.setExpCVM(expCVM);
				a.setExpAmi(expAMI);
			}
		}
	}

	public AnamnesisMdl copy(AnamnesisMdl oldAn){
		AnamnesisMdl newAn = null;
		if(oldAn!=null){
			newAn=new AnamnesisMdl();
			newAn.setComparto(oldAn.getComparto());
			newAn.setDitta(oldAn.getDitta());
			newAn.setEndValidity(oldAn.getEndValidity());
			newAn.setExpAmi(oldAn.getExpAmi());
			newAn.setExpCVM(oldAn.getExpCVM());
			newAn.setMansione(oldAn.getMansione());
			newAn.setNote(oldAn.getNote());
			newAn.setReparto(oldAn.getReparto());
			newAn.setSede(oldAn.getSede());
			newAn.setStartValidity(oldAn.getStartValidity());

			if(oldAn.getFattoreRischio()!=null && !oldAn.getFattoreRischio().isEmpty()){
				FattoreRischioAction fAction = FattoreRischioAction.instance();
				List<FattoreRischio> newFattori = new ArrayList<FattoreRischio>();
				for(FattoreRischio oldFa : oldAn.getFattoreRischio()){
					FattoreRischio newFa = fAction.copy(oldFa);
					if(newFa!=null){
						newFa.setAnamnesisMdl(newAn);
						newFattori.add(newFa);
					}
				}
				newAn.setFattoreRischio(newFattori);
			}
		}
		return newAn;
	}

	public String getMansione(AnamnesisMdl anamnesisMdl){
		String ret = "";
		
		if (anamnesisMdl==null)
			return ret;
				
		CodeValuePhi mansioneCv = anamnesisMdl.getMansioneCode();
		String mansione = anamnesisMdl.getMansione();

		if (mansione !=null && mansione != "")
			ret += mansione;
			
		if (mansioneCv!=null && mansioneCv.getCode()!=null)
			ret += " (" + mansioneCv.getCurrentTranslation() + ")";
		
		return ret;
	}
}