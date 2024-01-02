package com.phi.cs.exception;

/**
 * Thrown to indicate that any data uniqueness violation has been triggered.
 * 
 * @author Davide Magni
 * @version 1.0
 */
public class UniqnessException extends ApplicationException {

	private static final long serialVersionUID = -2341279955911373539L;

	public UniqnessException(String _code) {
		super(_code);
	}

	public UniqnessException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public UniqnessException(String messages, String _code) {
		super(messages, _code);
	}

	public UniqnessException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}