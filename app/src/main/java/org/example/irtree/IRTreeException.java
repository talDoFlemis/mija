package org.example.irtree;

public class IRTreeException extends RuntimeException {
    public IRTreeException(String errorMessage) {
        super(errorMessage);
    }

    public IRTreeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
