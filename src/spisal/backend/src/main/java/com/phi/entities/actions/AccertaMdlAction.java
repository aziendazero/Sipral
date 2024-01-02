package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.PrestMdl;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AccertaMdlAction")
@Scope(ScopeType.CONVERSATION)
public class AccertaMdlAction extends BaseAction<AccertaMdl, Long> {

	private static final long serialVersionUID = 427629119L;
    private static final Logger log = Logger.getLogger(AccertaMdlAction.class);

	public static AccertaMdlAction instance() {
		return (AccertaMdlAction) Component.getInstance(AccertaMdlAction.class, ScopeType.CONVERSATION);
	}

	public void initList(){
		PrestMdlAction prestMdlAction = PrestMdlAction.instance();
		prestMdlAction.injectList(new ArrayList<PrestMdl>());
	}
	
	public String getPrestazioniStr(Attivita att){
		try {
			AccertaMdl acc = null;
			String ret = "";
			if (att!=null && att.getAccertaMdl()!=null && att.getAccertaMdl().size()>0)
				acc = att.getAccertaMdl().get(0);
			
			if (acc!=null && acc.getPrestMdl()!=null && acc.getPrestMdl().size()>0){
				for (PrestMdl prestMdl : acc.getPrestMdl()){
					CodeValuePhi prestCode = prestMdl.getPrest();
					if (prestCode!=null) {
						if (!"".equals(ret))
							ret += " - ";
						
						ret += "(" + prestCode.getCode() + ") " + prestCode.getDisplayName();
					}
				}
			}
			
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public List<String> getPrestForTemplate(Attivita att){
		
		List<String> ret = new ArrayList<String>();
		
		try {
			AccertaMdl acc = null;

			if (att!=null && att.getAccertaMdl()!=null && att.getAccertaMdl().size()>0)
				acc = att.getAccertaMdl().get(0);
			
			if (acc!=null && acc.getPrestMdl()!=null && acc.getPrestMdl().size()>0){
				for (PrestMdl prestMdl : acc.getPrestMdl()){
					CodeValuePhi prestCode = prestMdl.getPrest();
					if (prestCode!=null) {
						
						ret.add(prestCode.getDisplayName());
					}
				}
			}
			
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}		
	}
}