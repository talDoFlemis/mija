package org.example.visitor.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.irtree.ExpAbstract;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Log4j2
public class Exp {

    public ExpAbstract exp;

    public ExpAbstract unEx() {
        return exp;
    }
}
