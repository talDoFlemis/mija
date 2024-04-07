package org.example.visitor.mermaid;

public class MermaidASTPrinterException extends RuntimeException{
    public MermaidASTPrinterException(String errorMessage) {
        super(errorMessage);
    }
    public MermaidASTPrinterException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}