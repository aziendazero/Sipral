package com.phi.cs.exception;

public class DictionaryException extends PhiException {

	private static final long serialVersionUID = -1252777944847440722L;

	public DictionaryException(String _code) {
		super(_code);
	}

	public DictionaryException(Throwable cause, String _code) {
		super(cause, _code);
	}

	public DictionaryException(String messages, String _code) {
		super(messages, _code);
	}

	public DictionaryException(String messages, Throwable cause, String _code) {
		super(messages, cause, _code);
	}

}