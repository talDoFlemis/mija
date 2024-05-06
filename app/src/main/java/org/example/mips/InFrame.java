package org.example.mips;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.frame.Access;
import org.example.irtree.BINOP;
import org.example.irtree.CONST;
import org.example.irtree.ExpAbstract;
import org.example.irtree.MEM;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class InFrame extends Access {
	int offset;

	public ExpAbstract exp(ExpAbstract fp) {
		return new MEM
			(new BINOP(BINOP.PLUS, fp, new CONST(offset)));
	}

	public String toString() {
		return Integer.toString(this.offset);
	}
}
