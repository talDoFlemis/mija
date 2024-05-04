package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AllocaInstruction implements Instruction{
    private Type type;
    private Value result;

    public String getInstructionAsString() {
        return String.format("%s = alloca %s", result.getV(), type.getTypeString());
    }
}
