package org.example.ast;

import lombok.*;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FormalList {
	@Builder.Default
	private ArrayList<Formal> formals = new ArrayList<>();

	public void addFormal(Formal formal) {
		formals.add(formal);
	}
}
