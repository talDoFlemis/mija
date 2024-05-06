package org.example.visitor.jasmin

import jasmin.IfJasmin
import jasmin.Instruction
import org.example.ast.*

abstract class InstructionTest<T : Instruction<*>>(
    example: String, instance: T
) : JasminTest<T>(example, instance)

data object IfTest : InstructionTest<IfJasmin>(
    IfJasmin.example(
        mapOf(
            "block-print" to "10",
            "block-label" to "L1"
        )
    ),
    IfJasmin(
        If.builder()
            .condition(IntegerLiteral(1))
            .thenBranch(
                Block(
                    StatementList(
                        arrayListOf(Sout(IntegerLiteral(1)))
                    )
                )
            )
            .elseBranch(
                Block(
                    StatementList(
                        arrayListOf(Sout(IntegerLiteral(2)))
                    )
                )
            ).build()
    )
)