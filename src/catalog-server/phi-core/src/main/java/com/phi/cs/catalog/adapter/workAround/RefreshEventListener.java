package com.phi.cs.catalog.adapter.workAround;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.RefreshEvent;
import org.hibernate.event.def.DefaultRefreshEventListener;

/**
 * If you have a managed object, with a reference to a new object, 
 * where cascade refresh is enabled, and you refresh the managed object, 
 * then you will get an exception as refresh tries to cascade to the new object. 
 * In fact, the new object should just be ignored. This behavior is fixed from v3.5 onwards.
 * @author gbisson
 *
 */
@SuppressWarnings("serial")
public class RefreshEventListener extends DefaultRefreshEventListener {
	//private static final Log log = LogFactory.getLog(RefreshEventListener.class);

	/**
		This implementation throws no exception, if non-persistent object is refreshed
	*/
	@SuppressWarnings("rawtypes")
	@Override
	public void onRefresh(RefreshEvent event, Map refreshedAlready) throws HibernateException { 
		if (event.getSession().contains(event.getObject())) 
			super.onRefresh(event, refreshedAlready); 
		//else 
			//log.info("Object " + event.getObject() + " was probably not persisted yet"); 
	}
}