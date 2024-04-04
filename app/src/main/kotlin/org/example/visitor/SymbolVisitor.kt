package org.example.visitor;

import arrow.core.*
import org.example.ast.*

interface ASTVisitor : Visitor<Unit>

sealed interface Scope {
    val name: Identifier
}

data class ClassData(
    override val name: Identifier,
    val fields: Table,
    val methods: Table
) : Scope

data class MethodData(
    override val name: Identifier,
    val args: Table,
    val locals: Table,
) : Scope

data class ParamData(
    override val name: Identifier,
    val type: Type
) : Scope

data class FieldData(
    override val name: Identifier,
    val type: Type
) : Scope

data class Table(private val map: Map<Identifier, Scope>) {
    constructor(
        vararg pairs: Scope
    ) : this(listOf(*pairs).associateBy { it.name })

    constructor(
        list: ArrayList<Scope>
    ) : this(list.associateBy { it.name })

    operator fun plus(table: Table): Table = Table(map + table.map)
    operator fun plus(pair: Scope): Table = Table(map + (pair.name to pair))
    operator fun get(key: Identifier): Scope? = map[key]
    fun contains(key: Identifier): Boolean = map.containsKey(key)

    override fun toString(): String = map.entries
        .joinToString(prefix = "{ ", separator = ", ", postfix = " }") { (k, v) -> "$k -> $v" }
}

@JvmInline
value class Error(val message: String)

interface SymbolVisitor<T> {
    var table : Table
    fun visit(entity: T): Either<Error, Table>
}

