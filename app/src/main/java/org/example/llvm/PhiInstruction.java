package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class PhiInstruction implements Instruction {
    public String getInstructionAsString() {
        return "";
    }
}
