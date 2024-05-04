package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MinusInstruction implements Instruction {
	private final Type type = new IntegerType();
	private Value leftOperand;
	private Value rightOperand;
	private Value result;

	public String getInstructionAsString() {
		return result.getV() + " = minus " + type.getTypeString() + leftOperand.getV() + " , " + rightOperand.getV();
	}
}
