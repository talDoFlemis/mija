package org.example.parser;

import org.example.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JavaCCParserTest {
    private final JavaCCParser parser = new JavaCCParser();

    private InputStream getInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    static Stream<Arguments> shouldParseAType() {
        return Stream.of(
                Arguments.of("int", new IntegerType()),
                Arguments.of("boolean", new BooleanType()),
                Arguments.of("int[]", new IntArrayType()),
                Arguments.of("tubias", new IdentifierType("tubias"))
        );
    }
    @DisplayName("Should parse a type")
    @ParameterizedTest
    @MethodSource
    void shouldParseAType(String input, Type expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var type = parser.getType(stream);

        // ASSERT
        assertEquals(expected, type);
    }

    @Test
    @DisplayName("Should parse a Formal")
    void shouldParseAFormal() throws org.example.javacc.ParseException {
        // ARRANGE
        String input = "int x";
        var stream = getInputStream(input);

        // ACT
        var formal = parser.getFormal(stream);

        // ASSERT
        assertEquals(new Formal(new IntegerType(), "x"), formal);
    }

    @Test
    @DisplayName("Should parse a Formal List")
    void shouldParseAFormalList() throws org.example.javacc.ParseException {
        // ARRANGE
        String input = "int x, boolean y, tubias z, int[] xypfodase";
        var stream = getInputStream(input);

        var expected = new FormalList();
        expected.addFormal(new Formal(new IntegerType(), "x"));
        expected.addFormal(new Formal(new BooleanType(), "y"));
        expected.addFormal(new Formal(new IdentifierType("tubias"), "z"));
        expected.addFormal(new Formal(new IntArrayType(), "xypfodase"));

        // ACT
        var formals = parser.getFormalList(stream);

        // ASSERT
        assertEquals(expected, formals);
    }

    @Test
    @DisplayName("Should parse a VarDecl")
    void shouldParseAVarDecl() throws org.example.javacc.ParseException {
        // ARRANGE
        String input = "int[] x;";
        var stream = getInputStream(input);

        // ACT
        var varDecl = parser.getVarDecl(stream);

        // ASSERT
        assertEquals(new VarDecl(new IntArrayType(), "x"), varDecl);
    }

    static Stream<Arguments> shouldParseAnExpression() {
        return Stream.of(
                Arguments.of("5", new IntegerLiteral(5)),
                Arguments.of("true", new True()),
                Arguments.of("false", new False()),
                Arguments.of("x", new IdentifierExpression("x")),
                Arguments.of("x[5]", new ArrayLookup(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("x.length", new ArrayLength(new IdentifierExpression("x"))),
                Arguments.of("!x", new Not(new IdentifierExpression("x"))),
                Arguments.of("x + 5", new Plus(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("x - 5", new Minus(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("x * 5", new Times(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("x < 5", new LessThan(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("(x) && y", new And(new IdentifierExpression("x"), new IdentifierExpression("y"))),
                Arguments.of("x[y]", new ArrayLookup(new IdentifierExpression("x"), new IdentifierExpression("y"))),
                Arguments.of("new int[5]", new NewArray(new IntegerLiteral(5))),
                Arguments.of("new gipity()", new NewObject(new Identifier("gipity"))),
                Arguments.of("(x + 5)", new Plus(new IdentifierExpression("x"), new IntegerLiteral(5))),
                Arguments.of("this", new This()),
                Arguments.of("x.y()", new Call(new IdentifierExpression("x"), new Identifier("y"), new ExpressionList())),
                Arguments.of("new Fac().ComputeFac()", new Call(new NewObject(new Identifier("Fac")), new Identifier("ComputeFac"), new ExpressionList())),
                Arguments.of("!current_node.GetHas_Right() && !current_node.GetHas_Left()", new And(new Not(new Call(new IdentifierExpression("current_node"), new Identifier("GetHas_Right"), new ExpressionList())), new Not(new Call(new IdentifierExpression("current_node"), new Identifier("GetHas_Left"), new ExpressionList()))))
        );
    }

    @DisplayName("Should parse an Expression")
    @MethodSource
    @ParameterizedTest
    void shouldParseAnExpression(String input, Expression expectedExpression) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var expression = parser.getExpression(stream);

        // ASSERT
        assertEquals(expectedExpression, expression);
    }

    static Stream<Arguments> shouldCheckForExpressionPrecedence() {
        return Stream.of(
                Arguments.of("3 < 5 && false",
                        And.builder()
                                .lhe(LessThan.builder()
                                        .lhe(new IntegerLiteral(3))
                                        .rhe(new IntegerLiteral(5))
                                        .build()
                                )
                                .rhe(new False())
                                .build()
                ),
                Arguments.of("a + b * c + d * e - f",
                        Minus.builder()
                                .lhe(Plus.builder()
                                        .lhe(Plus.builder()
                                                .lhe(new IdentifierExpression("a"))
                                                .rhe(Times.builder()
                                                        .lhe(new IdentifierExpression("b"))
                                                        .rhe(new IdentifierExpression("c"))
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .rhe(Times.builder()
                                                .lhe(new IdentifierExpression("d"))
                                                .rhe(new IdentifierExpression("e"))
                                                .build()
                                        )
                                        .build()
                                )
                                .rhe(new IdentifierExpression("f"))
                                .build()
                ),
                Arguments.of("3 + 4 * 5 && 3 * 1 + 4 * 5",
                        And.builder()
                                .lhe(Plus.builder()
                                        .lhe(new IntegerLiteral(3))
                                        .rhe(Times.builder()
                                                .lhe(new IntegerLiteral(4))
                                                .rhe(new IntegerLiteral(5))
                                                .build()
                                        )
                                        .build()
                                )
                                .rhe(Plus.builder()
                                        .lhe(Times.builder()
                                                .lhe(new IntegerLiteral(3))
                                                .rhe(new IntegerLiteral(1))
                                                .build()
                                        )
                                        .rhe(Times.builder()
                                                .lhe(new IntegerLiteral(4))
                                                .rhe(new IntegerLiteral(5))
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                ),
                Arguments.of("3 * (4 + 5)",
                        Times.builder()
                                .lhe(new IntegerLiteral(3))
                                .rhe(Plus.builder()
                                        .lhe(new IntegerLiteral(4))
                                        .rhe(new IntegerLiteral(5))
                                        .build()
                                )
                                .build()
                )
        );
    }

    @DisplayName("Should check for expression precedence")
    @ParameterizedTest
    @MethodSource
    void shouldCheckForExpressionPrecedence(String input, Expression expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var expression = parser.getExpression(stream);

        // ASSERT
        assertEquals(expected, expression);
    }

    static Stream<Arguments> shouldParseAStatement() {
        return Stream.of(
                Arguments.of("x = 5;", new Assign(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("x[5] = 5;", new ArrayAssign(new Identifier("x"), new IntegerLiteral(5), new IntegerLiteral(5))),
                Arguments.of("System.out.println(5);", new Sout(new IntegerLiteral(5))),
                Arguments.of("if (true) x = 5; else x = 777;", new If(new True(), new Assign(new Identifier("x"), new IntegerLiteral(5)), new Assign(new Identifier("x"), new IntegerLiteral(777)))),
                Arguments.of("while (true) x = 5;", new While(new True(), new Assign(new Identifier("x"), new IntegerLiteral(5)))),
                Arguments.of("{ x = 5; }", new Block(new StatementList(new ArrayList<>() {{
                    add(new Assign(new Identifier("x"), new IntegerLiteral(5)));
                }}))));
    }

    @DisplayName("Should parse a Statement")
    @ParameterizedTest
    @MethodSource
    void shouldParseAStatement(String input, Statement expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var statement = parser.getStatement(stream);

        // ASSERT
        assertEquals(expected, statement);
    }

    static Stream<Arguments> shouldParseAMethodDeclaration() {
        return Stream.of(
                Arguments.of("public int x() { return 5; }",
                        MethodDecl.builder()
                                .type(new IntegerType())
                                .identifier("x")
                                .formals(new FormalList())
                                .varDecls(new VarDeclList())
                                .statements(new StatementList())
                                .returnExpression(new IntegerLiteral(5))
                                .build()
                ),
                Arguments.of("public int x(int y, boolean z) { int x; return 5; }",
                        MethodDecl.builder()
                                .type(new IntegerType())
                                .identifier("x")
                                .formals(new FormalList(new ArrayList<>() {{
                                    add(new Formal(new IntegerType(), "y"));
                                    add(new Formal(new BooleanType(), "z"));
                                }}))
                                .varDecls(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "x"));
                                }}))
                                .statements(new StatementList())
                                .returnExpression(new IntegerLiteral(5))
                                .build()
                ),
                Arguments.of("public int x(int y, boolean z) { int x; x = 5; return x; }",
                        MethodDecl.builder()
                                .type(new IntegerType())
                                .identifier("x")
                                .formals(new FormalList(new ArrayList<>() {{
                                    add(new Formal(new IntegerType(), "y"));
                                    add(new Formal(new BooleanType(), "z"));
                                }}))
                                .varDecls(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "x"));
                                }}))
                                .statements(new StatementList(new ArrayList<>() {{
                                    add(new Assign(new Identifier("x"), new IntegerLiteral(5)));
                                }}))
                                .returnExpression(new IdentifierExpression("x"))
                                .build()
                )
        );
    }

    @DisplayName("Should parse a Method Declaration")
    @ParameterizedTest
    @MethodSource
    void shouldParseAMethodDeclaration(String input, MethodDecl expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var methodDecl = parser.getMethodDecl(stream);

        // ASSERT
        assertEquals(expected, methodDecl);
    }

    static Stream<Arguments> shouldParseAClassDeclaration() {
        return Stream.of(
                Arguments.of("class Tubias { int x; }",
                        ClassDeclSimple.builder()
                                .className(new Identifier("Tubias"))
                                .fields(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "x"));
                                }}))
                                .methods(new MethodDeclList())
                                .build()
                ),
                Arguments.of("class Tubias { int x; public int x() { return 5; } }",
                        ClassDeclSimple.builder()
                                .className(new Identifier("Tubias"))
                                .fields(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "x"));
                                }}))
                                .methods(new MethodDeclList(new ArrayList<>() {{
                                    add(MethodDecl.builder()
                                            .type(new IntegerType())
                                            .identifier("x")
                                            .formals(new FormalList())
                                            .varDecls(new VarDeclList())
                                            .statements(new StatementList())
                                            .returnExpression(new IntegerLiteral(5))
                                            .build());
                                }}))
                                .build()
                ),
                Arguments.of("class Tubias extends Gipity { int x; public int x() { return 5; } }",
                        ClassDeclExtends.builder()
                                .className(new Identifier("Tubias"))
                                .parent(new Identifier("Gipity"))
                                .fields(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "x"));
                                }}))
                                .methods(new MethodDeclList(new ArrayList<>() {{
                                    add(MethodDecl.builder()
                                            .type(new IntegerType())
                                            .identifier("x")
                                            .formals(new FormalList())
                                            .varDecls(new VarDeclList())
                                            .statements(new StatementList())
                                            .returnExpression(new IntegerLiteral(5))
                                            .build());
                                }}))
                                .build()
                )
        );
    }

    @DisplayName("Should parse a Class Declaration")
    @ParameterizedTest
    @MethodSource
    void shouldParseAClassDeclaration(String input, ClassDecl expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var classDecl = parser.getClassDecl(stream);

        // ASSERT
        assertEquals(expected, classDecl);
    }

    static Stream<Arguments> shouldParseAProgram() {
        return Stream.of(
                Arguments.of("""
                                class Tubias {
                                    public static void main(String[] args) {
                                        System.out.println(1);
                                    }
                                }
                                """,
                        Program.builder()
                                .mainClass(MainClass.builder()
                                        .className(new Identifier("Tubias"))
                                        .argsName(new Identifier("args"))
                                        .statements(new StatementList(new ArrayList<>() {{
                                            add(new Sout(new IntegerLiteral(1)));
                                        }}))
                                        .build()
                                )
                                .classes(new ClassDeclList())
                                .build()
                ),
                Arguments.of("""
                                class Tubias {
                                    public static void main(String[] tobaianor) {
                                        System.out.println(1);
                                    }
                                }
                                class Gipity {
                                    int x;
                                }
                                """,
                        Program.builder()
                                .mainClass(MainClass.builder()
                                        .className(new Identifier("Tubias"))
                                        .argsName(new Identifier("tobaianor"))
                                        .statements(new StatementList(new ArrayList<>() {{
                                            add(new Sout(new IntegerLiteral(1)));
                                        }}))
                                        .build()
                                )
                                .classes(new ClassDeclList(new ArrayList<>() {{
                                    add(ClassDeclSimple.builder()
                                            .className(new Identifier("Gipity"))
                                            .fields(new VarDeclList(new ArrayList<>() {{
                                                add(new VarDecl(new IntegerType(), "x"));
                                            }}))
                                            .build());
                                }}))
                                .build()
                )
        );
    }

    @DisplayName("Should parse a Program")
    @ParameterizedTest
    @MethodSource
    void shouldParseAProgram(String input, Program expected) {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var option = parser.getProgram(stream);

        // ASSERT
        var program = assertDoesNotThrow(option::get);
        assertEquals(expected, program);
    }
}
