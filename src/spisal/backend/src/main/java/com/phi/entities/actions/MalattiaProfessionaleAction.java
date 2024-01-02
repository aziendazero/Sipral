package com.phi.entities.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

@BypassInterceptors
@Name("MalattiaProfessionaleAction")
@Scope(ScopeType.CONVERSATION)
public class MalattiaProfessionaleAction extends BaseAction<MalattiaProfessionale, Long> {

	private static final long serialVersionUID = 643283868L;
	private static final Logger log = Logger.getLogger(MalattiaProfessionaleAction.class);
	
	public static MalattiaProfessionaleAction instance() {
		return (MalattiaProfessionaleAction) Component.getInstance(MalattiaProfessionaleAction.class, ScopeType.CONVERSATION);
	}

	public void copyDataFromProtocol(Protocollo prot){
		MalattiaProfessionale mal = getEntity();
		if(prot==null || mal==null)
			return;
		
		mal.setData(prot.getData());
		mal.setApplicant((CodeValuePhi)prot.getApplicant());
		mal.setFonte((CodeValuePhi)prot.getFonte());
		
		mal.setCode((CodeValuePhi)prot.getCode());
		mal.setRichiedente(prot.getRichiedente());
		mal.setRichiedenteDitta(prot.getRichiedenteDitta());
		mal.setRichiedenteSede(prot.getRichiedenteSede());
		mal.setRichiedenteInterno(prot.getRichiedenteInterno());
		mal.setRichiedenteUtente(prot.getRichiedenteUtente());
		
		if(prot.getRiferimento()!=null && "Utente".equals(prot.getRiferimento().getCode())){
			mal.setRiferimento(prot.getRiferimento());
			mal.setRiferimentoUtente(prot.getRiferimentoUtente());
		}else{
			try {
				setCodeValue("riferimento", "PHIDIC", "TargetSource", "Utente");
			} catch (Exception e) {
				log.error("Error setting default riferimento code as 'Utente'");
			}
		}
		
		if(prot.getRichiedente()==null){
			try {
				setCodeValue("richiedente", "PHIDIC", "TargetSource", "Utente");
			} catch (Exception e) {
				log.error("Error setting default richiedente code as 'Utente'");
			}
		}else if(prot.getRichiedente()!=null){
			mal.setRichiedente(prot.getRichiedente());
			
			if("Medico".equals(prot.getRichiedente().getCode())){
				mal.setRichiedenteMedico(prot.getRichiedenteMedico());
			
			}else if("Utente".equals(prot.getRichiedente().getCode())){
				mal.setRichiedenteUtente(prot.getRichiedenteUtente());
				
			}else if("Ditta".equals(prot.getRichiedente().getCode())){
				mal.setRichiedenteUtente(prot.getRichiedenteUtente());
				
			}else if("Interno".equals(prot.getRichiedente().getCode())){
				mal.setRichiedenteUtente(prot.getRichiedenteUtente());
				
			}else{
				try {
					setCodeValue("richiedente", "PHIDIC", "TargetSource", "Utente");
				} catch (Exception e) {
					log.error("Error setting default richiedente code as 'Utente'");
				}
			}
		}
		
		if(prot.getMalattiaProfessionale()!=null){
			MalattiaProfessionale protMal = prot.getMalattiaProfessionale();
			mal.setDiagCode(protMal.getDiagCode());
			mal.setDiagText(protMal.getDiagText());
			mal.setSedeText(protMal.getSedeText());
			mal.setGravita(protMal.getGravita());
		}
	}
	
	public String getRiferimenti(String riferimenti){
		try{
			MalattiaProfessionale malProf = getEntity();
			
			if (riferimenti==null || malProf==null)
				return "";
			
			if (riferimenti.equals("richiedente")){
				//Ditta, Utente, Interno, Medico, Anonimo
				CodeValue richiedente = malProf.getRichiedente();
			
				if (richiedente != null){
					String code = richiedente.getCode();
					if (code != null){
						if (code.equals("Anonimo"))
							return "";
						
						if (code.equals("Medico")){
							String ret = "";
							Physician medico = malProf.getRichiedenteMedico();
							if (medico!=null){
								ret += " " + medico.getName().getFam() + " " + medico.getName().getGiv();
							}
							return ret;
						}
					
						if (code.equals("Ditta")) {
							String ret = "";
						
							PersoneGiuridiche pg = malProf.getRichiedenteDitta();
							if (pg!=null && pg.getDenominazione()!=null){
								ret += " " + pg.getDenominazione();
							
								Sedi sede = malProf.getRichiedenteSede();
								if (sede!=null && sede.getDenominazioneUnitaLocale()!=null)
									ret += " sede: " + sede.getDenominazioneUnitaLocale();
							}

							return ret;
						}
					
						if (code.equals("Interno")) {
							String ret = "";
						
							Employee interno = malProf.getRichiedenteInterno();
							if (interno!=null){
								ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
									//+ " CF: " + interno.getFiscalCode();
							}

							return ret;
						}
					
						if (code.equals("Utente")) {
							String ret = "";
						
							Person person = malProf.getRichiedenteUtente();
							if (person!=null){
								ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
								//+ " CF: " + person.getFiscalCode();
							}

							return ret;
						}

					}
				}
			}
			
			if (riferimenti.equals("riferimento")){
				setCodeValue("riferimento", "PHIDIC", "TargetSource", "Utente");
				//Solo Utente
				CodeValue riferimento = malProf.getRiferimento();
			
				if (riferimento != null){
					String code = riferimento.getCode();
					if (code != null){
					
						if (code.equals("Utente")) {
							String ret = "";
						
							Person person = malProf.getRiferimentoUtente();
							if (person!=null){
								ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
								//+ " CF: " + person.getFiscalCode();
							}

							return ret;
						}

					}
				}
			}

			return "";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void clearRichiedente(){
		MalattiaProfessionale mal = getEntity();
		if(mal==null)
			return;
		
		mal.setRichiedenteDitta(null);
		mal.setRichiedenteInterno(null);
		mal.setRichiedenteSede(null);
		mal.setRichiedenteUtente(null);
		mal.setRichiedenteMedico(null);
		mal.setRichiedente(null);
	}
	
	public void clearRiferimento() throws DictionaryException, PhiException{
		MalattiaProfessionale mal = getEntity();
		if(mal==null)
			return;
		
		mal.setRiferimentoUtente(null);
		setCodeValue("riferimento", "PHIDIC", "TargetSource", "Utente");
	}
	
	public void clearAttuale(){
		MalattiaProfessionale mal = getEntity();
		if(mal==null)
			return;
		
		mal.setAttualeDitta(null);
		mal.setAttualeSede(null);
		PersoneGiuridicheAction.instance().eject();
		SediAction.instance().eject();
	}

	public void setMpPraticheFilter(List<Object> lst) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		ProcpraticheAction pa = ProcpraticheAction.instance();
		pa.getIn().remove("malattiaProfessionale.ditteMalattie.personeGiuridiche.internalId");
		if(pa.getEqual().get("malattiaProfessionale.ditteMalattie.personeGiuridiche.internalId")!=null){
			return;
		}
		
		if(lst==null || lst.isEmpty()){
			pa.getIn().put("malattiaProfessionale.ditteMalattie.personeGiuridiche.internalId", new Long[]{-1L});

		}else {
			FunctionsBean fun = FunctionsBean.instance();
			
			pa.getIn().put("malattiaProfessionale.ditteMalattie.personeGiuridiche.internalId", fun.propertyAsList(lst, "personeGiuridiche.internalId"));
		}
	}
	
	public void copyAteco(PersoneGiuridiche p, Sedi s) throws PhiException{
		MalattiaProfessionale mal = getEntity();
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		mal.setComparto(aAction.getImportantAteco(p, s));
	}

	public void clearDecesso(){
		MalattiaProfessionale m = getEntity();
		if(m!=null){
			m.setDeathDate(null);
			m.setDeathPlace(null);
			m.setDeathCause(null);
			m.setDeathText(null);
		}
	}

}