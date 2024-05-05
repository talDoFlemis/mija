package org.example.visitor.bytecode


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
            return builder.use {
                mainClass.render(this)
                classes.forEach { it.render(this) }
            }
        }

        fun classDecl(isMain: Boolean = false, block: ClassCode.() -> Unit): ClassCode =
            initScope(ClassCode(isMain), block)
    }

    abstract class DeclarableCode : CodeNode() {
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

        var visibility
            get() = properties["visibility"]!!
            set(value) {
                require(value in setOf("public", "private", "protected")) { "Invalid visibility" }
                properties["visibility"] = value
            }

    }


    @JasminDSL
    class ClassCode(val isMain: Boolean) : DeclarableCode() {
        val methods get() = scope.filterIsInstance<MethodCode>()
        val fields get() = scope.filterIsInstance<FieldCode>()

        var path
            get() = properties["path"]!!
            set(value) {
                properties["path"] = value
            }


        override fun render(builder: java.lang.StringBuilder) = builder.use {
            appendLine(".class public $name")
            appendLine(".super $path")
            fields.forEach { it.render(this) }
            methods.forEach { it.render(this) }
            appendLine(".end class")
        }

        fun methodDecl(block: MethodCode.() -> Unit): MethodCode = initScope(MethodCode(), block)

        fun fieldDecl(block: FieldCode.() -> Unit): FieldCode = initScope(FieldCode(), block)
    }


    class FieldCode : DeclarableCode() {
        override fun render(builder: java.lang.StringBuilder) = builder.use {
            appendLine(".field $visibility $name $descriptor")
        }
    }

    @JasminDSL
    class MethodCode : DeclarableCode() {
        val statements get() = scope.filterIsInstance<StatementCode>()

        val stack get() = statements.size

        override fun render(builder: java.lang.StringBuilder) = builder.use {
            appendLine(".method public $name")
            appendLine(".descriptor $descriptor")
            appendLine(".limit stack $stack")

            statements.forEach { it.render(this) }

            appendLine(".end method")
        }

        fun fieldManipulation(block: FieldManipulationCode.() -> Unit) =
            initScope(FieldManipulationCode(), block)

        fun methodCall(block: MethodCallCode.() -> Unit) = initScope(MethodCallCode(), block)

        fun localVar(block: LocalVarCode.() -> Unit) = initScope(LocalVarCode(), block)

        fun classObjcOperation(block: ClassObjcOperationCode.() -> Unit) =
            initScope(ClassObjcOperationCode(), block)

        fun pushOrInc(block: PushOrIncCode.() -> Unit) = initScope(PushOrIncCode(), block)

        fun branch(block: BranchCode.() -> Unit) = initScope(BranchCode(), block)
    }


}