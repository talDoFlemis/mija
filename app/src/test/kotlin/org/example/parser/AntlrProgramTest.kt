package org.example.parser

import org.example.programs.Programs

abstract class AntlrProgramTest<Program: Programs.IProgram> : ParserTestDispatcher<AntlrParser, Program> {
    companion object {
        val antlrParser = AntlrParser()
    }

    override val parser = antlrParser

    abstract override val program: Program
}

object AntlrFactorial : AntlrProgramTest<Programs.Factorial>() {
    override val program = Programs.Factorial
}

object AntlrBinarySearch : AntlrProgramTest<Programs.BinarySearch>() {
    override val program = Programs.BinarySearch
}

object AntlrBubbleSort : AntlrProgramTest<Programs.BubbleSort>() {
    override val program = Programs.BubbleSort
}

object AntlrLinearSearch : AntlrProgramTest<Programs.LinearSearch>() {
    override val program = Programs.LinearSearch
}

object AntlrQuickSort : AntlrProgramTest<Programs.QuickSort>() {
    override val program = Programs.QuickSort
}

object AntlrTreeVisitor : AntlrProgramTest<Programs.TreeVisitor>() {
    override val program = Programs.TreeVisitor
}

object AntlrLinkedList : AntlrProgramTest<Programs.LinkedList>() {
    override val program = Programs.LinkedList
}

object AntlrBinaryTree : AntlrProgramTest<Programs.BinaryTree>() {
    override val program = Programs.BinaryTree
}