package org.example.visitor.symbols;

import lombok.*;
import org.example.ast.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class ClassTable {
    private String className;
    private ClassTable parent;
    @Builder.Default
    LinkedHashMap<String, Type> fieldsContext = new LinkedHashMap<>();
    @Builder.Default
    HashMap<String, MethodTable> methodsContext = new HashMap<>();
}
