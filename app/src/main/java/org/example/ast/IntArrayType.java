package org.example.ast;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@ToString
public class IntArrayType extends Type {
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
