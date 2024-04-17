package org.example.temp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class Label {
    private String name;
    private static int count;

    public Label() {
        this("L" + count++);
    }


    public String toString() {
        return name;
    }
}
