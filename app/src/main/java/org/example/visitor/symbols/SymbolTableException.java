package org.example.visitor.symbols;

public class SymbolTableException extends RuntimeException {
    public SymbolTableException(String errorMessage) {
        super(errorMessage);
    }
    public SymbolTableException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
