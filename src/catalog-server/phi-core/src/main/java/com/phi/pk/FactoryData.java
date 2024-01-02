package com.phi.pk;

import javax.faces.application.Application;
import javax.faces.render.RenderKitFactory;
 
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
 
/**
 * Class for holding the JSF "Application" instance and the renderKitFactory.
 * @author anemeth
 *
 */
@Name("factoryData")
@Scope(ScopeType.APPLICATION)
public class FactoryData {
  
 private Application application;
 
 private RenderKitFactory renderKitFactory;
  
 /**
  * @return the application
  */
 public Application getApplication() {
  return this.application;
 }
 
 /**
  * @return the renderKitFactory
  */
 public RenderKitFactory getRenderKitFactory() {
  return this.renderKitFactory;
 }
 
 /**
  * @param application
  *            the application to set
  */
 public void setApplication(Application application) {
  this.application = application;
 }
 
 /**
  * @param renderKitFactory
  *            the renderKitFactory to set
  */
 public void setRenderKitFactory(RenderKitFactory renderKitFactory) {
  this.renderKitFactory = renderKitFactory;
 }
 
 /**
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString() {
  StringBuilder builder = new StringBuilder();
  builder.append("FactoryData [application=");
  builder.append(this.application);
  builder.append(", renderKitFactory=");
  builder.append(this.renderKitFactory);
  builder.append("]");
  return builder.toString();
 }
}
