package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpList {
    public ExpAbstract head;
    public ExpList tail;
}
