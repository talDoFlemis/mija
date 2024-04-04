package org.example.ast2.org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.Formal
import org.example.ast.FormalList
import org.example.ast.Identifier
import org.example.ast.MethodDecl
import org.example.ast.MethodDeclList
import org.example.visitor.*

class FormalsListVisitor : SymbolVisitor<FormalList>() {
    override fun visit(entity: FormalList): Either<Error, Table> = either {
        FormalsVisitor().run {
            entity.formals.forEach {
                ensure(!table.contains(it.name)) {
                    Error("FormalsListVisitor: Formals must have unique names")
                }

                table += visit(it).bind()
            }

            this@FormalsListVisitor.table = table
        }

        table
    }
}

class FormalsVisitor : SymbolVisitor<Formal>() {
    override fun visit(entity: Formal): Either<Error, Table> = either {
        ensure(!table.contains(entity.name)) {
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

class MethodDeclListVisitor : SymbolVisitor<MethodDeclList>() {
    override fun visit(entity: MethodDeclList): Either<Error, Table> = either {
        MethodDeclVisitor().run {
            entity.methodDecls.forEach {
                ensure(!table.contains(it.identifier)) {
                    Error("MethodDeclListVisitor: MethodDecl must have a unique name")
                }

                table += visit(it).bind()
            }
        }

        table
    }
}

class MethodDeclVisitor : SymbolVisitor<MethodDecl>() {
    override fun visit(entity: MethodDecl): Either<Error, Table> = either {
        ensure(!table.contains(entity.identifier)) {
            Error("MethodDeclVisitor: MethodDecl must have a unique name")
        }

        Table(
            MethodData(
                name = entity.identifier,
                args = FormalsListVisitor()
                    .visit(entity.formals)
                    .bind(),
                locals = VarDeclListVisitor()
                    .visit(entity.varDecls)
                    .bind()
            )
        )
    }
}