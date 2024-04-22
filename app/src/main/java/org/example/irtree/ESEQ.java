package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class ESEQ extends ExpAbstract {
    public Stm stm;
    public ExpAbstract exp;

    public ExpList kids() {
        throw new IRTreeException("kids() not applicable to ESEQ");
    }

    public ExpAbstract build(ExpList kids) {
        throw new IRTreeException("build() not applicable to ESEQ");
    }
}
