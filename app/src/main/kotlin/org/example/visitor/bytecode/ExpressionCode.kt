package org.example.visitor.bytecode

abstract class ExpressionCode : CodeGen.CodeNode() {
    fun arithmetic(block: ArithmeticCode.() -> Unit) = initScope(ArithmeticCode(), block)

    fun arrayOperation(block: ArrayOperationCode.() -> Unit) = initScope(ArrayOperationCode(), block)
}

class ArithmeticCode : SimpleInstructionCode("iadd", "isub", "imul", "idiv", "irem")


class ArrayOperationCode : SimpleInstructionCode("newarray")