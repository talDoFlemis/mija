package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class CONST extends ExpAbstract {
    public int value;

    public ExpList children() {
        return null;
    }

    public ExpAbstract build(ExpList children) {
        return this;
    }
}
