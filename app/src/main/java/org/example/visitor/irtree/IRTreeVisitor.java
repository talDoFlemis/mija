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
    private List<ExpAbstract> listExp;
    @Builder.Default
    private MainTable mainTable = new MainTable();
    @Builder.Default
    private ClassTable currentClassTable = null;
    @Builder.Default
    private MethodTable currentMethodTable = null;

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

        this.addExp(new BINOP(BINOP.AND, lheExpr.unEx(), rheExpr.unEx()));

        return new Exp(new BINOP(BINOP.AND, lheExpr.unEx(), rheExpr.unEx()));
    }

    public Exp visit(BooleanType b) {
        return null;
    }

    public Exp visit(Not n) {
        var exp = n.getE().accept(this);

        this.addExp(new BINOP(BINOP.MINUS, new CONST(1), exp.unEx()));
        return new Exp(new BINOP(BINOP.MINUS, new CONST(1), exp.unEx()));
    }

    public Exp visit(True t) {
        this.addExp(new CONST(1));
        return new Exp(new CONST(1));
    }

    public Exp visit(False f) {
        this.addExp(new CONST(0));
        return new Exp(new CONST(0));
    }

    public Exp visit(Identifier i) {
        var allocLocal = this.frame.allocLocal(false);

        this.addExp(allocLocal.exp(new TEMP(this.frame.FP())));
        return new Exp(allocLocal.exp(new TEMP(this.frame.FP())));
    }

    public Exp visit(Call c) {
        ClassTable tmpClassTable1 = null, tmpClassTable2 = null;
        MethodTable tmpMethodTable = null;
        String tmpClassSymbol = null;
        ExpList expList = null;

        int size;
        size = c.getExpressionList().getList().size();

        for (int i = size - 1; i >= 0; i--)
            expList = new ExpList(c.getExpressionList().getList().get(i).accept(this).unEx(), expList);
        expList = new ExpList(c.getOwner().accept(this).unEx(), expList);
        if (c.getOwner() instanceof Call) {
            tmpClassTable1 = this.currentClassTable;

            tmpClassTable2 = this.mainTable.getMap().get(tmpClassTable1.getClassName());
            tmpMethodTable = tmpClassTable2.getMethodsContext().get(c.getMethod().getS());

            tmpClassTable1 = this.mainTable.getMap().get(tmpMethodTable.getClassParent().getClassName());
        }

        if (c.getOwner() instanceof IdentifierExpression idExp) {
            if (this.currentMethodTable.getLocalsContext().get(idExp.getId()) instanceof IdentifierType idType) {
                tmpClassSymbol = idType.getS();
            } else if (this.currentMethodTable.getParamsContext().get(idExp.getId()) instanceof IdentifierType idType) {
                tmpClassSymbol = idType.getS();
            } else if (this.currentClassTable.getFieldsContext().get(idExp.getId()) instanceof IdentifierType idType) {
                tmpClassSymbol = idType.getS();
            }
        }

        if (c.getOwner() instanceof NewObject idNewObject) {
            tmpClassTable1 = this.mainTable.getMap().get(idNewObject.getIdentifier().getS());
        }
        if (c.getOwner() instanceof This) tmpClassTable1 = this.currentClassTable;
        if (tmpClassTable1 != null) tmpClassSymbol = tmpClassTable1.getClassName();

        var label = new Label(tmpClassSymbol + "." + c.getMethod().getS());

        this.addExp(new CALL(new NAME(label), expList));
        return new Exp(new CALL(new NAME(label), expList));
    }

    public Exp visit(IdentifierExpression i) {
        var allocLocal = this.frame.allocLocal(false);

        this.addExp(allocLocal.exp(new TEMP(this.frame.FP())));
        return new Exp(allocLocal.exp(new TEMP(this.frame.FP())));
    }

    public Exp visit(IdentifierType i) {
        return null;
    }

    public Exp visit(NewObject n) {
        currentClassTable = mainTable.getMap().get(n.getIdentifier().getS());
        int sizeOfHash = this.mainTable.getMap().get(n.getIdentifier().getS()).getFieldsContext().size();

        var parametersList = new LinkedList<ExpAbstract>();
        parametersList.add(new BINOP(BINOP.MUL, new CONST(sizeOfHash + 1), new CONST(this.frame.wordSize())));

        this.addExp(this.frame.externalCall("malloc", parametersList));
        return new Exp(this.frame.externalCall("malloc", parametersList));
    }

    public Exp visit(This t) {
        this.addExp(new MEM(new TEMP(this.frame.FP())));
        return new Exp(new MEM(new TEMP(this.frame.FP())));
    }

    public Exp visit(ArrayLookup a) {
        var idx = a.getIdx().accept(this);
        var array = a.getArray().accept(this);

        this.addExp(new MEM(
                new BINOP(
                        BINOP.PLUS,
                        array.unEx(),
                        new BINOP(
                                BINOP.MUL,
                                idx.unEx(),
                                new CONST(this.frame.wordSize())
                        )
                )
        ));
        return new Exp(
                new MEM(
                        new BINOP(
                                BINOP.PLUS,
                                array.unEx(),
                                new BINOP(
                                        BINOP.MUL,
                                        idx.unEx(),
                                        new CONST(this.frame.wordSize())
                                )
                        )
                )
        );
    }

    public Exp visit(ArrayAssign a) {
        var id = a.getIdentifier().accept(this);
        var idx = a.getIndex().accept(this);
        var val = a.getValue().accept(this);

        var mul = new BINOP(BINOP.MUL, idx.unEx(), new CONST(this.frame.wordSize()));
        var plus = new BINOP(BINOP.PLUS, id.unEx(), mul);

        var move = new MOVE(new MEM(plus), val.unEx());

        this.addExp(new ESEQ(move, new CONST(0)));

        return new Exp(new ESEQ(move, new CONST(0)));
    }

    public Exp visit(ArrayLength a) {
        var array = a.getArray().accept(this);

        this.addExp(new MEM(array.unEx()));

        return new Exp(new MEM(array.unEx()));
    }

    public Exp visit(Plus p) {
        var lheExpr = p.getLhe().accept(this);
        var rheExpr = p.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.PLUS, lheExpr.unEx(), rheExpr.unEx()));
        return new Exp(new BINOP(BINOP.PLUS, lheExpr.unEx(), rheExpr.unEx()));
    }

    public Exp visit(Minus m) {
        var lheExpr = m.getLhe().accept(this);
        var rheExpr = m.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MINUS, lheExpr.unEx(), rheExpr.unEx()));
        return new Exp(new BINOP(BINOP.MINUS, lheExpr.unEx(), rheExpr.unEx()));
    }

    public Exp visit(Times t) {
        var lheExpr = t.getLhe().accept(this);
        var rheExpr = t.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MUL, lheExpr.unEx(), rheExpr.unEx()));
        return new Exp(new BINOP(BINOP.MUL, lheExpr.unEx(), rheExpr.unEx()));
    }

    public Exp visit(IntegerLiteral i) {
        this.addExp(new CONST(i.getValue()));
        return new Exp(new CONST(i.getValue()));
    }

    @Override
    public Exp visit(IntegerType i) {
        return null;
    }

    @Override
    public Exp visit(IntArrayType i) {
        return null;
    }

    public Exp visit(LessThan l) {
        var lheExpr = l.getLhe().accept(this);
        var rheExpr = l.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MINUS, lheExpr.unEx(), rheExpr.unEx()));
        return new Exp(new BINOP(BINOP.MINUS, lheExpr.unEx(), rheExpr.unEx()));
    }

    public Exp visit(NewArray n) {
        var newArraySize = n.getSize().accept(this);
        var parametersList = new LinkedList<ExpAbstract>();

        var exp = new BINOP(
                BINOP.MUL,
                new BINOP(BINOP.PLUS, newArraySize.unEx(), new CONST(1)),
                new CONST(this.frame.wordSize())
        );

        parametersList.add(exp);

        this.addExp(this.frame.externalCall("initArray", parametersList));
        return new Exp(this.frame.externalCall("initArray", parametersList));
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

        var cjump = new CJUMP(CJUMP.GT, cond, new CONST(1), bodyLabel, endLabel);

        var whileSeq = new SEQ(new SEQ(new SEQ(new LABEL(loopLabel), cjump), bodyJump), new LABEL(endLabel));

        this.addExp(new ESEQ(whileSeq, null));
        return new Exp(new ESEQ(whileSeq, null));
    }

    public Exp visit(If i) {
        var exp = i.getCondition().accept(this);
        var stm1 = new EXP(i.getThenBranch().accept(this).unEx());
        var stm2 = new EXP(i.getElseBranch().accept(this).unEx());

        var trueLabel = new Label();
        var falseLabel = new Label();
        var endLabel = new Label();

        var thenStatement = new SEQ(stm1, new LABEL(trueLabel));
        var elseStatement = new SEQ(stm2, new LABEL(falseLabel));
        var seqIfStatement = new SEQ(thenStatement, elseStatement);

        var cjumpIf = new CJUMP(CJUMP.EQ, new CONST(1), exp.unEx(), trueLabel, falseLabel);

        var condition = new SEQ(new LABEL(endLabel), cjumpIf);
        var seq = new SEQ(condition, seqIfStatement);

        this.addExp(
                new ESEQ(
                        new SEQ(seq, new LABEL(endLabel)),
                        null
                )
        );

        return new Exp(
                new ESEQ(
                        new SEQ(seq, new LABEL(endLabel)),
                        null
                )
        );
    }

    public Exp visit(Assign a) {
        var id = a.getIdentifier().accept(this);
        var value = a.getValue().accept(this);

        var move = new MOVE(id.unEx(), value.unEx());

        this.addExp(new ESEQ(move, new CONST(0)));
        return new Exp(new ESEQ(move, new CONST(0)));
    }

    public Exp visit(Sout s) {
        var argsList = new LinkedList<ExpAbstract>();
        var exp = s.getExpression().accept(this);
        argsList.add(exp.unEx());

        var call = this.frame.externalCall("print", argsList);

        this.addExp(call);
        return new Exp(call);
    }

    public Exp visit(Block b) {
        var size = b.getStatements().getStatements().size();
        ExpAbstract expBlock = new CONST(0);

        for (int i = 0; i < size; i++) {
            var expr = new EXP(b.getStatements().getStatements().get(i).accept(this).unEx());

            expBlock = new ESEQ(new SEQ(new EXP(expBlock), expr), new CONST(0));
        }

        this.addExp(expBlock);
        return new Exp(expBlock);
    }

    public Exp visit(MainClass m) {
        currentClassTable = mainTable.getMap().get(m.getClassName().getS());
        currentMethodTable = currentClassTable.getMethodsContext().get("main");

        var escapeList = new ArrayList<Boolean>();
        escapeList.add(false);
        frame = frame.newFrame("main", escapeList);


        var stm = m.getStatements().getStatements().getFirst().accept(this);
        var stmList = new ArrayList<Stm>();
        Stm stmBody = new EXP(stm.unEx());
        stmList.add(stmBody);

        frame.procEntryExit1(stmList);
        frag.setNext(new ProcFrag(stmBody, frame));
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

        for (int i = 0; i <= sizeFormals; i++) escapeList.add(false);


        this.frame = this.frame.newFrame(
                this.currentClassTable.getClassName() + "$" + this.currentMethodTable.getMethodName(),
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

        this.frame.procEntryExit1(stmList);

        this.frag.setNext(new ProcFrag(stmBody, this.frame));
        this.frag = this.frag.getNext();


        currentMethodTable = null;
        return null;
    }

    public Exp visit(VarDecl v) {
        return null;
    }

    public Exp visit(Formal f) {
        this.frame.allocLocal(false);
        return null;
    }

    public void addExp(ExpAbstract exp) {
        this.listExp.add(exp);
    }
}
