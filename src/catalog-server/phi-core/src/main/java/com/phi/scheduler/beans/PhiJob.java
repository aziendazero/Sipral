package com.phi.scheduler.beans;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.phi.entities.Scheduleitem;

/**
 * Represent a 'job' to be performed.
 * SchedulerProcessorCore schedules activities into Quartz scheduler 
 * and this class executes the job. 
 * @author Alex Zupan
 */

public class PhiJob implements Job {
	
	private static final Logger log = Logger.getLogger(PhiJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
		 JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	     Scheduleitem scheduleItem = (Scheduleitem)dataMap.get(SchedulerProcessorCore.scheduleItemJobDataMapName);
	     
     	log.debug("Example implementation of Job execute. This method must be overriden in a backEnd Project.");
    	log.debug(scheduleItem);
    	
//    	Lifecycle.beginCall();
//			if you want to use Seam components, decomment these two lines...
//      Lifecycle.endCall();

	}

}
