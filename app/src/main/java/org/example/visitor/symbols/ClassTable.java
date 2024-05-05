package org.example.visitor.symbols;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.ast.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class ClassTable {
	@Builder.Default
	LinkedHashMap<String, Type> fieldsContext = new LinkedHashMap<>();
	@Builder.Default
	HashMap<String, MethodTable> methodsContext = new HashMap<>();
	private String className;
	private ClassTable parent;
}
