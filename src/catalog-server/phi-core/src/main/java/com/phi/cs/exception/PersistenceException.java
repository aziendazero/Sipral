package com.phi.cs.exception;

/**
 * Thrown everytime that an data tier exception is catched.
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class PersistenceException extends PhiException {

	private static final long serialVersionUID = -2614866163795096924L;

	public PersistenceException(String _code) {
		super(_code);
	}

	public PersistenceException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public PersistenceException(String messages, String _code) {
		super(messages, _code);
	}

	public PersistenceException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}