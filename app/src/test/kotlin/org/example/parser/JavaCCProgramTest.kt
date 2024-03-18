package org.example.parser

import org.example.programs.Programs

abstract class JavaCCProgramTest<Program: Programs.IProgram> : ParserTestDispatcher<JavaCCParser, Program> {
    companion object {
        val javaCCParser = JavaCCParser()
    }

    override val parser = javaCCParser

    abstract override val program: Program
}

object JavaCCFactorial : JavaCCProgramTest<Programs.Factorial>() {
    override val program = Programs.Factorial
}

object JavaCCBinarySearch : JavaCCProgramTest<Programs.BinarySearch>() {
    override val program = Programs.BinarySearch
}

object JavaCCBubbleSort : JavaCCProgramTest<Programs.BubbleSort>() {
    override val program = Programs.BubbleSort
}

object JavaCCLinearSearch : JavaCCProgramTest<Programs.LinearSearch>() {
    override val program = Programs.LinearSearch
}

object JavaCCQuickSort : JavaCCProgramTest<Programs.QuickSort>() {
    override val program = Programs.QuickSort
}

object JavaCCTreeVisitor : JavaCCProgramTest<Programs.TreeVisitor>() {
    override val program = Programs.TreeVisitor
}

object JavaCCLinkedList : JavaCCProgramTest<Programs.LinkedList>() {
    override val program = Programs.LinkedList
}

object JavaCCBinaryTree : JavaCCProgramTest<Programs.BinaryTree>() {
    override val program = Programs.BinaryTree
}