package com.phi.cs.timer;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.util.AdviseMsgManager;
import com.phi.entities.baseEntity.AdviseMsg;

/**
 * When the timer expires, it enables or disables an Adivse Message. The timer can be set at runtime.
 * See AdviseMsgAction.  
 * @author Francesco Bragagna
 */

@Stateless
@Name("AdviseMsgTimer")
public class AdviseMsgTimer implements PhiTimerInterface {

	private static final Logger log = Logger.getLogger(AdviseMsgTimer.class);

	@Resource
	private TimerService timerService;

	private static final String TIMER_NAME = "Send advise messages to all logged users";

	// private static final String SEAM_PROP_TO_DISABLE_PERFLOG =
	// "disablePerformanceLogger";

	@Override
	public void init() {
		// not used for this bean
	}
	
	@Override
	public void init(boolean disable) {

		// not used recursive advise messages

	}

	public void initSingle(Long scheduledTime, String identifier,
			boolean disable) {
		if (disable) {
			for (Object obj : timerService.getTimers()) {
				Timer t = (Timer) obj;
				if (t.getInfo().equals(identifier)) {
					t.cancel();
				}
			}
		} else {
			Long current = (new Date()).getTime();
			Long diff = scheduledTime - current;
			if (diff < 0) {
				diff = 5000L; // 5 sec delay
			}
			timerService.createTimer(diff, identifier);
		}
	}

	public void cleanAllTimers() {
		for (Object obj : timerService.getTimers()) {
			Timer t = (Timer) obj;
			String info = ""+t.getInfo();
			if (info.startsWith("ON_AdviseMsg") || info.startsWith("OFF_AdviseMsg")) {
				t.cancel();
			}
		}
	}
	
	@Timeout
	public void execute(Timer timer) throws InterruptedException {
		log.info("executing timer.. " + timer.getInfo());
		if (!Contexts.isApplicationContextActive()) {
			Lifecycle.beginCall();
		}

		String info = "" + timer.getInfo();
		if (info != null && info.contains("AdviseMsg")) {
			Long id = Long.parseLong(info.substring(info.indexOf("-") + 1));
			try {
				GenericAdapterLocalInterface ga = GenericAdapter.instance();
				AdviseMsg msg = ga.get(AdviseMsg.class, id);

				if (info.startsWith("ON")) {
					log.info("ENABLED AdviseMsg " + id + ": " + msg.getText());
					AdviseMsgManager.instance().setAdviseMessage(msg);

				} else {
					AdviseMsg currentMsg = AdviseMsgManager.instance()
							.getAdviseMessage();

					if (currentMsg != null && currentMsg.getInternalId() == id) {
						log.info("DISABLED AdviseMsg " + id + ": "
								+ currentMsg.getText());
						AdviseMsgManager.instance().setAdviseMessage(null);
					} else {
						log.error("msg id to remove is not current message id ("
								+ id + ")");
					}
				}

			} catch (NamingException e) {
				log.error("error reading AdviseMsg with id " + id);
				e.printStackTrace();
			}
		}

	}

	public void executeTimer() {
		// not used
	}
}
