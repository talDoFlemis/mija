package org.example.visitor.llvm;

import org.example.ast.*;
import org.example.llvm.*;
import org.example.llvm.BooleanType;
import org.example.llvm.IntegerType;
import org.example.llvm.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LLVMIRVisitorTest {
    static Stream<Arguments> shouldParseBasicExpressions() {
        return Stream.of(
            Arguments.of(
                new IntegerLiteral(10),
                new ArrayList<>()
            ),
            Arguments.of(
                Plus.builder()
                    .lhe(new IntegerLiteral(10))
                    .rhe(new IntegerLiteral(11))
                    .build(),
                new ArrayList<>() {{
                    add(AddInstruction.builder()
                        .leftOperand(new Value("10"))
                        .rightOperand(new Value("11"))
                        .result(new Value("%0"))
                        .build());
                }}
            ),
            Arguments.of(
                Minus.builder()
                    .lhe(new IntegerLiteral(10))
                    .rhe(new IntegerLiteral(11))
                    .build(),
                new ArrayList<>() {{
                    add(MinusInstruction.builder()
                        .leftOperand(new Value("10"))
                        .rightOperand(new Value("11"))
                        .result(new Value("%0"))
                        .build());
                }}
            ),
            Arguments.of(
                Times.builder()
                    .lhe(new IntegerLiteral(10))
                    .rhe(new IntegerLiteral(11))
                    .build(),
                new ArrayList<>() {{
                    add(MulInstruction.builder()
                        .leftOperand(new Value("10"))
                        .rightOperand(new Value("11"))
                        .result(new Value("%0"))
                        .build());
                }}
            ),
            Arguments.of(
                Times.builder()
                    .lhe(new IntegerLiteral(10))
                    .rhe(Plus.builder()
                        .lhe(new IntegerLiteral(1))
                        .rhe(new IntegerLiteral(3))
                        .build())
                    .build(),
                new ArrayList<>() {{
                    add(AddInstruction.builder()
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("3"))
                        .result(new Value("%0"))
                        .build());
                    add(MulInstruction.builder()
                        .leftOperand(new Value("10"))
                        .rightOperand(new Value("%0"))
                        .result(new Value("%1"))
                        .build());
                }}
            ),
            Arguments.of(
                And.builder()
                    .lhe(new False())
                    .rhe(new True())
                    .build(),
                new ArrayList<>() {{
                    add(AndInstruction.builder()
                        .leftOperand(new Value("0"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }}
            ),
            Arguments.of(
                LessThan.builder()
                    .lhe(new IntegerLiteral(10))
                    .rhe(new IntegerLiteral(11))
                    .build(),
                new ArrayList<>() {{
                    add(LessThanInstruction.builder()
                        .leftOperand(new Value("10"))
                        .rightOperand(new Value("11"))
                        .result(new Value("%0"))
                        .build());
                }}
            ),
            Arguments.of(
                new Not(new True()),
                new ArrayList<>() {{
                    add(NotInstruction.builder()
                        .operand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }}
            )
        );
    }

    Instruction getMockedPrint(int i) {
        var f = Function.builder().name("print_int").build();
        var value = new Value(Integer.toString(i));

        LinkedHashMap<Value, Type> args = new LinkedHashMap<>();
        args.put(value, new org.example.llvm.IntegerType());

        return CallInstruction.builder()
            .functionCalled(f)
            .args(args)
            .build();
    }

    BasicBlock getMockedBlock() {
        return BasicBlock.builder()
            .label(new Value("entry"))
            .build();
    }

    Function getMockedFunction() {
        var mockedBlock = getMockedBlock();
        return Function.builder()
            .entryBlock(mockedBlock)
            .build();
    }

    ArrayList<BasicBlock> getAllBasicBlocksFromFunction(Function fn) {
        var actualBlocks = new ArrayList<BasicBlock>();
        actualBlocks.add(fn.getEntryBlock());
        actualBlocks.addAll(fn.getOtherBlocks());

        return actualBlocks;
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Should generate basic expressions instructions")
    void shouldParseBasicExpressions(Expression exp, ArrayList<Instruction> expectedInstructions) {
        // ARRANGE
        var mockedBlock = BasicBlock.builder()
            .label(new Value("entry"))
            .build();

        var visitor = LLVMIRVisitor.builder()
            .currentBlock(mockedBlock)
            .build();
        //ACT
        exp.accept(visitor);

        //ASSERT
        assertEquals(expectedInstructions, mockedBlock.getInstructions());
    }

    @Test
    @DisplayName("Should parse a basic if statement")
    void shouldParseBasicIfStatement() {
        // ARRANGE
        var mockedFn = getMockedFunction();

        var ifSon = If.builder()
            .condition(new True())
            .thenBranch(new Sout(new IntegerLiteral(1)))
            .elseBranch(new Sout(new IntegerLiteral(1)))
            .build();

        var visitor = LLVMIRVisitor.builder()
            .currentBlock(mockedFn.getEntryBlock())
            .currentFunction(mockedFn)
            .build();

        var expectedBlocks = new ArrayList<>() {{
            add(BasicBlock.builder()
                .label(new Value("entry"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%0"))
                    .trueLabel(new Value("if_true_0"))
                    .falseLabel(new Value("if_false_0"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_true_0"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(1));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_false_0"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(1));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_end_0"))
                .build());
        }};

        // ACT
        ifSon.accept(visitor);

        // ASSERT
        var actualBlocks = getAllBasicBlocksFromFunction(mockedFn);
        assertEquals(expectedBlocks, actualBlocks);
    }

    @Test
    @DisplayName("Should parse nested if statements")
    void shouldParseNestedIfStatement() {
        var mockedFn = getMockedFunction();

        /*
        if (true) {
            if (false) {
            } else {
                if (true) {
                    sout(1)
                } else {
                    sout(2)
                }
                sout(3)
            }
            sout(4)
        } else {
        }
         */
        var ifSon = If.builder()
            .condition(new True())
            .thenBranch(
                Block.builder().statements(new StatementList(new ArrayList<>() {{
                    add(If.builder()
                        .condition(new False())
                        .thenBranch(new Block())
                        .elseBranch(
                            Block.builder()
                                .statements(new StatementList(new ArrayList<>() {{
                                    add(If.builder()
                                        .condition(new True())
                                        .thenBranch(new Sout(new IntegerLiteral(1)))
                                        .elseBranch(new Sout(new IntegerLiteral(2)))
                                        .build());
                                    add(new Sout(new IntegerLiteral(3)));
                                }}))
                                .build()
                        )
                        .build());
                    add(new Sout(new IntegerLiteral(4)));
                }})).build()
            )
            .elseBranch(new Block())
            .build();

        var visitor = LLVMIRVisitor.builder()
            .currentBlock(mockedFn.getEntryBlock())
            .currentFunction(mockedFn)
            .build();

        var expectedBlocks = new ArrayList<>() {{
            add(BasicBlock.builder()
                .label(new Value("entry"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%0"))
                    .trueLabel(new Value("if_true_0"))
                    .falseLabel(new Value("if_false_0"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_true_0"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("0"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%1"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%1"))
                    .trueLabel(new Value("if_true_1"))
                    .falseLabel(new Value("if_false_1"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_true_1"))
                .instructions(new ArrayList<>())
                .terminalInstruction(new BranchInstruction(new Value("if_end_1")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_false_1"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%2"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%2"))
                    .trueLabel(new Value("if_true_2"))
                    .falseLabel(new Value("if_false_2"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_true_2"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(1));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_2")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_false_2"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(2));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_2")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_end_2"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(3));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_1")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_end_1"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(4));
                }})
                .terminalInstruction(new BranchInstruction(new Value("if_end_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_false_0"))
                .instructions(new ArrayList<>())
                .terminalInstruction(new BranchInstruction(new Value("if_end_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("if_end_0"))
                .build());
        }};

        // ACT
        ifSon.accept(visitor);

        // ASSERT
        var actualBlocks = getAllBasicBlocksFromFunction(mockedFn);
        assertEquals(expectedBlocks, actualBlocks);
    }

    @Test
    @DisplayName("Should parse a basic while statement")
    void shouldParseBasicWhileStatement() {
        // ARRANGE
        var mockedFn = getMockedFunction();

        var whileSon = While.builder()
            .condition(new True())
            .body(new Sout(new IntegerLiteral(1)))
            .build();

        var visitor = LLVMIRVisitor.builder()
            .currentFunction(mockedFn)
            .currentBlock(mockedFn.getEntryBlock())
            .build();

        var expectedBlocks = new ArrayList<BasicBlock>() {{
            add(BasicBlock.builder()
                .label(new Value("entry"))
                .terminalInstruction(new BranchInstruction(new Value("while_check_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_check_0"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%0"))
                    .trueLabel(new Value("while_body_0"))
                    .falseLabel(new Value("while_end_0"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_body_0"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(1));
                }})
                .terminalInstruction(new BranchInstruction(new Value("while_check_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_end_0"))
                .build());
        }};

        // ACT
        whileSon.accept(visitor);

        // ASSERT
        var actualBlocks = getAllBasicBlocksFromFunction(mockedFn);
        assertEquals(expectedBlocks, actualBlocks);
    }

    @Test
    @DisplayName("Should parse a nested while statements")
    void shouldParseNestedWhileStatements() {
        // ARRANGE
        var mockedFn = getMockedFunction();

        /*
        while(true) {
            while (false) {
                while (true) {
                    sout(1)
                }
                sout(2)
            }
            sout(3)
        }
         */
        var whileSon = While.builder()
            .condition(new True())
            .body(new Block(new StatementList(new ArrayList<>() {{
                add(While.builder()
                    .condition(new False())
                    .body(new Block(new StatementList(new ArrayList<>() {{
                        add(While.builder()
                            .condition(new True())
                            .body(new Sout(new IntegerLiteral(1)))
                            .build());
                        add(new Sout(new IntegerLiteral(2)));
                    }})))
                    .build());
                add(new Sout(new IntegerLiteral(3)));
            }})))
            .build();

        var visitor = LLVMIRVisitor.builder()
            .currentFunction(mockedFn)
            .currentBlock(mockedFn.getEntryBlock())
            .build();

        var expectedBlocks = new ArrayList<BasicBlock>() {{
            add(BasicBlock.builder()
                .label(new Value("entry"))
                .terminalInstruction(new BranchInstruction(new Value("while_check_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_check_0"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%0"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%0"))
                    .trueLabel(new Value("while_body_0"))
                    .falseLabel(new Value("while_end_0"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_body_0"))
                .terminalInstruction(new BranchInstruction(new Value("while_check_1")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_check_1"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("0"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%1"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%1"))
                    .trueLabel(new Value("while_body_1"))
                    .falseLabel(new Value("while_end_1"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_body_1"))
                .terminalInstruction(new BranchInstruction(new Value("while_check_2")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_check_2"))
                .instructions(new ArrayList<>() {{
                    add(EqualsInstruction.builder()
                        .type(new BooleanType())
                        .leftOperand(new Value("1"))
                        .rightOperand(new Value("1"))
                        .result(new Value("%2"))
                        .build());
                }})
                .terminalInstruction(BranchInstruction.builder()
                    .condition(new Value("%2"))
                    .trueLabel(new Value("while_body_2"))
                    .falseLabel(new Value("while_end_2"))
                    .build())
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_body_2"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(1));
                }})
                .terminalInstruction(new BranchInstruction(new Value("while_check_2")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_end_2"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(2));
                }})
                .terminalInstruction(new BranchInstruction(new Value("while_check_1")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_end_1"))
                .instructions(new ArrayList<>() {{
                    add(getMockedPrint(3));
                }})
                .terminalInstruction(new BranchInstruction(new Value("while_check_0")))
                .build());
            add(BasicBlock.builder()
                .label(new Value("while_end_0"))
                .build());
        }};

        // ACT
        whileSon.accept(visitor);

        // ASSERT
        var actualBlocks = getAllBasicBlocksFromFunction(mockedFn);
        assertEquals(expectedBlocks, actualBlocks);
    }

    @Test
    @DisplayName("Should parse a new Array Expression")
    void shouldParseNewArrayExpression() {
        // ARRANGE
        var mockedBlock = getMockedBlock();

        var size = 1;
        var newArray = new NewArray(new IntegerLiteral(size));
        var visitor = LLVMIRVisitor.builder().currentBlock(mockedBlock).build();

        // ACT
        newArray.accept(visitor);

        // ASSERT
        var expectedInstructions = new ArrayList<Instruction>() {{
            add(AllocaInstruction.builder()
                .type(new ArrayType())
                .result(new Value("%0"))
                .build());
            add(StoreInstruction.builder()
                .type(new IntegerType())
                .value(new Value(Integer.toString(size)))
                .pointer(new Value("%0"))
                .build());
            add(AllocaInstruction.builder()
                .type(new IntPointerType())
                .result(new Value("%1"))
                .build());
            add(GetElementPointerInstruction.builder()
                .baseType(new ArrayType())
                .baseAddress(new Value("%0"))
                .result(new Value("%2"))
                .offset(new Offset() {{
                    addOffset(new IntegerType(), new Value("0"));
                    addOffset(new IntegerType(), new Value("1"));
                }})
                .build());
            add(StoreInstruction.builder()
                .type(new IntPointerType())
                .pointer(new Value("%2"))
                .value(new Value("%1"))
                .build());
        }};

        assertEquals(expectedInstructions, mockedBlock.getInstructions());
    }

    @Test
    @DisplayName("Should parse an array length expression")
    void shouldParseAnArrayLengthExpression() {
        // ARRANGE
        var mockedBlock = getMockedBlock();

        var fakeAddress = 420;
        var newArray = new ArrayLength(new IntegerLiteral(fakeAddress));
        var visitor = LLVMIRVisitor.builder().currentBlock(mockedBlock).build();

        // ACT
        newArray.accept(visitor);

        // ASSERT
        var expectedInstructions = new ArrayList<Instruction>() {{
            add(GetElementPointerInstruction.builder()
                .baseType(new ArrayType())
                .baseAddress(new Value(Integer.toString(fakeAddress)))
                .offset(new Offset() {{
                    addOffset(new IntegerType(), new Value("0"));
                }})
                .result(new Value("%0"))
                .build());
            add(LoadInstruction.builder()
                .type(new IntegerType())
                .pointer(new Value("%0"))
                .result(new Value("%1"))
                .build());
        }};

        assertEquals(expectedInstructions, mockedBlock.getInstructions());
    }

    @Test
    @DisplayName("Should parse an array lookup expression")
    void shouldParseAnArrayLookupExpression() {
        // ARRANGE
        var mockedBlock = getMockedBlock();

        var size = 2;
        var fakeAddress = 42069;
        var newArray = new ArrayLookup(new IntegerLiteral(fakeAddress), new IntegerLiteral(size));
        var visitor = LLVMIRVisitor.builder().currentBlock(mockedBlock).build();

        // ACT
        newArray.accept(visitor);

        // ASSERT
        var expectedInstructions = new ArrayList<Instruction>() {{
            add(GetElementPointerInstruction.builder()
                .baseType(new ArrayType())
                .baseAddress(new Value(Integer.toString(fakeAddress)))
                .offset(new Offset() {{
                    addOffset(new IntegerType(), new Value("0"));
                    addOffset(new IntegerType(), new Value("1"));
                    addOffset(new IntegerType(), new Value(Integer.toString(size)));
                }})
                .result(new Value("%0"))
                .build());
            add(LoadInstruction.builder()
                .type(new IntegerType())
                .pointer(new Value("%0"))
                .result(new Value("%1"))
                .build());
        }};

        assertEquals(expectedInstructions, mockedBlock.getInstructions());
    }
}
