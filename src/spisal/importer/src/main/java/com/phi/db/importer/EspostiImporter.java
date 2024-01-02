package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Esposti;
import com.phi.entities.baseEntity.SchedaEsposti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Sostanze;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.prevnet.entities.Comunicazionicancerogeni;
import com.prevnet.entities.Fattoririschio;
import com.prevnet.entities.Lavoratoriespostiagenti;
import com.prevnet.entities.Preparaticancerogeni;
import com.prevnet.entities.Sistemicancerogeni;
import com.prevnet.entities.Sostanzecancerogene;
import com.prevnet.mappings.MapCantieri;
import com.prevnet.mappings.MapEsposti;
import com.prevnet.mappings.MapScans;

@SuppressWarnings({"unchecked"})
public class EspostiImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(EspostiImporter.class.getName());

	private static EspostiImporter instance = null;
	private Query qRisk;
	private CodeValuePhi sostanzaCv;
	private CodeValuePhi preparatoCv;
	private CodeValuePhi sistemiCv;
	private CodeValuePhi ncasCv;
	private CodeValuePhi nallCv;

	public static EspostiImporter getInstance() {
		if(instance == null) {
			instance = new EspostiImporter();
		}
		return instance;
	}

	public EspostiImporter() {
		super();		
		qRisk = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.id LIKE 'phidic.spisal.frisk.ncas.%' AND cv.code = :code AND cv.type = 'C' ");
		sostanzaCv = getCodeValue("phidic.spisal.esposti.tipo.01");
		preparatoCv = getCodeValue("phidic.spisal.esposti.tipo.02");
		sistemiCv = getCodeValue("phidic.spisal.esposti.tipo.03");
		ncasCv = getCodeValue("phidic.spisal.frisk.ncas");
		nallCv = getCodeValue("phidic.spisal.frisk.nall");
	}

	/**
	 * Importa l'attivita e la aggancia alla sede
	 * @param source
	 * @param sede
	 */
	public void importEsposti(Comunicazionicancerogeni source){		
		if(!checkMapping(source.getIdcomunicazionicancerogeni())){
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();

			SchedaEsposti target = new SchedaEsposti();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());

			Sedi sede = ditteImp.importDittaCompleta(source.getDitte());
			if(sede!=null){
				target.setSedi(sede);
				target.setPersoneGiuridiche(sede.getPersonaGiuridica());
			}

			ServiceDeliveryLocation loc = findUlss(ulss);
			if(loc!=null && loc.getChildren()!=null && !loc.getChildren().isEmpty()){
				for(ServiceDeliveryLocation s : loc.getChildren()) {
					if(s!=null && s.getId("HBS")!=null && distretto!=null && distretto.equals(s.getId("HBS").getExtension())) {
						target.setServiceDeliveryLocation(s);
						break;
					}
				}
			}

			target.setTipologia(getCodeValue("phidic.spisal.esposti.sostanze.subtype.1"));//Agenti cancerogeni
			target.setDataCompilazione(source.getDatacomunicazione());
			//target.setEndValidity(source.getDatacessazione());//sempre null

			if(source.getTotaleuomini()!=null)
				target.setTotUomini(source.getTotaleuomini().intValue());

			if(source.getTotaleuominiesposti()!=null)
				target.setEspUomini(source.getTotaleuominiesposti().intValue());

			if(source.getTotaledonne()!=null)
				target.setTotDonne(source.getTotaledonne().intValue());

			if(source.getTotaledonneesposte()!=null)
				target.setEspDonne(source.getTotaledonneesposte().intValue());

			if(source.getTotalelavoratori()!=null)
				target.setTotProd(source.getTotalelavoratori().intValue());

			saveOnTarget(target);
			saveMapping(source,target);
			//ESPOSTI
			if(target.getEsposti()==null)
				target.setEsposti(new ArrayList<Esposti>());

			if(source.getLavoratoriespostiagentis1()!=null && !source.getLavoratoriespostiagentis1().isEmpty()){
				for(Lavoratoriespostiagenti lav : source.getLavoratoriespostiagentis1()){
					Esposti esp = importLavoratore(lav);
					if(esp!=null){
						esp.setSchedaEsposti(target);
						target.getEsposti().add(esp);
					}					
				}
			}

			if(source.getLavoratoriespostiagentis2()!=null && !source.getLavoratoriespostiagentis2().isEmpty()){
				for(Lavoratoriespostiagenti lav : source.getLavoratoriespostiagentis2()){
					Esposti esp = importLavoratore(lav);
					if(esp!=null){
						esp.setSchedaEsposti(target);
						target.getEsposti().add(esp);
					}	
				}
			}

			//SOSTANZE
			target.setCancerogeno(new ArrayList<CodeValuePhi>());
			if(target.getSostanze()==null)
				target.setSostanze(new ArrayList<Sostanze>());

			if(source.getPreparaticancerogenis1()!=null && !source.getPreparaticancerogenis1().isEmpty()){

				for(Preparaticancerogeni obj : source.getPreparaticancerogenis1()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}

			if(source.getPreparaticancerogenis2()!=null && !source.getPreparaticancerogenis2().isEmpty()){

				for(Preparaticancerogeni obj : source.getPreparaticancerogenis2()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}

			if(source.getSistemicancerogenis1()!=null && !source.getSistemicancerogenis1().isEmpty()){

				for(Sistemicancerogeni obj : source.getSistemicancerogenis1()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}

			if(source.getSistemicancerogenis2()!=null && !source.getSistemicancerogenis2().isEmpty()){

				for(Sistemicancerogeni obj : source.getSistemicancerogenis2()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}

			if(source.getSostanzecancerogenes1()!=null && !source.getSostanzecancerogenes1().isEmpty()){

				for(Sostanzecancerogene obj : source.getSostanzecancerogenes1()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}

			if(source.getSostanzecancerogenes2()!=null && !source.getSostanzecancerogenes2().isEmpty()){

				for(Sostanzecancerogene obj : source.getSostanzecancerogenes2()){
					Sostanze sub = importSostanza(obj);
					if(sub!=null){
						sub.setSchedaEsposti(target);
						target.getSostanze().add(sub);
						saveOnTarget(sub);
					}
				}
			}
		}
	}

	private Sostanze importSostanza(Object obj){
		Sostanze sub = null;
		if(obj!=null){
			sub = new Sostanze();
			sub.setCreatedBy(this.getClass().getSimpleName()+ulss);
			sub.setCreationDate(new Date());


			if(obj instanceof Preparaticancerogeni){
				Preparaticancerogeni source = (Preparaticancerogeni)obj;
				CodeValuePhi sostanza = getSostanza(source.getFattoririschio());
				if(sostanza!=null){
					sub.setSostanza(sostanza);
					sub.setAgente(preparatoCv);
					sub.setTipologia(ncasCv);

					if(source.getTonnellate()!=null){
						sub.setQuantita(source.getTonnellate().toPlainString());
					}
				}
			}else if(obj instanceof Sistemicancerogeni){
				Sistemicancerogeni source = (Sistemicancerogeni)obj;
				CodeValuePhi sostanza = (CodeValuePhi) getMappedCode(source.getNumElAll(), ulss);
				if(sostanza!=null){
					sub.setSostanza(sostanza);
					sub.setAgente(sistemiCv);
					sub.setTipologia(nallCv);

					if(source.getTonnellate()!=null){
						sub.setQuantita(source.getTonnellate().toPlainString());
					}
				}				
			}else if(obj instanceof Sostanzecancerogene){
				Sostanzecancerogene source = (Sostanzecancerogene)obj;
				CodeValuePhi sostanza = getSostanza(source.getFattoririschio());
				if(sostanza!=null){
					sub.setSostanza(sostanza);
					sub.setAgente(sostanzaCv);
					sub.setTipologia(ncasCv);

					if(source.getTonnellate()!=null){
						sub.setQuantita(source.getTonnellate().toPlainString());
					}
				}
			}
		}


		return sub;
	}

	private Esposti importLavoratore(Lavoratoriespostiagenti lav){

		Esposti esp = null;
		if(lav!=null){
			esp = new Esposti();
			esp.setCreatedBy(this.getClass().getSimpleName()+ulss);
			esp.setCreationDate(lav.getDataorains());
			saveOnTarget(esp);
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			if(lav.getAnagrafiche()!=null){
				Person pers = persImp.importPerson(lav.getAnagrafiche());
				if(pers!=null){
					esp.setPerson(pers);

				}else{
					return null;
				}
			}

			esp.setStartDate(lav.getDatainizio());
			esp.setEndDate(lav.getDatafine());

			if(lav.getDescrattivita()!=null){
				String note = lav.getDescrattivita();
				int length = note.length();
				note = note.substring(0,length<=255?length:255);
				esp.setAttivita(note);
			}
			esp.setMansione((CodeValuePhi)getMappedCode(lav.getMansione(), ulss));

			CodeValuePhi sostanza = getSostanza(lav.getFattoririschio());
			if(sostanza!=null){
				esp.setSostanza(sostanza);
				esp.setAgente(sostanzaCv);
				esp.setTipologia(ncasCv);

				if(lav.getValore()!=null){
					try{
						esp.setValore(Double.parseDouble(lav.getValore()));
					}catch(NumberFormatException e){
						esp.setValore(null);
					}
				}
				esp.setUm((CodeValuePhi) getMappedCode(lav.getUnitaMisura(), ulss));
			}

			if(lav.getEsposizione()!=null){
				try{
					Integer gganno = Integer.parseInt(lav.getEsposizione());
					esp.setTempo(gganno);
				}catch (NumberFormatException e) {
					esp.setTempo(null);
				}
			}
		}

		return esp;
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapEsposti m = sourceEm.find(MapEsposti.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapEsposti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapEsposti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapEsposti map = list.get(0);
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
	private SchedaEsposti getMapped(long id){
		MapEsposti map = sourceEm.find(MapEsposti.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapEsposti m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapEsposti> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		
		if(map!=null){
			SchedaEsposti c = targetEm.find(SchedaEsposti.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qEsposti = targetEm.createQuery("SELECT p FROM SchedaEsposti p WHERE p.internalId = :id");
			qEsposti.setParameter("id", map.getIdphi());
			List<SchedaEsposti> lp = qEsposti.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}

	private void saveMapping(Comunicazionicancerogeni source, SchedaEsposti target){
		MapEsposti map = new MapEsposti();
		map.setIdprevnet(source.getIdcomunicazionicancerogeni());
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
		String hqlEsposti = "SELECT mf.idphi FROM MapEsposti mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlEsposti+="WHERE mf.ulss = :ulss";

		Query qEsposti = sourceEm.createQuery(hqlEsposti);
		if(ulss!=null && !ulss.isEmpty())
			qEsposti.setParameter("ulss", ulss);

		List<Long> allIdEsposti = qEsposti.getResultList();
		List<Long> idEsposti = new ArrayList<Long>();
		while(allIdEsposti!=null && !allIdEsposti.isEmpty()){
			if(allIdEsposti.size()>1000){
				idEsposti.clear();
				idEsposti.addAll(allIdEsposti.subList(0, 1000));
				allIdEsposti.removeAll(idEsposti);
			}else{
				idEsposti.clear();
				idEsposti.addAll(allIdEsposti);
				allIdEsposti.removeAll(idEsposti);
			}
			if(commit){
				//SGANCIO I FATTORI DI RISCHIO
				String deleteEsposti = "DELETE FROM esposti WHERE schedaesposti_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdEsp = targetEm.createNativeQuery(deleteEsposti);
				qUpdEsp.setParameter("ids", idEsposti);
				qUpdEsp.executeUpdate();
				targetEm.getTransaction().commit();

				String deleteSostanze = "DELETE FROM sostanze WHERE schedaesposti_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdSost = targetEm.createNativeQuery(deleteSostanze);
				qUpdSost.setParameter("ids", idEsposti);
				qUpdSost.executeUpdate();
				targetEm.getTransaction().commit();

				String deleteScheda = "DELETE FROM SchedaEsposti e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelScheda = targetEm.createQuery(deleteScheda);
				qDelScheda.setParameter("ids", idEsposti);
				qDelScheda.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_ESPOSTI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}

	/*
	 * Questa logica è DIVERSA da quella della malattia professionale cosi come la query.
	 * Per sostanze e preparati, IDNCAS coincide sempre (su prevnet) con voci del catalogo NCAS,
	 * cosi come è stato importato. Per le malattie professionali invece, i fattori di rischio hanno
	 * voci dal catalogo AgentiMalattie (solo tipo S)
	 */
	private CodeValuePhi getSostanza(Fattoririschio risk){
		if(risk!=null){
			String code = risk.getCodice();

			qRisk.setParameter("code", code);
			List<CodeValuePhi> list = qRisk.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}
		}

		return null;
	}
}
