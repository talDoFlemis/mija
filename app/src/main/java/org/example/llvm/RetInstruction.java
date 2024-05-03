package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetInstruction implements TerminalInstruction{
    private Type type;
    private Value value;

    public String getInstructionAsString() {
        if (type == null){
            return "ret void ";
        }
        return String.format("ret %s %s", type.getTypeString(), value.getV());
    }
}
