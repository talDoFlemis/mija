package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.temp.Temp;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class TEMP extends ExpAbstract {
    public Temp temp;

    public ExpList children() {
        return null;
    }

    public ExpAbstract build(ExpList children) {
        return this;
    }
}
