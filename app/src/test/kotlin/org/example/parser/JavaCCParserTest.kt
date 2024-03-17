package org.example.parser

import org.example.factories.ProgramBuilder
import org.example.factories.TokenFactory
import org.junit.jupiter.api.Test

class JavaCCParserTest {
    companion object {
        val parser = JavaCCParser()
    }

    val program = with(ProgramBuilder()) {
        classDeclaration("Factorial") {
            methodDeclaration("main", "static void", "String[] a") {
                statement("System.out.println(7)")
            }
        }
    }

    @Test
    fun `Should check the syntax return ok`() {
        with(JavaCCContext) {
            // Arrange
            val code = """
                | class Factorial{
                |        public static void main(String[] a){
                |            System.out.println(7);
                |        } 
                |}
                """.trimMargin()

            // Act
            val result = parser.checkSyntax(code)

            // Assert
            assert(result)
        }
    }

    @Test
    fun `Should check the syntax return error`() {
        with(JavaCCContext) {
            // Arrange
            val code = program.build()
                .trimMargin()
                .also { println(it) }

            // Act
            val result = parser.checkSyntax(code)

            // Assert
            assert(!result)
        }
    }
}