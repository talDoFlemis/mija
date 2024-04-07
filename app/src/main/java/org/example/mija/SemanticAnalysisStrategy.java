package org.example.mija;

import org.example.ast.Program;

public interface SemanticAnalysisStrategy {
    boolean isSemanticsOk(Program program);
    void isSemanticsOkOrThrow(Program program) throws SemanticAnalysisException;
}
