package org.example.parser

import kotlinx.coroutines.runBlocking
import org.example.mija.ParserStrategy
import org.example.programs.Programs
import org.example.visitor.symbols.SymbolTableVisitor
import org.example.visitor.types.TypeCheckingVisitor
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

interface ParserTestDispatcher<Parser: ParserStrategy, Program: Programs.IProgram> {
    val program: Program
    val parser: Parser

    suspend fun useParser(block: suspend Parser.() -> Unit): Unit = parser.block()

    @Test
    fun `Should check the syntax return ok`() =
        runBlocking {
            useParser {
                // Arrange
                val code = program.inputStream

                // Act
                val result = isSyntaxOk(code)

                // Assert
                assert(result)
            }
        }

    @Test
    fun `Should check the semantic`() =
            runBlocking {
                useParser {
                    // Arrange
                    val stream = program.inputStream
                    val result = getProgram(stream)
                    val program = result.get()
                    val symbolVisitor = SymbolTableVisitor()


                    // Act
                    program.accept(symbolVisitor)
                    val typeVisitor = TypeCheckingVisitor(symbolVisitor.mainTable)
                    program.accept(typeVisitor)

                    // Assert
                    assertFalse(result.isEmpty)
                    assertTrue(symbolVisitor.errors.isEmpty())
                    assertTrue(typeVisitor.errors.isEmpty())
                }
            }
}