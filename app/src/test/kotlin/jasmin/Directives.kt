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
    val example: String
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
    val superClass: ArrayList<ClassDef.Super> = arrayListOf()
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

        override val example = """
            |.source MyClass.j
            |.class  public MyClass
            |${FieldDef.example}    
            |${MethodDef.example}
            |.super  java/lang/Object
            """
            .trimMargin("|")
            .trim()
    }

    inner class Source(private val name: String) : Directive {
        override fun toJasmin(ident: String): String = "$ident.source $name.j"
    }

    inner class Class(private val name: String) : Directive {
        override fun toJasmin(ident: String): String = "$ident.class ${accessSpec.kind} $name"
    }

    inner class Super(val name: String) : Directive {
        override fun toJasmin(ident: String): String = "$ident.super $superClass"
    }


    override fun toJasmin(ident: String): String = """
        |${Source(name).toJasmin(ident)}
        |${Class(name).toJasmin(ident)}
        |${with(fieldsScope) { fields.toJasmin("$ident\t") }}
        |${with(methodsScope) { methods.toJasmin("$ident\t") }}
        |${with(superScope) { superClass.toJasmin("$ident\t") }}
        """
        .trimIndent()
        .trim()
}

data class FieldDef(
    val name: String,
    val accessSpec: AccessSpec,
    val type: String,
    val value: String?
) : Directive, Utils by Companion {
    companion object : Utils {
        override val example = ".field public final PI F = 3.14"
    }

    override fun toJasmin(ident: String): String =
        "$ident.field ${accessSpec.kind} $name $type" + (if (value != null) " = $value" else "")
}

data class MethodDef(
    val signature: Signature,
    val instructions: List<Instruction>,
    val varsLimit: Int,
    val stackLimit: Int
) : Directive, Utils by Companion {
    companion object : Utils {

        override val example = """
        |.method public static main([Ljava/lang/String;)V
        |   ${/*allocate stack big enough to hold 2 items*/""}
        |   .limit stack 2
        |   
        |   ${/*push java.lang.System.out (type PrintStream)*/""}
        |   getstatic java/lang/System/out Ljava/io/PrintStream;
        |   
        |   ${/*push string to be printed*/""}
        |   ldc "Hello World"
        |   
        |   ${/* invoke println */""}
        |   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
        |   
        |   ; terminate main
        |   return
        |.end method"""
            .trimMargin("|")
            .trim()
    }

    abstract class Limit(val token: String, val limit: Int) : Directive {
        override fun toJasmin(ident: String): String = "$ident.limit $token $limit"
    }

    inner class Stack(limit: Int) : Limit("stack", limit)

    inner class Vars(limit: Int) : Limit("vars", limit)

    inner class Signature(
        val name: String, val accessSpec: AccessSpec, val returnType: String, val args: List<String>
    ) : Directive {
        override fun toJasmin(ident: String): String =
            "$ident.method $name ${accessSpec.kind} $returnType(${args.joinToString("")})V"
    }

    inner class End : Directive {
        override fun toJasmin(ident: String): String = "$ident.end method"
    }

    override fun toJasmin(ident: String): String = """
        |${signature.toJasmin(ident)}
        |${Stack(stackLimit).toJasmin("\t$ident")}
        |${Vars(varsLimit).toJasmin("\t$ident")}
        |${instructions.joinToString("\n") { it.toJasmin("\t$ident") }}
        |${End().toJasmin(ident)}
        """
        .trimIndent()
        .trim()
}


sealed interface Instruction : Jasmin
abstract class UnaryInstruction(val token: String, val index: Int) : Instruction {
    override fun toJasmin(ident: String): String = "$ident.$token $index"
}

class Line(val number: Int) : UnaryInstruction("line", number)
class bipush(val value: Int) : UnaryInstruction("bipush", value)


sealed interface Label : Jasmin
