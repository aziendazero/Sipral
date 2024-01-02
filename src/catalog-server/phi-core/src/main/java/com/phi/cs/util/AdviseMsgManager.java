package com.phi.cs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.entities.actions.AdviseMsgAction;
import com.phi.entities.baseEntity.AdviseMsg;

/**
 * AdviseMsg manager runs at startup to load existing Adivise Msg which need to be shown.  
 * @author Francesco Bragagna
 */



@BypassInterceptors
@Name("AdviseMsgManager")
@Scope(ScopeType.APPLICATION)
//@Startup
public class AdviseMsgManager {

	public static AdviseMsgManager instance() {
		return (AdviseMsgManager) Component.getInstance(AdviseMsgManager.class, ScopeType.APPLICATION);
	}

	private AdviseMsg adviseMessage = null;
	private AdviseMsg adviseTestMessage = null;
	private Long adviseMessageAuthorId = null;
	List<Long> adviseMessageUserRestriction = new ArrayList<Long>();
	List<String> adviseMessageRoleRestriction = new ArrayList<String>();
	List<Long> adviseMessageSdlRestriction = new ArrayList<Long>();


	@Create
	public void init() {


		//load advise messages if any configured.
		
		
		Date now = new Date();
		String qry = "SELECT am FROM AdviseMsg am" +
				" WHERE am.isActive = :isActive" +
				" AND am.active = :active" +
				" AND am.scheduleTo = :now" +
				" AND am.scheduleFrom = :now";

		List<AdviseMsg> msgs = null;
		
		try {
			GenericAdapterLocalInterface ga = GenericAdapter.instance();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("now", now);
			pars.put("isActive", true);
			pars.put("activective", true);
			
			msgs = (List<AdviseMsg>)ga.executeHQL(qry, pars);
		} catch (Exception e) {
			// qualcosa
		}

		AdviseMsgAction ama = new AdviseMsgAction();
		AdviseMsg msg = null;

		if (msgs != null && msgs.size() == 1) {
			msg = msgs.get(0);
			adviseMessage = msg;
		}

		adviseMessageUserRestriction = ama.getEmployeesId(msg);
		adviseMessageRoleRestriction = ama.getRoleCodes(msg);
		adviseMessageSdlRestriction = ama.getSdlIds(msg);

		
		//TEST message cannot be read at startup because Session bean UserBean is not initialized.  
//		AdviseMsg testMsg = ama.readTest();
//		if (testMsg != null) {
//			adviseTestMessage = testMsg;
//			adviseMessageAuthorId = testMsg.getAuthor().getInternalId();
//		}



	}




	public AdviseMsg getAdviseMessage() {
		return adviseMessage;
	}

	public AdviseMsg getAdviseTestMessage() {
		return adviseTestMessage;
	}

	public Long getAuthorId() {
		return adviseMessageAuthorId;
	}

	public List<Long> getAdviseMessageUserRestriction() {
		return adviseMessageUserRestriction;
	}

	public List<String> getAdviseMessageRoleRestriction() {
		return adviseMessageRoleRestriction;
	}

	public List<Long> getAdviseMessageSdlRestriction() {
		return adviseMessageSdlRestriction;
	}

	public void setTestAdviseMessage (AdviseMsg msg) {
		if (msg == null) {
			cleanTestMessage();
			return;
		}
		
		if (msg.getTest() == true) {
			adviseTestMessage = msg;
			adviseMessageAuthorId = msg.getAuthor().getInternalId();
		}
		
	}

	public void cleanTestMessage() {
		adviseTestMessage = null;
		adviseMessageAuthorId = null;
	}

	public void setAdviseMessage(AdviseMsg msg) {
		if (msg == null) {
			adviseMessage = null;
			adviseMessageAuthorId = null;
			adviseMessageUserRestriction = new ArrayList<Long>();
			adviseMessageRoleRestriction = new ArrayList<String>();
			adviseMessageSdlRestriction = new ArrayList<Long>();
		}
		else {
			AdviseMsgAction ama = AdviseMsgAction.instance();
			adviseMessage = msg;
			adviseMessageAuthorId = msg.getAuthor().getInternalId();
			adviseMessageUserRestriction = ama.getEmployeesId(msg);
			adviseMessageRoleRestriction = ama.getRoleCodes(msg);
			adviseMessageSdlRestriction = ama.getSdlIds(msg);
		}
	}






}
