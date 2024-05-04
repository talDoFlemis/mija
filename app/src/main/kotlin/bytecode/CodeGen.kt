package bytecode


object CodeGen {
    @DslMarker
    annotation class JasminDSL

    interface Element {
        fun render(builder: java.lang.StringBuilder): java.lang.StringBuilder

        fun StringBuilder.use(block: StringBuilder.() -> Unit) = apply {
            append("\n")
            apply(block)
            append("\n")
        }
    }

    @JasminDSL
    abstract class CodeNode : Element {
        protected val properties = HashMap<String, String>()
        protected val scope = ArrayList<Element>()

        protected fun <T : Element> initScope(instance: T, block: T.() -> Unit): T {
            instance.apply(block)
            scope.add(instance)
            return instance
        }
    }

    data object BaseInstance : CodeNode() {
        fun program(block: ProgramCode.() -> Unit): ProgramCode = ProgramCode().apply(block)
        override fun render(builder: java.lang.StringBuilder): java.lang.StringBuilder {
            return scope.fold(builder) { acc, element -> element.render(acc) }
        }
    }

    @JasminDSL
    class ProgramCode : CodeNode() {
        val mainClass
            get() = scope.filterIsInstance<ClassCode>().filter { it.isMain }.run {
                require(size == 1) { "There should be only one main class" }
                first()
            }
        val classes get() = scope.filterIsInstance<ClassCode>().filter { !it.isMain }
        var name
            get() = properties["name"]!!
            set(value) {
                properties["name"] = value
            }

        override fun render(builder: java.lang.StringBuilder): StringBuilder {
            val line = "----------------------------------------"

            return builder.use {
                mainClass.render(this)
                classes.forEach { it.render(this) }
            }
        }

        fun classDecl(isMain: Boolean = false, block: ClassCode.() -> Unit): ClassCode =
            initScope(ClassCode(isMain), block)
    }

    @JasminDSL
    class ClassCode(val isMain: Boolean) : CodeNode() {
        val methods get() = scope.filterIsInstance<MethodDeclCode>()
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
            methods.forEach { it.render(this) }

            appendLine(".end class")
        }

        fun methodDecl(block: MethodDeclCode.() -> Unit): MethodDeclCode = initScope(MethodDeclCode(), block)
    }

    @JasminDSL
    class MethodDeclCode : CodeNode() {
        val statements get() = scope.filterIsInstance<StatementCode>()
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
            append("\n")
            appendLine(".method public $name")
            appendLine(".descriptor $descriptor")

            statements.forEach { it.render(this) }

            appendLine(".end method")
            append("\n")
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
            appendLine("statement $type")
            appendLine(".limit locals 10")
        }
    }


}