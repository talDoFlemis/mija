package org.example.visitor

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.assertj.core.api.Assertions
import org.example.ast.Identifier
import org.example.ast.MainClass
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MainClassVisitorTest {
    fun tearDown(): Unit {
        unmockkObject(MainClassVisitor)
    }

    @Test
    fun `should visit main class`(): Unit = MainClassVisitor.run {
        // Arrange
        val table = Table()
        val mainClass = MainClass.builder()
            .className(Identifier.builder().s("Main").build())
            .argsName(Identifier.builder().s("args").build())
            .build()
        val expectedTable = Companion.defaultMainTable

        // Act
        val result = table.visit(mainClass)

        // Assert
        result.fold(
            ifLeft = { Assertions.fail("Should not throw an error") },
            ifRight = { Assertions.assertThat(it).isEqualTo(expectedTable) }
        )
    }

    @Test
    fun `Should visit main class with error`(): Unit = MainClassVisitor.run {
        // Arrange
        val table = Table()
        val mainClass = MainClass.builder()
            .className(Identifier.builder().s("Main").build())
            .argsName(Identifier.builder().s("wrong").build())
            .build()

        // Act
        val result = table.visit(mainClass)

        // Assert
        result.fold(
            ifLeft = { Assertions.assertThat(it.message).isEqualTo("MainClassVisitor: MainClass args must be named 'args'") },
            ifRight = { Assertions.fail("Should throw an error") }
        )
    }

    companion object {
        val defaultClassData =
            ClassData(
                name = "Main",
                fields = Table(),
                methods = Table(
                    MethodData(
                        name = "Main",
                        args = Table(
                            ParamData(
                                name = "args",
                                type = "String[]"
                            )
                        ),
                        locals = Table(),
                        returnType = null
                    )
                )
            )
        val defaultMainTable = Table(
        Companion.defaultClassData
    )
    }
}