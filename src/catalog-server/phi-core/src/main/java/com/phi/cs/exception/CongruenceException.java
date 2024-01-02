package com.phi.cs.exception;

/**
 * Thrown to indicate that a data congruence violation has been triggered. E.G.
 * like begin date > end date
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class CongruenceException extends ApplicationException {

	private static final long serialVersionUID = 6808528758359065972L;

	public CongruenceException(String _code) {
		super(_code);
	}

	public CongruenceException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public CongruenceException(String messages, String _code) {
		super(messages, _code);
	}

	public CongruenceException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}