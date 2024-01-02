package com.phi.ps;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.GraphElement;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.signal.EventService;

import com.phi.cs.exception.PhiException;

/**
 * Listen to Jbpm events: process-start,process-end,subprocess-created,subprocess-end,superstate-enter,superstate-leave
 * Update ProcessManager internal state
 * @author alex.zupan
 */
public class JbpmEventService implements EventService {

	private static final long serialVersionUID = 839396709021647927L;

	private String supportedEvents;
	private Logger log = Logger.getLogger(JbpmEventService.class);

	public JbpmEventService(String supportedEvents) {
		this.supportedEvents = supportedEvents;
	}

	public void fireEvent(String eventType, GraphElement graphElement, ExecutionContext executionContext) {
		if(containsType(eventType)) {
			if (Event.EVENTTYPE_PROCESS_START.equals(eventType)) {
				log.debug("Starting process: " + executionContext.getToken().getProcessInstance());
			}
			if (Event.EVENTTYPE_PROCESS_END.equals(eventType)) {
				log.debug("Ending process: " + executionContext.getToken().getProcessInstance());
				try {
					ProcessManagerImpl.instance().endProcessExecution();
				} catch (PhiException e) {
					log.error("Error going home: " + executionContext.getToken().getName(), e);
					throw new RuntimeException("Error going home: " + executionContext.getToken().getName(), e);
				}
			}
			if (Event.EVENTTYPE_SUBPROCESS_CREATED.equals(eventType)) {
				ProcessManagerImpl.instance().setProcess(executionContext.getToken().getSubProcessInstance());
			}
			if (Event.EVENTTYPE_SUBPROCESS_END.equals(eventType)) {
				ProcessManager pm = ProcessManagerImpl.instance();
				pm.setProcess(executionContext.getToken().getProcessInstance());

				String subProcessName = pm.getSubProcessStack().pop();
				if (subProcessName.endsWith("?stateless=true")) {
					pm.setStateless(false);
				}
			}
			if (Event.EVENTTYPE_SUPERSTATE_ENTER.equals(eventType)) {
				log.debug("Starting super state: " + executionContext.getToken().getName());
			}
			if (Event.EVENTTYPE_SUPERSTATE_LEAVE.equals(eventType)) {
				try {
					ProcessManagerImpl.instance().saveSuperState();
				} catch (PhiException e) {
					log.error("Error flushing after super state: " + executionContext.getToken().getName(), e);
					throw new RuntimeException("Error flushing after super state: " + executionContext.getToken().getName(), e);
				}
			}
		}
	}

	public void close() {
	} 

	private boolean containsType(String eventType) {
		return (supportedEvents.indexOf(eventType)>=0);
	} 
}
