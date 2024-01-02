package com.phi.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.hibernate.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;

import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.AddebitoAction;
import com.phi.entities.actions.VerificaImpAction;
import com.phi.entities.baseEntity.ImpiantiDocument;
import com.phi.entities.dataTypes.CodeValuePhi;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ImpiantiDocumentRest")
@Path("/impiantidocuments")
public class ImpiantiDocumentRest extends BaseRest<ImpiantiDocument> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4927826699510772653L;

	@POST
	@Path("/verificaimp/list")
	public boolean selectVerifList(String list){
		list = list.replaceAll("[\\[\\]]", "");
		String[] idStringList = list.split(",");
		List<Long> selectedVers = new ArrayList<Long>();
		for(String s : idStringList){
			Long idVer = Long.valueOf(s);
			selectedVers.add(idVer);
		}
		VerificaImpAction va = VerificaImpAction.instance();
		va.getTemporary().put("selectedVers",selectedVers);
		return true;
	}

	@POST
	@Path("/addebito/list")
	public void selectAddList(String list){
		list = list.replaceAll("[\\[\\]]", "");
		String[] idStringList = list.split(",");
		List<Long> selectedAdds = new ArrayList<Long>();
		for(String s : idStringList){
			Long idAdd = Long.valueOf(s);
			selectedAdds.add(idAdd);
		}
		AddebitoAction aa = AddebitoAction.instance();
		aa.getTemporary().put("selectedAdds",selectedAdds);
	}

	@GET
	@Path("/verificaimp/status/")
	public String getVerifValidationStatus(){
		Query q = ca.createHibernateQuery("SELECT l.objectIdentifier FROM DataBaseLog l WHERE l.internalId = 1");
		String vId = (String)q.uniqueResult();
		return vId==null?"0":vId;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/verificaimp/causale")
	public int putCodeValeCusaleInTmp(String payload){
		String[] params = payload.split(";");
		Manager.instance().switchConversation(params[0].trim());
		VerificaImpAction va = VerificaImpAction.instance();
		HashMap<String, CodeValuePhi> causali = null;
		if(va.getTemporary().get("causali")==null){
			causali = new HashMap<String, CodeValuePhi>();
		}else{
			causali = (HashMap<String, CodeValuePhi>)va.getTemporary().get("causali") ;
		}

		//		Vocabularies voc = VocabulariesImpl.instance();
		//		try {
		//			causali.put(params[1].trim(), (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale." + params[1].trim()));
		//		} catch (PersistenceException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		Query qGetCv = ca.createHibernateQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid ORDER by cv.version");
		qGetCv.setParameter("oid", "phidic.arpav.ver.causale." + params[1].trim());
		List<CodeValuePhi> cvList = (List<CodeValuePhi>) qGetCv.list();
		causali.put(params[1].trim(), cvList.get(0));
		va.getTemporary().put("causali",causali);

		return ((HashMap<String, CodeValuePhi>)va.getTemporary().get("causali")).size();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/verificaimp/causales")
	public boolean putCausaleListInTmp(String cid){
		Manager.instance().switchConversation(cid);
		VerificaImpAction va = VerificaImpAction.instance();
		String[] causaleOids = {"04","04R","23","23R","24","24R","25","25R","26","26R","36","36R","86","86R"};
		HashMap<String, CodeValuePhi> causali = new HashMap<String, CodeValuePhi>();

		for(String causaleOid: causaleOids){
			Query qGetCv = ca.createHibernateQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid ORDER by cv.version DESC");
			qGetCv.setParameter("oid", "phidic.arpav.ver.causale." + causaleOid);
			List<CodeValuePhi> cvList = (List<CodeValuePhi>) qGetCv.list();
			causali.put(causaleOid, cvList.get(0));
		}
		va.getTemporary().put("causali",causali);

		return true;

	}

}