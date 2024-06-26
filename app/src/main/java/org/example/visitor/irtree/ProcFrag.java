package org.example.visitor.irtree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.frame.Frame;
import org.example.irtree.Stm;
import org.example.mips.MipsFrame;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Log4j2
public class ProcFrag extends Frag {
	private Stm body;
	private MipsFrame frame;

	public ProcFrag(Stm b, MipsFrame f, Frag next) {
		super(next);
		body = b;
		frame = f;
	}
}
