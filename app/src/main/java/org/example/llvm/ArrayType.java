package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArrayType implements StructuredType {
	public String getTypeString() {
		return "%Array";
	}

	public String getStructuredTypeString() {
		return "%Array = { i32, i32* }";
	}
}
