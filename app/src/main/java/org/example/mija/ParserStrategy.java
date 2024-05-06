package org.example.mija;

import org.example.ast.Program;

import java.io.InputStream;
import java.util.Optional;

public interface ParserStrategy {
	Optional<Program> getProgram(InputStream stream);

	boolean isSyntaxOk(InputStream stream);

	Program getProgramOrThrow(InputStream stream) throws LexicalOrSemanticAnalysisException;
}