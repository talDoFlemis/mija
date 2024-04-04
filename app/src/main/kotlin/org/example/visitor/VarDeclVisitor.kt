package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.Identifier
import org.example.ast.VarDecl
import org.example.ast.VarDeclList

class VarDeclListVisitor : SymbolVisitor<VarDeclList>() {
    override fun visit(entity: VarDeclList): Either<Error, Table> = either {
        VarDeclVisitor().run {
            entity.varDecls.forEach {
                ensure(!table.contains(it.name)) {
                    Error("VarDeclListVisitor: VarDecl must have a unique name")
                }

                table += visit(it).bind()
            }

            this@VarDeclListVisitor.table = table
        }

        table
    }
}

class VarDeclVisitor : SymbolVisitor<VarDecl>() {
    override fun visit(entity: VarDecl): Either<Error, Table> = either {
        ensure(!table.contains(entity.name)) {
            Error("VarDeclVisitor: VarDecl must have a unique name")
        }

        table + FormalData(
            name = entity.name,
            type = entity.type.toString()
        )
    }
}