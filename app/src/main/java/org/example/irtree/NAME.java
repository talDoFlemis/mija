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
public class NAME extends ExpAbstract {
    public Label label;

    public ExpList children() {
        return null;
    }

    public ExpAbstract build(ExpList children) {
        return this;
    }
}

