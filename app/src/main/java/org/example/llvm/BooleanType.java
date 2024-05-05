package org.example.llvm;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class BooleanType implements Type {
	public String getTypeString() {
		return "i32";
	}
}
