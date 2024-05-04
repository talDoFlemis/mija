package bytecode


object CodeGen {
    @DslMarker
    annotation class JasminDSL

    interface Element {
        fun render(builder: java.lang.StringBuilder): java.lang.StringBuilder
    }

    @JasminDSL
    abstract class CodeNode : Element {
        protected val properties = HashMap<String, String>()
        protected val scope = ArrayList<Element>()

        protected fun <T : Element> initScope(instance: T, block: T.() -> Unit): T {
            val result = instance.apply(block)
            scope.add(instance)
            return result
        }

        abstract override fun render(builder: java.lang.StringBuilder): java.lang.StringBuilder
    }

    @JasminDSL
    class ProgramCode : CodeNode() {
        val mainClass get() = scope.filterIsInstance<ClassCode>().first { it.isMain }
        val classes get() = scope.filterIsInstance<ClassCode>().filter { !it.isMain }
        var name
            get() = properties["name"]!!
            set(value) {
                properties["name"] = value
            }

        override fun render(builder: java.lang.StringBuilder) = builder.apply {
            val line = repeat("-", 80)

            append(line.insert(4, name))
            mainClass.render(this)

            classes.forEach { it.render(this) }

            append(line)
        }

        fun classDecl(isMain: Boolean = false, block: ClassCode.() -> Unit): ClassCode =
            initScope(ClassCode(isMain), block)
    }

    @JasminDSL
    class ClassCode(val isMain: Boolean) : CodeNode() {
        var name
            get() = properties["name"]!!
            set(value) {
                properties["name"] = value
            }
        var path
            get() = properties["path"]!!
            set(value) {
                properties["path"] = value
            }

        override fun render(builder: java.lang.StringBuilder) = builder.apply {
            appendLine(".class public $name")
            appendLine(".super $path")
            scope.forEach { it.render(this) }
        }

        fun methodDecl(block: MethodDeclCode.() -> Unit): MethodDeclCode = initScope(MethodDeclCode(), block)
    }

    @JasminDSL
    class MethodDeclCode : CodeNode() {
        var name
            get() = properties["name"]!!
            set(value) {
                properties["name"] = value
            }
        var descriptor
            get() = properties["descriptor"]!!
            set(value) {
                properties["descriptor"] = value
            }

        override fun render(builder: java.lang.StringBuilder) = builder.apply {
            appendLine(".method public $name")
            appendLine(".descriptor $descriptor")
            scope.forEach { it.render(this) }
            appendLine(".end method")
        }

        fun statement(block: StatementCode.() -> Unit): StatementCode = initScope(StatementCode(), block)
    }

    @JasminDSL
    class StatementCode : CodeNode() {
        var type
            get() = properties["type"]!!
            set(value) {
                properties["type"] = value
            }

        override fun render(builder: java.lang.StringBuilder) = builder.apply {
            appendLine(".limit stack 10")
            appendLine(".limit locals 10")
            scope.forEach { it.render(this) }
        }
    }


}

fun main(args: Array<String>) {
    val program = CodeGen.ProgramCode().apply {
        name = "HelloWorld Program"

        classDecl(isMain = true) {
            name = "HelloWorld"
            path = "java/lang/Object"

            methodDecl {
                name = "<init>"
                descriptor = "()V"
                statement {
                    type = "void"
                }
            }
        }
    }
    val builder = StringBuilder()
    program.render(builder).toString().also(::println)

}