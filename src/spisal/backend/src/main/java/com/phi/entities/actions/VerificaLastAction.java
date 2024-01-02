package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("VerificaLastAction")
@Scope(ScopeType.CONVERSATION)
public class VerificaLastAction extends BaseAction<VerificaImp, Long> {

	private static final long serialVersionUID = -3078413394774124946L;
	private static final Logger log = Logger.getLogger(VerificaImpAction.class); 


	public static VerificaLastAction instance() {
		return (VerificaLastAction) Component.getInstance(VerificaLastAction.class, ScopeType.CONVERSATION);
	}
	
	public VerificaLastAction() {
		super();
		conversationName = "VerificaLast";
	}
	
	/* Data in input una lista di verifiche, injetta come "VerificaLast" la più recente in stato validato o fatturato 
	 * (indipendentemente dal fatto che si tratti di una verifica interna o di una verifica esterna) */
	public void injectLast(List<VerificaImp> verificaImpList) {
		try {
			VerificaImp verificaImp = null;
			
			if (verificaImpList!=null){
				
				for (VerificaImp vi : verificaImpList){
					if (vi!=null){
						if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
						//if (vi.getData()!=null){
							if ((verificaImp==null) || (verificaImp.getData().before(vi.getData())))
								verificaImp=vi;
						}
					}
				}
			}
			
			if (verificaImp!=null)
				this.inject(verificaImp);
			
			else 
				this.eject();
					
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Data in input una verifica, risale alla lista delle verifiche dell'impianto associato e injetta come "VerificaLast" la più recente in stato validato o fatturato 
	 * (indipendentemente dal fatto che si tratti di una verifica interna o di una verifica esterna) */
	public void injectLastSibling(VerificaImp ver) {
		try {
			List<VerificaImp> verificaImpList = new ArrayList<VerificaImp>();
			//VerificaImp ver = getEntity();
			
			if (ver==null)
				return; 
			
			if (ver.getImpPress()!=null)
				verificaImpList = ver.getImpPress().getVerificaImp();
				
			else if (ver.getImpRisc()!=null)
				verificaImpList = ver.getImpRisc().getVerificaImp();
			
			else if (ver.getImpMonta()!=null)
				verificaImpList = ver.getImpMonta().getVerificaImp();
				
			else if (ver.getImpSoll()!=null)
				verificaImpList = ver.getImpSoll().getVerificaImp();
			
			else if (ver.getImpTerra()!=null)
				verificaImpList = ver.getImpTerra().getVerificaImp();
			
			if (verificaImpList!=null && verificaImpList.size()>0)
				this.injectLast(verificaImpList);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

}