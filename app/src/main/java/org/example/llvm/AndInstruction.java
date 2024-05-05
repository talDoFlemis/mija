package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AndInstruction implements Instruction {
	private final Type type = new BooleanType();
	private Value leftOperand;
	private Value rightOperand;
	private Value result;

	public String getInstructionAsString() {
		return result.getV() + " = and " + type.getTypeString() + leftOperand.getV() + " , " + rightOperand.getV();
	}
}
