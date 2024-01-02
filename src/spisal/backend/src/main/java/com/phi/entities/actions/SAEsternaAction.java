package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;

@BypassInterceptors
@Name("SAEsternaAction")
@Scope(ScopeType.CONVERSATION)
public class SAEsternaAction extends BaseAction<Sedi, Long> {

	/**
	 * Sedi addebito esterne (SAE)
	 */
	private static final long serialVersionUID = -3785193049469055005L;

	private static final Logger log = Logger.getLogger(SAEsternaAction.class);

	public SAEsternaAction() {
		super();
		conversationName = "SAEsterna";
	} 


	public static SAEsternaAction instance() {
		return (SAEsternaAction) Component.getInstance(SAEsternaAction.class, ScopeType.CONVERSATION);
	}

	public boolean isUnlinkable(PersoneGiuridiche pg, Sedi sae) throws PhiException{
		if(sae==null)
			return true;

		ImpiantoCheckAction imp = ImpiantoCheckAction.instance();
		imp.getEqual().put("sedi.internalId", sae.getInternalId());
		
		List<Impianto> impiantiAssociati = imp.list();
		imp.cleanRestrictions();

		if(impiantiAssociati==null || impiantiAssociati.isEmpty())
			return true;

		//Se la SAE è associata ad un impianto della persona giuridica non è cancellabile
		for(Impianto impianto : impiantiAssociati){
			PersoneGiuridiche dittaProprietaria = impianto.getSedeInstallazione().getSede().getPersonaGiuridica();
			if(dittaProprietaria.getInternalId()==pg.getInternalId())
				return false;
		}
		return true;
	}

}