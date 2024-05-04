package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class EXP extends Stm {
	public ExpAbstract exp;

	public ExpList children() {
		return new ExpList(exp, null);
	}

	public Stm build(ExpList children) {
		return new EXP(children.head);
	}
}