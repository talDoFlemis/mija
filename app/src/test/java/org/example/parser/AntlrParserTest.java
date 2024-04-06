
package org.example.parser;

import org.example.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AntlrParserTest {

    private InputStream getInputStream(String input, int needSout) {
        String s = switch (needSout) {
            case 1 -> " class Testes{ public static void main(String[] a){ System.out.println( " + input + " ); }} ";
            case 2 -> " class Testes{ public static void main(String[] a){ " + input + " }} ";
            case 3 -> input;
            case 4 -> " class Testes{ public static void main(String[] a){ System.out.println(!7); }} " + input;
            case 5 -> " class Factorial{ public static void main(String[] a){ System.out.println(!7); } } class Teste { " + input + " }";
            default -> " ";
        };
        return new ByteArrayInputStream(s.getBytes());
    }

    static Stream<Arguments> shouldParseAnExpression() {
        return Stream.of(
                Arguments.of("5",
                        Sout.builder().expression(
                                new IntegerLiteral(5)
                        ).build()
                ),
                Arguments.of("true",
                        Sout.builder().expression(
                                new True()).build()
                ),
                Arguments.of("false",
                        Sout.builder().expression(
                                new False()
                        ).build()
                ),
                Arguments.of("x",
                        Sout.builder().expression(
                                new IdentifierExpression("x")
                        ).build()
                ),
                Arguments.of("x[5]",
                        Sout.builder().expression(
                                new ArrayLookup(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("x.length",
                        Sout.builder().expression(
                                new ArrayLength(new IdentifierExpression("x"))
                        ).build()
                ),
                Arguments.of("!x",
                        Sout.builder().expression(
                                new Not(new IdentifierExpression("x"))
                        ).build()
                ),
                Arguments.of("x + 5",
                        Sout.builder().expression(
                                new Plus(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("x - 5",
                        Sout.builder().expression(
                                new Minus(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("x * 5",
                        Sout.builder().expression(
                                new Times(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("x < 5",
                        Sout.builder().expression(
                                new LessThan(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("(x) && y",
                        Sout.builder().expression(
                                new And(new IdentifierExpression("x"), new IdentifierExpression("y"))
                        ).build()
                ),
                Arguments.of("x[y]",
                        Sout.builder().expression(
                                new ArrayLookup(new IdentifierExpression("x"), new IdentifierExpression("y"))
                        ).build()
                ),
                Arguments.of("new int[5]",
                        Sout.builder().expression(
                                new NewArray(new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("new gipity()",
                        Sout.builder().expression(
                                new NewObject(new Identifier("gipity"))
                        ).build()
                ),
                Arguments.of("(x + 5)",
                        Sout.builder().expression(
                                new Plus(new IdentifierExpression("x"), new IntegerLiteral(5))
                        ).build()
                ),
                Arguments.of("this",
                        Sout.builder().expression(
                                new This()
                        ).build()
                ),
                Arguments.of("x.y()",
                        Sout.builder().expression(
                                new Call(new IdentifierExpression("x"), new Identifier("y"), new ExpressionList())
                        ).build()
                ),
                Arguments.of("new Fac().ComputeFac()",
                        Sout.builder().expression(
                                new Call(new NewObject(new Identifier("Fac")), new Identifier("ComputeFac"), new ExpressionList())
                        ).build()
                ),
                Arguments.of("!current_node.GetHas_Right() && !current_node.GetHas_Left()",
                        Sout.builder().expression(
                                new And(new Not(new Call(new IdentifierExpression("current_node"), new Identifier("GetHas_Right"), new ExpressionList())), new Not(new Call(new IdentifierExpression("current_node"), new Identifier("GetHas_Left"), new ExpressionList())))
                        ).build()
                )
        );
    }

    @DisplayName("Should parse an Expression")
    @MethodSource
    @ParameterizedTest
    void shouldParseAnExpression(String input, Statement expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input, 1);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = antlrParser.getProgram(stream);
        // ASSERT
        test.ifPresent(program -> {
            var statement = program.getMainClass().getStatements().getStatements().getFirst();
            assertEquals(expected, statement);
        });
    }

    static Stream<Arguments> shouldCheckForExpressionPrecedence() {
        return Stream.of(
                Arguments.of(
                        "3<5 && false",
                        Sout.builder()
                                .expression(
                                        And.builder()
                                                .lhe(LessThan.builder()
                                                        .lhe(new IntegerLiteral(3))
                                                        .rhe(new IntegerLiteral(5))
                                                        .build()
                                                )
                                                .rhe(new False())
                                                .build())
                                .build()
                ),
                Arguments.of(
                        "a + b * c + d * e - f",
                        Sout.builder()
                                .expression(
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
                                )
                                .build()
                ),
                Arguments.of(
                        "3 + 4 * 5 && 3 * 1 + 4 * 5",
                        Sout.builder()
                                .expression(
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
                                )
                                .build()
                ),
                Arguments.of(
                        "3 * (4 + 5)",
                        Sout.builder()
                                .expression(
                                        Times.builder()
                                                .lhe(new IntegerLiteral(3))
                                                .rhe(Plus.builder()
                                                        .lhe(new IntegerLiteral(4))
                                                        .rhe(new IntegerLiteral(5))
                                                        .build()
                                                )
                                                .build()
                                )
                                .build()
                )

        );


    }

    @DisplayName("Should check for expression precedence")
    @ParameterizedTest
    @MethodSource
    void shouldCheckForExpressionPrecedence(String input, Statement expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input, 1);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = antlrParser.getProgram(stream);
        // ASSERT
        test.ifPresent(program -> {
            var statement = program.getMainClass().getStatements().getStatements().getFirst();
            assertEquals(expected, statement);
        });
    }

    static Stream<Arguments> shouldParseAStatement() {
        return Stream.of(
                Arguments.of("x = 5;",
                        new Assign(
                                new Identifier("x"), new IntegerLiteral(5)
                        )
                ),
                Arguments.of("x[5] = 5;",
                        new ArrayAssign(
                                new Identifier("x"), new IntegerLiteral(5), new IntegerLiteral(5)
                        )
                ),
                Arguments.of("System.out.println(5);",
                        new Sout(
                                new IntegerLiteral(5)
                        )
                ),
                Arguments.of("if (true) x = 5; else x = 777;",
                        new If(
                                new True(), new Assign(new Identifier("x"), new IntegerLiteral(5)), // IF
                                new Assign(new Identifier("x"), new IntegerLiteral(777)) // Else
                        )
                ),
                Arguments.of("while (true) x = 5;",
                        new While(
                                new True(), new Assign(new Identifier("x"), new IntegerLiteral(5))
                        )
                ),
                Arguments.of("{ x = 5; }",
                        new Block(
                                new StatementList(
                                        new ArrayList<>() {{
                                            add(new Assign(
                                                    new Identifier("x"), new IntegerLiteral(5))
                                            );
                                        }}
                                )
                        )
                )

        );
    }

    @DisplayName("Should parse a Statement")
    @ParameterizedTest
    @MethodSource
    void shouldParseAStatement(String input, Statement expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input, 2);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = antlrParser.getProgram(stream);
        // ASSERT
        test.ifPresent(program -> {
            var statement = program.getMainClass().getStatements().getStatements().getFirst();
            assertEquals(expected, statement);
        });
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
        var stream = getInputStream(input, 4);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = assertDoesNotThrow(() -> antlrParser.getProgram(stream));

        // ASSERT
        test.ifPresent(program -> {
            var current = program.getClasses().getClassDecls().getFirst();
            assertEquals(expected, current);
        });
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
        var stream = getInputStream(input, 3);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = antlrParser.getProgram(stream);

        // ASSERT
        test.ifPresent(program -> {
            assertEquals(expected, program);
        });
    }

    static Stream<Arguments> shouldParseAMethodDeclaration() {
        return Stream.of(
                Arguments.of("public int x() { return 5; }",
                        ClassDeclSimple.builder()
                                .className(new Identifier("Teste"))
                                .fields(new VarDeclList())
                                .methods(new MethodDeclList(
                                        new ArrayList<>() {{
                                            add(
                                                    MethodDecl.builder()
                                                            .type(new IntegerType())
                                                            .identifier("x")
                                                            .formals(new FormalList())
                                                            .varDecls(new VarDeclList())
                                                            .statements(new StatementList())
                                                            .returnExpression(new IntegerLiteral(5))
                                                            .build()
                                            );
                                        }}
                                ))
                                .build()


                ),
                Arguments.of("public int x(int y, boolean z) { int x; return 5; }",
                        ClassDeclSimple.builder()
                                .className(new Identifier("Teste"))
                                .methods(new MethodDeclList(
                                        new ArrayList<>() {{
                                            add(
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
                                            );
                                        }}
                                ))
                                .build()
                ),
                Arguments.of("public int x(int y, boolean z) { int x; x = 5; return x; }",
                        ClassDeclSimple.builder()
                                .className(new Identifier("Teste"))
                                .methods(new MethodDeclList(
                                        new ArrayList<>() {{
                                            add(
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
                                            );
                                        }}
                                ))
                                .build()
                )
        );
    }

    @DisplayName("Should parse a Method Declaration")
    @ParameterizedTest
    @MethodSource
    void shouldParseAMethodDeclaration(String input, ClassDeclSimple expected) throws org.example.javacc.ParseException {
        // ARRANGE
        var stream = getInputStream(input, 5);

        // ACT
        AntlrParser antlrParser = new AntlrParser();
        var test = antlrParser.getProgram(stream);

        // ASSERT
        test.ifPresent(program -> {
            var current = program.getClasses().getClassDecls().getFirst();
            assertEquals(expected, current);
        });
    }

}
