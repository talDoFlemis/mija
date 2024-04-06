package org.example.visitor.symbols;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class MainTable {
    @Builder.Default
    HashMap<String, ClassTable> map = new HashMap<>();
}
