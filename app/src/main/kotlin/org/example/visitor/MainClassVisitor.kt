package org.example.ast2.org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.Identifier
import org.example.ast.IdentifierType
import org.example.ast.MainClass
import org.example.visitor.*

class MainClassVisitor(override var table: Table) : SymbolVisitor<MainClass> {
    override fun visit(entity: MainClass): Either<Error, Table> = either {
        ensure(!table.contains(entity.className)) {
            Error("MainClassVisitor: MainClass must have a unique name")
        }

        Table(
            ClassData(
                name = entity.className,
                fields = Table(),
                methods = Table(
                    MethodData(
                        name = Identifier("main"),
                        args = Table(
                            ParamData(
                                name = Identifier("args"),
                                type = IdentifierType("String[]")
                            )
                        ),
                        locals = Table()
                    )
                )
            )
        )
    }
}