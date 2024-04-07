package org.example.visitor.symbols;

import lombok.*;
import org.example.ast.Type;

import java.util.HashMap;

@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class ClassTable {
    private String className;
    private ClassTable parent;
    @Builder.Default
    HashMap<String, Type> fieldsContext = new HashMap<>();
    @Builder.Default
    HashMap<String, MethodTable> methodsContext = new HashMap<>();
}
