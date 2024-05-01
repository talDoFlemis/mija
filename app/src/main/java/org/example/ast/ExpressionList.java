package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionList {
    private ArrayList<Expression> list = new ArrayList<>();

    public void addExpression(Expression expression) {
        list.add(expression);
    }
}
