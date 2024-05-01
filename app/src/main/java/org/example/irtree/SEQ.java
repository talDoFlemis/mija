package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class SEQ extends Stm {
    public Stm left, right;

    public ExpList children() {
        throw new IRTreeException("children() not applicable to SEQ");
    }

    public Stm build(ExpList children) {
        throw new IRTreeException("build() not applicable to SEQ");
    }
}
