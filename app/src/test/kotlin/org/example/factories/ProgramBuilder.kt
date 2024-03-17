package org.example.factories

class ProgramBuilder {
    private val program = StringBuilder()
    private var scopeCount = 0
    private fun ident() = " ".repeat(scopeCount * 4)

    fun classDeclaration(className: String, block: ProgramBuilder.() -> Unit): ProgramBuilder {
        program.append("class $className ")
        scope(block)
        return this
    }

    fun scope(block: ProgramBuilder.() -> Unit): ProgramBuilder {
        scopeCount += 1
        program.append("{\n" + ident())
        block()
        scopeCount -= 1
        program.append("\n" + ident() + "}")
        return this
    }

    fun statement(content: String): ProgramBuilder {
        program.append(content)
        program.append(";")
        return this
    }

    fun methodDeclaration(
        methodName: String,
        returnType: String,
        parameters: String,
        block: ProgramBuilder.() -> Unit
    ): ProgramBuilder {
        program.append("public $returnType $methodName($parameters) ")
        scope(block)
        return this
    }

    fun build(): String {
        return program.toString()
    }
}