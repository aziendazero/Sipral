package com.phi.scheduler.beans;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.ApplicationException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.Scheduleitem;
import com.phi.entities.baseEntity.BaseEntity;

/**
 * This class is the main manager to CREATE/DELETE/SUSPENDED an schedule
 * activities that is create with ActAppointment RMIM and Schedule Component.
 * You need to add this in the component.xml:
 * 
 * <!-- Install the QuartzDispatcher --> 
 * <async:quartz-dispatcher/>
 */
@BypassInterceptors
@Name("schedulerHome")
public class ScheduleHomeCore {
	private static final Logger log = Logger.getLogger(ScheduleHomeCore.class);
	
	public static final int MISFIRE_INSTRUCTION_SMART_POLICY = 0;
	public static final int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;
	public static final int MISFIRE_INSTRUCTION_DO_NOTHING = 2;
	
	protected final static String CANCEL_OP = "CANCEL";
	protected final static String SUSPEND_OP = "SUSPEND";
	protected final static String RESUME_OP = "RESUME";
	protected final static String MODIFY_OP = "MODIFY";
	protected SchedulerProcessorCore processor;
	protected CatalogAdapter ca;
//	protected RimRepositoryBean repBean;
	protected BaseEntity actApp = null;
	protected String actAppRimName = null;
	protected BaseEntity patient = null;
	protected String patientRimName = null;

	public ScheduleHomeCore() {

		processor = new SchedulerProcessorCore();
		ca = CatalogPersistenceManagerImpl.instance();

	}

//	/**
//	 * This method is called as POST-ACTION in the process to create a SIMPLE
//	 * SCHEDULE ACTIVITY in QUARTZ with reference to PATIENT ID and
//	 * ActAppointment ID
//	 * 
//	 * NOT USED
//	 * 
//	 * @deprecated
//	 */
//	public void simpleScheduleAndSave() {
//		try {
//			List<Scheduleitem> listSI = createScheduleItemForSave();
//
//			for (int i = 0; i < listSI.size(); i++) {
//				try {
//					Scheduleitem scheduleItem = listSI.get(i);
////					QuartzTriggerHandle handle = processor.scheduleActivity(
////							scheduleItem.getStartDate(), scheduleItem
////							.getFrequency(), scheduleItem.getEndDate(),
////							scheduleItem);
//					
//					processor.schedule(scheduleItem);
//
////					scheduleItem.setQuartzTriggerHandle(handle);
//
//					this.saveScheduleItemInDB(scheduleItem);
//
//					if (log.isDebugEnabled())
//						log.debug("scheduling instance #"
//								+ scheduleItem.getInternalId()
//								+ " was saved/update with quartz handle"
//								+ scheduleItem.getTriggerName());
////								+ handle.toString());
//				} catch (RuntimeException e) {
//					FacesErrorUtils
//					.manageError(new PhiException(
//							ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG,
//							e,
//							ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE));
//				}
//			}
//		} catch (Exception e) {
//			FacesErrorUtils.manageError(new PhiException(
//					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG, e,
//					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE));
//		}
//	}

	/**
	 * This method is called as POST-ACTION in the process to create a SCHEDULE
	 * ACTIVITY n QUARTZ with reference to PATIENT ID and ActAppointment ID with
	 * a CRON string
	 */
	public void save() throws PhiException {
		Scheduleitem scheduleItem = null;
		try {
			List<Scheduleitem> listSI = createScheduleItemForSave();

			if (log.isDebugEnabled())
				log.debug(" [" + Thread.currentThread().getId() + "] listSI=" + listSI.size());

			for (int i = 0; i < listSI.size(); i++) {
				scheduleItem = listSI.get(i);
				
				saveScheduleItemInDB(scheduleItem);
				
				processor.schedule(scheduleItem);

				if (log.isDebugEnabled())
					log.debug("scheduling instance #"
							+ scheduleItem.getInternalId()
							+ " was saved/update with quartz triggerName: "
							+ scheduleItem.getTriggerName() + " triggerGroup: " + scheduleItem.getTriggerGroup());
			}
		} catch (Exception e) {
			log.error(" ERROR PARSING Frequency Cron String: " +scheduleItem.getFrequencyCron());
			throw new PhiException(
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG, e,
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE);
		}
	}

	/**
	 * Cancel a SCHEDULE starting from a POST action PROCESS NODE with
	 * ActAppointment or Patient in CONVERSATION
	 */
	public void cancel() throws PhiException {
		manageScheule(CANCEL_OP);
	}

	/**
	 * SUSPEND a SCHEDULE starting from a POST action PROCESS NODE with
	 * ActAppointment or Patient in CONVERSATION
	 */
	public void suspend() throws PhiException {
		manageScheule(SUSPEND_OP);
	}

	/**
	 * RESUME a SCHEDULE starting from a POST action PROCESS NODE with
	 * ActAppointment or Patient in CONVERSATION
	 */
	public void resume() throws PhiException {
		manageScheule(RESUME_OP);
	}

	/**
	 * MODIFY a SCHEDULE starting from a POST action PROCESS NODE with
	 * ActAppointment or Patient in CONVERSATION
	 */
	public void modify() throws PhiException {
		manageScheule(MODIFY_OP);
	}

	/**
	 * This method is called as POST-ACTION in the PROCESS to delete active
	 * scheduled activity
	 * 
	 * @throws SchedulerException
	 */
	protected void manageScheule(String operation) throws PhiException {
		try {
			List<Scheduleitem> listSI = null;
			/**
			 * reactivating patient you must avoid ActAppointement search for PRM: it this not
			 * happens activity are lost and no more activated
			 */
			if (!operation.equals(RESUME_OP))
					getActAppointment();
			if (actApp != null) {
				long actAppId = actApp.getInternalId();
				if (RESUME_OP.equals(operation)) {
					listSI = (List<Scheduleitem>) ca.executeHQL("from Scheduleitem as si where si.actAppointmentId='"
							+ actAppId
							+ "' and si.active=false and si.suspended=true");
				} else {
					listSI = (List<Scheduleitem>) ca.executeHQL("from Scheduleitem as si where si.actAppointmentId='"
							+ actAppId + "' and si.active=true");
				}
			} else {
				getPatient();
				if (patient != null) {
					long patientId = patient.getInternalId();
					if (RESUME_OP.equals(operation)) {
						listSI = (List<Scheduleitem>) ca.executeHQL("from Scheduleitem as si where si.patientId='"
								+ patientId
								+ "' and si.active=true and si.suspended=true");
					} else {
						listSI = (List<Scheduleitem>) ca.executeHQL("from Scheduleitem as si where si.patientId='"
								+ patientId + "' and si.active=true and si.suspended=false");
					}
				} else {
					log.error("NO PATIENT ACTIVE WAS FOUND");
				}
			}
			if (listSI != null) {
				for (int i = 0; i < listSI.size(); i++) {
					try {
						Scheduleitem si = listSI.get(i);
						manageSingleSchedule(si, operation);
					} catch (RuntimeException e) {
						throw new PhiException(
								ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG,
								e,
								ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE);
					}
				}
				if (MODIFY_OP.equals(operation))
					this.save();
			}

		} catch (Exception e) {
			throw new PhiException(
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG, e,
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE);
		}
	}

	/**
	 * Cancel a single ScheduleItem activity from RIM DB and overall from QUARTZ
	 * DB
	 * 
	 * @param si - ScheduleItem
	 * @param ca - RIM CatalogAdapter
	 */
	protected void manageSingleSchedule(Scheduleitem si, String operation) throws PhiException {

		try {
			if (CANCEL_OP.equals(operation)) {
				
				//FIXME PHI 2
				throw new IllegalStateException("TO BE IMPLEMENTED WITH PHI 2");
				
//				RimBean rimBean = (RimBean) Component.getInstance("RimBean");
//				String typeAppointment = rimBean.getRawValue(actAppRimName
//						+ ".code", actApp);
//				if (typeAppointment.equalsIgnoreCase(si.getScheduleSubType())){
//					processor.unschedule(si);
//					si.setActive(false);
//				}

			} else if (SUSPEND_OP.equals(operation)) {
				processor.pause(si);
				si.setActive(false);
				si.setSuspended(true);

			} else if (RESUME_OP.equals(operation)) {
				processor.resume(si);
				si.setActive(true);
				si.setSuspended(false);
			} else if (MODIFY_OP.equals(operation)) {
				// BEFORE MODIFY i have to cancel the old activities and the recreate them
				processor.unschedule(si);
				si.setActive(false);
			}
			if (!MODIFY_OP.equals(operation))
				ca.create(si);
			if (log.isDebugEnabled())
				log.debug("scheduling instance #" + si.getInternalId()
						+ " was deleted: triggerName: " + si.getTriggerName() + " triggerGroup: " + si.getTriggerGroup());
		} catch (Exception e) {
			throw new PhiException(
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG, e,
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE);
		}
	}

	/**
	 * Get ActAppointement injected in the CURRENT CONVERSATION
	 */
	protected void getActAppointment() {
//		Object[] names = repBean.getRimObjKeySet().toArray();
//		for (int i = 0; i < names.length; i++) {
//			if (repBean.getRimObject((String) names[i]) instanceof Act) {
//				Act actImpl = (Act) repBean
//				.getRimObject((String) names[i]);
//				if (actImpl.getClassCode().getCode().equals("ACT") && actImpl.getMoodCode().getCode().equals("APT")&& actImpl.getStatusCode().getCode().equals("active")) {
//					actAppRimName = (String) names[i];
//					actApp = actImpl;
//					if (log.isDebugEnabled())
//						log.debug("APT:" + actImpl.getInternalId());
//					break; //it was inserted here because we thougth one AcpApp per CONV
//				}
//			}
//		}
		
//		actApp = AppointmentRequestAction.instance().getEntity();
//
//		if (actApp == null) {
//			if (log.isDebugEnabled())
//				log.debug("NO ACT APPOINTMENT WAS FOUND");
//			actApp = null;
//			actAppRimName = null;
//		}
	}

	/**
	 * Get Patient active injected in the CURRENT CONVERSATION
	 */
	protected void getPatient() {
		patient = (BaseEntity)Contexts.getConversationContext().get("Patient");

		if (patient == null) {
			log.error("NO PATIENT ACTIVE WAS FOUND");
			patient = null;
			patientRimName = null;
		}

	}

	/**
	 * Useful method to prepare ScheduleItem that is the "ENTITY" that links RIM
	 * and QUARZT
	 * 
	 * @return list of Schedule item created reading the ActAppointement
	 *         Component
	 * 
	 * @throws PhiException
	 * @throws ParseException
	 */
	protected List<Scheduleitem> createScheduleItemForSave()
	throws PhiException, ParseException {
		
		//FIXME PHI 2
		throw new IllegalStateException("TO BE IMPLEMENTED WITH PHI 2");
		
//		List<Scheduleitem> listSI = new ArrayList<Scheduleitem>();
//
//		getActAppointment();
//		if (actApp != null && actAppRimName != null) {
//			RimBean rimBean = (RimBean) Component.getInstance("RimBean");
//			List<ActRelationship> actRels=null;// = (List)JxPathUtils.navigateJXPathList(actApp, "outboundRelationship[typeCode='REFR']"); FIXME PHI 2
//
//			String actAppointmentId = EntityCrudBase.getID4PHI(actApp).getExtension().toString();
//
//			String patientId = rimBean.getRimValue(actAppRimName + ".participation[typeCode=SBJ].role[classCode=PAT].id", actApp);
//			String typeAppointment = actApp.getCode().getCode();
//			//Fixed date formatting bug 12:00 -> 00:00
//			IVL<Date> interval =  ((IVL)actApp.getEffectiveTime().getData().iterator().next());
//			Date scheduleItemDateFrom = interval.getLow();
//			Date scheduleItemDateTo = interval.getHigh();
//
//			if (!typeAppointment.equalsIgnoreCase("PRM")) {
//			
//				for (ActRelationship actRel : actRels) {
//
//					EDbyteArrayImpl cronFrequencyED = (EDbyteArrayImpl)JxPathUtils.navigateJXPath(actRel, "target[classCode='ACT']/text");
//
//					if (cronFrequencyED != null) {
//						Scheduleitem scheduleItem = new Scheduleitem();
//
//						scheduleItem.setActive(true);
//						scheduleItem.setSuspended(false);
//						scheduleItem.setStartDate(scheduleItemDateFrom);
//						scheduleItem.setEndDate(scheduleItemDateTo);
//						scheduleItem.setFrequencyCron(cronFrequencyED.toString());
//						scheduleItem.setActAppointmentId(actAppointmentId);
//						scheduleItem.setPatientId(patientId);
//						scheduleItem.setScheduleType(typeAppointment);
//						scheduleItem.setScheduleSubType(actRel.getSubsetCode().getCode());
//
//						listSI.add(scheduleItem);
//
//						if (log.isDebugEnabled())
//							log.debug("ACT APPOINTMENT id:" + actAppointmentId + " with scedule type:" + typeAppointment
//									+ " subsetCode:" + actRel.getSubsetCode().getCode() + " on patient id:" + patientId);
//					}
//				}
//			} else  {
//
//				// Get the schedule for a SIMPLE activity
//				long frequency = 100000L;
//				scheduleItemDateFrom = new Date(scheduleItemDateTo.getTime()) ;
//				scheduleItemDateTo.setMinutes(scheduleItemDateTo.getMinutes()+1);
//				
//				Scheduleitem scheduleItem = new Scheduleitem();
//				scheduleItem.setActive(true);
//				scheduleItem.setSuspended(false);
//				scheduleItem.setStartDate(scheduleItemDateFrom);
//				scheduleItem.setEndDate(scheduleItemDateTo);
//				scheduleItem.setFrequency(frequency);
//				scheduleItem.setActAppointmentId(actAppointmentId);
//				scheduleItem.setPatientId(patientId);
//				scheduleItem.setScheduleType(typeAppointment);
//				scheduleItem.setScheduleSubType("0");
//
//				listSI.add(scheduleItem);
//
//			} 
//
//		} else log.error("NO ACT APPOINTMENT WAS FOUND");
//
//		return listSI;

	}

	/**
	 * Save the ScheduleItem in the RIM DB
	 * 
	 * @param scheduleItem
	 * @return ScheduleItem object saved
	 * 
	 * @throws PersistenceException
	 * @throws ApplicationException
	 */
	protected Scheduleitem saveScheduleItemInDB(Scheduleitem scheduleItem)
	throws PersistenceException, ApplicationException {
		Scheduleitem si = (Scheduleitem) ca.create(scheduleItem);
		return si;
	}

}
