package org.example.irtree;

public abstract class Stm {
    public abstract ExpList children();

    public abstract Stm build(ExpList children);
}
