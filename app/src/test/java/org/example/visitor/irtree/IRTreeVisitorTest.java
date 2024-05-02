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

    static Stream<Arguments> shouldCheckForASameList() {
        return Stream.of(
                Arguments.of(
                        BINOP.class,
                        ClassDeclSimple.builder()
                                .className(new Identifier("TubiasWithMethodLocal"))
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
                                .build()
                ),
                Arguments.of(
                        BINOP.class,
                        ClassDeclSimple.builder()
                                .className(new Identifier("TubiasWithMethodParams"))
                                .methods(new MethodDeclList(new ArrayList<>() {{
                                    add(MethodDecl.builder()
                                            .identifier("main")
                                            .formals(new FormalList(new ArrayList<>() {{
                                                add(Formal.builder().name("x").type(new IntegerType()).build());
                                            }}))
                                            .varDecls(new VarDeclList(new ArrayList<>()))
                                            .statements(new StatementList(new ArrayList<>() {{
                                                add(new Sout(new IdentifierExpression("x")));
                                            }}))
                                            .type(new IntegerType())
                                            .returnExpression(new IntegerLiteral(1))
                                            .build());
                                }}))
                                .build()
                ),
                Arguments.of(
                        BINOP.class,
                        ClassDeclSimple.builder()
                                .className(new Identifier("TubiasWithClassFields"))
                                .fields(new VarDeclList(new ArrayList<>() {{
                                    add(VarDecl.builder().name("x").type(new IntegerType()).build());
                                }}))
                                .methods(new MethodDeclList(new ArrayList<>() {{
                                    add(MethodDecl.builder()
                                            .identifier("main")
                                            .formals(new FormalList(new ArrayList<>()))
                                            .varDecls(new VarDeclList(new ArrayList<>()))
                                            .statements(new StatementList(new ArrayList<>() {{
                                                add(new Sout(new IdentifierExpression("x")));
                                            }}))
                                            .type(new IntegerType())
                                            .returnExpression(new IntegerLiteral(1))
                                            .build());
                                }}))
                                .build()
                ),
                Arguments.of(
                        BINOP.class,
                        ClassDeclSimple.builder()
                                .className(new Identifier("TubiasWithClassFields"))
                                .methods(new MethodDeclList(new ArrayList<>() {{
                                    add(MethodDecl.builder()
                                            .identifier("main")
                                            .formals(new FormalList(new ArrayList<>()))
                                            .varDecls(new VarDeclList(new ArrayList<>() {{
                                                add(VarDecl.builder().name("x").type(new IdentifierType(mockedMainClass().getClassName().getS())).build());
                                            }}))
                                            .statements(new StatementList(new ArrayList<>() {{
                                                add(Assign.builder()
                                                        .identifier(new Identifier("x"))
                                                        .value(new NewObject(new Identifier(mockedMainClass().getClassName().getS())))
                                                        .build());
                                            }}))
                                            .type(new IntegerType())
                                            .returnExpression(new IntegerLiteral(1))
                                            .build());
                                }}))
                                .build()
                )
        );
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

    @ParameterizedTest
    @DisplayName("Should check for a equals list")
    @MethodSource
    void shouldCheckForASameList(Class<? extends ExpAbstract> expected, ClassDeclSimple classDecl) {
        // ARRANGE
        Program prog = Program.builder()
                .mainClass(mockedMainClass())
                .classes(new ClassDeclList(new ArrayList<>() {{
                    add(classDecl);
                }}))
                .build();
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
        prog.accept(symbolTableVisitor);
        IRTreeVisitor irTreeVisitor = new IRTreeVisitor(symbolTableVisitor.getMainTable(), new MipsFrame());

        // ACT
        prog.accept(irTreeVisitor);

        System.out.println(irTreeVisitor.getListExp());

        // ASSERT

        for (var item : irTreeVisitor.getListExp()) {
            if (expected.isInstance(item)) {
                assertTrue(true);
                return;
            }
        }
        fail();
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
}