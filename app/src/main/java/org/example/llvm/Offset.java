package org.example.llvm;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Offset {
	@Builder.Default
	private ArrayList<Pair<Type, Value>> o = new ArrayList<>();

	public List<Pair<Type, Value>> getOffsets() {
		return o;
	}

	public void addOffset(Type type, Value value) {
		o.add(new Pair<>(type, value));
	}

	public int size() {
		return o.size();
	}

}
