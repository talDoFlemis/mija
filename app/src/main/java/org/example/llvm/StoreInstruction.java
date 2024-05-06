package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StoreInstruction implements Instruction {
	private Type type;
	private Value value;
	private Value pointer;

	public String getInstructionAsString() {
		return String.format("store %s %s, %s* %s", type, value, type, pointer);
	}
}
