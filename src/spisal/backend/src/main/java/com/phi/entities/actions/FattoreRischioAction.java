package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.AnamnesisMdl;
import com.phi.entities.baseEntity.ExpositionCoefficients;
import com.phi.entities.baseEntity.FattoreRischio;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("FattoreRischioAction")
@Scope(ScopeType.CONVERSATION)
public class FattoreRischioAction extends BaseAction<FattoreRischio, Long> {

	private static final long serialVersionUID = 813991196L;

	public static FattoreRischioAction instance() {
		return (FattoreRischioAction) Component.getInstance(FattoreRischioAction.class, ScopeType.CONVERSATION);
	}

	public boolean hasLA(List<FattoreRischio> lst){
		boolean rtn = false;
		if(lst!=null){
			for(FattoreRischio fatt : lst){
				if(fatt.getType()!=null && "LA".equals(fatt.getType().getCode()))
					return true;
			}
		}
		
		return rtn;
	}
	
	public boolean hasPrev(List<FattoreRischio> lst){
		boolean rtn = false;
		if(lst!=null){
			for(FattoreRischio fatt : lst){
				if(fatt.getCausa()!=null && "C1".equals(fatt.getCausa().getCode()))
					return true;
			}
		}
		
		return rtn;
	}
	
	public void manageMalattia(CodeValueIcd9 icd9, List<FattoreRischio> lst, MalattiaProfessionale mp) throws PhiException{
		if(lst==null)
			lst=new ArrayList<FattoreRischio>();
		
		if(icd9==null)
			return;
		
		String codiceMalattia = icd9.getCode();
		if(codiceMalattia.length()>=3){
			codiceMalattia=codiceMalattia.substring(0, 3);
		}else {
			return;
		}
		List<String> allowedDomains = new ArrayList<String>(); 
		Query q1 = ca.createQuery("SELECT m.codiceAgente FROM MalattiaAgente m WHERE m.codiceMalattia = :code");
		q1.setParameter("code", codiceMalattia);
		List<String> r1 = q1.getResultList();
		if(r1!=null && !r1.isEmpty()){
			allowedDomains.add("phidic.spisal.frisk.agmal_V0");
			for(String codiceAgente : r1){
				String parziale = codiceAgente;
				String id = "phidic.spisal.frisk.agmal."+parziale.toLowerCase()+"_V0";
				allowedDomains.add(id);
				while(parziale.indexOf(".")>0){
					parziale = parziale.substring(0, parziale.lastIndexOf("."));
					id = "phidic.spisal.frisk.agmal."+parziale.toLowerCase()+"_V0";
					if(!allowedDomains.contains(id))
						allowedDomains.add(id);
					
				}
			}
		}
		getTemporary().put("allowedDomains", allowedDomains);
	}
	
	public void manageMalattia(CodeValuePhi inail, List<FattoreRischio> lst, MalattiaProfessionale mp) throws PhiException{
		if(inail==null || inail.getParent()==null)
			return;
		
		if(lst==null)
			lst=new ArrayList<FattoreRischio>();
		
		String codiceAgente = inail.getParent().getCode();
		boolean found = false;
		for(FattoreRischio f : lst){
			if(f.getCode()!=null && f.getCode().getCode().equals(codiceAgente)){
				found=true;
				break;
			}
				
		}
		if(!found){
			Vocabularies voc = VocabulariesImpl.instance();
			CodeValuePhi agente = (CodeValuePhi)inail.getParent();
			if(agente!=null){
				FattoreRischio newFattore = new FattoreRischio();
				newFattore.setCode(agente);
				newFattore.setType((CodeValuePhi)voc.getCodeValue("PHIDIC", "TipiFattoriRischio", "LA", "C"));
				newFattore.setCausa((CodeValuePhi)voc.getCodeValue("PHIDIC", "Causa", "C1", "C"));
				mp.addFattoreRischio(newFattore);
				newFattore.setMalattiaProfessionale(mp);
				entity=newFattore;
				create();
				lst.add(newFattore);
				injectList(lst);
				entity=null;
			}
		}
	}

	public void presetMultiplier() throws PhiException{
		FattoreRischio f = getEntity();
		if(f!=null && f.getExpType()!=null){
			ExpositionCoefficientsAction ecAction = ExpositionCoefficientsAction.instance();
			ecAction.cleanRestrictions();
			ecAction.getEqual().put("code", f.getCoefficient());
			ecAction.getEqual().put("isActive", true);
			ecAction.getIsNull().put("multiplier", false);
			List<ExpositionCoefficients> lst = ecAction.select();
			if(lst!=null && !lst.isEmpty()){
				ExpositionCoefficients coeff = lst.get(0);
				if(coeff!=null){
					f.setMultiplier(coeff.getMultiplier());
				}
			}
		}
	}

	public void presetUnit() throws DictionaryException, PhiException {
		FattoreRischio f = getEntity();
		if(f!=null){
			f.setUnitaMisura(null);
			if(f.getExpType()!=null && "CVM".equals(f.getExpType().getCode())){
				setCodeValue("unitaMisura", "PHIDIC", "unit of measure", "ppm");
			}else if(f.getExpType()!=null && "AMI".equals(f.getExpType().getCode())){
				setCodeValue("unitaMisura", "PHIDIC", "unit of measure", "ff/cc");
			}
			f.setCoefficient(null);
			f.setMultiplier(null);
			f.setFrequenza(null);
			f.setIntensityQuant(null);
		}
	}
	
	public void presetYears(){
		AnamnesisMdlAction aAction = AnamnesisMdlAction.instance();
		AnamnesisMdl a = aAction.getEntity();
		FattoreRischio f = getEntity();
		if(a!=null && f!=null){
			Calendar cal = Calendar.getInstance();
			if(a.getStartValidity()!=null){
				cal.setTime(a.getStartValidity());
				f.setYearStart(cal.get(Calendar.YEAR));
			}
			if(a.getEndValidity()!=null){
				cal.setTime(a.getEndValidity());
				f.setYearStop(cal.get(Calendar.YEAR));
			}
		}
	}

	public void calculateSigRisk(){
		FattoreRischio f = getEntity();
		if(f!=null && f.getSigarette()!=null && f.getStartValidity()!=null && f.getEndValidity()!=null){
			long difference = f.getEndValidity().getTime()-f.getStartValidity().getTime();
			long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
			
			Double sigRisk = ((double)(days*f.getSigarette()))/20/365;
			
			double toRet = Math.round(sigRisk * 10);
			toRet = toRet/10;
			f.setSigRisk(toRet);
		}
	}
	
	public void calculateIntensity() throws DictionaryException, PhiException{
		MedicinaLavoro m = MedicinaLavoroAction.instance().getEntity();
		FattoreRischio f = getEntity();
		
		//il totale lo calcolo solo se il sottotipo è 02
		if(m!=null && m.getType()!=null && "02".equals(m.getType().getCode())){
			f.setIntensityQuant(null);
			if(f!=null && f.getYearStart()!=null && f.getYearStop()!=null && f.getMultiplier()!=null && f.getExpType()!=null){
				int difference = f.getYearStop()-f.getYearStart()+1;
				if("CVM".equals(f.getExpType().getCode())){
					Double intensity = difference*f.getMultiplier();
					f.setIntensityQuant(intensity);
				}else if("AMI".equals(f.getExpType().getCode()) && f.getFrequenza()!=null){
					Double intensity = difference*f.getMultiplier()*f.getFrequenza();
					f.setIntensityQuant(intensity);
				}
			}
		}		
	}

	public FattoreRischio copy(FattoreRischio oldFa){
		FattoreRischio newFa = null;
		if(oldFa!=null){
			newFa=new FattoreRischio();
			newFa.setCausa(oldFa.getCausa());
			newFa.setCode(oldFa.getCode());
			newFa.setCoefficient(oldFa.getCoefficient());
			newFa.setEndValidity(oldFa.getEndValidity());
			newFa.setExpType(oldFa.getExpType());
			newFa.setExt(oldFa.getExt());
			newFa.setFrequenza(oldFa.getFrequenza());
			newFa.setIntensityQual(oldFa.getIntensityQual());
			newFa.setIntensityQuant(oldFa.getIntensityQuant());
			newFa.setIntUnit(oldFa.getIntUnit());
			newFa.setMultiplier(oldFa.getMultiplier());
			newFa.setSigarette(oldFa.getSigarette());
			newFa.setSigRisk(oldFa.getSigRisk());
			newFa.setStartValidity(oldFa.getStartValidity());
			newFa.setType(oldFa.getType());
			newFa.setUnitaMisura(oldFa.getUnitaMisura());
			newFa.setUnitaPrefisso(oldFa.getUnitaPrefisso());
			newFa.setYearStart(oldFa.getYearStart());
			newFa.setYearStop(oldFa.getYearStop());
		}
		return newFa;
	}

	public void writeOver(FattoreRischio tmp){
		FattoreRischio cur = getEntity();
		if(cur!=null && tmp!=null){
			cur.setCausa(tmp.getCausa());
			cur.setCode(tmp.getCode());
			cur.setCoefficient(tmp.getCoefficient());
			cur.setEndValidity(tmp.getEndValidity());
			cur.setExpType(tmp.getExpType());
			cur.setExt(tmp.getExt());
			cur.setFrequenza(tmp.getFrequenza());
			cur.setIntensityQual(tmp.getIntensityQual());
			cur.setIntensityQuant(tmp.getIntensityQuant());
			cur.setIntUnit(tmp.getIntUnit());
			cur.setMultiplier(tmp.getMultiplier());
			cur.setSigarette(tmp.getSigarette());
			cur.setSigRisk(tmp.getSigRisk());
			cur.setStartValidity(tmp.getStartValidity());
			cur.setType(tmp.getType());
			cur.setUnitaMisura(tmp.getUnitaMisura());
			cur.setUnitaPrefisso(tmp.getUnitaPrefisso());
			cur.setYearStart(tmp.getYearStart());
			cur.setYearStop(tmp.getYearStop());
		}
	}
	
	public String getDurata(FattoreRischio f){
		String rtn = "";
		if(f!=null && f.getEndValidity()!=null && f.getStartValidity()!=null){
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.setTime(f.getEndValidity());
			int yearEnd = cal.get(Calendar.YEAR);
			cal.setTime(f.getStartValidity());
			int yearStart = cal.get(Calendar.YEAR);
			int durata = (yearEnd-yearStart+1);
			rtn+=durata;
		}
		return rtn;
	}
	
	public String getDurataInt(FattoreRischio f){
		String rtn = "";
		if(f!=null && f.getYearStart()!=null && f.getYearStop()!=null){

			int durata = (f.getYearStop()-f.getYearStart()+1);
			rtn+=durata;
		}
		return rtn;
	}
	
	/* Precedente binding: empty FattoreRischio.expType ? FattoreRischio.code : FattoreRischio.expType
	   Concateno eventuale rischio da Legge 81	
		*/
	public String getRischio(FattoreRischio fr) {
		String ret = "";
		
		if (fr==null)
			return ret;
		
		CodeValuePhi code = fr.getCode();
		CodeValuePhi expType = fr.getExpType();
		
		CodeValueLaw legge81 = fr.getLegge81code();		
		
		if (expType==null && code!=null)
			ret += code.getCode();
		
		else if (expType!=null)
			ret += expType.getCurrentTranslation();
			
		if (legge81!=null) {
			if (ret!="")
				ret += " / ";
			
			ret += legge81.getCurrentTranslation();
		}
	
		return ret;		
	}
	
}