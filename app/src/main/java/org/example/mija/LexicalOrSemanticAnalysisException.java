package org.example.mija;

public class LexicalOrSemanticAnalysisException extends RuntimeException {
	public LexicalOrSemanticAnalysisException(String message) {
		super(message);
	}

	public LexicalOrSemanticAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}
}
