package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MinusInstruction implements Instruction {
    private Value leftOperand;
    private Value rightOperand;
    private Value result;
    private final Type type = new IntegerType();

    public String getInstructionAsString() {
        return result.getV() + " = minus " + type.getTypeString() + leftOperand.getV() + " , " + rightOperand.getV();
    }
}
