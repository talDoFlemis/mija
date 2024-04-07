package org.example.visitor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.example.ast.MainClass

class MainClassVisitor : SymbolVisitor<MainClass>() {
    override fun visit(entity: MainClass): Either<Error, Table> = either {
        ensure(!table.contains(entity.className.s)) {
            Error("MainClassVisitor: MainClass must have a unique name")
        }

        Table(
            ClassData(
                name = entity.className.s,
                fields = Table(),
                methods = Table(
                    MethodData(
                        name = entity.className.s,
                        args = Table(
                            ParamData(
                                name = entity.argsName.s,
                                type = "String[]"
                            )
                        ),
                        locals = Table()
                    )
                )
            )
        )
    }
}