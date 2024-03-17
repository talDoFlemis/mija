package org.example.factories


interface BinaryToken {
    val left: String
    val right: String
}


object TokenFactory {

    sealed interface Token {
        interface Binary : Token {
            val left: String
            val right: String
            fun inject(content: String): String = "$left$content$right"
        }
        data object Bracket : Binary {
            override val left: String = "{"
            override val right: String = "}"
        }
        data object Paren : Binary {
            override val left: String = "("
            override val right: String = ")"
        }
        data object Squirly : Binary {
            override val left: String = "["
            override val right: String = "]"
        }
        interface Unary : Token {
            val value: String
            fun prepend(content: String): String = "$content$value"
            fun append(content: String): String = "$value$content"
        }
        data object SemiColon : Unary {
            override val value: String = ";"
        }
        data object Dot : Unary {
            override val value: String = "."
        }
        data object Comma : Unary {
            override val value: String = ","
        }
    }


}