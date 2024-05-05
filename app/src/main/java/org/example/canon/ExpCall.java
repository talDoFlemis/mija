package org.example.canon;

import lombok.*;
import org.example.irtree.CALL;
import org.example.irtree.EXP;
import org.example.irtree.ExpList;
import org.example.irtree.Stm;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ExpCall extends Stm {
	CALL call;

	public ExpList children() {
		return call.children();
	}

	public Stm build(ExpList kids) {
		return new EXP(call.build(kids));
	}
}