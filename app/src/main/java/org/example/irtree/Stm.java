package org.example.irtree;

public abstract class Stm {
    public abstract ExpList kids();
    public abstract Stm build(ExpList kids);
}
