package org.example.canon;

import lombok.*;
import org.example.irtree.ExpList;
import org.example.irtree.Stm;


@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StmExpList {
	Stm stm;
	ExpList exps;
}