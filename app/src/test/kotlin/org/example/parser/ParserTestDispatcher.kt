package org.example.parser

import kotlinx.coroutines.runBlocking
import org.example.programs.Programs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

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
}