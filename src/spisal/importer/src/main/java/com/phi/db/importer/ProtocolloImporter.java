package com.phi.db.importer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.prevnet.entities.ProtocolloPrevnet;
import com.prevnet.entities.Richiestaregistrazione;
import com.prevnet.mappings.MapPratiche;
import com.prevnet.mappings.MapProtocollo;

@SuppressWarnings({"unchecked"})
public class ProtocolloImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(ProtocolloImporter.class.getName());

	private static ProtocolloImporter instance = null;

	public static ProtocolloImporter getInstance() {
		if(instance == null) {
			instance = new ProtocolloImporter();
		}
		return instance;
	}

	public Protocollo importProtocollo(Richiestaregistrazione req, Procpratiche pratica){
		if(!checkMapping(req.getIdrichregistrazione())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			CantieriImporter cantImp = CantieriImporter.getInstance();
			MediciImporter medImp = MediciImporter.getInstance();

			Protocollo target = new Protocollo();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			target.setProcpratiche(pratica);

			target.setServiceDeliveryLocation(pratica.getUoc());
			target.setUos(pratica.getServiceDeliveryLocation());

			target.setData(req.getDatarichiesta());
			target.setDataASL(req.getDataarrivo());
			if(target.getData()==null)//richesto da scatto
				target.setData(target.getDataASL());
			if(target.getData()==null)//richiesto da scatto
				target.setData(pratica.getData());

			target.setApplicant(getMappedCode(req.getProvenienza(), ulss));
			//target.setNrichiesta(source.getNumerorichiesta()); non si può impostare..
			target.setStatusCode(getStatus("status.generic.completed")); //STATO ASSEGNATA
			target.setIsMaster(true);//COMUNICAZIONE PRINCIPALE

			String note = null;
			if(req.getDescrrichiesta()!=null){
				int length = req.getDescrrichiesta().length();
				note = req.getDescrrichiesta().substring(0,length<=4000?length:4000);
			}

			target.setNote(note);

			if(req.getProtocolloPrevnet()!=null){
				ProtocolloPrevnet prot = req.getProtocolloPrevnet();

				//modifica richiesta in I0050420
				if(prot.getNprotocollocentrale()!=null && !prot.getNprotocollocentrale().isEmpty()){
					DecimalFormat df = new DecimalFormat();
					df.setParseBigDecimal(true);
					try {
						target.setNprotocollo((BigDecimal)df.parse(prot.getNprotocollocentrale()));
					} catch (ParseException e) {
						target.setNprotocollo(null);
					}
				}

				target.setDataProtocollo(prot.getDatareg());
				target.setAnnoProtocollo(prot.getAnnoprotocollo());
				target.setTipoSpedizione(getMappedCode(prot.getTipoMissiva(), ulss));


				String oggetto = null;
				if(prot.getDescoggetto()!=null){
					int length = prot.getDescoggetto().length();
					oggetto = prot.getDescoggetto().substring(0,length<=4000?length:4000);
				}

				target.setOggetto(oggetto);
			}
			// modifica richiesta in I00073535 
			//if("050118".equals(ulss) || "050119".equals(ulss)) {
			//import Marca Trevigiana
				if(target.getNprotocollo()==null && req.getNumeroprotocollo()!=null) {
					DecimalFormat df = new DecimalFormat();
					df.setParseBigDecimal(true);
					try {
						target.setNprotocollo((BigDecimal)df.parse(req.getNumeroprotocollo()));
					} catch (ParseException e) {
						target.setNprotocollo(null);
					}
				}
				//import Marca Trevigiana
				if(target.getDataProtocollo()==null){
					target.setDataProtocollo(req.getDataarrivo());
				}
			//}



			//RICHIEDENTE
			if(req.getIdtipoanrichied()!=null){
				switch (req.getIdtipoanrichied().intValue()) {
				case 0:
					target.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getRichiedenteAnag());
					if(sede!=null){
						target.setRichiedenteSede(sede);
						target.setRichiedenteDitta(sede.getPersonaGiuridica());
					}

					break;
				case 3:
					//NON E' PREVISTO UN CANTIERE COME RICHIEDENTE

					break;
				case 4:
					target.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
					Person pers = persImp.importPerson(req.getRichiedenteAnag());
					if(pers!=null){
						target.setRichiedenteUtente(pers);
					}
					break;
				case 7:
					if(req.getMedico()!=null){
						target.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.medico"));
						Organization org = medImp.findOrganization(ulss);
						Physician med = medImp.importMedicoSpisal(req.getMedico(), org);
						if(med!=null){
							target.setRichiedenteMedico(med);
						}
					}
					break;
				default:
					break;
				}
			}

			//RIFERITO A
			if(req.getIdtipoanagrafica()!=null){
				switch (req.getIdtipoanagrafica().intValue()) {
				case 0:
					target.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getRiferitoAnag());
					if(sede!=null){
						target.setRiferimentoSede(sede);
						target.setRiferimentoDitta(sede.getPersonaGiuridica());
					}
					break;
				case 3:	
					target.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
					Cantiere cant = cantImp.importCantiere(req.getRiferitoAnag());
					if(cant!=null){
						target.setRiferimentoCantiere(cant);
					}

					break;
				case 4:
					target.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
					Person pers = persImp.importPerson(req.getRiferitoAnag());
					if(pers!=null){
						target.setRiferimentoUtente(pers);
					}
					break;
				default:
					break;
				}
			}

			//UBICAZIONE
			if(req.getIdtipoanubicaz()!=null){
				switch (req.getIdtipoanubicaz().intValue()) {
				case -1:
					target.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.targetsource.altro"));
					AD ubicazioneAddr = new AD();
					ubicazioneAddr.setStr(req.getDescrlocalizzato());
					manageAddrComune(ubicazioneAddr, req.getComuneUbicaz());
					break;
				case 0:
					target.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getUbicazioneAnag());
					if(sede!=null){
						target.setUbicazioneSede(sede);
						target.setUbicazioneDitta(sede.getPersonaGiuridica());
						if(sede.getAddr()!=null)
							target.setUbicazioneAddr(sede.getAddr().cloneAd());
					}
					break;
				case 3:
					target.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));	
					Cantiere cant = cantImp.importCantiere(req.getUbicazioneAnag());
					if(cant!=null){
						target.setUbicazioneCantiere(cant);
						if(cant.getAddr()!=null)
							target.setUbicazioneAddr(cant.getAddr().cloneAd());
					}
					break;
				case 4:
					target.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
					Person pers = persImp.importPerson(req.getUbicazioneAnag());
					if(pers!=null){
						target.setUbicazioneUtente(pers);
						if(pers.getAddr()!=null)
							target.setUbicazioneAddr(pers.getAddr().cloneAd());
					}
					break;
				default:
					break;
				}

				if(target.getUbicazioneAddr()==null || target.getUbicazioneAddr().getStr()==null || target.getUbicazioneAddr().getStr().isEmpty()
						|| target.getUbicazioneAddr().getCode()==null){
					AD ubicazioneAddr=null;
					if(target.getUbicazioneAddr()==null)
						ubicazioneAddr = new AD();
					else
						ubicazioneAddr = target.getUbicazioneAddr();

					if(ubicazioneAddr.getStr()==null || ubicazioneAddr.getStr().isEmpty())
						ubicazioneAddr.setStr(req.getDescrlocalizzato());

					if(ubicazioneAddr.getCode()==null)
						manageAddrComune(ubicazioneAddr, req.getComuneUbicaz());

					target.setUbicazioneAddr(ubicazioneAddr);
				}
			}

			saveOnTarget(target);
			saveMapping(req, target);
		}



		return getMapped(req.getIdrichregistrazione());

	}	

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapProtocollo m = sourceEm.find(MapProtocollo.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapProtocollo m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapProtocollo> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapProtocollo map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Protocollo getMapped(long id){
		MapProtocollo map = sourceEm.find(MapProtocollo.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapProtocollo m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapProtocollo> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Protocollo c = targetEm.find(Protocollo.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qProt = targetEm.createQuery("SELECT p FROM Protocollo p WHERE p.internalId = :id");
			qProt.setParameter("id", map.getIdphi());
			List<Protocollo> lp = qProt.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
			
		return null;
	}

	private void saveMapping(Richiestaregistrazione source, Protocollo target){
		MapProtocollo map = new MapProtocollo();
		map.setIdprevnet(source.getIdrichregistrazione());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);

		saveOnSource(map);

		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}	

	@Override
	protected void deleteImportedData(String ulss) {
		String hqlProt = "SELECT mf.idphi FROM MapProtocollo mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlProt+="WHERE mf.ulss = :ulss";

		Query qProt = sourceEm.createQuery(hqlProt);
		if(ulss!=null && !ulss.isEmpty())
			qProt.setParameter("ulss", ulss);

		List<Long> allIdProt = qProt.getResultList();
		List<Long> idProt = new ArrayList<Long>();
		while(allIdProt!=null && !allIdProt.isEmpty()){
			if(allIdProt.size()>1000){
				idProt.clear();
				idProt.addAll(allIdProt.subList(0, 1000));
				allIdProt.removeAll(idProt);
			}else{
				idProt.clear();
				idProt.addAll(allIdProt);
				allIdProt.removeAll(idProt);
			}
			if(commit){
				String deleteProt = "DELETE FROM Protocollo e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelMal = targetEm.createQuery(deleteProt);
				qDelMal.setParameter("ids", idProt);
				qDelMal.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_PROTOCOLLO WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}

}
