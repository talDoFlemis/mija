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
public class NAME extends Exp {
    public Label label;

    public ExpList kids() {
        return null;
    }

    public Exp build(ExpList kids) {
        return this;
    }
}

