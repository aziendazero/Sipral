package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

import com.phi.cs.catalog.adapter.sequence.SequenceManager;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.ParereTecnico;
import com.phi.entities.baseEntity.Patentini;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ParereTecnicoAction")
@Scope(ScopeType.CONVERSATION)
public class ParereTecnicoAction extends BaseAction<ParereTecnico, Long> {

	private static final long serialVersionUID = 569222910L;

	public static ParereTecnicoAction instance() {
		return (ParereTecnicoAction) Component.getInstance(ParereTecnicoAction.class, ScopeType.CONVERSATION);
	}
	private static final Logger log = Logger.getLogger(ParereTecnicoAction.class);

	public void manageTermini(List<Protocollo> lst) throws PhiException{
		ParereTecnico p = getEntity();
		if(p!=null && p.getInternalId()<=0 && lst!=null && !lst.isEmpty()){
			for(Protocollo prot : lst){
				if(prot.getIsMaster()){
					DettagliBonificheAction dAction = DettagliBonificheAction.instance();
					dAction.equal.put("protocollo",prot);
					List<DettagliBonifiche> ds = dAction.select();
					if(ds!=null && !ds.isEmpty()){
						DettagliBonifiche d = ds.get(0);
						p.setTerminiRisposta(d.getTerminiRisposta());
						p.setTerminiOriginali(d.getTerminiRisposta());
						return;
					}
				}
			}
		
		}/*else if(p!=null && p.getInternalId()<=0){
			p.setTerminiOriginali(p.getTerminiRisposta());
		}*/
	}
	
	public void clearValues(ParereTecnico par) throws PersistenceException{
		if(par!=null && par.getType()!=null){
			par.setRichiesta(null);
			par.setProvType(null);
			par.setClassCode(null);
			par.setClassText(null);
			par.setGradoPericolosita(null);
			par.setPerson(null);
			par.setDuratagg(null);
			par.setPatentini(null);
			par.setPatType(null);
			par.setDataParto(null);
			par.setIdoneita(null);
			par.setMatricola(null);
			
			if(par.getPersonaGiuridicaSede()!=null && !par.getPersonaGiuridicaSede().isEmpty()){
				PersonaGiuridicaSede type1 = null;
				for(PersonaGiuridicaSede p : par.getPersonaGiuridicaSede()){
					if(p.getType()!=null && !"DTYPE01".equals(p.getType().getCode())){
						p.setParereTecnico(null);
						p.setPersonaGiuridica(null);
						p.setSede(null);
						ca.delete(p);
					}else if(p.getType()!=null && "DTYPE01".equals(p.getType().getCode())){
						type1=p;
					}
				}
				List<PersonaGiuridicaSede> tmpList = new ArrayList<PersonaGiuridicaSede>();
				tmpList.add(type1);
				par.setPersonaGiuridicaSede(tmpList);
				PersonaGiuridicaSedeAction.instance().injectList(tmpList);
			}
			
			if("TA08".equals(par.getType().getCode())){
				par.setParere(null);
				IdataModel im = (IdataModel) Contexts.getConversationContext().get("ProtocolloList");
				if(im!=null && im.getList()!=null && !im.getList().isEmpty()){
					Protocollo master = null;
					List<Protocollo> lst = (List<Protocollo>)im.getList();
					for(Protocollo p : lst){
						if(p.getIsMaster()){
							master = p;
							break;
						}
					}
					if(master!=null)
						par.setPatDomDate(master.getData());
				}
			}
			
			ca.flushSession();
		}
	}

	public String printList(List<CodeValuePhi> gaslist){
		String rtn = "";
		if(gaslist!=null){	
			for(CodeValuePhi gas : gaslist){
				rtn+=(gas.getCurrentTranslation()+"\n");
			}
		}
		
		return rtn;
	}

	public void setMatricola(ParereTecnico par, Patentini pat) throws PhiException{
		if(par!=null && par.getIdoneita()!=null && pat!=null){
			if("PARPAT1".equals(par.getIdoneita().getCode()) && par.getDataEsame()!=null){
				Date dataEsame = par.getDataEsame();
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEsame);
				int year = cal.get(Calendar.YEAR);
				ProcpraticheAction pAction = ProcpraticheAction.instance();
				Procpratiche pratica = pAction.getEntity();
				if(pratica!=null && pratica.getServiceDeliveryLocation()!=null
					&& pratica.getServiceDeliveryLocation().getAddr()!=null){
					
					String provStruttura = pratica.getServiceDeliveryLocation().getAddr().getCpa();
					
					SequenceManager sm = (SequenceManager)Component.getInstance(SequenceManager.class, ScopeType.EVENT);
					sm.setOwnerId(provStruttura);
					String matricola = sm.nextOf("matricola", year+"");
					pat.setMatricola(provStruttura+"/"+year+"/"+matricola);
					sm.unlockSequence("matricola");
				}
				
				
			}else{
				par.setMatricola(null);
			}
		}
		
	}
	public void checkAndInject(ParereTecnico par, Procpratiche proc,boolean emptyReadList) throws PhiException{
		if(par==null){
			this.injectFirst();
			log.info("[cid="+Conversation.instance().getId()+"] there is no ParereTecnico in Conversation so inject from list if exist");
			return;
		}
		else{
			if (par.getInternalId()<=0){
				if(emptyReadList){
					log.info("[cid="+Conversation.instance().getId()+"] new ParereTecnico is just in conversation and there is not any ParereTecnico associated to Procpratiche");
					return;
				}
				else{
					this.injectFirst();
					log.info("[cid="+Conversation.instance().getId()+"] new ParereTecnico is just in conversation but  there is a ParereTecnico associated to Procpratiche so inject this ParereTecnico just associated");
					return;
				}
			}
			else if( par.getProcpratiche()==null){
				log.info("[cid="+Conversation.instance().getId()+"] there is just a created ParereTecnico in conv but not associated to Procpratiche,it will be associated to Procpratiche in next nodes");
				return;

			}
			else {
				if(par.getProcpratiche().getInternalId()!=proc.getInternalId()){
					this.injectFirst();
					log.info("[cid="+Conversation.instance().getId()+"] there is just  a created ParereTecnico in conv that is just associated to a Procpratiche but it's not the right ParereTecnico cause has the wrong Procpratiche,so inject first of Read list");
					return;
				}
				else{
					log.info("[cid="+Conversation.instance().getId()+"] there is just  a created ParereTecnico in conv that is just associated to a right Procpratiche,so continue to use that");
					return;
				}

			}
		}
	}
}