package org.example.visitor

import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.right
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.fail
import org.example.ast.*
import org.example.visitor.SymbolVisitor.Companion.dispatch
import org.junit.jupiter.api.Test

class ClassDeclExtensionTest {
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should visit class decl`(): Unit = ClassDeclVisitor.run {
        // Arrange
        val table = Table()
        val varDeclList = VarDeclList(
            ArrayList(
                listOf(
                    VarDecl(IntegerType(), "field1"),
                    VarDecl(IntegerType(), "field2")
                )
            )
        )
        val statementList = StatementList(
            ArrayList(
                listOf(
                    Assign(
                        Identifier("local1"),
                        IntegerLiteral(1)
                    )
                )
            )
        )
        val expression = And(True(), False())
        val formalList = FormalList(
            ArrayList(
                listOf(
                    Formal(IntegerType(), "arg1")
                )
            )
        )
        val methodDeclList = MethodDeclList(
            ArrayList(
                listOf(
                    MethodDecl(
                        IntegerType(),
                        "method1",
                        formalList,
                        varDeclList,
                        statementList,
                        expression
                    )
                )
            )
        )
        val superClass = ClassDeclSimple(
            Identifier("Super"),
            varDeclList,
            methodDeclList
        )
        val classDecl = ClassDeclExtends(
            Identifier("Main"),
            Identifier("Super"),
            varDeclList,
            methodDeclList
        )
        val expectedClass = ClassData(
            name = "Main",
            fields = dispatch(varDeclList).getOrElse { fail("Should not fail") },
            methods = dispatch(methodDeclList).getOrElse { fail("Should not fail") }
        )
        val expectedSuperClass = ClassData(
            name = "Super",
            fields = dispatch(varDeclList).getOrElse { fail("Should not fail") },
            methods = dispatch(methodDeclList).getOrElse { fail("Should not fail") }
        )
        val expectedTable = Table(
            expectedClass,
            expectedSuperClass
        )

        // Act
        val result = dispatch(superClass, table = table)
            .flatMap { dispatch(classDecl, table = it) }

        // Assert
        Assertions.assertThat(result).isEqualTo(expectedTable.right())
    }
}