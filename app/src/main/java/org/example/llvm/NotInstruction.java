package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotInstruction implements Instruction {
    private Value result;
    private Value operand;
    private final Type type = new BooleanType();

    public String getInstructionAsString() {
        return result.getV() + " = icmp eq " + type.getTypeString() + " " + operand.getV() + " , 0";
    }
}
