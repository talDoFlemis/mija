/** Mini-Java ANTLR4 grammar **/

grammar MiniJava;

@header {
package org.example.antlr;
}

// Ignore Whitespace
WS: [ \t\r\n]+ -> skip;

// Ignore Comments
COMMENT: '/*' .*? '*/' -> skip;

// Ignore Single Line Comments
LINE_COMMENT: '//' ~[\r\n]* -> skip;

// Tokens
/* Punctuation */
LSQUIRLY : '{';
RSQUIRLY : '}';
LPAREN : '(';
RPAREN : ')';
LBRACKET : '[';
RBRACKET : ']';
COMMA : ',';
SEMICOLON : ';';
DOT : '.';

/* Keywords */
CLASS : 'class';
PUBLIC : 'public';
STATIC : 'static';
VOID : 'void';
MAIN : 'main';
EXTENDS : 'extends';
RETURN : 'return';
STRING : 'String';
IF : 'if';
ELSE : 'else';
WHILE : 'while';
THIS : 'this';
NEW : 'new';
LENGTH : 'length';
SOUT : 'System.out.println';

/* Types */
INT : 'int';
INT_ARRAY : 'int[]';
BOOLEAN : 'boolean';

/* Literals */
TRUE_LITERAL : 'true';
FALSE_LITERAL : 'false';
fragment DIGIT : [0-9];
fragment LETTER : [a-zA-Z];
IDENTIFIER : LETTER (LETTER | DIGIT | '_' )*;
INTEGER_LITERAL : DIGIT+;

/* Operators */
EQ : '=';
AND : '&&';
LT : '<';
PLUS : '+';
MINUS : '-';
STAR : '*';
BANG : '!';

program : mainClass (objectDecl)* EOF;

mainClass : CLASS identifier LSQUIRLY PUBLIC STATIC VOID MAIN LPAREN STRING LBRACKET RBRACKET identifier RPAREN LSQUIRLY statement RSQUIRLY RSQUIRLY;

objectDecl : CLASS identifier (EXTENDS identifier)? LSQUIRLY (varDecl | methodDecl)* RSQUIRLY;

integerArrayType : INT_ARRAY;

integerType : INT;

booleanType : BOOLEAN;

type: integerType | booleanType | integerArrayType | identifier;

formal : type identifier;

formalRest : COMMA formal;

formalList : formal (formalRest)*;

varDecl : type identifier SEMICOLON;

methodDecl : PUBLIC type identifier LPAREN (formalList)? RPAREN LSQUIRLY (varDecl)* (statement)* RETURN expression SEMICOLON RSQUIRLY;

identifier : IDENTIFIER;

expression
    : expression ( AND | LT | PLUS | MINUS | STAR ) expression
    | expression LBRACKET expression RBRACKET
    | expression DOT LENGTH
    | expression DOT identifier LPAREN (expression (COMMA expression)* )? RPAREN
    | INTEGER_LITERAL
    | TRUE_LITERAL
    | FALSE_LITERAL
    | identifier
    | THIS
    | NEW INT LBRACKET expression RBRACKET
    | NEW identifier LPAREN RPAREN
    | BANG expression
    | LPAREN expression RPAREN;

statement
    : LSQUIRLY (statement)* RSQUIRLY
    | IF LPAREN expression RPAREN statement ELSE statement
    | WHILE LPAREN expression RPAREN statement
    | identifier EQ expression SEMICOLON
    | identifier LBRACKET expression RBRACKET EQ expression SEMICOLON
    | SOUT LPAREN expression RPAREN SEMICOLON;
