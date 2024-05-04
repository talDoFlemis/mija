package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class BitcastInstruction implements Instruction {
    private Type fromType;
    private Type toType;
    private Value value;
    private Value result;

    public String getInstructionAsString() {
        return String.format("%s = %s %s to %s", result.getV(), fromType.getTypeString(), value.getV(), toType.getTypeString());
    }
}
