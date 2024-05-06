package org.example.visitor.mermaid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.example.ast.*;
import org.example.visitor.Visitor;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Log4j2
public class MermaidASTPrinterVisitor implements Visitor<Void> {

	private PrintStream printStream;
	private boolean templateMermaid;
	private int count;

	public MermaidASTPrinterVisitor(OutputStream output) {
		printStream = (PrintStream) output;
		count = 0;
		templateMermaid = false;
	}

	public MermaidASTPrinterVisitor(String file) {
		try {
			printStream = new PrintStream(file);
		} catch (FileNotFoundException e) {
			log.error(e);
		}
		count = 0;
		templateMermaid = true;
	}

	public Void visit(And a) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[&&]");

		printStream.println(mermaidArrow(actualIndex, count));
		a.getLhe().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		a.getRhe().accept(this);
		count++;

		return null;
	}

	public Void visit(BooleanType b) {
		return null;
	}

	public Void visit(Not n) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[!]");
		printStream.println(mermaidArrow(actualIndex, count));
		n.getE().accept(this);
		return null;
	}

	public Void visit(True t) {
		printStream.println(count + "[True]");
		return null;
	}

	public Void visit(False f) {
		printStream.println(count + "[False]");
		return null;
	}

	public Void visit(Identifier i) {
		printStream.println(count + "[" + i.getS() + "]");
		return null;
	}

	public Void visit(Call c) {
		printStream.println(count + "[" + c.getMethod().getS() + "]");
		return null;
	}

	public Void visit(IdentifierExpression i) {
		printStream.println(count + "[" + i.getId() + "]");
		return null;
	}

	public Void visit(IdentifierType i) {
		return null;
	}

	public Void visit(NewObject n) {
		return null;
	}

	public Void visit(This t) {
		return null;
	}

	public Void visit(ArrayLookup a) {
		return null;
	}

	public Void visit(ArrayAssign a) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[=]");

		printStream.println(mermaidArrow(actualIndex, count));
		a.getIdentifier().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		a.getIndex().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		a.getValue().accept(this);
		count++;

		return null;
	}

	public Void visit(ArrayLength a) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[Length]");
		printStream.println(mermaidArrow(actualIndex, count));
		a.getArray().accept(this);
		return null;
	}

	public Void visit(Plus p) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[+]");

		printStream.println(mermaidArrow(actualIndex, count));
		p.getLhe().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		p.getRhe().accept(this);
		count++;

		return null;
	}

	public Void visit(Minus m) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[-]");

		printStream.println(mermaidArrow(actualIndex, count));
		m.getLhe().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		m.getRhe().accept(this);
		count++;

		return null;
	}

	public Void visit(Times t) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[*]");

		printStream.println(mermaidArrow(actualIndex, count));
		t.getLhe().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		t.getRhe().accept(this);
		count++;

		return null;
	}

	public Void visit(IntegerLiteral i) {
		printStream.println(count + "[" + i.getValue() + "]");
		return null;
	}

	public Void visit(IntegerType i) {
		return null;
	}

	public Void visit(IntArrayType i) {
		return null;
	}

	public Void visit(LessThan l) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[<]");

		printStream.println(mermaidArrow(actualIndex, count));
		l.getLhe().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		l.getRhe().accept(this);
		count++;

		return null;
	}

	public Void visit(NewArray n) {
		return null;
	}

	public Void visit(While w) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[While]");

		printStream.println(mermaidArrow(actualIndex, count));
		w.getCondition().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		w.getBody().accept(this);
		count++;

		return null;
	}

	public Void visit(If i) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[If]");

		printStream.println(mermaidArrow(actualIndex, count));
		i.getCondition().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		printStream.println(count + "[Then]");
		printStream.println(mermaidArrow(count++, count));
		i.getThenBranch().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		printStream.println(count + "[Else]");
		printStream.println(mermaidArrow(count++, count));
		i.getElseBranch().accept(this);
		count++;

		return null;
	}

	public Void visit(Assign a) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[=]");

		printStream.println(mermaidArrow(actualIndex, count));
		a.getIdentifier().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		a.getValue().accept(this);
		count++;

		return null;
	}

	public Void visit(Sout s) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[Sout]");

		printStream.println(mermaidArrow(actualIndex, count));
		s.getExpression().accept(this);
		count++;

		return null;
	}

	public Void visit(Block b) {
		int actualIndex = count++;

		b.getStatements().getStatements().forEach(s -> {
			printStream.println(mermaidArrow(actualIndex, count));
			s.accept(this);
			count++;
		});

		return null;
	}

	public Void visit(MainClass m) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + m.getClassName().getS() + "]");

		m.getStatements().getStatements().forEach(s -> {
			printStream.println(mermaidArrow(actualIndex, count));
			s.accept(this);
			count++;
		});

		return null;
	}

	public Void visit(ClassDeclSimple c) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + c.getClassName().getS() + "]");

		c.getMethods().getMethodDecls().forEach(m -> {
			printStream.println(mermaidArrow(actualIndex, count));
			m.accept(this);
			count++;
		});

		c.getFields().getVarDecls().forEach(v -> {
			printStream.println(mermaidArrow(actualIndex, count));
			v.accept(this);
			count++;
		});

		return null;
	}

	public Void visit(ClassDeclExtends c) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + c.getClassName().getS() + "]");

		c.getMethods().getMethodDecls().forEach(m -> {
			printStream.println(mermaidArrow(actualIndex, count));
			m.accept(this);
			count++;
		});

		c.getFields().getVarDecls().forEach(v -> {
			printStream.println(mermaidArrow(actualIndex, count));
			v.accept(this);
			count++;
		});

		return null;
	}

	public Void visit(Program p) {
		int actualIndex = count++;
		int actualClassIndex;
		if (templateMermaid) {
			printStream.println("```mermaid");
		}
		printStream.println("flowchart TD");
		printStream.println(actualIndex + "[Program]");

		printStream.println(mermaidArrow(actualIndex, count));
		printStream.println(count + "[MainClass]");

		printStream.println(mermaidArrow(count++, count));
		p.getMainClass().accept(this);
		count++;

		printStream.println(mermaidArrow(actualIndex, count));
		printStream.println(count + "[Classes]");
		actualClassIndex = count++;
		p.getClasses().getClassDecls().forEach(c -> {
			printStream.println(mermaidArrow(actualClassIndex, count));
			c.accept(this);
			count++;
		});

		if (templateMermaid) {
			printStream.println("```");
		}
		return null;
	}

	public Void visit(MethodDecl m) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + m.getIdentifier() + "]");

		m.getFormals().getFormals().forEach(f -> {
			printStream.println(mermaidArrow(actualIndex, count));
			f.accept(this);
			count++;
		});

		m.getVarDecls().getVarDecls().forEach(v -> {
			printStream.println(mermaidArrow(actualIndex, count));
			v.accept(this);
			count++;
		});

		m.getStatements().getStatements().forEach(s -> {
			printStream.println(mermaidArrow(actualIndex, count));
			s.accept(this);
			count++;
		});


		return null;
	}

	public Void visit(VarDecl v) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + v.getName() + "]");

		return null;
	}

	public Void visit(Formal f) {
		int actualIndex = count++;
		printStream.println(actualIndex + "[" + f.getName() + "]");
		return null;
	}

	private String mermaidArrow(int lheIndex, int rheIndex) {
		return lheIndex + " --> " + rheIndex;
	}

}