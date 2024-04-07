package org.example.ast;

import lombok.*;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ClassDeclList {
    @Builder.Default
    public ArrayList<ClassDecl> classDecls =  new ArrayList<>();

    public void addClassDecl(ClassDecl classDecl) {
        classDecls.add(classDecl);
    }
}
