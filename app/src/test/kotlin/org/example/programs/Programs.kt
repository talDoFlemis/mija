package org.example.programs

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.io.InputStreamReader

class Programs {
    interface Program {
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

        @Test
        fun `Should find and read the program (debug)`(): Unit =
            assertDoesNotThrow {
                println(program)
            }
    }

    data object Factorial : Program {
        override val path: String = "/programs/Factorial.java"
    }

    data object BinarySearch : Program {
        override val path: String = "/programs/BinarySearch.java"
    }

    data object BubbleSort : Program {
        override val path: String = "/programs/BubbleSort.java"
    }

    data object LinearSearch : Program {
        override val path: String = "/programs/LinearSearch.java"
    }

    data object QuickSort : Program {
        override val path: String = "/programs/QuickSort.java"
    }

    data object TreeVisitor : Program {
        override val path: String = "/programs/TreeVisitor.java"
    }

    data object LinkedList : Program {
        override val path: String = "/programs/LinkedList.java"
    }

    data object BinaryTree : Program {
        override val path: String = "/programs/BinaryTree.java"
    }
}