package org.example.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class MEM extends ExpAbstract {
	public ExpAbstract exp;

	public ExpList children() {
		return new ExpList(exp, null);
	}

	public ExpAbstract build(ExpList children) {
		return new MEM(children.head);
	}
}