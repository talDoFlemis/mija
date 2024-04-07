package org.example.ast2.org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.Formal
import org.example.ast.FormalList
import org.example.ast.MethodDecl
import org.example.ast.MethodDeclList
import org.example.visitor.*
import org.example.visitor.SymbolVisitor.Companion.dispatch


object FormalsListVisitor : SymbolVisitor<FormalList> {
    override fun Table.visit(entity: FormalList): Either<Error, Table> =
        with(FormalsVisitor) {
            fold(
                entity.formals.toList(),
                ::dispatch
            )
        }
}

object FormalsVisitor : SymbolVisitor<Formal> {
    override fun Table.visit(entity: Formal): Either<Error, Table> = either {
        ensure(!contains(entity.name)) {
            Error("FormalsVisitor: Formals must have unique names")
        }

        Table(
            FormalData(
                name = entity.name,
                type = entity.type.toString()
            )
        )
    }
}

object MethodDeclListVisitor : SymbolVisitor<MethodDeclList> {
    override fun Table.visit(entity: MethodDeclList): Either<Error, Table> =
        with(MethodDeclVisitor) {
            fold(
                entity.methodDecls.toList(),
                ::dispatch
            )
        }
}

object MethodDeclVisitor : SymbolVisitor<MethodDecl> {
    override fun Table.visit(entity: MethodDecl): Either<Error, Table> = either {
        ensure(!contains(entity.identifier)) {
            Error("MethodDeclVisitor: MethodDecl must have a unique name")
        }

        Table(
            MethodData(
                name = entity.identifier,
                args = dispatch(entity.formals).bind(),
                locals = dispatch(entity.varDecls).bind()
            )
        )
    }
}