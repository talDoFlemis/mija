package org.example.visitor.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.*;
import org.example.mija.SemanticAnalysisException;
import org.example.mija.SemanticAnalysisStrategy;
import org.example.visitor.Visitor;
import org.example.visitor.symbols.ClassTable;
import org.example.visitor.symbols.MainTable;
import org.example.visitor.symbols.MethodTable;
import org.example.visitor.symbols.SymbolTableVisitor;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Log4j2
public class TypeCheckingVisitor implements Visitor<Type>, SemanticAnalysisStrategy {
    @Builder.Default
    private MainTable mainTable = new MainTable();
    @Builder.Default
    private ClassTable currentClassTable = null;
    @Builder.Default
    private MethodTable currentMethodTable = null;
    @Builder.Default
    private ArrayList<TypeCheckingException> errors = new ArrayList<>();

    public TypeCheckingVisitor(MainTable mainTable) {
        this.mainTable = mainTable;
        this.errors = new ArrayList<>();
    }

    public Type visit(And a) {
        Type lheExpr = a.getLhe().accept(this);
        Type rheExpr = a.getRhe().accept(this);

        if (lheExpr instanceof BooleanType && rheExpr instanceof BooleanType) {
            return new BooleanType();
        }

        if (!(lheExpr instanceof BooleanType)) {
            addError("Left Hand Expression of AND Operator is not a Boolean Type");
        }
        if (!(rheExpr instanceof BooleanType)) {
            addError("Right Hand Expression of AND Operator is not a Boolean Type");
        }
        return null;
    }

    public Type visit(BooleanType b) {
        return b;
    }

    public Type visit(Not n) {
        Type expr = n.getE().accept(this);
        if (expr instanceof BooleanType) {
            return new BooleanType();
        }

        addError("Not operator must be applied to a boolean");
        return null;
    }

    public Type visit(True t) {
        return new BooleanType();
    }

    public Type visit(False f) {
        return new BooleanType();
    }

    public Type visit(Identifier i) {
        return getTypeFromEntireClassContextInsideAMethod(i.getS());
    }

    public Type visit(Call c) {
        Type object = c.getOwner().accept(this);
        if (!(object instanceof IdentifierType)) {
            addError("Class referenced wat not found my dude!");
            return null;
        }

        String tmpClassIdentifier = ((IdentifierType) object).getS();
        ClassTable tmpClassTable = mainTable.getMap().get(tmpClassIdentifier);
        if (tmpClassTable == null) {
            addError("Class " + tmpClassIdentifier + " was not found");
            return null;
        }

        MethodTable tmpMethodTable = tmpClassTable.getMethodsContext().get(c.getMethod().getS());
        if (tmpMethodTable == null) {
            addError("Method " + c.getMethod().getS() + " was not found in class" + tmpClassIdentifier);
            return null;
        }

        ArrayList<Expression> args = c.getExpressionList().getList();
        if (tmpMethodTable.getParamsContext().size() != args.size()) {
            addError("Method call" + c.getMethod().getS() + " has not respected arity of formals");
            return null;
        }

        var expectedValues = tmpMethodTable.getParamsContext().values().iterator();
        for (var arg : args) {
            Type argType = arg.accept(this);
            Type expectedType = expectedValues.next();
            if (!checkIfTypeMatchWithInheritance(argType, expectedType) && !(argType.equals(expectedType))) {
                addError("Type mismatch in params on method " + tmpMethodTable.getMethodName() + ", expected " + expectedType.toString() + " found " + argType);
            }
        }

        return tmpMethodTable.getMethodReturnType();
    }


    public Type visit(IdentifierExpression i) {
        Type type = getTypeFromEntireClassContextInsideAMethod(i.getId());
        if (type != null) {
            return type;
        }
        addError("The identifier was not found");
        return null;
    }

    public Type visit(IdentifierType i) {
        return i;
    }

    public Type visit(NewObject n) {
        currentClassTable = mainTable.getMap().get(n.getIdentifier().getS());
        if (currentClassTable != null) {
            return new IdentifierType(n.getIdentifier().getS());
        }
        addError("Class " + n.getIdentifier().getS() + " does not exist");
        return null;
    }

    public Type visit(This t) {
        if (currentClassTable != null) {
            return new IdentifierType(currentClassTable.getClassName());
        }
        addError("Cannot use 'this' outside of a class");
        return null;
    }

    public Type visit(ArrayLookup a) {
        Type arrayType = a.getArray().accept(this);
        Type idxType = a.getIdx().accept(this);

        if (arrayType instanceof IntArrayType && idxType instanceof IntegerType) {
            return new IntegerType();
        }

        checkIfIntArrayType("Array", arrayType);
        checkIfIntegerType("Index", idxType);

        return null;
    }

    public Type visit(ArrayAssign a) {
        Type arrayType = a.getIdentifier().accept(this);
        Type idxType = a.getIndex().accept(this);
        Type valueType = a.getValue().accept(this);

        checkIfIntArrayType("Array", arrayType);
        checkIfIntegerType("Index", idxType);
        checkIfIntegerType("Value", valueType);

        return null;
    }

    public Type visit(ArrayLength a) {
        Type arrayType = a.getArray().accept(this);
        if (checkIfIntArrayType("Array", arrayType)) {
            return new IntegerType();
        }

        return null;
    }

    public Type visit(Plus p) {
        Type lheType = p.getLhe().accept(this);
        Type rheType = p.getRhe().accept(this);

        if (checkBinaryOperation("Plus", lheType, rheType)) {
            return new IntegerType();
        }
        return null;
    }

    public Type visit(Minus m) {
        Type lheType = m.getLhe().accept(this);
        Type rheType = m.getRhe().accept(this);

        if (checkBinaryOperation("Minus", lheType, rheType)) {
            return new IntegerType();
        }
        return null;
    }

    public Type visit(Times t) {
        Type lheType = t.getLhe().accept(this);
        Type rheType = t.getRhe().accept(this);

        if (checkBinaryOperation("Times", lheType, rheType)) {
            return new IntegerType();
        }
        return null;
    }

    public Type visit(IntegerLiteral i) {
        return new IntegerType();
    }

    public Type visit(IntegerType i) {
        return i;
    }

    public Type visit(IntArrayType i) {
        return i;
    }

    public Type visit(LessThan l) {
        Type lheType = l.getLhe().accept(this);
        Type rheType = l.getRhe().accept(this);

        if (checkBinaryOperation("Less Than", lheType, rheType)) {
            return new BooleanType();
        }
        return null;
    }

    public Type visit(NewArray n) {
        Type size = n.getSize().accept(this);
        if (size instanceof IntegerType) {
            return new IntArrayType();
        }
        addError("Array size must be an integer");
        return null;
    }

    public Type visit(While w) {
        Type conditionType = w.getCondition().accept(this);

        checkIfBooleanType("While condition", conditionType);
        w.getBody().accept(this);
        return null;
    }

    public Type visit(If i) {
        Type conditionType = i.getCondition().accept(this);

        checkIfBooleanType("If condition", conditionType);

        i.getThenBranch().accept(this);
        i.getElseBranch().accept(this);
        return null;
    }

    public Type visit(Assign a) {
        Type typeToEvaluate = a.getValue().accept(this);

        if (typeToEvaluate == null) {
            addError("The expression type is null");
            return null;
        }

        String identifier = a.getIdentifier().getS();
        Type expectedType = getTypeFromEntireClassContextInsideAMethod(identifier);

        if (expectedType == null) {
            addError("The identifier was not defined");
            return null;
        }

        if (!checkIfTypeMatchWithInheritance(typeToEvaluate, expectedType) && !(typeToEvaluate.equals(expectedType))) {
            addError("Type mismatch in assignment, expected " + expectedType + " found " + typeToEvaluate);
        }

        return null;
    }

    public Type visit(Sout s) {
        Type expType = s.getExpression().accept(this);
        checkIfIntegerType("System.println.out", expType);
        return null;
    }

    public Type visit(Block b) {
        b.getStatements().getStatements().forEach(stm -> stm.accept(this));
        return null;
    }

    public Type visit(MainClass m) {
        currentClassTable = mainTable.getMap().get(m.getClassName().getS());
        currentMethodTable = currentClassTable.getMethodsContext().get("main");
        m.getStatements().getStatements().forEach(stm -> stm.accept(this));
        currentMethodTable = null;
        currentClassTable = null;
        return null;
    }

    public Type visit(ClassDeclSimple c) {
        currentClassTable = mainTable.getMap().get(c.getClassName().getS());

        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        currentClassTable = null;
        return null;
    }

    public Type visit(ClassDeclExtends c) {
        currentClassTable = mainTable.getMap().get(c.getClassName().getS());

        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        currentClassTable = null;
        return null;
    }

    public Type visit(Program p) {
        p.getMainClass().accept(this);
        p.getClasses().getClassDecls().forEach(clazz -> clazz.accept(this));
        return null;
    }

    public Type visit(MethodDecl m) {
        currentMethodTable = currentClassTable.getMethodsContext().get(m.getIdentifier());

        m.getStatements().getStatements().forEach(stm -> stm.accept(this));
        Type returnType = m.getReturnExpression().accept(this);
        Type expectedReturnType = m.getType();

        if (!(returnType.equals(expectedReturnType))) {
            addError("Method " + m.getIdentifier() + " return type didn't match");
        }

        currentMethodTable = null;
        return null;
    }

    public Type visit(VarDecl v) {
        return null;
    }

    public Type visit(Formal f) {
        return null;
    }

    private Type getTypeFromMethodLocals(String localName) {
        if (currentMethodTable == null)
            return null;
        return currentMethodTable.getLocalsContext().get(localName);
    }

    private Type getTypeFromMethodParams(String paramName) {
        if (currentMethodTable == null)
            return null;
        return currentMethodTable.getParamsContext().get(paramName);
    }

    private Type getTypeFromClassFields(String classField) {
        if (currentClassTable == null)
            return null;
        return currentClassTable.getFieldsContext().get(classField);
    }

    private Type getTypeFromMainTable(String className) {
        ClassTable table = mainTable.getMap().get(className);
        if (table == null) {
            return null;
        }

        return new IdentifierType(table.getClassName());
    }

    private Type getTypeFromEntireClassContextInsideAMethod(String name) {
        Type type = getTypeFromMethodLocals(name);
        if (type != null)
            return type;

        type = getTypeFromMethodParams(name);
        if (type != null)
            return type;

        type = getTypeFromClassFields(name);
        if (type != null)
            return type;

        type = getTypeFromMainTable(name);
        return type;
    }

    private boolean checkIfIntArrayType(String name, Type anotherType) {
        boolean ok = true;
        if (!(anotherType instanceof IntArrayType)) {
            String s = anotherType != null ? anotherType.toString() : "null";
            addError(name + " is not an instance of IntArrayType, but " + s);
            ok = false;
        }
        return ok;
    }

    private boolean checkIfIntegerType(String name, Type anotherType) {
        boolean ok = true;
        if (!(anotherType instanceof IntegerType)) {
            String s = anotherType != null ? anotherType.toString() : "null";
            addError(name + " is not an instance of IntegerType, but " + s);
            ok = false;
        }
        return ok;
    }

    private boolean checkIfBooleanType(String name, Type anotherType) {
        boolean ok = true;
        if (!(anotherType instanceof BooleanType)) {
            ok = false;
            String s = anotherType != null ? anotherType.toString() : "null";
            addError(name + " is not an instance of BooleanType, but " + s);
        }
        return ok;
    }

    private boolean checkBinaryOperation(String name, Type lheType, Type rheType) {
        if (lheType instanceof IntegerType && rheType instanceof IntegerType) {
            return true;
        }

        checkIfIntegerType("Left Hand Expression of " + name + " ", lheType);
        checkIfIntegerType("Right Hand Expression of " + name + " ", rheType);

        return false;
    }

    private boolean checkIfTypeMatchWithInheritance(Type argType, Type expectedType) {
        while (argType instanceof IdentifierType type && !argType.equals(expectedType)) {
            ClassTable classTable = mainTable.getMap().get(type.getS());
            if (classTable == null || classTable.getParent() == null) {
                return false;
            }

            String parentClassName = classTable.getParent().getClassName();
            argType = new IdentifierType(parentClassName);
        }

        if (!(argType instanceof IdentifierType)) {
            return false;
        }

        return true;
    }

    private void addError(String message) {
        errors.add(new TypeCheckingException(message));
    }

    public boolean isSemanticsOk(Program program) {
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
        program.accept(symbolTableVisitor);

        if (!symbolTableVisitor.getErrors().isEmpty()) {
            log.error("Symbol errors {}", symbolTableVisitor.getErrors());
            return false;
        }

        mainTable = symbolTableVisitor.getMainTable();
        program.accept(this);
        if (!errors.isEmpty()) {
            log.error("Type errors {}", errors);
            return false;
        }

        return true;
    }

    public void isSemanticsOkOrThrow(Program program) throws SemanticAnalysisException {
        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
        program.accept(symbolTableVisitor);

        if (!symbolTableVisitor.getErrors().isEmpty()) {
            log.error("Symbol errors {}", symbolTableVisitor.getErrors());
            throw new SemanticAnalysisException("Symbol Exception");
        }

        mainTable = symbolTableVisitor.getMainTable();
        program.accept(this);

        if (!errors.isEmpty()) {
            log.error("Type errors {}", errors);
            throw new SemanticAnalysisException("Type Exception");
        }
    }
}
