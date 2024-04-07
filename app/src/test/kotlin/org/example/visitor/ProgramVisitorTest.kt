package org.example.visitor


import arrow.core.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.example.ast.*
import org.example.visitor.SymbolVisitor.Companion.dispatch
import org.junit.jupiter.api.Test

//class ClassDeclVisitorTest {
//    fun tearDown() {
//        unmockkAll()
//    }
//
//
//    @Test
//    fun `should visit class decl`(): Unit = ClassDeclVisitor.run {
//        // Arrange
//        val table = Table()
//        val classDecl = ClassDecl.builder()
//            .className(Identifier.builder().s("Main").build())
//            .varDecls(VarDeclList.builder().build())
//            .methodDecls(MethodDeclList.builder().build())
//            .build()
//        val expectedTable = Table(
//            ClassData(
//                name = "Main"
//            )
//        )
//
//        // Act
//        val result = table.visit(classDecl)
//
//        // Assert
//        assertThat(result).isEqualTo(expectedTable.right())
//    }
//
//    @Test
//    fun `should visit class decl with error`(): Unit = ClassDeclVisitor.run {
//        // Arrange
//        val table = Table()
//        val classDecl = ClassDecl.builder()
//            .className(Identifier.builder().s("Main").build())
//            .varDecls(VarDeclList.builder().build())
//            .methodDecls(MethodDeclList.builder().build())
//            .build()
//        val expectedTable = Table(
//            ClassData(
//                name = "Main"
//            )
//        )
//        val expectedError = Error("ClassDeclVisitor: ClassDecl must have a unique name")
//
//        // Act
//        val result = table.visit(classDecl)
//
//        // Assert
//        assertThat(result).isEqualTo(expectedTable.right())
//    }
//
//}

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