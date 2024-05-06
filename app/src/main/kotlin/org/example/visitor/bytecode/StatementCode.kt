package org.example.visitor.bytecode

@CodeGen.JasminDSL
class StatementCode(
    vararg val opcodes: String,
) : CodeGen.CodeNode() {


    fun fieldManipulation(block: FieldManipulationCode.() -> Unit) =
        initScope(FieldManipulationCode(), block)

    fun methodCall(block: MethodCallCode.() -> Unit) = initScope(MethodCallCode(), block)

    fun localVar(block: LocalVarCode.() -> Unit) = initScope(LocalVarCode(), block)

    fun classObjcOperation(block: ClassObjcOperationCode.() -> Unit) =
        initScope(ClassObjcOperationCode(), block)

    fun pushOrInc(block: PushOrIncCode.() -> Unit) = initScope(PushOrIncCode(), block)

    fun branch(block: BranchCode.() -> Unit) = initScope(BranchCode(), block)
    override fun render(builder: java.lang.StringBuilder): java.lang.StringBuilder {
        return scope.fold(builder) { acc, element -> element.render(acc) }
    }


}

fun interface Instruction : CodeGen.Element {
    fun requireOpcode(value: String, opcodes: Array<out String>) {
        require(value in opcodes) { "Invalid opcode: $value" }
    }
}


abstract class Aux(vararg val opcodes: String) : CodeGen.CodeNode(), Instruction {
    var opcode: String
        get() = properties["opcode"]!!
        set(value) {
            require(value in opcodes) { "Invalid opcode: $value" }
            properties["opcode"] = value
        }
}

@CodeGen.JasminDSL
abstract class SimpleInstructionCode(
    vararg opcodes: String
) : Aux(*opcodes) {
    var operand: String
        get() = properties["operand"]!!
        set(value) {
            properties["operand"] = value
        }

    override fun render(builder: StringBuilder) = builder.use {
        appendLine("$opcode $operand")
    }
}

abstract class BinaryInstructionCode(
    vararg opcode: String
) : Aux(*opcode) {
    var operand1: String
        get() = properties["op_first"]!!
        set(value) {
            properties["op_first"] = value
        }
    var operand2: String
        get() = properties["op_second"]!!
        set(value) {
            properties["op_second"] = value
        }

    override fun render(builder: StringBuilder) = builder.use {
        appendLine("$opcode $operand1 $operand2")
    }
}

@CodeGen.JasminDSL
class FieldManipulationCode : BinaryInstructionCode(
    "getfield", "getstatic", "putfield", "putstatic"
)

@CodeGen.JasminDSL
class LocalVarCode : SimpleInstructionCode(
    "ret", "aload", "astore", "dload", "dstore", "fload", "fstore", "iload", "istore", "lload", "lstore"
)

class PushOrIncCode : SimpleInstructionCode(
    "iinc", "bipush", "sipush"
)

@CodeGen.JasminDSL
class BranchCode : SimpleInstructionCode(
    "goto", "goto_w", "if_acmpeq", "if_acmpne", "if_icmpeq",
    "if_icmpge", "if_icmpgt", "if_icmple", "if_icmplt",
    "if_icmpne", "ifeq", "ifge", "ifgt", "ifle", "iflt",
    "ifne", "ifnonnull", "ifnull", "jsr", "jsr_w"
)

@CodeGen.JasminDSL
class ClassObjcOperationCode : SimpleInstructionCode(
    "new", "anewarray", "checkcast", "instanceof"
)

@CodeGen.JasminDSL
class MethodCallCode : SimpleInstructionCode(
    "invokeinterface", "invokestatic", "invokevirtual"
)
