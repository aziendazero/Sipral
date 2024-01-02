package com.phi.cs.exception;

/**
 * PhiException is the superclass for all the exceptions that are managed by PHI
 * and the ONLY that must arrive to the ErrorManager
 * 
 * @author Davide Magni
 * @version 1.0
 * @see com.phi.cs.error.ErrorManager
 */
public class PhiException extends Exception {

	private static final long serialVersionUID = 4356541046251102094L;

	// Code is the code error that must be associated when the Exception is
	// thrown and must be present in the bundle
	private String code;

	public PhiException(String _code) {
		super();
		code = _code;
	}

	public PhiException(Throwable cause, String _code) {
		super(cause);
		code = _code;
	}

	public PhiException(String messages, String _code) {
		super(messages);
		code = _code;
	}

	public PhiException(String messages, Throwable cause, String _code) {
		super(messages, cause);
		code = _code;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		String exMsg = super.getMessage();
		String retMsg = null;
		if (exMsg != null) {
			int posEx = exMsg.lastIndexOf("Caused by:");

			if (posEx > 0) {
				retMsg = exMsg.substring(posEx, exMsg.length());
			} else
				retMsg = exMsg;
		} else
			retMsg = "NO EXCEPTION MSG";
		return retMsg;
	}
}