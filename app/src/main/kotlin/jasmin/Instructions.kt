package jasmin

import org.example.ast.*

interface Instruction<T : Node> : Jasmin {
    val statement: T
    fun T.toJasmin(ident: String): String

    override fun toJasmin(ident: String): String = statement.toJasmin(ident)

}

data class Eval(
    override val statement: Expression
) : Instruction<Expression> {
    override fun Expression.toJasmin(ident: String): String =
        when (this) {
            is IntegerLiteral -> IntegerValue(this).toJasmin(ident)
            else -> throw IllegalArgumentException("Unsupported expression: ${this::class.simpleName}")
        }
}

data class Print(
    override val statement: Sout
) : Instruction<Sout> {
    constructor(value: Int) : this(
        Sout(
            IntegerLiteral(value)
        )
    )

    override fun Sout.toJasmin(ident: String): String = """|
        |${ident}getstatic java/lang/System/out Ljava/io/PrintStream
        |${Eval(expression).toJasmin("$ident\t")}
        |${ident}invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V"""
        .trimMargin("|")
        .trim()
}

data class IntegerValue(
    override val statement: IntegerLiteral
) : Instruction<IntegerLiteral> {
    override fun IntegerLiteral.toJasmin(ident: String): String = "ldc $value"
}

data class IfJasmin(
    override val statement: If,
) : Instruction<If> {

    companion object : Utils {
        override fun example(inject: Map<String, String>, exIndent: String) =
            """
            |${exIndent}ldc 1
            |${exIndent}ifne L1
            |${exIndent}goto L2
            |${Label.example(mapOf("block-label-name" to "L1", "block-print" to "1"), exIndent)}
            |${Label.example(mapOf("block-label-name" to "L2", "block-print" to "2"), exIndent)}
            """
                .trimMargin("|")
                .trim()
    }

    override fun If.toJasmin(ident: String): String =
        """
        |${Eval(this.condition).toJasmin(ident)}
        |${ident}ifne L1
        |${ident}goto L2
        |${Label("L1", thenBranch).toJasmin("\t$ident")}
        ${Label("L2", elseBranch).toJasmin("\t$ident")}
        """
            .trimMargin("|")
            .trim()
}

data class Label(
    val label: String,
    val instructions: Statement
) : Jasmin {
    companion object : Utils {
        override fun example(inject: Map<String, String>, exIndent: String) =
            """
            |${exIndent}${inject["block-label-name"] ?: "L1"}:
            |${exIndent}getstatic java/lang/System/out Ljava/io/PrintStream
            |${exIndent}ldc ${inject["block-print"] ?: "10"}
            |${exIndent}invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
            """
                .trimMargin("|")
                .trim()
    }

    fun Statement.toJasmin(ident: String): String = when (this) {
        is Block -> this.statements.statements.joinToString("\n") {
            it.toJasmin(ident)
        }

        is If -> IfJasmin(this).toJasmin(ident)
        is Sout -> Print(this).toJasmin(ident)
        else -> throw IllegalArgumentException("Unsupported statement: ${this::class.simpleName}")
    }

    override fun toJasmin(ident: String): String {
        return "$label:\n${instructions.toJasmin("\t$ident")}"
    }
}