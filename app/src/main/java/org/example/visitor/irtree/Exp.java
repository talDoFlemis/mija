package org.example.visitor.irtree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.irtree.ExpAbstract;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Exp {

	public ExpAbstract exp;

	public ExpAbstract unEx() {
		return exp;
	}
}
