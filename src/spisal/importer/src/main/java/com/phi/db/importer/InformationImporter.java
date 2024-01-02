package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.BenessereOrg;
import com.phi.entities.baseEntity.Formazione;
import com.phi.entities.baseEntity.Informazione;
import com.phi.entities.baseEntity.Lifestyle;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Operatore;
import com.prevnet.entities.Interventiattivitasvolta;
import com.prevnet.entities.Interventiformazione;
import com.prevnet.entities.Operatori;
import com.prevnet.entities.Pratiche;
import com.prevnet.entities.Riunionioperative;

@SuppressWarnings({"unchecked"})
public class InformationImporter extends EntityManagerUtilities {

	private static InformationImporter instance = null;
	
	public static InformationImporter getInstance() {
		if(instance == null) {
			instance = new InformationImporter();
		}
		return instance;
	}

	public Object importInformation(Pratiche source, Procpratiche target){
		OperatoriImporter opImp = OperatoriImporter.getInstance();
		RiunioniImporter riunImp = RiunioniImporter.getInstance();

		if(target.getServiceDeliveryLocation()!=null && target.getServiceDeliveryLocation().getArea()!=null){
			String workingLine = target.getServiceDeliveryLocation().getArea().getOid();

			if(workingLine.endsWith("training") 
					&& source.getInterventiformaziones()!=null && !source.getInterventiformaziones().isEmpty()){
				if(target.getFormazione()==null){
					Formazione form = new Formazione();
					form.setCreatedBy(this.getClass().getSimpleName()+ulss);
					form.setCreationDate(new Date());
					form.setProcpratiche(target);
					Interventiformazione intervento = source.getInterventiformaziones().get(0);
					String dettagli="";
					if(intervento.getAreatematica()!=null){
						dettagli+=("AREA TEMATICA: "+intervento.getAreatematica().getDescrizione()+"\r\n");
					}
					if(intervento.getMacroprodotto()!=null){
						dettagli+=("MACROPRODOTTO: "+intervento.getMacroprodotto().getDescrizione()+"\r\n");
					}
					if(intervento.getNoteint()!=null){
						dettagli+= intervento.getNoteint();
						int length = dettagli.length();
						dettagli = dettagli.substring(0,length<=4000?length:4000);
						form.setDettagli(dettagli);
					}
					
					List<CodeValuePhi> tipiAttivita = form.getTipoAttivita();
					if(tipiAttivita==null)
						tipiAttivita = new ArrayList<CodeValuePhi>();
					
					if(intervento.getInterventiattivitasvoltas()!=null && !intervento.getInterventiattivitasvoltas().isEmpty()){
						for(Interventiattivitasvolta attivita : intervento.getInterventiattivitasvoltas()){
							CodeValuePhi toBeAdded = (CodeValuePhi)getMappedCode(attivita.getTipoAttivita(), ulss);
							toBeAdded = targetEm.find(CodeValuePhi.class, toBeAdded.getId());
							if(attivita.getTipoAttivita()!=null && toBeAdded!=null && "prevn07".equals(toBeAdded.getCode())){
								riunImp.importIntervento(attivita);	//PRODUZIONE MATERIALE IN-FORMATIVO
								
							}else{
								
								if(toBeAdded!=null && !tipiAttivita.contains(toBeAdded))
									tipiAttivita.add(toBeAdded);
								
								
								//OPERATORI
								if(target.getOperatori()==null)
									target.setOperatori(new ArrayList<Operatore>());
								
								for(Operatori op : attivita.getOperatoris()){
									Operatore o = opImp.importOperatore(op);
									if(o!=null){
										if(!target.getOperatori().contains(o))
											target.getOperatori().add(o);

										if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
											if(target.getUpg()==null)
												target.setUpg(new ArrayList<Operatore>());

											if(!target.getUpg().contains(o))
												target.getUpg().add(o);
										}
									}
								}
							}
						}
						form.setTipoAttivita(tipiAttivita);
					}
					
					saveOnTarget(form);
					return form;

				}else{
					return target.getFormazione();
				}
			}else if(workingLine.endsWith("information") 
					&& source.getInterventiformaziones()!=null && !source.getInterventiformaziones().isEmpty()){
				if(target.getInformazione()==null){
					Informazione info = new Informazione();
					info.setCreatedBy(this.getClass().getSimpleName()+ulss);
					info.setCreationDate(new Date());
					info.setProcpratiche(target);
					Interventiformazione intervento = source.getInterventiformaziones().get(0);
					String dettagli="";
					if(intervento.getAreatematica()!=null){
						dettagli+=("AREA TEMATICA: "+intervento.getAreatematica().getDescrizione()+"\r\n");
					}
					if(intervento.getMacroprodotto()!=null){
						dettagli+=("MACROPRODOTTO: "+intervento.getMacroprodotto().getDescrizione()+"\r\n");
					}
					if(intervento.getNoteint()!=null){
						dettagli = intervento.getNoteint();
						int length = dettagli.length();
						dettagli = dettagli.substring(0,length<=4000?length:4000);
						info.setDettagli(dettagli);
					}

					List<CodeValuePhi> tipiAttivita = info.getTipoAttivita();
					if(tipiAttivita==null)
						tipiAttivita = new ArrayList<CodeValuePhi>();
					
					if(intervento.getInterventiattivitasvoltas()!=null && !intervento.getInterventiattivitasvoltas().isEmpty()){
						for(Interventiattivitasvolta attivita : intervento.getInterventiattivitasvoltas()){
							if(attivita.getTipoAttivita()!=null && "05".equals(attivita.getTipoAttivita().getUtcodice())){
								riunImp.importIntervento(attivita);
								
							}else{
								CodeValuePhi toBeAdded = (CodeValuePhi)getMappedCode(attivita.getTipoAttivita(), ulss);
								toBeAdded = targetEm.find(CodeValuePhi.class, toBeAdded.getId());

								if(toBeAdded!=null && !tipiAttivita.contains(toBeAdded))
									tipiAttivita.add(toBeAdded);
								
								
								//OPERATORI
								if(target.getOperatori()==null)
									target.setOperatori(new ArrayList<Operatore>());
								
								for(Operatori op : attivita.getOperatoris()){
									Operatore o = opImp.importOperatore(op);
									if(o!=null){
										if(!target.getOperatori().contains(o))
											target.getOperatori().add(o);

										if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
											if(target.getUpg()==null)
												target.setUpg(new ArrayList<Operatore>());

											if(!target.getUpg().contains(o))
												target.getUpg().add(o);
										}
									}
								}
							}
						}
						info.setTipoAttivita(tipiAttivita);
					}
					
					saveOnTarget(info);
					return info;
					
				}else{
					return target.getInformazione();
				}
			}else if(workingLine.endsWith("lifestyle") 
					&& source.getInterventiformaziones()!=null && !source.getInterventiformaziones().isEmpty()){
				if(target.getLifestyle()==null){
					Lifestyle lifestyle = new Lifestyle();
					lifestyle.setCreatedBy(this.getClass().getSimpleName()+ulss);
					lifestyle.setCreationDate(new Date());
					lifestyle.setProcpratiche(target);
					Interventiformazione intervento = source.getInterventiformaziones().get(0);
					String dettagli="";
					if(intervento.getAreatematica()!=null){
						dettagli+=("AREA TEMATICA: "+intervento.getAreatematica().getDescrizione()+"\r\n");
					}
					if(intervento.getMacroprodotto()!=null){
						dettagli+=("MACROPRODOTTO: "+intervento.getMacroprodotto().getDescrizione()+"\r\n");
					}
					if(intervento.getNoteint()!=null){
						dettagli = intervento.getNoteint();
						int length = dettagli.length();
						dettagli = dettagli.substring(0,length<=4000?length:4000);
						lifestyle.setDettagli(dettagli);
					}

					List<CodeValuePhi> tipiAttivita = lifestyle.getTipoAttivita();
					if(tipiAttivita==null)
						tipiAttivita = new ArrayList<CodeValuePhi>();
					
					if(intervento.getInterventiattivitasvoltas()!=null && !intervento.getInterventiattivitasvoltas().isEmpty()){
						for(Interventiattivitasvolta attivita : intervento.getInterventiattivitasvoltas()){
							if(attivita.getTipoAttivita()!=null && "05".equals(attivita.getTipoAttivita().getUtcodice())){
								riunImp.importIntervento(attivita);
								
							}else{
								CodeValuePhi toBeAdded = (CodeValuePhi)getMappedCode(attivita.getTipoAttivita(), ulss);
								toBeAdded = targetEm.find(CodeValuePhi.class, toBeAdded.getId());

								if(toBeAdded!=null && !tipiAttivita.contains(toBeAdded))
									tipiAttivita.add(toBeAdded);
								
								
								//OPERATORI
								if(target.getOperatori()==null)
									target.setOperatori(new ArrayList<Operatore>());
								
								for(Operatori op : attivita.getOperatoris()){
									Operatore o = opImp.importOperatore(op);
									if(o!=null){
										if(!target.getOperatori().contains(o))
											target.getOperatori().add(o);

										if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
											if(target.getUpg()==null)
												target.setUpg(new ArrayList<Operatore>());

											if(!target.getUpg().contains(o))
												target.getUpg().add(o);
										}
									}
								}
							}
						}
						lifestyle.setTipoAttivita(tipiAttivita);
					}
					
					saveOnTarget(lifestyle);
					return lifestyle;
					
				}else{
					return target.getLifestyle();
				}
			}else if(workingLine.endsWith("counseling") 
					&& source.getInterventiformaziones()!=null && !source.getInterventiformaziones().isEmpty()){
				if(target.getBenessereOrg()==null){
					BenessereOrg benessere = new BenessereOrg();
					benessere.setCreatedBy(this.getClass().getSimpleName()+ulss);
					benessere.setCreationDate(new Date());
					benessere.setProcpratiche(target);
					Interventiformazione intervento = source.getInterventiformaziones().get(0);
					String dettagli="";
					if(intervento.getAreatematica()!=null){
						dettagli+=("AREA TEMATICA: "+intervento.getAreatematica().getDescrizione()+"\r\n");
					}
					if(intervento.getMacroprodotto()!=null){
						dettagli+=("MACROPRODOTTO: "+intervento.getMacroprodotto().getDescrizione()+"\r\n");
					}
					if(intervento.getNoteint()!=null){
						dettagli = intervento.getNoteint();
						int length = dettagli.length();
						dettagli = dettagli.substring(0,length<=4000?length:4000);
						benessere.setDettagli(dettagli);
					}

					List<CodeValuePhi> tipiAttivita = benessere.getTipoAttivita();
					if(tipiAttivita==null)
						tipiAttivita = new ArrayList<CodeValuePhi>();
					
					if(intervento.getInterventiattivitasvoltas()!=null && !intervento.getInterventiattivitasvoltas().isEmpty()){
						for(Interventiattivitasvolta attivita : intervento.getInterventiattivitasvoltas()){
							if(attivita.getTipoAttivita()!=null && "05".equals(attivita.getTipoAttivita().getUtcodice())){
								riunImp.importIntervento(attivita);
								
							}else{
								CodeValuePhi toBeAdded = (CodeValuePhi)getMappedCode(attivita.getTipoAttivita(), ulss);
								toBeAdded = targetEm.find(CodeValuePhi.class, toBeAdded.getId());

								if(toBeAdded!=null && !tipiAttivita.contains(toBeAdded))
									tipiAttivita.add(toBeAdded);
								
								
								//OPERATORI
								if(target.getOperatori()==null)
									target.setOperatori(new ArrayList<Operatore>());
								
								for(Operatori op : attivita.getOperatoris()){
									Operatore o = opImp.importOperatore(op);
									if(o!=null){
										if(!target.getOperatori().contains(o))
											target.getOperatori().add(o);

										if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
											if(target.getUpg()==null)
												target.setUpg(new ArrayList<Operatore>());

											if(!target.getUpg().contains(o))
												target.getUpg().add(o);
										}
									}
								}
							}
						}
						benessere.setTipoAttivita(tipiAttivita);
					}
					
					saveOnTarget(benessere);
					return benessere;
					
				}else{
					return target.getBenessereOrg();
				}
			}
			
			if(source.getRiunionioperatives()!=null && !source.getRiunionioperatives().isEmpty()){
				for(Riunionioperative riunione : source.getRiunionioperatives()){
					riunImp.importRiunione(riunione);
				}
			}
		}
		
		
		return null;	
	}

	@Override
	protected void deleteImportedData(String ulss) {

		String hqlPratiche = "SELECT mf.idphi FROM MapPratiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPratiche+="WHERE mf.ulss = :ulss";

		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		if(ulss!=null && !ulss.isEmpty())
			qPratiche.setParameter("ulss", ulss);

		List<Long> allIdPrat = qPratiche.getResultList();
		List<Long> idPrat = new ArrayList<Long>();
		while(allIdPrat!=null && !allIdPrat.isEmpty()){
			if(allIdPrat.size()>1000){
				idPrat.clear();
				idPrat.addAll(allIdPrat.subList(0, 1000));
				allIdPrat.removeAll(idPrat);
			}else{
				idPrat.clear();
				idPrat.addAll(allIdPrat);
				allIdPrat.removeAll(idPrat);
			}
			if(commit){
				String hqlFormazione = "SELECT f.internalId FROM Formazione f JOIN f.procpratiche p WHERE p.internalId IN (:ids)";
				Query qFormazione = targetEm.createQuery(hqlFormazione);
				qFormazione.setParameter("ids", idPrat);
				List<Long> idFormazione = qFormazione.getResultList();
				if(idFormazione!=null && !idFormazione.isEmpty()){
					String deleteArticoli = "DELETE FROM formazione_arglegge81 WHERE formazione_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelArticoli = targetEm.createNativeQuery(deleteArticoli);
					qDelArticoli.setParameter("ids", idFormazione);
					qDelArticoli.executeUpdate();
					targetEm.getTransaction().commit();
										
					String deleteTipoAttivita = "DELETE FROM formazione_tipoattivita WHERE formazione_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelTipoAttivita = targetEm.createNativeQuery(deleteTipoAttivita);
					qDelTipoAttivita.setParameter("ids", idFormazione);
					qDelTipoAttivita.executeUpdate();
					targetEm.getTransaction().commit();
					
					String delFormHql = "DELETE FROM formazione WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelFormazione = targetEm.createNativeQuery(delFormHql);
					qDelFormazione.setParameter("ids", idFormazione);
					qDelFormazione.executeUpdate();
					targetEm.getTransaction().commit();
				}

				String hqlInformazione = "SELECT f.internalId FROM Informazione f JOIN f.procpratiche p WHERE p.internalId IN (:ids)";
				Query qInformazione = targetEm.createQuery(hqlInformazione);
				qInformazione.setParameter("ids", idPrat);
				List<Long> idInformazione = qInformazione.getResultList();
				if(idInformazione!=null && !idInformazione.isEmpty()){
					String deleteArticoli = "DELETE FROM info_arglegge81 WHERE informazione_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelArticoli = targetEm.createNativeQuery(deleteArticoli);
					qDelArticoli.setParameter("ids", idInformazione);
					qDelArticoli.executeUpdate();
					targetEm.getTransaction().commit();
					
					String deleteTipoAttivita = "DELETE FROM informazione_tipoattivita WHERE informazione_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelTipoAttivita = targetEm.createNativeQuery(deleteTipoAttivita);
					qDelTipoAttivita.setParameter("ids", idInformazione);
					qDelTipoAttivita.executeUpdate();
					targetEm.getTransaction().commit();
					
					String delInformHql = "DELETE FROM informazione WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelInformazione = targetEm.createNativeQuery(delInformHql);
					qDelInformazione.setParameter("ids", idInformazione);
					qDelInformazione.executeUpdate();
					targetEm.getTransaction().commit();
				}

				String hqlLifestyle = "SELECT f.internalId FROM Lifestyle f JOIN f.procpratiche p WHERE p.internalId IN (:ids)";
				Query qLifestyle = targetEm.createQuery(hqlLifestyle);
				qLifestyle.setParameter("ids", idPrat);
				List<Long> idLifestyle = qLifestyle.getResultList();
				if(idLifestyle!=null && !idLifestyle.isEmpty()){
					String deleteArticoli = "DELETE FROM lifestyle_arglegge81 WHERE lifestyle_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelArticoli = targetEm.createNativeQuery(deleteArticoli);
					qDelArticoli.setParameter("ids", idLifestyle);
					qDelArticoli.executeUpdate();
					targetEm.getTransaction().commit();
					
					String deleteTipoAttivita = "DELETE FROM lifestyle_tipoattivita WHERE lifestyle_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelTipoAttivita = targetEm.createNativeQuery(deleteTipoAttivita);
					qDelTipoAttivita.setParameter("ids", idLifestyle);
					qDelTipoAttivita.executeUpdate();
					targetEm.getTransaction().commit();
					
					String delFormHql = "DELETE FROM lifestyle WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelLifestyle = targetEm.createNativeQuery(delFormHql);
					qDelLifestyle.setParameter("ids", idLifestyle);
					qDelLifestyle.executeUpdate();
					targetEm.getTransaction().commit();
				}

				String hqlBenessereOrg = "SELECT f.internalId FROM BenessereOrg f JOIN f.procpratiche p WHERE p.internalId IN (:ids)";
				Query qBenessereOrg = targetEm.createQuery(hqlBenessereOrg);
				qBenessereOrg.setParameter("ids", idPrat);
				List<Long> idBenessereOrg = qBenessereOrg.getResultList();
				if(idBenessereOrg!=null && !idBenessereOrg.isEmpty()){
					String deleteArticoli = "DELETE FROM benorg_arglegge81 WHERE benorg_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelArticoli = targetEm.createNativeQuery(deleteArticoli);
					qDelArticoli.setParameter("ids", idBenessereOrg);
					qDelArticoli.executeUpdate();
					targetEm.getTransaction().commit();
					
					String deleteTipoAttivita = "DELETE FROM benessereorg_tipoattivita WHERE benessereorg_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelTipoAttivita = targetEm.createNativeQuery(deleteTipoAttivita);
					qDelTipoAttivita.setParameter("ids", idBenessereOrg);
					qDelTipoAttivita.executeUpdate();
					targetEm.getTransaction().commit();
					
					String delFormHql = "DELETE FROM benessere_org WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelBenessereOrg = targetEm.createNativeQuery(delFormHql);
					qDelBenessereOrg.setParameter("ids", idBenessereOrg);
					qDelBenessereOrg.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}
	}
}