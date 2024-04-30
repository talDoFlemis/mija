package org.example.visitor.irtree;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.example.frame.Frame;
import org.example.irtree.Stm;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Log4j2
public class ProcFrag extends Frag {
    private Stm body;
    private Frame frame;

    public ProcFrag(Stm b, Frame f, Frag next) {
        super(next);
        body = b;
        frame = f;
    }
}
