package org.example.visitor.irtree;

import org.example.ast.*;
import org.example.mips.MipsFrame;
import org.example.visitor.symbols.SymbolTableVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;

class IRTreeVisitorTest {
    static MainClass mockedMainClass() {
        return MainClass.builder()
                .className(new Identifier("Main"))
                .argsName(new Identifier("args"))
                .statements(new StatementList(new ArrayList<>() {{
                    add(new Sout(new IntegerLiteral(1)));
                }}))
                .build();
    }

    @Test
    @DisplayName("Should check a non empty list of expression")
    void shouldCheckANonEmptyListOfExpression() {
        // ARRANGE
        Program prog = Program.builder()
                .mainClass(mockedMainClass())
                .classes(new ClassDeclList(new ArrayList<>() {{
                    add(ClassDeclSimple.builder()
                            .className(new Identifier("method"))
                            .methods(new MethodDeclList(new ArrayList<>() {{
                                add(MethodDecl.builder()
                                        .identifier("main")
                                        .formals(new FormalList(new ArrayList<>()))
                                        .varDecls(new VarDeclList(new ArrayList<>() {{
                                            add(VarDecl.builder().name("x").type(new IntegerType()).build());
                                        }}))
                                        .statements(new StatementList(new ArrayList<>() {{
                                            add(new Sout(new IdentifierExpression("x")));
                                        }}))
                                        .type(new IntegerType())
                                        .returnExpression(new IntegerLiteral(1))
                                        .build());
                            }}))
                            .build());
                }}))
                .build();
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
        prog.accept(symbolTableVisitor);
        IRTreeVisitor irTreeVisitor = new IRTreeVisitor(symbolTableVisitor.getMainTable(), new MipsFrame());

        // ACT
        prog.accept(irTreeVisitor);

        System.out.println(irTreeVisitor.getListExp());

        // ASSERT
        assertFalse(irTreeVisitor.getListExp().isEmpty());
    }
}
