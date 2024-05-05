package org.example.temp;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DefaultMap implements TempMap {
	public String tempMap(Temp t) {
		return t.toString();
	}
}
