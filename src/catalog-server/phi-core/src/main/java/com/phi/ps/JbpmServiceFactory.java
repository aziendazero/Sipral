package com.phi.ps;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

/**
 * Service factory for JbpmEventService
 * @author alex.zupan
 */
public class JbpmServiceFactory implements ServiceFactory {

  private static final long serialVersionUID = 1L;
  
  private String supportedEventTypes;
  
  public void setSupportedEventTypes(String eventTypes) {
    supportedEventTypes = eventTypes;
  }
  
  public Service openService() {
    return new JbpmEventService(supportedEventTypes);
  }

  public void close() {
  }
}
