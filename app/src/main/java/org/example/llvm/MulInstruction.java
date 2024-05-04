package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MulInstruction implements Instruction{
    private Value leftOperand;
    private Value rightOperand;
    private Value result;
    private final Type type = new IntegerType();

    public String getInstructionAsString() {
        return result.getV() + " = mul " + type.getTypeString() + leftOperand.getV() + " , " + rightOperand.getV();
    }
}
