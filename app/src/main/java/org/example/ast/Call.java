package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
public class Call extends Expression {
	private Expression owner;
	private Identifier method;
	@Builder.Default
	private ExpressionList expressionList = new ExpressionList();

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
