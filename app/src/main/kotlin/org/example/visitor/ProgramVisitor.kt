package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import org.example.ast.Program

object ProgramVisitor : SymbolVisitor<Program>() {
    override fun Table.visit(entity: Program): Either<Error, Table> = either {
        val mainClassTable = with(MainClassVisitor) {
            visit(entity.mainClass)
        }.bind()
        val classDeclListTable = with(ClassDeclListVisitor) {
            visit(entity.classes)
        }.bind()

        this@visit + mainClassTable + classDeclListTable
    }
}