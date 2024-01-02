package com.phi.entities.actions;

import java.util.HashMap;
import java.util.List;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.actions.BaseAction;
import com.phi.parameters.ParameterManager;
import com.phi.security.UserBean;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("InfortuniExtAction")
@Scope(ScopeType.CONVERSATION)
public class InfortuniExtAction extends BaseAction<InfortuniExt, Long> {

	private static final long serialVersionUID = 627284171L;

	public static InfortuniExtAction instance() {
		return (InfortuniExtAction) Component.getInstance(InfortuniExtAction.class, ScopeType.CONVERSATION);
	}



	public void resetAddress(String code){

		if(code.equals("Other")){

			InfortuniExt ie = getEntity();
			ie.setAddr(new AD("","","","","","","","","","")); 

			ie.setLatitudine("");
			ie.setLongitudine("");


		}		

	}

	public boolean isUlssEnabledToInformo(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter informoVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.modinformo");

		if (informoVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(informoVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}

	public boolean isUlssEnabledToInformoFilter(){
		boolean ret = false;

		CodeValueParameter informoVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.modinformo");

		if (informoVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		List<Long> sdlocIds = UserBean.instance().getSdLocs();
		for(Long sdlocId : sdlocIds){
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(informoVisible, sdlocId);
			String value = evaluatedParameter.get("visible").toString();

			ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
			if(ret)
				break;
		}
		return ret;
	}


	public boolean isUlssEnabledToInfProf(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter infProfVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.infprof");

		if (infProfVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(infProfVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}

	public boolean isUlssEnabledToInfProfFilter(){
		boolean ret = false;

		CodeValueParameter infProfVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.infprof");

		if (infProfVisible==null)
			return ret;

		List<Long> sdlocIds = UserBean.instance().getSdLocs();
		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		for(Long sdlocId : sdlocIds){
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(infProfVisible, sdlocId);
			String value = evaluatedParameter.get("visible").toString();

			ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
			if(ret)
				break;
		}
		return ret;
	}

	public boolean isUlssEnabledToNotifica(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter notifDecessoVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.notifdecesso");

		if (notifDecessoVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(notifDecessoVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}

	public boolean isUlssEnabledToNotificaFilter(){
		boolean ret = false;

		CodeValueParameter notifDecessoVisible = ca.get(CodeValueParameter.class, "p.home.vp_pratica_workaccident.notifdecesso");

		if (notifDecessoVisible==null)
			return ret;

		List<Long> sdlocIds = UserBean.instance().getSdLocs();
		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		for(Long sdlocId : sdlocIds){
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(notifDecessoVisible, sdlocId);
			String value = evaluatedParameter.get("visible").toString();

			ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
			if(ret)
				break;
		}
		return ret;
	}
	

	public String computeClass(InfortuniExt infortuniExt){
		String rtn = "";
		
		if (infortuniExt.getModInformo()!=null &&
				"NO".equals(infortuniExt.getModInformo().getCode())){
				rtn = "layoutRequired";
		}
		
		return rtn;
	}

}