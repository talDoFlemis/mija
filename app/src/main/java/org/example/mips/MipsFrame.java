package org.example.mips;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.frame.Access;
import org.example.frame.Frame;
import org.example.irtree.*;
import org.example.temp.Label;
import org.example.temp.Temp;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MipsFrame extends Frame {

    // Registradores de MIPS
    static final Temp ZERO = new Temp(); // zero reg
    static final Temp AT = new Temp(); // reserved for assembler
    static final Temp V0 = new Temp(); // function result
    static final Temp V1 = new Temp(); // second function result
    static final Temp A0 = new Temp(); // argument1
    static final Temp A1 = new Temp(); // argument2
    static final Temp A2 = new Temp(); // argument3
    static final Temp A3 = new Temp(); // argument4
    static final Temp T0 = new Temp(); // caller-saved: T0 .. T7
    static final Temp T1 = new Temp();
    static final Temp T2 = new Temp();
    static final Temp T3 = new Temp();
    static final Temp T4 = new Temp();
    static final Temp T5 = new Temp();
    static final Temp T6 = new Temp();
    static final Temp T7 = new Temp();
    static final Temp S0 = new Temp(); // callee-saved: S0 .. S7
    static final Temp S1 = new Temp();
    static final Temp S2 = new Temp();
    static final Temp S3 = new Temp();
    static final Temp S4 = new Temp();
    static final Temp S5 = new Temp();
    static final Temp S6 = new Temp();
    static final Temp S7 = new Temp();
    static final Temp T8 = new Temp(); // caller-saved
    static final Temp T9 = new Temp(); // callee-saved
    static final Temp K0 = new Temp(); // reserved for OS kernel
    static final Temp K1 = new Temp(); // reserved for OS kernel
    static final Temp GP = new Temp(); // pointer to global area
    static final Temp SP = new Temp(); // stack pointer
    static final Temp S8 = new Temp(); // callee-save (frame pointer)
    static final Temp FP = new Temp(); // virtual frame pointer (eliminated)
    static final Temp RA = new Temp(); // return address
    private static final int wordSize = 4;
    // Register lists: must not overlap and must include every register that
    // might show up in
    private static final Temp[]
            specialRegs = {ZERO, AT, K0, K1, GP, SP},
            argRegs = {A0, A1, A2, A3},
            calleeSaves = {RA, S0, S1, S2, S3, S4, S5, S6, S7, S8},
            callerSaves = {T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, V0, V1};
    private static final Label badPtr = new Label("BADPTR");
    private static final Label badSub = new Label("BADSUB");
    private static final HashMap<Temp, String> tempMap = new HashMap<>(32);
    // Registers defined by a call
    static Temp[] calldefs = {};
    private static final HashMap<String, Integer> functions = new HashMap<>();
    private static final HashMap<String, Label> labels = new HashMap<>();
    // Registers live on return
    private static Temp[] returnSink = {};
    private static Temp[] registers = {};
    private static final boolean spilling = true;

    static {
        tempMap.put(ZERO, "$0");
        tempMap.put(AT, "$at");
        tempMap.put(V0, "$v0");
        tempMap.put(V1, "$v1");
        tempMap.put(A0, "$a0");
        tempMap.put(A1, "$a1");
        tempMap.put(A2, "$a2");
        tempMap.put(A3, "$a3");
        tempMap.put(T0, "$t0");
        tempMap.put(T1, "$t1");
        tempMap.put(T2, "$t2");
        tempMap.put(T3, "$t3");
        tempMap.put(T4, "$t4");
        tempMap.put(T5, "$t5");
        tempMap.put(T6, "$t6");
        tempMap.put(T7, "$t7");
        tempMap.put(S0, "$s0");
        tempMap.put(S1, "$s1");
        tempMap.put(S2, "$s2");
        tempMap.put(S3, "$s3");
        tempMap.put(S4, "$s4");
        tempMap.put(S5, "$s5");
        tempMap.put(S6, "$s6");
        tempMap.put(S7, "$s7");
        tempMap.put(T8, "$t8");
        tempMap.put(T9, "$t9");
        tempMap.put(K0, "$k0");
        tempMap.put(K1, "$k1");
        tempMap.put(GP, "$gp");
        tempMap.put(SP, "$sp");
        tempMap.put(S8, "$fp");
        tempMap.put(RA, "$ra");
    }

    int maxArgOffset = 0;
    private int offset = 0;
    private List<Access> actuals;

    static {
        LinkedList<Temp> l = new LinkedList<>();
        l.add(V0);
        addAll(l, specialRegs);
        addAll(l, calleeSaves);
        returnSink = l.toArray(returnSink);
    }

    static {
        LinkedList<Temp> l = new LinkedList<>();
        l.add(RA);
        addAll(l, argRegs);
        addAll(l, callerSaves);
        calldefs = l.toArray(calldefs);
    }

    static {
        LinkedList<Temp> l = new LinkedList<>();
        addAll(l, callerSaves);
        addAll(l, calleeSaves);
        addAll(l, argRegs);
        addAll(l, specialRegs);
        registers = l.toArray(registers);
    }

    private MipsFrame(String n, List<Boolean> f) {
        Integer count = functions.get(n);
        if (count == null) {
            count = 0;
            name = new Label(n);
        } else {
            count = count + 1;
            name = new Label(n + "." + count);
        }
        functions.put(n, count);
        actuals = new LinkedList<>();
        formals = new LinkedList<>();
        int offset = 0;
        Iterator<Boolean> escapes = f.iterator();
        formals.add(allocLocal(escapes.next()));
        actuals.add(new InReg(V0));
        for (Temp argReg : argRegs) {
            if (!escapes.hasNext())
                break;
            offset += wordSize;
            actuals.add(new InReg(argReg));
            if (escapes.next())
                formals.add(new InFrame(offset));
            else
                formals.add(new InReg(new Temp()));
        }
        while (escapes.hasNext()) {
            offset += wordSize;
            Access actual = new InFrame(offset);
            actuals.add(actual);
            if (escapes.next())
                formals.add(actual);
            else
                formals.add(new InReg(new Temp()));
        }
    }

    private static <R> void addAll(java.util.Collection<R> c, R[] a) {
        c.addAll(Arrays.asList(a));
    }

    private static Stm SEQ(Stm left, Stm right) {
        if (left == null)
            return right;
        if (right == null)
            return left;
        return new SEQ(left, right);
    }

    private static MOVE MOVE(ExpAbstract d, ExpAbstract s) {
        return new MOVE(d, s);
    }

    private static TEMP TEMP(Temp t) {
        return new TEMP(t);
    }

    // Mini Java Library will be appended to end of
    // program
    public String programTail() {

        return
                "         .text            \n" +
                        "         .globl _halloc   \n" +
                        "_halloc:                  \n" +
                        "         li $v0, 9        \n" +
                        "         syscall          \n" +
                        "         j $ra            \n" +
                        "                          \n" +
                        "         .text            \n" +
                        "         .globl _printint \n" +
                        "_printint:                \n" +
                        "         li $v0, 1        \n" +
                        "         syscall          \n" +
                        "         la $a0, newl     \n" +
                        "         li $v0, 4        \n" +
                        "         syscall          \n" +
                        "         j $ra            \n" +
                        "                          \n" +
                        "         .data            \n" +
                        "         .align   0       \n" +
                        "newl:    .asciiz \"\\n\"  \n" +
                        "         .data            \n" +
                        "         .align   0       \n" +
                        "str_er:  .asciiz \" ERROR: abnormal termination\\n\" " +
                        "                          \n" +
                        "         .text            \n" +
                        "         .globl _error    \n" +
                        "_error:                   \n" +
                        "         li $v0, 4        \n" +
                        "         la $a0, str_er   \n" +
                        "         syscall          \n" +
                        "         li $v0, 10       \n" +
                        "         syscall          \n";
    }

    public Frame newFrame(String name, List<Boolean> formals) {
        if (this.name != null)
            name = this.name + "." + name;
        return new MipsFrame(name, formals);
    }

    public int wordSize() {
        return wordSize;
    }

    public Access allocLocal(boolean escape) {
        if (escape) {
            Access result = new InFrame(offset);
            offset -= wordSize;
            return result;
        } else
            return new InReg(new Temp());
    }

    public Temp FP() {
        return FP;
    }

    public Temp RV() {
        return V0;
    }

    public ExpAbstract externalCall(String s, List<ExpAbstract> args) {

        String func = s.intern();
        Label l = labels.get(func);
        if (l == null) {
            l = new Label("_" + func);
            labels.put(func, l);
        }
        args.addFirst(new CONST(0));

        ExpList auxiliar_exp2_list = null;

        for (ExpAbstract arg : args) {
            auxiliar_exp2_list = new ExpList(arg, auxiliar_exp2_list);
        }
        return new CALL(new NAME(l), auxiliar_exp2_list);
    }

    public String string(Label lab, String string) {
        int length = string.length();
        StringBuilder lit = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
                case '\b':
                    lit.append("\\b");
                    break;
                case '\t':
                    lit.append("\\t");
                    break;
                case '\n':
                    lit.append("\\n");
                    break;
                case '\f':
                    lit.append("\\f");
                    break;
                case '\r':
                    lit.append("\\r");
                    break;
                case '\"':
                    lit.append("\\\"");
                    break;
                case '\\':
                    lit.append("\\\\");
                    break;
                default:
                    if (c < ' ' || c > '~') {
                        lit.append("\\").append((c >> 6) & 7).append((c >> 3) & 7).append(c & 7);
                    } else
                        lit.append(c);
                    break;
            }
        }
        return "\t.data\n\t.word " + length + "\n" + lab.toString() + ":\t.asciiz\t\"" + lit + "\"";
    }

    public Label badPtr() {
        return badPtr;
    }

    public Label badSub() {
        return badSub;
    }

    public String tempMap(Temp temp) {
        return tempMap.get(temp);
    }

    public Temp[] calldefs() {
        return calldefs;
    }

    private void assignFormals(Iterator<Access> formals, Iterator<Access> actuals, List<Stm> body) {
        if (!formals.hasNext() || !actuals.hasNext())
            return;
        Access formal = formals.next();
        Access actual = actuals.next();
        assignFormals(formals, actuals, body);
        body.addFirst(MOVE(formal.exp(TEMP(FP)), actual.exp(TEMP(FP))));
    }

    private void assignCallees(int i, List<Stm> body) {
        if (i >= calleeSaves.length)
            return;
        Access a = allocLocal(!spilling);
        assignCallees(i + 1, body);
        body.addFirst(MOVE(a.exp(TEMP(FP)), TEMP(calleeSaves[i])));
        body.add(MOVE(TEMP(calleeSaves[i]), a.exp(TEMP(FP))));
    }

    public Temp[] registers() {
        return registers;
    }

}
