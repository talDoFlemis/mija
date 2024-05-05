package org.example.visitor.bytecode

@CodeGen.JasminDSL
abstract class StatementCode(
    vararg val operands: String,
) : CodeGen.CodeNode() {
    var opcode: String
        get() = properties["opcode"]!!
        set(value) {
            require(value in operands) { "Invalid opcode: $value" }
            properties["opcode"] = value
        }


}

@CodeGen.JasminDSL
abstract class SimpleInstructionCode(
    vararg opcode: String
) : StatementCode(*opcode) {
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
) : StatementCode(*opcode) {
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
