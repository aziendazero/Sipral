package com.phi.db.importer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cariche;
import com.phi.entities.baseEntity.Ex301Bis;
import com.phi.entities.baseEntity.Gruppi;
import com.phi.entities.baseEntity.Iter758;
import com.phi.entities.baseEntity.Miglioramenti;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.prevnet.entities.Articoliprovvedimenti;
import com.prevnet.entities.Dlgs758giorniproroga;
import com.prevnet.entities.Dlgs758iter;
import com.prevnet.entities.ProvvedimentiPrevnet;
import com.prevnet.entities.Sanzioni;
import com.prevnet.entities.Sanzioniarticoli;
import com.prevnet.entities.Soggettiprovvedimento;
import com.prevnet.mappings.MapMiglioramenti;
import com.prevnet.mappings.MapProtocollo;
import com.prevnet.mappings.MapProvvedimenti;

@SuppressWarnings({"unchecked","unused"})
public class ProvvedimentiImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(ProvvedimentiImporter.class.getName());

	private static ProvvedimentiImporter instance = null;

	public static ProvvedimentiImporter getInstance() {
		if(instance == null) {
			instance = new ProvvedimentiImporter();
		}
		return instance;
	}

	public void aggiornaProvvedimento(ProvvedimentiPrevnet source) {
		if(checkMappingProv(source.getIdprovvedimenti())){
			Provvedimenti target = getMappedProv(source.getIdprovvedimenti());
			String note = "";
			if(source.getNote()!=null)
				note+=source.getNote();

			List<Articoliprovvedimenti> articoliProvvedimenti = source.getArticoliprovvedimentis();
			if(articoliProvvedimenti!=null && !articoliProvvedimenti.isEmpty()){
				int i=0;
				for(Articoliprovvedimenti artProv : articoliProvvedimenti){
					if(artProv.getDlgs758iter()!=null) {
						note+=((note.isEmpty()?"":"\r\n")+artProv.getDlgs758iter().getNote());
					}
					if(artProv.getArticolilegge()!=null && artProv.getArticolilegge().getLeggi()!=null){

						if(target.getArticoli()!=null && i<target.getArticoli().size()) {
							Articoli art = target.getArticoli().get(i);
							if(art.getCode()==null) {
								art.setCode(getLaw(artProv.getArticolilegge(),target.getData()));
								saveOnTarget(art);
							}
						}
						i++;
					}
				}
			}

			if(note!=null){
				int length = note.length();
				note = note.substring(0,length<=4000?length:4000);
				target.setNote(note);
			}
			Query q = targetEm.createNativeQuery("select count(*) from z_provvedimenti z join revinfo r on r.id = z.rev where z.internal_id = :id and r.username<>'phi-esb'");
			BigInteger count = (BigInteger) q.setParameter("id", target.getInternalId()).getSingleResult();
			if(count==null || count.intValue()<=0) {
				saveOnTarget(target);
				thislog.warn("Aggiornato provvedimento id: "+source.getIdprovvedimenti());
			}
		}
	}

	public Provvedimenti importProvvedimento(ProvvedimentiPrevnet source, Attivita sopralluogo, StringBuffer noteProvv){
		if(!checkMappingProv(source.getIdprovvedimenti())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();

			Provvedimenti target = new Provvedimenti();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());

			target.setType((CodeValuePhi)getMappedCode(source.getTipologia(), ulss));
			target.setAttivita(sopralluogo);
			target.setProcpratiche(sopralluogo.getProcpratiche());

			if(sopralluogo.getSoggetto()==null)
				sopralluogo.setSoggetto(new ArrayList<Soggetto>());

			List<Soggetto> soggetti = sopralluogo.getSoggetto();

			if(source.getDitta()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDitta());
				if(sede!=null){
					Soggetto s = null;
					boolean contained = false;
					for(Soggetto sogg : soggetti){
						if(sogg.getSede()!=null && sogg.getSede().getInternalId()==sede.getInternalId()){
							contained = true;
							s=sogg;
							break;
						}
					}

					if(!contained){
						s = new Soggetto();
						s.setCreatedBy(this.getClass().getSimpleName()+ulss);
						s.setCreationDate(new Date());
						s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));

						s.setSede(sede);
						s.setDitta(sede.getPersonaGiuridica());
						s.setAttivita(sopralluogo);
						saveOnTarget(s);
						soggetti.add(s);
					}


					target.setSoggetto(s);

					if(source.getSoggettiprovvedimento()!=null && source.getSoggettiprovvedimento().getSoggetto()!=null
							&& source.getSoggettiprovvedimento().getSoggetto().getTipo()!=null){

						RappresentantiImporter rappImp = RappresentantiImporter.getInstance();
						Soggettiprovvedimento sProv = source.getSoggettiprovvedimento();
						if(sProv.getSoggetto().getTipo().intValue()==4){
							CodeValue role = getMappedCode(sProv.getRuolo(), ulss);
							Cariche car = rappImp.importTitolare(sede, sProv.getSoggetto(), null);
							car.setRuolo((CodeValuePhi) role);
							if(car!=null){
								target.setCarica(car);
							}
						}

						target.setDocumentNumber(sProv.getNumerodoc());
						target.setCountry((CodeValueCountry)getMappedCode(sProv.getStatoEsteroDoc(), ulss));
						target.setReleasedDate(sProv.getDatarilasciodoc());
						target.setCity(new AD());
						//comune o provincia
						if(sProv.getComuneDoc()!=null){
							manageAddrComune(target.getCity(), sProv.getComuneDoc());
						}else if(sProv.getProvinciaDoc()!=null){
							CodeValue cv = getMappedCode(sProv.getProvinciaDoc(), ulss);
							if(cv instanceof CodeValueCity){
								CodeValueCity provincia = (CodeValueCity) cv;
								target.getCity().setCode(provincia);
								target.getCity().setCpa(provincia.getProvince());
								target.getCity().setZip(provincia.getZip());
								target.getCity().setCty(provincia.getCurrentTranslation());
							}
						}


						if(sProv.getTipodoc()!=null){
							switch (sProv.getTipodoc()) {
								case 1:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.cartaidentita"));
									break;
								case 2:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.passaporto"));
									break;
								case 3:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.patenteguida"));
									break;
								case 4:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.patentenautica"));
									break;
								case 5:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.permessosoggiorno"));
									break;
								case 6:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.portoarmi"));
									break;
								case 7:
									//non corrisponde a nulla
									break;
								case 8:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.permessosoggiornoquestura"));
									break;
								case 9:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.tesserapolizia"));
									break;
								case 10:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.tesseramindif"));
									break;
								case 11:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.tesseraminfin"));
									break;
								case 12:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.tesserareglig"));
									break;
								case 13:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.permessocaccia"));
									break;
								case 14:
									target.setDocumentType((CodeValuePhi)getCodeValue("phidic.generic.documenttype.tesseraminint"));
									break;
								default:
									break;
							}
						}

						if(sProv.getEnterilasciantedoc()!=null){
							switch (sProv.getEnterilasciantedoc()) {
								case 1:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.comune"));
									break;
								case 2:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.prefettura"));
									break;
								case 3:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.motorizzazione"));
									break;
								case 4:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.questura"));
									break;
								case 5:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.cdp"));
									break;
								case 6:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.ucm"));
									break;
								case 7:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.uco"));
									break;
								case 8:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.statoestero"));
									break;
								case 9:
									//non corrisponde a nulla
									break;
								case 10:
									target.setReleasedBy((CodeValuePhi)getCodeValue("phidic.spisal.pratiche.pev.rilasciante.mindif"));
									break;
								default:
									break;
							}
						}
					}
				}
			}else if(source.getAnagrafica()!=null && source.getAnagrafica().getTipo()!=null
					&& source.getAnagrafica().getTipo().intValue()==4){


				Person pers = persImp.importPerson(source.getAnagrafica());
				if(pers!=null){
					Soggetto s = null;
					boolean contained = false;
					if(soggetti!=null && !soggetti.isEmpty()){
						for(Soggetto sogg : soggetti){
							if(sogg.getUtente()!=null && sogg.getUtente().getInternalId()==pers.getInternalId()){
								contained = true;
								s=sogg;
								break;
							}
						}
					}

					if(!contained){
						s = new Soggetto();
						s.setCreatedBy(this.getClass().getSimpleName()+ulss);
						s.setCreationDate(new Date());
						s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

						s.setUtente(pers);
						s.setAttivita(sopralluogo);
						saveOnTarget(s);
						soggetti.add(s);
					}

					target.setSoggetto(s);
				}
			}

			String sourceNumber = "";
			if(source.getNumprovvedimento()!=null && sopralluogo.getProcpratiche()!=null
					&& sopralluogo.getProcpratiche().getNumero()!=null){

				if(source.getAnnoprovvedimento()!=null){
					target.setNumero(ulss+"_"+source.getAnnoprovvedimento()+"_"+source.getNumprovvedimento().toPlainString());
					sourceNumber = source.getNumprovvedimento().toPlainString()+"/"+source.getAnnoprovvedimento();
				}else{
					target.setNumero(ulss+"_????_"+source.getNumprovvedimento().toPlainString());
					sourceNumber = source.getNumprovvedimento().toPlainString();
				}

			}

			target.setData(source.getDataprovv());
			target.setDataNotifica(source.getDatanotifica());
			target.setEsito((CodeValuePhi)getMappedCode(source.getEsito(), ulss));

			String note = "";
			if(source.getNote()!=null)
				note+=source.getNote();

			if(source.getTipologia()!=null && "10".equals(source.getTipologia().getUtcodice())) {
				note+="VERBALE PRESCRIZIONE\r\n";
			}
			if(target.getType()!=null && "758".equals(target.getType().getCode())) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				note+=(source.getDatadepprocura()!=null?("Data di deposito: "+sdf.format(source.getDatadepprocura())+"\r\n"):"");

				if(source.getDatadepprocura()!=null){
					noteProvv.append("Provvedimento "+sourceNumber+" - Data deposito in procura: "+sdf.format(source.getDatadepprocura())+"\r\n");
				}
			}
			if(source.getArticoliprovvedimentis()!=null) {
				for(Articoliprovvedimenti a : source.getArticoliprovvedimentis()) {
					if(a.getDlgs758iter()!=null) {
						note+=((note.isEmpty()?"":"\r\n")+a.getDlgs758iter().getNote());
					}
				}
			}
			if(note!=null){
				int length = note.length();
				note = note.substring(0,length<=4000?length:4000);
				target.setNote(note);
			}

			sopralluogo.setSoggetto(soggetti);
			saveOnTarget(target);
			saveMappingProv(source, target);
			if(target.getType()!=null && "758".equals(target.getType().getCode())){

				//ITER 758	
				Iter758 iter = new Iter758();
				iter.setCreatedBy(this.getClass().getSimpleName()+ulss);
				iter.setCreationDate(new Date());
				saveOnTarget(iter);
				iter.setProvvedimento(target);
				target.addIter758(iter);
			}else if(target.getType()!=null && "301bis".equals(target.getType().getCode())){

				//ITER 301	
				Ex301Bis iter = new Ex301Bis();
				iter.setCreatedBy(this.getClass().getSimpleName()+ulss);
				iter.setCreationDate(new Date());

				saveOnTarget(iter);
				iter.setProvvedimento(target);
				target.addEx301Bis(iter);
			}

			saveOnTarget(target);

			manageArticoliViolati(source, target, source.getArticoliprovvedimentis());

		}

		return getMappedProv(source.getIdprovvedimenti());
	}

	public Miglioramenti importMiglioramento(ProvvedimentiPrevnet source, Attivita sopralluogo){
		if(!checkMappingMig(source.getIdprovvedimenti())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();

			Miglioramenti target = new Miglioramenti();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());

			if(sopralluogo.getSoggetto()==null)
				sopralluogo.setSoggetto(new ArrayList<Soggetto>());

			List<Soggetto> soggetti = sopralluogo.getSoggetto();

			String soggetto="";
			if(source.getDitta()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDitta());
				if(sede!=null){
					Soggetto s = null;
					boolean contained = false;
					for(Soggetto sogg : soggetti){
						if(sogg.getSede()!=null && sogg.getSede().getInternalId()==sede.getInternalId()){
							if(sogg.getDitta()!=null)
								soggetto=sogg.getDitta().getDenominazione();
							contained = true;
							s=sogg;
							break;
						}
					}

					if(!contained){
						s = new Soggetto();
						s.setCreatedBy(this.getClass().getSimpleName()+ulss);
						s.setCreationDate(new Date());
						s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));

						s.setSede(sede);
						s.setDitta(sede.getPersonaGiuridica());
						s.setAttivita(sopralluogo);
						saveOnTarget(s);
						soggetti.add(s);

						if(s.getDitta()!=null)
							soggetto=s.getDitta().getDenominazione();
					}
				}
			}else if(source.getAnagrafica()!=null && source.getAnagrafica().getTipo()!=null
					&& source.getAnagrafica().getTipo().intValue()==4){


				Person pers = persImp.importPerson(source.getAnagrafica());
				if(pers!=null){
					Soggetto s = null;
					boolean contained = false;
					if(soggetti!=null && !soggetti.isEmpty()){
						for(Soggetto sogg : soggetti){
							if(sogg.getUtente()!=null && sogg.getUtente().getInternalId()==pers.getInternalId()){
								if(sogg.getUtente()!=null && sogg.getUtente().getName()!=null)
									soggetto=sogg.getUtente().getName().getFam()+" "+sogg.getUtente().getName().getGiv();
								contained = true;
								s=sogg;
								break;
							}
						}
					}

					if(!contained){
						s = new Soggetto();
						s.setCreatedBy(this.getClass().getSimpleName()+ulss);
						s.setCreationDate(new Date());
						s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

						s.setUtente(pers);
						s.setAttivita(sopralluogo);
						saveOnTarget(s);
						soggetti.add(s);

						if(s.getUtente()!=null && s.getUtente().getName()!=null)
							soggetto=s.getUtente().getName().getFam()+" "+s.getUtente().getName().getGiv();
					}
				}
			}


			target.setEsito((CodeValuePhi)getMappedCode(source.getEsito(), ulss));
			target.setDataEmissione(source.getDatanotifica());
			String note = "";
			if(source.getTipologia()!=null){
				note+="Tipo provvedimento: "+source.getTipologia().getDescrizione()+"\r\n";
			}
			if(source.getNumprovvedimento()!=null){
				note+="Numero provvedimento: "+source.getNumprovvedimento().toPlainString();
				if(source.getAnnoprovvedimento()!=null){
					note+="/"+source.getAnnoprovvedimento().toString();
				}
				note+="\r\n";
			}
			if(source.getDataprovv()!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				note+="Data provvedimento: "+sdf.format(source.getDataprovv())+"\r\n";
			}

			if(!soggetto.isEmpty())
				note+="Soggetto: "+soggetto+"\r\n";

			if(source.getTipoProvvedimento()!=null){
				note+="Tipo Infrazione: "+source.getTipoProvvedimento().getDescrizione()+"\r\nNote\r\n";
			}
			note+=source.getNote();
			if(note!=null){
				int length = note.length();
				note = note.substring(0,length<=4000?length:4000);
				target.setNote(note);
			}




			target.setAttivita(sopralluogo);

			saveMappingMig(source, target);
			saveOnTarget(target);


		}

		return getMappedMig(source.getIdprovvedimenti());
	}

	private void manageArticoliViolati(ProvvedimentiPrevnet sourceProv, Provvedimenti targetProv, List<Articoliprovvedimenti> articoliProvvedimenti){
		PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
		if(articoliProvvedimenti!=null && !articoliProvvedimenti.isEmpty()){
			for(Articoliprovvedimenti artProv : articoliProvvedimenti){
				if(artProv.getArticolilegge()!=null && artProv.getArticolilegge().getLeggi()!=null){

					Articoli art = new Articoli();
					art.setCreatedBy(this.getClass().getSimpleName()+ulss);
					art.setCreationDate(new Date());
					art.setCode(getLaw(artProv.getArticolilegge(),targetProv.getData()));
					art.setProvvedimento(targetProv);

					String violazioni = null;
					if(artProv.getNoteviolazione()!=null){
						int length = artProv.getNoteviolazione().length();
						violazioni = artProv.getNoteviolazione().substring(0,length<=4000?length:4000);
					}
					art.setViolazione(violazioni);

					String prescrizioni = null;
					if(artProv.getNote()!=null){
						int length = artProv.getNote().length();
						prescrizioni = artProv.getNote().substring(0,length<=4000?length:4000);
					}
					art.setPrescrizione(prescrizioni);


					saveOnTarget(art);
					targetProv.addArticoli(art);

					String nrGruppo = "1"; //default
					Gruppi gruppoArticolo = null;
					//questo loop serve solo a vedere se c'è già un gruppo con lo stesso nome tra gli articoli presenti
					if(targetProv.getType()!=null && "phidic.spisal.pratiche.pev.pevtype.758".equals(targetProv.getType().getOid()) && artProv.getDlgs758iter()!=null){
						Dlgs758iter iterSource = artProv.getDlgs758iter();

						if(targetProv.getIter758()!=null && !targetProv.getIter758().isEmpty()){
							Iter758 iterTarget = targetProv.getIter758().get(0);

							if(iterSource.getMagistrato()!=null &&
									iterSource.getMagistrato().getCognome()!=null && iterSource.getMagistrato().getNome()!=null){
								String magistrato = iterSource.getMagistrato().getCognome() + " " + iterSource.getMagistrato().getNome();
								int length = magistrato.length();
								magistrato = magistrato.substring(0,length<=255?length:255);
								iterTarget.setMagistratoStr(magistrato);
							}

							if(iterSource.getProcura()!=null && iterSource.getProcura().getDenominazione()!=null){
								String procura = iterSource.getProcura().getDenominazione();
								int length = procura.length();
								procura = procura.substring(0,length<=255?length:255);
								iterTarget.setProcuraStr(procura);
							}

							if(iterSource.getNumfascicolo()!=null){
								String nrFascicolo = iterSource.getNumfascicolo();
								int length = nrFascicolo.length();
								nrFascicolo = nrFascicolo.substring(0,length<=255?length:255);
								iterTarget.setNumeroFascicolo(nrFascicolo);
							}

							if(iterSource.getLegaleFiducia()!=null &&
									iterSource.getLegaleFiducia().getCognome()!=null && iterSource.getLegaleFiducia().getNome()!=null){
								String legale = iterSource.getLegaleFiducia().getCognome() + " " + iterSource.getLegaleFiducia().getNome();
								int length = legale.length();
								legale = legale.substring(0,length<=255?length:255);
								iterTarget.setLegaleStr(legale);
							}
						}

						if(iterSource.getGruppo()!=null){
							nrGruppo = iterSource.getGruppo().toPlainString();
							for(Articoli a : targetProv.getArticoli()){
								if(a.getGruppo()!=null && nrGruppo.equals(a.getGruppo().getName())){
									gruppoArticolo = a.getGruppo();
									break;
								}
							}
						}
					}



					if(gruppoArticolo==null){
						gruppoArticolo = new Gruppi();
						gruppoArticolo.setCreatedBy(this.getClass().getSimpleName()+ulss);
						gruppoArticolo.setCreationDate(new Date());
						gruppoArticolo.setName(nrGruppo);

					}

					CodeValue esitoArt = null;
					if(targetProv.getType()!=null && "phidic.spisal.pratiche.pev.pevtype.758".equals(targetProv.getType().getOid()) && artProv.getDlgs758iter()!=null){
						Dlgs758iter iter = artProv.getDlgs758iter();

						//GIORNI PROROGA ITER
						if(iter.getGiorniinizialiart()!=null){
							gruppoArticolo.setGiorniIniziali(iter.getGiorniinizialiart().intValue());
						}
						if(iter.getDlgs758giorniprorogas()!=null && !iter.getDlgs758giorniprorogas().isEmpty()){
							Dlgs758giorniproroga primaProroga = iter.getDlgs758giorniprorogas().get(0);
							Dlgs758giorniproroga secondaProroga = null;
							if(iter.getDlgs758giorniprorogas().size()>1){
								secondaProroga=iter.getDlgs758giorniprorogas().get(1);
							}

							if(primaProroga!=null && primaProroga.getGiorni()!=null){
								gruppoArticolo.setPrimaProroga(primaProroga.getGiorni().intValue());
							}
							if(secondaProroga!=null && secondaProroga.getGiorni()!=null){
								if(gruppoArticolo.getPrimaProroga()==null) {
									gruppoArticolo.setPrimaProroga(secondaProroga.getGiorni().intValue());
								}else {
									gruppoArticolo.setSecondaProroga(secondaProroga.getGiorni().intValue());
								}
							}
						}

						if(iter.getScadenzaVerifica()!=null){
							gruppoArticolo.setDataDellaVerifica(iter.getScadenzaVerifica().getDataevasione());
						}

						gruppoArticolo.setAmmissionePagamento(iter.getDatainvioesito());
						gruppoArticolo.setNotificaPagamento(iter.getDataammissionepagamento());

						if(iter.getScadenzaPagamento()!=null){
							gruppoArticolo.setDataPagamento(iter.getScadenzaPagamento().getDataevasione());
							gruppoArticolo.setPagamentoNonEffettuato(false);
						}

						if(sourceProv.getDataarchiviazione()!=null &&
								gruppoArticolo.getDataArchiviazioneSede()==null){
							gruppoArticolo.setDataArchiviazioneSede(sourceProv.getDataarchiviazione());
						}

						if(iter.getSanzioni()!=null){
							Sanzioni sanzione = iter.getSanzioni();
							if(sanzione.getImportoversato()!=null)	{
								if(gruppoArticolo.getImportoVersato()==null) {
									gruppoArticolo.setImportoVersato(sanzione.getImportoversato().doubleValue());
								}else {
									gruppoArticolo.setImportoVersato(gruppoArticolo.getImportoVersato() +
											sanzione.getImportoversato().doubleValue());
								}
								gruppoArticolo.setPagamentoNonEffettuato(false);
							}else {
								//gruppoArticolo.setPagamentoNonEffettuato(true);
							}

							if(sanzione.getSpeseaggiuntive()!=null) {
								if(gruppoArticolo.getSpeseNotifica()==null) {
									gruppoArticolo.setSpeseNotifica(sanzione.getSpeseaggiuntive().doubleValue());
								}else {
									gruppoArticolo.setSpeseNotifica(gruppoArticolo.getSpeseNotifica() +
											sanzione.getSpeseaggiuntive().doubleValue());
								}
							}

						}
						//NON PAGATO
						if(gruppoArticolo.getDataPagamento()==null && gruppoArticolo.getImportoVersato()==null) {
							gruppoArticolo.setPagamentoNonEffettuato(true);
						}

						gruppoArticolo.setDettagli(iter.getNote());

						//ESITO ARTICOLO
						//Esito della verifica: 0=nessuna voce scelta, 1=Ottemperanza, 2=Non ottemperanza, 3=Ottemperanza con diverse modalità
						String esitoArticolo = "";
						if(iter.getOptesito758()!=null)
							esitoArticolo = iter.getOptesito758().toPlainString();

						if("1".equals(esitoArticolo)){
							esitoArt = getCodeValue("phidic.spisal.pratiche.pev.iter758.singleresult.compliedpayed");

						}else if("2".equals(esitoArticolo)){
							esitoArt = getCodeValue("phidic.spisal.pratiche.pev.iter758.singleresult.notcomplaye");

						}else if("3".equals(esitoArticolo)){
							esitoArt = getCodeValue("phidic.spisal.pratiche.pev.iter758.singleresult.complayeddifferent");

						}
					}else if(targetProv.getType()!=null && "phidic.spisal.pratiche.pev.pevtype.301bis".equals(targetProv.getType().getOid())){
						//andrebbe fatto una volta..
						if("1".equals(sourceProv.getChkricorso301())){
							gruppoArticolo.setRicorso(true);
							gruppoArticolo.setDataRicorso(sourceProv.getDataricorso301());

							//EsitoRicorso (Iter301) null=In Attesa di riposta 0=Respinto, 1=Accolto
							if("1".equals(sourceProv.getEsitoricorso301())){
								gruppoArticolo.setRicorsoAccolto(getCodeValue("phidic.generic.YN.Y"));
								gruppoArticolo.setDataEsitoRicorso(sourceProv.getDataaccettazionericorso301());

							}else if("0".equals(sourceProv.getEsitoricorso301())){
								gruppoArticolo.setRicorsoAccolto(getCodeValue("phidic.generic.YN.N"));
								gruppoArticolo.setDataEsitoRicorso(sourceProv.getDatanotificarigetto301());

							}
						}
						gruppoArticolo.setDataDellaVerifica(sourceProv.getDataverifica301());
						if(artProv.getNumgginiziali()!=null){
							gruppoArticolo.setGiorniIniziali(artProv.getNumgginiziali().intValue());
						}

						if(sourceProv.getDataarchiviazione()!=null &&
								gruppoArticolo.getDataArchiviazioneSede()==null){
							gruppoArticolo.setDataArchiviazioneSede(sourceProv.getDataarchiviazione());
						}

						//ESITO ARTICOLO
						//Esito della verifica: null=nessuna voce scelta, 1=Ottemperanza, 0=Non ottemperanza
						String esitoArticolo = "";
						if(artProv.getOttemperanza()!=null)
							esitoArticolo = artProv.getOttemperanza();

						if("1".equals(esitoArticolo)){
							esitoArt = getCodeValue("phidic.spisal.pratiche.pev.iter758.singleresult.compliedpayed");
							gruppoArticolo.setNotificaPagamento(artProv.getDatanotificaammpag301());
							gruppoArticolo.setComunicazioneComune(artProv.getDatacomottcomune301());
							gruppoArticolo.setComune(new AD());
							manageAddrComune(gruppoArticolo.getComune(), artProv.getComuneComunicazioneOtt301());

							if(artProv.getSanzioniarticolis()!=null && !artProv.getSanzioniarticolis().isEmpty()){
								Sanzioniarticoli sa = artProv.getSanzioniarticolis().get(0);
								Sanzioni s = sa.getSanzioni();
								if(s!=null && s.getScadenzaOblazione()!=null){
									//OBBLIGATO IN SOLIDO - noi ne registriamo solo uno: salvo il primo
									if(s.getObbligatoInSolido()!=null && targetProv.getSoggettoInSolido()==null){
										Person p = persImp.importPerson(s.getObbligatoInSolido());
										if(p!=null){
											Soggetto obbligato = new Soggetto();
											obbligato.setCreatedBy(this.getClass().getSimpleName()+ulss);
											obbligato.setCreationDate(new Date());
											obbligato.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

											obbligato.setUtente(p);
											obbligato.addProvvedimenti(targetProv);
											saveOnTarget(obbligato);
											targetProv.setSoggettoInSolido(obbligato);
										}
									}

									//PAGATO
									if("1".equals(s.getScadenzaOblazione().getEvaso())){
										gruppoArticolo.setPagamentoNonEffettuato(false);
										gruppoArticolo.setDataPagamento(s.getDataoblazione());
										if(s.getImportoversato()!=null) {
											if(gruppoArticolo.getImportoVersato()==null) {
												gruppoArticolo.setImportoVersato(s.getImportoversato().doubleValue());
											}else {
												gruppoArticolo.setImportoVersato(gruppoArticolo.getImportoVersato() +
														s.getImportoversato().doubleValue());
											}
										}
									}

									//NON PAGATO
									if(gruppoArticolo.getDataPagamento()==null && gruppoArticolo.getImportoVersato()==null) {
										gruppoArticolo.setPagamentoNonEffettuato(true);
									}
								}
							}

						}else if("0".equals(esitoArticolo)){
							esitoArt = getCodeValue("phidic.spisal.pratiche.pev.iter758.singleresult.notcomplaye");

							gruppoArticolo.setComunicazioneInottemperanza(artProv.getDatacominottcontravv301());
							gruppoArticolo.setComunicazioneComune(artProv.getDatacominottcomune301());
							gruppoArticolo.setComune(new AD());
							manageAddrComune(gruppoArticolo.getComune(), artProv.getComuneComunicazioneInott301());
							gruppoArticolo.setComune(new AD());
							manageAddrComune(gruppoArticolo.getComune(), artProv.getComuneComunicazioneOtt301());

							if(artProv.getSanzioniarticolis()!=null && !artProv.getSanzioniarticolis().isEmpty()){
								Sanzioniarticoli sa = artProv.getSanzioniarticolis().get(0);
								Sanzioni s = sa.getSanzioni();
								//OBBLIGATO IN SOLIDO - noi ne registriamo solo uno: salvo il primo
								if(s.getObbligatoInSolido()!=null && targetProv.getSoggettoInSolido()==null){
									Person p = persImp.importPerson(s.getObbligatoInSolido());
									if(p!=null){
										Soggetto obbligato = new Soggetto();
										obbligato.setCreatedBy(this.getClass().getSimpleName()+ulss);
										obbligato.setCreationDate(new Date());
										obbligato.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

										obbligato.setUtente(p);
										obbligato.addProvvedimenti(targetProv);
										saveOnTarget(obbligato);
										targetProv.setSoggettoInSolido(obbligato);
									}
								}
								if(s!=null && s.getScadenzaOblazione()!=null){
									//PAGATO
									if("1".equals(s.getScadenzaOblazione().getEvaso())){
										gruppoArticolo.setPagamentoNonEffettuato(false);
										gruppoArticolo.setDataPagamento(s.getDataoblazione());
										if(s.getImportoversato()!=null) {
											if(gruppoArticolo.getImportoVersato()==null) {
												gruppoArticolo.setImportoInottemperanza(s.getImportoversato().doubleValue());
											}else {
												gruppoArticolo.setImportoInottemperanza(gruppoArticolo.getImportoInottemperanza() +
														s.getImportoversato().doubleValue());
											}
										}
									}
								}
							}
							//NON PAGATO
							if(gruppoArticolo.getDataPagamento()==null && gruppoArticolo.getImportoVersato()==null) {
								gruppoArticolo.setPagamentoNonEffettuato(true);
							}
						}
					}
					art.setEsito((CodeValuePhi)esitoArt);

					saveOnTarget(gruppoArticolo);
					art.setGruppo(gruppoArticolo);
					gruppoArticolo.addArticoli(art);
					saveOnTarget(art);
				}
			}
		}
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingProv(long id){
		MapProvvedimenti m = sourceEm.find(MapProvvedimenti.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapProvvedimenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapProvvedimenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapProvvedimenti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingMig(long id){
		MapMiglioramenti m = sourceEm.find(MapMiglioramenti.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapMiglioramenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapMiglioramenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapMiglioramenti map = list.get(0);
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
	private Provvedimenti getMappedProv(long id){
		String hqlMapping = "SELECT m FROM MapProvvedimenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapProvvedimenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapProvvedimenti map = list.get(0);
			Query qProvvedimenti = targetEm.createQuery("SELECT m FROM Provvedimenti m WHERE m.internalId = :id");
			qProvvedimenti.setParameter("id", map.getIdphi());
			List<Provvedimenti> lp = qProvvedimenti.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Miglioramenti getMappedMig(long id){
		String hqlMapping = "SELECT m FROM MapMiglioramenti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapMiglioramenti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapMiglioramenti map = list.get(0);
			Query qMiglioramenti = targetEm.createQuery("SELECT m FROM Miglioramenti m WHERE m.internalId = :id");
			qMiglioramenti.setParameter("id", map.getIdphi());
			List<Miglioramenti> lp = qMiglioramenti.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMappingProv(ProvvedimentiPrevnet source, Provvedimenti target){
		MapProvvedimenti map = new MapProvvedimenti();
		map.setIdprevnet(source.getIdprovvedimenti());
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

	private void saveMappingMig(ProvvedimentiPrevnet source, Miglioramenti target){
		MapMiglioramenti map = new MapMiglioramenti();
		map.setIdprevnet(source.getIdprovvedimenti());
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
		String hqlProvvedimenti = "SELECT mf.idphi FROM MapProvvedimenti mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlProvvedimenti+="WHERE mf.ulss = :ulss";

		Query qProvvedimenti = sourceEm.createQuery(hqlProvvedimenti);
		if(ulss!=null && !ulss.isEmpty())
			qProvvedimenti.setParameter("ulss", ulss);

		List<Long> allIdProv = qProvvedimenti.getResultList();
		List<Long> idProv = new ArrayList<Long>();
		while(allIdProv!=null && !allIdProv.isEmpty()){
			if(allIdProv.size()>1000){
				idProv.clear();
				idProv.addAll(allIdProv.subList(0, 1000));
				allIdProv.removeAll(idProv);
			}else{
				idProv.clear();
				idProv.addAll(allIdProv);
				allIdProv.removeAll(idProv);
			}
			if(commit){

				String hqlArt = "SELECT a.internalId FROM Articoli a JOIN a.provvedimento p WHERE p.internalId IN (:ids)";
				Query qArt = targetEm.createQuery(hqlArt);
				qArt.setParameter("ids", idProv);

				List<Long> idArticoli = qArt.getResultList();
				if(idArticoli!=null && !idArticoli.isEmpty()){
					String hqlGruppi = "SELECT g.internalId FROM Articoli a JOIN a.gruppo g WHERE a.internalId IN (:ids)";
					Query qGruppi = targetEm.createQuery(hqlGruppi);
					qGruppi.setParameter("ids", idArticoli);

					List<Long> idGruppi = qGruppi.getResultList();
					String deleteArts = "DELETE FROM Articoli a WHERE a.internalId IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelArts = targetEm.createQuery(deleteArts);
					qDelArts.setParameter("ids", idArticoli);
					qDelArts.executeUpdate();
					targetEm.getTransaction().commit();

					if(idGruppi!=null && !idGruppi.isEmpty()){
						String deleteGroups = "DELETE FROM Gruppi a WHERE a.internalId IN (:ids)";
						targetEm.getTransaction().begin();
						Query qDelGroups = targetEm.createQuery(deleteGroups);
						qDelGroups.setParameter("ids", idGruppi);
						qDelGroups.executeUpdate();
						targetEm.getTransaction().commit();
					}
				}

				String deleteProv = "DELETE FROM Provvedimenti e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelProv = targetEm.createQuery(deleteProv);
				qDelProv.setParameter("ids", idProv);
				qDelProv.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_PROVVEDIMENTI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}
}
