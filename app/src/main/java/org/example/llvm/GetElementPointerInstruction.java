package org.example.llvm;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@Builder
public class GetElementPointerInstruction implements Instruction {
    private Type baseType;
    private Value baseAddress;
    private Value result;
    private Offset offset;

    public String getInstructionAsString() {
        String dimensions = convertOffsets();
        return String.format("%s = getelementptr %s, %s* %s, %s", result.getV(), baseType.getTypeString(), baseType.getTypeString(), baseAddress.getV(), dimensions);
    }

    private String convertOffsets() {
        var arr = new ArrayList<>(offset.size());
        for (Pair<Type, Value> entry : offset.getOffsets()) {
            arr.add(String.format("%s %s", entry.component1().getTypeString(), entry.component2().getV()));
        }
        return Strings.join(arr, ',');
    }
}
