package org.example.visitor


import arrow.core.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.example.ast.*
import org.example.visitor.SymbolVisitor.Companion.dispatch
import org.junit.jupiter.api.Test

class ProgramVisitorTest {
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should visit program`(): Unit = ProgramVisitor.run {
        // Arrange
        val expectedMainTable = MainClassVisitorTest.defaultMainTable
        val expectedClassTable = expectedMainTable + Table(
            MainClassVisitorTest.defaultClassData.copy(
                name = "otherClass"
            )
        )
        val expectedFullTable = expectedClassTable + Table(
            MainClassVisitorTest.defaultClassData,
            MainClassVisitorTest.defaultClassData.copy(
                name = "otherClass"
            )
        )
        val table = Table()
        val program = slot<Program>()
        mockkObject(SymbolVisitor.Companion) {
            every { dispatch(any(MainClass::class), table) } returns expectedMainTable.right()
            every { dispatch(any(ClassDeclList::class), table) } returns expectedClassTable.right()

            // Act
            val result = dispatch(program, table)

            // Assert
            assertThat(result).isEqualTo(expectedFullTable.right())
        }
    }

    @Test
    fun `should visit program with error`(): Unit = ProgramVisitor.run {
        // Arrange
        val table = Table()
        val program = slot<Program>()
        mockkObject(SymbolVisitor.Companion) {
            every { dispatch(any(MainClass::class), table) } returns Error("MainClassVisitor: MainClass must have a unique name").left()

            // Act
            val result = dispatch(program, table)

            // Assert
            assertThat(result).isEqualTo(Error("MainClassVisitor: MainClass must have a unique name").left())
        }
    }

    @Test
    fun `should visit program with error in class decl list`(): Unit = ProgramVisitor.run {
        // Arrange
        val table = Table()
        val expectedMainTable = MainClassVisitorTest.defaultMainTable
        val program = slot<Program>()
        mockkObject(SymbolVisitor.Companion) {
            every { dispatch(any(MainClass::class), table) } returns expectedMainTable.right()
            every { dispatch(any(ClassDeclList::class), table) } returns Error("ClassDeclVisitor: ClassDecl must have a unique name").left()

            // Act
            val result = dispatch(program, table)

            // Assert
            assertThat(result).isEqualTo(Error("ClassDeclVisitor: ClassDecl must have a unique name").left())
        }
    }
}