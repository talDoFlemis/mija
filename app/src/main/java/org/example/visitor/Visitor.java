package org.example.visitor;

import org.example.ast.*;

public interface Visitor<T> {
    T visit(And a);

    T visit(BooleanType b);

    T visit(Not n);

    T visit(True t);

    T visit(False f);

    T visit(Identifier i);

    T visit(Call c);

    T visit(IdentifierExpression i);

    T visit(IdentifierType i);

    T visit(NewObject n);

    T visit(This t);

    T visit(ArrayLookup a);

    T visit(ArrayAssign a);

    T visit(ArrayLength a);

    T visit(Plus p);

    T visit(Minus m);

    T visit(Times t);

    T visit(IntegerLiteral i);

    T visit(IntegerType i);

    T visit(IntArrayType i);

    T visit(LessThan l);

    T visit(NewArray n);

    T visit(While w);

    T visit(If i);

    T visit(Assign a);

    T visit(Sout s);

    T visit(Block b);

    T visit(MainClass m);

    T visit(ClassDeclSimple c);

    T visit(ClassDeclExtends c);

    T visit(Program p);

    T visit(MethodDecl m);

    T visit(VarDecl v);

    T visit(Formal f);
}
