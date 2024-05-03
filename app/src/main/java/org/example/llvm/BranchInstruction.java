package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BranchInstruction implements TerminalInstruction {
    private Value condition;
    private Value trueLabel;
    private Value falseLabel;

    public BranchInstruction(Value destination){
        trueLabel = destination;
    }

    public String getInstructionAsString() {
        if (condition == null) {
            return String.format("br label %%%s", trueLabel.getV());
        }
        return String.format("br i1 %s, label %%%s, label %%%s", condition.getV(), trueLabel.getV(), falseLabel.getV());
    }
}
