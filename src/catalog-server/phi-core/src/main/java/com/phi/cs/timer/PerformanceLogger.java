package com.phi.cs.timer;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import com.phi.cs.repository.RepositoryManager;
import com.phi.security.SessionManager;


@Stateless
@Name("PerformanceLogger")
public class PerformanceLogger implements PhiTimerInterface{
	
	private static final Logger log = Logger.getLogger(PerformanceLogger.class);
	
	@Resource
    private TimerService timerService;
	
	//timer frequency, expressed in ms. Default 300s = 5'
	private static final long frequencyMs = 300000; 
	//use jbossdb to easy change this value. e.g.:
	//update timers set timerinterval = 5000 where targetid like '%PerfornnceLogger%' and utl_raw.cast_to_varchar2(dbms_lob.substr(info)) like '%BRAGAGNAF1_PortsDefaultBindings%'
	//update timers set timerinterval = 3600000 where targetid like '%PerfornnceLogger%' and utl_raw.cast_to_varchar2(dbms_lob.substr(info)) like '%BRAGAGNAF1_ports-01%'
	//info fields contains  <hostname>_<portBindingSet>  
	
	private static final String TIMER_NAME="Log periodically jvm stat on log file";
	private final int AVERAGE_SAMPLE_NUM = 10;  //number of sample on which average is calculated.
	//private static final String SEAM_PROP_TO_DISABLE_PERFLOG = "disablePerformanceLogger";
	
	@Observer("org.jboss.seam.postInitialization")
	public void init() {
		boolean disable = "true".equals(RepositoryManager.instance().getSeamProperty("disable"+this.getClass().getSimpleName()));
		init(disable);
	}
	
	@Override
	public void init(boolean disable) {
		log.info("Initialize timer for logging jvm info, frequency: "+frequencyMs/1000+"s");
		Collection<Timer> existingTimers = timerService.getTimers();
		log.info("Exiting timers: "+ (existingTimers == null ? "0" : existingTimers.size()));
		boolean found=false;
		
		String TimerHostName = TIMER_NAME+" on "+PhiTimerCommons.getHostName(); 
		
		if (existingTimers != null && !existingTimers.isEmpty()) {
			for (Timer t: existingTimers){
				if (t!=null  && TimerHostName.equals(t.getInfo())) {
					if (found) {
						log.error("Duplicate timer \""+TimerHostName+"\", killing it. "+t.toString());
						t.cancel();
					}
					else {
						found=true;
						//Note: if the timer is alredy present in db table, is not added again, 
						//but its configuration are not checked. (e.g. frequency).
						log.info("Already existing \""+TimerHostName+"\" timer: "+t.toString()+ "    next execution: " +new Date(  System.currentTimeMillis() + t.getTimeRemaining()));  
						if (disable) {
							log.info("disable"+this.getClass().getSimpleName()+"=true, so timer is killed.");
							t.cancel();
						}
					}
				}
			}
		}
		if (!found) {
			if (disable) {
				log.info("disable"+this.getClass().getSimpleName()+"=true, so the timer is not created.");
			}
			else {
				Timer t = timerService.createTimer(30000, frequencyMs, TimerHostName);  //wait 30 sec before start timer the new timer.
				log.info("Created new \""+TimerHostName+"\" timer: "+t.toString());
			}
		}
		
	}
	
	
	@Timeout
	public void execute(Timer timer) throws InterruptedException {
		if (timer == null || timer.getInfo() == null)
			return;

		String hostTimerName = PhiTimerCommons.getHostName();
		if (!timer.getInfo().toString().contains(hostTimerName)) {
			return; //this is not a Performance timer of this host/port.
		}
		
		executeTimer();
	}
	
	private void executeTimer() {
		//get previous execution information, stored in SessionManager hashamp (stateful).
		TimerPrvStats prv =null;
		try {
			prv = getTimerSTats();
		}
		catch (Exception e) {
			//do nothing, next if will log.
		}
		if (prv == null) {
			log.info("Session manager not available in context, application problably not completed started.");
			return;
		}
		
		HashMap<String, long[]> prvGcValues = prv.prvGcValues;
		Long prvFree = prv.prvFree;
		Integer rollingIndex = prv.rollingIndex;
		Long[] memoryStats = prv.memoryStats;
		
		if (prvGcValues == null || prvFree == null || rollingIndex == null || memoryStats == null) {
			log.info ("Null previous timer data. execution skipped.");  //if properly retrieved, they are never null.
			return;
		}
		
		
		int mb = 1024*1024;
		
		Runtime r =Runtime.getRuntime();
		long tot = r.totalMemory();
		long free = r.freeMemory();

		StringBuffer gcInfo = new StringBuffer();
		List<GarbageCollectorMXBean> gcmxb = (List<GarbageCollectorMXBean>) ManagementFactory.getGarbageCollectorMXBeans();
		
		String execution="";
		Long memoryGain = null;
		
		for(GarbageCollectorMXBean gcBean: gcmxb){ 
			String gcName = gcBean.getName(); 
			long collectionsTime=gcBean.getCollectionTime();
			long collectionsCount=gcBean.getCollectionCount();
			if (prvGcValues.containsKey(gcName)) {
				long prvCollectionsCount = prvGcValues.get(gcName)[0];
				if (collectionsCount > prvCollectionsCount) {
					//gc was run!
					if (memoryGain ==null) { //make this stuff only one time.
						
						//memory gain it could be negative, if timer is executed many time after GC, 
						//or if the application usage is raising up quickly and the free memory is less than the new used. 
						memoryGain = free-prvFree;   
						execution+=" ** GC executed! ** [";
					}
					
					long timeTaken = collectionsTime - prvGcValues.get(gcName)[1];
					execution += " "+gcName+ " run in "+timeTaken+ "ms ";
					
					//update prv values only if the gc is run.
					prvGcValues.put(gcName, new long[]{collectionsCount, collectionsTime}); 
				}
			}
			//add if never added!
			if (!prvGcValues.containsKey(gcName)) {
				prvGcValues.put(gcName, new long[]{collectionsCount, collectionsTime}); 
			}
            gcInfo.append(" "+gcName+" "/*+collectionsTime+"ms "*/+"("+collectionsCount+"x) "); 
        }
		
		if (!execution.isEmpty()) {
			execution += "] Memory gain: "+memoryGain/mb+ "MB";
		}
		
		//calculate free memory average of last AVERAGE_SAMPLE_NUM free memory samples
		//Long average=average(free);
		memoryStats[rollingIndex] = free;
		if (rollingIndex == AVERAGE_SAMPLE_NUM-1)
			rollingIndex =0;
		else 
			rollingIndex++;
		
		long average=0;
		int nullCount=0;
		for (Long l : memoryStats){
			if (l==null) {
				nullCount++;
				continue;
			}
			average+=l;
		}
		average = average/(AVERAGE_SAMPLE_NUM-nullCount);
		
		String av =" FreeAverage "+average/mb+"MB ";
		
		SessionManager sm = SessionManager.instance();
		int sSize = sm.getSessions().size();
		
		log.info("JVM HEAP [Total: "+ tot/mb +"MB  Used: "+ (tot-free)/mb + "MB  Free: "+ free/mb + av +"] - GC ["+gcInfo.toString()+"]"+execution + " - Sessions count: "+sSize);
		prvFree=free;
		
		updateTimerStats(new TimerPrvStats (prvGcValues, prvFree, rollingIndex, memoryStats));
	}
	
	private TimerPrvStats getTimerSTats() {
		
		if (!Contexts.isApplicationContextActive()) {
			Lifecycle.beginCall();
		}
		
		//RepositoryManager rm = RepositoryManager.instance();
		SessionManager sm = SessionManager.instance();
		
		if (sm == null) {
			return null;
		}
		
		HashMap timerStats = sm.getTimerStat();
		if(timerStats == null) {
			timerStats = new HashMap<String, Object>();
			sm.setTimerStat(timerStats);
		}
		
		TimerPrvStats ret = new TimerPrvStats(null,null,null,null);
		
		
		if (timerStats.keySet().contains("prvGcValues")) { 
			ret.prvGcValues=(HashMap<String,long[]>) timerStats.get("prvGcValues");
		}
		else {
			ret.prvGcValues = new HashMap<String,long[]>();
			timerStats.put("prvGcValues", ret.prvGcValues);
		}
	
		if (timerStats.keySet().contains("prvFree")) { 
			ret.prvFree=(Long) timerStats.get("prvFree");
		}
		else {
			ret.prvFree = 0L;
			timerStats.put("prvFree", ret.prvFree);
		}
		
		if (timerStats.keySet().contains("rollingIndex")) { 
			ret.rollingIndex=(Integer) timerStats.get("rollingIndex");
		}
		else {
			ret.rollingIndex = 0;
			timerStats.put("rollingIndex", ret.rollingIndex);
		}
		
		if (timerStats.keySet().contains("memoryStats")) { 
			ret.memoryStats=(Long[]) timerStats.get("memoryStats");
		}
		else {
			ret.memoryStats = new Long[AVERAGE_SAMPLE_NUM];
			timerStats.put("memoryStats", ret.memoryStats);
		}
		
		return ret;
	}
	
	
	private void updateTimerStats (TimerPrvStats next) {
		
		if (!Contexts.isApplicationContextActive()) {
			log.warn("Context should be already initialized by getStatefulProp()");
			Lifecycle.beginCall();
		}
		
		//RepositoryManager rm = RepositoryManager.instance();
		SessionManager sm = SessionManager.instance();
		
		if (sm == null) {
			log.warn("Session manager not available for timer information update");
			return;
		}
		
		HashMap timerStats = sm.getTimerStat();
		if(timerStats == null) {
			timerStats = new HashMap<String, Object>();
			sm.setTimerStat(timerStats);
		}
		
		timerStats.put("prvGcValues", next.prvGcValues);
		timerStats.put("prvFree", next.prvFree);
		timerStats.put("rollingIndex", next.rollingIndex);
		timerStats.put("memoryStats", next.memoryStats);
		
		//if (Contexts.isApplicationContextActive()) {
			//Lifecycle.endCall();  //It breaks JB4. Under JB5 it works. Commented.
		//}
	
	}
	
	private class TimerPrvStats {
		public TimerPrvStats(HashMap<String, long[]> prvGcValues, Long prvFree, Integer rollingIndex, Long[] memoryStats) {
			this.prvGcValues=prvGcValues;
			this.prvFree=prvFree;
			this.rollingIndex=rollingIndex;
			this.memoryStats=memoryStats;
		}
		
		public HashMap<String, long[]> prvGcValues;
		public Long prvFree;
		public Integer rollingIndex;
		public Long[] memoryStats;
	}

	@Override
	public void initSingle(Long scheduledTime, String identifier, boolean disabled){
		//not used for Performance Logger
	}

	@Override
	public void cleanAllTimers() {
		// TODO Auto-generated method stub
		
	}

	
//	private String getHostName() {
//		String hostname = "";
//		try {
//			hostname = InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {
//			log.error("Check Network configuraiton and host resolution: hostname can not be found.");
//			hostname = "unknowHost";//+(new Random()).nextInt(8000000) + 1000000;
//		}
//		
//		java.util.Properties b =System.getProperties();
//		if (b==null)
//			return hostname+"_"+"PortsDefaultBindings";
//		String portSet = (String)b.get("jboss.service.binding.set");
//		if (portSet==null || portSet.isEmpty())
//			portSet="PortsDefaultBindings";
//		return hostname+"_"+portSet;
//	}
	
}
