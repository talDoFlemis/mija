package org.example.visitor.irtree;

import kotlin.Pair;
import org.example.ast.*;
import org.example.irtree.*;
import org.example.mips.MipsFrame;
import org.example.temp.Label;
import org.example.visitor.symbols.SymbolTableVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IRTreeVisitorTest {
	static MainClass mockedMainClass() {
		return MainClass.builder()
			.className(new Identifier("Main"))
			.argsName(new Identifier("args"))
			.statements(new StatementList(new ArrayList<>() {{
				add(new Sout(new IntegerLiteral(1)));
			}}))
			.build();
	}

	static Pair<Exp, Sout> mockedSout() {
		var exp = new Exp(new CALL(new NAME(new Label("_print")), new ExpList(new CONST(1), new ExpList(new CONST(0), null))));
		var sout = new Sout(new IntegerLiteral(1));
		return new Pair<>(exp, sout);
	}


	@Test
	@DisplayName("Should check a non empty list of expression")
	void shouldCheckANonEmptyListOfExpression() {
		// ARRANGE
		Program prog = Program.builder()
			.mainClass(mockedMainClass())
			.classes(new ClassDeclList(new ArrayList<>() {{
				add(ClassDeclSimple.builder()
					.className(new Identifier("method"))
					.methods(new MethodDeclList(new ArrayList<>() {{
						add(MethodDecl.builder()
							.identifier("main")
							.formals(new FormalList(new ArrayList<>()))
							.varDecls(new VarDeclList(new ArrayList<>() {{
								add(VarDecl.builder().name("x").type(new IntegerType()).build());
							}}))
							.statements(new StatementList(new ArrayList<>() {{
								add(new Sout(new IdentifierExpression("x")));
							}}))
							.type(new IntegerType())
							.returnExpression(new IntegerLiteral(1))
							.build());
					}}))
					.build());
			}}))
			.build();
		SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
		prog.accept(symbolTableVisitor);

		IRTreeVisitor irTreeVisitor = new IRTreeVisitor(symbolTableVisitor.getMainTable(), new MipsFrame());

		// ACT
		prog.accept(irTreeVisitor);

		// ASSERT
		assertFalse(irTreeVisitor.getListExp().isEmpty());
	}

	static Stream<Arguments> shouldParseBinaryAndUnaryOperations() {
		return Stream.of(
			Arguments.of(
				new Plus(new IntegerLiteral(1), new IntegerLiteral(2)),
				new Exp(new BINOP(BINOP.PLUS, new CONST(1), new CONST(2)))
			),
			Arguments.of(
				new Minus(new IntegerLiteral(1), new IntegerLiteral(2)),
				new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
			),
			Arguments.of(
				new Times(new IntegerLiteral(1), new IntegerLiteral(2)),
				new Exp(new BINOP(BINOP.MUL, new CONST(1), new CONST(2)))
			),
			Arguments.of(
				new Not(new IntegerLiteral(2)),
				new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
			),
			Arguments.of(
				new And(new True(), new False()),
				new Exp(new BINOP(BINOP.AND, new CONST(1), new CONST(0)))
			),
			Arguments.of(
				new LessThan(new IntegerLiteral(1), new IntegerLiteral(2)),
				new Exp(new BINOP(BINOP.MINUS, new CONST(1), new CONST(2)))
			),
			Arguments.of(
				new Plus(
					new Plus(new IntegerLiteral(1),
						new Minus(new IntegerLiteral(2), new IntegerLiteral(3))
					), new IntegerLiteral(4)
				),
				new Exp(new BINOP(BINOP.PLUS,
					new BINOP(BINOP.PLUS, new CONST(1),
						new BINOP(BINOP.MINUS, new CONST(2), new CONST(3))
					), new CONST(4)
				))
			)
		);
	}

	@DisplayName("Should parse binary and unary operations")
	@MethodSource
	@ParameterizedTest
	void shouldParseBinaryAndUnaryOperations(Node node, Exp expectedNode) {
		// Arrange
		var visitor = IRTreeVisitor.builder().build();

		// Act
		Exp actualNode = node.accept(visitor);

		// Assert
		assertEquals(expectedNode, actualNode);
	}

	static Stream<Arguments> shouldParseSimpleLiterals() {
		return Stream.of(
			Arguments.of(new IntegerLiteral(1), new Exp(new CONST(1))),
			Arguments.of(new True(), new Exp(new CONST(1))),
			Arguments.of(new False(), new Exp(new CONST(0)))
		);
	}

	@ParameterizedTest
	@DisplayName("Should parse simple literals")
	@MethodSource
	void shouldParseSimpleLiterals(Node node, Exp expectedNode) {
		// Arrange
		var visitor = IRTreeVisitor.builder().build();

		// Act
		Exp actualNode = node.accept(visitor);

		// Assert
		assertEquals(expectedNode, actualNode);
	}

	static Stream<Arguments> shouldParseASOUTStatement() {
		return Stream.of(
			Arguments.of(
				new Sout(new IntegerLiteral(1)),
				new Exp(new CALL(new NAME(new Label("_print")), new ExpList(new CONST(1), new ExpList(new CONST(0), null))))
			),
			Arguments.of(
				new Sout(new IntegerLiteral(2)),
				new Exp(new CALL(new NAME(new Label("_print")), new ExpList(new CONST(2), new ExpList(new CONST(0), null))))
			),
			Arguments.of(
				new Sout(new Plus(new IntegerLiteral(1), new IntegerLiteral(2))),
				new Exp(new CALL(new NAME(new Label("_print")), new ExpList(
					new BINOP(BINOP.PLUS, new CONST(1), new CONST(2)), new ExpList(new CONST(0), null))
				))
			)
		);
	}

	@DisplayName("Should parse a SOUT statement")
	@ParameterizedTest
	@MethodSource
	void shouldParseASOUTStatement(Sout node, Exp expectedNode) {
		// Arrange
		var frame = new MipsFrame();
		var visitor = IRTreeVisitor.builder()
			.frame(frame)
			.build();

		// Act
		Exp actualNode = node.accept(visitor);

		// Assert
		assertEquals(expectedNode, actualNode);
	}

	@Test
	@DisplayName("Should parse a if statement")
	void shouldParseIfStatement() {
		// Arrange
		var frame = new MipsFrame();
		Pair<Exp, Sout> mockedSout = mockedSout();
		var ifSon = If.builder()
			.condition(new True())
			.thenBranch(mockedSout.component2())
			.elseBranch(mockedSout.component2())
			.build();

		var visitor = IRTreeVisitor.builder().frame(frame).build();

		var expectedNode = ESEQ.builder()
			.stm(
				SEQ.builder()
					.left(
						SEQ.builder()
							.left(
								SEQ.builder()
									.left(new LABEL(new Label("if_end_0")))
									.right(
										CJUMP.builder()
											.relop(CJUMP.EQ)
											.left(new CONST(1))
											.right(new CONST(1))
											.condTrue(new Label("if_true_0"))
											.condFalse(new Label("if_false_0"))
											.build()
									)
									.build()
							)
							.right(
								null
							)
							.build()
					)
					.right(
						SEQ.builder()
							.left(
								SEQ.builder()
									.left(EXP.builder().exp(mockedSout.component1().unEx()).build())
									.right(new LABEL(new Label("if_true_0")))
									.build()
							)
							.right(
								SEQ.builder()
									.left(EXP.builder().exp(mockedSout.component1().unEx()).build())
									.right(new LABEL(new Label("if_false_0")))
									.build()
							)
							.build()
					)
					.right(new LABEL(new Label("if_end_0")))
					.build()
			)
			.exp(null)
			.build();

		// Act
		Exp actualNode = ifSon.accept(visitor);

		// Assert
		assertEquals(expectedNode, actualNode.unEx());
	}

//	@Test
//	@DisplayName("Should parse
}
