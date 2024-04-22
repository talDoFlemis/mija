package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class MEM extends ExpAbstract {
    public ExpAbstract exp;

    public ExpList kids() {
        return new ExpList(exp, null);
    }

    public ExpAbstract build(ExpList kids) {
        return new MEM(kids.head);
    }
}