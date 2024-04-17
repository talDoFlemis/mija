package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class EXPSTM extends Stm {
    public Exp exp;

    public ExpList kids() {
        return new ExpList(exp, null);
    }

    public Stm build(ExpList kids) {
        return new EXPSTM(kids.head);
    }
}
