package org.example.temp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class Temp {
    private static int count;
    private int num;

    public String toString() {
        return "t" + num;
    }

    public Temp() {
        num = count++;
    }
}

