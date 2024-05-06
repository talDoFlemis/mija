package org.example.visitor.jasmin

import jasmin.*
import jasmin.MethodDef.Signature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class JasminTest<T : Jasmin>(
    val example: String, val instance: T
) {
    @Test
    fun `Should convert to Jasmin`() {
        // Arrange
        val expected = example

        // Act
        val result = instance.toJasmin("")

        // Assert
        assertThat(result).isEqualToIgnoringWhitespace(expected)
    }
}

abstract class DirectivesTest<T : Directive>(
    example: String, instance: T
) : JasminTest<T>(example, instance)

data object FieldDefTest : DirectivesTest<FieldDef>(
    FieldDef.example(), FieldDef(
        name = "PI",
        accessSpec = AccessSpec(
            visibility = Visibility.PUBLIC,
            access = Access.FINAL
        ),
        type = "F",
        value = "3.14"
    )
)

data object MethodDefTest : DirectivesTest<MethodDef>(
    MethodDef.example(
        mapOf(
            "block-print" to "10"
        )
    ), MethodDef(
        signature = Signature(
            name = "main",
            accessSpec = AccessSpec(
                visibility = Visibility.PUBLIC,
                access = Access.STATIC
            ),
            returnType = "V",
            args = listOf("Ljava/lang/String;")
        ),
        stackLimit = 2,
        instructions = listOf(
            Print(10)
        )
    )
)

data object ClassDefTest : DirectivesTest<ClassDef>(
    ClassDef.example(
        mapOf(
            "block-print" to "10",
            "field-value" to "3.14159"
        ), ""
    ), ClassDef(
        name = "MyClass",
        accessSpec = AccessSpec(
            visibility = Visibility.PUBLIC,
            access = Access.NONE
        ),
        superClass = arrayListOf(ClassDef.Super("java/lang/Object")),
        fields = arrayListOf(FieldDefTest.instance.copy(value = "3.14159")),
        methods = arrayListOf(MethodDefTest.instance)
    )
)

