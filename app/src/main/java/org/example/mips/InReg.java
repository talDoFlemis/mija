package org.example.mips;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.frame.Access;
import org.example.irtree.ExpAbstract;
import org.example.irtree.TEMP;
import org.example.temp.Temp;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class InReg extends Access {
	Temp temp;

	public ExpAbstract exp(ExpAbstract fp) {
		return new TEMP(temp);
	}

	public String toString() {
		return temp.toString();
	}
}