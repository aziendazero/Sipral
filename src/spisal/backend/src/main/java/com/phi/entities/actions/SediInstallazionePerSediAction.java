package com.phi.entities.actions;

import java.util.List;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.actions.BaseAction;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SediInstallazionePerSediAction")
@Scope(ScopeType.CONVERSATION)
public class SediInstallazionePerSediAction extends BaseAction<SediInstallazione, Long> {

	private static final long serialVersionUID = 510349294L;
    //private static final Logger log = Logger.getLogger(SediInstallazionePerSediAction.class);

    public SediInstallazionePerSediAction() {
        super();
        conversationName = "SediInstallazionePerSedi";
    } 
    
	public static SediInstallazionePerSediAction instance() {
		return (SediInstallazionePerSediAction) Component.getInstance(SediInstallazionePerSediAction.class, ScopeType.CONVERSATION);
	}

	public Boolean isDeletable(SediInstallazione si) throws PhiException {
		if (si!=null && si.getIsActive()) {
			/* Applico questa tecnica becera perché Impianto è un abstract e non mi viene in mente altro */
			ImpiantoCheckAction imp = ImpiantoCheckAction.instance();
			imp.getEqual().put("sedeInstallazione.internalId", si.getInternalId());
			
			List<Impianto> impList = imp.list();
			imp.cleanRestrictions();
			
			if (impList != null && impList.size()>0) 
				return false;
		}

		return true;
	}
	
}