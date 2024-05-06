package org.example.llvm;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Data
@AllArgsConstructor
@Builder
public class ClassType implements StructuredType {
	private String name;
	@Builder.Default
	private LinkedHashMap<String, Pair<Type, Offset>> fields = new LinkedHashMap<>();
	@Builder.Default
	private HashMap<String, Function> methods = new LinkedHashMap<>();

	public String getStructuredTypeString() {
		return String.format("%%%s = type { %s }", name, String.
			join(", ", fields.values().stream()
				.map(Pair::getFirst)
				.map(Type::getTypeString)
				.toArray(String[]::new)));
	}

	public String getTypeString() {
		return String.format("%%%s", name);
	}

	public void addMethod(String name, Function fn) {
		methods.put(name, fn);
	}
}
