package org.example.ast;

import lombok.*;
import org.example.visitor.ASTVisitor;
import org.example.visitor.TypeVisitor;

@EqualsAndHashCode(callSuper = false)
@ToString
@Data
@AllArgsConstructor
@Builder
public class Identifier extends Expression {
    public String s;
    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}
