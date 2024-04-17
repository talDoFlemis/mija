package org.example.irtree;

import org.example.temp.Label;
import org.example.temp.LabelList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class JUMP extends Stm {
    public Exp exp;
    public LabelList targets;

    public JUMP(Label target) {
        this(new NAME(target), new LabelList(target, null));
    }

    public ExpList kids() {
        return new ExpList(exp, null);
    }

    public Stm build(ExpList kids) {
        return new JUMP(kids.head, targets);
    }
}
