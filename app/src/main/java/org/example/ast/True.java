package org.example.ast;

import org.example.visitor.ASTVisitor;
import org.example.visitor.TypeVisitor;

public class True extends Expression {
    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}
