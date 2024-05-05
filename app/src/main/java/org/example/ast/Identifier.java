package org.example.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.visitor.Visitor;

@EqualsAndHashCode(callSuper = false)
@ToString
@Data
@AllArgsConstructor
public class Identifier extends Node {
	private String s;

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}
}
