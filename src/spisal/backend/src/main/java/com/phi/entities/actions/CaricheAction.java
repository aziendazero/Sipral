package com.phi.entities.actions;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Cariche;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.role.SediPersone;

@BypassInterceptors
@Name("CaricheAction")
@Scope(ScopeType.CONVERSATION)
public class CaricheAction extends BaseAction<Cariche, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7201989595900410086L;
	private static final Logger log = Logger.getLogger(CaricheAction.class);


	public static CaricheAction instance() {
		return (CaricheAction) Component.getInstance(CaricheAction.class, ScopeType.CONVERSATION);
	}

	public void removeFromProvvedimenti(Cariche carica){
		try{
			List<Provvedimenti> provvedimenti = carica.getProvvedimenti();
			//ProvvedimentiAction provvedimentoAction = ProvvedimentiAction.instance();
			if(provvedimenti!=null){
				for (Provvedimenti provvedimento : provvedimenti) {
					if (provvedimento.getCarica()==carica){
						carica.removeProvvedimenti(provvedimento);
						//provvedimento.setCarica(null);
						//provvedimentoAction.create();
						//ca.flushSession();
					}
				}
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* H00225889
	 * 
	 *  PF - Persona fisica
	 *  AL - Anagrafica locale
	 *  
	 * 1. Creazione provvedimento con soggetto Ditta.
	 * 2. Selezione del destinatario del provvedimento (recuperato dalle cariche)
	 * 3. La PF destinataria del provvedimento deve essere salvata in AL 
	 * 
	 * Scenari:
	 * 
	 * La PF esite in AL:
	 * - Se l'incarico ha già il MPI non faccio NULLA
	 * 
	 * - Se l'incarico NON ha già il MPI, aggiorniamo il dettaglio dell'incarico con i dati della PF (analogamente a quanto accade 
	 *   usando il pulsante "aggiorna" nella pagina incarichi ditta)
	 * 
	 * La PF non esite in AL:
	 * - Se troviamo la PF in AUR e il risultato è univoco, la creiamo in AL e aggiorniamo il dettaglio dell'incarico con i dati della PF 
	 *   (impostare il created_by con lo username dell'operatore che sta gestendo il provvedimento)
	 * 
	 * - Se non troviamo la PF in AUR, creiamo manualmente una AL con i dati recuperati da PARIX
	 *   (impostare il created_by con lo username dell'operatore che sta gestendo il provvedimento)
	 *   
	 **/
	
	public void managePerson(Cariche carica){
		try{
			if (carica==null)
				return;
				
			if (carica.getSediPersone()==null)
				carica.setSediPersone(new SediPersone());

			String fc = carica.getSediPersone().getFiscalCode();
			
			if (fc==null || "".equals(fc))
				return;
			
			//Controllo se la PF esiste in AL
			String qry = "SELECT p FROM Person p WHERE p.isActive=1 AND p.fiscalCode = :fc";
			
			HashMap<String, Object> parameters = new HashMap<String, Object>(2);
			parameters.put("fc", fc);
			
			List<com.phi.entities.role.Person> persone = (List<com.phi.entities.role.Person>) ca.executeHQLwithParameters(qry, parameters);
			
			SediPersoneAction spa = SediPersoneAction.instance();
			com.phi.entities.role.Person persona = null;
			
			//Se esiste in AL aggiorno il dettaglio della carica con i dati della PF
			if (persone!=null && persone.size()>0){
				persona = persone.get(0);
				
				//Scrive i dati della persona sulla carica
				spa.writeOver(persona, carica.getSediPersone());
			
			//Altrimenti cerco in AUR
			} else {
				PersonAction pa = PersonAction.instance();
				persone = pa.aurSearch(null, null, fc, null, null, null, null, null, null);
				
				//Se la trovo in AUR, la creo in AL e aggiorniamo il dettaglio dell'incarico con i dati della PF
				if (persone!=null && persone.size()>0){
					persona = persone.get(0);
					
					//Scrive i dati della persona sulla carica
					spa.writeOver(persona, carica.getSediPersone());
					
				//creiamo manualmente una AL con i dati recuperati da PARIX
				} else 
					//Crea la Persona dai dati della carica
					persona = spa.getPerson(carica.getSediPersone());
			}
			
			if (persona!=null)
				PersonAction.instance().inject(persona);

		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
		} 
	}
	
}