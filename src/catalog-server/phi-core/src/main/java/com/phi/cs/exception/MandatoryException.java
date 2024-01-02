package com.phi.cs.exception;

/**
 * Thrown to indicate that a mandatory field is not setted
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class MandatoryException extends ApplicationException {

	private static final long serialVersionUID = 7690170883805320532L;

	public MandatoryException(String _code) {
		super(_code);
	}

	public MandatoryException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public MandatoryException(String messages, String _code) {
		super(messages, _code);
	}

	public MandatoryException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}