package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.visitor.ASTVisitor;
import org.example.visitor.TypeVisitor;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@AllArgsConstructor
public class Identifier extends Expression {
    private String s;
    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}