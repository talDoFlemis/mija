package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Builder
@AllArgsConstructor
@Data
public class Function {
	private String name;
	@Builder.Default
	private LinkedHashMap<Value, Type> arguments = new LinkedHashMap<>();
	private BasicBlock entryBlock;
	@Builder.Default
	private ArrayList<BasicBlock> otherBlocks = new ArrayList<>();
	private Type returnType;

	public void addBlock(BasicBlock b) {
		otherBlocks.add(b);
	}
}
