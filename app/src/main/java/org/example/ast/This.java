package org.example.ast;

import lombok.EqualsAndHashCode;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
public class This extends Expression {
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}