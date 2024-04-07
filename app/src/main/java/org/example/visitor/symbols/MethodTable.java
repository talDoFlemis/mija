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
    private String methodName;
    private Type methodReturnType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ClassTable classParent;
    @Builder.Default
    LinkedHashMap<String, Type> paramsContext = new LinkedHashMap<>();
    @Builder.Default
    HashMap<String, Type> localsContext = new HashMap<>();
}
