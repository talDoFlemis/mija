package org.example.visitor;

import arrow.core.*
import org.example.ast.*
import org.example.ast2.org.example.visitor.FormalsListVisitor
import org.example.ast2.org.example.visitor.FormalsVisitor
import org.example.ast2.org.example.visitor.MethodDeclVisitor

interface ASTVisitor : Visitor<Unit>

@JvmInline
value class Id(val name: String) {
    constructor(name: Identifier) : this(name.s)
}

sealed interface Scope {
    val name: String
}

data class ClassData(
    override val name: String,
    val fields: Table,
    val methods: Table
) : Scope

data class ParamData(
    override val name: String,
    val type: String
) : Scope

data class MethodData(
    override val name: String,
    val args: Table,
    val locals: Table,
    val returnType: String? = null,
) : Scope

data class FormalData(
    override val name: String,
    val type: String
) : Scope

data class Table(private val map: Map<String, Scope>) {
    constructor(
        vararg pairs: Scope
    ) : this(listOf(*pairs).associateBy { it.name })

    constructor(
        list: ArrayList<Scope>
    ) : this(list.associateBy { it.name })

    operator fun plus(table: Table): Table = Table(map + table.map)
    operator fun plus(pair: Scope): Table = Table(map + (pair.name to pair))
    operator fun get(key: String): Scope? = map[key]
    fun contains(key: String): Boolean = map.containsKey(key)

    override fun toString(): String = map.entries
        .joinToString(prefix = "{ ", separator = ", ", postfix = " }") { (k, v) -> "$k -> $v" }
}

@JvmInline
value class Error(val message: String)

interface SymbolVisitor<T> {
    companion object {
        fun <T> dispatch(entity: T, table: Table = Table()): Either<Error, Table> =
            when (entity) {
                is MainClass -> MainClassVisitor.run { table.visit(entity) }
                is ClassDecl -> ClassDeclVisitor.run { table.visit(entity) }
                is VarDecl -> VarDeclVisitor.run { table.visit(entity) }
                is MethodDecl -> MethodDeclVisitor.run { table.visit(entity) }
                is FormalList -> FormalsListVisitor.run { table.visit(entity) }
                is Formal -> FormalsVisitor.run { table.visit(entity) }
                is Program -> ProgramVisitor.run { table.visit(entity) }
                else -> throw IllegalArgumentException("SymbolVisitor: Unknown entity type")
            }
    }

    fun fold(entityList: List<T>, f: (T) -> Either<Error, Table>): Either<Error, Table> =
        entityList.fold(Table().right()) {
            acc: Either<Error, Table>, entity -> f(entity)
                .flatMap { table -> acc.map { it + table } }
        }

    fun Table.visit(entity: T): Either<Error, Table>
}


