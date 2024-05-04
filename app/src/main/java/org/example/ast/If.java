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
public class If extends Statement {
	private Expression condition;
	@Builder.Default
	private Statement thenBranch = new Block(new StatementList());
	@Builder.Default
	private Statement elseBranch = new Block(new StatementList());

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
