package org.example.llvm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

public interface StructuredType extends Type {
    String getStructuredTypeString();
}
