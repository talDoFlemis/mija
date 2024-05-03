package org.example.llvm;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class VoidType implements Type{
    public String getTypeString() {
        return "void";
    }
}
