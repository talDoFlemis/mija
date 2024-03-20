package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.visitor.ASTVisitor;
import org.example.visitor.TypeVisitor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class ClassDeclExtends extends ClassDecl {
    private Identifier className;
    private Identifier parent;
    private VarDeclList fields;
    private MethodDeclList methods;

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}