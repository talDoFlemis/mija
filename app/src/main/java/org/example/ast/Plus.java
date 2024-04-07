package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class Plus extends Expression {
    private Expression lhe;
    private Expression rhe;

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}