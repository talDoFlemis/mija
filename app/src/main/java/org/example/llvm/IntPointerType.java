package org.example.llvm;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class IntPointerType implements Type {
	public String getTypeString() {
		return "int*";
	}
}
