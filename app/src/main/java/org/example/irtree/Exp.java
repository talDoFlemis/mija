package org.example.irtree;

public abstract class Exp {
    public abstract ExpList kids();
    public abstract Exp build(ExpList kids);
}
