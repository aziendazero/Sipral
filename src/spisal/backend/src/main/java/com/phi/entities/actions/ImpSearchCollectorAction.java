package com.phi.entities.actions;

import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ImpSearchCollectorAction")
@Scope(ScopeType.CONVERSATION)
public class ImpSearchCollectorAction extends BaseAction<ImpSearchCollector, Long> {

	private static final long serialVersionUID = 154177104L;
    private static final Logger log = Logger.getLogger(ImpSearchCollectorAction.class); 

	public static ImpSearchCollectorAction instance() {
		return (ImpSearchCollectorAction) Component.getInstance(ImpSearchCollectorAction.class, ScopeType.CONVERSATION);
	}

	public ImpSearchCollector copyFrom(Impianto imp) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;
			
			CodeValuePhi codeCv = imp.getCode();
			String code = codeCv.getCode();

			ImpSearchCollector coll = new ImpSearchCollector();
			
			coll.setCode(codeCv);
			coll.setSigla(imp.getSigla());
			coll.setMatricola(imp.getMatricola());
			coll.setAnno(imp.getAnno());
			
			if (imp.getSedeInstallazione()!=null)
				coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			if (imp.getSedeAddebito()!=null)
				coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());
			*/
			
			// I0070276
			if (imp.getSedi()!=null)
				coll.setDenominazioneSA(imp.getSedi().getDenominazioneUnitaLocale());
			
			if(imp.getIndirizzoSped()!=null)
				coll.setDenominazioneIS(imp.getIndirizzoSped().getDenominazione());
						
			if (!"".equals(code)){
				
				//Apparecchi a pressione
				if ("01".equals(code))
					coll.setNumeroFabbrica(((ImpPress)imp).getNumeroFabbrica());
				
				//Impianti di riscaldamento
				//else if ("02".equals(code))
				
				//Ascensori e montacarichi
				//else if ("03".equals(code))
			
				//Apparecchi di sollevamento	
				else if ("04".equals(code))
					coll.setSubTypeSoll(((ImpSoll)imp).getSubTypeSoll());
			
				//Impianti elettrici	
				else if ("05".equals(code))
					coll.setSubTypeTerra(((ImpTerra)imp).getSubTypeTerra());
			}
			
			ca.persist(coll);
			return coll;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
}