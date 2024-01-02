package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sintomo;
import com.phi.entities.baseEntity.VisitaMedica;

@BypassInterceptors
@Name("VisitaMedicaAction")
@Scope(ScopeType.CONVERSATION)
public class VisitaMedicaAction extends BaseAction<VisitaMedica, Long> {

	private static final long serialVersionUID = 814670805L;

	public static VisitaMedicaAction instance() {
		return (VisitaMedicaAction) Component.getInstance(VisitaMedicaAction.class, ScopeType.CONVERSATION);
	}

	public void linkUnlinkSintomi(List<Sintomo> link, List<Sintomo> unlink){
		if(entity==null)
			return;
		
		entity.setSintomo(new ArrayList<Sintomo>());
		
		if(link!=null){			
			for(Sintomo sost : link){
				entity.getSintomo().add(sost);
				sost.setVisitaMedica(entity);
			}
		}
		if(unlink!=null){
			for(Sintomo sost : unlink){
				entity.getSintomo().remove(sost);
				sost.setVisitaMedica(null);
			}
		}
	}

//	public void clona(VisitaMedica vOld) throws PersistenceException{
//		if(vOld==null)
//			return;
//		
//		VisitaMedica vNew = new VisitaMedica();
//		if(vOld.getPrestazioni()!=null && !vOld.getPrestazioni().isEmpty()){
//			List<CodeValuePhi> pNew = new ArrayList<CodeValuePhi>();
//			pNew.addAll(vOld.getPrestazioni());
//			vNew.setPrestazioni(pNew);
//		}
//		
//		vNew.setAnamFam(vOld.getAnamFam());
//		vNew.setAnamFisio(vOld.getAnamFisio());
//		vNew.setAnamProx(vOld.getAnamProx());
//		vNew.setAnamRem(vOld.getAnamRem());
//		
//		if(vOld.getSintomo()!=null && !vOld.getSintomo().isEmpty()) {
//			List<Sintomo> sNewList = new ArrayList<Sintomo>();
//			SintomoAction sAction = SintomoAction.instance();
//			for(Sintomo sOld : vOld.getSintomo()){
//				Sintomo sNew = sAction.copy(sOld);
//				sNew.setVisitaMedica(vNew);
//				if(sNew!=null)
//					sNewList.add(sNew);
//			}
//			vNew.setSintomo(sNewList);
//		}
//		
//		vNew.setMalattiaProfessionale(vOld.getMalattiaProfessionale());
//		vNew.getMalattiaProfessionale().addVisitaMedica(vNew);
//		ca.create(vNew);
//	}
}