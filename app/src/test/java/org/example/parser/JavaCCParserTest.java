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
