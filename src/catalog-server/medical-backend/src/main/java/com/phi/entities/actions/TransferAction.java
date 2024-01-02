package com.phi.entities.actions;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.baseEntity.Transfer;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("TransferAction")
@Scope(ScopeType.CONVERSATION)
public class TransferAction extends BaseAction<Transfer, Long> {

	private static final long serialVersionUID = 2142113924L;

	public static TransferAction instance() {
		return (TransferAction) Component.getInstance(TransferAction.class,
				ScopeType.CONVERSATION);
	}
	public boolean onlyPreRocovery(List<CodeValue> encSdl){
		boolean noAdt= false;
		if(encSdl!=null){
			if(encSdl.size()==0 || (encSdl.size()==1 && encSdl.get(0).getCode().equals("PRE"))){
				noAdt = true;
			}
		}
		return noAdt;
	}
	
	public void setEncounterCodeSdlocTo(PatientEncounter patEnc,List<CodeValue> encSdl){
		if(encSdl!=null && patEnc!=null){
			if ( encSdl.size()> 1){
				for(CodeValue cv: encSdl){
					if(!cv.getCode().equals("PRE")){
						patEnc.setCode(cv);
					}
				}
			}
			else if(encSdl.size()== 1 && !encSdl.get(0).getCode().equals("PRE")){
				patEnc.setCode(encSdl.get(0));
			}
		}
	}

}