package com.phi.pk;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
 
/**
 * ServletContextListener implementation to capture the JSF Application and
 * renderKitFactory. Should be registered into web.xml to work
 * 
 * @author anemeth
 * 
 */
public class MockServletContextListener implements ServletContextListener {
 
 public void contextDestroyed(ServletContextEvent event) {
 }
 
 public void contextInitialized(ServletContextEvent event) {
 
  Lifecycle.beginCall();
 
  Application application = ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication();
 
  RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
 
  FactoryData factoryData = (FactoryData) Component.getInstance("factoryData", true);
  factoryData.setApplication(application);
  factoryData.setRenderKitFactory(renderKitFactory);
 
 }
 
}
