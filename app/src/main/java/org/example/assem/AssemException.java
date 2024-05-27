package org.example.assem;

public class AssemException extends RuntimeException {
	public AssemException(String errorMessage) {
		super(errorMessage);
	}

	public AssemException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
