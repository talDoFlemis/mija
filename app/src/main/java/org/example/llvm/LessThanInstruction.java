package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LessThanInstruction implements Instruction {
    private Value leftOperand;
    private Value rightOperand;
    private Value result;
    private final Type type = new BooleanType();

    public String getInstructionAsString() {
        return result.getV() + " = icmp slt " + type.getTypeString() + " " + leftOperand.getV() + " , " + rightOperand.getV();
    }
}
