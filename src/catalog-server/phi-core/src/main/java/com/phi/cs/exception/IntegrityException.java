package com.phi.cs.exception;

/**
 * Thrown to indicate that any data integrity violation has been triggered. E.G.
 * like unknown patient reference
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class IntegrityException extends ApplicationException {

	private static final long serialVersionUID = -2968356932187066518L;

	public IntegrityException(String _code) {
		super(_code);
	}

	public IntegrityException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public IntegrityException(String messages, String _code) {
		super(messages, _code);
	}

	public IntegrityException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}