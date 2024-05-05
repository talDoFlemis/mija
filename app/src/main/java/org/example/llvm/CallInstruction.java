package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class CallInstruction implements Instruction {
	private Type returnType;
	private Function functionCalled;
	private LinkedHashMap<Value, Type> args;
	private Value result;

	public String getInstructionAsString() {
		var argList = args.sequencedEntrySet()
			.stream()
			.map(e -> String.format("%s %s", e.getKey().getV(), e.getValue().getTypeString()))
			.collect(Collectors.joining(","));

		if (returnType instanceof VoidType) {
			return String.format("%s %s(%s)", returnType.getTypeString(), functionCalled.getName(), argList);
		}
		return String.format("%s = %s %s(%s)", result.getV(), returnType.getTypeString(), functionCalled.getName(), argList);
	}
}
