package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EqualsInstruction implements Instruction {
	private Type type;
	private Value leftOperand;
	private Value rightOperand;
	private Value result;

	public String getInstructionAsString() {
		return String.format("%s = icmp eq %%%s %%%s, %%%s", result.getV(), type.getTypeString(), leftOperand.getV(), rightOperand.getV());
	}
}
