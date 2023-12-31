/**
 * 
 */
package com.phi.pk;

import javax.faces.context.FacesContext;

import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;

/**
 * @author russian
 *
 */
public class EmptyFacesContext extends MockFacesContext {

    FacesContext originalFacesContext;

    public EmptyFacesContext(){
        super(new MockExternalContext());
        originalFacesContext = FacesContext.getCurrentInstance();
        createViewRoot();
        FacesContext.setCurrentInstance(this);
    }

    public void restore(){
        setCurrentInstance(originalFacesContext);
    }

}