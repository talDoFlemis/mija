package org.example.ast2.org.example.visitor

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import org.example.ast.Identifier
import org.example.ast.VarDecl
import org.example.visitor.*

class VarDeclListVisitor(override var table: Table) : SymbolVisitor<List<VarDecl>> {
    override fun visit(entity: List<VarDecl>): Either<Error, Table> = either {
        VarDeclVisitor(table).run {
            entity.forEach {
                ensure(!table.contains(Identifier(it.name))) {
                    Error("VarDeclListVisitor: VarDecl must have a unique name")
                }

                table += visit(it).bind()
            }

            this@VarDeclListVisitor.table = table
        }

        table
    }
}

class VarDeclVisitor(override var table: Table) : SymbolVisitor<VarDecl> {
    override fun visit(entity: VarDecl): Either<Error, Table> = either {
        ensure(!table.contains(Identifier(entity.name))) {
            Error("VarDeclVisitor: VarDecl must have a unique name")
        }

        Table(FieldData(Identifier(entity.name), entity.type))
    }
}