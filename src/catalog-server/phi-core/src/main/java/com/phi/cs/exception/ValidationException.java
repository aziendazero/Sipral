package com.phi.cs.exception;

/**
 * Thrown to indicate that any data validation violation has been triggered.
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class ValidationException extends ApplicationException {

	private static final long serialVersionUID = -3387519484966303706L;

	public ValidationException(String _code) {
		super(_code);
	}

	public ValidationException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public ValidationException(String messages, String _code) {
		super(messages, _code);
	}

	public ValidationException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}