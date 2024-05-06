package org.example.canon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.irtree.*;
import org.example.temp.Label;


@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BasicBlocks {
	public StmListList blocks;
	public Label done;

	private StmList lastStm;
	private StmListList lastBlock;

	public BasicBlocks(StmList stms) {
		done = new Label();
		makeBlocks(stms);
	}

	private void addStm(Stm s) {
		lastStm = lastStm.tail = new StmList(s, null);
	}

	private void doStmt(StmList l) {
		if (l == null)
			doStmt(
				StmList.builder()
					.head(new JUMP(done))
					.tail(null)
					.build()
			);
		else if (l.head instanceof JUMP || l.head instanceof CJUMP) {
			addStm(l.head);
			makeBlocks(l.tail);
		} else if (l.head instanceof LABEL lHead) {
			doStmt(
				StmList.builder()
					.head(new JUMP((lHead.label)))
					.tail(l)
					.build()
			);
		} else {
			addStm(l.head);
			doStmt(l.tail);
		}
	}

	void makeBlocks(StmList l) {
		if (l == null) {
		} else if (l.head instanceof LABEL lHead) {
			lastStm = new StmList(lHead, null);
			if (lastBlock == null)
				lastBlock = blocks = new StmListList(lastStm, null);
			else
				lastBlock = lastBlock.tail = new StmListList(lastStm, null);
			doStmt(l.tail);
		} else makeBlocks(new StmList(new LABEL(new Label()), l));
	}
}
