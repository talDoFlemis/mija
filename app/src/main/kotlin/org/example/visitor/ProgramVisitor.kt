package org.example.visitor

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import org.example.ast.Program
import org.example.visitor.SymbolVisitor.Companion.dispatch

object ProgramVisitor : SymbolVisitor<Program> {
    override fun Table.visit(entity: Program): Either<Error, Table> = either {
        val mainClassTable = dispatch(entity.mainClass, this@visit).bind()

        val fullTable = dispatch(entity.classes, mainClassTable).bind()

        fullTable
    }
}