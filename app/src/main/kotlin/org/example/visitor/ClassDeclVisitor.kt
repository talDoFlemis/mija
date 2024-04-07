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
        ) { visit(entity) }
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
                    fields = dispatch(entity.fields, table = this@visit).bind(),
                    methods = with(MethodDeclListVisitor) {
                        visit(entity.methods).bind()
                    }
                )
            )
        }
    }

    object ClassDeclExtendsVisitor : SymbolVisitor<ClassDeclExtends> {
        override fun Table.visit(entity: ClassDeclExtends): Either<Error, Table> = either {
            this@visit + Table(
                ClassData(
                    name = extractName(entity),
                    fields = with(VarDeclListVisitor) {
                        visit(entity.fields).bind()
                    },
                    methods = with(MethodDeclListVisitor) {
                        visit(entity.methods).bind()
                    }
                )
            )
        }
    }

    override fun Table.visit(entity: ClassDecl): Either<Error, Table> = either {
        val name = extractName(entity)

        ensure(!this@visit.contains(name)) {
            Error("ClassDeclVisitor: ClassDecl must have a unique name")
        }

        val newTable = when (entity) {
            is ClassDeclSimple ->
                with(ClassDeclSimpleVisitor) {
                    visit(entity).bind()
                }
            is ClassDeclExtends ->
                with(ClassDeclExtendsVisitor) {
                    visit(entity).bind()
                }
            else -> throw IllegalArgumentException(
                "ClassDeclVisitor: ClassDecl must be either ClassDeclSimple or ClassDeclExtends"
            )
        }

        this@visit + newTable
    }
}