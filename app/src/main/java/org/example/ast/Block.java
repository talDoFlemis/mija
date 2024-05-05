package org.example.ast;

import lombok.*;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Block extends Statement {
	@Builder.Default
	private StatementList statements = new StatementList();

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
