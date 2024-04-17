package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class CALL extends Exp {
    public Exp func;
    public ExpList args;

    public ExpList kids() {
        return new ExpList(func, args);
    }

    public Exp build(ExpList kids) {
        return new CALL(kids.head, kids.tail);
    }

}
