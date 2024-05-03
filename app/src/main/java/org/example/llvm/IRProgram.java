package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;

@AllArgsConstructor
@Builder
@Data
public class IRProgram {
    private LinkedHashMap<String, StructuredType> structuredTypes;
    private LinkedHashMap<String, Function> functions;

    public void addFunction(Function f) {
        functions.putIfAbsent(f.getName(), f);
    }

    public void addStructuredTypes(StructuredType t){
        structuredTypes.putIfAbsent(t.getTypeString(), t);
    }
}
