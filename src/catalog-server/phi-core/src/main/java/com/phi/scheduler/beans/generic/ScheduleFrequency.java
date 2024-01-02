package com.phi.scheduler.beans.generic;

public class ScheduleFrequency {

	public static final Long ONCE = null;
	public static final Long EVERY_SECOND = new Long(1000l);
	public static final Long EVERY_MINUTE = new Long(60*1000l);
	public static final Long HOURLY = new Long(60*60*1000l);
	public static final Long DAILY = new Long(60*60*1000l);
	public static final Long WEEKLY = new Long(7*24*60*60*1000l);

	Long interval; 

    public ScheduleFrequency(Long interval) {
        this.interval = interval;
    }
    
    public Long getInterval() {
        return interval;
    }

}
