package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ExpressionList {
    private ArrayList<Expression> list;
}
