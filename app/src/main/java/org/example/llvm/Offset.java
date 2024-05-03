package org.example.llvm;

import kotlin.Pair;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Offset {
    @Builder.Default
    private ArrayList<Pair<Type, Value>> o = new ArrayList<>();

    public List<Pair<Type, Value>> getOffsets() {
        return o;
    }

    public void addOffset(Type type, Value value) {
        o.add(new Pair<>(type, value));
    }

    public int size() {
        return o.size();
    }

}
