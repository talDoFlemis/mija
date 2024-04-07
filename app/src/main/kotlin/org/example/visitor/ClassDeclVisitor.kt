package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.*
import org.example.ast2.org.example.visitor.MethodDeclListVisitor
import org.example.visitor.SymbolVisitor.Companion.dispatch

object ClassDeclListVisitor : SymbolVisitor<ClassDeclList> {
    override fun Table.visit(entity: ClassDeclList): Either<Error, Table> =
        ClassDeclVisitor.fold(
            entity.classDecls.toList()
        ) { dispatch(it) }
}

object ClassDeclVisitor : SymbolVisitor<ClassDecl> {
    private fun extractName(entity: ClassDecl): String =
        when (entity) {
            is ClassDeclSimple -> entity.className.s
            is ClassDeclExtends -> entity.className.s
            else -> throw IllegalArgumentException(
                "ClassDeclVisitor: ClassDecl must be either ClassDeclSimple or ClassDeclExtends"
            )
        }

    object ClassDeclSimpleVisitor : SymbolVisitor<ClassDeclSimple> {
        override fun Table.visit(entity: ClassDeclSimple): Either<Error, Table> = either {
            this@visit + Table(
                ClassData(
                    name = extractName(entity),
                    fields = dispatch(entity.fields).bind(),
                    methods = dispatch(entity.methods).bind()
                )
            )
        }
    }

    object ClassDeclExtendsVisitor : SymbolVisitor<ClassDeclExtends> {
        override fun Table.visit(entity: ClassDeclExtends): Either<Error, Table> = either {
            this@visit + Table(
                ClassData(
                    name = extractName(entity),
                    fields = dispatch(entity.fields).bind(),
                    methods = dispatch(entity.methods).bind()
                )
            )
        }
    }

    override fun Table.visit(entity: ClassDecl): Either<Error, Table> = either {
        val name = extractName(entity)

        ensure(!this@visit.contains(name)) {
            Error("ClassDeclVisitor: ClassDecl must have a unique name")
        }

        val newTable = dispatch(entity, table = this@visit).bind()

        this@visit + newTable
    }
}