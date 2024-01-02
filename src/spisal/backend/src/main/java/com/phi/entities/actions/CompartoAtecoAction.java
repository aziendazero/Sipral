package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.CompartoAteco;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("CompartoAtecoAction")
@Scope(ScopeType.CONVERSATION)
public class CompartoAtecoAction extends BaseAction<CompartoAteco, Long> {

	private static final long serialVersionUID = 1722498833L;
	private CodeValueAteco currentAteco;
	private CompartoAteco currentCompartoAteco;

	public static CompartoAtecoAction instance() {
		return (CompartoAtecoAction) Component.getInstance(CompartoAtecoAction.class, ScopeType.CONVERSATION);
	}

	public String getComparto(CodeValueAteco cv) throws PhiException{
		return getCompartoOrSpecificazione(cv, false);
	}
	
	public String getSpecificazione(CodeValueAteco cv) throws PhiException{
		return getCompartoOrSpecificazione(cv, true);
	}

	public List<SelectItem> createAtecoFilterModeCombo() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		
		SelectItem attivitaIstat = new SelectItem(new String("attivitaIstat"), (String)app.evaluateExpressionGet(fc, "${static.Label_attivitaIstat}", String.class));
		SelectItem compartoAteco = new SelectItem(new String("compartoAteco"), (String)app.evaluateExpressionGet(fc, "${static.Label_compartoAteco}", String.class));
		SelectItem specificazione = new SelectItem(new String("specificazione"), (String)app.evaluateExpressionGet(fc, "${static.Label_specificazione}", String.class));
				
		options.add(attivitaIstat);
		options.add(compartoAteco);
		options.add(specificazione);
		
		return options;
	}
	
	@SuppressWarnings("unchecked")
	private String getCompartoOrSpecificazione(Object cv, boolean isSpec) throws PhiException {
		if(cv instanceof CodeValueAteco){ 
			if (!cv.equals(currentAteco)) {
				currentAteco = null;
				currentCompartoAteco = null;
				List<CompartoAteco> list = (List<CompartoAteco>) ca.executeNamedQuery("CompartoAteco.getCompartoOrSpecificazione", Collections.singletonMap("cvAteco", cv));
				if(list!=null && !list.isEmpty()){
					currentCompartoAteco = list.get(0);
				}
			}
			
			if (currentCompartoAteco != null) {
				currentAteco = (CodeValueAteco) cv;
				CodeValuePhi comparto = currentCompartoAteco.getComparto();
				if(comparto!=null && !"comparti".equals(comparto.getParent().getCode())){
					return isSpec ? comparto.getCurrentTranslation() : comparto.getParent().getCurrentTranslation();
				}else{
					return isSpec ? "-" : comparto.getCurrentTranslation();
				}
			}
		}
		
		return "";
	}
}