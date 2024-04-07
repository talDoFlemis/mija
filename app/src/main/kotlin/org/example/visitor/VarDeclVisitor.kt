package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.VarDecl
import org.example.ast.VarDeclList

object VarDeclListVisitor : SymbolVisitor<VarDeclList> {
    override fun Table.visit(entity: VarDeclList): Either<Error, Table> =
        with(VarDeclVisitor) {
            fold(
                entity.varDecls.toList()
            ) { visit(it) }
        }
}

object VarDeclVisitor : SymbolVisitor<VarDecl> {
    override fun Table.visit(entity: VarDecl): Either<Error, Table> = either {
        ensure(!contains(entity.name)) {
            Error("VarDeclVisitor: VarDecl must have a unique name")
        }

         this@visit + FormalData(
            name = entity.name,
            type = entity.type
        )
    }
}