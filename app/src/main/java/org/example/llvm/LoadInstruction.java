package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoadInstruction implements Instruction {
    private Type type;
    private Value pointer;
    private Value result;

    public String getInstructionAsString() {
        return String.format("%s = load %s, %s* %s", result, type, type, pointer);
    }
}
