package org.example.mips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.assem.*;
import org.example.irtree.*;
import org.example.temp.Label;
import org.example.temp.LabelList;
import org.example.temp.Temp;
import org.example.temp.TempList;

@EqualsAndHashCode()
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaximalMunchCodegen implements MipsCodegen {
	private MipsFrame frame;
	private InstrList ilist;
	private InstrList last;

	public MaximalMunchCodegen(MipsFrame f) {
		frame = f;
		ilist = null;
		last = null;
	}

	public MaximalMunchCodegen(MipsFrame f, InstrList list) {
		frame = f;
		ilist = list;
		last = list;

		while (last.tail != null) {
			last = last.tail;
		}
	}

	public InstrList codegen(Stm s) {
		InstrList l;
		munchStm(s);
		l = ilist;
		ilist = last = null;
		return l;
	}

	private void emit(Instr instr) {
		if (last != null) {
			last = ilist.tail = new InstrList(instr, null);
		} else {
			last = ilist = new InstrList(instr, null);
		}
	}

	private TempList munchArgs(int i, ExpList args) {
		ExpList temp = args;
		TempList ret = null;

		while (!(temp == null)) {
			Temp temp_head = munchExp(temp.head);
			ret = new TempList(temp_head, ret);
			temp = temp.tail;
		}

		return ret;
	}


	public void munchStm(Stm stm) {
		if (stm instanceof SEQ seq) {
			munchStmSeq(seq);
		} else if (stm instanceof MOVE move) {
			munchStmMove(move.dst, move.src);
		} else if (stm instanceof LABEL label) {
			emit(
				new AssemLABEL(label.label + ":\n", label.label)
			);
		} else if (stm instanceof JUMP jump) {
			munchStmJUMP(jump);
		} else if (stm instanceof CJUMP cjump) {
			munchStmCJUMP(cjump);
		} else if (stm instanceof EXP exp && (exp.exp instanceof CALL call)) {
			Temp temp = munchExp(call.func);
			TempList tempList = munchArgs(0, call.args);
			NAME name = (NAME) (call.func);
			emit(new AssemOPER("jump " + name.label + "\n", null, new TempList(temp, tempList)));
		}

	}

	public void munchStmCJUMP(CJUMP cjump) {
		String relop = switch (cjump.relop) {
			case CJUMP.EQ -> "beq";
			case CJUMP.GE -> "bge";
			case CJUMP.LT -> "blt";
			case CJUMP.NE -> "bne";
			default -> "";
		};
		Temp l = munchExp(cjump.left);
		Temp r = munchExp(cjump.right);
		emit(new AssemOPER(relop + "`s0, `s1, `j0\n", null, new TempList(l, new TempList(r, null)),
			new LabelList(cjump.condTrue, new LabelList(cjump.condFalse, null))));
	}

	private void munchStmJUMP(JUMP jump) {
		NAME jname = ((NAME) jump.exp);
		emit(new AssemOPER("jump `j0\n", null, null, new LabelList(jname.label, null)));
	}

	public void munchStmSeq(SEQ seq) {
		munchStm(seq.left);
		munchStm(seq.right);
	}

	public void munchStmMove(ExpAbstract dst, ExpAbstract src) {
		if (dst instanceof MEM mem) {
			munchStmMove(mem, src);
		} else if (dst instanceof TEMP && src instanceof CALL call) {
			Temp temp = munchExp(call.func);
			TempList templist = munchArgs(0, ((CALL) src).args);
			Label funcname = ((NAME) (call.func)).label;
			emit(new AssemOPER("jump " + funcname.toString() + "\n", new TempList(temp, null), templist));
		}

	}

	void munchStmMove(MEM dst, ExpAbstract src) {
	}

	Temp munchExp(ExpAbstract exp2) {
		Temp temp_reg = new Temp();
		return temp_reg;
	}

	Temp munchExpCall(CALL call) {
		Temp temp_reg = new Temp();
		return temp_reg;
	}

	Temp munchExpMem(MEM mem) {
		Temp temp_reg = new Temp();
		return temp_reg;
	}

	Temp munchExpBinop(BINOP binop) {
		Temp temp_reg = new Temp();
		TempList d = new TempList(temp_reg, null);
		TempList munchTempList = new TempList(munchExp(binop.left), new TempList(munchExp(binop.right), null));

		switch (binop.binop) {
			case BINOP.PLUS: {
				if (binop.right instanceof CONST cons) {
					emit(new AssemOPER("addi `d0, `s0," + cons.value + "\n", d, munchTempList));
				} else if (binop.left instanceof CONST cons) {
					emit(new AssemOPER("addi `d0, `s0," + cons.value + "\n", d, munchTempList));
				} else {
					emit(new AssemOPER("add `d0, `s0, `s1 \n", d, munchTempList));
				}
				break;
			}
			case BINOP.MINUS: {
				if (binop.right instanceof CONST cons) {
					emit(new AssemOPER("subi `d0, `s0," + cons.value + "\n", d, munchTempList));
				} else if (binop.left instanceof CONST cons) {
					emit(new AssemOPER("subi `d0, `s0," + cons.value + "\n", d, munchTempList));
				} else {
					emit(new AssemOPER("sub `d0, `s0, `s1 \n", d, munchTempList));
				}
				break;
			}
			case BINOP.MUL:
				emit(new AssemOPER("mul `d0, `s0, `s1 \n", d, munchTempList));
				break;
			case BINOP.DIV:
				emit(new AssemOPER("div `s0,`s1\nmflo `d0\n", d, munchTempList));
				break;
			default:
				break;
		}
		return temp_reg;
	}

	Temp munchExpTemp(TEMP temp) {
		return temp.temp;
	}
}
