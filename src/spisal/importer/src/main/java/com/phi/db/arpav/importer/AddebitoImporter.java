package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapAddebito;
import com.phi.entities.actions.ImpPressAction;
import com.phi.entities.actions.IndirizzoSpedAction;
import com.phi.entities.actions.SediAction;
import com.phi.entities.actions.SediInstallazioneAction;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;

public class AddebitoImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(AddebitoImporter.class.getName());

	private static AddebitoImporter instance = null;

	public static AddebitoImporter getInstance() {
		if (instance == null) {
			instance = new AddebitoImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		Addebito addebito = getMapped(row[0].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (addebito == null) {
			addebito = new Addebito();
			addebito.setCreatedBy(this.getClass().getSimpleName() + ulss);
			addebito.setCreationDate(new Date());
		
			if(row[1].getContents()!=null && !row[1].getContents().trim().isEmpty()){
					addebito.setData(sdf.parse(row[1].getContents()));
					}
			addebito.setSigla(row[2].getContents());
			addebito.setMatricola(row[3].getContents());
			addebito.setAnno(row[4].getContents());
			addebito.setNote(row[5].getContents());
			addebito.setTecnicoOut(controlBoolean(row[6].getContents()));
			addebito.setHhServizio(row[7].getContents());
			addebito.setMmServizio(row[8].getContents());
			addebito.setQuantita(row[9].getContents());
			addebito.setImporto(row[10].getContents());
			if(row[11].getContents()!=null && !row[11].getContents().trim().isEmpty()){
					addebito.setDataUltimaModifica(sdf.parse(row[11].getContents()));
			}
			if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
				addebito.setUtenteUltimaModifica(getEmployee(row[12].getContents()));
			}
			addebito.setServiceDeliveryLocation(findArpav(row[13].getContents()));
			addebito.setOperatore(getOperatore(row[14].getContents()));
			if(row[15].getContents()!=null && !row[15].getContents().trim().isEmpty()){
				addebito.setPersoneGiuridiche(getPersonaGiuridica(row[15]
					.getContents()));
			}
			addebito.setImpType(getCodeValue(row[16].getContents()));
			addebito.setSubTypeSoll(getCodeValue(row[17].getContents()));
			addebito.setSubTypeTerra(getCodeValue(row[18].getContents()));
			addebito.setRegimeFiscale(getCodeValue(row[19].getContents()));
			addebito.setAliquota(getCodeValue(row[20].getContents()));
			addebito.setCausale(getCodeValue(row[21].getContents()));
			addebito.setCasualeAdd(getCodeValue(row[22].getContents()));
			addebito.setCausaleAddTxt(row[23].getContents());
			if(row[24].getContents()!=null && !row[24].getContents().trim().isEmpty()){
				//copy ImpPress 
				addebito.setImpPress(getImpPress(row[24].getContents(),row[24].getContents(),row[24].getContents(),row[24].getContents()));
				addebito.setImpPressCpy(getImpPressCpy(row[24].getContents(),row[24].getContents(),row[24].getContents(),row[24].getContents()));
				//copy ImpPress of verificaImp into ImpSearchCollector
				addebito.setImpSearchCollector(copyIntoCollector(addebito.getImpPress()));
			}
			if(row[25].getContents()!=null && !row[25].getContents().trim().isEmpty()){
				//copy ImpRisc 
				addebito.setImpRisc(getImpRisc(row[25].getContents(),row[25].getContents(),row[25].getContents(),row[25].getContents()));
				addebito.setImpRiscCpy(getImpRiscCpy(row[25].getContents(),row[25].getContents(),row[25].getContents(),row[25].getContents()));
				//copy ImpRisc of verificaImp into ImpSearchCollector
				addebito.setImpSearchCollector(copyIntoCollector(addebito.getImpRisc()));
			}
			if(row[26].getContents()!=null && !row[26].getContents().trim().isEmpty()){
				//copy impMonta
				addebito.setImpMonta(getImpMonta(row[26].getContents(),row[26].getContents(),row[26].getContents(),row[26].getContents()));
				addebito.setImpMontaCpy(getImpMontaCpy(row[26].getContents(),row[26].getContents(),row[26].getContents(),row[26].getContents()));
				//copy impMonta of verificaImp into ImpSearchCollector
				addebito.setImpSearchCollector(copyIntoCollector(addebito.getImpMonta()));
			}
			if(row[27].getContents()!=null && !row[27].getContents().trim().isEmpty()){
				//copy impSoll
				addebito.setImpSoll(getImpSoll(row[27].getContents(),row[27].getContents(),row[27].getContents(),row[27].getContents(),row[27].getContents()));
				addebito.setImpSollCpy(getImpSollCpy(row[27].getContents(),row[27].getContents(),row[27].getContents(),row[27].getContents(),row[27].getContents()));
				//copy impSoll of verificaImp into ImpSearchCollector
				addebito.setImpSearchCollector(copyIntoCollector(addebito.getImpSoll()));
			}
			if(row[28].getContents()!=null && !row[28].getContents().trim().isEmpty()){
				//copy impTerra
				addebito.setImpTerra(getImpTerra(row[28].getContents(), row[28].getContents(), row[28].getContents(), row[28].getContents(), row[28].getContents()));
				addebito.setImpTerraCpy(getImpTerraCpy(row[28].getContents(), row[28].getContents(), row[28].getContents(), row[28].getContents(), row[28].getContents()));
				//copy impTerra of verificaImp into ImpSearchCollector
				addebito.setImpSearchCollector(copyIntoCollector(addebito.getImpTerra
						()));
			}
			saveOnTarget(addebito);
			saveMapping(row[0].getContents(), addebito);
		} else {
			log.info("Already imported Addebito with id: "
					+ row[0].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	private Addebito getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapAddebito m WHERE m.idexcel = :id";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idexcel);
		List<MapAddebito> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapAddebito map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM Addebito e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<Addebito> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sourceId, Addebito target) {
		MapAddebito map = new MapAddebito();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "Addebito id: "
				+ target.getSigla() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}



	private Boolean controlBoolean(String data) {
		if (data != null) {
			if (data.equals("1"))
				return true;
		} else
			return false;

		return null;
	}

	private PersoneGiuridiche getPersonaGiuridica(String id) {
		PersoneGiuridicheImporter persGiudImp = PersoneGiuridicheImporter.getInstance();
		return persGiudImp.getMapped(id);
	}



	private Employee getEmployee(String id) {
		EmployeeImporter eImp = EmployeeImporter.getInstance();
		return eImp.getMapped(id);
	}
	private Operatore getOperatore(String id) {
		OperatoreImporter opeImp = OperatoreImporter.getInstance();
		return opeImp.getMapped(id);
	}

	private ImpTerra getImpTerra(String sigla, String matr, String anno, String subtype, String dep){
		ImpTerraImporter opeImp = ImpTerraImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, subtype, dep);
	}
	private ImpRisc getImpRisc(String sigla, String matr, String anno, String dep){
		ImpRiscImporter opeImp = ImpRiscImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpMonta getImpMonta(String sigla, String matr, String anno, String dep){
		ImpMontaImporter opeImp = ImpMontaImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpPress getImpPress(String sigla, String matr, String anno, String dep){
		ImpPressImporter opeImp = ImpPressImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpSoll getImpSoll(String sigla, String matr, String anno, String subtype, String dep){
		ImpSollImporter opeImp = ImpSollImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, subtype, dep);
	}
	
	private ImpMonta getImpMontaCpy(String sigla, String matr, String anno, String dep) {
		try{
			ImpMontaImporter impMontaImp = ImpMontaImporter.getInstance();
			ImpMonta toCopy = impMontaImp.getMapped(sigla, matr, anno, dep);
			ImpMonta copy = new ImpMonta();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);
			
			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();
			
			if (toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());
			
			saveOnTarget(si);
			copy.setSedeInstallazione(si);
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();
			
			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());
			
			ca.create(sa);
			copy.setSedeAddebito(sa);
			*/
			
			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();
			
			if(toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());
			
			saveOnTarget(se);
			copy.setSedi(se);
			
			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			
			if ( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());
			
			saveOnTarget(is);
			copy.setIndirizzoSped(is);
			
			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setAmministratore(toCopy.getAmministratore());
			copy.setAnno(toCopy.getAnno());
			//copy.setAnnoCostruz(toCopy.getAnnoCostruz());
			copy.setCategoria(toCopy.getCategoria());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setCorsa(toCopy.getCorsa());
			copy.setCostruttore(toCopy.getCostruttore());
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setDestinazione(toCopy.getDestinazione());
			copy.setDistanza(toCopy.getDistanza());
			copy.setFermate(toCopy.getFermate());
			copy.setLicenza(toCopy.getLicenza());
			copy.setManovra(toCopy.getManovra());
			copy.setManutentore(toCopy.getManutentore());
			copy.setMatricola(toCopy.getMatricola());
			copy.setMatrcomune(toCopy.getMatrcomune());
			copy.setMotore(toCopy.getMotore());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setPortata(toCopy.getPortata());
			copy.setSigla(toCopy.getSigla());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setTrazione(toCopy.getTrazione());
			copy.setVelocita(toCopy.getVelocita());
			
			// I0064822 
			copy.setStatoImpianto(toCopy.getStatoImpianto());
			
			// I0070276
			copy.setPorte(toCopy.getPorte());
			copy.setNote(toCopy.getNote());
			
			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private ImpPress getImpPressCpy(String sigla, String matr, String anno, String dep) {
		try{
		ImpPressImporter impPressImp = ImpPressImporter.getInstance();
		ImpPress toCopy = impPressImp.getMapped(sigla, matr, anno, dep);
		
		ImpPress copy = new ImpPress();
		copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
		copy.setCreationDate(new Date());
		copy.setCopy(true);
		copy.setIsActive(false);

		//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
		SediInstallazione si = new SediInstallazione();

		if (toCopy.getSedeInstallazione()!=null)
			si = copySedeInstallazione(toCopy.getSedeInstallazione());

		saveOnTarget(si);
		copy.setSedeInstallazione(si);

		Sedi se = new Sedi();

		if(toCopy.getSedi()!=null)
			se = copySede(toCopy.getSedi());

		saveOnTarget(se);
		copy.setSedi(se);

		//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
		IndirizzoSped is = new IndirizzoSped();

		if (toCopy.getIndirizzoSped()!=null)
			is = copyIndirizzoSped(toCopy.getIndirizzoSped());

		saveOnTarget(is);
		copy.setIndirizzoSped(is);

		// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
		if(toCopy.getAddr()!=null)
			copy.setAddr(toCopy.getAddr().cloneAd());

		copy.setAnno(toCopy.getAnno());
		copy.setBombole(toCopy.getBombole());
		copy.setCapacita(toCopy.getCapacita());
		//copy.setCaratteristiche(toCopy.getCaratteristiche());
		copy.setCategoriaRischio(toCopy.getCategoriaRischio());
		copy.setCode(toCopy.getCode());
		copy.setCodiceImpianto(toCopy.getCodiceImpianto());
		copy.setComodante(toCopy.getComodante());
		copy.setCompetenza(toCopy.getCompetenza());
		copy.setCostruttore(toCopy.getCostruttore());

		copy.setDataCostruzione(toCopy.getDataCostruzione());
		copy.setDenominazione(toCopy.getDenominazione());

		copy.setFluido(toCopy.getFluido());
		copy.setMatricola(toCopy.getMatricola());
		copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
		copy.setPressBar1(toCopy.getPressBar1());
		copy.setPressBar2(toCopy.getPressBar2());
		copy.setProducibilita(toCopy.getProducibilita());

		copy.setSigla(toCopy.getSigla());
		copy.setSubTypeSoll(toCopy.getSubTypeSoll());
		copy.setSubTypeTerra(toCopy.getSubTypeTerra());
		copy.setSuperficie(toCopy.getSuperficie());

		copy.setTempS1(toCopy.getTempS1());
		copy.setTempS2(toCopy.getTempS2());
		copy.setTempV1(toCopy.getTempV1());
		copy.setTempV2(toCopy.getTempV2());
		copy.setTipoApparecchio(toCopy.getTipoApparecchio());
		
		//campi aggiuntivi I00074067
		copy.setProtNumero(toCopy.getProtNumero());
		copy.setUtenteLettera(toCopy.getUtenteLettera());
		copy.setSezione(toCopy.getSezione());
		copy.setLetteraTrasm(toCopy.getLetteraTrasm());
		copy.setArtEsonero(toCopy.getArtEsonero());

		// I0064820 
		copy.setStatoImpianto(toCopy.getStatoImpianto());

		// I0070276
		/*List<CodeValuePhi> caratteristicheToCopy = toCopy.getCaratteristiche();
		if(caratteristicheToCopy != null){
			List<CodeValuePhi> caratteristicheCopy = new ArrayList<CodeValuePhi>();
			for(CodeValuePhi cv : caratteristicheToCopy){
				caratteristicheCopy.add(cv);
			}
			copy.setCaratteristicheSpec(caratteristicheCopy);

		}*/
		copy.setCaratteristicheSpec(toCopy.getCaratteristicheSpec());

		copy.setSuperficie(toCopy.getSuperficie());
		copy.setTubazioni(toCopy.getTubazioni());
		copy.setNote(toCopy.getNote());

		//this.create();

		return copy;
		
	} catch (Exception ex) {
		log.error(ex);
		throw new RuntimeException(ex);
	} 
	
	}

	private ImpRisc getImpRiscCpy(String sigla, String matr, String anno, String dep) {
		try{
			ImpRiscImporter impRiscImp = ImpRiscImporter.getInstance();
			ImpRisc toCopy = impRiscImp.getMapped(sigla, matr, anno, dep);
			ImpRisc copy = new ImpRisc();
			copy.setCopy(true);
			copy.setIsActive(false);
			
			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();
			
			if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());
			
			saveOnTarget(si);
			copy.setSedeInstallazione(si);
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();
			
			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());
			
			ca.create(sa);
			copy.setSedeAddebito(sa);
				*/
			
			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();
			
			if(toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());
			
			saveOnTarget(se);
			copy.setSedi(se);
			
			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			
			if( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());
			
			saveOnTarget(is);
			copy.setIndirizzoSped(is);
			
			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setAnno(toCopy.getAnno());
			copy.setCode(toCopy.getCode());

			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataAutocert1(toCopy.getDataAutocert1());
			copy.setDataAutocert2(toCopy.getDataAutocert2());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setDescrizImpianto(toCopy.getDescrizImpianto());
			copy.setDescrizLocali(toCopy.getDescrizLocali());
			copy.setDestImp(toCopy.getDestImp());

			copy.setGiorniAA(toCopy.getGiorniAA());
			copy.setImpianto(toCopy.getImpianto());
			copy.setManutentore(toCopy.getManutentore());
			copy.setMatricola(toCopy.getMatricola());
			copy.setNumGen(toCopy.getNumGen());
			copy.setOreGG(toCopy.getOreGG());
			copy.setPotGlob(toCopy.getPotGlob());
			copy.setPotGlobNom(toCopy.getPotGlobNom());

			copy.setSigla(toCopy.getSigla());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setVasiAperti(toCopy.getVasiAperti());
			copy.setVasiChiusi(toCopy.getVasiChiusi());
			
			// I0064821 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276
			copy.setSuperficieRisc(toCopy.getSuperficieRisc());
			copy.setNote(toCopy.getNote());
			
			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		
	}

	private ImpSoll getImpSollCpy(String sigla, String matr, String anno, String subtype, String dep) {
		
		try{
			ImpSollImporter impSollImp = ImpSollImporter.getInstance();
			ImpSoll toCopy= impSollImp.getMapped(sigla, matr, anno, subtype, dep);
			ImpSoll copy = new ImpSoll();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);
			
			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();
			
		if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());
			
			saveOnTarget(si);
			copy.setSedeInstallazione(si);
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();
			
			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());
			
			ca.create(sa);
			copy.setSedeAddebito(sa);
				*/
			
			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();
			
			if( toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());
			
			saveOnTarget(se);
			copy.setSedi(se);
			
			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			if ( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());
			
			saveOnTarget(is);
			copy.setIndirizzoSped(is);
			
			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setAnno(toCopy.getAnno());
			//copy.setAnnoCostruz(toCopy.getAnnoCostruz());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setCompetenza(toCopy.getCompetenza());

			copy.setCostrRadioc(toCopy.getCostrRadioc());
			copy.setCostruttore(toCopy.getCostruttore());
			
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataInstRadioc(toCopy.getDataInstRadioc());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setMarcCE(toCopy.getMarcCE());
			copy.setMatricola(toCopy.getMatricola());
			copy.setMezzo(toCopy.getMezzo());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setNumRadioc(toCopy.getNumRadioc());

			copy.setPortata(toCopy.getPortata());

			copy.setSigla(toCopy.getSigla());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());

			copy.setTipoFabb(toCopy.getTipoFabb());
			copy.setVelocitaMax(toCopy.getVelocitaMax());
			
			// I0064823 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276 
			copy.setNote(toCopy.getNote());
			
			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		
	}

	private ImpTerra getImpTerraCpy(String sigla, String matr, String anno, String subtype, String dep) {
		
		try{
			ImpTerraImporter impTerraImp = ImpTerraImporter.getInstance();
			ImpTerra toCopy= impTerraImp.getMapped(sigla, matr, anno, subtype, dep);
			ImpTerra copy = new ImpTerra();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);
			
			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();
			
			if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());
			
			saveOnTarget(si);
			copy.setSedeInstallazione(si);
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();
			
			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());
			
			ca.create(sa);
			copy.setSedeAddebito(sa);
				*/
			
			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();
			
			if(toCopy.getSedi()!=null)
				se =copySede(toCopy.getSedi());
			
			saveOnTarget(se);
			copy.setSedi(se);
			
			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			if ( toCopy.getIndirizzoSped()!=null)
				is =copyIndirizzoSped(toCopy.getIndirizzoSped());
			
			saveOnTarget(is);
			copy.setIndirizzoSped(is);
			
			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setAnno(toCopy.getAnno());
			copy.setAreaPeric(toCopy.getAreaPeric());
			copy.setAste(toCopy.getAste());
			copy.setCabine(toCopy.getCabine());
			//copy.setCantieri(toCopy.getCantieri());

			copy.setCi0(toCopy.getCi0());
			copy.setCi1(toCopy.getCi1());
			copy.setCi2(toCopy.getCi2());
			copy.setCi3(toCopy.getCi3());
					
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setCompetenza(toCopy.getCompetenza());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setDisperdenti(toCopy.getDisperdenti());
			copy.setDispersori(toCopy.getDispersori());
			copy.setImpColl(toCopy.getImpColl());
//			copy.setIsolanti01(toCopy.getIsolanti01());
//			copy.setIsolanti02(toCopy.getIsolanti02());

			copy.setMatricola(toCopy.getMatricola());
//			copy.setMetalliche(toCopy.getMetalliche());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setPar(toCopy.getPar());
//			copy.setParafulmini(toCopy.getParafulmini());

			copy.setPot(toCopy.getPot());
			copy.setRaggruppati01(toCopy.getRaggruppati01());
			copy.setRaggruppati02(toCopy.getRaggruppati02());
			
//			copy.setSerbatoi(toCopy.getSerbatoi());
			copy.setSigla(toCopy.getSigla());
				
//			copy.setStruttAereop(toCopy.getStruttAereop());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeB(toCopy.getSubTypeB());
			copy.setSubTypeC(toCopy.getSubTypeC());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			
			copy.setSuperf01(toCopy.getSuperf01());
			copy.setSuperf02(toCopy.getSuperf03());
			copy.setSuperf03(toCopy.getSuperf03());
			
			copy.setTipologia(toCopy.getTipologia());
			copy.setTipologiaTesto(toCopy.getTipologiaTesto());
			copy.setType(toCopy.getType());
			
			// I0064824 
			copy.setStatoImpianto(toCopy.getStatoImpianto());
			
			// I0070276
			copy.setCabineCode(toCopy.getCabineCode());
			copy.setCabineNum(toCopy.getCabineNum());
			copy.setImpAutoprod(toCopy.getImpAutoprod());
//			copy.setStruttAutopCode(toCopy.getStruttAutopCode());
			copy.setStruttAutopNum(toCopy.getStruttAutopNum());
			copy.setNote(toCopy.getNote());
			
			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());
			
			return copy;			
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private SediInstallazione copySedeInstallazione(SediInstallazione toCopy){
		try{
			SediInstallazione copy = new SediInstallazione();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);
			
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setTipoSede(toCopy.getTipoSede());
			copy.setTipologiaSede(toCopy.getTipologiaSede());
			
			//this.create();

			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private Sedi copySede(Sedi toCopy){
		try{
			Sedi copy = new Sedi();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setSedeAddebito(toCopy.getSedeAddebito());

			copy.setCig(toCopy.getCig());
			copy.setCodiceUnivoco(toCopy.getCodiceUnivoco());
			copy.setClasseEconomica(toCopy.getClasseEconomica());
			copy.setCodContabilita(toCopy.getCodContabilita());
			copy.setCodiceVia(toCopy.getCodiceVia());
			copy.setDenominazioneUnitaLocale(toCopy.getDenominazioneUnitaLocale());
			copy.setEsenzione(toCopy.getEsenzione());
			copy.setImpSpesa(toCopy.getImpSpesa());
			copy.setNote(toCopy.getNote());
			copy.setPrincipaleAdd(toCopy.getPrincipaleAdd());
			copy.setSedePrincipale(toCopy.getSedePrincipale());
			copy.setSettore(toCopy.getSettore());
			copy.setStato(toCopy.getStato());

			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());

			copy.setTipoAttivita(toCopy.getTipoAttivita());
			copy.setTipoUtente(toCopy.getTipoUtente());
			copy.setZona(toCopy.getZona());

			List<IndirizzoSped> indirizzi = toCopy.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				copy.setIndirizzoSped(new ArrayList<IndirizzoSped>());

				for (IndirizzoSped ind:indirizzi){
					IndirizzoSped icopy = IndirizzoSpedAction.instance().copy(ind);
					copy.getIndirizzoSped().add(icopy);
					saveOnTarget(icopy);
				}
			}

			copy.setPersonaGiuridica(toCopy.getPersonaGiuridica());
			
			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private IndirizzoSped copyIndirizzoSped(IndirizzoSped toCopy){
		try{
			IndirizzoSped copy = new IndirizzoSped();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setIsActive(false);
			copy.setCopy(true);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());
			
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setPrincipale(toCopy.getPrincipale());
			copy.setStato(toCopy.getStato());
			
			//this.create();
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private ImpSearchCollector copyIntoCollector(Impianto imp) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;
			
			CodeValuePhi codeCv = imp.getCode();
			String code = codeCv.getCode();

			ImpSearchCollector coll = new ImpSearchCollector();
			coll.setCreatedBy(this.getClass().getSimpleName() + ulss);
			coll.setCreationDate(new Date());
			coll.setCode(codeCv);
			coll.setSigla(imp.getSigla());
			coll.setMatricola(imp.getMatricola());
			coll.setAnno(imp.getAnno());
			
			if (imp.getSedeInstallazione()!=null)
				coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());
			
			/* I0070276 pezzo sostituito con sede con flag addebito
			if (imp.getSedeAddebito()!=null)
				coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());
			*/
			
			// I0070276
			if (imp.getSedi()!=null)
				coll.setDenominazioneSA(imp.getSedi().getDenominazioneUnitaLocale());
			
			if(imp.getIndirizzoSped()!=null)
				coll.setDenominazioneIS(imp.getIndirizzoSped().getDenominazione());
						
			if (!"".equals(code)){
				
				//Apparecchi a pressione
				if ("01".equals(code))
					coll.setNumeroFabbrica(((ImpPress)imp).getNumeroFabbrica());
				
				//Impianti di riscaldamento
				//else if ("02".equals(code))
				
				//Ascensori e montacarichi
				//else if ("03".equals(code))
			
				//Apparecchi di sollevamento	
				else if ("04".equals(code))
					coll.setSubTypeSoll(((ImpSoll)imp).getSubTypeSoll());
			
				//Impianti elettrici	
				else if ("05".equals(code))
					coll.setSubTypeTerra(((ImpTerra)imp).getSubTypeTerra());
			}
			
			saveOnTarget(coll);
			return coll;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
}
