package org.example.canon;

public class CanonException extends RuntimeException {
	public CanonException(String errorMessage) {
		super(errorMessage);
	}

	public CanonException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
