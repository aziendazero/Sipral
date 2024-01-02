package com.phi.cs.exception;

/**
 * Thrown everytime that an infrastracture exception is catched.
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class SystemException extends PhiException {

	private static final long serialVersionUID = 6096629725721945740L;

	public SystemException(String _code) {
		super(_code);
	}

	public SystemException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public SystemException(String messages, String _code) {
		super(messages, _code);
	}

	public SystemException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}
}