package org.example.parser

import java.io.ByteArrayInputStream
import java.io.InputStream

interface ParserScope<T> {
    fun T.checkSyntax(code: String): Boolean

    val name : String

    fun asStream(str: String): InputStream = str
        .toByteArray()
        .let(::ByteArrayInputStream)
}

object JavaCCContext : ParserScope<JavaCCParser> {
    override val name = "JavaCC"

    override fun JavaCCParser.checkSyntax(code: String): Boolean = code
        .let(::asStream)
        .let(::isSyntaxOk)
}

object AntlrContext : ParserScope<AntlrParser> {
    override val name = "Antlr"

    override fun AntlrParser.checkSyntax(code: String): Boolean = code
        .let(::asStream)
        .let(::isSyntaxOk)
}