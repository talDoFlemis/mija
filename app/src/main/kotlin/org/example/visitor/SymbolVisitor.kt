package org.example.visitor;

import arrow.core.*
import org.example.ast.*

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

abstract class SymbolVisitor<T> {
    var table : Table = Table()
    abstract fun visit(entity: T): Either<Error, Table>
}

