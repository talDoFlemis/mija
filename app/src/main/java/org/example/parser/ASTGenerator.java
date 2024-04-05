package org.example.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.example.antlr.MiniJavaParser;
import org.example.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class ASTGenerator extends org.example.antlr.MiniJavaBaseListener {

    private Program prog;
    private MainClass mc;
    private ClassDeclList cdl;
    private Stack<MethodDeclList> mdlStack;
    private Stack<FormalList> flclStack;
    private Stack<VarDeclList> vdlStack;
    private Stack<Type> typeStack;
    private Stack<Statement> stmStack;
    private Stack<Stack<Statement>> stmStackStack;
    private Stack<StatementList> stmListStack;
    private Stack<Expression> expStack;
    private Stack<Stack<Expression>> expStackStack;
    private Stack<ExpressionList> expListStack;

    public ASTGenerator() {
        super();
    }

    public Program getProgram() {
        return prog;
    }

    private Identifier getId(ParseTree ctx, int i) {
        return new Identifier(ctx.getChild(i).getText());
    }

    public void enterProgram(MiniJavaParser.ProgramContext ctx) {
        mdlStack = new Stack<>();
        flclStack = new Stack<>();
        vdlStack = new Stack<>();
        typeStack = new Stack<>();

        expStack = new Stack<>();
        expStackStack = new Stack<>();
        expListStack = new Stack<>();

        stmStack = new Stack<>();
        stmStackStack = new Stack<>();
        stmListStack = new Stack<>();

        cdl = new ClassDeclList();
    }

    public void exitProgram(MiniJavaParser.ProgramContext ctx) {
        prog = new Program(mc, cdl);
    }

    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        StatementList stmList = stmListStack.pop();
        Identifier className = getId(ctx, 1);
        Identifier args = new Identifier(ctx.getChild(11).getText());
        mc = new MainClass(className, args, stmList);
    }

    public void exitClassDecl(MiniJavaParser.ClassDeclContext ctx) {
        Identifier className = getId(ctx, 1);
        VarDeclList vdl = vdlStack.pop();
        MethodDeclList mdl = mdlStack.pop();
        if (ctx.getChild(2).getText().equals("extends")) {
            Identifier parentClass = new Identifier(ctx.getChild(3).getText());
            cdl.addClassDecl(new ClassDeclExtends(className, parentClass, vdl, mdl));
        } else {
            cdl.addClassDecl(new ClassDeclSimple(className, vdl, mdl));
        }
    }

    public void enterMethodDeclList(MiniJavaParser.MethodDeclListContext ctx) {
        mdlStack.push(new MethodDeclList());
    }

    public void exitMethodDecl(MiniJavaParser.MethodDeclContext ctx) {
        Type t = typeStack.pop();
        Identifier id = getId(ctx, 2);
        FormalList flcl = flclStack.pop();
        VarDeclList vdl = vdlStack.pop();
        StatementList stml = stmListStack.pop();
        Expression exp = expStack.pop();
        mdlStack.peek().addMethodDecl(new MethodDecl(t, id.getS(), flcl, vdl, stml, exp));
    }

    public void enterFormalList(MiniJavaParser.FormalListContext ctx) {
        flclStack.push(new FormalList());
    }

    public void exitFormal(MiniJavaParser.FormalContext ctx) {
        Type t = typeStack.pop();
        Identifier id = getId(ctx, 1);
        flclStack.peek().addFormal(new Formal(t, id.getS()));
    }

    public void enterVarDeclList(MiniJavaParser.VarDeclListContext ctx) {
        vdlStack.push(new VarDeclList());
    }

    public void exitVarDecl(MiniJavaParser.VarDeclContext ctx) {
        Identifier id = getId(ctx, 1);
        Type t = typeStack.pop();

        vdlStack.peek().addVarDecl(new VarDecl(t, id.getS()));
    }

    public void exitTypeInteger(MiniJavaParser.TypeIntegerContext ctx) {
        typeStack.push(new IntegerType());
    }

    public void exitTypeBoolean(MiniJavaParser.TypeBooleanContext ctx) {
        typeStack.push(new BooleanType());
    }

    public void exitTypeIntArray(MiniJavaParser.TypeIntArrayContext ctx) {
        typeStack.push(new IntArrayType());
    }

    public void exitTypeIdentifier(MiniJavaParser.TypeIdentifierContext ctx) {
        typeStack.push(new IdentifierType(ctx.getText()));
    }

    public void enterStmList(@NotNull org.example.antlr.MiniJavaParser.StmListContext ctx) {
        stmStackStack.push(stmStack);
        stmStack = new Stack<>();
    }

    public void exitStmList(@NotNull MiniJavaParser.StmListContext ctx) {
        StatementList stml = new StatementList();
        for (Statement stm : stmStack) {
            stml.addStatement(stm);
        }
        stmListStack.push(stml);

        stmStack = stmStackStack.pop();
    }

    public void exitStmBlock(@NotNull MiniJavaParser.StmBlockContext ctx) {
        stmStack.push(new Block(stmListStack.pop()));
    }

    public void exitStmWhile(@NotNull MiniJavaParser.StmWhileContext ctx) {
        Expression exp = expStack.pop();
        Statement stm = stmStack.pop();
        stmStack.push(new While(exp, stm));
    }

    public void exitStmArrayAssign(@NotNull MiniJavaParser.StmArrayAssignContext ctx) {
        Identifier id = getId(ctx, 0);
        //attention to order!
        Expression rightSide = expStack.pop();
        Expression index = expStack.pop();
        stmStack.push(new ArrayAssign(id, index, rightSide));
    }

    public void exitStmIf(@NotNull MiniJavaParser.StmIfContext ctx) {
        Expression exp = expStack.pop();
        //attention to order!
        Statement stmElse = stmStack.pop();
        Statement stmThen = stmStack.pop();
        stmStack.push(new If(exp, stmThen, stmElse));
    }

    public void exitStmPrint(@NotNull MiniJavaParser.StmPrintContext ctx) {
        Expression exp = expStack.pop();
        stmStack.push(new Sout(exp));
    }

    public void exitStmAssign(@NotNull MiniJavaParser.StmAssignContext ctx) {
        Identifier id = getId(ctx, 0);
        Expression rightSide = expStack.pop();
        stmStack.push(new Assign(id, rightSide));
    }

    public void exitExpArrayLookup(@NotNull MiniJavaParser.ExpArrayLookupContext ctx) {
        //attention to order!
        Expression address = expStack.pop();
        Expression arr = expStack.pop();
        expStack.push(new ArrayLookup(arr, address));
    }

    public void exitExpTimes(@NotNull MiniJavaParser.ExpTimesContext ctx) {
        Expression e2 = expStack.pop();
        Expression e1 = expStack.pop();
        expStack.push(new Times(e1, e2));
    }

    public void exitExpAnd(@NotNull MiniJavaParser.ExpAndContext ctx) {
        //attention to order!
        Expression e2 = expStack.pop();
        Expression e1 = expStack.pop();
        expStack.push(new And(e1, e2));
    }

    public void exitExpPlus(@NotNull MiniJavaParser.ExpPlusContext ctx) {
        Expression e2 = expStack.pop();
        Expression e1 = expStack.pop();
        expStack.push(new Plus(e1, e2));
    }

    public void exitExpMinus(@NotNull MiniJavaParser.ExpMinusContext ctx) {
        Expression e2 = expStack.pop();
        Expression e1 = expStack.pop();
        expStack.push(new Minus(e1, e2));
    }

    public void exitExpLessThan(@NotNull MiniJavaParser.ExpLessThanContext ctx) {
        //attention to order!
        Expression e2 = expStack.pop();
        Expression e1 = expStack.pop();
        expStack.push(new LessThan(e1, e2));
    }

    public void exitExpIntegerLiteral(@NotNull MiniJavaParser.ExpIntegerLiteralContext ctx) {
        int i = Integer.parseInt(ctx.getText());
        expStack.push(new IntegerLiteral(i));
    }

    public void exitExpIdentifierExp(@NotNull MiniJavaParser.ExpIdentifierExpContext ctx) {
        expStack.push(new Identifier(ctx.getText()));
    }

    public void exitExpNot(@NotNull MiniJavaParser.ExpNotContext ctx) {
        Expression e = expStack.pop();
        expStack.push(new Not(e));
    }

    public void exitExpNewObject(@NotNull MiniJavaParser.ExpNewObjectContext ctx) {
        Identifier id = getId(ctx, 1);
        expStack.push(new NewObject(id));
    }

    public void exitExpTrue(@NotNull MiniJavaParser.ExpTrueContext ctx) {
        expStack.push(new True());
    }

    public void exitExpFalse(@NotNull MiniJavaParser.ExpFalseContext ctx) {
        expStack.push(new False());
    }

    public void exitExpBracket(@NotNull MiniJavaParser.ExpBracketContext ctx) {
        //no corresponding element in AST
    }

    public void exitExpNewArray(@NotNull MiniJavaParser.ExpNewArrayContext ctx) {
        Expression e = expStack.pop();
        expStack.push(new NewArray(e));
    }

    public void exitExpThis(@NotNull MiniJavaParser.ExpThisContext ctx) {
        expStack.push(new This());
    }

    public void exitExpArrayLength(@NotNull MiniJavaParser.ExpArrayLengthContext ctx) {
        Expression e = expStack.pop();
        expStack.push(new ArrayLength(e));
    }

    public void enterCallArguments(@NotNull MiniJavaParser.CallArgumentsContext ctx) {
        expStackStack.push(expStack);
        expStack = new Stack<>();
    }

    public void exitCallArguments(@NotNull MiniJavaParser.CallArgumentsContext ctx) {
        ExpressionList expl = new ExpressionList();
        for (Expression e : expStack) {
            expl.addExpression(e);
        }
        expListStack.push(expl);
        expStack = expStackStack.pop();
    }

    public void exitExpCall(@NotNull MiniJavaParser.ExpCallContext ctx) {
        ExpressionList args = expListStack.pop();
        Identifier id = getId(ctx, 2);
        Expression e = expStack.pop();
        expStack.push(new Call(e, id, args));
    }
}