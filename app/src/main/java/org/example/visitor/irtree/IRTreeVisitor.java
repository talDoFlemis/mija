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
    private MainTable mainTable;
    private ClassTable currentClassTable;
    private MethodTable currentMethodTable;

    public IRTreeVisitor(MainTable mainTable, Frame frame) {
        this.mainTable = mainTable;
        this.frame = frame;
        this.frag = new Frag(null);
        this.initialFrag = this.frag;
        this.listExp = new ArrayList<>();
    }

    public void addExp(ExpAbstract exp) {
        this.listExp.add(exp);
    }

    public Exp visit(And a) {
        var expLhe = a.getLhe().accept(this);
        var expRhe = a.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.AND, expLhe.unEx(), expRhe.unEx()));

        return new Exp(new BINOP(BINOP.AND, expLhe.unEx(), expRhe.unEx()));
    }

    @Override
    public Exp visit(BooleanType b) {
        // dont
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
        ClassTable classTable1 = null, classTable2 = null;
        MethodTable methodTable = null;
        String classSymbol = null;
        ExpList expList = null;

        int size;
        size = c.getExpressionList().getList().size();

        for (int i = size - 1; i >= 0; i--)
            expList = new ExpList(c.getExpressionList().getList().get(i).accept(this).unEx(), expList);
        expList = new ExpList(c.getOwner().accept(this).unEx(), expList);

        if (c.getOwner() instanceof Call) {
            classTable1 = this.currentClassTable;

            classTable2 = this.mainTable.getMap().get(classTable1.getClassName());
            methodTable = classTable2.getMethodsContext().get(c.getMethod().toString());

            classTable1 = this.mainTable.getMap().get(methodTable.getClassParent().getClassName());
        }

        if (c.getOwner() instanceof IdentifierExpression idExp) {
            idExp.getId();

            if (this.currentMethodTable.getLocalsContext().get(idExp.getId()) instanceof IdentifierType) {
                var type = this.currentMethodTable.getLocalsContext().get(idExp.getId());
                var idType = (IdentifierType) type;
                classSymbol = idType.getS();
            } else if (this.currentMethodTable.getParamsContext().get(idExp.getId()) instanceof IdentifierType) {
                var type = this.currentMethodTable.getParamsContext().get(idExp.getId());
                var idType = (IdentifierType) type;
                classSymbol = idType.getS();
            } else if (this.currentClassTable.getFieldsContext().get(idExp.getId()) instanceof IdentifierType) {
                var type = this.currentClassTable.getFieldsContext().get(idExp.getId());
                var idType = (IdentifierType) type;
                classSymbol = idType.getS();
            }
        }

        if (c.getOwner() instanceof NewObject idNewObject) {
            classTable1 = this.mainTable.getMap().get(idNewObject.getIdentifier().toString());
        }
        if (c.getOwner() instanceof This) classTable1 = this.currentClassTable;
        if (classTable1 != null) classSymbol = classTable1.getClassName();

        var label = new Label(classSymbol + "." + c.getMethod().toString());

        this.addExp(new CALL(new NAME(label), expList));
        return new Exp(new CALL(new NAME(label), expList));
    }

    public Exp visit(IdentifierExpression i) {
        var allocLocal = this.frame.allocLocal(false);

        this.addExp(allocLocal.exp(new TEMP(this.frame.FP())));
        return new Exp(allocLocal.exp(new TEMP(this.frame.FP())));
    }

    @Override
    public Exp visit(IdentifierType i) {
        return null;
    }

    public Exp visit(NewObject n) {
        int sizeOfHash = this.mainTable.getMap().get(n.getIdentifier().toString()).getFieldsContext().size();

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
        var expLhe = p.getLhe().accept(this);
        var expRhe = p.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.PLUS, expLhe.unEx(), expRhe.unEx()));
        return new Exp(new BINOP(BINOP.PLUS, expLhe.unEx(), expRhe.unEx()));
    }

    public Exp visit(Minus m) {
        var expLhe = m.getLhe().accept(this);
        var expRhe = m.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MINUS, expLhe.unEx(), expRhe.unEx()));
        return new Exp(new BINOP(BINOP.MINUS, expLhe.unEx(), expRhe.unEx()));
    }

    public Exp visit(Times t) {
        var expLhe = t.getLhe().accept(this);
        var expRhe = t.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MUL, expLhe.unEx(), expRhe.unEx()));
        return new Exp(new BINOP(BINOP.MUL, expLhe.unEx(), expRhe.unEx()));
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
        var expLhe = l.getLhe().accept(this);
        var expRhe = l.getRhe().accept(this);

        this.addExp(new BINOP(BINOP.MINUS, expLhe.unEx(), expRhe.unEx()));
        return new Exp(new BINOP(BINOP.MINUS, expLhe.unEx(), expRhe.unEx()));
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
        var escapeList = new ArrayList<Boolean>();
        escapeList.add(false);

        this.currentClassTable = this.mainTable.getMap().get(m.getClassName().toString());
        this.frame = this.frame.newFrame("main", escapeList);

        var stm = m.getStatements().getStatements().getFirst().accept(this);
        var stmList = new ArrayList<Stm>();
        Stm stmBody = new EXP(stm.unEx());
        stmList.add(stmBody);

        this.frame.procEntryExit1(stmList);
        this.frag.setNext(new ProcFrag(stmBody, this.frame));
        this.frag = this.frag.getNext();

        return null;
    }

    public Exp visit(ClassDeclSimple c) {
        this.currentClassTable = this.mainTable.getMap().get(c.getClassName().toString());

        c.getClassName().accept(this);

        c.getFields().getVarDecls().forEach(field -> field.accept(this));
        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));

        return null;
    }

    public Exp visit(ClassDeclExtends c) {
        this.currentClassTable = this.mainTable.getMap().get(c.getClassName().toString());

        c.getClassName().accept(this);
        c.getParent().accept(this);

        c.getMethods().getMethodDecls().forEach(method -> method.accept(this));
        c.getFields().getVarDecls().forEach(field -> field.accept(this));

        return null;
    }

    public Exp visit(Program p) {
        p.getMainClass().accept(this);
        p.getClasses().getClassDecls().forEach(classDecl -> classDecl.accept(this));
        return null;
    }

    public Exp visit(MethodDecl m) {
        Stm stmBody = new EXP(new CONST(0));
        var escapeList = new ArrayList<Boolean>();
        int sizeFormals = m.getFormals().getFormals().size();
        int sizeStatement = m.getStatements().getStatements().size();

        // variaveis nao escapam no MiniJava
        for (int i = 0; i < sizeFormals; i++) escapeList.add(false);

        this.currentMethodTable = this.currentClassTable.getMethodsContext().get(m.getIdentifier());

        this.frame = this.frame.newFrame(
                this.currentClassTable.getClassName() + "." + this.currentMethodTable.getMethodName(),
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

        this.currentMethodTable = null;

        return null;
    }

    @Override
    public Exp visit(VarDecl v) {
        return null;
    }

    public Exp visit(Formal f) {
        this.frame.allocLocal(false);
        return null;
    }
}
