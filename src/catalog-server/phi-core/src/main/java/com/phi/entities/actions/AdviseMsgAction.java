package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.timer.AdviseMsgTimer;
import com.phi.cs.timer.PhiTimerInterface;
import com.phi.cs.util.AdviseMsgManager;
import com.phi.cs.view.banner.Banner;
import com.phi.entities.baseEntity.AdviseMsg;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.security.UserBean;

/**
 * AdviseMsg Action set and unset curernt active message which is stored at application context by AdviseMsgManager.
 * AdviseMsgAction stores also in Banner bean (Session context) the event of message closure, to avoid to replicate 
 * the same message to each user every time it comes back to home. 
 * 
 * @author Francesco Bragagna
 */


@BypassInterceptors
@Name("AdviseMsgAction")
@Scope(ScopeType.CONVERSATION)
public class AdviseMsgAction extends BaseAction<AdviseMsg, Long> {

	private static final long serialVersionUID = 794817870L;
	private static final Logger log = Logger.getLogger(AdviseMsgAction.class);

	public static AdviseMsgAction instance() {
		return (AdviseMsgAction) Component.getInstance(AdviseMsgAction.class,
				ScopeType.CONVERSATION);
	}

	public AdviseMsg readTest() {
		
		getEqual().put("isActive", true);
		getEqual().put("test", true);
		getEqual().put("author", UserBean.instance().getCurrentEmployee());
		List<AdviseMsg> msgs;
		try {
			msgs = list();
			if (msgs.size() > 0) {
				return msgs.get(0);
			}

		} catch (PhiException e) {
			log.error("error getting list of AdviseMsg");
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	public AdviseMsg readActive() {
		Date now = new Date();
		getEqual().put("isActive", true);  //not deleted logically
		getEqual().put("active", true);    //active as activated messages
		getGreaterEqual().put("scheduleTo", now );
		getLessEqual().put("scheduleFrom", now );
		List<AdviseMsg> msgs;
		try {
			msgs = list();
			if (msgs.size() == 1) {
				return msgs.get(0);
			}

		} catch (PhiException e) {
			log.error("error getting list of active AdviseMsg");
			e.printStackTrace();
		}
		return null;

	}

	public void updateActiveMessage(AdviseMsg msg) {
		AdviseMsgManager amm = AdviseMsgManager.instance(); 
		AdviseMsg currentMsg = amm.getAdviseMessage();
		
		if (msg == null) {
			return;
		}
		
		if (msg.getActive() != null && msg.getActive() == true){
			Date now = new Date();
			if (dateInRange(msg.getScheduleFrom(), msg.getScheduleTo(), now)) {
				amm.setAdviseMessage(msg);
				setAsUnReadInSession(msg);
				return;
			}
		}
		if (currentMsg != null && msg.getInternalId() == currentMsg.getInternalId()){
			amm.setAdviseMessage(null);
			cleanMsgInSession(msg);
		}
		
	}
	
	public void cleanAllMessage() throws PhiException {
		
		//disable any current activated message at application level
		AdviseMsgManager amm = AdviseMsgManager.instance(); 
		amm.setAdviseMessage(null);
		
		//read all active messages and set as not active
		cleanRestrictions();
		this.equal.put("active", true);
		List<AdviseMsg> msgs = list();
		
		for (AdviseMsg msg : msgs){
			msg.setActive(false);
			create(msg);
		}
		
		//remove any timers
		PhiTimerInterface atim = (PhiTimerInterface) Component.getInstance(AdviseMsgTimer.class,ScopeType.APPLICATION);
		atim.cleanAllTimers();
	
	}
	
	public boolean nowInRange(Date rangeFrom, Date rangeTo) {
		return dateInRange(rangeFrom, rangeTo, new Date());
	}
	
	public boolean dateInRange(Date rangeFrom, Date rangeTo, Date test) {
		if (test == null || rangeFrom == null || rangeTo == null) {
			return false;
		}
	
		if (test.after(rangeFrom) && test.before(rangeTo)) {
			return true;
		}
		
		return false;
	}
	
	public void updateTestMessage(AdviseMsg msg) {
		AdviseMsgManager amm = AdviseMsgManager.instance(); 
		AdviseMsg currentMsg = amm.getAdviseTestMessage();
		
		if (msg == null) {
			return;
		}
		
		if (msg.getTest() != null && msg.getTest() == true){
			amm.setTestAdviseMessage(msg);
			setAsUnReadInSession(msg);
		}
		
		if (currentMsg != null && msg.getInternalId() == currentMsg.getInternalId()){
			amm.setTestAdviseMessage(null);
			cleanMsgInSession(msg);
		}
		
		
		
	}

	public String getTestMessage() {
		AdviseMsgManager amm = AdviseMsgManager.instance();
		Employee emp = UserBean.instance().getCurrentEmployee();
		
		AdviseMsg msg = amm.getAdviseTestMessage();
		Long authorId = amm.getAuthorId();
		
		
		
		if (msg != null ) {
			Boolean isReaded = isReaded(msg);
			if (isReaded == true) {
				return null;
			}
			
			if(authorId != null && emp.getInternalId() == authorId) {
				return msg.getText();
			}
			
		}
		
		return null;
	}
	
	
	public void setAsRead() {
		AdviseMsgManager amm = AdviseMsgManager.instance();
		AdviseMsg msg = amm.getAdviseMessage();
		if (msg != null) {
			setAsReadInSession(msg);
		}
	}
	
	public void setAsReadTest() {
		AdviseMsgManager amm = AdviseMsgManager.instance();
		AdviseMsg msg = amm.getAdviseTestMessage();
		if (msg != null) {
			setAsReadInSession(msg);
		}
	}

	public void setAsReadInSession(AdviseMsg msg) {
		Banner.instance().put("AdviseMsgReaded-"+msg.getInternalId(), true);
	}
	
	public void setAsUnReadInSession(AdviseMsg msg) {
		Banner.instance().put("AdviseMsgReaded-"+msg.getInternalId(), false);
	}
	
	public void cleanMsgInSession(AdviseMsg msg) {
		if (msg == null) {
			//restore all as messages, if any.
			Set<String> keys = Banner.instance().keySet();
			
			for (Iterator<String> it= keys.iterator(); it.hasNext();){  
				String key = it.next();
				if (key.contains("AdviseMsgReaded-")){
					it.remove();
				}
			}
			return;
		}
		Banner.instance().remove("AdviseMsgReaded-"+msg.getInternalId());
	}
	
	public Boolean isReaded(AdviseMsg msg) {
		Boolean isReaded = (Boolean)Banner.instance().get("AdviseMsgReaded-"+msg.getInternalId());
		if (isReaded == null) 
			return false;
		else return isReaded;
	}
	
	public boolean showAdviseTestMessage(){
		if (getTestMessage() == null)
			return false;
		else
			return true;
	}
	
	public boolean showAdviseMessage(){
		if (getMessage() == null)
			return false;
		else
			return true;
	}
	
	public String getMessage() {
		AdviseMsgManager amm = AdviseMsgManager.instance();
		AdviseMsg msg = amm.getAdviseMessage();
		List<Long> emps = amm.getAdviseMessageUserRestriction();
		List<String> roles = amm.getAdviseMessageRoleRestriction();
		if (msg != null) {
			Boolean isReaded = isReaded(msg);
			if (isReaded == true)
				return null;
			
			
			if (emps != null && !emps.isEmpty()){
				Employee emp = UserBean.instance().getCurrentEmployee();
				if (emps.contains(emp.getInternalId())){
					return msg.getText();
				}
				else {
					return null;
				}
			}
			
			if (roles != null && !roles.isEmpty()) {
				CodeValueRole role = UserBean.instance().getCurrentSystemUser().getCode();
				if ( roles.contains(role.getCode())) {
					return msg.getText();
				}
				else {
					return null;
				}
			}
			
			return msg.getText();
		}
		
		return null;
		
	}
	
	public void duplicate(AdviseMsg m) {
		
		AdviseMsg dup = new AdviseMsg();
		dup.setScheduleFrom(m.getScheduleFrom());
		dup.setScheduleTo(m.getScheduleTo());
		dup.setText(m.getText());
		
		//dup.setRestrictTo(m.getRestrictTo());
		//dup.setRestrictToRol(m.getRestrictToRol());
		//dup.setRestrictSdl(m.getRestrictSdl());
		
		inject(dup);
	}
	
	public List<Long> getEmployeesId (AdviseMsg m) {
		List<Long> ret = new ArrayList<Long>(); 
		if (m == null || m.getRestrictTo() == null){
			return ret;
		}
		
		for (Employee e : m.getRestrictTo()) {
			ret.add(e.getInternalId());
		}
		
		return ret;
	}
	
	public List<Long> getSdlIds (AdviseMsg m) {
		List<Long> ret = new ArrayList<Long>();
		if (m == null){
			return ret;
		}
		
		/* BRAGA
		for (ServiceDeliveryLocation sdl : m.getS()) {
			ret.add(sdl.getInternalId());
		}
		*/
		return ret;
	}

	public List<String> getRoleCodes (AdviseMsg m) {
		List<String> ret = new ArrayList<String>();
		if (m == null || m.getRestrictToRol() == null){
			return ret;
		}
		
		for (CodeValueRole cv : m.getRestrictToRol()) {
			ret.add(cv.getCode());
		}
		
		return ret;
	}
	
	public boolean isOtherIncompatibleActive(AdviseMsg msg, List<AdviseMsg> msgs) {

		for (AdviseMsg existing : msgs) {
			if (existing.getInternalId() == msg.getInternalId()) {
				continue;
			}
			if (existing.getActive() != null && true == existing.getActive()) {

				// verify if is overlap: overlap on date range
				// when (StartDate1 <= EndDate2) and (StartDate2 <= EndDate1)
				Date startDate1 = msg.getScheduleFrom();
				Date endDate1 = msg.getScheduleTo();
				Date startDate2 = existing.getScheduleFrom();
				Date endDate2 = existing.getScheduleTo();

				if (startDate1 == null || startDate2 == null
						|| endDate1 == null || endDate2 == null) {
					return true;
				}

				if ((startDate1.before(endDate2) || startDate1.equals(endDate2))
						&& (startDate2.before(endDate1) || startDate2
								.equals(endDate1))) {
					return true;
				}
			}
		} // for

		return false;

	}
	
	public void initTimer(AdviseMsg msg) {
		if (msg == null) {
			log.error("unable to activate timer for null advise message");
			return;
		}
		
		log.info("setting timer for message id: "+msg.getInternalId()+ " from "+msg.getScheduleFrom()+" to "+msg.getScheduleTo());
		PhiTimerInterface atim = (PhiTimerInterface) Component.getInstance(AdviseMsgTimer.class,ScopeType.APPLICATION);
		
		atim.initSingle(msg.getScheduleFrom().getTime(), "ON_AdviseMsg-"+msg.getInternalId(), false);
		atim.initSingle(msg.getScheduleTo().getTime(), "OFF_AdviseMsg-"+msg.getInternalId(), false);
	}
	
	public void deleteTimer(AdviseMsg msg) {
		if (msg == null) {
			log.error("unable to delete timer for null advise message");
			return;
		}
		
		log.info("deleting timer for message id: "+msg.getInternalId()+ " from "+msg.getScheduleFrom()+" to "+msg.getScheduleTo());
		PhiTimerInterface atim = (PhiTimerInterface) Component.getInstance(AdviseMsgTimer.class,ScopeType.APPLICATION);
		
		atim.initSingle(msg.getScheduleFrom().getTime(), "ON_AdviseMsg-"+msg.getInternalId(), true);
		atim.initSingle(msg.getScheduleTo().getTime(), "OFF_AdviseMsg-"+msg.getInternalId(), true);
	}

}