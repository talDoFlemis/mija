package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.temp.Label;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class LABEL extends Stm {
    public Label label;

    public ExpList children() {
        return null;
    }

    public Stm build(ExpList children) {
        return this;
    }
}
