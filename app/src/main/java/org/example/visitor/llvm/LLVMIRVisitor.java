package org.example.visitor.llvm;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.ast.*;
import org.example.ast.BooleanType;
import org.example.ast.IntegerType;
import org.example.llvm.*;
import org.example.llvm.Type;
import org.example.visitor.Visitor;
import org.example.visitor.symbols.ClassTable;
import org.example.visitor.symbols.MainTable;
import org.example.visitor.symbols.MethodTable;

import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
@Builder
public class LLVMIRVisitor implements Visitor<Value> {
    private MainTable mainTable;
    private ClassTable currentClassTable;
    private MethodTable currentMethodTable;

    private IRProgram program;
    private Function currentFunction;
    private BasicBlock currentBlock;
    @Builder.Default
    private int currentVirtualRegister = -1;
    @Builder.Default
    private HashMap<String, Integer> labelTable = new HashMap<>();
    private ClassType currentClassStructuredType;
    @Builder.Default
    private HashMap<Value, ClassType> objectRegisters = new HashMap<>();
    @Builder.Default
    private HashMap<String, Value> methodVariableTracker = new HashMap<>();

    public Value visit(And a) {
        Value leftValue = a.getLhe().accept(this);
        Value rightValue = a.getRhe().accept(this);

        var i = AndInstruction.builder()
            .leftOperand(leftValue)
            .rightOperand(rightValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(BooleanType b) {
        return null;
    }

    public Value visit(Not n) {
        Value currentValue = n.getE().accept(this);
        var i = NotInstruction.builder()
            .operand(currentValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(True t) {
        return new Value("1");
    }

    public Value visit(False f) {
        return new Value("0");
    }

    public Value visit(Identifier i) {
        return processIdentifier(i.getS());
    }

    public Value visit(Call c) {
        Value object = c.getOwner().accept(this);

        ClassType objectClass;
        if (object.getV().equals("this")) {
            objectClass = currentClassStructuredType;
        } else {
            objectClass = objectRegisters.get(object);
        }

        Function method = objectClass.getMethods().get(c.getMethod().getS());
        var callArgs = new LinkedHashMap<Value, Type>();

        List<Type> params = method.getArguments().values().stream().toList();
        List<Expression> args = c.getExpressionList().getList();
        for (int i = 0; i < method.getArguments().size(); i++) {
            Value arg = args.get(i).accept(this);
            Type param = params.get(i);

            if (param instanceof ClassType) {
                var fromType = objectRegisters.get(arg);
                var bitcast = BitcastInstruction.builder()
                    .value(arg)
                    .fromType(fromType)
                    .toType(param)
                    .result(getNewVirtualRegister())
                    .build();

                arg = bitcast.getResult();
                currentBlock.addInstruction(bitcast);
            }

            callArgs.put(arg, param);
        }

        var call = CallInstruction.builder()
            .functionCalled(method)
            .args(callArgs)
            .returnType(method.getReturnType())
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(call);
        return call.getResult();
    }

    public Value visit(IdentifierExpression i) {
        return processIdentifier(i.getId());
    }

    public Value visit(IdentifierType i) {
        return null;
    }

    public Value visit(NewObject n) {
        var type = program.getStructuredTypes().get("%" + n.getIdentifier());
        var alloca = AllocaInstruction.builder()
            .type(type)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(alloca);
        objectRegisters.put(alloca.getResult(), (ClassType) type);

        return alloca.getResult();
    }

    public Value visit(This t) {
        return new Value("%this");
    }

    public Value visit(ArrayLookup a) {
        Value pointer = a.getArray().accept(this);
        Value idx = a.getIdx().accept(this);

        var offset = new Offset();
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("0"));
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("1"));
        offset.addOffset(new org.example.llvm.IntegerType(), idx);

        var gep = GetElementPointerInstruction.builder()
            .baseType(new ArrayType())
            .baseAddress(pointer)
            .result(getNewVirtualRegister())
            .offset(offset)
            .build();
        currentBlock.addInstruction(gep);

        var load = LoadInstruction.builder()
            .type(new org.example.llvm.IntegerType())
            .pointer(gep.getResult())
            .result(getNewVirtualRegister())
            .build();
        currentBlock.addInstruction(load);

        return load.getResult();
    }

    public Value visit(ArrayAssign a) {
        Value arrayStartPointer = a.getIdentifier().accept(this);
        Value idx = a.getIndex().accept(this);

        var offset = new Offset();
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("0"));
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("1"));
        offset.addOffset(new org.example.llvm.IntegerType(), idx);

        var elementPointer = GetElementPointerInstruction.builder()
            .baseType(new ArrayType())
            .baseAddress(arrayStartPointer)
            .result(getNewVirtualRegister())
            .offset(offset)
            .build();
        currentBlock.addInstruction(elementPointer);

        Value value = a.getValue().accept(this);

        var store = StoreInstruction.builder()
            .type(new org.example.llvm.IntegerType())
            .pointer(elementPointer.getResult())
            .value(value)
            .build();
        currentBlock.addInstruction(store);
        return null;
    }

    public Value visit(ArrayLength a) {
        Value pointer = a.getArray().accept(this);
        var offset = new Offset();
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("0"));

        var gep = GetElementPointerInstruction.builder()
            .baseType(new ArrayType())
            .baseAddress(pointer)
            .result(getNewVirtualRegister())
            .offset(offset)
            .build();
        currentBlock.addInstruction(gep);

        LoadInstruction load = LoadInstruction.builder()
            .type(new org.example.llvm.IntegerType())
            .pointer(gep.getResult())
            .result(getNewVirtualRegister())
            .build();
        currentBlock.addInstruction(load);

        return load.getResult();
    }

    public Value visit(Plus p) {
        Value leftValue = p.getLhe().accept(this);
        Value rightValue = p.getRhe().accept(this);

        AddInstruction i = AddInstruction.builder()
            .leftOperand(leftValue)
            .rightOperand(rightValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(Minus m) {
        Value leftValue = m.getLhe().accept(this);


        Value rightValue = m.getRhe().accept(this);

        MinusInstruction i = MinusInstruction.builder()
            .leftOperand(leftValue)
            .rightOperand(rightValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(Times t) {
        Value leftValue = t.getLhe().accept(this);
        Value rightValue = t.getRhe().accept(this);

        MulInstruction i = MulInstruction.builder()
            .leftOperand(leftValue)
            .rightOperand(rightValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(IntegerLiteral i) {
        return new Value(Integer.toString(i.getValue()));
    }

    public Value visit(IntegerType i) {
        return null;
    }

    public Value visit(IntArrayType i) {
        return null;
    }

    public Value visit(LessThan l) {
        Value leftValue = l.getLhe().accept(this);
        Value rightValue = l.getRhe().accept(this);

        var i = LessThanInstruction.builder()
            .leftOperand(leftValue)
            .rightOperand(rightValue)
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(i);
        return i.getResult();
    }

    public Value visit(NewArray n) {
        Value size = n.getSize().accept(this);
        var allocObj = AllocaInstruction.builder()
            .type(new ArrayType())
            .result(getNewVirtualRegister())
            .build();
        currentBlock.addInstruction(allocObj);

        var storeSize = StoreInstruction.builder()
            .type(new org.example.llvm.IntegerType())
            .value(size)
            .pointer(allocObj.getResult())
            .build();
        currentBlock.addInstruction(storeSize);

        var allocArray = AllocaInstruction.builder()
            .type(new IntPointerType())
            .result(getNewVirtualRegister())
            .build();
        currentBlock.addInstruction(allocArray);

        var offset = new Offset();
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("0"));
        offset.addOffset(new org.example.llvm.IntegerType(), new Value("1"));
        var vecPointer = GetElementPointerInstruction.builder()
            .baseType(new ArrayType())
            .baseAddress(allocObj.getResult())
            .result(getNewVirtualRegister())
            .offset(offset)
            .build();
        currentBlock.addInstruction(vecPointer);

        var storeArray = StoreInstruction.builder()
            .type(new IntPointerType())
            .value(allocArray.getResult())
            .pointer(vecPointer.getResult())
            .build();
        currentBlock.addInstruction(storeArray);

        return allocObj.getResult();
    }

    public Value visit(While w) {
        Value checkLabel = getNewLabel("while_check_");
        Value bodyLabel = getNewLabel("while_body_");
        Value endLabel = getNewLabel("while_end_");

        var branchWhileCheck = new BranchInstruction(checkLabel);
        currentBlock.setTerminalInstruction(branchWhileCheck);

        var whileCheck = BasicBlock.builder().label(checkLabel).build();
        currentBlock = whileCheck;
        currentFunction.addBlock(whileCheck);

        Value conditionValue = w.getCondition().accept(this);
        var isConditionTrue = checkIfConditionIsTrue(conditionValue);
        var branchInstruction = BranchInstruction.builder()
            .condition(isConditionTrue)
            .trueLabel(bodyLabel)
            .falseLabel(endLabel)
            .build();
        currentBlock.setTerminalInstruction(branchInstruction);

        currentBlock = BasicBlock.builder().label(bodyLabel).build();
        currentFunction.addBlock(currentBlock);
        w.getBody().accept(this);

        // It can change
        currentBlock.setTerminalInstruction(new BranchInstruction(checkLabel));

        currentBlock = BasicBlock.builder().label(endLabel).build();
        currentFunction.addBlock(currentBlock);
        return currentBlock.getLabel();
    }

    public Value visit(If i) {
        Value conditionValue = i.getCondition().accept(this);
        var equalsInstruction = EqualsInstruction.builder()
            .type(new org.example.llvm.BooleanType())
            .leftOperand(conditionValue)
            .rightOperand(new Value("1"))
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(equalsInstruction);

        Value isConditionTrue = equalsInstruction.getResult();
        Value thenLabel = getNewLabel("if_true_");
        Value elseLabel = getNewLabel("if_false_");
        Value ifEnd = getNewLabel("if_end_");

        var branchInstruction = BranchInstruction.builder()
            .condition(isConditionTrue)
            .falseLabel(elseLabel)
            .trueLabel(thenLabel)
            .build();

        currentBlock.setTerminalInstruction(branchInstruction);

        var thenBlock = BasicBlock.builder()
            .label(thenLabel)
            .build();
        currentBlock = thenBlock;
        currentFunction.addBlock(thenBlock);

        i.getThenBranch().accept(this);
        currentBlock.setTerminalInstruction(new BranchInstruction(ifEnd));

        var elseBlock = BasicBlock.builder()
            .label(elseLabel)
            .build();
        currentBlock = elseBlock;
        currentFunction.addBlock(elseBlock);

        i.getElseBranch().accept(this);
        currentBlock.setTerminalInstruction(new BranchInstruction(ifEnd));

        currentBlock = BasicBlock.builder()
            .label(ifEnd)
            .build();

        currentFunction.addBlock(currentBlock);
        return null;
    }

    public Value visit(Assign a) {
        Value exprValue = a.getValue().accept(this);
        var assignInstruction = StoreInstruction.builder().value(exprValue);
        Identifier id = a.getIdentifier();
        Value pointer = id.accept(this);
        assignInstruction.pointer(pointer);
        Type type = null;

        if (isIdentifierInsideCurrentMethodScope(id)) {
            type = convertASTTypeToLLVMType(currentMethodTable.getTypeOfMethodVariable(id.getS()));
        } else {
            type = convertASTTypeToLLVMType(currentClassTable.getFieldsContext().get(id.getS()));
        }

        assignInstruction.type(type);
        currentBlock.addInstruction(assignInstruction.build());
        return null;
    }

    public Value visit(Sout s) {
        Value value = s.getExpression().accept(this);

        var f = Function.builder().name("print_int").build();

        LinkedHashMap<Value, Type> args = new LinkedHashMap<>();
        args.put(value, new org.example.llvm.IntegerType());
        var printInstruction = CallInstruction.builder()
            .functionCalled(f)
            .args(args)
            .build();
        currentBlock.addInstruction(printInstruction);
        return null;
    }

    public Value visit(Block b) {
        b.getStatements().getStatements().forEach(s -> s.accept(this));
        return null;
    }

    public Value visit(MainClass m) {
        Function f = Function.builder()
            .name("main")
            .returnType(new org.example.llvm.IntegerType())
            .build();

        program.addFunction(f);
        currentFunction = f;

        BasicBlock entryBlock = BasicBlock.builder()
            .terminalInstruction(new RetInstruction(new org.example.llvm.BooleanType(), new Value("0")))
            .build();

        f.setEntryBlock(entryBlock);
        currentBlock = entryBlock;

        m.getStatements().getStatements().forEach(stm -> stm.accept(this));

        // Default Structs
        var array = new ArrayType();

        program.addStructuredTypes(array);

        resetClassScope();
        return null;
    }

    public Value visit(ClassDeclSimple c) {
        currentClassTable = mainTable.getMap().get(c.getClassName().getS());

        int currentAggregate = 0;
        LinkedHashMap<String, Pair<Type, Offset>> currentClassFields = new LinkedHashMap<>();

        for (Map.Entry<String, org.example.ast.Type> field : currentClassTable.getFieldsContext().entrySet()) {
            String name = field.getKey();
            org.example.ast.Type type = field.getValue();

            var offset = new Offset();
            offset.addOffset(new org.example.llvm.IntegerType(), new Value("0"));
            offset.addOffset(new org.example.llvm.IntegerType(), new Value(Integer.toString(currentAggregate)));

            var llvmType = convertASTTypeToLLVMType(type);
            currentClassFields.put(name, new Pair<>(llvmType, offset));
            if (llvmType instanceof ClassType clazz) {
                objectRegisters.put(new Value(name), clazz);
            }
            currentAggregate++;
        }

        var structuredType = ClassType.builder()
            .name(currentClassTable.getClassName())
            .fields(currentClassFields)
            .build();
        currentClassStructuredType = structuredType;
        program.addStructuredTypes(structuredType);

        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        resetClassScope();
        return null;
    }

    public Value visit(ClassDeclExtends c) {
        return null;
    }

    public Value visit(Program p) {
        p.getMainClass().accept(this);
        p.getClasses().getClassDecls().forEach(clazz -> clazz.accept(this));
        return null;
    }

    public Value visit(MethodDecl m) {
        currentMethodTable = currentClassTable.getMethodsContext().get(m.getIdentifier());

        String methodName = String.format("%s_%s", currentClassTable.getClassName(),
            currentMethodTable.getMethodName());

        LinkedHashMap<Value, Type> args = new LinkedHashMap<>();
        args.put(new Value("%this"), currentClassStructuredType);

        for (Formal formal : m.getFormals().getFormals()) {
            String name = formal.getName();
            org.example.ast.Type type = formal.getType();
            args.put(new Value("%" + name), convertASTTypeToLLVMType(type));
        }

        var bb = new BasicBlock();
        currentBlock = bb;

        for (var local : currentMethodTable.getLocalsContext().entrySet()) {
            var alloca = AllocaInstruction.builder()
                .type(convertASTTypeToLLVMType(local.getValue()))
                .result(new Value(local.getKey()))
                .build();

            bb.addInstruction(alloca);
        }

        var fn = Function.builder()
            .name(methodName)
            .arguments(args)
            .entryBlock(bb)
            .build();

        currentFunction = fn;
        program.addFunction(fn);
        currentClassStructuredType.addMethod(m.getIdentifier(), fn);

        m.getStatements().getStatements().forEach(s -> s.accept(this));

        currentVirtualRegister = -1;
        labelTable = new HashMap<>();
        currentFunction = null;
        currentMethodTable = null;
        return null;
    }

    public Value visit(VarDecl v) {
        return null;
    }

    public Value visit(Formal f) {
        return null;
    }

    private Value getNewVirtualRegister() {
        currentVirtualRegister++;
        return new Value("%" + currentVirtualRegister);
    }

    private Value getNewLabel(String l) {
        var counter = labelTable.getOrDefault(l, -1);
        counter++;
        labelTable.put(l, counter);
        return new Value(l + counter);
    }

    private Value checkIfConditionIsTrue(Value v) {
        var equalsInstruction = EqualsInstruction.builder()
            .type(new org.example.llvm.BooleanType())
            .leftOperand(v)
            .rightOperand(new Value("1"))
            .result(getNewVirtualRegister())
            .build();

        currentBlock.addInstruction(equalsInstruction);

        return equalsInstruction.getResult();
    }

    private boolean isIdentifierInsideCurrentMethodScope(Identifier id) {
        boolean isParam = currentMethodTable.getParamsContext().containsKey(id.getS());
        boolean isLocal = currentMethodTable.getLocalsContext().containsKey(id.getS());

        return isParam || isLocal;
    }

    private Type convertASTTypeToLLVMType(org.example.ast.Type type) {
        switch (type) {
            case IntegerType integerType -> {
                return new org.example.llvm.IntegerType();
            }
            case BooleanType booleanType -> {
                return new org.example.llvm.BooleanType();
            }
            case IntArrayType intArrayType -> {
                return new ArrayType();
            }
            case null, default -> {
                // should be identifier
                var id = ((IdentifierType) type).getS();
                return program.getStructuredTypes().get(id);
            }
        }
    }

    private void resetClassScope() {
        currentVirtualRegister = -1;
        labelTable = new HashMap<>();
        currentMethodTable = null;
        currentBlock = null;
        currentFunction = null;
        currentClassStructuredType = null;
    }

    private Value processIdentifier(String id) {
        if (isIdentifierInsideCurrentMethodScope(new Identifier(id))) {
            return methodVariableTracker.get(id);
        }
        // is a field
        Pair<Type, Offset> pair = currentClassStructuredType.getFields().get(id);

        var elementPointer = GetElementPointerInstruction.builder()
            .baseType(new ArrayType())
            .baseAddress(new Value("%this"))
            .result(getNewVirtualRegister())
            .offset(pair.component2())
            .build();

        var load = LoadInstruction.builder()
            .type(pair.component1())
            .pointer(elementPointer.getResult())
            .result(getNewVirtualRegister())
            .build();

        return load.getResult();
    }
}
