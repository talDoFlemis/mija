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
public class MainClass extends Node {
    private Identifier className;
    private Identifier argsName;
    private StatementList statements;

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}