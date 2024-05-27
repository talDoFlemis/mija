package org.example.mips;

import org.example.assem.*;
import org.example.irtree.*;

public interface MipsCodegen {
	InstrList codegen(Stm stm);
}

