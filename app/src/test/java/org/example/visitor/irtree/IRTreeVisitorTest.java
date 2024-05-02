package org.example.visitor.irtree;

import org.example.ast.*;
import org.example.irtree.BINOP;
import org.example.irtree.CONST;
import org.example.irtree.ExpAbstract;
import org.example.mips.MipsFrame;
import org.example.visitor.symbols.SymbolTableVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

        // ASSERT
        assertFalse(irTreeVisitor.getListExp().isEmpty());
    }

    static Stream<Arguments> shouldParseBinaryAndUnaryOperations() {
        return Stream.of(
                Arguments.of(
                        new Plus(new IntegerLiteral(1), new IntegerLiteral(2)),
                        new Exp(new BINOP(BINOP.PLUS, new CONST(1), new CONST(2)))
                ),
                Arguments.of(
                        new Minus(new IntegerLiteral(1), new IntegerLiteral(2)),
                        new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
                ),
                Arguments.of(
                        new Times(new IntegerLiteral(1), new IntegerLiteral(2)),
                        new Exp(new BINOP(BINOP.MUL, new CONST(1), new CONST(2)))
                ),
                Arguments.of(
                        new Not(new IntegerLiteral(2)),
                        new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
                ),
                Arguments.of(
                        new And(new True(), new False()),
                        new Exp(new BINOP(BINOP.AND, new CONST(1), new CONST(0)))
                ),
                Arguments.of(
                        new LessThan(new IntegerLiteral(1), new IntegerLiteral(2)),
                        new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
                ),
                Arguments.of(
                        new Plus(
                                new Plus(new IntegerLiteral(1),
                                        new Minus(new IntegerLiteral(2), new IntegerLiteral(3))
                                ), new IntegerLiteral(4)
                        ),
                        new Exp(new BINOP(BINOP.PLUS,
                                new BINOP(BINOP.PLUS, new CONST(1),
                                        new BINOP(BINOP.MINUS, new CONST(2), new CONST(3))
                                ), new CONST(4)
                        ))
                )
        );
    }

    @DisplayName("Should parse binary and unary operations")
    @MethodSource
    @ParameterizedTest
    void shouldParseBinaryAndUnaryOperations(Node node, Exp expectedNode) {
        // Arrange
        var visitor = IRTreeVisitor.builder().build();

        // Act
        Exp actualNode = node.accept(visitor);

        // Assert
        assertEquals(expectedNode, actualNode);
    }

    static Stream<Arguments> shouldParseSimpleLiterals() {
        return Stream.of(
                Arguments.of(new IntegerLiteral(1), new Exp(new CONST(1))),
                Arguments.of(new True(), new Exp(new CONST(1))),
                Arguments.of(new False(), new Exp(new CONST(0)))
        );
    }

    @ParameterizedTest
    @DisplayName("Should parse simple literals")
    @MethodSource
    void shouldParseSimpleLiterals(Node node, Exp expectedNode) {
        // Arrange
        var visitor = IRTreeVisitor.builder().build();

        // Act
        Exp actualNode = node.accept(visitor);

        // Assert
        assertEquals(expectedNode, actualNode);
    }
}
