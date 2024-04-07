package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class Program extends Node {
    private MainClass mainClass;
    @Builder.Default
    private ClassDeclList classes = new ClassDeclList();

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}