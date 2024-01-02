package com.phi.scheduler.beans;

import java.rmi.server.UID;
import java.text.ParseException;
import java.util.Date;

import org.jboss.seam.async.QuartzDispatcher;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.phi.entities.Scheduleitem;

/**
 * Schedules activities into Quartz scheduler.
 * The execution of an activity is done by jobClass, in this case PhiJob.
 */

public class SchedulerProcessorCore {
	
	//Class that implements the quartz job interface. Handles the execution of the trigger.
	protected Class jobClass;
	
	public SchedulerProcessorCore() {
		jobClass = PhiJob.class;
	}

	public static final String scheduleItemJobDataMapName = "Scheduleitem";
	
	private Scheduler quartzScheduler = QuartzDispatcher.instance().getScheduler();
	
	/**
	 * Schedule an activity with quartz. 
	 * When the activity is fired by Quartz, the PhiJob.execute method is called.
	 * @param scheduleItem
	 * @throws ParseException
	 * @throws SchedulerException
	 */
	public void schedule(Scheduleitem scheduleItem) throws ParseException, SchedulerException {

		JobDetail jobDetail = new JobDetail(new UID().toString(), scheduleItem.getActAppointmentId(), jobClass);
		jobDetail.getJobDataMap().put(scheduleItemJobDataMapName, scheduleItem);
		
		jobDetail.setDescription(scheduleItem.toString());
		
		Trigger trigger;
		scheduleItem.setTriggerName( new UID().toString() );
		scheduleItem.setTriggerGroup( scheduleItem.getPatientId() );
		
		if (scheduleItem.getFrequencyCron() != null) {
			//Create a new CronTrigger
			trigger = new CronTrigger (scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
			((CronTrigger)trigger).setCronExpression(scheduleItem.getFrequencyCron());
		} else {
			//Create a new SimpleTrigger
		    trigger = new SimpleTrigger(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
		    ((SimpleTrigger)trigger).setRepeatInterval(scheduleItem.getFrequency());
		}	
		
		trigger.setStartTime (scheduleItem.getStartDate());
		trigger.setEndTime(scheduleItem.getEndDate());
		
		trigger.setDescription(scheduleItem.toString());
		
		quartzScheduler.scheduleJob( jobDetail, trigger );
	}
	
	/**
	 * UnSchedule an activity with quartz. 
	 * @param scheduleItem
	 * @throws SchedulerException
	 */
	public void unschedule(Scheduleitem scheduleItem) throws SchedulerException {
		quartzScheduler.unscheduleJob(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
	}
	
	/**
	 * Pause an activity with quartz. 
	 * @param scheduleItem
	 * @throws SchedulerException
	 */
	public void pause(Scheduleitem scheduleItem) throws SchedulerException {
		quartzScheduler.pauseTrigger(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
	}
	
	/**
	 * Resume an activity with quartz.
	 * If a job has to be launched during the pause period, will be launched on resume.
	 * @param scheduleItem
	 * @throws SchedulerException
	 */
	public void resume(Scheduleitem scheduleItem) throws SchedulerException {
		quartzScheduler.resumeTrigger(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
	}
	
	/**
	 * Reschedule an activity with quartz.
	 * Remove (delete) the Trigger with the given name, and store a new one.
	 * The new one has StartTime = now.
	 * If a job has to be launched during the pause period, will NOT be launched on resume.
	 * @param scheduleItem
	 * @throws SchedulerException
	 */
	public void reschedule(Scheduleitem scheduleItem) throws SchedulerException {
		Trigger trigger = quartzScheduler.getTrigger(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup());
		
		Date newStartDate = new Date();
		trigger.setStartTime(newStartDate);
		
		quartzScheduler.rescheduleJob(scheduleItem.getTriggerName(), scheduleItem.getTriggerGroup(), trigger);
	}

}
