package org.example.parser;

import java.io.InputStream;
import java.util.Optional;

public interface ParserStrategy {
    Optional<Void> getProgram(InputStream stream);
    boolean isSyntaxOk(InputStream stream);
}