package org.example.visitor.bytecode

import org.example.ast.*

object JasminVisitor {


    fun CodeGen.ProgramCode.visit(mainClass: MainClass) {
        classDecl(isMain = true) {

            name = mainClass.className.s
            path = "java/lang/Object"

            methodDecl {
                name = "main"
                descriptor = "([Ljava/lang/String;)V"
                visibility = "public"

                statement {

                    for (statement in mainClass.statements.statements)
                        visit(statement)
                }
            }
        }
    }

    fun StatementCode.visit(statement: Assign) =
        fieldManipulation {
            opcode = "putfield"
            operand1 = "Main"
            operand2 = statement.identifier.s
        }

    fun StatementCode.visit(statement: While) {
        branch {
            opcode = "ifne"
            operand = "L1"
        }
        visit(statement.condition)

        visit(statement.body)

        branch {
            opcode = "goto"
            operand = "L0"
        }
    }

    fun StatementCode.visit(statement: Sout) {
        fieldManipulation {
            opcode = "getstatic"
            operand1 = "java/lang/System/out"
        }
        fieldManipulation {
            opcode = "invokevirtual"
            operand1 = "java/io/PrintStream/println(I)V"
        }
    }


    fun StatementCode.visit(statement: Block) =
        statement.statements.statements.forEach { visit(it) }


    fun StatementCode.visit(expression: Expression) {
        when (expression) {
            is IntegerLiteral -> {
                pushOrInc {
                    opcode = "bipush"
                    operand = expression.value.toString()
                }
            }

            is IdentifierExpression -> {
                fieldManipulation {
                    opcode = "getfield"
                    operand1 = "Main"
                    operand2 = expression.id.toString()
                }
            }

            is Times -> {
                visit(expression.lhe)
                visit(expression.rhe)

            }
            // Add more cases as needed for other types of expressions
        }
    }

    fun StatementCode.visit(statement: If) {

        pushOrInc {
            opcode = "bipush"
            operand = "1"
        }


        branch {
            opcode = "g"
        }
        visit(statement.thenBranch)

        visit(statement.elseBranch)

        branch {
            opcode = "iflt"
            operand = "L1"
        }
    }


    fun StatementCode.visit(statement: StatementList) {
        when (statement) {
            is Assign -> {
                fieldManipulation {
                    opcode = "putfield"
                    operand1 = "Main"
                    operand2 = statement.identifier.s
                }
            }

            is Sout -> {
                fieldManipulation {
                    opcode = "getstatic"
                    operand1 = "java/lang/System/out"
                }
                fieldManipulation {
                    opcode = "invokevirtual"
                    operand1 = "java/io/PrintStream/println(I)V"
                }
            }


        }
    }


    fun <T : Statement> StatementCode.visit(statement: T) {
        when (statement) {
            is Assign -> {
                fieldManipulation {
                    opcode = "putfield"
                    operand1 = "Main"
                    operand2 = statement.identifier.s
                }
            }

            is Sout -> {
                fieldManipulation {
                    opcode = "getstatic"
                    operand1 = "java/lang/System/out"
                }
                fieldManipulation {
                    opcode = "invokevirtual"
                    operand1 = "java/io/PrintStream/println(I)V"
                }
            }
        }
    }


    fun visit(program: Program): CodeGen.ProgramCode {
        return CodeGen.BaseInstance.program {
            name = "MiniJava Program"


            for (classDecl in program.classes.classDecls.filterIsInstance<ClassDeclSimple>()) {
                classDecl(isMain = false) {
                    name = classDecl.className.s
                    path = "java/lang"

                    for (local in classDecl.fields.varDecls) {
                        fieldDecl {
                            name = local.name
                            descriptor = "I"
                            visibility = "private"
                        }
                    }

                    for (method in classDecl.methods.methodDecls) {
                        methodDecl {
                            name = method.identifier.toString()
                            descriptor = "()V"

                            statement {
                            for (statement in method.statements.statements)
                                visit(statement)
                            }
                        }
                    }
                }

            }
        }
    }
}
