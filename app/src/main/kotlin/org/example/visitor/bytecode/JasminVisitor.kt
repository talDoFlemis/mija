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


                for (statement in mainClass.statements.statements) {

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

                            for (statement in method.statements.statements) {
                                when (statement) {
                                    is Assign -> {
                                        fieldManipulation {
                                            opcode = "putfield"
                                            operand1 = classDecl.className.s
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
                        }
                    }
                }

            }
        }
    }
}
