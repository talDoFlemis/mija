package org.example.irtree;

abstract public class Stm {
    abstract public ExpList kids();
    abstract public Stm build(ExpList kids);
}
