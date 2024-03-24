package org.example.parser;

import kotlin.Pair;
import org.example.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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

    @DisplayName("Should parse a type")
    @ParameterizedTest
    @CsvSource({
            "int, org.example.ast.IntegerType",
            "boolean, org.example.ast.BooleanType",
            "int[], org.example.ast.IntArrayType",
            "gipity, org.example.ast.IdentifierType"
    })
    void shouldParseAType(String input, String expectedClassName) throws org.example.javacc.ParseException, ClassNotFoundException {
        // ARRANGE
        var stream = getInputStream(input);

        // ACT
        var type = parser.getType(stream);

        // ASSERT
        assertEquals(Class.forName(expectedClassName), type.getClass());
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
                Arguments.of("x", new Identifier("x")),
                Arguments.of("x[5]", new ArrayLookup(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("x.length", new ArrayLength(new Identifier("x"))),
                Arguments.of("!x", new Not(new Identifier("x"))),
                Arguments.of("x + 5", new Plus(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("x - 5", new Minus(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("x * 5", new Times(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("x < 5", new LessThan(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("(x) && y", new And(new Identifier("x"), new Identifier("y"))),
                Arguments.of("x[y]", new ArrayLookup(new Identifier("x"), new Identifier("y"))),
                Arguments.of("new int[5]", new NewArray(new IntegerLiteral(5))),
                Arguments.of("new gipity()", new NewObject(new Identifier("gipity"))),
                Arguments.of("(x + 5)", new Plus(new Identifier("x"), new IntegerLiteral(5))),
                Arguments.of("this", new This()),
                Arguments.of("x.y()", new Call(new Identifier("x"), new Identifier("y"), new ExpressionList())),
                Arguments.of("new Fac().ComputeFac()", new Call(new NewObject(new Identifier("Fac")), new Identifier("ComputeFac"), new ExpressionList())),
                Arguments.of("!current_node.GetHas_Right() && !current_node.GetHas_Left()", new And(new Not(new Call(new Identifier("current_node"), new Identifier("GetHas_Right"), new ExpressionList())), new Not(new Call(new Identifier("current_node"), new Identifier("GetHas_Left"), new ExpressionList()))))
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
                                                .lhe(new Identifier("a"))
                                                .rhe(Times.builder()
                                                        .lhe(new Identifier("b"))
                                                        .rhe(new Identifier("c"))
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .rhe(Times.builder()
                                                .lhe(new Identifier("d"))
                                                .rhe(new Identifier("e"))
                                                .build()
                                        )
                                        .build()
                                )
                                .rhe(new Identifier("f"))
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

//    @Test
//    @DisplayName("Should parse a Main Class")
//    void shouldParseAMainClass() {
//        // ARRANGE
//        String input = """
//                class Tubias {
//                    public static void gepeto(String[] args) {
//                        System.out.println("Hello, World!");
//                    }
//                }
//                    """;
//        var stream = getInputStream(input);
//
//        // ACT
//        var option = parser.getProgram(stream);
//        var kkk  = Program.builder();
//
//        // ASSERT
//        var program = assertDoesNotThrow(option::get);
//        var mainClass = program.getMainClass();
//        assertEquals(new Identifier("Main"), mainClass.getClassName());
//        assertEquals(new Identifier("gepeto"), mainClass.getMainMethodName());
//    }
}
