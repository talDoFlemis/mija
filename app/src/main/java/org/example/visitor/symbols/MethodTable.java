package org.example.visitor.symbols;

import lombok.*;
import org.example.ast.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class MethodTable {
	@Builder.Default
	LinkedHashMap<String, Type> paramsContext = new LinkedHashMap<>();
	@Builder.Default
	HashMap<String, Type> localsContext = new HashMap<>();
	private String methodName;
	private Type methodReturnType;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ClassTable classParent;

	public Type getParamType(String paramName) {
		return paramsContext.get(paramName);
	}

	public Type getLocalType(String localName) {
		return localsContext.get(localName);
	}

	public Type getTypeOfMethodVariable(String variableName) {
		Type type = getLocalType(variableName);
		if (type == null) {
			type = getParamType(variableName);
		}
		return type;
	}
}
