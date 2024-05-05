package org.example.llvm;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class IntegerType implements Type {
	public String getTypeString() {
		return "i32";
	}
}
