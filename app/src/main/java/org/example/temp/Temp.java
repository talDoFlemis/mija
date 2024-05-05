package org.example.temp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class Temp {
	private static int count;
	private int num;

	public Temp() {
		num = count++;
	}

	public String toString() {
		return "t" + num;
	}
}

