package org.example.ast;

import org.example.visitor.ASTVisitor;
import org.example.visitor.TypeVisitor;

public abstract class Node {
    public abstract void accept(ASTVisitor v);
    public abstract Type accept(TypeVisitor v);
}
