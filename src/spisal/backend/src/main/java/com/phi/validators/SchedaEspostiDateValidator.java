package com.phi.validators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.entities.actions.SchedaEspostiAction;
import com.phi.entities.baseEntity.SchedaEsposti;

/**
 * Questo validatore controlla che la data di compilazione di una scheda esposti sia successiva alla data fine validità
 * dell'ultima scheda esposti chiusa in precedenza
 * @author 510087
 *
 */
public class SchedaEspostiDateValidator implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)	throws ValidatorException {
		Date date = null;

		if (arg2 instanceof Date) {
			date = (Date)arg2;

		}else {
			return;
		}
		
		SchedaEsposti oldScheda = (SchedaEsposti)SchedaEspostiAction.instance().getTemporary().get("oldScheda");
		//se non c'è una scheda precedente, va bene qualunque data
		if(oldScheda==null)
			return;
		
		Date previousDate = oldScheda.getEndValidity();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		if(previousDate!=null && previousDate.after(date)){
			throw new ValidatorException(new FacesMessage (FacesMessage.SEVERITY_ERROR, "", "La data deve essere successiva a quella di fine validità della precedente scheda (" + sdf.format(previousDate) +")"));
		}
	}
}
