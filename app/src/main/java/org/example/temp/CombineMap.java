package org.example.temp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class CombineMap implements TempMap {
	TempMap tmap1, tmap2;

	public String tempMap(Temp t) {
		String s = tmap1.tempMap(t);
		if (s != null)
			return s;
		return tmap2.tempMap(t);
	}

}

