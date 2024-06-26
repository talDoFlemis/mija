package jasmin


enum class Visibility(val kind: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected")
}

enum class Access(val kind: String) {
    STATIC("static"),
    FINAL("final"),
    NONE("")
}

data class AccessSpec(val visibility: Visibility, val access: Access) {
    val kind: String = "${visibility.kind} ${access.kind}"
}

interface Utils {
    fun example(inject: Map<String, String> = mapOf(), exIndent: String = ""): String
}

fun interface Jasmin {

    fun toJasmin(ident: String): String
}

sealed interface Directive : Jasmin

fun interface Scope<T : Directive> {
    fun index(directive: List<T>): Map<String, T>

    fun List<T>.toJasmin(ident: String): String =
        index(this).values.joinToString("\n") { it.toJasmin(ident) }
}

data class ClassDef(
    val name: String,
    val accessSpec: AccessSpec,
    val methods: ArrayList<MethodDef>,
    val fields: ArrayList<FieldDef>,
    val superClass: ArrayList<Super> = arrayListOf()
) : Directive, Utils by Companion {


    companion object : Utils {

        val methodsScope = Scope<MethodDef> {
            it.associateBy { method -> method.signature.name }
        }

        val fieldsScope = Scope {
            it.associateBy(FieldDef::name)
        }

        val superScope = Scope {
            it.associateBy(Super::name)
        }

        override fun example(inject: Map<String, String>, exIndent: String) = """
            |.source MyClass.j
            |.class public ${inject["class-name"] ?: "MyClass"}
            |${FieldDef.example(inject, exIndent = "\t$exIndent")}    
            |${MethodDef.example(inject, "\t$exIndent")}
            |.super java/lang/Object
            """
            .trimMargin("|")
            .trim()
    }

    class Source(private val name: String) : Directive {
        override fun toJasmin(ident: String): String = "$ident.source $name.j"
    }

    class Class(private val name: String, private val accessSpec: AccessSpec) : Directive {
        override fun toJasmin(ident: String): String = "$ident.class ${accessSpec.kind} $name"
    }

    class Super(val name: String) : Directive {
        override fun toJasmin(ident: String): String = "$ident.super $name"
    }


    override fun toJasmin(ident: String): String = """
        |${Source(name).toJasmin(ident)}
        |${Class(name, accessSpec).toJasmin(ident)}
        |${with(fieldsScope) { fields.toJasmin("$ident\t") }}
        |${with(methodsScope) { methods.toJasmin("$ident\t") }}
        |${with(superScope) { superClass.toJasmin("$ident\t") }}
        """
        .trimMargin("|")
        .trim()
}

data class FieldDef(
    val name: String,
    val accessSpec: AccessSpec,
    val type: String,
    val value: String?
) : Directive, Utils by Companion {
    companion object : Utils {

        override fun example(inject: Map<String, String>, exIndent: String) =
            "$exIndent.field public final PI F = ${inject["field-value"] ?: 3.14}"
    }

    override fun toJasmin(ident: String): String =
        "$ident.field ${accessSpec.kind} $name $type" + (if (value != null) " = $value" else "")
}

data class MethodDef(
    val signature: Signature,
    val instructions: List<Instruction<*>>,
    val varsLimit: Int? = null,
    val stackLimit: Int? = null
) : Directive, Utils by Companion {
    companion object : Utils {

        override fun example(inject: Map<String, String>, exIndent: String) = """
        |.method public static main([Ljava/lang/String;)V
        |   .limit stack 2
        |   getstatic java/lang/System/out Ljava/io/PrintStream
        |   ldc ${inject["block-print"] ?: "10"}
        |   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
        |   return
        |.end method"""
            .trimMargin("|")
            .trim()
    }

    abstract class Limit(val token: String, val limit: Int) : Directive {
        override fun toJasmin(ident: String): String = "$ident.limit $token $limit"
    }

    class Stack(limit: Int) : Limit("stack", limit)

    class Vars(limit: Int) : Limit("vars", limit)

    class Signature(
        val name: String, val accessSpec: AccessSpec, val returnType: String, val args: List<String>
    ) : Directive {
        override fun toJasmin(ident: String): String =
            "$ident.method ${accessSpec.kind} $name([${args.joinToString("")})$returnType"
    }

    class End : Directive {
        override fun toJasmin(ident: String): String = "$ident\treturn\n$ident.end method"
    }

    override fun toJasmin(ident: String): String = """
        |${signature.toJasmin(ident)}
        |${stackLimit?.let { Stack(it).toJasmin("\t$ident") } ?: ""}
        |${varsLimit?.let { Vars(varsLimit).toJasmin("\t$ident") } ?: ""}
        |${instructions.joinToString("\n") { it.toJasmin("\t$ident") }}
        |${End().toJasmin(ident)}
        """
        .trimMargin("|")
        .trim()
}


