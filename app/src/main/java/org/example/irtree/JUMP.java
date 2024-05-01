package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.temp.Label;
import org.example.temp.LabelList;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class JUMP extends Stm {
    public ExpAbstract exp;
    public LabelList targets;

    public JUMP(Label target) {
        this(new NAME(target), new LabelList(target, null));
    }

    public ExpList children() {
        return new ExpList(exp, null);
    }

    public Stm build(ExpList children) {
        return new JUMP(children.head, targets);
    }
}
