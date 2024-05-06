package org.example.canon;

import lombok.*;
import org.example.irtree.*;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class MoveCall extends Stm {
	TEMP dst;
	CALL src;

	public ExpList children() {
		return src.children();
	}

	public Stm build(ExpList kids) {
		return new MOVE(dst, src.build(kids));
	}
}