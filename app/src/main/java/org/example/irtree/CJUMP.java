package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.temp.Label;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class CJUMP extends Stm {
    public final static int EQ = 0, NE = 1, LT = 2, GT = 3, LE = 4, GE = 5,
            ULT = 6, ULE = 7, UGT = 8, UGE = 9;
    public int relop;
    public ExpAbstract left, right;
    public Label condTrue, condFalse;


    public static int notRel(int relop) {
        return switch (relop) {
            case EQ -> NE;
            case NE -> EQ;
            case LT -> GE;
            case GE -> LT;
            case GT -> LE;
            case LE -> GT;
            case ULT -> UGE;
            case UGE -> ULT;
            case UGT -> ULE;
            case ULE -> UGT;
            default -> throw new Error("bad relop in CJUMP.notRel");
        };
    }

    public ExpList kids() {
        return new ExpList(left, new ExpList(right, null));
    }

    public Stm build(ExpList kids) {
        return new CJUMP(relop, kids.head, kids.tail.head, condTrue, condFalse);
    }
}
