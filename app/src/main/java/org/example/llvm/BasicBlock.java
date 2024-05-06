package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicBlock {
	@Builder.Default
	private Value label = new Value("entry");
	@Builder.Default
	private ArrayList<PhiInstruction> phiInstructions = new ArrayList<>();
	@Builder.Default
	private ArrayList<Instruction> instructions = new ArrayList<>();
	@Builder.Default
	private TerminalInstruction terminalInstruction = new RetInstruction();

	public void addInstruction(Instruction i) {
		instructions.add(i);
	}
}
