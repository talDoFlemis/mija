package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class CONST extends Exp {
    public int value;

    public ExpList kids() {
        return null;
    }

    public Exp build(ExpList kids) {
        return this;
    }
}
