package org.example.parser;

import org.example.ast.Program;

import java.io.InputStream;
import java.util.Optional;

public interface ParserStrategy {
    Optional<Program> getProgram(InputStream stream);

    boolean isSyntaxOk(InputStream stream);
}