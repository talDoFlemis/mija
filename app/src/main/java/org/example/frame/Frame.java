package org.example.frame;

import org.example.irtree.ExpAbstract;
import org.example.irtree.Stm;
import org.example.temp.Label;
import org.example.temp.Temp;
import org.example.temp.TempMap;

import java.util.List;

public abstract class Frame implements TempMap {
    public Label name;
    public List<Access> formals;

    public abstract Frame newFrame(String name, List<Boolean> formals);

    public abstract Access allocLocal(boolean escape);

    public abstract Temp FP();

    public abstract int wordSize();

    public abstract ExpAbstract externalCall(String func, List<ExpAbstract> args);

    public abstract Temp RV();

    public abstract String string(Label label, String value);

    public abstract Label badPtr();

    public abstract Label badSub();

    public abstract String tempMap(Temp temp);

    public abstract Temp[] registers();

    public abstract String programTail();

    public abstract void procEntryExit1(List<Stm> body);

    /*
        public abstract List<Assem.Instr> codegen(List<Stm> stms);


        public abstract void procEntryExit2(List<Assem.Instr> body);

        public abstract void procEntryExit3(List<Assem.Instr> body);

        public abstract void spill(List<Assem.Instr> insns, Temp[] spills);
    */
}
