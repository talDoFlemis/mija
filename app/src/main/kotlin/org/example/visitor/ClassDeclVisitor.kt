package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.ClassDeclSimple
import org.example.ast2.org.example.visitor.MethodDeclListVisitor

class ClassDeclVisitor : SymbolVisitor<ClassDeclSimple>() {
    override fun visit(entity: ClassDeclSimple): Either<Error, Table> = either {
        ensure(!table.contains(entity.className.s)) {
            Error("ClassDeclVisitor: ClassDecl must have a unique name")
        }

        table + Table(
            ClassData(
                name = entity.className.s,
                fields = VarDeclListVisitor()
                    .visit(entity.fields)
                    .bind(),
                methods = MethodDeclListVisitor()
                    .visit(entity.methods)
                    .bind()
            )
        )
    }
}