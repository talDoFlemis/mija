package org.example.visitor.symbols;

import lombok.*;
import org.example.ast.*;
import org.example.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class SymbolTableVisitor implements Visitor<Void> {
    @Builder.Default
    private MainTable mainTable = new MainTable();
    @Builder.Default
    private ClassTable currentClassTable = null;
    @Builder.Default
    private MethodTable currentMethodTable = null;
    @Builder.Default
    private ArrayList<SymbolTableException> errors = new ArrayList<>();
    @Builder.Default
    private boolean ignoreExtends = false;

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
        var classTable = ClassTable.builder()
                .className(m.getClassName().getS())
                .build();
        addClassToMainTable(classTable);
        currentClassTable = classTable;

        MethodTable methodTmpTable = MethodTable
                .builder()
                .classParent(currentClassTable)
                .methodName("main")
                .methodReturnType(null)
                .build();
        addMethodToClassTable(currentClassTable, methodTmpTable);

        currentClassTable = null;
        return null;
    }

    public Void visit(ClassDeclSimple c) {
        ClassTable classTableTmp = ClassTable
                .builder()
                .className(c.getClassName().getS())
                .build();

        addClassToMainTable(classTableTmp);
        currentClassTable = classTableTmp;

        c.getFields().getVarDecls().forEach(field -> field.accept(this));
        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        currentClassTable = null;
        return null;
    }

    public Void visit(ClassDeclExtends c) {
        ClassTable extendsClass = mainTable.getMap().get(c.getParent().getS());

        if (extendsClass == null) {
            var e = new SymbolTableException("The class " + c.getParent().getS() + " was not defined");
            errors.add(e);
            return null;
        }

        currentClassTable = ClassTable
                .builder()
                .className(c.getClassName().getS())
                .parent(extendsClass)
                .fieldsContext(new LinkedHashMap<>(extendsClass.getFieldsContext()))
                .methodsContext(new HashMap<>(extendsClass.getMethodsContext()))
                .build();

        addClassToMainTable(currentClassTable);

        ignoreExtends = true;
        c.getFields().getVarDecls().forEach(varDecl -> varDecl.accept(this));
        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        currentClassTable = null;
        ignoreExtends = false;
        return null;
    }

    public Void visit(Program p) {
        p.getMainClass().accept(this);
        p.getClasses().getClassDecls().forEach(clazz -> clazz.accept(this));
        return null;
    }

    public Void visit(MethodDecl m) {
        MethodTable methodTable = MethodTable
                .builder()
                .methodName(m.getIdentifier())
                .classParent(currentClassTable)
                .methodReturnType(m.getType())
                .build();

        addMethodToClassTable(currentClassTable, methodTable);
        currentMethodTable = methodTable;

        m.getFormals().getFormals().forEach(formal -> formal.accept(this));
        m.getVarDecls().getVarDecls().forEach(varDecl -> varDecl.accept(this));

        currentMethodTable = null;
        return null;
    }

    public Void visit(VarDecl v) {
        var name = v.getName();
        if (currentMethodTable == null) {
            addFieldToClassTable(currentClassTable, v.getType(), name);
        } else {
            addLocalsToMethodTable(currentMethodTable, v.getType(), name);
        }
        return null;
    }

    public Void visit(Formal f) {
        addParamToMethodTable(currentMethodTable, f.getType(), f.getName());
        return null;
    }

    private void addClassToMainTable(ClassTable table) {
        var name = table.getClassName();
        if (mainTable.getMap().containsKey(name)) {
            var e = new SymbolTableException("The class " + name + " has been already defined my men");
            errors.add(e);
            return;
        }
        mainTable.getMap().put(name, table);
    }

    private void addFieldToClassTable(ClassTable table, Type type, String name) {
        if (table.getFieldsContext().containsKey(name)) {
            var e = new SymbolTableException("The field " + name + " has been already defined on class " + table.getClassName());
            errors.add(e);
            return;
        }
        table.getFieldsContext().put(name, type);
    }

    private void addMethodToClassTable(ClassTable classTable, MethodTable methodTable) {
        if (!ignoreExtends && classTable.getMethodsContext().containsKey(methodTable.getMethodName())) {
            var e = new SymbolTableException("The method " + methodTable.getMethodName() + " was already defined in class " + classTable.getClassName());
            errors.add(e);
            return;
        }
        classTable.getMethodsContext().put(methodTable.getMethodName(), methodTable);
    }

    private void addParamToMethodTable(MethodTable table, Type type, String name) {
        if (table.getParamsContext().containsKey(name)) {
            var e = new SymbolTableException("The param " + name + " has been already defined in method " + table.getMethodName() + " on class " + table.getClassParent().getClassName());
            errors.add(e);
            return;
        }
        table.getParamsContext().put(name, type);
    }

    private void addLocalsToMethodTable(MethodTable table, Type type, String name) {
        if (!ignoreExtends && table.getLocalsContext().containsKey(name)) {
            var e = new SymbolTableException("The param " + name + " has been already defined in method " + table.getMethodName() + " on class " + table.getClassParent().getClassName());
            errors.add(e);
            return;
        }
        table.getLocalsContext().put(name, type);
    }
}
