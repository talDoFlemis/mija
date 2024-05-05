package org.example.mija;

import org.example.ast.Program;
import org.example.visitor.symbols.MainTable;

public interface SemanticAnalysisStrategy {
	boolean isSemanticsOk(Program program);

	void isSemanticsOkOrThrow(Program program) throws SemanticAnalysisException;

	MainTable getMainTable();
}
