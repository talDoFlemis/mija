package org.example.mips;

import org.example.frame.Access;
import org.example.temp.Temp;
import org.example.irtree.ExpAbstract;
import org.example.irtree.TEMP;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class InReg extends Access {
    Temp temp;

    public ExpAbstract exp(ExpAbstract fp) {
        return new TEMP(temp);
    }

    public String toString() {
        return temp.toString();
    }
}