package org.example.ast;

import lombok.*;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MethodDeclList {
    @Builder.Default
    public ArrayList<MethodDecl> methodDecls = new ArrayList<>();

    public void addMethodDecl(MethodDecl methodDecl) {
        methodDecls.add(methodDecl);
    }
}
