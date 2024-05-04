package org.example.canon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.irtree.StmList;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StmListList {
	public StmList head;
	public StmListList tail;
}

