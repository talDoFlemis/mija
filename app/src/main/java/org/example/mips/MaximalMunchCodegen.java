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
			emit(new AssemOPER("jal " + name.label + "\n", null, new TempList(temp, tempList)));
		}

	}

	public void munchStmCJUMP(CJUMP cjump) {
		String relop = switch (cjump.relop) {
			case CJUMP.EQ -> "beq";
			case CJUMP.GE -> "bge";
			case CJUMP.GT -> "bgt";
			case CJUMP.LE -> "ble";
			case CJUMP.LT -> "blt";
			case CJUMP.NE -> "bne";
			case CJUMP.UGE -> "jae";
			case CJUMP.UGT -> "ja";
			case CJUMP.ULE -> "jbe";
			case CJUMP.ULT -> "jb";
			default -> "";
		};
		Temp l = munchExp(cjump.left);
		Temp r = munchExp(cjump.right);
		emit(new AssemOPER(relop + "`s0, `s1, `j0\n", null, new TempList(l, new TempList(r, null)),
			new LabelList(cjump.condTrue, new LabelList(cjump.condFalse, null))));
	}

	private void munchStmJUMP(JUMP jump) {
		NAME jname = ((NAME) jump.exp);
		emit(new AssemOPER("j `j0\n", null, null, new LabelList(jname.label, null)));
	}

	public void munchStmSeq(SEQ seq) {
		munchStm(seq.left);
		munchStm(seq.right);
	}

	public void munchStmLabel(LABEL label) {
		String label_str = label.label.toString();
		var label_assem = new AssemLABEL(label_str + ":\n", label.label);
		emit(label_assem);
	}

	void munchStmMove(TEMP dst, ExpAbstract src) {
		Temp temp = munchExp(src);

		emit(new AssemMOVE("move `s0, `d0", dst.temp, temp));
	}

	public void munchStmMove(ExpAbstract dst, ExpAbstract src) {
		if (dst instanceof MEM mem) {
			munchStmMove(mem, src);
		} else if (dst instanceof TEMP && src instanceof CALL call) {
			Temp temp = munchExp(call.func);
			TempList templist = munchArgs(0, ((CALL) src).args);
			Label funcname = ((NAME) (call.func)).label;
			emit(new AssemOPER("jal " + funcname.toString() + "\n", new TempList(temp, null), templist));
		}

	}

	void munchStmMove(MEM dst, ExpAbstract src) {
		if (dst.exp instanceof BINOP binop) {
			if (binop.binop == BINOP.PLUS) {
				if (binop.right instanceof CONST) {
					Temp temp1 = munchExp(binop.left);
					Temp temp2 = munchExp(src);
					TempList s = new TempList(temp2, null);
					TempList d = new TempList(temp1, null);
					emit(new AssemOPER("sw `s0, `d0\n", d, s));
				} else if (binop.left instanceof CONST) {
					Temp temp1 = munchExp(binop.right);
					Temp temp2 = munchExp(src);
					TempList s = new TempList(temp2, null);
					TempList d = new TempList(temp1, null);
					emit(new AssemOPER("sw `s0, `d0\n", d, s));
				}
			}
		} else if (src instanceof MEM) {
			Temp temp1 = munchExp(dst.exp);
			TempList d = new TempList(temp1, null);
			Temp temp2 = munchExp(src);
			TempList s = new TempList(temp2, null);

			emit(new AssemOPER("move `s0, `d0\n", d, s));
		} else if (dst.exp instanceof CONST) {
			Temp temp1 = munchExp(dst.exp);
			TempList d = new TempList(temp1, null);
			Temp temp2 = munchExp(src);
			TempList s = new TempList(temp2, null);

			emit(new AssemOPER("sw, `s0, `d0\n", d, s));
		} else {
			TempList d = new TempList(munchExp(dst.exp), null);
			TempList s = new TempList(munchExp(src), null);
			emit(new AssemOPER("sw `s0, 0(`s0)\n", d, s));
		}

	}

	Temp munchExp(ExpAbstract exp2) {
		switch (exp2) {
			case BINOP binop -> {
				return munchExpBinop(binop);
			}
			case CONST cons -> {
				Temp temp = new Temp();
				emit(new AssemOPER("li `d0," + cons.value + "\n", new TempList(temp, null), null));
				return temp;
			}
			case MEM mem -> {
				return munchExpMem(mem);
			}
			case TEMP temp -> {
				return temp.temp;
			}
			case NAME name -> {
				return new Temp();
			}
			case null, default -> {
				return null;
			}
		}
	}

	Temp munchExpCall(CALL call) {
		TempList tempListArgs = new TempList(null, null);
		int callArgsCount = 0;

		for (ExpList iter = call.args; iter != null; iter = iter.tail) {
			callArgsCount = callArgsCount + 1;
			tempListArgs = new TempList(munchExp(iter.head), tempListArgs);
		}

		for (TempList iter = tempListArgs; iter != null; iter = iter.tail)
			emit(new AssemOPER("push `s0", new TempList(this.frame.registers()[0], null),
				new TempList(iter.head, new TempList(this.frame.registers()[0], null))));

		int wordSizeCount = callArgsCount * this.frame.wordSize();

		Temp[] calldefs = frame.calldefs();
		TempList trueCalldefs = new TempList(calldefs[0], null);
		for (int i = 1; i < calldefs.length; i++) {
			trueCalldefs.tail = new TempList(calldefs[i], null);
		}

		if (call.func instanceof NAME name) {
			emit(new AssemOPER("call " + name.label, trueCalldefs, null));
		} else {
			Temp adr = munchExp(call.func);
			emit(new AssemOPER("call `s0", trueCalldefs, new TempList(adr, null)));
		}

		Temp temp_reg = new Temp();
		emit(new AssemMOVE("mov `d0,`s0", temp_reg, frame.RV()));
		emit(new AssemOPER("add `d0," + wordSizeCount, new TempList(this.frame.registers()[0], null),
			new TempList(this.frame.registers()[0], null)));
		return temp_reg;
	}

	Temp munchExpMem(MEM mem) {
		Temp temp_reg = new Temp();
		TempList d = new TempList(temp_reg, null);

		if (mem.exp instanceof BINOP binop && binop.binop == BINOP.PLUS) {
			ExpAbstract bLeft = binop.left;
			ExpAbstract bRight = binop.right;

			if (bRight instanceof CONST cons) {
				TempList s = new TempList(munchExp(bLeft), null);
				emit(new AssemOPER("lw `d0, " + cons.value + "(`s0)\n", d, s));
			} else if (bLeft instanceof CONST cons) {
				TempList s = new TempList(munchExp(bRight), null);
				emit(new AssemOPER("lw `d0, " + cons.value + "(`s0) \n", d, s));
			}
		} else if (mem.exp instanceof CONST cons) {
			emit(new AssemOPER("lw `d0, " + cons.value + "($zero) \n", d, null));
		} else {
			emit(new AssemOPER("lw `d0, `s0\n", d, new TempList(munchExp(mem.exp), null)));
		}
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
			case BINOP.MINUS:
				emit(new AssemOPER("sub `d0, `s0, `s1 \n", d, munchTempList));
				break;
			case BINOP.MUL:
				emit(new AssemOPER("mul `d0, `s0, `s1 \n", d, munchTempList));
				break;
			case BINOP.DIV:
				emit(new AssemOPER("div `s0,`s1\nmflo `d0\n", d, munchTempList));
				break;
			case BINOP.AND:
				emit(new AssemOPER("and `d0, `s0, `s1 \n", d, munchTempList));
				break;
			case BINOP.OR:
				emit(new AssemOPER("or `d0, `s0, `s1 \n", d, munchTempList));
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
