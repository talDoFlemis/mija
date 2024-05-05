package bytecode

import org.example.ast.*
import org.example.visitor.bytecode.CodeGen
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JasminVisitorTest {

    @Test
    fun visit() {
        // Arrange
        val mainClassNode = MainClass.builder()
            .className(Identifier("Main"))
            .argsName(Identifier("args"))
            .statements(
                StatementList(
                    arrayListOf(
                        Assign.builder()
                            .value(IntegerLiteral(0))
                            .identifier(Identifier("field1"))
                            .build()
                    )
                )
            )
            .build()
        val expected = CodeGen.BaseInstance.program {
            classDecl(isMain = true) {
                name = "Main"
                path = "java/lang/Object"

                methodDecl {
                    name = "main"
                    descriptor = "([Ljava/lang/String;)V"
                    visibility = "public"

                    statement {
                        type = "void"
                        name = "field1 = 0"
                    }
                }
            }
        }.render(StringBuilder()).toString()

        // Act
        val program = CodeGen.BaseInstance.program {
            name = "MiniJava Program"

            with(JasminVisitor) {
                visit(mainClassNode)
            }
        }.render(StringBuilder()).toString()

        // Assert
        Assertions.assertEquals(
            expected.trim(),
            program.trim()
        )
    }


}