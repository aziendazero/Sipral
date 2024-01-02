package com.phi.cs.validators;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class NationalIdentificationNumberValidator extends UniqueRecordValidator {

	protected boolean check = true;
	
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {


		String nin = arg2.toString().toUpperCase().split(",")[0];

		// Italian National Identification Number syntactic regular expression 
		String regex = "[a-zA-Z]{6}[0-9l-np-vL-NP-V]{2}[a-zA-Z][0-9l-np-vL-NP-V]{2}[a-zA-Z][0-9l-np-vL-NP-V]{3}[a-zA-Z]"; 

		if (!Pattern.matches(regex, nin)) {
			// NIN syntax verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_NIN_TYPE_FORMAT_ERR_CODE));
		}

		int sum = 0; 

		// Odd character hash
		HashMap<Character,Integer> oddChar = new HashMap<Character,Integer>(); 
		oddChar.put('0',1);
		oddChar.put('1',0);
		oddChar.put('2',5);
		oddChar.put('3',7);
		oddChar.put('4',9);
		oddChar.put('5',13);
		oddChar.put('6',15);
		oddChar.put('7',17);
		oddChar.put('8',19);
		oddChar.put('9',21);
		oddChar.put('A',1);
		oddChar.put('B',0);
		oddChar.put('C',5);
		oddChar.put('D',7);
		oddChar.put('E',9);
		oddChar.put('F',13);
		oddChar.put('G',15);
		oddChar.put('H',17);
		oddChar.put('I',19);
		oddChar.put('J',21);
		oddChar.put('K',2);
		oddChar.put('L',4);
		oddChar.put('M',18);
		oddChar.put('N',20);
		oddChar.put('O',11);
		oddChar.put('P',3);
		oddChar.put('Q',6);
		oddChar.put('R',8);
		oddChar.put('S',12);
		oddChar.put('T',14);
		oddChar.put('U',16);
		oddChar.put('V',10);
		oddChar.put('W',22);
		oddChar.put('X',25);
		oddChar.put('Y',24);
		oddChar.put('Z',23);

		//Even character hash
		HashMap<Character,Integer> evenChar = new HashMap<Character,Integer>(); 
		evenChar.put('0',0);
		evenChar.put('1',1);
		evenChar.put('2',2);
		evenChar.put('3',3);
		evenChar.put('4',4);
		evenChar.put('5',5);
		evenChar.put('6',6);
		evenChar.put('7',7);
		evenChar.put('8',8);
		evenChar.put('9',9);
		evenChar.put('A',0);
		evenChar.put('B',1);
		evenChar.put('C',2);
		evenChar.put('D',3);
		evenChar.put('E',4);
		evenChar.put('F',5);
		evenChar.put('G',6);
		evenChar.put('H',7);
		evenChar.put('I',8);
		evenChar.put('J',9);
		evenChar.put('K',10);
		evenChar.put('L',11);
		evenChar.put('M',12);
		evenChar.put('N',13);
		evenChar.put('O',14);
		evenChar.put('P',15);
		evenChar.put('Q',16);
		evenChar.put('R',17);
		evenChar.put('S',18);
		evenChar.put('T',19);
		evenChar.put('U',20);
		evenChar.put('V',21);
		evenChar.put('W',22);
		evenChar.put('X',23);
		evenChar.put('Y',24);
		evenChar.put('Z',25);

		// Control Character values hash
		HashMap<Integer,Character> controlChar = new HashMap<Integer,Character>(); 
		controlChar.put(0,'A');
		controlChar.put(1,'B');
		controlChar.put(2,'C');
		controlChar.put(3,'D');
		controlChar.put(4,'E');
		controlChar.put(5,'F');
		controlChar.put(6,'G');
		controlChar.put(7,'H');
		controlChar.put(8,'I');
		controlChar.put(9,'J');
		controlChar.put(10,'K');
		controlChar.put(11,'L');
		controlChar.put(12,'M');
		controlChar.put(13,'N');
		controlChar.put(14,'O');
		controlChar.put(15,'P');
		controlChar.put(16,'Q');
		controlChar.put(17,'R');
		controlChar.put(18,'S');
		controlChar.put(19,'T');
		controlChar.put(20,'U');
		controlChar.put(21,'V');
		controlChar.put(22,'W');
		controlChar.put(23,'X');
		controlChar.put(24,'Y');
		controlChar.put(25,'Z');

		// NIN Characters values sum 
		for(int i=0; i<15; i++) { 
			if (i % 2 == 1) { 
				sum += evenChar.get(nin.charAt(i)); 
			} else { 
				sum += oddChar.get(nin.charAt(i)); 
			} 
		} 

		// Control Character calculation
		Character calculatedControlChar = controlChar.get(sum % 26); 

		// Inserted Control Character
		Character insertedControlChar = nin.charAt(15);

		// Validation
		if (!insertedControlChar.equals(calculatedControlChar)) {
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_NIN_TYPE_PARITY_CHECK_ERR_CODE));
		}
		
		if (check)
			checkUniqueness(arg1,arg2);
	}
}
