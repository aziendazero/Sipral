package com.phi.cs.exception;

/**
 * Thrown everytime that an application error occurs.
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class ApplicationException extends PhiException {

	private static final long serialVersionUID = 4810678859832222864L;

	public ApplicationException(String _code) {
		super(_code);
	}

	public ApplicationException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public ApplicationException(String messages, String _code) {
		super(messages, _code);
	}

	public ApplicationException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}
}