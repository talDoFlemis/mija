package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class ESEQ extends Exp {
    public Stm stm;
    public Exp exp;

    public ExpList kids() {
        throw new Error("kids() not applicable to ESEQ");
    }

    public Exp build(ExpList kids) {
        throw new Error("build() not applicable to ESEQ");
    }
}
