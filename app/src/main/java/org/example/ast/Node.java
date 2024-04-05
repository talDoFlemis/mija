package org.example.ast;

import org.example.visitor.Visitor;

public abstract class Node {
    public abstract <T> T accept(Visitor<T> v);
}
