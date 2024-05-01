package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class MOVE extends Stm {
    public ExpAbstract dst, src;

    public ExpList children() {
        if (dst instanceof MEM)
            return new ExpList(((MEM) dst).exp, new ExpList(src, null));
        else return new ExpList(src, null);
    }

    public Stm build(ExpList children) {
        if (dst instanceof MEM)
            return new MOVE(new MEM(children.head), children.tail.head);
        else return new MOVE(dst, children.head);
    }
}
