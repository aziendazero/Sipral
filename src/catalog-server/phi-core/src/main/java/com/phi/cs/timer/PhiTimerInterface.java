package com.phi.cs.timer;

import javax.ejb.Timer;

public interface PhiTimerInterface {

	public void init();
	public void init(boolean disable);
	public void initSingle(Long scheduledTime, String identifier, boolean disable);
	public void execute(Timer timer) throws InterruptedException;
	
	public void cleanAllTimers();
	
}
