package org.example.ast2.org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.ClassDeclSimple
import org.example.visitor.*

class ClassDeclVisitor(
    override var table: Table
) : SymbolVisitor<ClassDeclSimple> {
    override fun visit(entity: ClassDeclSimple): Either<Error, Table> = either {
        ensure(!table.contains(entity.className)) {
            Error("ClassDeclVisitor: ClassDecl must have a unique name")
        }

        table + Table(
            ClassData(
                name = entity.className,
                fields = VarDeclListVisitor(Table())
                    .visit(entity.fields.varDecls)
                    .bind(),
                methods = Table(

                )
            )
        )
    }
}