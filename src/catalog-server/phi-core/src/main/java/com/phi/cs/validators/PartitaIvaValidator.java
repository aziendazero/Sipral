package com.phi.cs.validators;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class PartitaIvaValidator extends UniqueRecordValidator {

	protected boolean check = true;
	
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {


		String nin = arg2.toString().toUpperCase().split(",")[0];

		// Italian National Identification Number syntactic regular expression 
		String regex = "[0-9]{11}"; 

		if (!Pattern.matches(regex, nin)) {
			// NIN syntax verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_PIVA_TYPE_FORMAT_ERR_CODE));
		}

		int sum = 0; 

		// Odd character hash
		HashMap<Character,Integer> oddChar = new HashMap<Character,Integer>(); 
		oddChar.put('0',0);
		oddChar.put('1',1);
		oddChar.put('2',2);
		oddChar.put('3',3);
		oddChar.put('4',4);
		oddChar.put('5',5);
		oddChar.put('6',6);
		oddChar.put('7',7);
		oddChar.put('8',8);
		oddChar.put('9',9);

		//Even character hash
		HashMap<Character,Integer> evenChar = new HashMap<Character,Integer>(); 
		evenChar.put('0',0);
		evenChar.put('1',2);
		evenChar.put('2',4);
		evenChar.put('3',6);
		evenChar.put('4',8);
		evenChar.put('5',1);
		evenChar.put('6',3);
		evenChar.put('7',5);
		evenChar.put('8',7);
		evenChar.put('9',9);

		// Control Character values hash
		HashMap<Integer,Character> controlChar = new HashMap<Integer,Character>(); 
		controlChar.put(0,'0');
		controlChar.put(1,'1');
		controlChar.put(2,'2');
		controlChar.put(3,'3');
		controlChar.put(4,'4');
		controlChar.put(5,'5');
		controlChar.put(6,'6');
		controlChar.put(7,'7');
		controlChar.put(8,'8');
		controlChar.put(9,'9');

		// NIN Characters values sum 
		for(int i=0; i<10; i++) { 
			if (i % 2 == 1) { 
				sum += evenChar.get(nin.charAt(i)); 
			} else { 
				sum += oddChar.get(nin.charAt(i)); 
			} 
		} 

		// Control Character calculation
		Character calculatedControlChar = controlChar.get((10 -(sum % 10))%10); 

		// Inserted Control Character
		Character insertedControlChar = nin.charAt(10);

		// Validation
		if (!insertedControlChar.equals(calculatedControlChar)) {
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_PIVA_TYPE_PARITY_CHECK_ERR_CODE));
		}
		
		if (check)
			checkUniqueness(arg1,arg2);
	}

}
