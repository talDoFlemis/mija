package org.example.programs

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.io.InputStreamReader

class Programs {
    interface IProgram {
        val path: String

        val inputStream: InputStream get() = javaClass.getResourceAsStream(path)!!

        val program: String get() = inputStream
            .reader()
            .use(InputStreamReader::readText)

        @Test
        fun `Should find and read the program`(): Unit =
            assertDoesNotThrow {
                assert(program.isNotBlank())
            }
    }

    data object Factorial : IProgram {
        override val path: String = "/programs/Factorial.java"
    }

    data object BinarySearch : IProgram {
        override val path: String = "/programs/BinarySearch.java"
    }

    data object BubbleSort : IProgram {
        override val path: String = "/programs/BubbleSort.java"
    }

    data object LinearSearch : IProgram {
        override val path: String = "/programs/LinearSearch.java"
    }

    data object QuickSort : IProgram {
        override val path: String = "/programs/QuickSort.java"
    }

    data object TreeVisitor : IProgram {
        override val path: String = "/programs/TreeVisitor.java"
    }

    data object LinkedList : IProgram {
        override val path: String = "/programs/LinkedList.java"
    }

    data object BinaryTree : IProgram {
        override val path: String = "/programs/BinaryTree.java"
    }
}