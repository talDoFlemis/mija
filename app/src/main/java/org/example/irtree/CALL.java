package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class CALL extends ExpAbstract {
    public ExpAbstract func;
    public ExpList args;

    public ExpList children() {
        return new ExpList(func, args);
    }

    public ExpAbstract build(ExpList children) {
        return new CALL(children.head, children.tail);
    }

}
