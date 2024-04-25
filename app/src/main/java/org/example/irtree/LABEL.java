package org.example.irtree;
import org.example.temp.Label;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class LABEL extends Stm {
    public Label label;

    public ExpList kids() {
        return null;
    }

    public Stm build(ExpList kids) {
        return this;
    }
}
