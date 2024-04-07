package org.example.visitor.mermaid;

import lombok.*;
import org.example.ast.*;
import org.example.visitor.Visitor;
import org.example.visitor.symbols.ClassTable;
import org.example.visitor.symbols.MainTable;
import org.example.visitor.symbols.MethodTable;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class MermaidASTPrinterVisitor implements Visitor<Void> {

    private PrintStream printStream;

    @Builder.Default
    private MainTable mainTable = new MainTable();
    @Builder.Default
    private ClassTable currentClassTable = null;
    @Builder.Default
    private MethodTable currentMethodTable = null;
    @Builder.Default
    private ArrayList<MermaidASTPrinterException> errors = new ArrayList<>();

    public MermaidASTPrinterVisitor(String file) throws FileNotFoundException {
        this.printStream = new PrintStream(file);
    }

    public Void visit(And a) {
        return null;
    }

    public Void visit(BooleanType b) {
        return null;
    }

    public Void visit(Not n) {
        return null;
    }

    public Void visit(True t) {
        return null;
    }

    public Void visit(False f) {
        return null;
    }

    public Void visit(Identifier i) {
        return null;
    }

    public Void visit(Call c) {
        return null;
    }

    public Void visit(IdentifierExpression i) {
        return null;
    }

    public Void visit(IdentifierType i) {
        return null;
    }

    public Void visit(NewObject n) {
        return null;
    }

    public Void visit(This t) {
        return null;
    }

    public Void visit(ArrayLookup a) {
        return null;
    }

    public Void visit(ArrayAssign a) {
        return null;
    }

    public Void visit(ArrayLength a) {
        return null;
    }

    public Void visit(Plus p) {
        return null;
    }

    public Void visit(Minus m) {
        return null;
    }

    public Void visit(Times t) {
        return null;
    }

    public Void visit(IntegerLiteral i) {
        return null;
    }

    public Void visit(IntegerType i) {
        return null;
    }

    public Void visit(IntArrayType i) {
        return null;
    }

    public Void visit(LessThan l) {
        return null;
    }

    public Void visit(NewArray n) {
        return null;
    }

    public Void visit(While w) {
        return null;
    }

    public Void visit(If i) {
        return null;
    }

    public Void visit(Assign a) {
        return null;
    }

    public Void visit(Sout s) {
        return null;
    }

    public Void visit(Block b) {
        return null;
    }

    public Void visit(MainClass m) {
        return null;
    }

    public Void visit(ClassDeclSimple c) {
        return null;
    }

    public Void visit(ClassDeclExtends c) {
        return null;
    }

    public Void visit(Program p) {
        return null;
    }

    public Void visit(MethodDecl m) {
        return null;
    }

    public Void visit(VarDecl v) {
        return null;
    }

    public Void visit(Formal f) {
        return null;
    }

    private String nodeKeyValue(String value) {
        return value + "[" + value + "]";
    }
}