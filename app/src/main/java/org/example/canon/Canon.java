package org.example.canon;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.irtree.*;
import org.example.temp.Temp;

@Builder
@Data
@NoArgsConstructor
public class Canon {

	static StmExpList nopNull = StmExpList.builder()
		.stm(new EXP(new CONST(0)))
		.exps(null)
		.build();

	static boolean isNop(Stm a) {
		return a instanceof EXP aExp && aExp.exp instanceof CONST;
	}

	static Stm seq(Stm a, Stm b) {
		if (isNop(a)) return b;
		else if (isNop(b)) return a;
		else return new SEQ(a, b);
	}

	static boolean commute(Stm a, ExpAbstract b) {
		return isNop(a)
			|| b instanceof NAME
			|| b instanceof CONST;
	}

	static Stm doStm(SEQ s) {
		return seq(doStm(s.left), doStm(s.right));
	}

	static Stm doStm(MOVE s) {
		if (s.dst instanceof TEMP sTEMP && s.src instanceof CALL sCALL) {
			var moveCall = new MoveCall(sTEMP, sCALL);
			return reorderStm(moveCall);
		} else if (s.dst instanceof ESEQ sESEQ) {
			var algo = SEQ.builder()
				.left(sESEQ.stm)
				.right(
					MOVE.builder()
						.dst(sESEQ.exp)
						.src(s.src)
						.build()
				)
				.build();
			return doStm(algo);
		} else {
			return reorderStm(s);
		}
	}

	static Stm doStm(EXP s) {
		if (s.exp instanceof CALL sCall) {
			return reorderStm(new ExpCall(sCall));
		} else {
			return reorderStm(s);
		}
	}

	static Stm doStm(Stm s) {
		return switch (s) {
			case SEQ seq -> doStm(seq);
			case MOVE move -> doStm(move);
			case EXP exp -> doStm(exp);
			case null, default -> reorderStm(s);
		};
	}

	static Stm reorderStm(Stm s) {
		StmExpList x = reorder(s.children());
		return seq(x.stm, s.build(x.exps));
	}

	static ESEQ doExp(ESEQ e) {
		Stm stmts = doStm(e.stm);
		ESEQ b = doExp(e.exp);
		return ESEQ.builder()
			.exp(b.exp)
			.stm(seq(stmts, b.stm))
			.build();
	}

	static ESEQ doExp(ExpAbstract e) {
		if (e instanceof ESEQ eESEQ) {
			return doExp(eESEQ);
		} else {
			return reorderExp(e);
		}

	}

	static ESEQ reorderExp(ExpAbstract e) {
		StmExpList x = reorder(e.children());
		return ESEQ.builder()
			.exp(e.build(x.exps))
			.stm(x.stm)
			.build();
	}

	static StmExpList reorder(ExpList exps) {
		if (exps == null) {
			return nopNull;
		} else {
			var a = exps.head;
			if (a instanceof CALL) {
				Temp t = new Temp();
				var e = new ESEQ(new MOVE(new TEMP(t), a),
					new TEMP(t));
				return reorder(new ExpList(e, exps.tail));
			} else {
				ESEQ aa = doExp(a);
				StmExpList bb = reorder(exps.tail);
				if (commute(bb.stm, aa.exp))
					return StmExpList.builder()
						.exps(new ExpList(aa.exp, bb.exps))
						.stm(seq(aa.stm, bb.stm))
						.build();
				else {
					Temp t = new Temp();
					return StmExpList.builder()
						.exps(
							ExpList.builder()
								.head(new TEMP(t))
								.tail(bb.exps)
								.build()
						)
						.stm(
							seq(
								MOVE.builder()
									.dst(new TEMP(t))
									.src(aa.exp)
									.build(),
								bb.stm
							)
						)
						.build();
				}
			}
		}
	}

	static StmList linear(SEQ s, StmList l) {
		var rightSideLinear = linear(s.right, l);
		return linear(s.left, rightSideLinear);
	}

	static StmList linear(Stm s, StmList l) {
		if (s instanceof SEQ sSEQ) {
			return linear(sSEQ, l);
		} else {
			return new StmList(s, l);
		}
	}

	public static StmList linearize(Stm s) {
		var stm = doStm(s);
		return linear(stm, null);
	}
}




