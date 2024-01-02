package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.AcquisizioneInformazioni;

@BypassInterceptors
@Name("AcquisizioneInformazioniAction")
@Scope(ScopeType.CONVERSATION)
public class AcquisizioneInformazioniAction extends BaseAction<AcquisizioneInformazioni, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5635893798955159813L;
	
	public static AcquisizioneInformazioniAction instance() {
        return (AcquisizioneInformazioniAction) Component.getInstance(AccessoAttiAction.class, ScopeType.CONVERSATION);
    }
}