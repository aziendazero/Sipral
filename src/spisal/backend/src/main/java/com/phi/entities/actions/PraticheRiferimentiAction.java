package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueAteco;

@BypassInterceptors
@Name("PraticheRiferimentiAction")
@Scope(ScopeType.CONVERSATION)
public class PraticheRiferimentiAction extends BaseAction<PraticheRiferimenti, Long> {

	private static final long serialVersionUID = 69405920L;

	public static PraticheRiferimentiAction instance() {
		return (PraticheRiferimentiAction) Component.getInstance(PraticheRiferimentiAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Same as:
	 * 
	 * not empty Sedi ? PraticheRiferimenti.setUbicazioneAddr(ProtocolloAction.copyAddr(Sedi.addr)) : (not empty Cantiere ?  PraticheRiferimenti.setUbicazioneAddr(ProtocolloAction.copyAddr(Cantiere.addr)) : '')
	 * (not empty PersoneGiuridiche and empty Sedi) ? PraticheRiferimenti.setUbicazioneAddr(ProtocolloAction.copyAddr(PersoneGiuridicheAction.getAddrSedePrincipale(PersoneGiuridiche))) : ''
	 * 
	 * but sometimes:
	 * Not a Valid Method Expression: ${(not empty PersoneGiuridiche and empty Sedi) ? Protocollo.setUbicazioneAddr(ProtocolloAction.copyAddr(PersoneGiuridicheAction.getAddrSedePrincipale(PersoneGiuridiche))) : ''}
	 * see: http://support.insielmercato.it/mantis/view.php?id=33371
	 * @param sedi
	 * @param cantiere
	 * @param personeGiuridiche
	 */
	public void setUbicazioneAddr(Sedi sedi, Cantiere cantiere, PersoneGiuridiche personeGiuridiche ) {
		getEntity();
		
		if (sedi != null && sedi.getAddr() != null) {
			entity.setUbicazioneAddr(sedi.getAddr().cloneAd());
		} else if (cantiere != null && cantiere.getAddr() != null) {
			entity.setUbicazioneAddr(cantiere.getAddr().cloneAd());
		} else if (personeGiuridiche != null) {
			AD addrSedePrincipale = PersoneGiuridicheAction.instance().getAddrSedePrincipale(personeGiuridiche);
			if (addrSedePrincipale != null) {
				entity.setUbicazioneAddr(addrSedePrincipale.cloneAd());
			}
		}
	}	
	
	public CodeValueAteco copyAteco(PersoneGiuridiche p, Sedi s)
			throws PhiException {
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		return aAction.getImportantAteco(p, s);
	}
	
}