package org.example.visitor.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.*;
import org.example.frame.Frame;
import org.example.irtree.*;
import org.example.temp.Label;
import org.example.visitor.Visitor;
import org.example.visitor.symbols.ClassTable;
import org.example.visitor.symbols.MainTable;
import org.example.visitor.symbols.MethodTable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Log4j2
public class IRTreeVisitor implements Visitor<Exp> {
    private Frame frame;
    private Frag frag;
    private Frag initialFrag;
    @Builder.Default
    private List<ExpAbstract> listExp = new ArrayList<>();
    @Builder.Default
    private MainTable mainTable = new MainTable();
    @Builder.Default
    private ClassTable currentClassTable = null;
    @Builder.Default
    private MethodTable currentMethodTable = null;
    @Builder.Default
    private int currentIfCount = -1;

    public IRTreeVisitor(MainTable mainTable, Frame frame) {
        this.mainTable = mainTable;
        this.frame = frame;
        this.frag = new Frag(null);
        this.initialFrag = this.frag;
        this.listExp = new ArrayList<>();
    }

    public Exp visit(And a) {
        var lheExpr = a.getLhe().accept(this);
        var rheExpr = a.getRhe().accept(this);

        var andBinop = BINOP.builder()
                .binop(BINOP.AND)
                .left(lheExpr.unEx()).right(rheExpr.unEx())
                .build();

        addExp(andBinop);
        return new Exp(andBinop);
    }

    public Exp visit(BooleanType b) {
        return null;
    }

    public Exp visit(Not n) {
        var exp = n.getE().accept(this);

        var notBinop = BINOP.builder()
                .binop(BINOP.MINUS)
                .left(new CONST(1)).right(exp.unEx())
                .build();
        addExp(notBinop);
        return new Exp(notBinop);
    }

    public Exp visit(True t) {
        var constExpr = CONST.builder().value(1).build();
        addExp(constExpr);
        return new Exp(constExpr);
    }

    public Exp visit(False f) {
        var constExpr = CONST.builder().value(0).build();
        addExp(constExpr);
        return new Exp(constExpr);
    }

    public Exp visit(Identifier i) {
        var allocLocal = frame.allocLocal(false);
        var tempReg = allocLocal.exp(new TEMP(frame.FP()));
        addExp(tempReg);
        return new Exp(tempReg);
    }

    public Exp visit(Call c) {
        ClassTable tmpClassTable = null;
        MethodTable tmpMethodTable = null;
        String tmpClassSymbol = null;
        ExpList expList = null;

        int size = c.getExpressionList().getList().size();

        for (int i = size - 1; i >= 0; i--) {
            var expr = c.getExpressionList().getList().get(i).accept(this);
            expList = new ExpList(expr.unEx(), expList);
        }

        expList = new ExpList(c.getOwner().accept(this).unEx(), expList);

        if (c.getOwner() instanceof Call) {
            tmpClassTable = currentClassTable;

            tmpMethodTable = tmpClassTable.getMethodsContext().get(c.getMethod().getS());

            tmpClassTable = mainTable.getMap().get(tmpMethodTable.getClassParent().getClassName());
        }

        if (c.getOwner() instanceof IdentifierExpression idExp) {
            if (currentMethodTable.getLocalsContext().get(idExp.getId()) instanceof IdentifierType idType) {
                tmpClassSymbol = idType.getS();
            } else if (currentMethodTable.getParamsContext().get(idExp.getId()) instanceof IdentifierType idType)
                tmpClassSymbol = idType.getS();
            else if (currentClassTable.getFieldsContext().get(idExp.getId()) instanceof IdentifierType idType) {
                tmpClassSymbol = idType.getS();
            }
        }

        if (c.getOwner() instanceof NewObject idNewObject) {
            tmpClassTable = mainTable.getMap().get(idNewObject.getIdentifier().getS());
        }
        if (c.getOwner() instanceof This) tmpClassTable = currentClassTable;
        if (tmpClassTable != null) tmpClassSymbol = tmpClassTable.getClassName();

        var label = new Label(tmpClassSymbol + "." + c.getMethod().getS());

        var callExpr = CALL.builder()
                .func(new NAME(label))
                .args(expList)
                .build();
        addExp(callExpr);
        return new Exp(callExpr);
    }

    public Exp visit(IdentifierExpression i) {
        var allocLocal = frame.allocLocal(false);
        var tempReg = allocLocal.exp(new TEMP(frame.FP()));
        addExp(tempReg);
        return new Exp(tempReg);
    }

    public Exp visit(IdentifierType i) {
        return null;
    }

    public Exp visit(NewObject n) {
        currentClassTable = mainTable.getMap().get(n.getIdentifier().getS());
        int sizeOfFields = mainTable.getMap().get(n.getIdentifier().getS()).getFieldsContext().size();

        var parametersList = new LinkedList<ExpAbstract>();
        parametersList.add(new BINOP(BINOP.MUL, new CONST(sizeOfFields + 1), new CONST(frame.wordSize())));

        var externalCall = frame.externalCall("malloc", parametersList);
        addExp(externalCall);
        return new Exp(externalCall);
    }

    public Exp visit(This t) {
        var tempAllocation = MEM.builder()
                .exp(new TEMP(frame.FP()))
                .build();
        addExp(tempAllocation);
        return new Exp(tempAllocation);
    }

    public Exp visit(ArrayLookup a) {
        var idx = a.getIdx().accept(this);
        var array = a.getArray().accept(this);

        var arrayLoopUp = MEM.builder()
                .exp(
                        BINOP.builder()
                                .binop(BINOP.PLUS)
                                .left(array.unEx())
                                .right(
                                        BINOP.builder()
                                                .binop(BINOP.MUL)
                                                .left(idx.unEx())
                                                .right(new CONST(frame.wordSize()))
                                                .build()
                                )
                                .build()
                )
                .build();
        addExp(arrayLoopUp);
        return new Exp(arrayLoopUp);
    }

    public Exp visit(ArrayAssign a) {
        var id = a.getIdentifier().accept(this);
        var idx = a.getIndex().accept(this);
        var val = a.getValue().accept(this);

        // TODO: See if this shit goes down because first element is array size my dude
        var offset = BINOP.builder()
                .binop(BINOP.MUL)
                .left(idx.unEx())
                .right(new CONST(frame.wordSize()))
                .build();
        var pointer = BINOP.builder()
                .binop(BINOP.PLUS)
                .left(BINOP.builder()
                        .binop(BINOP.PLUS)
                        .left(id.unEx()).right(new CONST(1))
                        .build()
                )
                .right(offset)
                .build();

        var move = new MOVE(new MEM(pointer), val.unEx());

        var arrayAssign = ESEQ.builder()
                .stm(move)
                .exp(new CONST(0))
                .build();
        addExp(arrayAssign);
        return new Exp(arrayAssign);
    }

    public Exp visit(ArrayLength a) {
        var pointer = a.getArray().accept(this);

        var tempAlloc = MEM.builder()
                .exp(pointer.unEx())
                .build();
        addExp(tempAlloc);
        return new Exp(tempAlloc);
    }

    public Exp visit(Plus p) {
        var lheExpr = p.getLhe().accept(this);
        var rheExpr = p.getRhe().accept(this);

        var plusBinop = BINOP.builder()
                .binop(BINOP.PLUS)
                .left(lheExpr.unEx())
                .right(rheExpr.unEx())
                .build();
        addExp(plusBinop);
        return new Exp(plusBinop);
    }

    public Exp visit(Minus m) {
        var lheExpr = m.getLhe().accept(this);
        var rheExpr = m.getRhe().accept(this);

        var minusBinop = BINOP.builder()
                .binop(BINOP.MINUS)
                .left(lheExpr.unEx())
                .right(rheExpr.unEx())
                .build();
        addExp(minusBinop);
        return new Exp(minusBinop);
    }

    public Exp visit(Times t) {
        var lheExpr = t.getLhe().accept(this);
        var rheExpr = t.getRhe().accept(this);

        var timeBinop = BINOP.builder()
                .binop(BINOP.MUL)
                .left(lheExpr.unEx())
                .right(rheExpr.unEx())
                .build();
        addExp(timeBinop);
        return new Exp(timeBinop);
    }

    public Exp visit(IntegerLiteral i) {
        var integerValue = CONST.builder().value(i.getValue()).build();
        addExp(integerValue);
        return new Exp(integerValue);
    }

    public Exp visit(IntegerType i) {
        return null;
    }

    public Exp visit(IntArrayType i) {
        return null;
    }

    public Exp visit(LessThan l) {
        var lheExpr = l.getLhe().accept(this);
        var rheExpr = l.getRhe().accept(this);

        var lessThanBinop = BINOP.builder()
                .binop(BINOP.MINUS)
                .left(lheExpr.unEx())
                .right(rheExpr.unEx())
                .build();
        addExp(lessThanBinop);
        return new Exp(lessThanBinop);
    }

    public Exp visit(NewArray n) {
        var newArraySize = n.getSize().accept(this);
        var parametersList = new LinkedList<ExpAbstract>();

        // mem size to alloc
        var exp = new BINOP(
                BINOP.MUL,
                new BINOP(BINOP.PLUS, newArraySize.unEx(), new CONST(1)),
                new CONST(frame.wordSize())
        );

        parametersList.add(exp);

        var arrayAlloc = frame.externalCall("initArray", parametersList);
        addExp(arrayAlloc);
        return new Exp(arrayAlloc);
    }

    public Exp visit(While w) {
        var cond = w.getCondition().accept(this).unEx();
        var stm = new EXP(w.getBody().accept(this).unEx());

        var loopLabel = new Label();
        var endLabel = new Label();
        var bodyLabel = new Label();

        var bodyStm = new SEQ(stm, new LABEL(bodyLabel));
        var jump = new JUMP(endLabel);
        var bodyJump = new SEQ(bodyStm, jump);

        // LessThan: uses Minus in your implementation BINOP
        var cjump = CJUMP.builder()
                .relop(CJUMP.GT)
                .left(cond)
                .right(new CONST(1))
                .condTrue(bodyLabel)
                .condFalse(endLabel)
                .build();

        var whileStatement = SEQ.builder()
                .left(
                        SEQ.builder()
                                .left(
                                        SEQ.builder()
                                                .left(new LABEL(loopLabel))
                                                .right(cjump)
                                                .build()
                                )
                                .right(bodyJump)
                                .build()
                )
                .right(new LABEL(endLabel))
                .build();


        var whileEseq = new ESEQ(whileStatement, null);
        addExp(whileEseq);
        return new Exp(whileEseq);
    }

    public Exp visit(If i) {
        currentIfCount++;
        var trueLabel = new Label("if_true_" + currentIfCount);
        var falseLabel = new Label("if_false_" + currentIfCount);
        var endLabel = new Label("if_end_" + currentIfCount);

        var condExpr = i.getCondition().accept(this);
        var trueStmt = new EXP(i.getThenBranch().accept(this).unEx());
        var falseStmt = new EXP(i.getElseBranch().accept(this).unEx());

        var thenStatement = new SEQ(
            new SEQ(new LABEL(trueLabel), trueStmt),
            new JUMP(endLabel)
        );
        var elseStatement = new SEQ(
            new SEQ(new LABEL(falseLabel), falseStmt),
            new JUMP(endLabel)
        );
        var thenElseStmt = new SEQ(thenStatement, elseStatement);

        var condStmt = CJUMP.builder()
                .relop(CJUMP.EQ)
                .left(new CONST(1))
                .right(condExpr.unEx())
                .condTrue(trueLabel)
                .condFalse(falseLabel)
                .build();

        var ifStmt = new SEQ(condStmt, thenElseStmt);

        // sera que precisa desse ESEQ? IDK you tell me
        var ifESEQ = new ESEQ(new SEQ(ifStmt, new LABEL(endLabel)), null);
        addExp(ifESEQ);
        return new Exp(ifESEQ);
    }

    public Exp visit(Assign a) {
        var id = a.getIdentifier().accept(this);
        var value = a.getValue().accept(this);

        var move = new MOVE(id.unEx(), value.unEx());

        var assignEseq = new ESEQ(move, new CONST(0));
        addExp(assignEseq);
        return new Exp(assignEseq);
    }

    public Exp visit(Sout s) {
        var argsList = new LinkedList<ExpAbstract>();
        var exp = s.getExpression().accept(this);
        argsList.add(exp.unEx());

        var call = frame.externalCall("print", argsList);
        addExp(call);
        return new Exp(call);
    }

    public Exp visit(Block b) {
        var size = b.getStatements().getStatements().size();
        ExpAbstract expBlock = new CONST(0);

        for (int i = 0; i < size; i++) {
            var expr = new EXP(b.getStatements().getStatements().get(i).accept(this).unEx());

            expBlock = new ESEQ(new SEQ(new EXP(expBlock), expr), new CONST(0));
        }

        addExp(expBlock);
        return new Exp(expBlock);
    }

    public Exp visit(MainClass m) {
        currentClassTable = mainTable.getMap().get(m.getClassName().getS());
        currentMethodTable = currentClassTable.getMethodsContext().get("main");

        Stm stmBody = null;
        var stmList = new ArrayList<Stm>();
        var escapeList = new ArrayList<Boolean>();
        escapeList.add(false);
        frame = frame.newFrame("main", escapeList);

        var size = m.getStatements().getStatements().size();
        for (int i = 0; i < size; i++) {
            var stm = m.getStatements().getStatements().get(i).accept(this);
            stmBody = new EXP(stm.unEx());
            stmList.add(stmBody);
        }

        frame.procEntryExit1(stmList);
        frag.setNext(new ProcFrag(stmList.getFirst(), frame));
        frag = frag.getNext();

        currentClassTable = null;
        currentMethodTable = null;
        return null;
    }

    public Exp visit(ClassDeclSimple c) {
        currentClassTable = mainTable.getMap().get(c.getClassName().getS());

        c.getClassName().accept(this);

        c.getFields().getVarDecls().forEach(field -> field.accept(this));
        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        currentClassTable = null;
        return null;
    }

    public Exp visit(ClassDeclExtends c) {
        currentClassTable = mainTable.getMap().get(c.getClassName().getS());

        c.getClassName().accept(this);
        c.getParent().accept(this);

        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));
        c.getFields().getVarDecls().forEach(field -> field.accept(this));

        currentClassTable = null;
        return null;
    }

    public Exp visit(Program p) {
        p.getMainClass().accept(this);
        p.getClasses().getClassDecls().forEach(classDecl -> classDecl.accept(this));
        return null;
    }

    public Exp visit(MethodDecl m) {
        currentMethodTable = currentClassTable.getMethodsContext().get(m.getIdentifier());

        Stm stmBody = new EXP(new CONST(0));
        var escapeList = new ArrayList<Boolean>();
        int sizeFormals = m.getFormals().getFormals().size();
        int sizeStatement = m.getStatements().getStatements().size();

        for (int i = 0; i <= sizeFormals; i++) {
            escapeList.add(false);
        }


        frame = frame.newFrame(
                currentClassTable.getClassName() + "$" + currentMethodTable.getMethodName(),
                escapeList
        );

        m.getFormals().getFormals().forEach(formal -> formal.accept(this));
        m.getVarDecls().getVarDecls().forEach(varDecl -> varDecl.accept(this));

        for (int i = 0; i < sizeStatement; i++)
            stmBody = new SEQ(
                    stmBody,
                    new EXP(
                            m.getStatements().getStatements().get(i).accept(this).unEx()
                    )
            );

        var stmList = new ArrayList<Stm>();
        stmList.add(stmBody);

        frame.procEntryExit1(stmList);
        frag.setNext(new ProcFrag(stmBody, frame));
        frag = frag.getNext();


        currentMethodTable = null;
        return null;
    }

    public Exp visit(VarDecl v) {
        return null;
    }

    public Exp visit(Formal f) {
        frame.allocLocal(false);
        return null;
    }

    public void addExp(ExpAbstract exp) {
        listExp.add(exp);
    }
}
